package jam.rain.com.kidrewards.dagger;

import android.app.Application;

public class Injector {
    private static AppComponent appComponent;

    private Injector() {
        // Private Constructor
    }

    public static void init(Application application) {
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(application))
                .build();
    }

    public static AppComponent get() {
        return appComponent;
    }
}

