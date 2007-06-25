package ao.holdem.def.state.domain;

import ao.holdem.game.impl.HandState;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 *
 */
public class Domain
{
    //--------------------------------------------------------------------
    private final BetsToCall     BETS;
    private final BettingRound   ROUND;
    private final DealerDistance DEALER_DISTANCE;
    private final Opposition     OPPOSITION;


    //--------------------------------------------------------------------
    public Domain(HandState handState, int awayFromDealer)
    {
        this(handState.domainBets(awayFromDealer),
             handState.domainBettingRound(),
             handState.domainPosition(awayFromDealer),
             handState.domainOpposition());
    }

    private Domain(BetsToCall     bets,
                   BettingRound   round,
                   DealerDistance dealerDistance,
                   Opposition     opposition)
    {
        BETS            = bets;
        ROUND           = round;
        DEALER_DISTANCE = dealerDistance;
        OPPOSITION      = opposition;
    }


    //--------------------------------------------------------------------
    public BetsToCall bets()
    {
        return BETS;
    }

    public BettingRound round()
    {
        return ROUND;
    }

    public DealerDistance dealerDistance()
    {
        return DEALER_DISTANCE;
    }

    public Opposition opposition()
    {
        return OPPOSITION;
    }


    //--------------------------------------------------------------------
    public static void writeTabDelimitedHeader(OutputStream to)
    {
        new PrintStream(to).print("Bets to Call\t"    +
                                  "Betting Round\t"   +
                                  "Dealer Distance\t" +
                                  "Num Opponents");
    }

    public void writeTabDelimited(OutputStream to)
    {
        new PrintStream(to).print(BETS            + "\t" +
                                  ROUND           + "\t" +
                                  DEALER_DISTANCE + "\t" +
                                  OPPOSITION);
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "<" + BETS            + "," +
                     ROUND           + "," +
                     DEALER_DISTANCE + "," +
                     OPPOSITION      + ">";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Domain domain = (Domain) o;
        return BETS            == domain.BETS &&
               DEALER_DISTANCE == domain.DEALER_DISTANCE &&
               OPPOSITION      == domain.OPPOSITION &&
               ROUND           == domain.ROUND;
    }

    @Override
    public int hashCode()
    {
        int result;
        result = BETS.hashCode();
        result = 31 * result + ROUND.hashCode();
        result = 31 * result + DEALER_DISTANCE.hashCode();
        result = 31 * result + OPPOSITION.hashCode();
        return result;
    }
}
