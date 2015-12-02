import calculate.Edge;
import calculate.KochFractal;
import timeutil.TimeStamp;

import java.io.*;
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
    private KochFractal kf;
    //Binary
    private FileOutputStream fos;
    private ObjectOutputStream outBin;
    private BufferedOutputStream bos;
    //Text
    private FileWriter fw;
    private BufferedWriter out;

    public writingToFileWithLevel(int level, int soort)
    {
        edges = new ArrayList<>();
        if (level < 1 || level > 12) {
            System.out.println("Invalid level number");
            return;
        }

        objecten = new ArrayList<>();
        objecten.add(level);
        kf = new KochFractal();

        kf.addObserver(this);

        kf.setLevel(level);
        System.out.println("Level: " + level);

        if (soort == 1) {
            bufferedBinary();
        }
        else if (soort == 2) {
            normalBinary();
        }
        else if (soort == 3){
            bufferedText(level);
        }



    }

    public synchronized void addEdge(Edge e) {
        edges.add(e);
    }

    public void bufferedBinary() {
        TimeStamp ts = new TimeStamp();
        ts.setBegin();
        kf.generateBottomEdge();
        kf.generateRightEdge();
        kf.generateLeftEdge();
        ts.setEnd();
        objecten.add(ts.toString());
        objecten.add(edges);

        //With Binary
        fos = null;
        outBin = null;
        bos = null;

        try
        {
            fos = new FileOutputStream("edges.dat");
            bos = new BufferedOutputStream(fos);
            outBin = new ObjectOutputStream(bos);
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
            TimeStamp tsWrite = new TimeStamp();
            tsWrite.setBegin();
            outBin.writeObject(objecten);
            tsWrite.setEnd();
            System.out.println(tsWrite.toString());
            outBin.close();
        }
        catch (IOException e)
        {
            System.out.println("IOException");
            e.printStackTrace();
        }

        System.out.println(edges.size());
        System.out.println("finished");
    }

    public void normalBinary()
    {
        TimeStamp ts = new TimeStamp();
        ts.setBegin();
        kf.generateBottomEdge();
        kf.generateRightEdge();
        kf.generateLeftEdge();
        ts.setEnd();
        objecten.add(ts.toString());
        objecten.add(edges);

        //With Binary
        fos = null;
        outBin = null;

        try
        {
            fos = new FileOutputStream("edges.dat");
            outBin = new ObjectOutputStream(fos);
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
            TimeStamp tsWrite = new TimeStamp();
            tsWrite.setBegin();
            outBin.writeObject(objecten);
            tsWrite.setEnd();
            System.out.println(tsWrite.toString());
            outBin.close();
        }
        catch (IOException e)
        {
            System.out.println("IOException");
            e.printStackTrace();
        }

        System.out.println(edges.size());
        System.out.println("finished");
    }

    public void bufferedText(int level) {
        TimeStamp ts = new TimeStamp();
        ts.setBegin();
        kf.generateBottomEdge();
        kf.generateRightEdge();
        kf.generateLeftEdge();
        ts.setEnd();
        objecten.add(ts.toString());
        objecten.add(edges);

        //With Textfile
        fw = null;
        out = null;


        try
        {
            fw = new FileWriter("edges.txt");
            out = new BufferedWriter(fw);
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
            TimeStamp tsWrite = new TimeStamp();
            tsWrite.setBegin();
            String sendString = "";
            sendString += level + System.lineSeparator();
            sendString += ts.toString() + System.lineSeparator();
            for (Edge e : edges) {
                sendString += e.toString() + System.lineSeparator();
            }
            out.write(sendString);
            tsWrite.setEnd();
            System.out.println(tsWrite.toString());
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
    public static void main(String[] args) {
        int level = 0;
        System.out.print("Insert level between 1 and 12; Level: ");
        Scanner in = new Scanner(System.in);
        level = in.nextInt();
        new writingToFileWithLevel(level, 1);
    }

    @Override
    public void update(Observable observable, Object o)
    {
        addEdge((Edge)o);
    }
}
