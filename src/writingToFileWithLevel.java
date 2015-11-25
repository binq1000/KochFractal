import calculate.Edge;
import calculate.KochFractal;
import timeutil.TimeStamp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Nekkyou on 25-11-2015.
 */
public class writingToFileWithLevel implements Observer
{

    private final ArrayList<Edge> edges;
    private ArrayList<Object> objecten;
    private FileOutputStream fos;
    private ObjectOutputStream out;
    private int counter = 0;

    public writingToFileWithLevel(int level)
    {
        edges = new ArrayList<>();
        if (level < 1 || level > 12) {
            System.out.println("Invalid level number");
            return;
        }

        objecten = new ArrayList<>();
        objecten.add(level);
        KochFractal kf = new KochFractal();

        kf.addObserver(this);

        kf.setLevel(level);
        System.out.println("Level: " + level);





        //Actual logic

//        ExecutorService pool = Executors.newFixedThreadPool(3);
//        pool.execute(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                kf.generateLeftEdge();
//                raiseCounter();
//            }
//        });
//
//        pool.execute(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                kf.generateBottomEdge();
//                raiseCounter();
//            }
//        });
//
//        pool.execute(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                kf.generateRightEdge();
//                raiseCounter();
//            }
//        });
//
//        while (counter < 3) {
//            try
//            {
//                Thread.sleep(1);
//            }
//            catch (InterruptedException e)
//            {
//                System.out.println("Got interrupted!");
//                e.printStackTrace();
//            }
//        }
        TimeStamp ts = new TimeStamp();
        ts.setBegin();
        kf.generateBottomEdge();
        kf.generateRightEdge();
        kf.generateLeftEdge();
        ts.setEnd();
        objecten.add(ts.toString());
        objecten.add(edges);

        //Writing of the object
        fos = null;
        out = null;


        try
        {
            fos = new FileOutputStream("edges.dat");
            out = new ObjectOutputStream(fos);
        }
        catch (FileNotFoundException e)
        {
            System.out.printf("Waarom zou je filenotfound krijgen O.o");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            System.out.println("Errors");
            e.printStackTrace();
        }

        try
        {
            out.writeObject(objecten);
        }
        catch (IOException e)
        {
            System.out.println("IOException");
            e.printStackTrace();
        }

        try
        {
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println(edges.size());
        System.out.println("finished");

    }

    public synchronized void addEdge(Edge e) {
        edges.add(e);
    }

    public synchronized void raiseCounter() {
        counter++;
    }

    public static void main(String[] args) {
        int level = 0;
        System.out.print("Insert level between 1 and 12; Level: ");
        Scanner in = new Scanner(System.in);
        level = in.nextInt();
        new writingToFileWithLevel(level);
    }

    @Override
    public void update(Observable observable, Object o)
    {
        addEdge((Edge)o);
    }
}
