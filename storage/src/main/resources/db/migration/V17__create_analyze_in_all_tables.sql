ANALYZE public.contract (contract_uuid);
ANALYZE public.contract (contract_number);

ANALYZE public.logistic_city (city_uuid);
ANALYZE public.logistic_city (city_code);

ANALYZE public.office (office_uuid);

ANALYZE public.package_status (status_uuid);
ANALYZE public.package_status (order_uuid);

ANALYZE public.orders (order_uuid);

ANALYZE public.order_storage (order_storage_uuid);
ANALYZE public.order_storage (shelf_life_order_in_days, order_uuid);
ANALYZE public.order_storage (order_uuid);

ANALYZE public.packages (package_uuid);
ANALYZE public.packages (deleted, order_uuid);

ANALYZE public.sellers (id);
ANALYZE public.sellers (contract_uuid, seller_name);

ANALYZE public.work_calendar_day ("date", calendar_uuid);
ANALYZE public.work_calendar_day ("date", day_type_code, calendar_uuid);

ANALYZE public.work_calendar (calendar_uuid);
ANALYZE public.work_calendar (calendar_uuid, active, region_uuid, country_uuid);
ANALYZE public.work_calendar (region_uuid, year, active);
