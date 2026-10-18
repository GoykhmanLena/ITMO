--liquibase formatted sql

--changeset lab1:02-add-constraints splitStatements:true
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
