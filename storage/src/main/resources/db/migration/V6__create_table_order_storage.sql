CREATE TABLE IF NOT EXISTS public.order_storage (
    order_storage_uuid UUID PRIMARY KEY,
    order_uuid UUID NOT NULL REFERENCES orders (order_uuid) ON DELETE CASCADE,
    order_number TEXT NOT NULL,
    deadline_for_storage TIMESTAMP WITH TIME ZONE,
    date_of_receipt TIMESTAMP WITH TIME ZONE,
    shelf_life_order_in_days INT,
    "timestamp" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC')
    );

COMMENT ON TABLE public.order_storage IS 'Сроки хранения заказа';
COMMENT ON COLUMN public.order_storage.order_storage_uuid IS 'Идентификатор сущности "Сроки хранения заказа"';
COMMENT ON COLUMN public.order_storage.order_uuid IS 'Идентификатор заказа';
COMMENT ON COLUMN public.order_storage.order_number IS 'Номер заказа';
COMMENT ON COLUMN public.order_storage.deadline_for_storage IS 'Крайняя дата хранения на складе';
COMMENT ON COLUMN public.order_storage.date_of_receipt IS 'Дата получения в офисе доставки (или дата закладки в постамат)';
COMMENT ON COLUMN public.order_storage.shelf_life_order_in_days IS 'Срок хранения заказа в днях на складе или при закладке в постамат';
