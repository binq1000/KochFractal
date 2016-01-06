import calculate.Edge;
import calculate.KochFractal;
import calculate.WatchDirMain;
import timeutil.TimeStamp;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    private int soort = 0;
    private int level = 0;
    //Binary
    private FileOutputStream fos;
    private ObjectOutputStream outBin;
    private BufferedOutputStream bos;
    //Text
    private FileWriter fw;
    private BufferedWriter out;
    //MemoryMapped
    private ByteArrayOutputStream baos;
    //FileLocking
    FileLock lock = null;

    public writingToFileWithLevel(int level, int soort)
    {
        this.level = level;
        this.soort = soort;
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
        else if (soort == 4) {
            memoryMapped(level);
        }
        else if (soort == 5) {
            memMappedZonderSendString();
        }
        else if (soort == 6) {
            writeAndRead();
        }



    }

    public synchronized void addEdge(Edge e) {
        edges.add(e);
        if (soort == 6) {
            writeSingleEdge(false);
        }
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
            fos = new FileOutputStream("D:\\edges2.dat");
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

        //Renaming file:
        try
        {
            Files.delete(Paths.get("D:\\edges.dat"));
        }
        catch (IOException e)
        {
            System.out.println("Deleting failed");
            e.printStackTrace();
        }

        // File (or directory) with old name
        File file = new File("D:\\edges2.dat");

        // File (or directory) with new name
        File file2 = new File("D:\\edges.dat");

        // Rename file (or directory)
        boolean success = file.renameTo(file2);

        if (!success) {
            // File was not successfully renamed
            System.out.println("Renamed failed");
        }


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
            fw = new FileWriter("D:\\edges.txt");
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


    public void memoryMapped(int level) {
        TimeStamp ts = new TimeStamp();
        ts.setBegin();
        kf.generateBottomEdge();
        kf.generateRightEdge();
        kf.generateLeftEdge();
        ts.setEnd();
        objecten.add(ts.toString());
        objecten.add(edges);

        String sendString = "";
        sendString += level + System.lineSeparator();
        sendString += ts.toString() + System.lineSeparator();
        for (Edge e : edges) {
            sendString += e.toString() + System.lineSeparator();
        }

        int nLevelByte = 4;
        int nEdgeByte = 7 * 8;
        int nTotalBytes = nLevelByte + (edges.size() * nEdgeByte);

        RandomAccessFile memoryMappedFile = null;
        try
        {
            memoryMappedFile = new RandomAccessFile("D:\\data.txt", "rw");
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        //Mapping a file into memory
        FileChannel      fc = memoryMappedFile.getChannel();
        MappedByteBuffer out = null;
        try
        {
            out = fc.map(FileChannel.MapMode.READ_WRITE, 0, sendString.getBytes().length);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        //Writing into Memory Mapped File
        TimeStamp tsWrite = new TimeStamp();
        tsWrite.setBegin();

        System.out.println(sendString.getBytes().length);
        out.put(sendString.getBytes());
        tsWrite.setEnd();
        System.out.println(tsWrite.toString());

        System.out.println("Writing to Memory Mapped File is completed");
    }

    public void memMappedZonderSendString() {
        //Test stuff
        TimeStamp ts = new TimeStamp();
        ts.setBegin();
        kf.generateBottomEdge();
        kf.generateRightEdge();
        kf.generateLeftEdge();
        ts.setEnd();
        objecten.add(ts.toString());
        objecten.add(edges);

//        int nLevelByte = 4;         //The level is 4 bytes
//        int nEdgeByte = 7 * 8;      //X1, Y1, X2, Y2, red, green, red (all doubles)
//        int nTotalBytes = nLevelByte + (edges.size() * nEdgeByte);
//        System.out.println("Total bytes" + nTotalBytes);

        createBaosAndOutBin();

        byte[] bytes = null;

        try
        {
            outBin.writeObject(objecten);
            bytes = baos.toByteArray();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


        RandomAccessFile memoryMappedFile = null;
        try
        {
            memoryMappedFile = new RandomAccessFile("D:\\data.txt", "rw");
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        //Mapping a file into memory
        FileChannel      fc = memoryMappedFile.getChannel();
        MappedByteBuffer out = null;
        try
        {
            out = fc.map(FileChannel.MapMode.READ_WRITE, 0, bytes.length);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        //Writing into Memory Mapped File
        TimeStamp tsWrite = new TimeStamp();
        tsWrite.setBegin();

        out.put(bytes);

        tsWrite.setEnd();
        System.out.println(tsWrite.toString());

        System.out.println("Writing to Memory Mapped File is completed");
    }



    //Write and read at the same time
    public void writeAndRead() {
        TimeStamp ts = new TimeStamp();
        ts.setBegin();
        kf.generateBottomEdge();
        kf.generateRightEdge();
        kf.generateLeftEdge();
        writeSingleEdge(true);
        ts.setEnd();
//        objecten.add(ts.toString());

        System.out.println("Done with writing the files");
    }

    private void createBaosAndOutBin() {
        try
        {
            baos = new ByteArrayOutputStream();
            outBin = new ObjectOutputStream(baos);
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
    }

    public synchronized void writeSingleEdge(boolean isEnd) {
        createBaosAndOutBin();

        objecten.clear();
        objecten.add(level);
        objecten.add(edges.size());
        objecten.add(edges);
        objecten.add(isEnd);

        byte[] bytes = null;

        try
        {
            outBin.writeObject(objecten);
            bytes = baos.toByteArray();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


        RandomAccessFile memoryMappedFile = null;
        try
        {
            memoryMappedFile = new RandomAccessFile("D:\\readThis.txt", "rw");
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        //Mapping a file into memory
        FileChannel      fc = memoryMappedFile.getChannel();

        do
        {
            try
            {
                lock = fc.tryLock();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (OverlappingFileLockException olfle)
            {
                System.out.println("File already locked");
                olfle.printStackTrace();
            }
            try
            {
                Thread.currentThread().sleep(1);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        while (lock == null);


        MappedByteBuffer out = null;
        try
        {
            out = fc.map(FileChannel.MapMode.READ_WRITE, 0, bytes.length);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        //Writing into Memory Mapped File
        TimeStamp tsWrite = new TimeStamp();
        tsWrite.setBegin();

        out.put(bytes);

        tsWrite.setEnd();
        System.out.println(tsWrite.toString());

        if (lock != null) {
            try
            {
                lock.release();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }


        try
        {
            fc.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int level = 0;
        int soort = 0;
        System.out.println("Insert een soort: ");
        System.out.println("1. Buffered Binary");
        System.out.println("2. Normal Binary");
        System.out.println("3. Buffered Text");
        System.out.println("4. Memory mapped");
        System.out.println("5. Memory Mapped zonder sendstring");
        System.out.println("6. WriteAndRead");
        Scanner in = new Scanner(System.in);
        soort = in.nextInt();

        System.out.print("Insert level between 1 and 12; Level: ");
        Scanner inLevel = new Scanner(System.in);
        level = inLevel.nextInt();
        new writingToFileWithLevel(level, soort);
    }

    @Override
    public void update(Observable observable, Object o)
    {

        addEdge((Edge)o);
    }
}
