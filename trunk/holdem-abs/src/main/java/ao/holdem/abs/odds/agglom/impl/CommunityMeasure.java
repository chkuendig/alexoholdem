package ao.holdem.abs.odds.agglom.impl;

import ao.holdem.engine.eval.EvalBy5;
import ao.holdem.engine.eval.HandStrength;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.util.math.stats.FastCombiner;

/**
 * Estimates how "hot" the community cards are,
 *  in the sense of AAK being hotter than 26J
 */
public class CommunityMeasure
{
    //--------------------------------------------------------------------
    private static final int
            COM_A  = 52 - 1, COM_B  = 52 - 2,
            COM_C  = 52 - 3, COM_D  = 52 - 4, COM_E  = 52 - 5;

    private static final
            CommunityMeasure instance = new CommunityMeasure();
    public static double measure(Community of)
    {
        return instance.heat( of );
    }


    //--------------------------------------------------------------------
    private Community prevCommunity;
    private double    prevHeat;


    //--------------------------------------------------------------------
    public synchronized double heat(Community of)
    {
        if (of == null || !of.hasFlop()) return -1;
        if (of.equals(prevCommunity)) return prevHeat; // cache

        final Card cards[] = Card.values();
        moveKnownCommunitiesToEnd(cards, of);

//        int shortcut     = Eval7Faster.shortcutFor(of.known());
        int unknownCount = 52 - of.knownCount();

//        TemprotureVisitor thermostat = new TemprotureVisitor(shortcut);
        TemprotureVisitor thermostat = new TemprotureVisitor(of.toArray());
        FastCombiner<Card> combin =
                new FastCombiner<Card>(cards, unknownCount);
        combin.combine(thermostat);

        prevCommunity = of;
        prevHeat      = thermostat.averageHeat();
        return thermostat.averageHeat();
    }


    //--------------------------------------------------------------------
    private static void moveKnownCommunitiesToEnd(
            Card cards[], Community community)
    {
        switch (community.knownCount())
        {
            case 5:
                swap(cards, community.river().ordinal(), COM_E);

            case 4:
                swap(cards, community.turn().ordinal(),  COM_D);

            case 3:
                swap(cards, community.flopC().ordinal(), COM_C);
                swap(cards, community.flopB().ordinal(), COM_B);
                swap(cards, community.flopA().ordinal(), COM_A);
        }
    }


    //--------------------------------------------------------------------
    private static void swap(Card cards[], int i, int j)
    {
        Card temp = cards[i];
        cards[i]  = cards[j];
        cards[j]  = temp;
    }


    //--------------------------------------------------------------------
    private static class TemprotureVisitor
            implements FastCombiner.CombinationVisitor2<Card>
    {
        private int    count;
        private double cumulativeHeat;
        private int    shortcut;
        private Card[] community;

        public TemprotureVisitor(int shortcut)
        {
            this.shortcut = shortcut;
        }
        public TemprotureVisitor(Card[] community)
        {
            this.community = community;
        }

        public void visit(Card cardA, Card cardB)
        {
//            short value = Eval7Faster.fastValueOf(
//                    shortcut, cardA, cardB);

            Card hand[] = new Card[ community.length + 2 ];
            System.arraycopy(community, 0, hand, 0, community.length);
            hand[ hand.length - 1 ] = cardA;
            hand[ hand.length - 2 ] = cardB;
            
            short value = EvalBy5.valueOf(hand);

            double heat = value / (double) HandStrength.HIGHEST;

            count++;
            cumulativeHeat += heat;
        }

        public double averageHeat()
        {
            return cumulativeHeat / count;
        }
    }
}
