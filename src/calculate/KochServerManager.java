package calculate;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Nekkyou on 12-1-2016.
 */
public class KochServerManager implements Observer
{
    private KochFractal kochFractal;
    private ArrayList<Edge> edges;
    private int protocol;

    public KochServerManager(int protocol) {
        this.protocol = protocol;

        kochFractal = new KochFractal();
        kochFractal.addObserver(this);
        edges = new ArrayList<>();

    }

    @Override
    public synchronized void update(Observable o, Object o1)
    {
        Edge e = (Edge)o1;
        edges.add(e);
    }

    public void writeAllEdges() {
        kochFractal.generateBottomEdge();
        kochFractal.generateLeftEdge();
        kochFractal.generateRightEdge();

        sendEdges();
    }

    public void sendEdges(){

    }
}
