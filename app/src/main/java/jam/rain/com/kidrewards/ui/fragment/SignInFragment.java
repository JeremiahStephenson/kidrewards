package jam.rain.com.kidrewards.ui.fragment;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import bolts.Continuation;
import bolts.Task;
import butterknife.OnClick;
import jam.rain.com.kidrewards.InternalIntents;
import jam.rain.com.kidrewards.R;
import jam.rain.com.kidrewards.dagger.Injector;
import jam.rain.com.kidrewards.util.ExecutorUtil;

public class SignInFragment extends BaseFragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "SignInFragment";

    public static final int RC_GOOGLE_LOGIN = 1;

    private static final int REQUEST_ACCOUNT = 2;
    private static final String GOOGLE = "google";

    /* Client used to interact with Google APIs. */
    private GoogleApiClient googleApiClient;

    /* A flag indicating that a PendingIntent is in progress and prevents us from starting further intents. */
    private boolean googleIntentInProgress;

    /* Track whether the sign-in button has been clicked so that we know to resolve all issues preventing sign-in
     * without waiting. */
    private boolean googleLoginClicked;

    /* Store the connection result from onConnectionFailed callbacks so that we can resolve them when the user clicks
     * sign-in. */
    private ConnectionResult googleConnectionResult;

    /* A dialog that is presented until the Firebase authentication finished. */
    private ProgressDialog authProgressDialog;

    /* Listener for Firebase session changes */
    private Firebase.AuthStateListener authStateListener;

    private AuthData authData;

    @Inject
    Firebase firebaseRef;

    @Inject
    InternalIntents internalIntents;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_sign_in;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.get().inject(this);
        setHasOptionsMenu(true);

        /* Setup the Google API object to allow Google+ logins */
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        authProgressDialog = new ProgressDialog(getActivity());
        authProgressDialog.setTitle(R.string.loading);
        authProgressDialog.setMessage(getString(R.string.authenticating));
        authProgressDialog.setCancelable(false);
        authProgressDialog.show();

        authStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                authProgressDialog.hide();
                setAuthenticatedUser(authData);
            }
        };
        /* Check if the user is authenticated with Firebase already. If this is the case we can set the authenticated
         * user and hide hide any login buttons */
        firebaseRef.addAuthStateListener(authStateListener);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        // if changing configurations, stop tracking firebase session.
        firebaseRef.removeAuthStateListener(authStateListener);
    }

    @OnClick(R.id.login_with_google)
    public void onLoginGoogle() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Request missing location permission.
            requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS}, REQUEST_ACCOUNT);
        } else {
            signIn();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ACCOUNT) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                signIn();
            } else {
                getActivity().finish();
            }
        }
    }

    /**
     * This method fires when any startActivityForResult finishes. The requestCode maps to
     * the value passed into startActivityForResult.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_LOGIN) {
            /* This was a request by the Google API */
            if (resultCode != Activity.RESULT_OK) {
                googleLoginClicked = false;
            }
            googleIntentInProgress = false;
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        }
    }

    /**
     * Unauthenticate from Firebase and from providers where necessary.
     */
    private void logout() {
        if (this.authData != null) {
            /* logout of Firebase */
            firebaseRef.unauth();
            /* Logout from Google+ */
            if (googleApiClient != null && googleApiClient.isConnected()) {
                googleApiClient.clearDefaultAccountAndReconnect().setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        googleApiClient.disconnect();
                        setAuthenticatedUser(null);
                    }
                });
            };
        }
    }

    private void signIn() {
        googleLoginClicked = true;
        if (googleApiClient.isConnecting()) {
            return;
        }
        if (googleConnectionResult != null) {
            resolveSignInError();
        } else if (googleApiClient.isConnected()) {
            getGoogleOAuthTokenAndLogin();
        } else {
            Log.d(TAG, "Trying to connect to Google API");
            googleApiClient.connect();
        }
    }

    /**
     * Show errors to users
     */
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.error)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /* A helper method to resolve the current ConnectionResult error. */
    private void resolveSignInError() {
        if (googleConnectionResult.hasResolution()) {
            try {
                googleIntentInProgress = true;
                googleConnectionResult.startResolutionForResult(getActivity(), RC_GOOGLE_LOGIN);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                googleIntentInProgress = false;
                googleApiClient.connect();
            }
        }
    }

    private void getGoogleOAuthTokenAndLogin() {
        Task.callInBackground(new Callable<String[]>() {
            @Override
            public String[] call() throws Exception {
                String token = null;
                String errorMessage = null;
                try {
                    String scope = String.format("oauth2:%s", Scopes.PLUS_LOGIN);
                    Account[] accounts = AccountManager.get(getActivity()).getAccounts();
                    if (accounts.length > 0) {
                        token = GoogleAuthUtil.getToken(getActivity(), accounts[0], scope);
                    } else {
                        errorMessage = getString(R.string.error_authenticating, "no accounts");
                    }
                } catch (IOException transientEx) {
                    /* Network or server error */
                    Log.e(TAG, "Error authenticating with Google: " + transientEx);
                    errorMessage = "Network error: " + transientEx.getMessage();
                } catch (UserRecoverableAuthException e) {
                    Log.w(TAG, "Recoverable Google OAuth error: " + e.toString());
                    /* We probably need to ask for permissions, so start the intent if there is none pending */
                    if (!googleIntentInProgress) {
                        googleIntentInProgress = true;
                        Intent recover = e.getIntent();
                        startActivityForResult(recover, RC_GOOGLE_LOGIN);
                    }
                } catch (GoogleAuthException authEx) {
                    /* The call is not ever expected to succeed assuming you have already verified that
                     * Google Play services is installed. */
                    Log.e(TAG, "Error authenticating with Google: " + authEx.getMessage(), authEx);
                    errorMessage = getString(R.string.error_authenticating, authEx.getMessage());
                }
                return new String[]{token, errorMessage};
            }
        }).continueWith(new Continuation<String[], Object>() {
            @Override
            public Object then(Task<String[]> stringTask) throws Exception {
                if (stringTask.getResult() == null || stringTask.getResult().length != 2) {
                    return null;
                }
                googleLoginClicked = false;
                if (!StringUtils.isEmpty(stringTask.getResult()[0])) {
                    /* Successfully got OAuth token, now login with Google */
                    firebaseRef.authWithOAuthToken(GOOGLE, stringTask.getResult()[0], new AuthResultHandler(GOOGLE));
                } else if (StringUtils.isEmpty(stringTask.getResult()[1])) {
                    authProgressDialog.hide();
                    showErrorDialog(stringTask.getResult()[1]);
                }
                return null;
            }
        }, ExecutorUtil.getThreadExecutor);
    }

    @Override
    public void onConnected(final Bundle bundle) {
        /* Connected with Google API, use this to authenticate with Firebase */
        getGoogleOAuthTokenAndLogin();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        if (!googleIntentInProgress) {
            /* Store the ConnectionResult so that we can use it later when the user clicks on the Google+ login button */
            googleConnectionResult = result;
            if (googleLoginClicked) {
                /* The user has already clicked login so we attempt to resolve all errors until the user is signed in,
                 * or they cancel. */
                resolveSignInError();
            } else {
                Log.e(TAG, result.toString());
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // ignore
    }

    /**
     * Utility class for authentication results
     */
    private class AuthResultHandler implements Firebase.AuthResultHandler {

        private final String provider;

        public AuthResultHandler(String provider) {
            this.provider = provider;
        }

        @Override
        public void onAuthenticated(AuthData authData) {
            authProgressDialog.hide();
            Log.i(TAG, provider + " auth successful");
            setAuthenticatedUser(authData);
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            authProgressDialog.hide();
            showErrorDialog(firebaseError.toString());
        }
    }

    /**
     * Once a user is logged in, take the mAuthData provided from Firebase and "use" it.
     */
    private void setAuthenticatedUser(AuthData authData) {
        if (authData != null) {
            final Intent intent = internalIntents.setupGeneralIntent(getActivity(), ChildFragment.class, 0, null);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
