ALTER TABLE owners
    ADD password VARCHAR(255);

ALTER TABLE owners
    ADD role VARCHAR(255);

ALTER TABLE owners
    ADD username VARCHAR(255);

ALTER TABLE owners
DROP
COLUMN name;

UPDATE owners
    SET password = 'default', role = 'ROLE_USER';

INSERT INTO owners (username, password, role)
VALUES ('base-admin', '$2a$10$eImiTXuWVxfM37uY4JANjQ==', 'ROLE_ADMIN');