CREATE TABLE IF NOT EXISTS public.packages (
    package_uuid UUID PRIMARY KEY,
    order_uuid UUID REFERENCES orders (order_uuid) ON DELETE CASCADE,
    package_number TEXT,
    bar_code TEXT,
    itm_bar_code TEXT,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    "timestamp" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC')
    );

COMMENT ON TABLE public.packages IS 'Упаковки';
COMMENT ON COLUMN public.packages.package_uuid IS 'Идентификатор упаковки';
COMMENT ON COLUMN public.packages.order_uuid IS 'Идентификатор заказа';
COMMENT ON COLUMN public.packages.package_number IS 'Номер упаковки';
COMMENT ON COLUMN public.packages.bar_code IS 'Штрих-код упаковки';
COMMENT ON COLUMN public.packages.deleted IS 'Удалена ли упаковка';
