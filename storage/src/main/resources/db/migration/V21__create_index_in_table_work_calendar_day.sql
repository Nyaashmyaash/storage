CREATE UNIQUE INDEX IF NOT EXISTS idx_wcd_calendar_uuid_date ON public.work_calendar_day (calendar_uuid, "date");
