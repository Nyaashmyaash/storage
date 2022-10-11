ALTER TABLE IF EXISTS public.sellers ADD COLUMN IF NOT EXISTS postamat_order_storage_days_count SMALLINT;

COMMENT ON COLUMN public.sellers.postamat_order_storage_days_count IS 'Срок хранения постаматных заказов';
