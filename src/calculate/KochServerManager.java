package calculate;

import timeutil.TimeStamp;

import java.io.*;
import java.net.Socket;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Nekkyou on 12-1-2016.
 */
public class KochServerManager implements Observer
{
    private KochFractal kochFractal;
    private ArrayList<Edge> edges;
    private int protocol;
    private Socket socket;
    private int level;

    //For returning stuff
    private OutputStream outStream;
    private ObjectOutputStream out;

    //For MAF
    private ByteArrayOutputStream baos;
    private ObjectOutputStream outBin;


    public KochServerManager(int protocol, int level, Socket socket) {
        System.out.println("Created a KochServerManager");
        this.protocol = protocol;
        this.socket = socket;
        this.level = level;

        try
        {
            outStream = socket.getOutputStream();
            out = new ObjectOutputStream(outStream);

            baos = new ByteArrayOutputStream();
            outBin = new ObjectOutputStream(baos);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


        kochFractal = new KochFractal();
        kochFractal.addObserver(this);
        kochFractal.setLevel(level);
        edges = new ArrayList<>();

        if(protocol == 1){
            writeAllEdges();
        }
        else if(protocol == 2){
            writeEdgeForEdge();
        }

        createMemMappedFile();
    }

    @Override
    public synchronized void update(Observable o, Object o1)
    {
        Edge e = (Edge)o1;
        edges.add(e);

        if (protocol == 2) {
            sendSingleEdge(e);
        }
    }

    public void writeAllEdges() {
        System.out.println("Starting to write all edges");
        kochFractal.generateBottomEdge();
        kochFractal.generateLeftEdge();
        kochFractal.generateRightEdge();

        sendEdges();
    }

    public void writeEdgeForEdge() {
        System.out.println("Starting to write all edges");
        kochFractal.generateBottomEdge();
        kochFractal.generateLeftEdge();
        kochFractal.generateRightEdge();

        String endString = "Ended";

        try
        {
            out.writeObject(endString);
            outBin.writeObject(edges);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendSingleEdge(Edge e) {
        try {
            out.writeObject(e);
            out.flush();
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
    }

    public void sendEdges(){
        System.out.println("Sending edges!");

        try
        {
            out.writeObject(edges);
            outBin.writeObject(edges);
            out.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    public void createMemMappedFile() {
        byte[] bytes = null;

        bytes = baos.toByteArray();

        RandomAccessFile memoryMappedFile = null;
        try
        {
            memoryMappedFile = new RandomAccessFile("D:\\cacheFile.txt", "rw");
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        //Mapping a file into memory
        FileChannel      fc  = memoryMappedFile.getChannel();
        MappedByteBuffer out = null;
        try
        {
            out = fc.map(FileChannel.MapMode.READ_WRITE, 0, bytes.length);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        out.put(bytes);
    }

    public void readMemMappedFile() {
        try
        {
            RandomAccessFile memoryMappedFile = new RandomAccessFile("D:\\cacheFile.txt", "r");

            //Mapping a file into memory
            FileChannel      fc  = memoryMappedFile.getChannel();
            MappedByteBuffer out = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

            byte[] bytes = new byte[(int) fc.size()];

            //reading from memory file in Java
            out.get(bytes);
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream    ois  = new ObjectInputStream(bais);

            try
            {
                edges = (ArrayList<Edge>) ois.readObject();
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
