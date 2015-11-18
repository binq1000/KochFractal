package calculate;

import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Logger;

/**
 * Created by Nekkyou on 18-11-2015.
 */
public class MyTask extends Task<ArrayList<Edge>> implements Observer {

    private String name;
    private static final Logger LOG = Logger.getLogger(MyTask.class.getName());

    private KochFractal kf = new KochFractal();
    private String s = "";
    private boolean mogelijk = true;
    private KochManager km;
    private ArrayList<Edge> edges;
    private CyclicBarrier cb;

    public MyTask(String side, KochManager k, CyclicBarrier c) {
        km = k;
        kf.addObserver(this);
        kf.setLevel(km.getLevel());
        if (side.matches("Left") || side.matches("Right") || side.matches("Bottom")) {
            s = side;
        }
        else {
            System.out.println("Verkeerde input");
            mogelijk = false;
        }
        edges = new ArrayList<Edge>();
        cb = c;
    }

    @Override
    public void update(Observable observable, Object o) {
        edges.add((Edge) o);
    }

    @Override
    protected ArrayList<Edge> call() throws Exception
    {
        if (!mogelijk) {
            return null;
        }

        if(s.matches("Left")) {
            kf.generateLeftEdge();
        }
        else if(s.matches("Right")) {
            kf.generateRightEdge();
        }
        else if(s.matches("Bottom")) {
            kf.generateBottomEdge();
        }
        //Hier wacht je tot de berekeningen klaar zijn
        if (cb.await() == 0)
        {
            km.signalEnd();
        }

        //Return wat je hebt (deze worden toegevoegd in de update methode.
        return edges;
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
