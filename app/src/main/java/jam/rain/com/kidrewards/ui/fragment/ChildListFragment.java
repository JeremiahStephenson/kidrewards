package jam.rain.com.kidrewards.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import javax.inject.Inject;

import butterknife.Bind;
import jam.rain.com.kidrewards.R;
import jam.rain.com.kidrewards.dagger.Injector;
import jam.rain.com.kidrewards.domain.Child;
import jam.rain.com.kidrewards.ui.adapter.ChildListAdapter;
import jam.rain.com.kidrewards.util.FirebaseUtil;

public class ChildListFragment extends BaseFragment {

    @Inject
    Firebase firebase;

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    private ChildListAdapter adapter;

    @Override
    protected void onPostViewCreated() {
        super.onPostViewCreated();
        Injector.get().inject(this);

        setupRecyclerView();

        loadList();
    }

    @Override
    protected int getLayoutResourceId() {
        return 0;
    }

    private void loadList() {
        Firebase childrenRef = firebase.child(FirebaseUtil.FAMILY_REF).child(FirebaseUtil.DUMMY_FAMILY_ID).child(FirebaseUtil.CHILDREN_REF);
        childrenRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                adapter.addChild(dataSnapshot.getValue(Child.class));
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

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ChildListAdapter();
        recyclerView.setAdapter(adapter);
    }
}
