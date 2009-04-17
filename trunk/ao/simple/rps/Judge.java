package ao.simple.rps;

/**
 * User: alex
 * Date: 16-Apr-2009
 * Time: 7:04:07 PM
 */
public class Judge
{
    //--------------------------------------------------------------------
    private final int RUNS = 100 * 1000 * 1000;


    //--------------------------------------------------------------------
    public int tournament(Player a, Player b)
    {
        int score = 0;
        for (int i = 0; i < RUNS; i++)
        {
            Outcome outcome = witness(a, b);

                 if (outcome == Outcome.WIN ) score++;
            else if (outcome == Outcome.LOSS) score--;
        }
        return score;
    }


    //--------------------------------------------------------------------
    // outcome from player a's pov
    public Outcome witness(Player a, Player b)
    {
        return a.play().vs(
                  b.play());
    }
}
