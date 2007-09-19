package ao.holdem.history.irc;

import ao.holdem.def.model.card.Card;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.state.domain.BettingRound;
import ao.holdem.def.state.env.RealAction;
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
     column 4        position of player (starting at 1,
                            in order of cards received)
     column 5        betting action preflop (see below)
     column 6        betting action on flop (see below)
     column 7        betting action on turn (see below)
     column 8        betting action on river (see below)
     column 9        player's bankroll at start of hand
     column 10       total action of player during hand
     column 11       amount of pot won by player
     column 12+      pocket cards of player (if revealed at showdown)
 */
public class IrcAction
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

    private       RealAction[] preflop;
    private final RealAction[] onFlop;
    private final RealAction[] onTurn;
    private final RealAction[] onRiver;

    private final int  startingBankroll;
    private final int  totalAction;
    private final int  potWon;
    private final Hole revealedHole;

    private final String asIs;


    //--------------------------------------------------------------------
    public IrcAction(String line)
    {
        Matcher m = pat.matcher(line);
        if (! m.matches())
        {
            throw new Error("IrcAction can't match: " + line);
        }

        name       = m.group(1);
        timestamp  = Long.parseLong(   m.group(2) );
        numPlayers = Integer.parseInt( m.group(3) );
        position   = Integer.parseInt( m.group(4) );

        preflop = parseActions( m.group(5), false );
        onFlop  = parseActions( m.group(6),
                                hasFolded(preflop) );
        onTurn  = parseActions( m.group(7),
                                hasFolded(preflop, onFlop) );
        onRiver = parseActions( m.group(8),
                                hasFolded(preflop, onFlop, onTurn) );

        startingBankroll = Integer.parseInt( m.group(9)  );
        totalAction      = Integer.parseInt( m.group(10) );
        potWon           = Integer.parseInt( m.group(11) );

        revealedHole = parseHole( m.group(12) );
        asIs = line;
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
    private RealAction[] parseActions(
            String actionString, boolean hasFolded)
    {
        if (hasFolded) return new RealAction[0];

        RealAction[] actions =
                new RealAction[ actionString.length() ];


        int nextIndex = 0;
        char_loop:
        for (char actionChar : actionString.toCharArray())
        {
            switch (actionChar)
            {
                case '-': // don't do anything
                    break;

                case 'B':
                    actions[ nextIndex++ ] = RealAction.BIG_BLIND;
                    break;

                case 'A': // indicates all-in, comes up
                          // as rA, cA, bA, BA 
                    actions[ nextIndex - 1 ] =
                            actions[ nextIndex - 1 ].toAllIn();
                    break;

                case 'f':
                    actions[ nextIndex++ ] = RealAction.FOLD;
                    break char_loop;

                case 'Q':
                case 'K':
                    actions[ nextIndex++ ] = RealAction.QUIT;
                    break char_loop;

                case 'k':
                    actions[ nextIndex++ ] = RealAction.CHECK;
                    break;

                case 'c':
                    actions[ nextIndex++ ] = RealAction.CALL;
                    break;

                case 'b':
                    actions[ nextIndex++ ] = RealAction.BET;
                    break;

                case 'r':
                    actions[ nextIndex++ ] = RealAction.RAISE;
                    break;

                default:
                    throw new Error(
                            "unrecognized action: '" + actionChar + "'");
            }
        }

        return Arrays.copyOf(actions, nextIndex);
    }

    private boolean hasFolded(RealAction[] ... actions)
    {
        for (RealAction[] actionSet : actions)
        {
            for (RealAction act : actionSet)
            {
                if (act.toTakenAction() == TakenAction.FOLD)
                {
                    return true;
                }
            }
        }
        return false;
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

    //--------------------------------------------------------------------
    public RealAction[] preFlop()
    {
        return preflop;
    }
    public RealAction[] onFlop()
    {
        return onFlop;
    }
    public RealAction[] onTurn()
    {
        return onTurn;
    }
    public RealAction[] onRiver()
    {
        return onRiver;
    }

    public RealAction[] action(BettingRound during)
    {
        switch (during)
        {
            case PREFLOP: return preFlop();
            case FLOP:    return onFlop();
            case TURN:    return onTurn();
            case RIVER:   return onRiver();
        }
        return null;
    }


    //--------------------------------------------------------------------
    public void removeBlind()
    {
        assert preflop[0].isBlind();

        RealAction newPreflop[] = new RealAction[preflop.length - 1];
        System.arraycopy(preflop, 1, newPreflop, 0, newPreflop.length);
        preflop = newPreflop;
    }


    //--------------------------------------------------------------------
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


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
//        return  name     + "\t" +
//                position + "\t" +
//                Arrays.toString(preflop) + "\t" +
//                Arrays.toString(onFlop)  + "\t" +
//                Arrays.toString(onTurn)  + "\t" +
//                Arrays.toString(onRiver) + "\t" +
//                totalAction + "\t" +
//                potWon      + "\t" +
//                revealedHole;
        return asIs;
    }
}
