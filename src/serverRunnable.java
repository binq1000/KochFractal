import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Nekkyou on 6-1-2016.
 */
public class serverRunnable implements Runnable {
    private Socket socket = null;
    private CalculateProtocol calculateProtocol;

    public serverRunnable(Socket socket) {

        this.socket = socket;
    }

    @Override
    public void run()
    {
        calculateProtocol = new CalculateProtocol();
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            HashMap<Integer, Integer> levelWithProtocol = (HashMap<Integer, Integer>) ois.readObject();
            //Do stuff with the level and protocol
            int level = 0;
            int protocol = 0;
            for (Map.Entry<Integer, Integer> entry : levelWithProtocol.entrySet()) {
                level = entry.getKey();
                protocol = entry.getValue();
            }

            calculateProtocol.processInput(level, protocol);

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
