package cu.models.statistics;

import java.time.LocalDate;

/**
 * Created by T on 01/04/2016.
 */
public class Statistics
{
    private LocalDate date;
    private long averageUsage = 0;
    private int faultyReturns = 0;
    private int totalReturns = 0;

    public Statistics(LocalDate date, long averageUsage, int faultyReturns, int totalReturns)
    {

        setDate(date);
        setAverageUsage(averageUsage);
        setFaultyReturns(faultyReturns);
        setTotalReturns(totalReturns);
    }


    public long getAverageUsage()
    {
        return averageUsage;
    }

    public void setAverageUsage(long averageUsage)
    {
        this.averageUsage = averageUsage;
    }

    public int getFaultyReturns()
    {
        return faultyReturns;
    }

    public void setFaultyReturns(int faultyReturns)
    {
        this.faultyReturns = faultyReturns;
    }

    public int getTotalReturns()
    {
        return totalReturns;
    }

    public void setTotalReturns(int totalReturns)
    {
        this.totalReturns = totalReturns;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
