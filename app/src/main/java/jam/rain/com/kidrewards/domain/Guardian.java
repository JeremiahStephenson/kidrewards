package jam.rain.com.kidrewards.domain;

public class Guardian {
    private String name;
    private String email;

    public Guardian() {
        // empty default constructor, necessary for Firebase to be able to deserialize
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
