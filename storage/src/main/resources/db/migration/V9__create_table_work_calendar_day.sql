CREATE TABLE IF NOT EXISTS public.work_calendar_day (
    id bigserial PRIMARY KEY,
    "date" DATE NOT NULL,
    day_type_code TEXT NOT NULL,
    calendar_uuid UUID REFERENCES work_calendar (calendar_uuid)
);

COMMENT ON TABLE public.work_calendar_day IS 'Дни календаря';
COMMENT ON COLUMN public.work_calendar_day.date IS 'Дата';
COMMENT ON COLUMN public.work_calendar_day.day_type_code IS 'Тип дня (Рабочий, Выходной, Предпраздничный, Праздничный)';
COMMENT ON COLUMN public.work_calendar_day.calendar_uuid IS 'Уникальный идентификатор календаря';
