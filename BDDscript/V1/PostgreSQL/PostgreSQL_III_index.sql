--add indexes to speed up queries
CREATE INDEX idx_trip_activity_trip_day
  ON trip_activity(trip_id, day);
CREATE INDEX idx_trip_activity_by_activity
  ON trip_activity(activity_id);

CREATE INDEX idx_trip_accommodation_trip_day
  ON trip_accommodation(trip_id, day);
CREATE INDEX idx_trip_accommodation_by_acco
  ON trip_accommodation(accommodation_id);