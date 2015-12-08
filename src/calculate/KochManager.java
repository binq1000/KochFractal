/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;
import jsf31kochfractalfx.JSF31KochFractalFX;
import timeutil.TimeStamp;

/**
 *
 * @author Nekkyou
 */
public class KochManager implements Observer {

    private JSF31KochFractalFX application;
    private KochFractal kf;
    final ArrayList<Edge> edges;
    //Tasks
    private Task taskLeft = null;
    private Task taskRight = null;
    private Task taskBottom = null;
    //Pool
    ExecutorService pool = Executors.newFixedThreadPool(3);
    //Counter
    private int counter = 0;
    //ReadFromFile
    private FileInputStream fis;
    private BufferedInputStream bis;
    private ObjectInputStream oin;
//    private FileReader fr;
//    private BufferedReader br;
    
    public KochManager(JSF31KochFractalFX application) {
        this.application = application;
        //KochFractal en Observer aanmaken
        kf = new KochFractal();
        kf.addObserver(this);
        edges = new ArrayList<Edge>();

    }

    @Override
    public void update(Observable o, Object o1) {
        Edge e = (Edge)o1;
        edges.add(e);
    }

    public void changeLevel(int currentLevel) {
        application.disableButtons();
        kf.setLevel(currentLevel);

        edges.clear();
        TimeStamp ts = new TimeStamp();
        ts.setBegin();


        if (taskLeft != null && taskRight != null && taskBottom != null) {
            taskLeft.cancel();
            taskRight.cancel();
            taskBottom.cancel();
        }

        createTasks();
        startTasks();


        ts.setEnd();
        application.setTextCalc(ts.toString());

    }

    public void startTasks() {
        Thread thLeft = new Thread(taskLeft);
        Thread thRight = new Thread(taskRight);
        Thread thBottom = new Thread(taskBottom);

        pool.submit(thLeft);
        pool.submit(thRight);
        pool.submit(thBottom);


    }

    public void drawEdges() {
        application.clearKochPanel();

        TimeStamp ts = new TimeStamp();
        ts.setBegin();
        for (Edge e : edges)
        {
            application.drawEdge(e);
        }
        ts.setEnd();
        application.setTextDraw(ts.toString());
        
        int nrEdges = kf.getNrOfEdges();
        application.setTextNrEdges(String.valueOf(nrEdges));

        application.enableButtons();
    }

    public void drawEdge(Edge e) {
        application.drawWhiteEdge(e);
    }

    public synchronized void addEdge(Edge e) {
        edges.add(e);
    }
    
    public int getLevel() {
        return kf.getLevel();
    }
    
    public synchronized void signalEnd() {
        counter++;
        if (counter >= 3) {
            application.requestDrawEdges();
            counter = 0;
        }

    }

    public void createTasks() {
        if (taskLeft != null) {
            application.getProgressBarLeft().progressProperty().unbind();
            application.getLblLeftCalc().textProperty().unbind();
        }
        if (taskRight != null) {
            application.getProgressBarRight().progressProperty().unbind();
            application.getLblRightCalc().textProperty().unbind();
        }
        if (taskBottom != null) {
            application.getProgressBarBottom().progressProperty().unbind();
            application.getLblBottomCalc().textProperty().unbind();
        }

        taskLeft = new MyTask("Left", this);
        taskRight = new MyTask("Right", this);
        taskBottom = new MyTask("Bottom", this);

        application.getProgressBarLeft().setProgress(0);
        application.getProgressBarLeft().progressProperty().bind(taskLeft.progressProperty());
        application.getLblLeftCalc().textProperty().bind(taskLeft.messageProperty());

        application.getProgressBarRight().setProgress(0);
        application.getProgressBarRight().progressProperty().bind(taskRight.progressProperty());
        application.getLblRightCalc().textProperty().bind(taskRight.messageProperty());

        application.getProgressBarBottom().setProgress(0);
        application.getProgressBarBottom().progressProperty().bind(taskBottom.progressProperty());
        application.getLblBottomCalc().textProperty().bind(taskBottom.messageProperty());
    }


    public Task getTaskLeft() {
        return taskLeft;
    }
    public Task getTaskRight() {
        return taskRight;
    }
    public Task getTaskBottom() {
        return taskBottom;
    }

    public void readFromFile() {
        //ReadFromFile
        try {
            //Binear met buffer
            fis = new FileInputStream("edges.dat");
            bis = new BufferedInputStream(fis);
            oin = new ObjectInputStream(bis);
            //Tekstueel met buffer
//            fr = new FileReader("edges.txt");
//            br = new BufferedReader(fr);

        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to create input streams");
        }


        try {
            //Binear met buffer
            TimeStamp ts = new TimeStamp();
            ts.setBegin();

            ArrayList<Object> objectenInFile = (ArrayList<Object>)oin.readObject();
            application.setCurrentLevel((Integer) objectenInFile.get(0));
            kf.setLevel((Integer) objectenInFile.get(0));
            application.setTextCalc((String) objectenInFile.get(1));
            ArrayList<Edge> edgesFromFile = (ArrayList<Edge>) objectenInFile.get(2);

            edges.clear();
            for (Edge e : edgesFromFile) {
                addEdge(e);
            }
            oin.close();
            ts.setEnd();
            System.out.println(ts.toString());
            drawEdges();

            //Textueel met buffer
//            edges.clear();
//            int counter = 0;
//            String line = "";
//            TimeStamp ts = new TimeStamp();
//            ts.setBegin();
//            while ((line = br.readLine()) != null) {
//                if(counter == 0){
//                    application.setCurrentLevel(Integer.parseInt(line));
//                    kf.setLevel(Integer.parseInt(line));
//                    counter++;
//                }
//                else if (counter == 1){
//                    application.setTextCalc(line);
//                    counter++;
//                }
//                else if (counter == 2) {
//                    counter++;
//                }
//                else{
//                    if (line != "") {
//                        double X1 = Double.parseDouble(line.substring(0, line.indexOf(",")));
//                        line = line.substring(line.indexOf(",") + 1, line.length());
//                        double Y1 = Double.parseDouble(line.substring(0, line.indexOf(",")));
//                        line = line.substring(line.indexOf(",") + 1, line.length());
//                        double X2 = Double.parseDouble(line.substring(0, line.indexOf(",")));
//                        line = line.substring(line.indexOf(",") + 1, line.length());
//                        double Y2 = Double.parseDouble(line.substring(0, line.indexOf(",")));
//                        line = line.substring(line.indexOf(",") + 1, line.length());
//                        double red = Double.parseDouble(line.substring(0, line.indexOf(",")));
//                        line = line.substring(line.indexOf(",") + 1, line.length());
//                        double green = Double.parseDouble(line.substring(0, line.indexOf(",")));
//                        line = line.substring(line.indexOf(",") + 1, line.length());
//                        double blue = Double.parseDouble(line);
//                        addEdge(new Edge(X1, Y1, X2, Y2, red, green, blue));
//                    }
//                }
//            }
//            ts.setEnd();
//            System.out.println(ts.toString());
//            br.close();
//            drawEdges();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed");
        }

    }

    public void readFromFileMap() throws IOException {
        /*RandomAccessFile memoryMappedFile = new RandomAccessFile("data.txt", "r");

        //Mapping a file into memory
        FileChannel fc = memoryMappedFile.getChannel();
        MappedByteBuffer out = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

        //reading 10 bytes from memory file in Java
        for (int i = 0; i < fc.size(); i++) {
            System.out.print((char) out.get(i));
        }
        System.out.println("\nReading from Memory Mapped File is completed");*/


        /*char[] cbuf = new char[edges.size() + 3];
        FileReader fstream = new FileReader("edges.dat");

        BufferedReader bf;
        bf = new BufferedReader(fstream);

        while(bf.readLine() != null){
            bf.read(cbuf,0,10);
        }*/

        DataInputStream input = new DataInputStream(new FileInputStream("edges.dat"));

        //int x1;
        /*int y1;
        int x2;
        int y2;
        int red;
        int green;
        int blue;*/
        boolean stopLoop = true;
        int firstVar = 555;
        while (stopLoop) {
            //First coordinate
            if(firstVar == 555){
                firstVar = input.readInt();
                System.out.println(firstVar);
            }
            else{
                int X = input.readInt();
                System.out.println(X);
                if(firstVar == X){
                    stopLoop = false;
                }
            }
            //int x1 = input.readInt();
            /*y1 = input.readInt();
            x2 = input.readInt();
            y2 = input.readInt();
            red = input.readInt();
            green = input.readInt();
            blue = input.readInt();
            addEdge(new Edge(x1, y1, x2, y2, red, green, blue));*/
            //System.out.println(x1);
        }

        input.close();

        /*//Create file object
        File file = new File("edges.dat");

        //Get file channel in readonly mode
        FileChannel fileChannel = new RandomAccessFile(file, "r").getChannel();

        //Get direct byte buffer access using channel.map() operation
        MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());

        // the buffer now reads the file as if it were loaded in memory.
        System.out.println(buffer.isLoaded());  //prints false
        System.out.println(buffer.capacity());  //Get the size based on content size of file

        //You can read the file from this buffer the way you like.
        for (int i = 0; i < buffer.limit(); i++)
        {
            System.out.print((char) buffer.get()); //Print the content of file
        }*/

    }
          
}
