package model;

import java.io.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import controller.*;
import view.*;

public class ComparisonTable 
{
    private List<CompareFileTask> compareTasks;
    private LinkedBlockingQueue<ComparisonResult> resultQueue;
    private List<ComparisonResult> finished;
    private FileIO fileHandler;
    private File directory;
    public CompareFileTask POISON = new CompareFileTask(null, null);
    private ComparerUI ui;

}
