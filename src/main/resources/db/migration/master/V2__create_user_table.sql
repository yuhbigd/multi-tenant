CREATE TYPE role_enum AS ENUM ('ADMIN', 'USER');

CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    role role_enum NOT NULL UNIQUE
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    tenant_name TEXT NOT NULL
);
CREATE TABLE user_role (
    role_id INTEGER REFERENCES roles(id),
    user_id INTEGER REFERENCES users(id),
    PRIMARY KEY (role_id, user_id)
);
