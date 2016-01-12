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
        boolean listening = true;

        try {
            serverSocket = new ServerSocket(8189);
        }
        catch (IOException e) {
            System.err.println("Could not listen on port: 4444.");
            System.exit(-1);
        }

        while (listening) {
            Thread t = new Thread(new serverRunnable(serverSocket.accept()));
            t.start();
        }

        serverSocket.close();
    }
}
