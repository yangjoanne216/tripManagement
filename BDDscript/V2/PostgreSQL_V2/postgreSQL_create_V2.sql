-- 0) 删除已有触发器、函数和表（按顺序保证关联依赖）
DROP TRIGGER IF EXISTS trg_check_trip_day_activity ON trip_activity;
DROP TRIGGER IF EXISTS trg_check_trip_day_accommodation ON trip_accommodation;
DROP TRIGGER IF EXISTS trg_check_trip_day_day ON trip_day;
DROP FUNCTION IF EXISTS public.check_trip_day();
DROP TABLE IF EXISTS trip_accommodation;
DROP TABLE IF EXISTS trip_activity;
DROP TABLE IF EXISTS trip_day;
DROP TABLE IF EXISTS trip;

-- 1) 主表：trip
CREATE TABLE public.trip (
  id         SERIAL PRIMARY KEY,
  name       TEXT   NOT NULL,
  start_date DATE   NOT NULL,
  end_date   DATE   NOT NULL,
  -- 保证结束日期不早于开始日期
  CONSTRAINT chk_trip_dates CHECK (end_date >= start_date)
);

-- 2) 触发器函数：检查 day 不超出行程天数
CREATE OR REPLACE FUNCTION public.check_trip_day()
RETURNS TRIGGER AS $$
DECLARE
  max_days INT;
BEGIN
  SELECT (t.end_date - t.start_date + 1)
    INTO max_days
    FROM public.trip t
   WHERE t.id = NEW.trip_id;

  IF NEW.day > max_days THEN
    RAISE EXCEPTION
      'trip_id=% day=% Exceeding the total number of days for the trip %',
      NEW.trip_id, NEW.day, max_days;
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 3) 表：trip_activity
CREATE TABLE public.trip_activity (
  trip_id     INT      NOT NULL
    REFERENCES public.trip(id)
      ON DELETE CASCADE ON UPDATE CASCADE,
  day         INT      NOT NULL
    CONSTRAINT chk_activity_day_min CHECK (day >= 1),
  sequence    INT      NOT NULL,
  activity_id CHAR(24) NOT NULL
    CONSTRAINT chk_activity_id_fmt CHECK (activity_id ~ '^[0-9a-f]{24}$'),
  PRIMARY KEY (trip_id, day, sequence)
);

-- 触发器绑定
CREATE TRIGGER trg_check_trip_day_activity
  BEFORE INSERT OR UPDATE ON public.trip_activity
  FOR EACH ROW EXECUTE FUNCTION public.check_trip_day();

-- 4) 表：trip_accommodation
CREATE TABLE public.trip_accommodation (
  trip_id          INT      NOT NULL
    REFERENCES public.trip(id)
      ON DELETE CASCADE ON UPDATE CASCADE,
  day              INT      NOT NULL
    CONSTRAINT chk_acco_day_min CHECK (day >= 1),
  accommodation_id CHAR(24) NOT NULL
    CONSTRAINT chk_acco_id_fmt CHECK (accommodation_id ~ '^[0-9a-f]{24}$'),
  PRIMARY KEY (trip_id, day)
);

-- 触发器绑定
CREATE TRIGGER trg_check_trip_day_accommodation
  BEFORE INSERT OR UPDATE ON public.trip_accommodation
  FOR EACH ROW EXECUTE FUNCTION public.check_trip_day();

-- 5) 表：trip_day
CREATE TABLE public.trip_day (
  trip_id   INT      NOT NULL
    REFERENCES public.trip(id)
      ON DELETE CASCADE ON UPDATE CASCADE,
  day       INT      NOT NULL
    CONSTRAINT chk_day_min CHECK (day >= 1),
  city_id   CHAR(24) NOT NULL
    CONSTRAINT chk_city_id_fmt CHECK (city_id ~ '^[0-9a-f]{24}$'),
  PRIMARY KEY (trip_id, day)
);

-- 触发器绑定
CREATE TRIGGER trg_check_trip_day_day
  BEFORE INSERT OR UPDATE ON public.trip_day
  FOR EACH ROW EXECUTE FUNCTION public.check_trip_day();
