-- Создание пользовательского типа role_type
DROP TYPE IF EXISTS role_type;
CREATE TYPE role_type AS ENUM ('ROLE_USER','ROLE_ADMIN');