ALTER TABLE IF EXISTS public.logistic_city ADD COLUMN IF NOT EXISTS time_zone TEXT;

COMMENT ON COLUMN public.logistic_city.time_zone IS 'Таймзона, в которой находится город';
