package cuceiverdecom.example;

public class Collaborator {
    private String userId;
    private String userName;
    private int postCount;

    public Collaborator(String userId, String userName, int postCount) {
        this.userId = userId;
        this.userName = userName;
        this.postCount = postCount;
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

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }
}
