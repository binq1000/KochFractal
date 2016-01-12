/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf31kochfractalfx;

import calculate.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

/**
 *
 * @author Nico Kuijpers
 */
public class JSF31KochFractalFX extends Application {
    
    // Zoom and drag
    private double zoomTranslateX = 0.0;
    private double zoomTranslateY = 0.0;
    private double zoom = 1.0;
    private double startPressedX = 0.0;
    private double startPressedY = 0.0;
    private double lastDragX = 0.0;
    private double lastDragY = 0.0;

    // Koch manager
    // TO DO: Create class KochManager in package calculate
    private KochManager kochManager;
    
    // Current level of Koch fractal
    private int currentLevel = 1;
    
    // Labels for level, nr edges, calculation time, and drawing time
    private Label labelLevel;
    private Label labelNrEdges;
    private Label labelNrEdgesText;
    private Label labelCalc;
    private Label labelCalcText;
    private Label labelDraw;
    private Label labelDrawText;

    private Label lblLeftCalc;
    private Label lblRightCalc;
    private Label lblBottomCalc;
    
    // Koch panel and its size
    private Canvas kochPanel;
    private final int kpWidth = 500;
    private final int kpHeight = 500;

    //ProgressBar
    private final ProgressBar progressBarLeft = new ProgressBar();
    private final ProgressBar progressBarRight = new ProgressBar();
    private final ProgressBar progressBarBottom = new ProgressBar();

    //Tasks
    private Task taskLeft = null;
    private Task taskRight = null;
    private Task taskBottom = null;

    //Buttons
    private Button buttonFitFractal;
    private Button buttonReadEdges;
    private Button buttonReadEdgesMap;
    
    @Override
    public void start(Stage primaryStage) {
       
        // Define grid pane
        GridPane grid;
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // For debug purposes
        // Make de grid lines visible
        // grid.setGridLinesVisible(true);

        // Drawing panel for Koch fractal
        kochPanel = new Canvas(kpWidth,kpHeight);
        grid.add(kochPanel, 0, 3, 25, 1);

        // Labels to present number of edges for Koch fractal
        labelNrEdges = new Label("Nr edges:");
        labelNrEdgesText = new Label();
        grid.add(labelNrEdges, 0, 0, 4, 1);
        grid.add(labelNrEdgesText, 3, 0, 22, 1);

        // Labels to present time of calculation for Koch fractal
        labelCalc = new Label("Calculating:");
        labelCalcText = new Label();
        grid.add(labelCalc, 0, 1, 4, 1);
        grid.add(labelCalcText, 3, 1, 22, 1);

        // Labels to present time of drawing for Koch fractal
        labelDraw = new Label("Drawing:");
        labelDrawText = new Label();
        grid.add(labelDraw, 0, 2, 4, 1);
        grid.add(labelDrawText, 3, 2, 22, 1);

        // Label to present current level of Koch fractal
        labelLevel = new Label("Level: " + currentLevel);
        grid.add(labelLevel, 0, 6);

        // Button to increase level of Koch fractal
        Button buttonIncreaseLevel = new Button();
        buttonIncreaseLevel.setText("Increase Level");
        buttonIncreaseLevel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                increaseLevelButtonActionPerformed(event);
            }
        });
        grid.add(buttonIncreaseLevel, 3, 6);

        // Button to decrease level of Koch fractal
        Button buttonDecreaseLevel = new Button();
        buttonDecreaseLevel.setText("Decrease Level");
        buttonDecreaseLevel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                decreaseLevelButtonActionPerformed(event);
            }
        });
        grid.add(buttonDecreaseLevel, 5, 6);

        // Button to fit Koch fractal in Koch panel
        buttonFitFractal = new Button();
        buttonFitFractal.setText("Fit Fractal");
        buttonFitFractal.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                fitFractalButtonActionPerformed(event);
            }
        });
        grid.add(buttonFitFractal, 14, 6);

        // Button to read from file and draw edges
        buttonReadEdges = new Button();
        buttonReadEdges.setText("Read Edges");
        buttonReadEdges.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                readEdgesFromFile(event);
            }
        });
        grid.add(buttonReadEdges, 14, 7);

        // Button to read from fileMap and draw edges
        buttonReadEdgesMap = new Button();
        buttonReadEdgesMap.setText("Read Edges Map");
        buttonReadEdgesMap.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                try {
                    readEdgesFromFileMap(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        grid.add(buttonReadEdgesMap, 14, 8);

        // Add mouse clicked event to Koch panel
        kochPanel.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                kochPanelMouseClicked(event);
            }
        });

        // Add mouse pressed event to Koch panel
        kochPanel.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>()
                                  {
                                      @Override
                                      public void handle(MouseEvent event)
                                      {
                                          kochPanelMousePressed(event);
                                      }
                                  });

        // Add mouse dragged event to Koch panel
        kochPanel.setOnMouseDragged(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                kochPanelMouseDragged(event);
            }
        });


        //TODO Add ProgressBar to GUI
        Label LeftLabel = new Label("Left Progress");
        lblLeftCalc = new Label(". . . . .");
        grid.add(lblLeftCalc, 7, 10);
        grid.add(LeftLabel, 3, 10);
        grid.add(progressBarLeft, 5, 10);

        Label RightLabel = new Label("Right Progress");
        lblRightCalc = new Label(". . . . .");
        grid.add(lblRightCalc, 7, 11);
        grid.add(RightLabel, 3, 11);
        grid.add(progressBarRight, 5, 11);

        Label BottomLabel = new Label("Bottom Progress");
        lblBottomCalc = new Label(". . . . .");
        grid.add(lblBottomCalc, 7, 12);
        grid.add(BottomLabel, 3, 12);
        grid.add(progressBarBottom, 5, 12);



        // Create Koch manager and set initial level
        resetZoom();
        kochManager = new KochManager(this);
        File f = new File("D:\\readThis.txt");
        if (!f.exists()) {
            kochManager.changeLevel(currentLevel);
        }


        // Create the scene and add the grid pane
        Group root = new Group();
        Scene scene = new Scene(root, kpWidth+75, kpHeight+350);
        root.getChildren().add(grid);

        // Define title and assign the scene for main window
        primaryStage.setTitle("Koch Fractal");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public void clearKochPanel() {
        GraphicsContext gc = kochPanel.getGraphicsContext2D();
        gc.clearRect(0.0,0.0,kpWidth,kpHeight);
        gc.setFill(Color.BLACK);
        gc.fillRect(0.0, 0.0, kpWidth, kpHeight);
    }
    
    public void drawEdge(Edge e) {
        // Graphics
        GraphicsContext gc = kochPanel.getGraphicsContext2D();
        
        // Adjust edge for zoom and drag
        Edge e1 = edgeAfterZoomAndDrag(e);
        
        // Set line color
        Color c = Color.hsb(e1.red, e1.green, e1.blue);
        gc.setStroke(c);
        
        // Set line width depending on level
        if (currentLevel <= 3) {
            gc.setLineWidth(2.0);
        }
        else if (currentLevel <=5 ) {
            gc.setLineWidth(1.5);
        }
        else {
            gc.setLineWidth(1.0);
        }
        
        // Draw line
        gc.strokeLine(e1.X1, e1.Y1, e1.X2, e1.Y2);
    }

    public void drawWhiteEdge(Edge e) {
        // Graphics
        GraphicsContext gc = kochPanel.getGraphicsContext2D();

        // Adjust edge for zoom and drag
        Edge e1 = edgeAfterZoomAndDrag(e);

        // Set line color
        gc.setStroke(Color.WHITE);

        // Set line width depending on level
        if (currentLevel <= 3) {
            gc.setLineWidth(2.0);
        }
        else if (currentLevel <=5 ) {
            gc.setLineWidth(1.5);
        }
        else {
            gc.setLineWidth(1.0);
        }

        // Draw line
        gc.strokeLine(e1.X1, e1.Y1, e1.X2, e1.Y2);
    }
    
    public void setTextNrEdges(String text) {
        labelNrEdgesText.setText(text);
    }
    
    public void setTextCalc(String text) {
        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                labelCalcText.setText(text);
            }
        });
        //labelCalcText.setText(text);
    }
    
    public void setTextDraw(String text) {
        labelDrawText.setText(text);
    }
    
    public void requestDrawEdges() {
        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                kochManager.drawEdges();
            }
        });
    }

    
    private void increaseLevelButtonActionPerformed(ActionEvent event) {
        if (currentLevel < 12) {
            // resetZoom();
            currentLevel++;
            labelLevel.setText("Level: " + currentLevel);
            kochManager.changeLevel(currentLevel);
        }
    } 
    
    private void decreaseLevelButtonActionPerformed(ActionEvent event) {
        if (currentLevel > 1) {
            // resetZoom();
            currentLevel--;
            labelLevel.setText("Level: " + currentLevel);
            kochManager.changeLevel(currentLevel);
        }
    } 

    private void fitFractalButtonActionPerformed(ActionEvent event) {
        resetZoom();
        kochManager.drawEdges();
    }

    private void readEdgesFromFile(ActionEvent event) {
        kochManager.readFromFile();
    }

    private void readEdgesFromFileMap(ActionEvent event) throws IOException {
        kochManager.readFromFileMap();
    }
    
    private void kochPanelMouseClicked(MouseEvent event) {
        if (Math.abs(event.getX() - startPressedX) < 1.0 && 
            Math.abs(event.getY() - startPressedY) < 1.0) {
            double originalPointClickedX = (event.getX() - zoomTranslateX) / zoom;
            double originalPointClickedY = (event.getY() - zoomTranslateY) / zoom;
            if (event.getButton() == MouseButton.PRIMARY) {
                zoom *= 2.0;
            } else if (event.getButton() == MouseButton.SECONDARY) {
                zoom /= 2.0;
            }
            zoomTranslateX = (int) (event.getX() - originalPointClickedX * zoom);
            zoomTranslateY = (int) (event.getY() - originalPointClickedY * zoom);
            kochManager.drawEdges();
        }
    }                                      

    private void kochPanelMouseDragged(MouseEvent event) {
        zoomTranslateX = zoomTranslateX + event.getX() - lastDragX;
        zoomTranslateY = zoomTranslateY + event.getY() - lastDragY;
        lastDragX = event.getX();
        lastDragY = event.getY();
        kochManager.drawEdges();
    }

    private void kochPanelMousePressed(MouseEvent event) {
        startPressedX = event.getX();
        startPressedY = event.getY();
        lastDragX = event.getX();
        lastDragY = event.getY();
    }                                                                        

    private void resetZoom() {
        int kpSize = Math.min(kpWidth, kpHeight);
        zoom = kpSize;
        zoomTranslateX = (kpWidth - kpSize) / 2.0;
        zoomTranslateY = (kpHeight - kpSize) / 2.0;
    }

    private Edge edgeAfterZoomAndDrag(Edge e) {
        return new Edge(
                e.X1 * zoom + zoomTranslateX,
                e.Y1 * zoom + zoomTranslateY,
                e.X2 * zoom + zoomTranslateX,
                e.Y2 * zoom + zoomTranslateY,
                e.red,
                e.green,
                e.blue);
    }

    public ProgressBar getProgressBarLeft() {
        return progressBarLeft;
    }
    public ProgressBar getProgressBarRight() {
        return progressBarRight;
    }
    public ProgressBar getProgressBarBottom() {
        return progressBarBottom;
    }
    public Label getLblLeftCalc() {
        return lblLeftCalc;
    }
    public Label getLblRightCalc() {
        return lblRightCalc;
    }
    public Label getLblBottomCalc() {
        return lblBottomCalc;
    }

    public void setCurrentLevel(int level) {
        currentLevel = level;
        labelLevel.setText("Level: " + currentLevel);
    }

    public void enableButtons() {
        buttonFitFractal.setDisable(false);
        buttonReadEdges.setDisable(false);
    }

    public void disableButtons() {
        buttonFitFractal.setDisable(true);
        buttonReadEdges.setDisable(true);
    }


    public void sendToServer(int level, int protocol) {
        HashMap<Integer, Integer> levelWithProtocol = new HashMap<>();
        levelWithProtocol.put(level, protocol);

        try
        {
            Socket s = new Socket("localhost", 8189);
            try {
                OutputStream outStream = s.getOutputStream();
                InputStream inStream = s.getInputStream();

                ObjectOutputStream out = new ObjectOutputStream(outStream);
                ObjectInputStream in = new ObjectInputStream(inStream);

                System.out.println("Sending data");
                out.writeObject(levelWithProtocol);
                out.flush();
            }
            finally
            {
                s.close();
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }


    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
