package ao.holdem.tourney;

/**
 *
 */
public class Winnings implements Comparable<Winnings>
{
    //--------------------------------------------------------------------
    private double winnings;
    private int    confidence;
    private double avgWins;


    //--------------------------------------------------------------------
    public Winnings() {}

    public Winnings(Winnings copyWinnings)
    {
        this(copyWinnings.winnings, copyWinnings.confidence, false);
    }

    public Winnings(double deltaStack, int teamMembers)
    {
        this(deltaStack / teamMembers, 1, false);
    }

    private Winnings(double value, int certainty, boolean disambiguator)
    {
        winnings   = value;
        confidence = certainty;

        avgWins = winnings / certainty;
    }


    //--------------------------------------------------------------------
    public boolean isCloseToZero()
    {
        return Math.abs(winnings) < 0.0001;
    }


    //--------------------------------------------------------------------
    public void add(Winnings addend)
    {
        winnings   += addend.winnings;
        confidence += addend.confidence;
        avgWins     = winnings / confidence;
    }


    //--------------------------------------------------------------------
    public Winnings negate()
    {
        return new Winnings(-winnings, confidence);
    }


    //--------------------------------------------------------------------
    @Override
    public int compareTo(Winnings o)
    {
//        return Double.compare(avgWins, o.avgWins);
        return Double.compare(winnings, o.winnings);
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
//        return Double.toString( avgWins );
        return Double.toString( winnings );
    }
}
