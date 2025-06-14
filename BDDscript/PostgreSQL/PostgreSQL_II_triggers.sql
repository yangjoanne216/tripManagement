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
