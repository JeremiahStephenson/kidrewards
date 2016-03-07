package jam.rain.com.kidrewards.util;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import jam.rain.com.kidrewards.domain.Family;

public class FirebaseUtil {

    public static final String DUMMY_FAMILY_ID = "-KCHiqTIwxtEycdZg3wk";
    public static final String DUMMY_CHILD_REBECCA_ID = "-KCHiqTZdouoQ8rJTFEF";

    public static final String CHILDREN_REF = "children";
    public static final String NAME_REF = "name";
    public static final String FAMILY_NAME_REF = "familyName";
    public static final String EMAIL_REF = "email";
    public static final String POINTS_REF = "points";
    public static final String GUARDIAN_REF = "guardian";
    public static final String SETTINGS_REF = "settings";
    public static final String CONVERSION_RATE_REF = "conversionRate";
    public static final String FAMILY_REF = "family";

    @Inject
    Firebase firebase;

    @Inject
    public FirebaseUtil() {
        // Dagger Constructor
    }

    public void  createDummyData() {
        Firebase initialized = firebase.child("global-settings").child("initialized");
        String initializedValue = getStringFromRef(initialized);
        if (initializedValue == null || initializedValue.isEmpty()) {
            Firebase familyRef = firebase.child(FAMILY_REF);
            Firebase family = familyRef.child(addFamily("Johnson"));
            setFamilySetting(family, CONVERSION_RATE_REF, 5);
            addGuardian(family, "John", "john@dummy.net");
            addGuardian(family, "Jane", "jane@dummy.net");
            addChild(family, "Rebecca", "rebecca@dummy.net", 23);
            addChild(family, "Billy", "billy@dummy.net", 5);

            initialized.setValue("true");
        }
    }

    public String addFamily(String name) {
        Firebase familyRef = firebase.child(FAMILY_REF);
        Firebase newFamilyRef = familyRef.push();
        newFamilyRef.setValue(name);
        String newFamilyKey = newFamilyRef.getKey();
        familyRef.child(newFamilyKey).child(FAMILY_NAME_REF).setValue(name);
        return newFamilyKey;
    }

    public String addChild(Firebase family, String name, String email, int points) {
        Map<String, Object> child = new HashMap<>();
        child.put(NAME_REF, name);
        child.put(EMAIL_REF, email);
        child.put(POINTS_REF, points);
        Firebase newChildRef = family.child(CHILDREN_REF).push();
        newChildRef.setValue(child);
        return newChildRef.getKey();
    }

    public String addGuardian(Firebase family, String name, String email) {
        Map<String, Object> guardian = new HashMap<>();
        guardian.put(NAME_REF, name);
        guardian.put(EMAIL_REF, email);
        Firebase newGuardianRef = family.child(GUARDIAN_REF).push();
        newGuardianRef.setValue(guardian);
        return newGuardianRef.getKey();
    }

    public void setFamilySetting(Firebase family, String name, Object value) {
        family.child(SETTINGS_REF).child(name).setValue(value);
    }

    public void getFamily(String familyName) {
        Firebase familyRef = firebase.child(FAMILY_REF);
        Query queryRef = familyRef.orderByChild(FAMILY_NAME_REF).equalTo(familyName);

        final List<Family> families = new ArrayList<>();
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                String key = snapshot.getKey();
                Log.d("KidReward", "FamilyQuery: " + snapshot.getKey() + " is " + snapshot.child(FAMILY_NAME_REF));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public String getStringFromRef(Firebase ref) {
        final String[] text = new String[1];
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                text[0] = snapshot.getValue(String.class);
            }
            @Override public void onCancelled(FirebaseError error) { }
        });
        return text[0];
    }

}
