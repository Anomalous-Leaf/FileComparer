package view;

import javafx.application.Application;
import javafx.beans.property.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;
import model.*;

public class ComparerUI extends Application
{
    public static void main(String[] args)
    {
        Application.launch(args);
    }
    
    private TableView<ComparisonResult> resultTable = new TableView<>();  
    private ProgressBar progressBar = new ProgressBar();
    private ComparisonTable comparer;
    List<ComparisonResult> newResults;


    
    @Override
    public void start(Stage stage)
    {
        stage.setTitle("File comparer");
        stage.setMinWidth(600);

        // Create toolbar
        Button compareBtn = new Button("Compare...");
        Button stopBtn = new Button("Stop");
        ToolBar toolBar = new ToolBar(compareBtn, stopBtn);
        
        // Set up button event handlers.
        compareBtn.setOnAction(event -> crossCompare(stage));
        stopBtn.setOnAction(event -> stopComparison());
        
        // Initialise progressbar
        progressBar.setProgress(0.0);
        
        TableColumn<ComparisonResult,String> file1Col = new TableColumn<>("File 1");
        TableColumn<ComparisonResult,String> file2Col = new TableColumn<>("File 2");
        TableColumn<ComparisonResult,String> similarityCol = new TableColumn<>("Similarity");
        
        // The following tell JavaFX how to extract information from a ComparisonResult 
        // object and put it into the three table columns.
        file1Col.setCellValueFactory(   
            (cell) -> new SimpleStringProperty(cell.getValue().getFile1()) );
            
        file2Col.setCellValueFactory(   
            (cell) -> new SimpleStringProperty(cell.getValue().getFile2()) );
            
        similarityCol.setCellValueFactory(  
            (cell) -> new SimpleStringProperty(
                String.format("%.1f%%", cell.getValue().getSimilarity() * 100.0)) );
          
        // Set and adjust table column widths.
        file1Col.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.40));
        file2Col.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.40));
        similarityCol.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.20));            
        
        // Add the columns to the table.
        resultTable.getColumns().add(file1Col);
        resultTable.getColumns().add(file2Col);
        resultTable.getColumns().add(similarityCol);

        // Add the main parts of the UI to the window.
        BorderPane mainBox = new BorderPane();
        mainBox.setTop(toolBar);
        mainBox.setCenter(resultTable);
        mainBox.setBottom(progressBar);
        Scene scene = new Scene(mainBox);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    @Override
    public void stop()
    {
        //Called here just in case user does not click the stop button in window before closing application
        stopComparison();
    }
    
    private void crossCompare(Stage stage)
    {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File("."));
        dc.setTitle("Choose directory");
        File directory = dc.showDialog(stage);
        
        if (directory != null)
        {
            System.out.println("Comparing files within " + directory + "...");
        }
        else
        {
            System.out.println("No directory found");
        }

        //Create new thread and run in new thread if valid directory
        if (directory != null)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    comparer = new ComparisonTable(ComparerUI.this, directory);
                    comparer.start();
                }
            }).start();
        }
    }
    
    private void stopComparison()
    {
        //If comparer is null, comparing process is not started. Only call stop() if started, else there is nothing to stop
        if (comparer != null)
        {
            System.out.println("Stopping comparison...");
            comparer.stop();
        }

    }

    public void setResults(List<ComparisonResult> inNewResults)
    {
        newResults = inNewResults;
        resultTable.getItems().setAll(newResults);
    }

    public void setProgress(double progress)
    {
        progressBar.setProgress(progress);
    }
}
