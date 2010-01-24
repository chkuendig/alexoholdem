package ao.irc;

import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * timestamp      hand #     #players/starting potsize
             dealer    #play flop    turn    river  showdn     board
    766303976   1   455  8  6/600   6/1200  6/1800  3/2400  3s Jc Qd 5c Ah

    column 1        timestamp (supposed to be unique integer)
    column 2        game set # (incremented when column 3 resets)
    column 3        game # reported by dealer bot
    column 4        number of players dealt cards
    column 5        number of players who see the flop
    column 6        pot size at beginning of flop
    column 7        number of players who see the turn
    column 8        pot size at beginning of turn
    column 9        number of players who see the river
    column 10       pot size at beginning of river
    column 11       number of players at showdown
    column 12       pot size at showdown
    column 13+      cards on board (0, 3, 4 or 5)
 */
public class IrcHand
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
        Logger.getLogger(IrcHand.class);


    //--------------------------------------------------------------------
    // 766303976   1   455  8  6/600   6/1200  6/1800  3/2400  3s Jc Qd 5c Ah
    private final static Pattern pat =
            Pattern.compile(//"\\D*" +
                            "(\\d+)\\s+" +
                            "(\\d+)\\s+" +
                            "(\\d+)\\s+" +
                            "(\\d+)\\s+" +
                            "(\\d+)/"    +
                            "(\\d+)\\s+" +
                            "(\\d+)/"    +
                            "(\\d+)\\s+" +
                            "(\\d+)/"    +
                            "(\\d+)\\s+" +
                            "(\\d+)/"    +
                            "(\\d+)\\s*" +
                            "(.*)");

    public static IrcHand fromLine(String line)
    {
        try
        {
            return new IrcHand( line.trim() );
        }
        catch (Error e)
        {
            LOG.error("can't load from line: " + line, e);
            return null;
        }
    }


    //--------------------------------------------------------------------
    private final long timestamp;
    private final int  dealerSeat;
    private final int  handNumber;

    private final int  totalPlayers;

    private final int  flopPlayers;
    private final int  flopPot;
    private final int  turnPlayers;
    private final int  turnPot;
    private final int  riverPlayers;
    private final int  riverPot;
    private final int  showdownPlayers;
    private final int  showdownPot;

    private final Community community;


    //--------------------------------------------------------------------
    // 766303976   1   455  8  6/600   6/1200  6/1800  3/2400  3s Jc Qd 5c Ah
    private IrcHand(String line)
    {
        Matcher m = pat.matcher(line);
        if (! m.matches())
        {
            throw new Error("irc hand can't match: " + line);
        }

        timestamp  = Long.parseLong(  m.group(1));
        dealerSeat = Integer.parseInt(m.group(2));
        handNumber = Integer.parseInt(m.group(3));

        totalPlayers = Integer.parseInt(m.group(4));

        flopPlayers     = Integer.parseInt(m.group(5));
        flopPot         = Integer.parseInt(m.group(6));
        turnPlayers     = Integer.parseInt(m.group(7));
        turnPot         = Integer.parseInt(m.group(8));
        riverPlayers    = Integer.parseInt(m.group(9));
        riverPot        = Integer.parseInt(m.group(10));
        showdownPlayers = Integer.parseInt(m.group(11));
        showdownPot     = Integer.parseInt(m.group(12));

        String commString = m.group(13);
        community = parseCommunity(commString);
    }

    private Community parseCommunity(String comm)
    {
        if (comm == null || comm.length() == 0)
        {
            return new Community();
        }

        String cardNames[] = comm.split("\\s+");
        Card   cards[]     = new Card[ cardNames.length ]; 

        for (int i = 0; i < cardNames.length; i++)
        {
            cards[ i ] = Card.valueOfCard( cardNames[i] );
        }

        switch (cards.length)
        {
            case 3:
                return new Community(cards[0], cards[1], cards[2]);

            case 4:
                return new Community(cards[0], cards[1], cards[2],
                                     cards[3]);

            case 5:
                return new Community(cards[0], cards[1], cards[2],
                                     cards[3], cards[4]);
        }
        throw new Error("weird community cards: " + comm +
                        " as " + Arrays.toString(cards));
    }


    //--------------------------------------------------------------------
    public long timestamp()
    {
        return timestamp;
    }

    public int dealerSeat()
    {
        return dealerSeat;
    }

    public int handNumber()
    {
        return handNumber;
    }

    //--------------------------------------------------------------------
    public int numberOfPlayers()
    {
        return totalPlayers;
    }

    public int numberOfPlayersWhoSeeFlop()
    {
        return flopPlayers;
    }
    public int potSizeAtBeginningOfFlop()
    {
        return flopPot;
    }

    public int numberOfPlayersWhoSeeTurn()
    {
        return turnPlayers;
    }
    public int potSizeAtBeginningOfTurn()
    {
        return turnPot;
    }

    public int numberOfPlayersWhoSeeRiver()
    {
        return riverPlayers;
    }
    public int potSizeAtBeginningOfRiver()
    {
        return riverPot;
    }

    public int numberOfPlayersAtShowdown()
    {
        return showdownPlayers;
    }
    public int potSizeAtShowdown()
    {
        return showdownPot;
    }

    public Community community()
    {
        return community;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return  totalPlayers    + "\t" +
                flopPlayers     + "\t" +
                turnPlayers     + "\t" +
                riverPlayers    + "\t" +
                showdownPlayers + "\t" +
                community;
    }
}
