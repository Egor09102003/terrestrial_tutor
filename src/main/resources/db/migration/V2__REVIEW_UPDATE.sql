CREATE SEQUENCE IF NOT EXISTS public.enrollments_sequence START WITH 1 INCREMENT BY 10;

CREATE TABLE pupil_answers
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

CREATE TABLE tasks_checking
(
    id            BIGINT NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    updated_at    TIMESTAMP WITHOUT TIME ZONE,
    homework      BIGINT,
    task_id       BIGINT,
    checking_type INTEGER,
    CONSTRAINT pk_tasks_checking PRIMARY KEY (id)
);

ALTER TABLE public.admins
    ADD created_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.admins
    ADD updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.checks
    ADD created_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.checks
    ADD updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.enrollments
    ADD created_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.enrollments
    ADD updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.homework_solutions
    ADD created_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.homework_solutions
    ADD updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.homeworks
    ADD created_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.homeworks
    ADD updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.payments
    ADD created_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.payments
    ADD updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.pupils
    ADD created_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.pupils
    ADD updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.subjects
    ADD created_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.subjects
    ADD updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.supports
    ADD created_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.supports
    ADD updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.tasks
    ADD created_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.tasks
    ADD updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.tutors
    ADD created_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE public.tutors
    ADD updated_at TIMESTAMP WITHOUT TIME ZONE;

DROP TABLE public.task_tables CASCADE;

ALTER TABLE public.homework_solutions
DROP
COLUMN answers;

ALTER TABLE public.tasks
DROP
COLUMN crdate;

ALTER TABLE public.tasks
DROP
COLUMN answer_type;

ALTER TABLE public.homeworks
DROP
COLUMN task_checking_types;

DROP SEQUENCE public.enrolls_sequence CASCADE;

ALTER TABLE public.tasks
    ADD answer_type INTEGER;