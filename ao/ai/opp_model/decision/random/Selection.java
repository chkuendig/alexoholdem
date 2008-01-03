package ao.ai.opp_model.decision.random;

/**
 *
 */
public class Selection
{
    //--------------------------------------------------------------------
    private final double selected;
    private final double total;


    //--------------------------------------------------------------------
    public Selection(double proportion,
                     double sampleSize)
    {
        this.selected = proportion;
        this.total    = sampleSize;
    }

    public Selection()
    {
        this(0, 0);
    }


    //--------------------------------------------------------------------
    public double proportion()
    {
        return selected;
    }
    public double sampleSize()
    {
        return total;
    }


    //--------------------------------------------------------------------
    public Selection plus(Selection addend)
    {
        return new Selection(selected + addend.selected,
                             total    + addend.total);
    }

    public int averageSelected(int over)
    {
        return (int) Math.round(selected / over); 
    }
}
