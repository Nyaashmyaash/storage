CREATE INDEX concurrently IF NOT EXISTS idx_contract_contract_uuid ON public.contract (contract_uuid);
CREATE INDEX concurrently IF NOT EXISTS idx_contract_contract_number ON public.contract (contract_number);

CREATE INDEX concurrently IF NOT EXISTS idx_logistic_city_city_uuid ON public.logistic_city (city_uuid);
CREATE INDEX concurrently IF NOT EXISTS idx_logistic_city_city_code ON public.logistic_city (city_code);

CREATE INDEX concurrently IF NOT EXISTS idx_office_office_uuid ON public.office (office_uuid);

CREATE INDEX concurrently IF NOT EXISTS idx_package_status_status_uuid ON public.package_status (status_uuid);
CREATE INDEX concurrently IF NOT EXISTS idx_package_status_order_uuid ON public.package_status (order_uuid);

CREATE INDEX concurrently IF NOT EXISTS idx_orders ON public.orders (order_uuid);

CREATE INDEX concurrently IF NOT EXISTS idx_order_storage_order_storage_uuid ON public.order_storage (order_storage_uuid);
CREATE INDEX concurrently IF NOT EXISTS idx_order_storage_shelf_life_order_in_days_and_order_uuid ON public.order_storage (shelf_life_order_in_days, order_uuid);
CREATE INDEX concurrently IF NOT EXISTS idx_order_storage_order_uuid ON public.order_storage (order_uuid);

CREATE INDEX concurrently IF NOT EXISTS idx_packages_package_uuid ON public.packages (package_uuid);
CREATE INDEX concurrently IF NOT EXISTS idx_packages_deleted_and_order_uuid ON public.packages (deleted, order_uuid);

CREATE INDEX concurrently IF NOT EXISTS idx_sellers_id ON public.sellers (id);
CREATE INDEX concurrently IF NOT EXISTS idx_sellers_contract_uuid_and_seller_name ON public.sellers (contract_uuid, seller_name);

CREATE INDEX concurrently IF NOT EXISTS idx_work_calendar_day_date_and_calendar_uuid ON public.work_calendar_day ("date", calendar_uuid);
CREATE INDEX concurrently IF NOT EXISTS idx_work_calendar_day_date_type_calendar_uuid ON public.work_calendar_day ("date", day_type_code, calendar_uuid);

CREATE INDEX concurrently IF NOT EXISTS idx_work_calendar_calendar_uuid ON public.work_calendar (calendar_uuid);
CREATE INDEX concurrently IF NOT EXISTS idx_work_calendar_calendar_uuid_active_region_country ON public.work_calendar (calendar_uuid, active, region_uuid, country_uuid);
CREATE INDEX concurrently IF NOT EXISTS idx_work_calendar_region_uuid_year_active ON public.work_calendar (region_uuid, year, active);
