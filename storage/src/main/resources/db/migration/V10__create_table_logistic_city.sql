CREATE TABLE IF NOT EXISTS public.logistic_city (
    city_uuid UUID PRIMARY KEY,
    city_code TEXT NOT NULL,
    region_uuid UUID,
    country_uuid UUID NOT NULL,
    update_timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC')
    );

COMMENT ON TABLE logistic_city IS 'Логистический город';
COMMENT ON COLUMN logistic_city.city_uuid IS 'Уникальный идентификатор';
COMMENT ON COLUMN logistic_city.city_code IS 'Код города из ЭК4';
COMMENT ON COLUMN logistic_city.region_uuid IS 'UUID региона, в котором находится город';
COMMENT ON COLUMN logistic_city.country_uuid IS 'UUID страны, в которой находится город';
COMMENT ON COLUMN logistic_city.update_timestamp IS 'Дата и время обновления сущности';

