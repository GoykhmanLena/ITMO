DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM app_users WHERE username = 'user1') THEN
        INSERT INTO app_users (username, password)
        VALUES ('user1', '$2a$10$FRGF9D4Mudco97/PO6yDNu3aXA3inmLK7Xl/dm7R2Hmnbc6QqaSvG');
    END IF;

    IF NOT EXISTS (SELECT 1 FROM app_users WHERE username = 'user2') THEN
        INSERT INTO app_users (username, password)
        VALUES ('user2', '$2a$10$FRGF9D4Mudco97/PO6yDNu3aXA3inmLK7Xl/dm7R2Hmnbc6QqaSvG');
    END IF;

    IF NOT EXISTS (SELECT 1 FROM persons WHERE name IN ('Andrew', 'Maksim', 'Petr', 'Anastasya')) THEN
        INSERT INTO locations (x, y, name) VALUES (10, 20.0, 'Saint Petersburg');
        INSERT INTO locations (x, y, name) VALUES (55, 37.5, 'Moscow');
        INSERT INTO locations (x, y, name) VALUES (100, 200.0, 'London');
        INSERT INTO locations (x, y, name) VALUES (150, 250.0, 'Los Angeles');

        INSERT INTO persons (name, eyecolor, haircolor, weight, location_id)
        VALUES ('Andrew', 'GREEN', 'BLACK', 75, (SELECT id FROM locations WHERE name = 'Saint Petersburg' LIMIT 1));

        INSERT INTO persons (name, eyecolor, haircolor, weight, location_id)
        VALUES ('Maksim', 'BLACK', 'ORANGE', 80, (SELECT id FROM locations WHERE name = 'Moscow' LIMIT 1));

        INSERT INTO persons (name, eyecolor, haircolor, weight, location_id)
        VALUES ('Petr', 'ORANGE', 'GREEN', 85, (SELECT id FROM locations WHERE name = 'London' LIMIT 1));

        INSERT INTO persons (name, eyecolor, haircolor, weight, location_id)
        VALUES ('Anastasya', 'GREEN', 'BLACK', 55, (SELECT id FROM locations WHERE name = 'Los Angeles' LIMIT 1));
    END IF;

    IF NOT EXISTS (SELECT 1 FROM movies WHERE name IN ('Moana', 'Movie1', 'Movie2', 'Masha and bear', 'Luntik')) THEN
        INSERT INTO coordinates (x, y) VALUES (10, 20.5);
        INSERT INTO coordinates (x, y) VALUES (15, 30.0);
        INSERT INTO coordinates (x, y) VALUES (25, 40.0);
        INSERT INTO coordinates (x, y) VALUES (5, -50.0);
        INSERT INTO coordinates (x, y) VALUES (100, 150.0);

        INSERT INTO movies (name, tagline, genre, budget, total_box_office, usa_box_office, oscars_count, golden_palm_count, length, mpaa_rating, creation_date, coordinates_id, operator_id, director_id, screenwriter_id)
        VALUES (
            'Moana',
            'The ocean is calling',
            'ADVENTURE',
            1500809.0,
            687000,
            2487574.0,
            2,
            1,
            107,
            'PG',
            NOW(),
            (SELECT id FROM coordinates WHERE x = 10 AND y = 20.5 LIMIT 1),
            (SELECT id FROM persons WHERE name = 'Andrew' LIMIT 1),
            (SELECT id FROM persons WHERE name = 'Maksim' LIMIT 1),
            (SELECT id FROM persons WHERE name = 'Petr' LIMIT 1)
        );

        INSERT INTO movies (name, tagline, genre, budget, total_box_office, usa_box_office, oscars_count, golden_palm_count, length, mpaa_rating, creation_date, coordinates_id, operator_id, director_id, screenwriter_id)
        VALUES (
            'Movie1',
            'Blablabla',
            'THRILLER',
            5003578.0,
            12000,
            60000.0,
            1,
            1,
            120,
            'PG_13',
            NOW(),
            (SELECT id FROM coordinates WHERE x = 15 AND y = 30.0 LIMIT 1),
            (SELECT id FROM persons WHERE name = 'Petr' LIMIT 1),
            (SELECT id FROM persons WHERE name = 'Andrew' LIMIT 1),
            (SELECT id FROM persons WHERE name = 'Anastasya' LIMIT 1)
        );

        INSERT INTO movies (name, tagline, genre, budget, total_box_office, usa_box_office, oscars_count, golden_palm_count, length, mpaa_rating, creation_date, coordinates_id, operator_id, director_id, screenwriter_id)
        VALUES (
            'Movie2',
            'Wiiiiiiiiiiii',
            'FANTASY',
            800567.0,
            2500000,
            100000.0,
            3,
            2,
            135,
            'G',
            NOW(),
            (SELECT id FROM coordinates WHERE x = 25 AND y = 40.0 LIMIT 1),
            (SELECT id FROM persons WHERE name = 'Anastasya' LIMIT 1),
            (SELECT id FROM persons WHERE name = 'Petr' LIMIT 1),
            (SELECT id FROM persons WHERE name = 'Maksim' LIMIT 1)
        );

        INSERT INTO movies (name, tagline, genre, budget, total_box_office, usa_box_office, oscars_count, golden_palm_count, length, mpaa_rating, creation_date, coordinates_id, operator_id, director_id, screenwriter_id)
        VALUES (
            'Masha and bear',
            'Hihi haha',
            'MUSICAL',
            775482.0,
            50000,
            150000.0,
            NULL,
            1,
            75,
            'G',
            NOW(),
            (SELECT id FROM coordinates WHERE x = 5 AND y = -50.0 LIMIT 1),
            (SELECT id FROM persons WHERE name = 'Andrew' LIMIT 1),
            (SELECT id FROM persons WHERE name = 'Anastasya' LIMIT 1),
            NULL
        );

        INSERT INTO movies (name, tagline, genre, budget, total_box_office, usa_box_office, oscars_count, golden_palm_count, length, mpaa_rating, creation_date, coordinates_id, operator_id, director_id, screenwriter_id)
        VALUES (
            'Luntik',
            'I was born on the Moon',
            'ADVENTURE',
            57764.0,
            80000,
            20000.0,
            1,
            1,
            85,
            'G',
            NOW(),
            (SELECT id FROM coordinates WHERE x = 100 AND y = 150.0 LIMIT 1),
            (SELECT id FROM persons WHERE name = 'Maksim' LIMIT 1),
            (SELECT id FROM persons WHERE name = 'Andrew' LIMIT 1),
            (SELECT id FROM persons WHERE name = 'Petr' LIMIT 1)
        );
    END IF;
END $$;
