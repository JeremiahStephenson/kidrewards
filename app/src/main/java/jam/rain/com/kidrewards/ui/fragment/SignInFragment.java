package jam.rain.com.kidrewards.ui.fragment;

import android.os.Bundle;

import javax.inject.Inject;

import jam.rain.com.kidrewards.InternalIntents;
import jam.rain.com.kidrewards.R;
import jam.rain.com.kidrewards.dagger.Injector;

public class SignInFragment extends BaseFragment {

    @Inject
    InternalIntents internalIntents;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_sign_in;
    }

    @Override
    protected void onPostViewCreated() {
        super.onPostViewCreated();
        Injector.get().inject(this);
        internalIntents.showGeneralActivity(getActivity(), ChildFragment.class, R.string.child, new Bundle());
    }
}
