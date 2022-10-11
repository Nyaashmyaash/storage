-- Заполнить данными таблицу sellers

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
        SELECT dblink_connect(DB_LINK, 'connect_timeout=0 port=5432 hostaddr=#### dbname=contract user=#### password=####') INTO connection_result;
        RAISE NOTICE 'Connect [%] status %', DB_LINK, connection_result;

        WHILE affected_rows != 0 LOOP
                WITH inserted_subjects AS (
                    INSERT INTO storage.public.sellers
                        (id,
                         contract_uuid,
                         seller_name,
                         free_storage_days_count,
                         "timestamp")
                        SELECT
                            id,
                            contract_uuid,
                            seller_name,
                            free_storage_days_count,
                            "timestamp"
                        FROM dblink(DB_LINK,
                                    'SELECT
                                        seller.id as id,
                                        client_contract.uuid as contract_uuid,
                                        seller.name as seller_name,
                                        refund_rule.free_storage_days_count as free_storage_days_count,
                                        client_contract.last_update as timestamp
                                    FROM public.seller
                                        LEFT JOIN public.client_contract
                                            ON public.seller.contract_id = public.client_contract.id
                                        LEFT JOIN public.refund_rule
                                            ON public.seller.id = public.refund_rule.seller_id
                                    ORDER BY seller.id
                                    LIMIT ' || LIMIT_VALUE || ' OFFSET ' || offset_value
                                 ) AS remote_subject(id BIGINT,
                                                     contract_uuid UUID,
                                                     seller_name TEXT,
                                                     free_storage_days_count TEXT,
                                                     "timestamp" timestamp)
                        ON CONFLICT DO NOTHING
                        RETURNING 1
                )
SELECT COUNT(1) INTO affected_rows FROM inserted_subjects;
offset_value = offset_value + LIMIT_VALUE;
COMMIT;

RAISE NOTICE 'affected_rows % rows', affected_rows;
END LOOP;

SELECT dblink_disconnect(DB_LINK) INTO connection_result;
RAISE NOTICE 'Disconnect [%] status %', DB_LINK, connection_result;
END$$;
