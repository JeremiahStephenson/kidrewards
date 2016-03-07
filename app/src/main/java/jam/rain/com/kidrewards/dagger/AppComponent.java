package jam.rain.com.kidrewards.dagger;

import javax.inject.Singleton;

import dagger.Component;
import jam.rain.com.kidrewards.MainActivity;
import jam.rain.com.kidrewards.ui.activity.GeneralActivity;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(MainActivity target);

    void inject(GeneralActivity target);
}