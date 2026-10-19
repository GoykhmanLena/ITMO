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
