-- 1) 插入 4 个 trip
INSERT INTO public.trip(id, name, start_date, end_date) VALUES
  (1, 'parisLyonTrip1',      '2025-07-10', '2025-07-11'),
  (2, 'parisGrenobleTrip1',  '2025-08-05', '2025-08-06'),
  (3, 'lyonStrasbourgTrip1', '2025-09-01', '2025-09-02'),
  (4, 'toulouseLeHavreTrip1','2025-10-15', '2025-10-16');

-- 2) 对应的 trip_day（每天一条）
INSERT INTO public.trip_day(trip_id, day, city_id) VALUES
  -- Trip 1: Day1=Paris, Day2=Lyon
  (1, 1, '507f191e810c19729de860b1'),
  (1, 2, '507f191e810c19729de860b4'),
  -- Trip 2: Day1=Paris, Day2=Grenoble
  (2, 1, '507f191e810c19729de860b1'),
  (2, 2, '507f191e810c19729de860b6'),
  -- Trip 3: Day1=Lyon, Day2=Strasbourg
  (3, 1, '507f191e810c19729de860b4'),
  (3, 2, '507f191e810c19729de860b3'),
  -- Trip 4: Day1=Toulouse, Day2=LeHavre
  (4, 1, '507f191e810c19729de860b5'),
  (4, 2, '507f191e810c19729de860b8');

-- 3) 每天选一个 accommodation
--    （这里选各城市的第 1 条，ID 由 MongoDB 脚本生成）
INSERT INTO public.trip_accommodation(trip_id, day, accommodation_id) VALUES
  (1, 1, '507f191e810c19729de86033'),  -- accommodation1Paris
  (1, 2, '507f191e810c19729de86042'),  -- accommodation1Lyon
  (2, 1, '507f191e810c19729de86033'),  -- accommodation1Paris
  (2, 2, '507f191e810c19729de8604c'),  -- accommodation1Grenoble
  (3, 1, '507f191e810c19729de86042'),  -- accommodation1Lyon
  (3, 2, '507f191e810c19729de8603d'),  -- accommodation1Strasbourg
  (4, 1, '507f191e810c19729de86047'),  -- accommodation1Toulouse
  (4, 2, '507f191e810c19729de86056');  -- accommodation1LeHavre

-- 4) 每天插入 2 个 activity，按 sequence 1、2
--    （选各城市 POI1 和 POI2 的 activity1，各自的第一个和第二个 activity）
INSERT INTO public.trip_activity(trip_id, day, sequence, activity_id) VALUES
  -- Trip1 (Paris)
  (1, 1, 1, '507f191e810c19729de86021'),  -- activity1POI1Paris
  (1, 1, 2, '507f191e810c19729de86022'),  -- activity1POI2Paris
  -- Trip1 (Lyon)
  (1, 2, 1, '507f191e810c19729de86033'),  -- activity1POI1Lyon
  (1, 2, 2, '507f191e810c19729de86034'),  -- activity1POI2Lyon

  -- Trip2 (Paris)
  (2, 1, 1, '507f191e810c19729de86021'),
  (2, 1, 2, '507f191e810c19729de86022'),
  -- Trip2 (Grenoble)
  (2, 2, 1, '507f191e810c19729de86033'),
  (2, 2, 2, '507f191e810c19729de86034'),

  -- Trip3 (Lyon)
  (3, 1, 1, '507f191e810c19729de86033'),
  (3, 1, 2, '507f191e810c19729de86034'),
  -- Trip3 (Strasbourg)
  (3, 2, 1, '507f191e810c19729de8602d'),
  (3, 2, 2, '507f191e810c19729de86031'),

  -- Trip4 (Toulouse)
  (4, 1, 1, '507f191e810c19729de86039'),
  (4, 1, 2, '507f191e810c19729de8603a'),
  -- Trip4 (LeHavre)
  (4, 2, 1, '507f191e810c19729de8604b'),
  (4, 2, 2, '507f191e810c19729de8604f');
