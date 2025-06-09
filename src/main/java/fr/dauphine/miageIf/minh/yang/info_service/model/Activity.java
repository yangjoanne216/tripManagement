package fr.dauphine.miageIf.minh.yang.info_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("activity")
@Data
public class Activity {
    @Id
    private String id;
    private String name;
    private PoiRef pointOfInterest;
    private List<String> photos;
    private List<String> seasons;
    private Price price;
    @Data public static class PoiRef { String id, name; }
    @Data public static class Price { double adult, child; }
}
