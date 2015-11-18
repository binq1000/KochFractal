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
    private int c = 0;
    private ExecutorService pool;
    
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
        
        CyclicBarrier cb = new CyclicBarrier(3);
        
        //Maak een Thread pool van 3 aan
        
        pool = Executors.newFixedThreadPool(3);
        //Maak 3 Callables
        Callable c1 = new RunClass("Left", this, cb);
        Callable c2 = new RunClass("Right", this, cb);
        Callable c3 = new RunClass("Bottom", this, cb);
        
        
//
//        Thread t1 = new Thread(r1);
//        Thread t2 = new Thread(r2);
//        Thread t3 = new Thread(r3);
//
//        t1.start();
//        t2.start();
//        t3.start();
//
//        try {
//            t1.join();
//            t2.join();
//            t3.join();
//        }
//        catch (InterruptedException ex) {
//            System.out.println("Interrupted Exception bij KochManager changeLevel");
//        }
        
        Future<ArrayList<Edge>> fut = pool.submit(c1);
        Future<ArrayList<Edge>> fut2 = pool.submit(c2);
        Future<ArrayList<Edge>> fut3 = pool.submit(c3);
        
        ArrayList<Edge> ed1 = null;
        ArrayList<Edge> ed2 = null;
        ArrayList<Edge> ed3 = null;
        
        try {
            
            ed1 = fut.get();
            ed2 = fut2.get();
            ed3 = fut3.get();
        }
        catch (Exception ex) {
            //Exception handling!
        }
        
        for (Edge e : ed1) {
            addEdge(e);
        }
        for (Edge e : ed2) {
            addEdge(e);
        }
        for (Edge e : ed3) {
            addEdge(e);
        }
        
        ts.setEnd();
        application.setTextCalc(ts.toString());
        
//        if (c == 3) {
//            c = 0;
//            application.setTextCalc(ts.toString());
//            application.requestDrawEdges();
//        }
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
    
    public synchronized void addCount() {
        c++;
    }
    
    public int getLevel() {
        return kf.getLevel();
    }
    
    public void signalEnd() {
        pool.shutdown();
        application.requestDrawEdges();
    }
          
}
