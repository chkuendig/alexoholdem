package ao.bucket.index.iso_turn;

import ao.bucket.index.iso_cards.wild.suit.WildSuit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date: Aug 27, 2008
 * Time: 7:53:14 PM
 */
public class TurnCase
{
    //--------------------------------------------------------------------


    //--------------------------------------------------------------------
    private final WildSuit HOLE_A, HOLE_B;
    private final WildSuit FLOP_A, FLOP_B, FLOP_C;
    private final WildSuit TURN;

//    private final int TURN_SUIT_DUPES;


    //--------------------------------------------------------------------
    private static final Pattern PARSE = Pattern.compile(
            "\\[(.*), (.*)\\]\\[(.*), (.*), (.*)\\]\\[(.*)\\]");
    public static TurnCase valueOf(String asString)
    {
        Matcher m = PARSE.matcher( asString );
        if (! m.matches()) return null;

        return new TurnCase(WildSuit.valueOf(m.group(1)),
                            WildSuit.valueOf(m.group(2)),
                            WildSuit.valueOf(m.group(3)),
                            WildSuit.valueOf(m.group(4)),
                            WildSuit.valueOf(m.group(5)),
                            WildSuit.valueOf(m.group(6)));
    }

    public static final List<TurnCase> VALUES = new ArrayList<TurnCase>();
    static
    {
        try
        {
            BufferedReader reader =
                    new BufferedReader(new FileReader("cases.txt"));
            for (String line = reader.readLine(); line != null;
                        line = reader.readLine())
            {
                VALUES.add( valueOf(line) );
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    //--------------------------------------------------------------------
    public TurnCase(WildSuit holeA, WildSuit holeB,
                    WildSuit flopA, WildSuit flopB, WildSuit flopC,
                    WildSuit turn)
    {
        HOLE_A = holeA;
        HOLE_B = holeB;
        FLOP_A = flopA;
        FLOP_B = flopB;
        FLOP_C = flopC;
        TURN   = turn;

//        TURN_SUIT_DUPES = turnSuitDupes();
    }


    //--------------------------------------------------------------------
    public int subIndex()
    {
        int seenCount = 0;

        if (TURN == HOLE_A) seenCount++;
        if (TURN == HOLE_B) seenCount++;
        if (TURN == FLOP_A) seenCount++;
        if (TURN == FLOP_B) seenCount++;
        if (TURN == FLOP_C) seenCount++;

        return (13 - seenCount);
    }

    public int turnSuitDupes()
    {
        int seenCount = 0;

        if (TURN == HOLE_A) seenCount++;
        if (TURN == HOLE_B) seenCount++;
        if (TURN == FLOP_A) seenCount++;
        if (TURN == FLOP_B) seenCount++;
        if (TURN == FLOP_C) seenCount++;

        return seenCount;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return Arrays.toString(new WildSuit[]{HOLE_A, HOLE_B}) +
               Arrays.toString(new WildSuit[]{FLOP_A, FLOP_B, FLOP_C}) +
               "[" + TURN + "]";
    }


    //--------------------------------------------------------------------
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (getClass() != o.getClass()) return false;

        TurnCase turnCase = (TurnCase) o;

        return HOLE_A == turnCase.HOLE_A &&
               HOLE_B == turnCase.HOLE_B &&
               FLOP_A == turnCase.FLOP_A &&
               FLOP_B == turnCase.FLOP_B &&
               FLOP_C == turnCase.FLOP_C &&
               TURN == turnCase.TURN;
    }

    @Override
    public int hashCode()
    {
        int result = 0;
        result = 31 * result + HOLE_A.hashCode();
        result = 31 * result + HOLE_B.hashCode();
        result = 31 * result + FLOP_A.hashCode();
        result = 31 * result + FLOP_B.hashCode();
        result = 31 * result + FLOP_C.hashCode();
        result = 31 * result + TURN.hashCode();
        return result;
    }
}
