-- Заполнить данными таблицы work_calendar и work_calendar_day

CREATE EXTENSION IF NOT EXISTS "dblink";

------------------------------------------------------------
-- 1. Импорт данных в таблицу work_calendar
------------------------------------------------------------
CREATE OR REPLACE PROCEDURE importWorkCalendar()
    LANGUAGE plpgsql
AS
$$
DECLARE
LIMIT_VALUE CONSTANT BIGINT := 50000;
    offset_value BIGINT := 0;
    affected_rows INTEGER := -1;
    connection_result TEXT;

    DB_LINK CONSTANT TEXT := 'subject_import';
BEGIN
SELECT dblink_connect(DB_LINK, 'connect_timeout=0 port=5432 hostaddr=#### dbname=work-calendar user=#### password=####') INTO connection_result;
RAISE NOTICE 'Connect [%] status %', DB_LINK, connection_result;

    WHILE affected_rows != 0 LOOP
            WITH inserted_subjects AS (
                INSERT INTO storage.public.work_calendar
                    (calendar_uuid, active, date_updated, year, country_code, country_uuid, region_code, region_uuid)
                    SELECT
                        calendar_uuid,
                        active,
                        date_updated,
                        year,
                        country_code,
                        country_uuid,
                        region_code,
                        region_uuid
                    FROM dblink(DB_LINK,
                                'SELECT
                                    uuid as calendar_uuid,
                                    active,
                                    date_updated,
                                    year,
                                    country_code,
                                    country_uuid,
                                    region_code,
                                    region_uuid
                                FROM calendar.calendar
                                ORDER BY uuid
                                LIMIT ' || LIMIT_VALUE || ' OFFSET ' || offset_value
                             ) AS remote_subject(calendar_uuid UUID, active BOOLEAN, date_updated TIMESTAMP,
                                                 year SMALLINT, country_code TEXT, country_uuid UUID, region_code TEXT, region_uuid UUID)
                    ON CONFLICT DO NOTHING RETURNING 1
            )

SELECT COUNT(1) INTO affected_rows FROM inserted_subjects;
offset_value = offset_value + LIMIT_VALUE;

COMMIT;

RAISE NOTICE 'Updated [work calendar subjects] % rows', affected_rows;
END LOOP;

SELECT dblink_disconnect(DB_LINK) INTO connection_result;
RAISE NOTICE 'Disconnect [%] status %', DB_LINK, connection_result;
END
$$;

------------------------------------------------------------
-- 2. Импорт данных в таблицу work_calendar_day
------------------------------------------------------------
CREATE OR REPLACE PROCEDURE importWorkCalendarDay()
    LANGUAGE plpgsql
AS
$$
DECLARE
LIMIT_VALUE CONSTANT BIGINT := 50000;
    offset_value BIGINT := 0;
    affected_rows INTEGER := -1;
    connection_result TEXT;

    DB_LINK CONSTANT TEXT := 'day_import';
BEGIN
SELECT dblink_connect(DB_LINK, 'connect_timeout=0 port=5432 hostaddr=#### dbname=work-calendar user=#### password=####') INTO connection_result;
RAISE NOTICE 'Connect [%] status %', DB_LINK, connection_result;

    WHILE affected_rows != 0 LOOP
            WITH inserted_days AS (
                INSERT INTO work_calendar_day
                    (date, day_type_code, calendar_uuid)
                    SELECT
                        date,
                        day_type_code,
                        calendar_uuid
                    FROM dblink(DB_LINK,
                                'SELECT
                                    date,
                                    day_type_code,
                                    calendar_uuid
                                FROM calendar.calendar_day
                                ORDER BY id
                                LIMIT ' || LIMIT_VALUE || ' OFFSET ' || offset_value
                             ) AS remote_day(date DATE, day_type_code TEXT, calendar_uuid UUID)
                    ON CONFLICT DO NOTHING RETURNING 1
            )

SELECT COUNT(1) INTO affected_rows FROM inserted_days;
offset_value = offset_value + LIMIT_VALUE;

COMMIT;

RAISE NOTICE 'Inserted [work calendar days] % rows', affected_rows;
END LOOP;

SELECT dblink_disconnect(DB_LINK) INTO connection_result;
RAISE NOTICE 'Disconnect [%] status %', DB_LINK, connection_result;
END;
$$;

CALL importWorkCalendar();
CALL importWorkCalendarDay();

DROP PROCEDURE IF EXISTS importWorkCalendar();
DROP PROCEDURE IF EXISTS importWorkCalendarDay();
