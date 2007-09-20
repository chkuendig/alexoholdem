package ao.odds;

/**
 * odds from the point of view of one player.
 */
public class Odds
{
    //--------------------------------------------------------------------
    private final long WIN;
    private final long LOSE;
    private final long SPLIT;


    //--------------------------------------------------------------------
    public Odds()
    {
        this(0, 0, 0);
    }

    public Odds(long winOdds, long loseOdds, long splitOdds)
    {
        WIN   = winOdds;
        LOSE  = loseOdds;
        SPLIT = splitOdds;
    }


    //--------------------------------------------------------------------
    public Odds plus(Odds addend)
    {
//        return (winPercent() < between.winPercent())
//                ? this
//                : between;
            return new Odds(WIN   + addend.WIN,
                            LOSE  + addend.LOSE,
                            SPLIT + addend.SPLIT);
    }


    //--------------------------------------------------------------------
    public long winOdds()
    {
        return WIN;
    }
    public double winPercent()
    {
        return ((double) WIN) / (WIN + LOSE + SPLIT);
    }


    //--------------------------------------------------------------------
    public long loseOdds()
    {
        return LOSE;
    }
    public double losePercent()
    {
        return ((double) LOSE) / (WIN + LOSE + SPLIT);
    }


    //--------------------------------------------------------------------
    public long splitOdds()
    {
        return SPLIT;
    }
    public double splitPercent()
    {
        return ((double) SPLIT) / (WIN + LOSE + SPLIT);
    }


    //--------------------------------------------------------------------
    public double strengthVsRandom()
    {
        return (WIN + SPLIT/2.0)
               / (WIN + LOSE + SPLIT);

    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "[win: "    + WIN   +
                " (" + Math.round(winPercent()  * 100) + ")" +
               ", lose: "  + LOSE  +
                " (" + Math.round(losePercent() * 100) + ")" +
               ", values: " + SPLIT +
               " (" + Math.round(splitPercent() * 100) + ")" + "]";
//        return "new Odds(" + WIN + ", " + LOSE + ", " + SPLIT + ")";
//        return WIN + "\t" + LOSE + "\t" + SPLIT;
    }
}
