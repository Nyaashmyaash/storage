-- Заполнить данными таблицу logistic_city

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
SELECT dblink_connect(DB_LINK, 'connect_timeout=0 port=5432 hostaddr=#### dbname=locality user=#### password=####') INTO connection_result;
RAISE NOTICE 'Connect [%] status %', DB_LINK, connection_result;

    WHILE affected_rows != 0 LOOP
            WITH inserted_subjects AS (
                INSERT INTO storage.public.logistic_city
                    (city_uuid,
                     city_code,
                     region_uuid,
                     country_uuid,
                     update_timestamp)
                    SELECT
                        city_uuid,
                        city_code,
                        region_uuid,
                        country_uuid,
                        update_timestamp
                    FROM dblink(DB_LINK,
                                'SELECT
                                    uuid AS city_uuid,
                                    ek4_code AS city_code,
                                    region_uuid AS region_uuid,
                                    country_uuid AS country_uuid,
                                    update_time AS update_timestamp
                                FROM public.city
                                ORDER BY uuid
                                LIMIT ' || LIMIT_VALUE || ' OFFSET ' || offset_value
                             ) AS remote_subject(city_uuid UUID,
                                                city_code TEXT,
                                                region_uuid UUID,
                                                country_uuid UUID,
                                                update_timestamp timestamp)
                    ON CONFLICT DO NOTHING RETURNING 1
            )

SELECT COUNT(1) INTO affected_rows FROM inserted_subjects;
offset_value = offset_value + LIMIT_VALUE;

COMMIT;

RAISE NOTICE 'Updated [city subjects] % rows', affected_rows;
END LOOP;

SELECT dblink_disconnect(DB_LINK) INTO connection_result;
RAISE NOTICE 'Disconnect [%] status %', DB_LINK, connection_result;
END
$$;
