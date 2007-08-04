package ao.holdem.bots.opp_model.neat;

/**
 *
 */
public class InnovationNumber
{
    //--------------------------------------------------------------------
    private final int VAL;


    //--------------------------------------------------------------------
    public InnovationNumber(int value)
    {
        VAL = value;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return String.valueOf(VAL);
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InnovationNumber that = (InnovationNumber) o;
        return VAL == that.VAL;
    }


    @Override
    public int hashCode()
    {
        return VAL;
    }
}

