import calculate.Edge;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Kees on 06/01/2016.
 */
public class CalculateProtocol {
    private ArrayList<Edge> edges = new ArrayList<>();

    private String[] clues = { "Turnip", "Little Old Lady", "Atch", "Who", "Who" };
    private String[] answers = { "Turnip the heat, it's cold in here!",
            "I didn't know you could yodel!",
            "Bless you!",
            "Is there an owl in here?",
            "Is there an echo in here?" };


    public void processInput(int level, int kindOfCalculation) {
        //For kindOfCalculation:
        // 1 == Read AFTER write
        // 2 == Read DURING write
        // 3 == Zooming


        switch (kindOfCalculation){
            case 1: CalculateAllEdges(level);
                break;
            case 2: CalculateEdgesPartial(level);
                break;
            case 3: Zoom(level);
                break;
            default: System.out.println("Wrong number");
                break;
        }
    }

    public void CalculateAllEdges(int level) {

    }

    public void CalculateEdgesPartial(int level) {

    }

    public void Zoom(int level) {

    }
}
