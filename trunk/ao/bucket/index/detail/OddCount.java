package ao.bucket.index.detail;

import ao.holdem.persist.GenericBinding;
import ao.odds.agglom.Odds;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

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
        this( odds, 0 );
    }
    private OddCount(Odds odds, int initialCount)
    {
        ODDS  = odds;
        count = initialCount;
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


    //--------------------------------------------------------------------
    public static final Binding BINDING = new Binding();
    public static class Binding extends GenericBinding<OddCount>
    {
        public OddCount read(TupleInput input)
        {
            return new OddCount(
                new Odds(input.readLong(),
                         input.readLong(),
                         input.readLong()),
                input.readInt());
        }

        public void write(OddCount o, TupleOutput to)
        {
            to.writeLong( o.ODDS.winOdds() );
            to.writeLong( o.ODDS.loseOdds() );
            to.writeLong( o.ODDS.splitOdds() );
            to.writeLong( o.count );
        }
    }
}
