package ao.holdem.tourney;

/**
 *
 */
public class Winnings implements Comparable<Winnings>
{
    //--------------------------------------------------------------------
    private double winnings;


    //--------------------------------------------------------------------
    public Winnings(Winnings copyWinnings)
    {
        winnings = copyWinnings.winnings;
    }
    public Winnings()
    {
        winnings = 0;
    }
    public Winnings(double deltaStack, int teamMembers)
    {
        winnings = deltaStack / teamMembers;
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
