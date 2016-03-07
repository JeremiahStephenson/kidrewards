package jam.rain.com.kidrewards.dagger;

import javax.inject.Singleton;

import dagger.Component;
import jam.rain.com.kidrewards.ui.activity.GeneralActivity;
import jam.rain.com.kidrewards.ui.adapter.ChildListAdapter;
import jam.rain.com.kidrewards.ui.fragment.ChildFragment;
import jam.rain.com.kidrewards.ui.fragment.ChildListFragment;
import jam.rain.com.kidrewards.ui.fragment.SignInFragment;
import jam.rain.com.kidrewards.ui.fragment.SignInFragment;
import jam.rain.com.kidrewards.ui.fragment.SplashFragment;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(GeneralActivity target);

    void inject(ChildFragment target);

    void inject(ChildListFragment target);

    void inject(ChildListAdapter target);

    void inject(SignInFragment target);

    void inject(SplashFragment target);
}