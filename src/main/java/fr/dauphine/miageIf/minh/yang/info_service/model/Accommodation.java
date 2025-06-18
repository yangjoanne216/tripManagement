package fr.dauphine.miageIf.minh.yang.info_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "accommodation")
@Data
public class Accommodation {
    @Id
    private String id;

    private String name;
    private String address;
    private List<String> photos;

    // Reference to City by ID
    private org.bson.types.ObjectId cityId;

    private double price;
    private boolean available;
}
