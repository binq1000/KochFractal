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

/**
 *
 * @author Nekkyou
 */
public class RunClass implements Callable, Observer{

    private KochFractal kf = new KochFractal();
    private String s = "";
    private boolean mogelijk = true;
    private KochManager km;
    private ArrayList<Edge> edges;
    private CyclicBarrier cb;
    
    public RunClass(String side, KochManager k, CyclicBarrier c) {
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
    public void update(Observable o, Object o1) {
       edges.add((Edge) o1);
    }

    @Override
    public ArrayList<Edge> call() throws Exception {
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
    
}
