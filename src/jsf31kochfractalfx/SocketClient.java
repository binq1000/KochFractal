package jsf31kochfractalfx;

import calculate.Edge;
import timeutil.TimeStamp;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Nekkyou on 12-1-2016.
 */
public class SocketClient
{
    private ArrayList<Integer> levelWithProtocol;
    private OutputStream outStream;
    private ObjectOutputStream out;

    private InputStream inStream;
    private ObjectInputStream in;

    private ArrayList<Edge> edges;

    private JSF31KochFractalFX application;

    public SocketClient(ArrayList<Integer> levelWithProtocol, JSF31KochFractalFX application) {
        this.levelWithProtocol = levelWithProtocol;
        edges = new ArrayList<>();
        this.application = application;

        try
        {
            Socket s = new Socket("localhost", 8189);
            try {
                outStream = s.getOutputStream();
                out = new ObjectOutputStream(outStream);

                System.out.println("Sending data");
                out.writeObject(levelWithProtocol);
                out.flush();

                inStream = s.getInputStream();
                in = new ObjectInputStream(inStream);

                edges = (ArrayList<Edge>) in.readObject();
                application.requestDrawEdgesSC();
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
            finally
            {
                //s.close();
                //out.close();
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawEdges() {
        //Draw the edges here!
        System.out.println("Starting to draw the edges!");
        application.clearKochPanel();

        TimeStamp ts = new TimeStamp();
        ts.setBegin();
        for (Edge e : edges)
        {
            application.drawEdge(e);
        }
        ts.setEnd();
        application.setTextDraw(ts.toString());

        application.enableButtons();
    }
}
