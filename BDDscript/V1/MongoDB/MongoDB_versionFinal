// 1) City collection: basic attribute validation only
db.createCollection("city", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["_id", "name", "photos", "geoInfo"],
      properties: {
        _id: {
          bsonType: "objectId",
          description: "Unique identifier"
        },
        name: {
          bsonType: "string",
          description: "City name"
        },
        photos: {
          bsonType: "array",
          description: "List of city photo URLs",
          items: {
            bsonType: "string",
            pattern: "^https?://",
            description: "Valid HTTP/HTTPS URL"
          }
        },
        geoInfo: {
          bsonType: "object",
          description: "Geographical coordinates",
          required: ["lat", "lon"],
          properties: {
            lat: {
              bsonType: ["double", "decimal"],
              minimum: -90,
              maximum: 90,
              description: "Latitude"
            },
            lon: {
              bsonType: ["double", "decimal"],
              minimum: -180,
              maximum: 180,
              description: "Longitude"
            }
          }
        }
      }
    }
  }
});

// 2) PointOfInterest collection: references using cityId
db.createCollection("pointOfInterest", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["_id", "name", "photos", "address", "geoInfo", "cityId"],
      properties: {
        _id: {
          bsonType: "objectId",
          description: "Unique identifier"
        },
        name: {
          bsonType: "string",
          description: "Point of interest name"
        },
        photos: {
          bsonType: "array",
          description: "List of photo URLs",
          items: {
            bsonType: "string",
            pattern: "^https?://",
            description: "Valid HTTP/HTTPS URL"
          }
        },
        address: {
          bsonType: "string",
          description: "Point of interest address"
        },
        geoInfo: {
          bsonType: "object",
          description: "Geographical coordinates",
          required: ["lat", "lon"],
          properties: {
            lat: {
              bsonType: ["double", "decimal"],
              minimum: -90,
              maximum: 90,
              description: "Latitude"
            },
            lon: {
              bsonType: ["double", "decimal"],
              minimum: -180,
              maximum: 180,
              description: "Longitude"
            }
          }
        },
        cityId: {
          bsonType: "objectId",
          description: "Reference to the City document's _id"
        }
      }
    }
  }
});

// 3) Activity collection: references using pointOfInterestId
db.createCollection("activity", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["_id", "name", "photos", "seasons", "price", "pointOfInterestId"],
      properties: {
        _id: {
          bsonType: "objectId",
          description: "Unique identifier"
        },
        name: {
          bsonType: "string",
          description: "Activity name"
        },
        photos: {
          bsonType: "array",
          description: "List of photo URLs",
          items: {
            bsonType: "string",
            pattern: "^https?://",
            description: "Valid HTTP/HTTPS URL"
          }
        },
        seasons: {
          bsonType: "array",
          description: "Activity seasons (month names)",
          items: {
            bsonType: "string",
            enum: [
              "January", "February", "March", "April",
              "May", "June", "July", "August",
              "September", "October", "November", "December"
            ]
          }
        },
        price: {
          bsonType: "object",
          required: ["adult", "child"],
          properties: {
            adult: {
              bsonType: ["int", "double", "decimal"],
              minimum: 0,
              description: "Adult price"
            },
            child: {
              bsonType: ["int", "double", "decimal"],
              minimum: 0,
              description: "Child price"
            }
          }
        },
        pointOfInterestId: {
          bsonType: "objectId",
          description: "Reference to the PointOfInterest document's _id"
        }
      }
    }
  }
});

// 4) Accommodation collection: references using cityId
db.createCollection("accommodation", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["_id", "name", "address", "photos", "price", "available", "cityId"],
      properties: {
        _id: {
          bsonType: "objectId",
          description: "Unique identifier"
        },
        name: {
          bsonType: "string",
          description: "Accommodation name"
        },
        address: {
          bsonType: "string",
          description: "Accommodation address"
        },
        photos: {
          bsonType: "array",
          description: "List of photo URLs",
          items: {
            bsonType: "string",
            pattern: "^https?://",
            description: "Valid HTTP/HTTPS URL"
          }
        },
        price: {
          bsonType: ["int", "double", "decimal"],
          minimum: 0,
          description: "Price"
        },
        available: {
          bsonType: "bool",
          description: "Availability status"
        },
        cityId: {
          bsonType: "objectId",
          description: "Reference to the City document's _id"
        }
      }
    }
  }
});

// (Create an index on city.name
db.city.createIndex({ name: 1 }, { unique: true, name: "city_name_idx" });

db.pointOfInterest.createIndex(
  { name: 1 },
  { unique: true, name: "poi_name_idx" }
);

db.accommodation.createIndex(
  { name: 1 },
  { unique: true, name: "acc_name_idx" }
);

db.activity.createIndex(
  { name: 1 },
  { unique: true, name: "act_name_idx" }
);