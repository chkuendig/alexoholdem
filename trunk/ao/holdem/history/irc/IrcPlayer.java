package ao.holdem.history.irc;

import ao.holdem.def.model.card.Card;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.state.env.TakenAction;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PLAYER INFORMATION (in pdb.* files)
    -----------------------------------
    player             #play prflop    turn         bankroll    winnings
              timestamp    pos   flop       river          action     cards
    Marzon    766303976  8  1 Bc  bc    kc    kf      12653  300    0

     PDB format
     ==========
     column 1        player nickname
     column 2        timestamp of this hand (see HDB)
     column 3        number of player dealt cards
     column 4        position of player (starting at 1, in order of cards received)
     column 5        betting action preflop (see below)
     column 6        betting action on flop (see below)
     column 7        betting action on turn (see below)
     column 8        betting action on river (see below)
     column 9        player's bankroll at start of hand
     column 10       total action of player during hand
     column 11       amount of pot won by player
     column 12+      pocket cards of player (if revealed at showdown)
 */
public class IrcPlayer
{
    //--------------------------------------------------------------------
    // Marzon    766303976  8  1 Bc  bc    kc    kf      12653  300    0
    private final static Pattern pat =
            Pattern.compile("([^\\s]+)\\s+" +
                            "(\\d+)\\s+" +
                            "(\\d+)\\s+" +
                            "(\\d+)\\s+" +
                            "(\\S+)\\s+" +
                            "(\\S+)\\s+" +
                            "(\\S+)\\s+" +
                            "(\\S+)\\s+" +
                            "(\\d+)\\s+" +
                            "(\\d+)\\s+" +
                            "(\\d+)\\s+" +
                            "(.*)");

    //--------------------------------------------------------------------
    private final String name;
    private final long   timestamp;
    private final int    numPlayers;
    private final int    position;

    private final TakenAction[] preflop;
    private final TakenAction[] onFlop;
    private final TakenAction[] onTurn;
    private final TakenAction[] onRiver;

    private final int  startingBankroll;
    private final int  totalAction;
    private final int  potWon;
    private final Hole revealedHole;


    //--------------------------------------------------------------------
    public IrcPlayer(String line)
    {
        Matcher m = pat.matcher(line);
        if (! m.matches())
        {
            throw new Error("IrcPlayer can't match: " + line);
        }

        name       = m.group(1);
        timestamp  = Long.parseLong(   m.group(2) );
        numPlayers = Integer.parseInt( m.group(3) );
        position   = Integer.parseInt( m.group(4) );

        preflop = parseActions( m.group(5) );
        onFlop  = parseActions( m.group(6) );
        onTurn  = parseActions( m.group(7) );
        onRiver = parseActions( m.group(8) );

        startingBankroll = Integer.parseInt( m.group(9)  );
        totalAction      = Integer.parseInt( m.group(10) );
        potWon           = Integer.parseInt( m.group(11) );

        revealedHole = parseHole( m.group(12) );
    }


    //--------------------------------------------------------------------
    private Hole parseHole(String holeString)
    {
        if (holeString == null ||
                holeString.length() == 0) return new Hole();

        String holes[] = holeString.split("\\s+");
        return new Hole(Card.valueOfCard(holes[0]),
                        Card.valueOfCard(holes[1]));
    }


    //--------------------------------------------------------------------
    /* The betting action is encoded with a single character for each action:
         -       no action; player is no longer contesting pot
         B       blind bet
         f       fold
         k       check
         b       bet
         c       call
         r       raise
         A       all-in
         Q       quits game
         K       kicked from game
     */
    private TakenAction[] parseActions(
            String actionString)
    {
        TakenAction[] actions =
                new TakenAction[ actionString.length() ];

        int nextIndex = 0;
        for (char actionChar : actionString.toCharArray())
        {
            switch (actionChar)
            {
                case '-':
                case 'B':
                    break;

                case 'f':
                case 'Q':
                case 'K':
                    actions[ nextIndex++ ] = TakenAction.FOLD;
                    break;

                case 'k':
                    actions[ nextIndex++ ] = TakenAction.CHECK;
                    break;

                case 'c':
                    actions[ nextIndex++ ] = TakenAction.CALL;
                    break;

                case 'b':
                case 'r':
                case 'A':
                    actions[ nextIndex++ ] = TakenAction.RAISE;
                    break;

                default:
                    throw new Error(
                            "unrecognized action: '" + actionChar + "'");
            }
        }

        return Arrays.copyOf(actions, nextIndex);
    }


    //--------------------------------------------------------------------
    public String name()
    {
        return name;
    }
    public long timestamp()
    {
        return timestamp;
    }
    public int numPlayers()
    {
        return numPlayers;
    }
    public int position()
    {
        return position;
    }

    public TakenAction[] preflop()
    {
        return preflop;
    }
    public TakenAction[] onFlop()
    {
        return onFlop;
    }
    public TakenAction[] onTurn()
    {
        return onTurn;
    }
    public TakenAction[] onRiver()
    {
        return onRiver;
    }

    public int startingBankroll()
    {
        return startingBankroll;
    }
    public int totalAction()
    {
        return totalAction;
    }
    public int potWon()
    {
        return potWon;
    }
    public Hole holes()
    {
        return revealedHole;
    }
}
