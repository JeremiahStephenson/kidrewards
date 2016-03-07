package jam.rain.com.kidrewards.domain;

public class Child {
    private String name;
    private String email;
    private int points;

    public Child() {
        // empty default constructor, necessary for Firebase to be able to deserialize
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getPoints() {
        return points;
    }
}
