ALTER TABLE IF EXISTS public.orders ADD COLUMN IF NOT EXISTS payer_contract_number TEXT;

COMMENT ON COLUMN public.orders.payer_contract_number IS 'Номер договора контрагента-плательщика';
