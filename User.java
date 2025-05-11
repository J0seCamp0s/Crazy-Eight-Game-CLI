public class User {
    protected String name;
    protected String passworddHash;

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
