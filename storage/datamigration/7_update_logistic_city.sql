--Обновить данные в колонке time_zone в таблице logistic_city

CREATE EXTENSION IF NOT EXISTS "dblink";

DO
$$
    DECLARE
        LIMIT_VALUE CONSTANT BIGINT := 50000;
        offset_value BIGINT := 0;
        affected_rows INTEGER := -1;
        connection_result TEXT;

        DB_LINK CONSTANT TEXT := 'subject_update';
    BEGIN
    SELECT dblink_connect(DB_LINK, 'connect_timeout=0 port=#### hostaddr=#### dbname=locality user=#### password=####') INTO connection_result;
    RAISE NOTICE 'Connect [%] status %', DB_LINK, connection_result;

        WHILE affected_rows != 0 LOOP
                WITH updated_subjects AS (
                    UPDATE storage.public.logistic_city
                        SET time_zone = t.time_zone
                        FROM dblink(DB_LINK,
                            'SELECT uuid, time_zone
                            FROM locality.public.city
                            ORDER BY city.uuid
                            LIMIT ' || LIMIT_VALUE || ' OFFSET ' || offset_value)
                            as t(uuid UUID,
                                 time_zone TEXT)
                        WHERE storage.public.logistic_city.city_uuid = t.uuid

                        RETURNING 1
                )
            SELECT COUNT(1) INTO affected_rows FROM updated_subjects;
            offset_value = offset_value + LIMIT_VALUE;
            COMMIT;

            RAISE NOTICE 'affected_rows % rows', affected_rows;
        END LOOP;

    SELECT dblink_disconnect(DB_LINK) INTO connection_result;
    RAISE NOTICE 'Disconnect [%] status %', DB_LINK, connection_result;
END$$;
