package fr.dauphine.miageIf.minh.yang.info_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "activity")
@Data
public class Activity {
    @Id
    private String id;

    @Indexed(unique = true, name = "act_name_idx")
    private String name;
    private List<String> photos;
    private List<String> seasons;
    private Price price;

    // Reference to PointOfInterest by ID
    private org.bson.types.ObjectId pointOfInterestId;

    @Data
    public static class Price {
        private double adult;
        private double child;
    }
}