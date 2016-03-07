package jam.rain.com.kidrewards.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import jam.rain.com.kidrewards.R;
import jam.rain.com.kidrewards.dagger.Injector;
import jam.rain.com.kidrewards.ui.fragment.BaseFragment;
import jam.rain.com.kidrewards.util.FirebaseUtil;
import pocketknife.BindExtra;
import pocketknife.NotRequired;
import pocketknife.PocketKnife;

public class GeneralActivity extends AppCompatActivity {

    public static final String EXTRA_FRAGMENT_CLASS_NAME = "EXTRA_FRAGMENT_CLASS_NAME";
    public static final String EXTRA_FRAGMENT_BUNDLE = "EXTRA_FRAGMENT_BUNDLE";
    public static final String EXTRA_FRAGMENT_TITLE = "EXTRA_FRAGMENT_TITLE";

    @Inject
    FirebaseUtil firebaseUtil;

    @NotRequired
    @BindExtra(EXTRA_FRAGMENT_CLASS_NAME)
    String fragmentName;

    @NotRequired
    @BindExtra(EXTRA_FRAGMENT_BUNDLE)
    Bundle bundle;

    @NotRequired
    @StringRes
    @BindExtra(EXTRA_FRAGMENT_TITLE)
    int titleResourceId;

    @Nullable
    @Bind(R.id.ab_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Injector.init(getApplication());
        Injector.get().inject(this);
        super.onCreate(savedInstanceState);
        PocketKnife.bindExtras(this, getIntent());

        if (titleResourceId == 0) {
            titleResourceId = R.string.app_name;
        }

        if (StringUtils.isEmpty(fragmentName)) {
            fragmentName = getFragmentClassName();
        }

        if (StringUtils.isEmpty(fragmentName)) {
            return;
        }

        setContentView(R.layout.activity_general);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(titleResourceId);
        }

        setupFragment();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setupInjections();
    }

    protected void setupInjections() {
        ButterKnife.bind(this);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onBackPressed() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentName);
        if (!(fragment instanceof BaseFragment) || ((BaseFragment) fragment).onBackPressed()) {
            super.onBackPressed();
        }
    }

    protected String getFragmentClassName() {
        // children can override this
        return null;
    }

    private void setupFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentName);
        if (fragment == null) {
            fragment = Fragment.instantiate(this, fragmentName);
            if (fragment != null && bundle != null) {
                fragment.setArguments(bundle);
            }
        }

        if (fragment != null) {
            final FragmentManager manager = getSupportFragmentManager();
            final FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragment, fragmentName);
            transaction.commit();
        }
    }
}