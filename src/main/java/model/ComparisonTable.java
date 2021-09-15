package model;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
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
    private List<Path> fileList;
    private LinkedBlockingQueue<ComparisonResult> resultQueue;
    private List<ComparisonResult> finished;
    private List<ComparisonResult> highSimilarity;
    private FileIO fileHandler;
    private File directory;
    public CompareFileTask POISON = new CompareFileTask(null, null);
    private ComparerUI ui;
    private ExecutorService executor;
    private double progress;
    private List<String> fileExtensions;
    private Thread comparisonTableThread;

    public ComparisonTable(ComparerUI inUi, File inDirectory)
    {
        directory = inDirectory;
        ui = inUi;
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        compareTasks = new ArrayList<>();
        finished = new ArrayList<>();
        resultQueue = new LinkedBlockingQueue<>();
        fileHandler = new FileIO("results.csv");
        fileList = new ArrayList<>();
        highSimilarity = new ArrayList<>();
        progress = 0.0;

        fileExtensions = new ArrayList<>();
        fileExtensions.add("txt");
        fileExtensions.add("md");
        fileExtensions.add("java");
        fileExtensions.add("cs");
    }

    private void run()
    {
        Path[] fileArray;
        ComparisonPair tempPair;
        Iterator<CompareFileTask> iter;
        ComparisonResult newResult;

        //Get paths to all files
        try {
            Files.walkFileTree(directory.toPath(), new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                {
                    String fileName;
                    String[] splitName;

                    fileName = file.getFileName().toString();
                    splitName = fileName.split("\\.");


                    try 
                    {
                        //Check if file extension is in list of valid extensions and if size is not zero. Add file if valid
                        if (fileExtensions.contains(splitName[splitName.length -1]) && Files.size(file) > 0)
                        {
                            System.out.println("Testing");
                            fileList.add(file);
                        }
                        
                    } catch (IOException e) {
                        //Just skip file if an exception is thrown
                    }

                    return FileVisitResult.CONTINUE;
                }
            });

            //Interrupt thread if only 1 file (No comparisons possible)
            if (fileList.size() <= 1)
            {
                comparisonTableThread.interrupt();
                //Sleep to check if check if interrupted and throw exception to skip comparing
                Thread.sleep(1);
            }



            //Loop over list of files to get all combinations (tasks) there *should* be no duplicate tasks
            fileArray = fileList.toArray(new Path[1]);

            for (int ii = 0; ii < fileArray.length - 1; ii++)
            {
                for (int jj = ii + 1; jj < fileArray.length; jj++)
                {
                    tempPair = new ComparisonPair(fileArray[ii], fileArray[jj]);
                    compareTasks.add(new CompareFileTask(tempPair, this));

                }
            }

            iter = compareTasks.iterator();

            //Submit every task to the executor service
            while (iter.hasNext())
            {
                executor.submit(iter.next());
            }



            //Start the fileIO thread
            fileHandler.start();


            //Loop until all tasks are complete (interrupted exception thrown)
            while (true)
            {
                //Take new result (Can throw interrupted exception)
                newResult = resultQueue.take();
                
                //Add to results.csv
                fileHandler.add(newResult);


                //Add to finished
                finished.add(newResult);

                //Update progress
                progress = (double)finished.size() / (double)compareTasks.size();

                //Check for high similarity
                if (newResult.getSimilarity() > 0.5)
                {
                    highSimilarity.add(newResult);
                }

                updateUI();

                //If all tasks complete, interrupt thread
                if (finished.size() == compareTasks.size())
                {
                    Thread.currentThread().interrupt();
                }


            }

        } catch (IOException e) {
            //TODO: handle exception
        }
        catch (InterruptedException interruptException)
        {
            //Exit loop. All files finished comparing OR stop button clicked

            //Shutdown executor after currently executing tasks finish executing
            executor.shutdownNow();
            System.out.println(executor.toString());

            //Also stop the File IO thread if started
            fileHandler.stop();
        }

        System.out.println("Compare Tasks: " + compareTasks.size());
        System.out.println("Finished: " + finished.size());




        
    }

    public void queueResult(ComparisonResult newResult)
    {
        resultQueue.add(newResult);
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

    public void start()
    {
        //Start the run() method in a new thread
        comparisonTableThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                ComparisonTable.this.run();
            }
        });

        comparisonTableThread.start();
    }

    public void stop()
    {
        //Stop comparisons
        comparisonTableThread.interrupt();
    }

}
