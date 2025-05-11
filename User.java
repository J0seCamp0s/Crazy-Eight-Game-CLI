public class User {
    private String name;
    private String passworddHash;

    public User(String userString) {
        Integer comaIndex = userString.indexOf(",");
        name = userString.substring(0, comaIndex);
        passworddHash = userString.substring(comaIndex+1, userString.length());
    }

    public String getName() {
        return name;
    }

    public String getPasswordHash() {
        return passworddHash;
    }
}
