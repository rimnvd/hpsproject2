CREATE TABLE if not exists users
(
    id       BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    email    VARCHAR(255),
    password VARCHAR(255),
    username VARCHAR(255),
    description VARCHAR(255),
    balance  INTEGER,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE if not exists roles
(
    id   INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    role VARCHAR(255),
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE if not exists users_roles
(
    role_id INTEGER NOT NULL,
    user_id BIGINT  NOT NULL
);