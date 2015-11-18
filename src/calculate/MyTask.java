package calculate;

import javafx.concurrent.Task;

import java.util.logging.Logger;

/**
 * Created by Nekkyou on 18-11-2015.
 */
public class MyTask extends Task<Void>
{
    private String name;
    private static final Logger LOG = Logger.getLogger(MyTask.class.getName());

    public MyTask(String name) {
        this.name = name;
    }

    @Override
    protected Void call() throws Exception
    {
        final int MAX = 1000;
        for (int i = 1; i <= MAX; i++) {
            if (isCancelled()) {
                break;
            }
            updateProgress(i, MAX);
            updateMessage(name + " " + String.valueOf(i));
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                if (isCancelled()) {
                    LOG.info("Exception occurred: " + e.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * *
     * Called if execution state is Worker.State CANCELLED
     */
    @Override
    protected void cancelled() {
        super.cancelled();
        LOG.info(name + " cancelled()");
    }

    /***
     * Called if execution state is Worker.State FAILED
     * (see interface Worker<V>)
     */
    @Override
    protected void failed() {
        super.failed();
        LOG.info(name + " failed()");
    }

    /**
     * *
     * Called if execution state is Worker.State RUNNING
     */
    @Override
    protected void running() {
        super.running();
        LOG.info(name + " running()");
    }

    /**
     * *
     * Called if execution state is Worker.State SCHEDULED
     */
    @Override
    protected void scheduled() {
        super.scheduled();
        LOG.info(name + " scheduled()");
    }

    /**
     * *
     * Called if execution state is Worker.State SUCCEEDED
     */
    @Override
    protected void succeeded() {
        super.succeeded();
        LOG.info(name + " succeeded()");
    }

    /***
     * Called if FutureTask behaviour is done
     */
    @Override
    protected void done() {
        super.done();
        LOG.info(name + " done()");
    }

}
