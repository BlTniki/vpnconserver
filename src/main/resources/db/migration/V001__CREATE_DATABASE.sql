CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    login varchar(255) UNIQUE NOT NULL,
    password varchar(255) NOT NULL,
    token varchar(255),
    role varchar(255) NOT NULL DEFAULT 'ACTIVATED_USER',
    telegram_id BIGINT,
    telegram_nickname varchar(255)
);

CREATE TABLE IF NOT EXISTS hosts (
    id BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    name varchar(255) UNIQUE NOT NULL,
    ipaddress varchar(255) NOT NULL,
    port INTEGER NOT NULL DEFAULT 80,
    host_internal_network_prefix varchar(255) NOT NULL,
    host_password varchar(255) NOT NULL,
    host_public_key varchar(255) NOT NULL,

    CONSTRAINT ipaddress_password UNIQUE (ipaddress, port)
);

CREATE TABLE IF NOT EXISTS peers (
    id BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    peer_conf_name varchar(255) NOT NULL,
    peer_ip varchar(255) NOT NULL,
    peer_private_key varchar(255) NOT NULL,
    peer_public_key varchar(255) NOT NULL,
    is_activated BOOLEAN DEFAULT TRUE NOT NULL,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    host_id BIGINT REFERENCES hosts(id) ON DELETE SET NULL,

    CONSTRAINT peer_ip_host_id UNIQUE (peer_ip, host_id)
);