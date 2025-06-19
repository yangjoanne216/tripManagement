package fr.dauphine.miageIf.minh.yang.info_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "pointOfInterest")
@Data
public class PointOfInterest {
    @Id
    private String id;
    @Indexed(unique = true, name = "poi_name_idx")
    private String name;
    private List<String> photos;
    private String address;

    // Reference to City by ID
    private org.bson.types.ObjectId  cityId;

    private GeoInfo geoInfo;

    @Data
    public static class GeoInfo {
        private double lat;
        private double lon;
    }
}
