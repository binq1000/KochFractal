import java.io.*;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by Nekkyou on 6-1-2016.
 */
public class serverRunnable implements Runnable {
    private Socket socket = null;

    public serverRunnable(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run()
    {
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            HashMap<Integer, Integer> levelWithProtocol = (HashMap<Integer, Integer>) ois.readObject();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
