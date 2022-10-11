CREATE TABLE IF NOT EXISTS public.contract (
    contract_uuid UUID PRIMARY KEY,
    id BIGINT NOT NULL,
    contract_number TEXT NOT NULL,
    type_code TEXT NOT NULL,
    status_code TEXT NOT NULL,
    contragent_uuid UUID NOT NULL,
    "timestamp" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC')
    );

COMMENT ON TABLE public.contract IS 'Договор';
COMMENT ON COLUMN public.contract.contract_uuid IS 'Идентификатор договора';
COMMENT ON COLUMN public.contract.id IS 'ID договора';
COMMENT ON COLUMN public.contract.contract_number IS 'Номер договора';
COMMENT ON COLUMN public.contract.type_code IS 'Тип договора';
COMMENT ON COLUMN public.contract.status_code IS 'Статус договора';
COMMENT ON COLUMN public.contract.contragent_uuid IS 'Идентификатор контрагента, с которым заключен договор';
