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

-- 4) 绑定触发器到 trip_activity
CREATE TRIGGER trg_check_trip_day_activity
  BEFORE INSERT OR UPDATE ON trip_activity
  FOR EACH ROW EXECUTE FUNCTION check_trip_day();

-- 5) 子表：trip_accommodation
CREATE TABLE trip_accommodation (
  trip_id          INT       NOT NULL
    -- 同样 ON UPDATE/DELETE CASCADE
    REFERENCES trip(id) ON DELETE CASCADE ON UPDATE CASCADE,
  day              INT       NOT NULL
    CONSTRAINT chk_acco_day_min CHECK (day >= 1),
  accommodation_id CHAR(24)  NOT NULL
    CONSTRAINT chk_acco_id_fmt CHECK (accommodation_id ~ '^[0-9a-f]{24}$'),
  PRIMARY KEY (trip_id, day)
);

-- 6) 绑定触发器到 trip_accommodation
CREATE TRIGGER trg_check_trip_day_accommodation
  BEFORE INSERT OR UPDATE ON trip_accommodation
  FOR EACH ROW EXECUTE FUNCTION check_trip_day();

-- 7) 为常用查询添加索引
CREATE INDEX idx_trip_activity_trip_day
  ON trip_activity(trip_id, day);
CREATE INDEX idx_trip_activity_by_activity
  ON trip_activity(activity_id);

CREATE INDEX idx_trip_accommodation_trip_day
  ON trip_accommodation(trip_id, day);
CREATE INDEX idx_trip_accommodation_by_acco
  ON trip_accommodation(accommodation_id);

-- 8) 示例数据插入
INSERT INTO trip(id, name, start_date, end_date)
VALUES
  (1, 'Montreal & Toronto Tour', '2025-07-01', '2025-07-04');

INSERT INTO trip_activity(trip_id, day, sequence, activity_id) VALUES
  (1, 1, 1, '507f191e810c19729de860f1'),
  (1, 1, 2, '507f191e810c19729de860f2'),
  (1, 2, 1, '507f191e810c19729de860f3'),
  (1, 2, 2, '507f191e810c19729de860f4'),
  (1, 3, 1, '507f191e810c19729de860f5'),
  (1, 3, 2, '507f191e810c19729de860f6'),
  (1, 4, 1, '507f191e810c19729de860f7'),
  (1, 4, 2, '507f191e810c19729de860f8');

INSERT INTO trip_accommodation(trip_id, day, accommodation_id) VALUES
  (1, 1, '507f191e810c19729de860d1'),
  (1, 2, '507f191e810c19729de860d2'),
  (1, 3, '507f191e810c19729de860d3'),
  (1, 4, '507f191e810c19729de860d4');
