package ao.regret.holdem;

/**
 * User: alex
 * Date: 31-Mar-2009
 * Time: 12:09:53 PM
 */
public class Expectation
{
    //--------------------------------------------------------------------
    private final double dealer;
    private final double dealee;


    //--------------------------------------------------------------------
    public Expectation()
    {
        this(0, 0);
    }

    private Expectation(double dealerExpect, double dealeeExpect)
    {
        dealer = dealerExpect;
        dealee = dealeeExpect;
    }

    public Expectation(int    stakes,
                       double dealerNonLossProb)
    {
        dealer = stakes * (dealerNonLossProb - 0.5) * 2.0;
        dealee = -dealer;
    }

    public Expectation(int     dealerCommit,
                       int     dealeeCommit,
                       boolean dealerWins)
    {
        if (dealerWins) {
            dealer = dealeeCommit;
            dealee = -dealer;
        } else {
            dealee = dealerCommit;
            dealer = -dealee;
        }
    }


    //--------------------------------------------------------------------
    public double value(boolean forDealer)
    {
        return forDealer ? dealer : dealee;
    }


    //--------------------------------------------------------------------
    public Expectation temper(double by)
    {
        return new Expectation(dealer * by, dealee * by);
    }
    
    public Expectation plus(Expectation addend)
    {
        return new Expectation(dealer + addend.dealer,
                               dealee + addend.dealee);
    }
}
