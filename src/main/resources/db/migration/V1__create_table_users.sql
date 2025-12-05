CREATE TABLE users (
       id_user UUID NOT NULL,
       name VARCHAR(200) NOT NULL,
       email VARCHAR(150) NOT NULL,
       password VARCHAR(255) NOT NULL,
       status VARCHAR(15) NOT NULL,
       created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
       updated_at TIMESTAMP WITHOUT TIME ZONE,
       deleted_at TIMESTAMP WITHOUT TIME ZONE,

       CONSTRAINT pk_users PRIMARY KEY (id_user),
       CONSTRAINT uc_users_email UNIQUE (email)
);