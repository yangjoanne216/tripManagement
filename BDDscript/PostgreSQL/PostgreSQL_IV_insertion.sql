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