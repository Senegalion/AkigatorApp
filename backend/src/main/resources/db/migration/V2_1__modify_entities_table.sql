ALTER TABLE entities
    ADD COLUMN animal_id INT,
    ADD CONSTRAINT fk_entity_animal FOREIGN KEY (animal_id) REFERENCES animals (animal_id);