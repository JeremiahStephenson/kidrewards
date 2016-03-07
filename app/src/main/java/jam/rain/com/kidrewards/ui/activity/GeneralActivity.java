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

import butterknife.Bind;
import jam.rain.com.kidrewards.R;
import jam.rain.com.kidrewards.ui.fragment.BaseFragment;
import pocketknife.BindExtra;
import pocketknife.PocketKnife;

public class GeneralActivity extends AppCompatActivity {

    public static final String EXTRA_FRAGMENT_CLASS_NAME = "EXTRA_FRAGMENT_CLASS_NAME";
    public static final String EXTRA_FRAGMENT_BUNDLE = "EXTRA_FRAGMENT_BUNDLE";
    public static final String EXTRA_FRAGMENT_TITLE = "EXTRA_FRAGMENT_TITLE";
    public static final String EXTRA_SHOW_UNIT_SELECTOR = "EXTRA_SHOW_UNIT_SELECTOR";

    @BindExtra(EXTRA_FRAGMENT_CLASS_NAME)
    String fragmentName;

    @Nullable
    @BindExtra(EXTRA_FRAGMENT_BUNDLE)
    Bundle bundle;

    @StringRes
    @BindExtra(EXTRA_FRAGMENT_TITLE)
    int titleResourceId;

    @BindExtra(EXTRA_SHOW_UNIT_SELECTOR)
    boolean showUnitSelector;

    @Nullable
    @Bind(R.id.ab_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Injector.get().inject(this);
        super.onCreate(savedInstanceState);
        PocketKnife.bindExtras(this, getIntent());

        if (StringUtils.isEmpty(fragmentName)) {
            return;
        }

        setContentView(R.layout.activity_general);

        setupFragment();

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(titleResourceId);
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

    @Override
    public void onBackPressed() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentName);
        if (!(fragment instanceof BaseFragment) || ((BaseFragment) fragment).onBackPressed()) {
            super.onBackPressed();
        }
    }
}