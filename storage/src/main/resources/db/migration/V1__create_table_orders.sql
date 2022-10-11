CREATE TABLE IF NOT EXISTS public.orders (
    order_uuid UUID PRIMARY KEY,
    order_number TEXT NOT NULL,
    order_status_code TEXT NOT NULL,
    order_type_code TEXT,
    true_delivery_mode_code TEXT,
    payer_code TEXT,
    payer_uuid UUID,
    payer_contract_uuid UUID,
    seller_name TEXT,
    count_day TEXT,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    "timestamp" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC')
    );

COMMENT ON TABLE public.orders IS 'Заказ';
COMMENT ON COLUMN public.orders.order_uuid IS 'Уникальный идентификатор заказа';
COMMENT ON COLUMN public.orders.order_number IS 'Номер заказа';
COMMENT ON COLUMN public.orders.order_status_code IS 'Код статуса заказа';
COMMENT ON COLUMN public.orders.order_type_code IS 'Код типа заказа';
COMMENT ON COLUMN public.orders.true_delivery_mode_code IS 'Истинный режим доставки';
COMMENT ON COLUMN public.orders.payer_code IS 'Код контрагента-плательщика';
COMMENT ON COLUMN public.orders.payer_uuid IS 'Уникальный идентификатор контрагента-плательщика';
COMMENT ON COLUMN public.orders.payer_contract_uuid IS 'Уникальный идентификатор договора контрагента-плательщика';
COMMENT ON COLUMN public.orders.seller_name IS 'Имя продавца (актуально только для заказа типа ИМ)';
COMMENT ON COLUMN public.orders.count_day IS 'Кол-во дней, указанных в доп. услуге "Хранение на складе"';
COMMENT ON COLUMN public.orders.deleted IS 'Признак, является ли заказ удаленным';
