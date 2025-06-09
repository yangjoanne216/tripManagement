package fr.dauphine.miageIf.minh.yang.info_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("pointOfInterest")
@Data
public class PointOfInterest {
    @Id
    private String id;
    private String name;
    private List<String> photos;
    private String address;
    private CityRef city;
    private GeoInfo geoInfo;
    @Data public static class CityRef { String id, name; }
    @Data public static class GeoInfo { double lat, lon; }
}
