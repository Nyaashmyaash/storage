ALTER TABLE IF EXISTS public.package_status ALTER COLUMN package_uuid SET NOT NULL;

CREATE INDEX IF NOT EXISTS package_status_package_uuid_and_timestamp_idx ON public.package_status (package_uuid, "timestamp");
