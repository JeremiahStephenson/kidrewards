package jam.rain.com.kidrewards.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import jam.rain.com.kidrewards.InternalIntents;
import jam.rain.com.kidrewards.R;
import jam.rain.com.kidrewards.dagger.Injector;

public class SplashFragment extends BaseFragment {
    @Inject
    Firebase firebaseRef;

    @Inject
    InternalIntents internalIntents;

    private static long TIME = 1500;

    /* Listener for Firebase session changes */
    private Firebase.AuthStateListener authStateListener;
    private long start;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_splash;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.get().inject(this);

        start = Calendar.getInstance().getTimeInMillis();
        authStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                final Class fragment = authData == null ? SignInFragment.class : TestFragment.class;
                final long sleep = TIME - (Calendar.getInstance().getTimeInMillis() - start);
                if (sleep > 0) {
                    waitSomeTime(sleep, fragment);
                } else {
                    goToFragment(fragment);
                }
            }
        };
        /* Check if the user is authenticated with Firebase already. If this is the case we can set the authenticated
         * user and hide hide any login buttons */
        firebaseRef.addAuthStateListener(authStateListener);
    }

    @Override
    public void onDestroy() {
        firebaseRef.removeAuthStateListener(authStateListener);
        super.onDestroy();
    }

    private void waitSomeTime(long time, final Class fragment) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                goToFragment(fragment);
            }
        }, time);
    }

    private void goToFragment(Class fragment) {
        final Intent intent = internalIntents.setupGeneralIntent(getActivity(),
                fragment, 0, null);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
