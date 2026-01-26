package interfaceui;

public class UserSession {
    private static UserSession instance;
    private int userId;
    private String username;
    private String email;
    private String fullName;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setUserData(int userId, String username, String email, String fullName) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
    }

    public void clearSession() {
        this.userId = 0;
        this.username = null;
        this.email = null;
        this.fullName = null;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public boolean isLoggedIn() {
        return userId > 0 && username != null;
    }
}