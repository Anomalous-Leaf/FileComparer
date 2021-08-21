package model;

import java.nio.file.*;

public class ComparisonPair 
{
    private Path file1;
    private Path file2;

    public ComparisonPair(Path inFile1, Path inFile2)
    {
        file1 = inFile1;
        file2 = inFile2;
    }

    public Path getFirstPath()
    {
        return file1;
    }

    public Path getSecondPath()
    {
        return file2;
    }
}
