import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by Nekkyou on 6-1-2016.
 */
public class MultiServer
{
    public static void main(String[] args) throws IOException
    {
        ServerSocket serverSocket = null;
        int port = 8189;
        boolean listening = true;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Created a ServerSocket at MultiServer");
        }
        catch (IOException e) {
            System.err.println("Could not listen on port: " + port + ".");
            System.exit(-1);
        }

        while (listening) {
            System.out.println("Creating a Thread and waiting for input");
            Thread t = new Thread(new serverRunnable(serverSocket.accept()));
            System.out.println("Input received, will now start Thread");
            t.start();
        }

        System.out.println("Closing ServerSocket");
        serverSocket.close();
    }
}
