-- 0) 如果已存在，先删除触发器、函数和表
DROP TRIGGER IF EXISTS trg_check_trip_day_activity ON trip_activity;
DROP TRIGGER IF EXISTS trg_check_trip_day_accommodation ON trip_accommodation;
DROP FUNCTION  IF EXISTS check_trip_day();

DROP TABLE IF EXISTS trip_accommodation;
DROP TABLE IF EXISTS trip_activity;
DROP TABLE IF EXISTS trip;

-- 1) 主表：trip
CREATE TABLE trip (
  id         SERIAL      PRIMARY KEY,
  name       TEXT        NOT NULL,
  start_date DATE        NOT NULL,
  end_date   DATE        NOT NULL,
  -- 保证结束日期不早于开始日期
  CONSTRAINT chk_trip_dates CHECK (end_date >= start_date)
);

-- 2) 触发器函数：确保 day <= 行程总天数
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
      'trip_id=% day=% 超出行程总天数 %', NEW.trip_id, NEW.day, max_days;
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 3) 子表：trip_activity
CREATE TABLE trip_activity (
  trip_id     INT        NOT NULL
    -- 外键：更新主表 id 时级联，删除主表时级联
    REFERENCES trip(id) ON DELETE CASCADE ON UPDATE CASCADE,
  day         INT        NOT NULL
    CONSTRAINT chk_activity_day_min CHECK (day >= 1),
  sequence    INT        NOT NULL,
  activity_id CHAR(24)   NOT NULL
    CONSTRAINT chk_activity_id_fmt CHECK (activity_id ~ '^[0-9a-f]{24}$'),
  PRIMARY KEY (trip_id, day, sequence)
);



