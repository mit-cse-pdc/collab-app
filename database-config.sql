-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Create ENUM types
CREATE TYPE faculty_position AS ENUM ('PROFESSOR', 'ASSOCIATE_PROFESSOR', 'ASSISTANT_PROFESSOR', 'ADDITIONAL_PROFESSOR', 'PROFESSOR_OF_PRACTICE');
CREATE TYPE course_status AS ENUM ('ACTIVE', 'INACTIVE');
CREATE TYPE question_type AS ENUM ('MULTIPLE_CHOICE', 'TRUE_FALSE', 'SHORT_ANSWER');
CREATE TYPE lecture_status AS ENUM ('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED');
CREATE TYPE lecture_question_status AS ENUM ('PENDING', 'ACTIVE', 'COMPLETED');

-- Schools table
CREATE TABLE schools
(
    school_id  UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    name       VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Specializations table
CREATE TABLE specializations
(
    specialization_id UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    school_id         UUID                NOT NULL REFERENCES schools (school_id) ON DELETE CASCADE,
    name              VARCHAR(255) UNIQUE NOT NULL,
    created_at        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Faculty table (User Service)
CREATE TABLE faculty
(
    faculty_id UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    school_id  UUID                NOT NULL REFERENCES schools (school_id) ON DELETE CASCADE,
    position   faculty_position    NOT NULL,
    name       VARCHAR(255)        NOT NULL,
    email      VARCHAR(255) UNIQUE NOT NULL,
    password   VARCHAR(255)        NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Courses table
CREATE TABLE courses
(
    course_id         UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    specialization_id UUID               NOT NULL REFERENCES specializations (specialization_id) ON DELETE CASCADE,
    course_code       VARCHAR(50) UNIQUE NOT NULL,
    name              VARCHAR(255)       NOT NULL,
    description       TEXT,
    credits           INTEGER            NOT NULL,
    semester          INTEGER            NOT NULL,
    academic_year     INTEGER            NOT NULL,
    status            course_status            DEFAULT 'ACTIVE',
    created_at        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Faculty Courses table
CREATE TABLE faculty_courses
(
    faculty_course_id UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    faculty_id        UUID NOT NULL REFERENCES faculty (faculty_id) ON DELETE CASCADE,
    course_id         UUID NOT NULL REFERENCES courses (course_id) ON DELETE CASCADE,
    created_at        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Chapters table
CREATE TABLE chapters
(
    chapter_id  UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    course_id   UUID         NOT NULL REFERENCES courses (course_id) ON DELETE CASCADE,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    chapter_no  INTEGER      NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Questions table
CREATE TABLE questions
(
    question_id   UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    chapter_id    UUID          NOT NULL REFERENCES chapters (chapter_id) ON DELETE CASCADE,
    faculty_id    UUID          NOT NULL REFERENCES faculty (faculty_id) ON DELETE CASCADE,
    title         VARCHAR(255)  NOT NULL,
    text          TEXT          NOT NULL,
    question_type question_type NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Answers table
CREATE TABLE answers
(
    answer_id   UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    question_id UUID    NOT NULL REFERENCES questions (question_id) ON DELETE CASCADE,
    text        TEXT    NOT NULL,
    is_correct  BOOLEAN NOT NULL,
    explanation TEXT,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Lectures table
CREATE TABLE lectures
(
    lecture_id UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    faculty_id UUID         NOT NULL REFERENCES faculty (faculty_id) ON DELETE CASCADE,
    chapter_id UUID         NOT NULL REFERENCES chapters (chapter_id) ON DELETE CASCADE,
    title      VARCHAR(255) NOT NULL,
    status     lecture_status           DEFAULT 'SCHEDULED',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Lecture Questions table
CREATE TABLE lecture_questions
(
    lecture_question_id UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    lecture_id          UUID NOT NULL REFERENCES lectures (lecture_id) ON DELETE CASCADE,
    question_id         UUID NOT NULL REFERENCES questions (question_id) ON DELETE CASCADE,
    status              lecture_question_status  DEFAULT 'PENDING',
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Students table (User Service)
CREATE TABLE students
(
    student_id      UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    school_id       UUID                NOT NULL REFERENCES schools (school_id) ON DELETE CASCADE,
    registration_no VARCHAR(50) UNIQUE  NOT NULL,
    name            VARCHAR(255)        NOT NULL,
    email           VARCHAR(255) UNIQUE NOT NULL,
    password        VARCHAR(255)        NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Enrollments table
CREATE TABLE enrollments
(
    enrollment_id UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    student_id    UUID NOT NULL REFERENCES students (student_id) ON DELETE CASCADE,
    course_id     UUID NOT NULL REFERENCES courses (course_id) ON DELETE CASCADE,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Student Responses table
CREATE TABLE student_responses
(
    response_id         UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    student_id          UUID NOT NULL REFERENCES students (student_id) ON DELETE CASCADE,
    lecture_question_id UUID NOT NULL REFERENCES lecture_questions (lecture_question_id) ON DELETE CASCADE,
    answer_id           UUID NOT NULL REFERENCES answers (answer_id) ON DELETE CASCADE,
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Refresh Token table
CREATE TABLE refresh_tokens
(
    token_id   UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    user_id    UUID                     NOT NULL,
    token      VARCHAR(255) UNIQUE      NOT NULL,
    revoked    BOOLEAN                  DEFAULT FALSE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Function to delete expired refresh tokens
CREATE OR REPLACE FUNCTION cleanup_expired_refresh_tokens()
    RETURNS TRIGGER AS $$
BEGIN
    -- Delete tokens that have expired
    DELETE FROM refresh_tokens
    WHERE expires_at < CURRENT_TIMESTAMP
       OR revoked = true;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger that runs periodically
CREATE OR REPLACE TRIGGER trigger_cleanup_expired_refresh_tokens
    AFTER INSERT OR UPDATE ON refresh_tokens
    FOR EACH STATEMENT
EXECUTE FUNCTION cleanup_expired_refresh_tokens();


-- Create indexes for frequently queried columns and foreign keys
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens (token);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_revoked ON refresh_tokens(revoked);
CREATE INDEX idx_students_registration_no ON students (registration_no);
CREATE INDEX idx_courses_course_code ON courses (course_code);
CREATE INDEX idx_specializations_school_id ON specializations (school_id);
CREATE INDEX idx_faculty_school_id ON faculty (school_id);
CREATE INDEX idx_courses_specialization_id ON courses (specialization_id);
CREATE INDEX idx_chapters_course_id ON chapters (course_id);
CREATE INDEX idx_questions_chapter_id ON questions (chapter_id);
CREATE INDEX idx_questions_faculty_id ON questions (faculty_id);
CREATE INDEX idx_answers_question_id ON answers (question_id);
CREATE INDEX idx_lectures_faculty_id ON lectures (faculty_id);
CREATE INDEX idx_lectures_chapter_id ON lectures (chapter_id);
CREATE INDEX idx_lecture_questions_lecture_id ON lecture_questions (lecture_id);
CREATE INDEX idx_lecture_questions_question_id ON lecture_questions (question_id);
CREATE INDEX idx_enrollments_student_id ON enrollments (student_id);
CREATE INDEX idx_enrollments_course_id ON enrollments (course_id);
CREATE INDEX idx_student_responses_student_id ON student_responses (student_id);
CREATE INDEX idx_student_responses_lecture_question_id ON student_responses (lecture_question_id);
CREATE INDEX idx_student_responses_answer_id ON student_responses (answer_id);