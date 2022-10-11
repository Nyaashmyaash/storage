CREATE TABLE IF NOT EXISTS public.package_status (
    status_uuid UUID PRIMARY KEY,
    order_uuid UUID REFERENCES orders (order_uuid),
    package_uuid UUID REFERENCES packages (package_uuid),
    location TEXT,
    location_office_uuid UUID,
    next_location TEXT,
    status TEXT,
    "timestamp" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC')
    );

COMMENT ON TABLE public.package_status IS 'Статусы грузомест';
COMMENT ON COLUMN public.package_status.status_uuid IS 'Идентификатор статуса';
COMMENT ON COLUMN public.package_status.order_uuid IS 'Идентификатор заказа';
COMMENT ON COLUMN public.package_status.package_uuid IS 'Идентификатор упаковки';
COMMENT ON COLUMN public.package_status.location IS 'Местонахождение ГМ';
COMMENT ON COLUMN public.package_status.location_office_uuid IS 'Идентификатор офиса-местонахождения';
COMMENT ON COLUMN public.package_status.next_location IS 'Следующее местонахождение';
COMMENT ON COLUMN public.package_status.status IS 'Статус грузоместа';
