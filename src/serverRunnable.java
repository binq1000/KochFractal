import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * Created by Nekkyou on 6-1-2016.
 */
public class serverRunnable implements Runnable {
    private Socket socket = null;
    private CalculateProtocol calculateProtocol;

    private int level = 0;
    private int protocol = 0;

    private InputStream inStream;
    private ObjectInputStream in;

    public serverRunnable(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run()
    {
        System.out.println("Thread started");
        calculateProtocol = new CalculateProtocol();
        try {
            inStream = socket.getInputStream();
            in = new ObjectInputStream(inStream);

            boolean done = false;
            Object inObject = null;
            while (!done) {
                try {
                    System.out.println("Reading Object");
                    inObject = in.readObject();
                    System.out.println("Object succesfully read");
                    if (inObject instanceof ArrayList<?>) {
                        level = (int) ((ArrayList) inObject).get(0);
                        protocol = (int) ((ArrayList) inObject).get(1);

                        System.out.println("Calling ProcessInput!");
                        calculateProtocol.processInput(level, protocol, socket);
                        done = true;
                    } else {
                        System.out.println("Object send is not an ArrayList!");
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    System.out.println("Object not known");
                }
                catch (EOFException eo) {
                    System.out.println("Got another EOFEXception!");
                    //Internet tells me to do this, dont think it's good though
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
