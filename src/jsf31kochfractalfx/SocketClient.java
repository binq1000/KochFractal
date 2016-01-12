package jsf31kochfractalfx;

import calculate.Edge;

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

    public SocketClient(ArrayList<Integer> levelWithProtocol) {
        this.levelWithProtocol = levelWithProtocol;
        edges = new ArrayList<>();

        try
        {
            Socket s = new Socket("localhost", 8189);
            try {
                outStream = s.getOutputStream();
                inStream = s.getInputStream();

                out = new ObjectOutputStream(outStream);
                in = new ObjectInputStream(inStream);

                System.out.println("Sending data");
                out.writeObject(levelWithProtocol);
                out.flush();

                edges = (ArrayList<Edge>) in.readObject();
                drawEdges();
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
    }

    public void drawEdges() {
        //Draw the edges here!
    }
}
