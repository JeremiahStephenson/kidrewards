package jam.rain.com.kidrewards.ui.activity;

import jam.rain.com.kidrewards.ui.fragment.SplashFragment;

public class SplashActivity extends GeneralActivity {

    @Override
    protected String getFragmentClassName() {
        return SplashFragment.class.getName();
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
