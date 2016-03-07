package jam.rain.com.kidrewards;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import javax.inject.Inject;
import javax.inject.Singleton;

import jam.rain.com.kidrewards.ui.activity.GeneralActivity;

@Singleton
public class InternalIntents {

    @Inject
    public InternalIntents() {
    }

    public void showGeneralActivity(@NonNull Activity context, @NonNull Class fragment, @StringRes int titleResourceId) {
        showGeneralActivity(context, fragment, titleResourceId, null);
    }

    public void showGeneralActivity(@NonNull Activity context, @NonNull Class fragment,
                                     @StringRes int titleResourceId, @Nullable Bundle args) {
        context.startActivity(setupGeneralIntent(context, fragment, titleResourceId, args));
    }

    public void showGeneralActivityForResult(@NonNull Activity activity, @NonNull Fragment fragment, int requestCode,
                                              @NonNull Class fragmentClass, @StringRes int titleResourceId, @Nullable Bundle args) {
        fragment.startActivityForResult(setupGeneralIntent(activity, fragmentClass, titleResourceId, args), requestCode);
    }

    private Intent setupGeneralIntent(@NonNull Activity context, @NonNull Class fragment,
                                      @StringRes int titleResourceId, @Nullable Bundle args) {
        final Intent intent = new Intent(context, GeneralActivity.class);
        intent.putExtra(GeneralActivity.EXTRA_FRAGMENT_CLASS_NAME, fragment.getName());
        intent.putExtra(GeneralActivity.EXTRA_FRAGMENT_BUNDLE, args);
        intent.putExtra(GeneralActivity.EXTRA_FRAGMENT_TITLE, titleResourceId);
        return intent;
    }
}