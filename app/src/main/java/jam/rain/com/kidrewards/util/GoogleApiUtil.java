package jam.rain.com.kidrewards.util;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import javax.inject.Singleton;

@Singleton
public class GoogleApiUtil implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    /* Client used to interact with Google APIs. */
    private GoogleApiClient googleApiClient;

    private GoogleApiClient.ConnectionCallbacks connectionCallbacks;
    private GoogleApiClient.OnConnectionFailedListener failedListener;

    public GoogleApiUtil(Application application) {
        if (googleApiClient == null) {
            /* Setup the Google API object to allow Google+ logins */
            googleApiClient = new GoogleApiClient.Builder(application)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN)
                    .build();
        }
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public void removeListeners() {
        this.connectionCallbacks = null;
        this.failedListener = null;
    }

    public void setListeners(GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {
        this.connectionCallbacks = connectionCallbacks;
        this.failedListener = connectionFailedListener;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (connectionCallbacks != null) {
            connectionCallbacks.onConnected(bundle);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // nothing
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (failedListener != null) {
            failedListener.onConnectionFailed(connectionResult);
        }
    }
}
