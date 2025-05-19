package cuceiverdecom.example;

import com.google.firebase.Timestamp;
import java.util.HashMap; // Added for userVotes
import java.util.Map;    // Added for userVotes

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
    // private int resolutionVoteCount; // Removed old vote counter
    private int solvedVotesCount;     // New: count for "solved" votes
    private int unsolvedVotesCount;   // New: count for "unsolved" votes
    private Map<String, String> userVotes; // New: to track individual user votes (userId -> "solved"/"unsolved")

    // Constructor vacío necesario para Firestore
    public Report() {
        this.userVotes = new HashMap<>(); // Initialize map
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
        this.solvedVotesCount = 0;     // Initialize new counter
        this.unsolvedVotesCount = 0;   // Initialize new counter
        this.userVotes = new HashMap<>(); // Initialize map
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

    public int getSolvedVotesCount() {
        return solvedVotesCount;
    }

    public void setSolvedVotesCount(int solvedVotesCount) {
        this.solvedVotesCount = solvedVotesCount;
    }

    public int getUnsolvedVotesCount() {
        return unsolvedVotesCount;
    }

    public void setUnsolvedVotesCount(int unsolvedVotesCount) {
        this.unsolvedVotesCount = unsolvedVotesCount;
    }

    public Map<String, String> getUserVotes() {
        // Ensure userVotes is not null, especially for older documents in Firestore
        if (this.userVotes == null) {
            this.userVotes = new HashMap<>();
        }
        return userVotes;
    }

    public void setUserVotes(Map<String, String> userVotes) {
        this.userVotes = userVotes;
    }
}
