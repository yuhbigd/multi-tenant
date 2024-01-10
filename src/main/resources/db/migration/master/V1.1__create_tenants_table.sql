CREATE SEQUENCE TENANT_ID_SEQUENCE START WITH 1;

CREATE TABLE TENANTS
(
    TENANT_ID   BIGINT,
    TENANT_NAME VARCHAR,
    TENANT_SCHEMA VARCHAR,
    PRIMARY KEY (TENANT_ID)
);