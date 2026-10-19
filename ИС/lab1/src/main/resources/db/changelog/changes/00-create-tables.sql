CREATE TABLE IF NOT EXISTS app_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS locations (
    id BIGSERIAL PRIMARY KEY,
    x BIGINT NOT NULL,
    y REAL NOT NULL,
    name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS coordinates (
    id BIGSERIAL PRIMARY KEY,
    x BIGINT NOT NULL,
    y REAL NOT NULL
);

CREATE TABLE IF NOT EXISTS persons (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    eyecolor VARCHAR(50) NOT NULL,
    haircolor VARCHAR(50),
    location_id BIGINT NOT NULL REFERENCES locations(id) ON DELETE CASCADE,
    birthday TIMESTAMP,
    weight INT NOT NULL
);

CREATE TABLE IF NOT EXISTS movies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    coordinates_id BIGINT NOT NULL REFERENCES coordinates(id) ON DELETE CASCADE,
    creation_date TIMESTAMP NOT NULL,
    oscars_count BIGINT,
    budget REAL NOT NULL,
    total_box_office BIGINT NOT NULL,
    mpaa_rating VARCHAR(50),
    director_id BIGINT REFERENCES persons(id) ON DELETE SET NULL,
    screenwriter_id BIGINT REFERENCES persons(id) ON DELETE SET NULL,
    operator_id BIGINT NOT NULL REFERENCES persons(id) ON DELETE RESTRICT,
    length BIGINT NOT NULL,
    golden_palm_count INT NOT NULL,
    usa_box_office REAL NOT NULL,
    tagline VARCHAR(255) NOT NULL,
    genre VARCHAR(50) NOT NULL
);
