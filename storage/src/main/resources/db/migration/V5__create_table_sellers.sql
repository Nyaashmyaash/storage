CREATE TABLE IF NOT EXISTS public.sellers (
    id BIGINT PRIMARY KEY,
    contract_uuid UUID REFERENCES contract (contract_uuid),
    seller_name TEXT,
    free_storage_days_count TEXT,
    "timestamp" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC')
    );

COMMENT ON TABLE public.sellers IS 'Продавцы, указанные в договоре';
COMMENT ON COLUMN public.sellers.id IS 'Идентификатор продавца';
COMMENT ON COLUMN public.sellers.contract_uuid IS 'Идентификатор контракта';
COMMENT ON COLUMN public.sellers.seller_name IS 'Имя продавца';
COMMENT ON COLUMN public.sellers.free_storage_days_count IS 'Кол-во дней бесплатного хранения';
