/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import java.io.*;
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
import javafx.scene.paint.Color;
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
    //Pool
    ExecutorService pool = Executors.newFixedThreadPool(3);
    //Counter
    private int counter = 0;
    //Timer
    TimeStamp tsDraw = new TimeStamp();
    //ReadFromFile
    private FileInputStream fis;
    private ObjectInputStream oin;
    
    public KochManager(JSF31KochFractalFX application) throws IOException {
        this.application = application;
        //KochFractal en Observer aanmaken
        kf = new KochFractal();
        kf.addObserver(this);
        edges = new ArrayList<Edge>();
        //ReadFromFile
        fis = new FileInputStream("edges.dat");
        oin = new ObjectInputStream(fis);
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


        if (taskLeft != null && taskRight != null && taskBottom != null) {
            taskLeft.cancel();
            taskRight.cancel();
            taskBottom.cancel();
        }

        createTasks();
        tsDraw.setBegin();
        startTasks();


        ts.setEnd();
        application.setTextCalc(ts.toString());

    }

    public void startTasks() {
        Thread thLeft = new Thread(taskLeft);
        Thread thRight = new Thread(taskRight);
        Thread thBottom = new Thread(taskBottom);

        pool.submit(thLeft);
        pool.submit(thRight);
        pool.submit(thBottom);


    }

    public void drawEdges() {
        application.clearKochPanel();
        

        for (Edge e : edges)
        {
            application.drawEdge(e);
        }
        tsDraw.setEnd();
        application.setTextDraw(tsDraw.toString());
        
        int nrEdges = kf.getNrOfEdges();
        application.setTextNrEdges(String.valueOf(nrEdges));
    }

    public void drawEdge(Edge e) {
        application.drawWhiteEdge(e);
    }

    public synchronized void addEdge(Edge e) {
        edges.add(e);
    }
    
    public int getLevel() {
        return kf.getLevel();
    }
    
    public synchronized void signalEnd() {
        counter++;
        if (counter >= 3) {
            application.requestDrawEdges();
            counter = 0;
        }

    }

    public void createTasks() {
        if (taskLeft != null) {
            application.getProgressBarLeft().progressProperty().unbind();
            application.getLblLeftCalc().textProperty().unbind();
        }
        if (taskRight != null) {
            application.getProgressBarRight().progressProperty().unbind();
            application.getLblRightCalc().textProperty().unbind();
        }
        if (taskBottom != null) {
            application.getProgressBarBottom().progressProperty().unbind();
            application.getLblBottomCalc().textProperty().unbind();
        }

        taskLeft = new MyTask("Left", this);
        taskRight = new MyTask("Right", this);
        taskBottom = new MyTask("Bottom", this);

        application.getProgressBarLeft().setProgress(0);
        application.getProgressBarLeft().progressProperty().bind(taskLeft.progressProperty());
        application.getLblLeftCalc().textProperty().bind(taskLeft.messageProperty());

        application.getProgressBarRight().setProgress(0);
        application.getProgressBarRight().progressProperty().bind(taskRight.progressProperty());
        application.getLblRightCalc().textProperty().bind(taskRight.messageProperty());

        application.getProgressBarBottom().setProgress(0);
        application.getProgressBarBottom().progressProperty().bind(taskBottom.progressProperty());
        application.getLblBottomCalc().textProperty().bind(taskBottom.messageProperty());
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

    public void readFromFile() throws IOException {
        edges = (ArrayList<Edge>)oin.readObject();
        oin.close();
        drawEdges();
    }
          
}
