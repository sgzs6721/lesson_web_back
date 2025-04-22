ALTER TABLE edu_course
    ADD UNIQUE INDEX idx_unique_name_campus_institution (name, campus_id, institution_id, deleted);