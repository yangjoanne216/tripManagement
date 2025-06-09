package fr.dauphine.miageIf.minh.yang.info_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("accommodation")
@Data
public class Accommodation {
    @Id
    private String id;
    private String name;
    private String address;
    private List<String> photos;
    private CityRef city;
    private double price;
    private boolean available;
    @Data public static class CityRef { String id, name; }
}
