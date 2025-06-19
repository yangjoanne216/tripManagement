package fr.dauphine.miageIf.minh.yang.info_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "city")
@Data
public class City {
    @Id
    private String id;

    @Indexed(unique = true, name = "city_name_idx")
    private String name;

    private GeoInfo geoInfo;
    private List<String> photos;

    @Data
    public static class GeoInfo {
        private double lat;
        private double lon;
    }
}
