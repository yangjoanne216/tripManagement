-- 0) delete the trigger, function and table first.
DROP TRIGGER IF EXISTS trg_check_trip_day_activity ON trip_activity;
DROP TRIGGER IF EXISTS trg_check_trip_day_accommodation ON trip_accommodation;
DROP FUNCTION  IF EXISTS check_trip_day();

DROP TABLE IF EXISTS trip_accommodation;
DROP TABLE IF EXISTS trip_activity;
DROP TABLE IF EXISTS trip;

-- 1) Main table: trip
CREATE TABLE trip (
  id         SERIAL      PRIMARY KEY,
  name       TEXT        NOT NULL,
  start_date DATE        NOT NULL,
  end_date   DATE        NOT NULL,
  -- Ensure that the end date is no earlier than the start date
  CONSTRAINT chk_trip_dates CHECK (end_date >= start_date)
);

-- 2) Trigger function: Ensure day <= total trip days
CREATE OR REPLACE FUNCTION check_trip_day()
RETURNS TRIGGER AS $$
DECLARE
  max_days INT;
BEGIN
  SELECT (t.end_date - t.start_date + 1)
    INTO max_days
    FROM trip t
   WHERE t.id = NEW.trip_id;

  IF NEW.day > max_days THEN
    RAISE EXCEPTION
      'trip_id=% day=% Exceeding the total number of days for the trip %', NEW.trip_id, NEW.day, max_days;
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 3) trip_activity 
CREATE TABLE trip_activity (
  trip_id     INT        NOT NULL
    REFERENCES trip(id) ON DELETE CASCADE ON UPDATE CASCADE,
  day         INT        NOT NULL
    CONSTRAINT chk_activity_day_min CHECK (day >= 1),
  sequence    INT        NOT NULL,
  activity_id CHAR(24)   NOT NULL
    CONSTRAINT chk_activity_id_fmt CHECK (activity_id ~ '^[0-9a-f]{24}$'),
  PRIMARY KEY (trip_id, day, sequence)
);

-- 4) Bind trigger to trip_activity
CREATE TRIGGER trg_check_trip_day_activity
  BEFORE INSERT OR UPDATE ON trip_activity
  FOR EACH ROW EXECUTE FUNCTION check_trip_day();

-- 5) trip_accommodation
CREATE TABLE trip_accommodation (
  trip_id          INT       NOT NULL
    -- ON UPDATE/DELETE CASCADE
    REFERENCES trip(id) ON DELETE CASCADE ON UPDATE CASCADE,
  day              INT       NOT NULL
    CONSTRAINT chk_acco_day_min CHECK (day >= 1),
  accommodation_id CHAR(24)  NOT NULL
    CONSTRAINT chk_acco_id_fmt CHECK (accommodation_id ~ '^[0-9a-f]{24}$'),
  PRIMARY KEY (trip_id, day)
);

-- 6) Bind trigger to trip_accommodation
CREATE TRIGGER trg_check_trip_day_accommodation
  BEFORE INSERT OR UPDATE ON trip_accommodation
  FOR EACH ROW EXECUTE FUNCTION check_trip_day();


-- 7) Add nouveau
BEGIN;

-- 1) 定义或替换触发器函数（如果已存在会替换），放在最前面
CREATE OR REPLACE FUNCTION public.check_trip_day()
  RETURNS TRIGGER
AS $$
DECLARE
  max_days INT;
BEGIN
  SELECT (t.end_date - t.start_date + 1)
    INTO max_days
    FROM trip t
   WHERE t.id = NEW.trip_id;

  IF NEW.day > max_days THEN
    RAISE EXCEPTION
      'trip_id=% day=% Exceeding the total number of days for the trip %', NEW.trip_id, NEW.day, max_days;
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 2) 创建 trip_day 表
CREATE TABLE IF NOT EXISTS trip_day (
  trip_id   INT  NOT NULL
    REFERENCES trip(id) ON DELETE CASCADE,
  day       INT  NOT NULL,
  city_id   CHAR(24) NOT NULL
    CONSTRAINT chk_city_id_fmt CHECK (city_id ~ '^[0-9a-f]{24}$'),
  PRIMARY KEY (trip_id, day)
);

-- 3) 针对不同版本 Postgres 创建触发器
-- 如果是 Postgres 11+，使用 EXECUTE FUNCTION：
DO $$
BEGIN
  IF (SELECT current_setting('server_version_num')::int) >= 110000 THEN
    CREATE TRIGGER trg_check_trip_day_day
      BEFORE INSERT OR UPDATE ON trip_day
      FOR EACH ROW
      EXECUTE FUNCTION public.check_trip_day();
  ELSE
    -- 旧版用 EXECUTE PROCEDURE
    CREATE TRIGGER trg_check_trip_day_day
      BEFORE INSERT OR UPDATE ON trip_day
      FOR EACH ROW
      EXECUTE PROCEDURE public.check_trip_day();
  END IF;
END;
$$;

COMMIT;
COMMIT;