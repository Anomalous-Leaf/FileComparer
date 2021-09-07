package model;

import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;

public class FileIO 
{
    private String fileName;
    private Thread fileWritingThread;
    private LinkedBlockingQueue<ComparisonResult> appendList;
    public ComparisonResult POISON = new ComparisonResult(null, null, 0.0);

    public FileIO(String file)
    {
        fileName = file;
        appendList = new LinkedBlockingQueue<>();
    }

    public void add(ComparisonResult newResult)
    {
        // Add result to list
        appendList.add(newResult);
    }

    public void start()
    {
        String appendText;
        ComparisonResult result;
        fileWritingThread = Thread.currentThread();

        try (FileOutputStream outputStream = new FileOutputStream(fileName, true)) 
        {
            while (true)
            {
                //Get result. Can also interrupt here
                result = appendList.take(); 

                if (result != POISON)
                {
                    //Format the results into csv format
                    appendText = result.getFile1() + "," + result.getFile2() + "," + result.getSimilarity() + "\n";

                    //Convert to byte array and write to output  stream
                    outputStream.write(appendText.getBytes());
                }
                else
                {
                    //Poison value found. Stop thread
                    fileWritingThread.interrupt();
                }

            }

        } 
        catch (IOException e) {
            //TODO: handle exception
        }
        catch (InterruptedException interruptException)
        {
            //Exit loop
        }
    }

    public void stop()
    {
        //Stop the file writing thread if started
        if (fileWritingThread != null)
        {
            appendList.add(POISON);
        }
    }
    

}
