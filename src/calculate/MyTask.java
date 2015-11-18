package calculate;

import javafx.application.Platform;
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

    private static final Logger LOG = Logger.getLogger(MyTask.class.getName());

    private KochFractal kf = new KochFractal();
    private String s = "";
    private boolean mogelijk = true;
    private KochManager km;
    private ArrayList<Edge> edges;
    private int maxEdges;
    private int i = 0;

    public MyTask(String side, KochManager k) {
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
        maxEdges = kf.getNrOfEdges() / 3;
        edges = new ArrayList<Edge>();
    }

    @Override
    public void update(Observable observable, Object o) {
        edges.add((Edge) o);
        km.addEdge((Edge) o);

        updateMessage("Edges: " + edges.size());
        //TODO uitbreiden met tekenen
        Edge e = (Edge) o;
        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                i++;
                updateProgress(i, maxEdges);
                updateMessage(i + "/" + maxEdges);
                km.drawEdge(e);
            }
        });
    }

    @Override
    protected ArrayList<Edge> call() throws Exception
    {
        i = 0;
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
        LOG.info(s + " cancelled()");
    }

    /***
     * Called if execution state is Worker.State FAILED
     * (see interface Worker<V>)
     */
    @Override
    protected void failed() {
        super.failed();
        LOG.info(s + " failed()");
    }

    /**
     * *
     * Called if execution state is Worker.State RUNNING
     */
    @Override
    protected void running() {
        super.running();
        LOG.info(s + " running()");
    }

    /**
     * *
     * Called if execution state is Worker.State SCHEDULED
     */
    @Override
    protected void scheduled() {
        super.scheduled();
        LOG.info(s + " scheduled()");
    }

    /**
     * *
     * Called if execution state is Worker.State SUCCEEDED
     */
    @Override
    protected void succeeded() {
        super.succeeded();
        LOG.info(s + " succeeded()");
    }

    /***
     * Called if FutureTask behaviour is done
     */
    @Override
    protected void done() {
        super.done();
        LOG.info(s + " done()");
    }

}
