-- Создание пользовательского типа status_type
DROP TYPE IF EXISTS status_type;
CREATE TYPE status_type AS ENUM ('ACTIVE','BLOCKED','EXPIRED');