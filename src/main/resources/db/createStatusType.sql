-- User defined type status_type
DROP TYPE IF EXISTS status_type CASCADE;

CREATE TYPE status_type AS ENUM ('ACTIVE','BLOCKED','EXPIRED','PENDING');

CREATE CAST (varchar AS status_type) WITH INOUT AS IMPLICIT;

CREATE CAST (status_type AS varchar) WITH INOUT AS IMPLICIT;