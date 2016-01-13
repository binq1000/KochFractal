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
    private Socket s;

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
            s = new Socket("localhost", 8189);
            try {
                outStream = s.getOutputStream();
                out = new ObjectOutputStream(outStream);

                System.out.println("Sending data");
                out.writeObject(levelWithProtocol);
                out.flush();

                inStream = s.getInputStream();
                in = new ObjectInputStream(inStream);

                switch (levelWithProtocol.get(1)){
                    case 1: receiveAllEdges();
                        break;
                    case 2: receivePartsOfEdges();
                        break;
                    case 3: receiveZoom();
                        break;
                    default: System.out.println("Wrong number");
                        break;
                }

            }
            catch (Exception e)
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

    public void receiveAllEdges() {
        try {
            edges = (ArrayList<Edge>) in.readObject();
            application.requestDrawEdgesSC();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public void receivePartsOfEdges() {
        boolean done = false;
        Object inObject = null;
        while (!done) {
            try {
                System.out.println("Reading Object");
                inObject = in.readObject();
                System.out.println("Object succesfully read");
                if (inObject instanceof Edge) {
                    application.drawEdge((Edge) inObject);
//                    level = (int) ((ArrayList) inObject).get(0);
//                    protocol = (int) ((ArrayList) inObject).get(1);
//
//                    System.out.println("Calling ProcessInput!");
//                    calculateProtocol.processInput(level, protocol, socket);
                } else if (inObject instanceof String){
                    done = true;
                    System.out.println("Done sending.");
                }
                else {
                    System.out.println("Unidentified object has been send");
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Object not known");
            }
            catch (EOFException eo) {
                System.out.println("Got another EOFEXception!");
                //Internet tells me to do this, dont think it's good though
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void receiveZoom() {

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
