-- 1) Create tables
CREATE TABLE trip (
  id         SERIAL      PRIMARY KEY,
  name       TEXT        NOT NULL,
  start_date DATE        NOT NULL,
  end_date   DATE        NOT NULL
);

CREATE TABLE trip_activity (
  trip_id     INT     REFERENCES trip(id),
  day         INT     NOT NULL,
  sequence    INT     NOT NULL,
  activity_id TEXT    NOT NULL,
  PRIMARY KEY (trip_id, day, sequence)
);

CREATE TABLE trip_accommodation (
  trip_id          INT   REFERENCES trip(id),
  day              INT   NOT NULL,
  accommodation_id TEXT  NOT NULL,
  PRIMARY KEY (trip_id, day)
);

-- 2) Insert one sample trip (4-day)
INSERT INTO trip(id, name, start_date, end_date)
VALUES
  (1, 'Montreal & Toronto Tour', '2025-07-01', '2025-07-04');

-- 3) Map the 8 activities into the itinerary
INSERT INTO trip_activity(trip_id, day, sequence, activity_id) VALUES
  (1, 1, 1, '507f191e810c19729de860f1'),
  (1, 1, 2, '507f191e810c19729de860f2'),
  (1, 2, 1, '507f191e810c19729de860f3'),
  (1, 2, 2, '507f191e810c19729de860f4'),
  (1, 3, 1, '507f191e810c19729de860f5'),
  (1, 3, 2, '507f191e810c19729de860f6'),
  (1, 4, 1, '507f191e810c19729de860f7'),
  (1, 4, 2, '507f191e810c19729de860f8');

-- 4) Assign accommodations each night
INSERT INTO trip_accommodation(trip_id, day, accommodation_id) VALUES
  (1, 1, '507f191e810c19729de860d1'),
  (1, 2, '507f191e810c19729de860d2'),
  (1, 3, '507f191e810c19729de860d3'),
  (1, 4, '507f191e810c19729de860d4');