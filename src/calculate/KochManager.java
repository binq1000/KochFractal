/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
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

import com.sun.org.apache.xpath.internal.operations.Bool;
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
    private WatchDirMain wdm;
    private int totalEdgesRead = 0;
    private int totalEdgesInFile = 0;
    private boolean isDoneReading = false;
    
    public KochManager(JSF31KochFractalFX application) {
        wdm = new WatchDirMain("D:\\", false, this);

        this.application = application;
        //KochFractal en Observer aanmaken
        kf = new KochFractal();
        kf.addObserver(this);
        edges = new ArrayList<Edge>();

        startFileMapThread();
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
            fis = new FileInputStream("D:\\edges.dat");
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
        TimeStamp ts = new TimeStamp();
        ts.setBegin();

        RandomAccessFile memoryMappedFile = new RandomAccessFile("data.txt", "r");

        //Mapping a file into memory
        FileChannel fc = memoryMappedFile.getChannel();
        MappedByteBuffer out = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

        byte[] bytes = new byte[(int) fc.size()];

        //reading from memory file in Java
        out.get(bytes);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);

        ArrayList<Object> objecten = null;
        try {
            objecten = (ArrayList<Object>) ois.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        application.setCurrentLevel((Integer) objecten.get(0));
        kf.setLevel((Integer) objecten.get(0));
        application.setTextCalc((String) objecten.get(1));
        ArrayList<Edge> edgesFromFile = (ArrayList<Edge>) objecten.get(2);

        edges.clear();
        for (Edge e : edgesFromFile){
            addEdge(e);
        }
        ts.setEnd();
        System.out.println(ts.toString());

        drawEdges();
        System.out.println("\nReading from Memory Mapped File is completed");

    }

    public void startFileMapThread(){
        edges.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                readFromFileMapWithLocking();
            }
        }).start();
    }

    public void readFromFileMapWithLocking() {
        while (true){
            try {
                RandomAccessFile memoryMappedFile = new RandomAccessFile("D:\\readThis.txt", "rw");

                System.out.println("File found");

                //Mapping a file into memory
                FileChannel fc = memoryMappedFile.getChannel();
                FileLock lock = null;
                do {
                    try {
                        lock = fc.tryLock();
                    }
                    catch (OverlappingFileLockException olfe) {
                        System.out.println("OverLappingLockException");
                        try {
                            Thread.currentThread().sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
                while(lock == null);

                MappedByteBuffer out = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

                byte[] bytes = new byte[(int) fc.size()];

                //reading from memory file in Java
                out.get(bytes);
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = new ObjectInputStream(bais);

                ArrayList<Object> objecten = null;
                try {
                    objecten = (ArrayList<Object>) ois.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                if(lock != null){
                    lock.release();
                }


                application.setCurrentLevel((Integer) objecten.get(0));
                kf.setLevel((Integer) objecten.get(0));
                //application.setTextCalc((String) objecten.get(1));
                totalEdgesInFile = (Integer) objecten.get(1);
                ArrayList<Edge> edgesFromFile = (ArrayList<Edge>) objecten.get(2);
                isDoneReading = (Boolean) objecten.get(3);

                if (totalEdgesRead < totalEdgesInFile){

                    int counter = 0;
                    for (Edge e : edgesFromFile){
                        if(counter >= totalEdgesRead){
                            addEdge(e);
                        }
                        counter++;
                    }

                    totalEdgesRead = totalEdgesInFile;
                }

                if(totalEdgesRead == totalEdgesInFile && isDoneReading) {
                    totalEdgesRead = 0;
                }

                application.requestDrawEdges();
                System.out.println("\nReading from Memory Mapped File is completed");
            } catch (FileNotFoundException fnfe){
                //fnfe.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

    }

    public synchronized void entry_created() {
        readFromFile();
    }

    public synchronized void entry_deleted() {
        return;
    }

    public synchronized void entry_modified() {
        return;
    }
          
}
