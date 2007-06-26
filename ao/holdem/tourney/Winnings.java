package ao.holdem.tourney;

/**
 *
 */
public class Winnings implements Comparable<Winnings>
{
    //--------------------------------------------------------------------
    private double winnings;


    //--------------------------------------------------------------------
    public Winnings() {}

    public Winnings(Winnings copyWinnings)
    {
        this(copyWinnings.winnings);
    }

    public Winnings(double deltaStack, int teamMembers)
    {
        this(deltaStack / teamMembers);
    }

    private Winnings(double value)
    {
        winnings = value;
    }


    //--------------------------------------------------------------------
    public boolean isCloseToZero()
    {
        return Math.abs(winnings) < 0.0001;
    }

    //--------------------------------------------------------------------
    public void add(Winnings addend)
    {
        winnings += addend.winnings;
    }


    //--------------------------------------------------------------------
    public Winnings negate()
    {
        return new Winnings(-winnings);
    }


    //--------------------------------------------------------------------
    @Override
    public int compareTo(Winnings o)
    {
        return Double.compare(winnings, o.winnings);
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return Double.toString( winnings );
    }
}
