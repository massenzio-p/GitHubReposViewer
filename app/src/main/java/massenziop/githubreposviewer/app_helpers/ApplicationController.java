package massenziop.githubreposviewer.app_helpers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApplicationController {
    private static final int NETWORK_THREADS_AMOUNT = 2;
    private static ApplicationController instance;

    private ExecutorService backgroundTasksExecutor;
    private ExecutorService networkExecutor;
    private ExecutorService dbExecutor;

    private ApplicationController() {
        this.backgroundTasksExecutor = Executors.newCachedThreadPool();
        this.networkExecutor = Executors.newFixedThreadPool(NETWORK_THREADS_AMOUNT);
        this.dbExecutor = Executors.newSingleThreadExecutor();
    }

    public static ApplicationController getInstance() {
        if (instance == null) {
            instance = new ApplicationController();
        }
        return instance;
    }

    public ExecutorService getBackgroundTasksExecutor() {
        return backgroundTasksExecutor;
    }

    public ExecutorService getNetworkExecutor() {
        return networkExecutor;
    }

    public ExecutorService getDbExecutor() {
        return dbExecutor;
    }
}
