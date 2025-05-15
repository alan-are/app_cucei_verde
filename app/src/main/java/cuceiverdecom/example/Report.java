package cuceiverdecom.example;

import com.google.firebase.Timestamp;

public class Report {
    private String id;
    private String title;
    private String description;
    private String category;
    private String userId;
    private String userName;
    private String imageUrl;
    private String location;
    private Timestamp creationDate;
    private String status;

    // Constructor vacío necesario para Firestore
    public Report() {
    }

    // Constructor con todos los campos
    public Report(String id, String title, String description, String category, String userId, 
                 String userName, String imageUrl, String location, Timestamp creationDate, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.userId = userId;
        this.userName = userName;
        this.imageUrl = imageUrl;
        this.location = location;
        this.creationDate = creationDate;
        this.status = status;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
