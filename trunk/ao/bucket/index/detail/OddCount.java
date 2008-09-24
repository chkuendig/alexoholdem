package ao.bucket.index.detail;

import ao.odds.agglom.Odds;

/**
 * Date: Sep 22, 2008
 * Time: 4:10:51 PM
 *
 * counts the number of times that some given
 *  odds have been encountered.
 */
public class OddCount
{
    //--------------------------------------------------------------------
    private final Odds ODDS;
    private       int  count;


    //--------------------------------------------------------------------
    public OddCount(Odds odds)
    {
        ODDS  = odds;
        count = 0;
    }


    //--------------------------------------------------------------------
    public void increment()
    {
        count++;
    }


    //--------------------------------------------------------------------
    public int count()
    {
        return count;
    }


    //--------------------------------------------------------------------
    public boolean oddsEqual(Odds odds)
    {
        return ODDS.equals( odds );
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return ODDS + "\t" + count;
    }
}
