package ao.simple.rps;

/**
 * User: alex
 * Date: 16-Apr-2009
 * Time: 6:50:18 PM
 */
public enum Hand
{
    //--------------------------------------------------------------------
    ROCK, PAPER, SCISSORS;

    public static final Hand[] VALUES = values();


    //--------------------------------------------------------------------
    public Outcome vs(Hand other)
    {
        assert other != null;

        switch (this)
        {
            case ROCK: {
                switch (other) {
                    case ROCK:     return Outcome.TIE;
                    case PAPER:    return Outcome.LOSS;
                    case SCISSORS: return Outcome.WIN;
                }
            }

            case PAPER: {
                switch (other) {
                    case ROCK:     return Outcome.WIN;
                    case PAPER:    return Outcome.TIE;
                    case SCISSORS: return Outcome.LOSS;
                }
            }

            case SCISSORS: {
                switch (other) {
                    case ROCK:     return Outcome.LOSS;
                    case PAPER:    return Outcome.WIN;
                    case SCISSORS: return Outcome.TIE;
                }
            }
        }
        
        throw new IllegalStateException();
    }
}
