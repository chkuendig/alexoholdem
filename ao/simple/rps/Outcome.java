package ao.simple.rps;

/**
 * User: alex
 * Date: 16-Apr-2009
 * Time: 6:58:20 PM
 */
public enum Outcome
{
    //--------------------------------------------------------------------
    WIN, LOSS, TIE;


    //--------------------------------------------------------------------
    public int value()
    {
        switch (this)
        {
            case WIN:  return  1;
            case LOSS: return -1;
            case TIE:  return  0;
        }

        throw new IllegalStateException();
    }
}
