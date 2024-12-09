ALTER TABLE public.homeworks
    ADD IF NOT EXISTS deleted BOOLEAN DEFAULT false NOT NULL ;