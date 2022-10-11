DROP DATABASE IF EXISTS storage;
CREATE DATABASE storage
WITH OWNER = expresscourier
ENCODING = 'UTF8'
TABLESPACE = pg_default
LC_COLLATE = 'ru_RU.UTF-8'
LC_CTYPE = 'ru_RU.UTF-8'
CONNECTION LIMIT = -1;

GRANT ALL PRIVILEGES ON DATABASE storage TO expresscourier;

CREATE SCHEMA catalogs;

-- При создании базы требуется выполнить под суперпользователем:
CREATE EXTENSION IF NOT EXISTS "pgcrypto" SCHEMA public;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA public;
CREATE EXTENSION IF NOT EXISTS "pg_trgm" SCHEMA public;

CREATE EXTENSION IF NOT EXISTS "pgcrypto" SCHEMA catalogs;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA catalogs;
CREATE EXTENSION IF NOT EXISTS "pg_trgm" SCHEMA catalogs;
