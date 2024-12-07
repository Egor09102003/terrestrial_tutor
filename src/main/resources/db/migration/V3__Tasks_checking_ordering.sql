ALTER TABLE public.tasks_checking
    ADD order_index INTEGER;

ALTER TABLE public.pupil_answers
    ADD CONSTRAINT FK_PUPIL_ANSWERS_ON_ATTEMPT FOREIGN KEY (attempt) REFERENCES public.homework_solutions (id);

ALTER TABLE public.pupil_answers
    ADD CONSTRAINT FK_PUPIL_ANSWERS_ON_TASK FOREIGN KEY (task_id) REFERENCES public.tasks (id);

ALTER TABLE public.tasks_checking
    ADD CONSTRAINT FK_TASKS_CHECKING_ON_HOMEWORK FOREIGN KEY (homework) REFERENCES public.homeworks (id);

ALTER TABLE public.tasks_checking
    ADD CONSTRAINT FK_TASKS_CHECKING_ON_TASK FOREIGN KEY (task_id) REFERENCES public.tasks (id);