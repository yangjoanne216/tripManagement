# Part I: 3 types BDD
### (!!!!just for rules of programming, and the real code for creating BDD is in the branche edit-database-scripts) 
## I. The 4 MongoDB collections:

1.	Collection city
```sql
{
  "_id": "C123",                  // cityId
  "name": "Montréal",
  "photos": ["url1", "url2"],
  "geoInfo": { "lat": 45.50, "lon": -73.57 },

  /* embedded lists: denormalized but convenient
     (i.e. aggregate a city in one read) */
  "accommodations": [
    { "id": "A987", "name": "Hotel X", "price": 120, "available": true }
  ],
  "pointsOfInterest": [
    { "id": "P321", "name": "Biodôme" }
  ]
}
```
2.	Collection pointOfInterest
```sql
{
  "_id": "P321",
  "name": "Biodôme",
  "photos": ["p1", "p2"],
  "address": "xxx, Montréal",
  "geoInfo": { "lat": 45.56, "lon": -73.55 },
  "city": { "id": "C123", "name": "Montréal" }   // reference or DBRef
}
```
3.	Collection activity
```sql
{
  "_id": "A555",
  "name": "Biodôme Tour",
  "pointOfInterest": { "id": "P321", "name": "Biodôme" },
  "photos": ["a1"],
  "seasons": ["April", "May", "June"],
  "price": { "adult": 24, "child": 15 }           // or a single “price” field
}
```
4.	Collection accommodation
```sql
{
  "_id": "A987",
  "name": "Hotel X",
  "address": "yyy, Montréal",
  "photos": ["h1"],
  "city": { "id": "C123", "name": "Montréal" },
  "price": 120,
  "available": true
}
```

## II. The three tables in PostgreSQL
5.	Table trip
```sql
CREATE TABLE trip (
  id           SERIAL PRIMARY KEY,
  name         TEXT,
  start_date   DATE,
  end_date     DATE
);
```
6.	Table trip_activity
```sql
CREATE TABLE trip_activity (
  trip_id       INT     REFERENCES trip(id),
  day           INT     NOT NULL,
  sequence      INT     NOT NULL,
  activity_id   TEXT    NOT NULL,
  PRIMARY KEY (trip_id, day, sequence)
);
```
7.	Table trip_accommodation
```sql
CREATE TABLE trip_accommodation (
  trip_id           INT   REFERENCES trip(id),
  day               INT   NOT NULL,
  accommodation_id  TEXT  NOT NULL,
  PRIMARY KEY (trip_id, day)
);
```

## III. A node type and a relationship type in Neo4j
8.	City node and “LOCATED_AT” relationship
```sql
// ——— Node ———
(:City {
  cityId:       STRING,   // logical PK
  name:         STRING,
  latitude:     FLOAT,
  longitude:    FLOAT
})
```
```sql
// ——— Relationship ———
(:City)-[:LOCATED_AT {
  distanceKm:    FLOAT,   // kilometers
  travelTimeMin: INT      // minutes
}]->(:City)
// Recommended index
CREATE CONSTRAINT city_id IF NOT EXISTS
ON (c:City) ASSERT c.cityId IS UNIQUE;
```


# Part II: endpoints
## Micro-services Overview

| Micro-service | Persistence | Main role |
|---------------|-------------|-----------|
| **info-api**  | MongoDB     | CRUD + search on content resources: cities, points-of-interest, activities, accommodations |
| **trip-api**  | PostgreSQL  | Build and manage day-by-day trip itineraries |
| **route-api** | Neo4j       | Manage the city graph: calculate distances, travel times, find routes |

---

## 1. info-api (MongoDB)

All endpoints consume and return **JSON**, and accept an optional `?fields=` query string to project only the needed attributes.

### 1.1 Cities

| Method | URI | Description |
|--------|-----|-------------|
| **POST**   | `/cities` | Create a new city |
| **PUT**    | `/cities/{cityId}` | Update an existing city |
| **DELETE** | `/cities/{cityId}` | Delete a city |
| **GET**    | `/cities` | List all cities |
| **GET**    | `/cities/{cityId}` | Get a single city (e.g. `?fields=name,accommodations,pointsOfInterest,geoInfo`) |

### 1.2 Points of Interest (POIs)

| Method | URI | Description |
|--------|-----|-------------|
| **POST**   | `/points-of-interest` | Create a POI |
| **PUT**    | `/points-of-interest/{poiId}` | Update a POI |
| **DELETE** | `/points-of-interest/{poiId}` | Delete a POI |
| **GET**    | `/points-of-interest` | List all POIs |
| **GET**    | `/points-of-interest/{poiId}` | Get a single POI |
| **GET**    | `/cities/{cityId}/points-of-interest` | List all POIs in a given city |

### 1.3 Activities

| Method | URI | Description |
|--------|-----|-------------|
| **POST**   | `/activities` | Create a new activity |
| **PUT**    | `/activities/{activityId}` | Update an activity |
| **DELETE** | `/activities/{activityId}` | Delete an activity |
| **GET**    | `/activities` | Search activities by any combination of filters:<br>`?cityId=&poiId=&name~=&priceMin=&priceMax=&season=` |
| **GET**    | `/activities/{activityId}` | Get activity details |

### 1.4 Accommodations

| Method | URI | Description |
|--------|-----|-------------|
| **POST**   | `/accommodations` | Create a new accommodation |
| **PUT**    | `/accommodations/{accommodationId}` | Update an accommodation |
| **DELETE** | `/accommodations/{accommodationId}` | Delete an accommodation |
| **GET**    | `/accommodations/{accommodationId}` | Get accommodation details |
| **GET**    | `/cities/{cityId}/accommodations` | List all accommodations in a city (`?maxPrice=&available=true`) |

---

## 2. trip-api (PostgreSQL)

This service manages the `trip` table plus the join tables `trip_activity` and `trip_accommodation`.

| Method | URI | Description |
|--------|-----|-------------|
| **POST**   | `/trips` | Create a new trip (body = full itinerary payload) |
| **PUT**    | `/trips/{tripId}` | Update all or part of a trip |
| **DELETE** | `/trips/{tripId}` | Delete a trip |
| **GET**    | `/trips/{tripId}` | Retrieve the complete itinerary for one trip |
| **GET**    | `/trips` | Search trips by criteria:<br>`?startCity=&endCity=&minDays=&maxDays=` |
| **GET**    | `/trips/{tripId}/points-of-interest` | List all cities + POIs + activities + accommodations used in this trip |
| **GET**    | `/trips/{tripId}/days/count` | Return the number of days in the trip |

### Trip payload example (`POST` / `PUT /trips` body)

```json
{
  "name":      "Eastern Canada Tour",
  "startDate": "2025-07-01",
  "endDate":   "2025-07-04",
  "days": [
    {
      "day":             1,
      "accommodationId": "507f191e810c19729de860d1",
      "activities": [
        { "activityId": "507f191e810c19729de860f1", "order": 1 },
        { "activityId": "507f191e810c19729de860f2", "order": 2 }
      ]
    }
    /* …more days… */
  ]
}
```
## 3. route-api (Neo4j)

Manages `:City` nodes and the `[:LOCATED_AT]` edges between them, plus path-finding.

### 3.1 City-graph queries

| Method | URI | Description |
|--------|-----|-------------|
| **GET** | `/cities` | (Mirror of info-api) list all city nodes |
| **GET** | `/cities/{cityId}/neighbors?maxDistanceKm=` | Find neighboring cities within the specified distance |

### 3.2 Connection (edge) CRUD

| Method | URI | Description |
|--------|-----|-------------|
| **POST** | `/edges` | Create a new edge:<br>`{ sourceCityId, destinationCityId, distanceKm, travelTimeMin }` |
| **PUT** | `/edges/{routeId}` | Update an edge’s distance or travel time |
| **DELETE** | `/edges/{routeId}` | Delete an edge |
| **GET** | `/edges?source=&destination=` | Get the direct distance + time between two cities (the existing edge) |

### 3.3 Path-finding

| Method | URI | Description |
|--------|-----|-------------|
| **GET** | `/itineraries?source=&destination=` | Shortest path (list of `cityId`s) |
| **GET** | `/itineraries?source=&destination=&maxStops=` | All routes from source → destination with ≤ `maxStops` intermediate nodes |


# Part III:  Initializing the Project with Spring Initializr
<img width="940" alt="Screenshot 2025-05-24 at 16 30 36" src="https://github.com/user-attachments/assets/7d37a4ae-26ed-4980-a143-d39ca7bf0885" />

## PS: We can also add **Lombox** dependency

# Part IV: Name of branches
Lowercase letters, separated by -

Examples: 

edit-database-scripts

trip-service

