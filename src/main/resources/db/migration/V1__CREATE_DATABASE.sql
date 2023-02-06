create table hibernate_sequence (
    next_val bigint
) engine=MyISAM;

insert into hibernate_sequence values ( 1 );
insert into hibernate_sequence values ( 1 );

CREATE TABLE user (
    id BIGINT NOT NULL AUTO_INCREMENT,
    login varchar(64) NOT NULL UNIQUE,
    password varchar(255) NOT NULL,
    role varchar(255) NOT NULL DEFAULT "ACTIVATED_USER",
    token varchar(255),
    telegram_id BIGINT,
    telegram_username varchar(255),
    PRIMARY KEY (id)
) engine=MyISAM;

CREATE TABLE host (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name varchar(255) NOT NULL UNIQUE,
    ipadress varchar(255) NOT NULL UNIQUE,
    server_password varchar(255) NOT NULL,
    server_public_key varchar(255) NOT NULL,
    dns varchar(255),
    PRIMARY KEY (id)
) engine=MyISAM;

CREATE TABLE peer (
    id BIGINT NOT NULL AUTO_INCREMENT,
    peer_ip varchar(21) NOT NULL,
    peer_private_key varchar(255) not null,
    peer_public_key varchar(255) not null,
    peer_conf_name varchar(255) not null,
    is_activated BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
) engine=MyISAM;

CREATE TABLE activate_token (
    id BIGINT NOT NULL AUTO_INCREMENT,
    token varchar(64) NOT NULL UNIQUE,
    new_role varchar(255) NOT NULL,
    PRIMARY KEY (id)
) engine=MyISAM;

CREATE TABLE subscription (
    id BIGINT NOT NULL AUTO_INCREMENT,
    role varchar(255) NOT NULL,
    price_in_rub INT NOT NULL DEFAULT 0,
    peers_available INT NOT NULL DEFAULT 0,
    days INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
) engine=MyISAM;