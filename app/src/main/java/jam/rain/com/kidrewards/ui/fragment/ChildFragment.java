package jam.rain.com.kidrewards.ui.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import jam.rain.com.kidrewards.InternalIntents;
import jam.rain.com.kidrewards.R;
import jam.rain.com.kidrewards.dagger.Injector;
import jam.rain.com.kidrewards.domain.Child;
import jam.rain.com.kidrewards.util.FirebaseUtil;
import jam.rain.com.kidrewards.util.GoogleApiUtil;

public class ChildFragment extends BaseFragment {

    @Inject
    Firebase firebase;

    @Inject
    GoogleApiUtil googleApiUtil;

    @Inject
    InternalIntents internalIntents;

    @Bind(R.id.points)
    TextView pointsTextView;
    @Bind(R.id.reward)
    TextView rewardTextView;

    private int points;
    private Firebase familyRef;
    private Firebase childrenRef;
    private Firebase currentChildRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected void onPostViewCreated() {
        super.onPostViewCreated();
        Injector.get().inject(this);

        familyRef = firebase.child(FirebaseUtil.FAMILY_REF).child(FirebaseUtil.DUMMY_FAMILY_ID);
        childrenRef = familyRef.child(FirebaseUtil.CHILDREN_REF);

        loadData();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_child;
    }

    @OnClick(R.id.add_point)
    public void onAddPointClicked() {
        setPoints(++points);
    }

    @OnClick(R.id.remove_point)
    public void onRemovePointClicked() {
        setPoints(--points);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sign_out, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Unauthenticate from Firebase and from providers where necessary.
     */
    private void logout() {
        /* logout of Firebase */
        firebase.unauth();
        /* Setup the Google API object to allow Google+ logins */
        final GoogleApiClient googleApiClient = googleApiUtil.getGoogleApiClient();

        if (googleApiClient.isConnected()) {
            googleApiClient.clearDefaultAccountAndReconnect().setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    googleApiClient.disconnect();
                }
            });
        }

        internalIntents.showGeneralActivity(getActivity(), SignInFragment.class, 0);
    }

    private void setPoints(int points) {
        currentChildRef.child(FirebaseUtil.POINTS_REF).setValue(points);
    }

    private void loadData() {
        Query queryRef = childrenRef.orderByChild(FirebaseUtil.NAME_REF).equalTo("Rebecca");
        queryRef.addChildEventListener(childEventListener);
    }

    private void populateData(Child child) {
        if (child == null) {
            return;
        }

        Resources res = getActivity().getResources();

        points = child.getPoints();

        setTitle(child.getName());
        int reward = points / 5;
        pointsTextView.setText(res.getString(R.string.point_value, points));
        if (reward > 0) {
            rewardTextView.setText(res.getString(R.string.reward_value, reward));
        } else {
            rewardTextView.setText(res.getString(R.string.reward_value, 0));
        }
    }

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot snapshot, String previousChild) {
            populateData(snapshot.getValue(Child.class));
            currentChildRef = childrenRef.child(snapshot.getKey());
        }

        @Override
        public void onChildChanged(DataSnapshot snapshot, String s) {
            populateData(snapshot.getValue(Child.class));
        }

        @Override
        public void onChildRemoved(DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot snapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };
}
