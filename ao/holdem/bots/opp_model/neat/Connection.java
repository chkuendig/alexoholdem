package ao.holdem.bots.opp_model.neat;

/**
 * 
 */
public class Connection
{
    //--------------------------------------------------------------------
    private final Node             in;
    private final Node             out;
    private final double           weight;
    private final boolean          enabled;
    private final InnovationNumber innovation;


    //--------------------------------------------------------------------
    public Connection(Node             in,
                      Node             out,
                      double           weight,
                      boolean          enabled,
                      InnovationNumber innovation)
    {
        this.in         = in;
        this.out        = out;
        this.weight     = weight;
        this.enabled    = enabled;
        this.innovation = innovation;
    }


    //--------------------------------------------------------------------
    public Node in()
    {
        return in;
    }

    public Node out()
    {
        return out;
    }

    public InnovationNumber innovationNumber()
    {
        return innovation;
    }

    
    //--------------------------------------------------------------------
}
