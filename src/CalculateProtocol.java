import calculate.Edge;
import calculate.KochFractal;
import calculate.KochServerManager;

import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Kees on 06/01/2016.
 */
public class CalculateProtocol {

    private ArrayList<Edge> edges = new ArrayList<>();
    private KochServerManager ksm;
    private int level;
    private Socket socket;

    public void processInput(int level, int kindOfCalculation, Socket socket) {
        //For kindOfCalculation:
        // 1 == Read AFTER write
        // 2 == Read DURING write
        // 3 == Zooming

        this.level = level;
        this.socket = socket;

        switch (kindOfCalculation){
            case 1: CalculateAllEdges();
                break;
            case 2: CalculateEdgesPartial();
                break;
            case 3: Zoom();
                break;
            default: System.out.println("Wrong number");
                break;
        }
    }

    private void CalculateAllEdges() {
        ksm = new KochServerManager(1, level, socket);
    }

    private void CalculateEdgesPartial() {
        ksm = new KochServerManager(2, level, socket);
    }

    private void Zoom() {
        ksm = new KochServerManager(3, level, socket);
    }
}
