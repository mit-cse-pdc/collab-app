-- Begin transaction
BEGIN;

-- Insert school and store the ID
WITH inserted_school AS (
    INSERT INTO schools (
                         school_id,
                         name,
                         created_at,
                         updated_at
        )
        VALUES (gen_random_uuid(),
                'School of Computing',
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP)
        RETURNING school_id)
-- Insert faculty using the returned school_id
INSERT
INTO faculty (faculty_id,
              school_id,
              email,
              password,
              position,
              name,
              created_at,
              updated_at)
SELECT gen_random_uuid(),
       school_id,
       'mohan.shenoy@manipal.edu',
       '$2a$12$WYkCKN0FkaZAp85WbdB14uNJukjGz4rRr1KxeOnG8nMgdm5NybSD.',
       'PROFESSOR_OF_PRACTICE'::faculty_position,
       'Mohandas Shenoy P',
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
FROM inserted_school;

-- Commit transaction
COMMIT;