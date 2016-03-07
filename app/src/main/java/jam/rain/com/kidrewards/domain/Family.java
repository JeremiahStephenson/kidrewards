package jam.rain.com.kidrewards.domain;

public class Family {
    private String familyName;
    private String key;

    public Family() {
        // empty default constructor, necessary for Firebase to be able to deserialize
    }

    public String getFamilyName() {
        return familyName;
    }
}
