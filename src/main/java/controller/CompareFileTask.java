package controller;

import model.*;

public class CompareFileTask implements Runnable
{
    ComparisonPair pair;
    ComparisonTable table;

    public CompareFileTask(ComparisonPair inPair, ComparisonTable inTable)
    {
        pair = inPair;
        table = inTable;
    }
    
    
}
