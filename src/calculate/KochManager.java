/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import jsf31kochfractalfx.JSF31KochFractalFX;
import timeutil.TimeStamp;

/**
 *
 * @author Nekkyou
 */
public class KochManager implements Observer {

    private JSF31KochFractalFX application;
    private KochFractal kf;
    final ArrayList<Edge> edges;
    //Tasks
    private Task taskLeft = null;
    private Task taskRight = null;
    private Task taskBottom = null;
    
    public KochManager(JSF31KochFractalFX application) {
        this.application = application;
        //KochFractal en Observer aanmaken
        kf = new KochFractal();
        kf.addObserver(this);
        edges = new ArrayList<Edge>();
    }

    @Override
    public void update(Observable o, Object o1) {
        Edge e = (Edge)o1;
        edges.add(e);
    }

    public void changeLevel(int currentLevel) {
        kf.setLevel(currentLevel);
        
        edges.clear();
        TimeStamp ts = new TimeStamp();
        ts.setBegin();

        
        ArrayList<Edge> ed1 = null;
        ArrayList<Edge> ed2 = null;
        ArrayList<Edge> ed3 = null;

        if (ed1 !=null && ed2 != null && ed3 != null) {
            for (Edge e : ed1) {
                addEdge(e);
            }
            for (Edge e : ed2) {
                addEdge(e);
            }
            for (Edge e : ed3) {
                addEdge(e);
            }
        }

        
        ts.setEnd();
        application.setTextCalc(ts.toString());

    }

    public void drawEdges() {
        application.clearKochPanel();
        
        
        TimeStamp ts = new TimeStamp();
        ts.setBegin();
        for (Edge e : edges)
        {
            application.drawEdge(e);
        }
        ts.setEnd();
        application.setTextDraw(ts.toString());
        
        int nrEdges = kf.getNrOfEdges();
        application.setTextNrEdges(String.valueOf(nrEdges));
    }

    public synchronized void addEdge(Edge e) {
        edges.add(e);
    }
    
    public int getLevel() {
        return kf.getLevel();
    }
    
    public void signalEnd() {
        application.requestDrawEdges();
    }

    public void createTasks() {
        taskLeft = new MyTask("Left", this);
        taskRight = new MyTask("Right", this);
        taskBottom = new MyTask("Bottom", this);
    }

    public Task getTaskLeft() {
        return taskLeft;
    }
    public Task getTaskRight() {
        return taskRight;
    }
    public Task getTaskBottom() {
        return taskBottom;
    }
          
}
