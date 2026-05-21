CREATE TABLE member
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    login_id   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(50)  NOT NULL,
    name       VARCHAR(255) NOT NULL,
    role       VARCHAR(50)  NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT current_timestamp,
    updated_at TIMESTAMP    NOT NULL DEFAULT current_timestamp ON UPDATE current_timestamp,
    PRIMARY KEY (id)
);

CREATE TABLE reservation_time
(
    id         BIGINT    NOT NULL AUTO_INCREMENT,
    start_at   TIME      NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
    updated_at TIMESTAMP NOT NULL DEFAULT current_timestamp ON UPDATE current_timestamp,
    PRIMARY KEY (id)
);

CREATE TABLE theme
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    url         VARCHAR(512),
    created_at  TIMESTAMP    NOT NULL DEFAULT current_timestamp,
    updated_at  TIMESTAMP    NOT NULL DEFAULT current_timestamp ON UPDATE current_timestamp,
    PRIMARY KEY (id)
);

CREATE TABLE refresh_token
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    member_id  BIGINT       NOT NULL,
    token      VARCHAR(255) NOT NULL UNIQUE,
    expired_at TIMESTAMP    NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE store
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    member_id  BIGINT       NOT NULL,
    name       VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP    NOT NULL DEFAULT current_timestamp,
    updated_at TIMESTAMP    NOT NULL DEFAULT current_timestamp ON UPDATE current_timestamp,
    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE reservation
(
    id         BIGINT    NOT NULL AUTO_INCREMENT,
    date       DATE      NOT NULL,
    member_id  BIGINT    NOT NULL,
    time_id    BIGINT    NOT NULL,
    theme_id   BIGINT    NOT NULL,
    store_id   BIGINT    NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
    updated_at TIMESTAMP NOT NULL DEFAULT current_timestamp ON UPDATE current_timestamp,
    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (time_id) REFERENCES reservation_time (id),
    FOREIGN KEY (theme_id) REFERENCES theme (id),
    FOREIGN KEY (store_id) REFERENCES store (id),
    CONSTRAINT uk_reservation_theme_date_time_store UNIQUE (date, theme_id, time_id, store_id)
);
