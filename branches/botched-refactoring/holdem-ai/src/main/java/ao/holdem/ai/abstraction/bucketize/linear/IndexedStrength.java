package ao.holdem.ai.abstraction.bucketize.linear;

import ao.holdem.engine.detail.river.ProbabilityEncoding;
import ao.util.math.Calc;

/**
 * User: Alex
 * Date: 12-May-2009
 * Time: 9:21:14 PM
 */
public class IndexedStrength
        implements Comparable<IndexedStrength>
{
    //--------------------------------------------------------------------
    private final int  index;
    private final char strength;
    private final byte represents;


    //--------------------------------------------------------------------
    public IndexedStrength(
            long   canonIndex,
            double handStrength,
            byte   represents)
    {
        this(canonIndex,
             ProbabilityEncoding.encodeWinProb(
                     handStrength),
             represents);
    }

    public IndexedStrength(
            long canonIndex,
            char compactHandStrength,
            byte representsCards)
    {
        index      = (int) canonIndex;
        strength   = compactHandStrength;
        represents = representsCards;
    }


    //--------------------------------------------------------------------
    public long index()
    {
        return Calc.unsigned(index);
    }

    public double realStrength()
    {
        return ProbabilityEncoding.decodeWinProb( strength );
    }
    public char strength()
    {
        return strength;
    }

    public byte represents()
    {
        return represents;
    }


    //--------------------------------------------------------------------
    public int compareTo(IndexedStrength o) {
        return strength - o.strength;
    }
}
