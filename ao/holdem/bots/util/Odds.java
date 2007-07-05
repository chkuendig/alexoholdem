package ao.holdem.bots.util;

/**
 * odds from the point of view of one player.
 */
public class Odds
{
    //--------------------------------------------------------------------
    private final int WIN;
    private final int LOSE;
    private final int SPLIT;


    //--------------------------------------------------------------------
    public Odds()
    {
        this(-1, -1, -1);
    }

    public Odds(int winOdds, int loseOdds, int splitOdds)
    {
        WIN   = winOdds;
        LOSE  = loseOdds;
        SPLIT = splitOdds;
    }


    //--------------------------------------------------------------------
    public Odds min(Odds between)
    {
        return (winPercent() < between.winPercent())
                ? this
                : between;
//            return new Odds(WIN   + addend.WIN,
//                            LOSE  + addend.LOSE,
//                            SPLIT + addend.SPLIT);
    }


    //--------------------------------------------------------------------
    public int winOdds()
    {
        return WIN;
    }
    public double winPercent()
    {
        return ((double) WIN) / (WIN + LOSE + SPLIT);
    }


    //--------------------------------------------------------------------
    public int loseOdds()
    {
        return LOSE;
    }
    public double losePercent()
    {
        return ((double) LOSE) / (WIN + LOSE + SPLIT);
    }


    //--------------------------------------------------------------------
    public int splitOdds()
    {
        return SPLIT;
    }
    public double splitPercent()
    {
        return ((double) SPLIT) / (WIN + LOSE + SPLIT);
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "[win: "    + WIN   +
                " (" + Math.round(winPercent()  * 100) + ")" +
               ", lose: "  + LOSE  +
                " (" + Math.round(losePercent() * 100) + ")" +
               ", split: " + SPLIT +
               " (" + Math.round(splitPercent() * 100) + ")" + "]";
    }
}
