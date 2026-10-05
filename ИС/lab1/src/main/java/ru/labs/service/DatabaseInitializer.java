package ru.labs.service;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class DatabaseInitializer implements ApplicationListener<ContextRefreshedEvent> {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        String sql = """
                CREATE OR REPLACE FUNCTION delete_movies_by_genre(p_genre VARCHAR)
                RETURNS VOID AS $$
                BEGIN
                    DELETE FROM movies WHERE genre = p_genre;
                END;
                $$ LANGUAGE plpgsql;

                CREATE OR REPLACE FUNCTION get_movies_by_name_prefix(p_prefix VARCHAR)
                RETURNS SETOF movies AS $$
                BEGIN
                    RETURN QUERY SELECT * FROM movies WHERE name LIKE p_prefix || '%';
                END;
                $$ LANGUAGE plpgsql;

                CREATE OR REPLACE FUNCTION get_movies_by_genre_less_than(p_genre VARCHAR)
                RETURNS SETOF movies AS $$
                DECLARE
                    target_rank INT;
                BEGIN
                    target_rank := array_position(ARRAY['MUSICAL', 'ADVENTURE', 'THRILLER', 'HORROR', 'FANTASY']::VARCHAR[], p_genre);
                    RETURN QUERY
                    SELECT * FROM movies
                    WHERE array_position(ARRAY['MUSICAL', 'ADVENTURE', 'THRILLER', 'HORROR', 'FANTASY']::VARCHAR[], genre) < target_rank;
                END;
                $$ LANGUAGE plpgsql;

                CREATE OR REPLACE FUNCTION get_directors_without_oscars()
                RETURNS SETOF persons AS $$
                BEGIN
                    RETURN QUERY
                    SELECT DISTINCT p.*
                    FROM persons p
                    JOIN movies m ON p.id = m.director_id
                    WHERE m.oscars_count = 0 OR m.oscars_count IS NULL;
                END;
                $$ LANGUAGE plpgsql;


                CREATE OR REPLACE FUNCTION redistribute_oscars(from_genre VARCHAR, to_genre VARCHAR)
                RETURNS VOID AS $$
                DECLARE
                    total_oscars INT;
                    target_movies_count INT;
                    oscars_per_movie INT;
                BEGIN
                    SELECT COALESCE(SUM(oscars_count), 0) INTO total_oscars FROM movies WHERE genre = from_genre;
                    SELECT COUNT(*) INTO target_movies_count FROM movies WHERE genre = to_genre;

                    IF target_movies_count > 0 AND total_oscars > 0 THEN
                        oscars_per_movie := total_oscars / target_movies_count;

                        UPDATE movies SET oscars_count = NULL WHERE genre = from_genre;

                        IF oscars_per_movie > 0 THEN
                            UPDATE movies SET oscars_count = COALESCE(oscars_count, 0) + oscars_per_movie WHERE genre = to_genre;
                        END IF;
                    END IF;
                END;
                $$ LANGUAGE plpgsql;

                ALTER TABLE movies DROP CONSTRAINT IF EXISTS check_movie_name_not_empty;
                ALTER TABLE movies ADD CONSTRAINT check_movie_name_not_empty CHECK (length(trim(name)) > 0);

                ALTER TABLE movies DROP CONSTRAINT IF EXISTS check_movie_tagline_not_empty;
                ALTER TABLE movies ADD CONSTRAINT check_movie_tagline_not_empty CHECK (length(trim(tagline)) > 0);

                ALTER TABLE movies DROP CONSTRAINT IF EXISTS check_movie_budget_positive;
                ALTER TABLE movies ADD CONSTRAINT check_movie_budget_positive CHECK (budget > 0);

                ALTER TABLE movies DROP CONSTRAINT IF EXISTS check_movie_box_office_positive;
                ALTER TABLE movies ADD CONSTRAINT check_movie_box_office_positive CHECK (total_box_office > 0);

                ALTER TABLE movies DROP CONSTRAINT IF EXISTS check_movie_length_positive;
                ALTER TABLE movies ADD CONSTRAINT check_movie_length_positive CHECK (length > 0);

                ALTER TABLE movies DROP CONSTRAINT IF EXISTS check_movie_golden_palm_positive;
                ALTER TABLE movies ADD CONSTRAINT check_movie_golden_palm_positive CHECK (golden_palm_count > 0);

                ALTER TABLE movies DROP CONSTRAINT IF EXISTS check_movie_usa_box_positive;
                ALTER TABLE movies ADD CONSTRAINT check_movie_usa_box_positive CHECK (usa_box_office > 0);

                ALTER TABLE movies DROP CONSTRAINT IF EXISTS check_movie_oscars_positive;
                ALTER TABLE movies ADD CONSTRAINT check_movie_oscars_positive CHECK (oscars_count > 0 OR oscars_count IS NULL);

                ALTER TABLE coordinates DROP CONSTRAINT IF EXISTS check_coord_y_min;
                ALTER TABLE coordinates ADD CONSTRAINT check_coord_y_min CHECK (y > -176);

                ALTER TABLE persons DROP CONSTRAINT IF EXISTS check_person_weight_positive;
                ALTER TABLE persons ADD CONSTRAINT check_person_weight_positive CHECK (weight > 0);

                ALTER TABLE persons DROP CONSTRAINT IF EXISTS check_person_name_not_empty;
                ALTER TABLE persons ADD CONSTRAINT check_person_name_not_empty CHECK (length(trim(name)) > 0);

                """;

        em.createNativeQuery(sql).executeUpdate();
    }
}
