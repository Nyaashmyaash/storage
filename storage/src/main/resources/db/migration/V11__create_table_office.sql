CREATE TABLE IF NOT EXISTS public.office (
    office_uuid UUID PRIMARY KEY,
    city_code TEXT NOT NULL,
    update_timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC')
    );

COMMENT ON TABLE office IS 'Офисы и их местоположение';
COMMENT ON COLUMN office.office_uuid IS 'Уникальный идентификатор офиса';
COMMENT ON COLUMN office.city_code IS 'Код города из ЭК4';
COMMENT ON COLUMN office.update_timestamp IS 'Дата и время обновления сущности';
