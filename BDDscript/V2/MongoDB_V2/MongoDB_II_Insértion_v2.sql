// 1) 插入 City
const cities = [
  {_id: ObjectId("507f191e810c19729de860a1"), name: "Montréal",  photos: ["https://example.com/montréal1.jpg","https://example.com/montréal2.jpg"], geoInfo: {lat:45.50, lon:-73.57}},
  {_id: ObjectId("507f191e810c19729de860a2"), name: "Toronto",   photos: ["https://example.com/toronto1.jpg","https://example.com/toronto2.jpg"],   geoInfo: {lat:43.65, lon:-79.38}},
  {_id: ObjectId("507f191e810c19729de860b1"), name: "Paris",     photos: ["https://example.com/paris1.jpg","https://example.com/paris2.jpg"],       geoInfo: {lat:48.8566, lon:2.3522}},
  {_id: ObjectId("507f191e810c19729de860b2"), name: "Amiens",    photos: ["https://example.com/amiens1.jpg","https://example.com/amiens2.jpg"],     geoInfo: {lat:49.8941, lon:2.2958}},
  {_id: ObjectId("507f191e810c19729de860b3"), name: "Strasbourg",photos: ["https://example.com/strasbourg1.jpg","https://example.com/strasbourg2.jpg"], geoInfo: {lat:48.5734, lon:7.7521}},
  {_id: ObjectId("507f191e810c19729de860b4"), name: "Lyon",      photos: ["https://example.com/lyon1.jpg","https://example.com/lyon2.jpg"],         geoInfo: {lat:45.7640, lon:4.8357}},
  {_id: ObjectId("507f191e810c19729de860b5"), name: "Toulouse",  photos: ["https://example.com/toulouse1.jpg","https://example.com/toulouse2.jpg"],   geoInfo: {lat:43.6047, lon:1.4442}},
  {_id: ObjectId("507f191e810c19729de860b6"), name: "Grenoble",  photos: ["https://example.com/grenoble1.jpg","https://example.com/grenoble2.jpg"],   geoInfo: {lat:45.1885, lon:5.7245}},
  {_id: ObjectId("507f191e810c19729de860b7"), name: "Dijon",     photos: ["https://example.com/dijon1.jpg","https://example.com/dijon2.jpg"],       geoInfo: {lat:47.3220, lon:5.0415}},
  {_id: ObjectId("507f191e810c19729de860b8"), name: "LeHavre",   photos: ["https://example.com/lehavre1.jpg","https://example.com/lehavre2.jpg"],     geoInfo: {lat:49.4944, lon:0.1079}}
];
db.city.insertMany(cities);


// 2) 插入 PointOfInterest（每 city 2 条）
const poiDocs = [];
cities.forEach((city, ci) => {
  for (let pi = 1; pi <= 2; pi++) {
    const base = city._id.toHexString().slice(0, 22);
    const suffix = (ci * 2 + pi).toString(16).padStart(2, '0');
    const id = ObjectId(base + suffix);
    poiDocs.push({
      _id: id,
      name: `POI${pi}${city.name}`,
      photos: [`https://example.com/${city.name}-poi${pi}-1.jpg`,`https://example.com/${city.name}-poi${pi}-2.jpg`],
      address: `${pi} ${city.name} Main St`,
      geoInfo: city.geoInfo,
      cityId: city._id
    });
  }
});
db.pointOfInterest.insertMany(poiDocs);


// 3) 插入 Activity（每 POI 3 条）
const actDocs = [];
poiDocs.forEach((poi, idx) => {
  for (let ai = 1; ai <= 3; ai++) {
    const base = poi._id.toHexString().slice(0, 22);
    const suffix = (20 + idx * 3 + ai).toString(16).padStart(2, '0');
    const id = ObjectId(base + suffix);
    actDocs.push({
      _id: id,
      name: `activity${ai}${poi.name}`,
      photos: [`https://example.com/${poi.name}-activity${ai}-1.jpg`],
      seasons: ["June", "July"],
      price: { adult: 50 + ai * 10, child: 25 + ai * 5 },
      pointOfInterestId: poi._id
    });
  }
});
db.activity.insertMany(actDocs);


// 4) 插入 Accommodation（每 city 5 条）
const accDocs = [];
cities.forEach((city, ci) => {
  for (let ai = 1; ai <= 5; ai++) {
    const base = city._id.toHexString().slice(0, 22);
    const suffix = (40 + ci * 5 + ai).toString(16).padStart(2, '0');
    const id = ObjectId(base + suffix);
    accDocs.push({
      _id: id,
      name: `accommodation${ai}${city.name}`,
      address: `${ai} ${city.name} Blvd`,
      photos: [`https://example.com/${city.name}-acc${ai}-1.jpg`],
      price: 100 + ai * 20,
      available: ai % 2 === 0,
      cityId: city._id
    });
  }
});
db.accommodation.insertMany(accDocs);
