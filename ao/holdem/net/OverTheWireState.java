package ao.holdem.net;

/**
 *
 */
public class OverTheWireState
{
    //--------------------------------------------------------------------
    private final int    gameState;
    private final int    playersInTable;
    private final int    playersInHand;
    private final int    dealerSeat;
    private final int    smallBlindSeat;
    private final int    bigBlindSeat;
    private final int    potSize;
    private final int    bigBlindValue;
    private final String playerNames[];
    private final int    playerBets[];
    private final int    playerActions[];


    //--------------------------------------------------------------------
    public OverTheWireState(String fromDuaneFormat)
    {
        System.out.println("parsing: " + fromDuaneFormat);
        String chunks[] = fromDuaneFormat.split(";");

        gameState      = intAtEndOf( chunks[0] );
        playersInTable = intAtEndOf( chunks[1] );
        playersInHand  = intAtEndOf( chunks[2] );
        dealerSeat     = intAtEndOf( chunks[3] );
        smallBlindSeat = intAtEndOf( chunks[4] );
        bigBlindSeat   = intAtEndOf( chunks[5] );
        potSize        = intAtEndOf( chunks[6] );
        bigBlindValue  = intAtEndOf( chunks[7] );

        playerNames   = new String[ playersInTable ];
        playerBets    = new int[    playersInTable ];
        playerActions = new int[    playersInTable ];

        for (int i = 0; i < playersInTable; i++)
        {
            playerNames[i]   = stringAtEndOf( chunks[8  + 3*i] );
            playerActions[i] = intAtEndOf(    chunks[9  + 3*i] );
            playerBets[i]    = intAtEndOf(    chunks[10 + 3*i] );
        }
    }

    private String stringAtEndOf(String value)
    {
        return value.substring(
                value.indexOf('"') + 1,
                value.lastIndexOf('"'));

    }
    private int intAtEndOf(String value)
    {
        assert value.length() >= 2;

        char lastChar       = value.charAt( value.length() - 1 );
        char secondLastChar = value.charAt( value.length() - 2 );

        assert Character.isDigit(lastChar);
        if (Character.isDigit(secondLastChar))
        {
            return Integer.parseInt(
                    new String(new char[]{secondLastChar, lastChar}));
        }
        else
        {
            return Character.digit(lastChar, 10);
        }
    }


    //--------------------------------------------------------------------
    public Environment toEnvironment()
    {
        Player players[] = new Player[ playersInTable ];

        // hole do i get my Hole cards?
        // how do i get community cards?
        // how do i get # of bets remaining?

        return new Environment(null,
                               null,
                               players,
                               0,
                               0,
                               0,
                               0,
                               -1,
                               null,
                               null);
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        StringBuilder playersAsString = new StringBuilder();

        for (int i = 0; i < playersInTable; i++)
        {
            playersAsString
                    .append('[')
                    .append("seat: ")
                    .append(i)
                    .append(", name: ")
                    .append(playerNames[i])
                    .append(", action: ")
                    .append( playerActions[i] )
                    .append(", bet: ")
                    .append( playerBets[i] )
                    .append(']');
        }

        return "game state: "       + gameState      + ", " +
               "players in table: " + playersInTable + ", " +
               "players in hand: "  + playersInHand  + ", " +
               "dealer seat: "      + dealerSeat     + ", " +
               "small blind seat: " + smallBlindSeat + ", " +
               "big blind seat: "   + bigBlindSeat   + ", " +
               "pot size: "         + potSize        + ", " +
               "big blind value: "  + bigBlindValue  + ", " +
               playersAsString;
    }
}
