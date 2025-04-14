CREATE TABLE animals
(
    animal_id             SERIAL PRIMARY KEY,
    name                  VARCHAR(64) NOT NULL UNIQUE,
    height_cm             FLOAT,
    weight_kg             FLOAT,
    color                 VARCHAR(64),
    lifespan_years        INT,
    diet                  VARCHAR(64),
    habitat               VARCHAR(128),
    predators             VARCHAR(128),
    avg_speed_kmh         FLOAT,
    conservation_status   VARCHAR(64),
    family                VARCHAR(64),
    gestation_period_days INT,
    social_structure      VARCHAR(64),
    offspring_per_birth   INT
);