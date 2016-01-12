package calculate;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
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
    private Socket socket;
    private int level;

    //For returning stuff
    private OutputStream outStream;
    private ObjectOutputStream out;


    public KochServerManager(int protocol, int level, Socket socket) {
        System.out.println("Created a KochServerManager");
        this.protocol = protocol;
        this.socket = socket;
        this.level = level;

        try
        {
            outStream = socket.getOutputStream();
            out = new ObjectOutputStream(outStream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


        kochFractal = new KochFractal();
        kochFractal.addObserver(this);
        kochFractal.setLevel(level);
        edges = new ArrayList<>();

        if(protocol == 1){
            writeAllEdges();
        }
        else if(protocol == 2){
            writeEdgeForEdge();
        }
    }

    @Override
    public synchronized void update(Observable o, Object o1)
    {
        Edge e = (Edge)o1;
        edges.add(e);
    }

    public void writeAllEdges() {
        System.out.println("Starting to write all edges");
        kochFractal.generateBottomEdge();
        kochFractal.generateLeftEdge();
        kochFractal.generateRightEdge();

        sendEdges();
    }

    public void writeEdgeForEdge() {

    }

    public void sendEdges(){
        System.out.println("Sending edges!");

        try
        {
            out.writeObject(edges);
            out.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
