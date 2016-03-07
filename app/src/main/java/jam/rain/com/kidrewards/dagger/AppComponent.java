package jam.rain.com.kidrewards.dagger;

import javax.inject.Singleton;

import dagger.Component;
import jam.rain.com.kidrewards.ui.activity.GeneralActivity;
import jam.rain.com.kidrewards.ui.fragment.ChildFragment;
import jam.rain.com.kidrewards.ui.fragment.SignInFragment;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(GeneralActivity target);

    void inject(ChildFragment target);

    void inject(SignInFragment target);
}