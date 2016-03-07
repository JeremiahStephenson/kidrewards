package jam.rain.com.kidrewards.util;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

public class ExecutorUtil {
    public static Executor getThreadExecutor = new Executor() {
        public void execute(@NonNull Runnable command) {
            new Handler(Looper.getMainLooper()).post(command);
        }
    };
}
