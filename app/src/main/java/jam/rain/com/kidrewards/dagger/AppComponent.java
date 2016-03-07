package jam.rain.com.kidrewards.dagger;

import javax.inject.Singleton;

import dagger.Component;
import jam.rain.com.kidrewards.MainActivity;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(MainActivity target);
}