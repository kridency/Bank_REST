-- User defined type role_type
DROP TYPE IF EXISTS role_type CASCADE;

CREATE TYPE role_type AS ENUM ('ROLE_USER','ROLE_ADMIN');

CREATE CAST (varchar AS role_type) WITH INOUT AS IMPLICIT;

CREATE CAST (role_type AS varchar) WITH INOUT AS IMPLICIT;