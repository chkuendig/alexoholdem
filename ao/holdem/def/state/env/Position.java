package ao.holdem.def.state.env;

/**
 * preflop, the first to act is the guy to the left of the big blind,
 *  meaning the last to act is the big blind.
 * on all other rounds, betting starts with the small blind, and the
 *  dealer is last to act.
 *
 * when there are just 2 people playing at a table,  the dealer is
 *  small blind, and the other player is big blind.
 * which means the dealer will act first on preflop, and last every
 *  other round.
 *
 * named positions:
 * firstposition/under the gun (left of BB)
 * cutoff (right of dealer)
 * dealer (obvious)
 * small blind
 * big blind
 *
 * there is also the grouped position names:
 *  in a 10 player game early position is the two players to the
 *      left of the big blind.
 *  middle position is the 3 positions to the left of those
 *  late position is the dealer and the two to his right 
 */
public class Position
{
    //--------------------------------------------------------------------
    private final int     POSITION;
    private final int     NUM_PLAYERS;
    private final boolean PRE_FLOP;


    //--------------------------------------------------------------------
    public Position(int     awayFromFirstToAct,
                    int     numPlayers,
                    boolean isPreFlop)
    {
        NUM_PLAYERS = numPlayers;
        POSITION    = awayFromFirstToAct;
        PRE_FLOP    = isPreFlop;
    }


    //--------------------------------------------------------------------
    public int awayFromFirstToAct()
    {
        return POSITION;
    }


    //--------------------------------------------------------------------
    public boolean isSecondToAct()
    {
        return amAtPosition(1);
    }

    public boolean isLastToAct()
    {
        return amAtPosition(-1);
    }


    //--------------------------------------------------------------------
    public boolean isDealer()
    {
        return amAt(0, -1, -3, -1);
    }

    public boolean isSmallBlind()
    {
        return amAt(0, -1, -2, 0);
    }

    public boolean isBigBlind()
    {
        return amAt(-1, 0, -1, 1);
    }

//    public boolean isNextToAct()
//    {
//        return amAtPosition(0);
//    }

    public boolean isUnderTheGun()
    {
        return amAt(420, 420, 0, 2);
    }

    // right of dealer
    public boolean isCutoff()
    {
        return amAt(420, 420, -4, -2);
    }


    //--------------------------------------------------------------------
    private boolean amAt(int headsUpPreFlop, int headsUpPostFlop,
                         int    teamPreFlop, int    teamPostFlop)
    {
        return isHeadsUp()
                ? PRE_FLOP
                   ? amAtPosition(headsUpPreFlop)
                   : amAtPosition(headsUpPostFlop)
                : PRE_FLOP
                   ? amAtPosition(teamPreFlop)
                   : amAtPosition(teamPostFlop);
    }


    // -ve number means from last to act
    private boolean amAtPosition(int fromFirstToAct)
    {
        return POSITION == toAbsPosition(fromFirstToAct);
    }

    private int toAbsPosition(int position)
    {
        return (position + ((position >= 0) ? 0 : NUM_PLAYERS));
    }

    private boolean isHeadsUp()
    {
        return NUM_PLAYERS == 2;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        StringBuilder asString = new StringBuilder();

        asString.append("position: ")
                .append(awayFromFirstToAct());

        if (isDealer())
        {
            asString.append(", DEALER");
        }
        else if (isSmallBlind())
        {
            asString.append(", SMALL BLIND");
        }
        else if (isBigBlind())
        {
            asString.append(", BIG BLIND");
        }

        if (isUnderTheGun())
        {
            asString.append(", UNDER THE GUN");
        }
//        if (isNextToAct())
//        {
//            asString.append(", NEXT TO ACT");
//        }

        return asString.toString();
    }
}
