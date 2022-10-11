-- Заполнить данными таблицу contract

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
                INSERT INTO storage.public.contract
                    (contract_uuid,
                     id,
                     contract_number,
                     type_code,
                     status_code,
                     contragent_uuid,
                     "timestamp")
                    SELECT
                        contract_uuid,
                        id,
                        contract_number,
                        type_code,
                        status_code,
                        contragent_uuid,
                        "timestamp"
                    FROM dblink(DB_LINK,
                                'SELECT
                                    uuid as contract_uuid,
                                    id as id,
                                    number as contract_number,
                                    type_id as type_code,
                                    status_id as status_code,
                                    contragent_uuid as contragent_uuid,
                                    last_update as timestamp
                                FROM public.client_contract
                                ORDER BY uuid
                                LIMIT ' || LIMIT_VALUE || ' OFFSET ' || offset_value
                             ) AS remote_subject(contract_uuid UUID,
                                                 id BIGINT,
                                                 contract_number TEXT,
                                                 type_code TEXT,
                                                 status_code TEXT,
                                                 contragent_uuid UUID,
                                                 "timestamp" timestamp)
                    ON CONFLICT DO NOTHING RETURNING 1
            )

SELECT COUNT(1) INTO affected_rows FROM inserted_subjects;
offset_value = offset_value + LIMIT_VALUE;
COMMIT;

RAISE NOTICE 'affected_rows % rows', affected_rows;
END LOOP;

SELECT dblink_disconnect(DB_LINK) INTO connection_result;
RAISE NOTICE 'Disconnect [%] status %', DB_LINK, connection_result;
END$$;
