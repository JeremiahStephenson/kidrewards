package jam.rain.com.kidrewards.ui.fragment;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.lang3.StringUtils;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(getLayoutResourceId(), container, false);
        ButterKnife.bind(this, view);
        onPostViewCreated();
        return view;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    /**
     * Method to allow the fragment to determine if activity should finish
     * @return true if activity should finish or false if activity shouldn't do anything
     */
    public boolean onBackPressed() {
        return true;
    }

    protected void setTitle(String title) {
        setTitles(title, null);
    }

    protected void setTitle(@StringRes int titleResourceId) {
        setTitles(getString(titleResourceId), null);
    }

    protected void setSubTitle(String subTitle) {
        setTitles(null, subTitle);
    }

    protected void setSubTitle(@StringRes int subTitleResourceId) {
        setTitles(null, getString(subTitleResourceId));
    }

    protected void setTitles(@StringRes int titleResourceId, @StringRes int subTitleResourceId) {
        setTitles(getString(titleResourceId), getString(subTitleResourceId));
    }

    protected void setTitles(String title, String subTitle) {
        if (getActivity() != null) {
            final ActionBar toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (toolbar != null) {
                if (!StringUtils.isEmpty(title)) {
                    toolbar.setTitle(title);
                }
                if (!StringUtils.isEmpty(subTitle)) {
                    toolbar.setSubtitle(subTitle);
                }
            }
        }
    }

    protected void clearSubTitle() {
        if (getActivity() != null) {
            final ActionBar toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (toolbar != null) {
                toolbar.setSubtitle(null);
            }
        }
    }

    protected String getTitle() {
        if (getActivity() != null) {
            final ActionBar toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (toolbar != null && !StringUtils.isEmpty(toolbar.getTitle())) {
                return toolbar.getTitle().toString();
            }
        }
        return null;
    }

    protected void onPostViewCreated() {
        // nothing here but sub classes can override this
    }

    @LayoutRes
    protected abstract int getLayoutResourceId();
}