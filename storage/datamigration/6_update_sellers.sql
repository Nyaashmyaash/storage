--Обновить данные в колонке postamat_order_storage_days_count в таблице sellers

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
    SELECT dblink_connect(DB_LINK, 'connect_timeout=0 port=#### hostaddr=#### dbname=contract user=#### password=####') INTO connection_result;
    RAISE NOTICE 'Connect [%] status %', DB_LINK, connection_result;

        WHILE affected_rows != 0 LOOP
                WITH updated_subjects AS (
                    UPDATE storage.public.sellers
                        SET postamat_order_storage_days_count = t.postamat_order_storage_days_count
                        FROM dblink(DB_LINK,
                            'SELECT id, postamat_order_storage_days_count
                            FROM contract.public.seller
                            ORDER BY seller.id
                            LIMIT ' || LIMIT_VALUE || ' OFFSET ' || offset_value)
                            as t(id BIGINT,
                                 postamat_order_storage_days_count SMALLINT)
                        WHERE storage.public.sellers.id = t.id

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
