-- Заполнить данными таблицу office

CREATE EXTENSION IF NOT EXISTS "dblink";

DO
$$
DECLARE
    LIMIT_VALUE CONSTANT BIGINT := 50000;
    offset_value BIGINT := 0;
    affected_rows INTEGER := -1;
    connection_result TEXT;

    DB_LINK CONSTANT TEXT := 'subject_import';
BEGIN
SELECT dblink_connect(DB_LINK, 'connect_timeout=0 port=5432 hostaddr=#### dbname=api_pvz user=#### password=####') INTO connection_result;
RAISE NOTICE 'Connect [%] status %', DB_LINK, connection_result;

    WHILE affected_rows != 0 LOOP
            WITH inserted_subjects AS (
                INSERT INTO storage.public.office
                    (office_uuid,
                     city_code,
                     update_timestamp)
                    SELECT
                        office_uuid,
                        city_code,
                        update_timestamp
                    FROM dblink(DB_LINK,
                                'SELECT
                                    uuid as office_uuid,
                                    city_code as city_code,
                                    update_date_time as update_timestamp
                                FROM public.office_new
                                    INNER JOIN public.office_address
                                        ON public.office_new.uuid = public.office_address.office_uuid
                                ORDER BY uuid
                                LIMIT ' || LIMIT_VALUE || ' OFFSET ' || offset_value
                             ) AS remote_subject(office_uuid UUID,
                                                 city_code TEXT,
                                                 update_timestamp timestamp)
                    ON CONFLICT DO NOTHING RETURNING 1
            )

SELECT COUNT(1) INTO affected_rows FROM inserted_subjects;
offset_value = offset_value + LIMIT_VALUE;

COMMIT;

RAISE NOTICE 'Updated [office subjects] % rows', affected_rows;
END LOOP;

SELECT dblink_disconnect(DB_LINK) INTO connection_result;
RAISE NOTICE 'Disconnect [%] status %', DB_LINK, connection_result;
END
$$;
