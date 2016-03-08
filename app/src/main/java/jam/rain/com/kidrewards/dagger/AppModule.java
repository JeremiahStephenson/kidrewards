package jam.rain.com.kidrewards.dagger;

import android.app.Application;

import com.firebase.client.Firebase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import jam.rain.com.kidrewards.util.GoogleApiUtil;

@Module
public class AppModule {

    Application application;

    public AppModule(Application application) {
        this.application = application;
        Firebase.setAndroidContext(this.application.getApplicationContext());
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return application;
    }

    @Provides
    @Singleton
    Firebase providesFirebase() {
        return new Firebase("https://resplendent-fire-3556.firebaseio.com/");
    }

    @Provides
    @Singleton
    GoogleApiUtil providesApiUtil() {
        return new GoogleApiUtil(application);
    }
}
