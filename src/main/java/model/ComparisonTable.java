package model;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import controller.*;
import javafx.application.Platform;
import view.*;

public class ComparisonTable 
{
    private List<CompareFileTask> compareTasks;
    private LinkedBlockingQueue<ComparisonResult> resultQueue;
    private List<ComparisonResult> finished;
    private List<ComparisonResult> highSimilarity;
    private FileIO fileHandler;
    private File directory;
    public CompareFileTask POISON = new CompareFileTask(null, null);
    private ComparerUI ui;
    private ExecutorService executor;
    private double progress;

    public ComparisonTable(ComparerUI inUi, File inDirectory)
    {
        directory = inDirectory;
        ui = inUi;
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        compareTasks = new ArrayList<>();
        finished = new ArrayList<>();
        resultQueue = new LinkedBlockingQueue<>();
        fileHandler = new FileIO();
        progress = 0.0;
    }

    public void start()
    {
        //Get paths to all files

        //Loop over to get all combinations (tasks)

        //Iterate over and submit all tasks to executor

        //Loop until all tasks are complete
    }

    public void addCompared(ComparisonResult newResult)
    {
        finished.add(newResult);
    }

    public void updateUI()
    {
        Platform.runLater(new Runnable(){
            @Override
            public void run()
            {
                ui.setResults(highSimilarity);
                ui.setProgress(progress);
            }
        });
    }

}
