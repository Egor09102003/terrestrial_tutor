ALTER TABLE public.homeworks
    ADD deleted BOOLEAN DEFAULT false;

ALTER TABLE public.homeworks
    ALTER COLUMN deleted SET NOT NULL;