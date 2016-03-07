package jam.rain.com.kidrewards.ui.activity;

import android.content.Intent;

import jam.rain.com.kidrewards.ui.fragment.SignInFragment;

public class SignInActivity extends GeneralActivity {

    @Override
    protected String getFragmentClassName() {
        return SignInFragment.class.getName();
    }

    /**
     * Hack because we can't catch this in the fragment when calling startIntentSenderForResult
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SignInFragment.RC_GOOGLE_LOGIN) {
            final SignInFragment signInFragment = (SignInFragment)getSupportFragmentManager().findFragmentByTag(SignInFragment.class.getName());
            if (signInFragment != null) {
                signInFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    protected boolean shouldDisplayHomeAsUpEnabled() {
        return false;
    }

    @Override
    protected boolean shouldShowHomeButtonEnabled() {
        return false;
    }
}
