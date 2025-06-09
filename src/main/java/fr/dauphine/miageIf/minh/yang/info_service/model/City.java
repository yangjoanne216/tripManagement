package fr.dauphine.miageIf.minh.yang.info_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "city")
@Data
public class City {
    @Id
    private String id;
    private String name;
    private GeoInfo geoInfo;
    private List<String> photos;
    private List<AccommodationRef> accommodations;
    private List<PointOfInterestRef> pointsOfInterest;
    // nested classes
    @Data public static class GeoInfo { double lat, lon; }
    @Data public static class AccommodationRef { String id, name; double price; boolean available; }
    @Data public static class PointOfInterestRef { String id, name; }
}
