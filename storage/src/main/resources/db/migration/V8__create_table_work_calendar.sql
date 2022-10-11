CREATE TABLE IF NOT EXISTS public.work_calendar (
    calendar_uuid UUID PRIMARY KEY NOT NULL,
    active BOOLEAN DEFAULT false,
    date_updated TIMESTAMP WITH TIME ZONE NOT NULL,
    "year" SMALLINT NOT NULL,
    country_code TEXT,
    country_uuid UUID,
    region_code TEXT,
    region_uuid UUID
);

COMMENT ON TABLE public.work_calendar IS 'Производственные календари';
COMMENT ON COLUMN public.work_calendar.calendar_uuid IS 'Идентификатор кадендаря';
COMMENT ON COLUMN public.work_calendar.active IS 'Активный ли календарь';
COMMENT ON COLUMN public.work_calendar.date_updated IS 'Дата редактирования';
COMMENT ON COLUMN public.work_calendar.year IS 'Год';
COMMENT ON COLUMN public.work_calendar.country_code IS 'Код страны';
COMMENT ON COLUMN public.work_calendar.country_uuid IS 'Уникальный идентификатор страны';
COMMENT ON COLUMN public.work_calendar.region_code IS 'Код региона';
COMMENT ON COLUMN public.work_calendar.region_uuid IS 'Уникальный идентификатор региона';
