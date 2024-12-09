CREATE SEQUENCE IF NOT EXISTS public.enrollments_sequence START WITH 1 INCREMENT BY 10;

CREATE TABLE IF NOT EXISTS pupil_answers
(
    id         BIGINT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    answer     VARCHAR(255),
    task_id    BIGINT,
    attempt    BIGINT,
    points     INTEGER,
    status     INTEGER,
    CONSTRAINT pk_pupil_answers PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tasks_checking
(
    id            BIGINT NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    updated_at    TIMESTAMP WITHOUT TIME ZONE,
    homework      BIGINT,
    task_id       BIGINT,
    checking_type INTEGER,
    CONSTRAINT pk_tasks_checking PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS enrolls RENAME TO enrollments;

ALTER TABLE public.admins
    ADD IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.admins
    ADD IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.checks
    ADD IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.checks
    ADD IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.enrollments
    ADD IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.enrollments
    ADD IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.homework_solutions
    ADD IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.homework_solutions
    ADD IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.homeworks
    ADD IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.homeworks
    ADD IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.payments
    ADD IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.payments
    ADD IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.pupils
    ADD IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.pupils
    ADD IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.subjects
    ADD IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.subjects
    ADD IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.supports
    ADD IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.supports
    ADD IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.tasks
    ADD IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.tasks
    ADD IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.tutors
    ADD IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.tutors
    ADD IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

DROP TABLE public.task_tables CASCADE;

ALTER TABLE public.homework_solutions
RENAME
COLUMN answers TO answers_legacy;

ALTER TABLE public.tasks
DROP
COLUMN IF EXISTS crdate;

ALTER TABLE public.homeworks
RENAME
COLUMN task_checking_types TO task_checking_types_legacy;

DROP SEQUENCE public.enrolls_sequence CASCADE;

ALTER TABLE public.tasks
    ADD IF NOT EXISTS answer_type INTEGER;