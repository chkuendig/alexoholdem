package ao.holdem.abs.calc;


import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.abs.odds.agglom.OddFinder;
import ao.holdem.abs.odds.Odds;
import ao.holdem.abs.odds.agglom.impl.ApproximateOddFinder;
import ao.holdem.engine.eval.HandRank;
import ao.holdem.abs.odds.eval.eval7.Eval7FastLookup;
import ao.holdem.abs.odds.eval.eval7.Eval7Faster;
import ao.holdem.engine.eval.EvalBy5;
import ao.util.data.Arrs;
import ao.util.math.rand.Rand;
import ao.util.math.stats.Combiner;
import org.apache.log4j.Logger;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.BitSet;


/**
 *
 */
public class Main
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(Main.class);


    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        // configure log4j logging
//        BasicConfigurator.configure();

        // throw off number sequence
        Rand.nextInt();
//        Rand.nextDouble();

//        Combiner<Integer> c =
//                new Combiner<Integer>(
//                        new Integer[]{0, 1, 2, 3, 4, 5, 6},
//                        5);
//        while (c.hasMoreElements())
//        {
//            System.out.println(Arrays.toString(c.nextElement()));
//        }

        for (int i = 0; i < 1; i++)
        {
//            runHoldemGame();
//            runNetCode();
            runPoker();
        }
    }


    //----------------------------------------------------------
    public static void runPoker()
    {
//        doRun52c7();
//        doRunHandsOf5();
//        doRunHandsOf6();
//        doRunHandsOf7();
        doRunOdds();
    }
    public static void doRunOdds()
    {
//        PrintStream output;
//        try
//        {
//            File outFile = new File("9_player_river_approx.txt");
//            outFile.createNewFile();
//            output = new PrintStream(
//                        new BufferedOutputStream(
//                            new FileOutputStream(outFile)),
//                        false);
//        }
//        catch (IOException e)
//        {
//            throw new Error( e );
//        }
//
//        doRunOddsWith("600\t500", new ApproximateOddFinder(), output);
//        for (int flops = 1; flops < 2; flops += 50)
//        {
//            for (long holes = 1; holes < 10000; holes += 50)
//            {
//                String prefix = flops + "\t" + holes;
//                OddFinder f =
//                        new ApproximateOddFinder(
//                                flops, holes);
//
//                if (! doRunOddsWith(prefix, f, output)) break;
//            }
//            System.out.println();
//        }


        Community c  = new Community();
        OddFinder fa = new ApproximateOddFinder();
//        OddFinder fg = new PreciseHeadsUpOdds();
        for (int opps = 1; opps <= 1; opps++)
        {
            for (Card[] holes : new Combiner<>(Card.values(), 2))
            {
                Odds approxOdds =
                        fa.compute(Hole.valueOf(holes[0], holes[1]), c, opps);
//                Odds exactOdds =
//                        fg.compute(Hole.valueOf(holes[0], holes[1]), c, opps);

                LOG.info("approxLookup[" + opps + "]" +
                              "[" + holes[0].ordinal() + "]" +
                              "[" + holes[1].ordinal() + "]\t" + approxOdds);
//                System.out.println(
//                        "exactLookup[" + opps + "]" +
//                              "[" + holes[0].ordinal() + "]" +
//                              "[" + holes[1].ordinal() + "]\t" + exactOdds);
//                System.out.println(
//                        "compact[" + opps + "]" +
//                              "[" + holes[1].ordinal() + "]" +
//                              "[" + holes[0].ordinal() + "] = " + odds + ";");
            }
        }

//        for (Card.Suit suitA : Card.Suit.values())
//        {
//            for (Card.Suit suitB : Card.Suit.values())
//            {
//                Hole twoThree =
//                        new Hole(Card.valueOf(Card.Rank.TWO,   suitA),
//                                 Card.valueOf(Card.Rank.THREE, suitB));
//
//                System.out.println(
//                        twoThree + "\t" +
//                        f.compute(twoThree, c, 1));
//            }
//        }


//        for (Card.Rank rank : Card.Rank.values())
//        {
//            Hole pocketPair =
//                    new Hole(Card.valueOf(rank, Card.Suit.DIAMONDS),
//                             Card.valueOf(rank, Card.Suit.CLUBS));
//            Community c = new Community();
//
//            System.out.println(
//                    pocketPair + "\t" +
//                    f.compute(pocketPair, c, 9));
//        }
//        for (Card.Rank ranks[] :
//                new Combiner<Card.Rank>(Card.Rank.values(), 2))
//        {
//            Hole suited = new Hole(Card.valueOf(ranks[0], Card.Suit.CLUBS),
//                                   Card.valueOf(ranks[1], Card.Suit.CLUBS));
//            Hole unsuited = new Hole(Card.valueOf(ranks[0], Card.Suit.CLUBS),
//                                     Card.valueOf(ranks[1], Card.Suit.DIAMONDS));
//
//            System.out.println(
//                "suited\t" + suited + "\t" +
//                f.compute(suited, c, 10));
//            System.out.println(
//                "unsuited\t" + unsuited + "\t" +
//                f.compute(unsuited, c, 10));
//        }
    }
    public static boolean doRunOddsWith(
            String      prefix,
            OddFinder   f,
            PrintStream outputTo)
    {
        Community c = new Community(Card.TWO_OF_CLUBS,
                                    Card.SEVEN_OF_HEARTS,
                                    Card.TWO_OF_HEARTS)
                            .addTurn(Card.JACK_OF_HEARTS)
                            .addRiver(Card.NINE_OF_SPADES);

//        for (Card.Rank rank : Card.Rank.values())
//        {
//            Hole pocketPair =
//                    new Hole(Card.valueOf(rank, Card.Suit.DIAMONDS),
//                             Card.valueOf(rank, Card.Suit.CLUBS));
            Hole pocket = Hole.valueOf(
                                Card.SIX_OF_SPADES,
                                Card.SEVEN_OF_DIAMONDS);


            long before = System.currentTimeMillis();
            Odds odds   = f.compute(pocket, c, 8);
            long delta  = (System.currentTimeMillis() - before);
            
            outputTo.println(
                    prefix + "\t" +
                    pocket + "\t" +
                    odds   + "\t" +
                    delta);

            if (delta > 500) return false;
//        }
        outputTo.flush();
        System.out.print(".");
        return true;
    }


    public static void doRun52c7()
    {
        Combiner<Card> permuter =
                new Combiner<>(Card.values(), 7);
        BitSet mapped = new BitSet();

        while (permuter.hasMoreElements())
        {
            long mask = 0;
            for (Card card : permuter.nextElement())
            {
                mask |= (1L << card.ordinal());
            }

            int index = Eval7FastLookup.index52c7(mask);
            if (index >= 133784560)
            {
                LOG.error("OUT OF BOUNDS " + mask + " with " + index);
            }
            if (mapped.get(index))
            {
                LOG.error("COLLISION AT " + mask + " for " + index);
            }
            mapped.set(index);
        }
    }

    public static void doRunHandsOf7()
    {
//        Combiner<Card> combiner =
//                new Combiner<Card>(Card.values(), 7);

        long total = 0;
        long start = System.currentTimeMillis();
        int  count = 0;

        Card cards[] = Card.values();
        int exactFrequency[] = new int[ 7462 ];
        int frequency[] = new int[ HandRank.values().length ];
        for (int c0 = 1; c0 < 53; c0++) {
            Card card0 = cards[ c0 - 1 ];
            for (int c1 = c0 + 1; c1 < 53; c1++) {
                Card card1 = cards[ c1 - 1 ];
                for (int c2 = c1 + 1; c2 < 53; c2++) {
                    Card card2 = cards[ c2 - 1 ];
                    for (int c3 = c2 + 1; c3 < 53; c3++) {
                        Card card3 = cards[ c3 - 1 ];
                        for (int c4 = c3 + 1; c4 < 53; c4++) {
                            Card card4 = cards[ c4 - 1 ];
                            for (int c5 = c4 + 1; c5 < 53; c5++) {
                                Card card5 = cards[ c5 - 1 ];
                                for (int c6 = c5 + 1; c6 < 53; c6++) {
                                    Card card6 = cards[ c6 - 1 ];
//        while (combiner.hasMoreElements())
//        {
        {
            if (count % 10000000 == 0)
            {
                long delta = (System.currentTimeMillis() - start);
                System.out.println();
                System.out.println("took: " + delta);

                total += delta;
                start  = System.currentTimeMillis();
            }
            if (count++ % 1000000  == 0) System.out.print(".");

//            {
//                Permuter<Integer> perm =
//                        new Permuter<Integer>(
//                                new Integer[]{c0, c1, c2, c3, c4, c5, c6});
//
//                int fValue = Eval7Faster.valueOf(c0, c1, c2, c3, c4, c5, c6);
//                while (perm.hasMoreElements())
//                {
//                    Integer onePerm[] = perm.nextElement();
//
//                    if (fValue != Eval7Faster.valueOf(
//                            onePerm[0], onePerm[1], onePerm[2],
//                            onePerm[3], onePerm[4], onePerm[5], onePerm[6]))
//                    {
//                        System.out.println("### WTF?!?!");
//                    }
//                }
//            }

//            Card cards[] = combiner.nextElement();
            final short value = //EvalSlow.valueOf( cards );
//            Eval7Fast.valueOf(card0, card1, card2, card3, card4, card5, card6);
//            Eval7Faster.valueOf(c0, c1, c2, c3, c4, c5, c6);
//            Eval7Faster.valueOf(cards);
//            Eval7Fast.valueOf(combiner.nextElement());
            Eval7Faster.valueOf(card0, card1, card2, card3, card4, card5, card6);
//            EvalSlow.valueOf(card0, card1, card2, card3, card4, card5, card6);
//            new Hand(card0, card1, card2, card3, card4, card5, card6).value();

//            FastCombiner<Card> c =
//                    new FastCombiner<Card>(
//                            new Card[]{card0, card1, card2, card3, card4, card5, card6});
//            c.combine(new FastCombiner.CombinationVisitor7<Card>() {
//                public void visit(Card ca0, Card ca1, Card ca2, Card ca3, Card ca4, Card ca5, Card ca6)
//                {
//                    if (value != EvalSlow.valueOf(ca0, ca1, ca2, ca3, ca4, ca5, ca6))
//                    {
//                        System.out.println("wtf!?!?");
//                    }
//                }
//            });

            if (value != EvalBy5.valueOf(
                    card0, card1, card2, card3, card4, card5, card6))
            {
                LOG.error("WTF!!!!!\t" + value + "\t" +
                        EvalBy5.valueOf(card0, card1, card2,
                                card3, card4, card5, card6));
            }
            frequency[ HandRank.fromValue(value).ordinal() ]++;
            exactFrequency[ value ]++;
        }
        }}}}}}}
//        }

        LOG.info("total: " + total);
        LOG.info(Arrays.toString(frequency));
        LOG.info(Arrs.join(exactFrequency, "\t"));
    }


    public static void doRunHandsOf6()
    {
        Combiner<Card> permuter =
                new Combiner<Card>(Card.values(), 6);

        long start = System.currentTimeMillis();

        int frequency[] = new int[ HandRank.values().length ];
        while (permuter.hasMoreElements())
        {
//            if (count   % 10000000 == 0) System.out.println();
//            if (count++ % 1000000  == 0) System.out.print(".");

            Card hand[] = permuter.nextElement();

//            int value = EvalSlow.valueOf(hand);
            short value = Eval7Faster.valueOf(
                            hand[0], hand[1], hand[2], hand[3], hand[4], hand[5]);
            if (value != EvalBy5.valueOf(hand))
            {
                LOG.info(Arrays.toString(hand) + "\t" +
                        value + "\t" + EvalBy5.valueOf(hand));
                Eval7Faster.valueOf(
                            hand[0], hand[1], hand[2], hand[3], hand[4], hand[5]);
            }

            frequency[ HandRank.fromValue(value).ordinal() ]++;
        }

        LOG.info(Arrays.toString(frequency));
        LOG.info("took: " + (System.currentTimeMillis() - start));
    }
    public static void doRunHandsOf5()
    {
        Combiner<Card> permuter =
                new Combiner<Card>(Card.values(), 5 );

        long start = System.currentTimeMillis();

        int count       = 0;
        int frequency[] = new int[ HandRank.values().length ];
        while (permuter.hasMoreElements())
        {
            if (count   % 10000000 == 0) System.out.println();
            if (count++ % 1000000  == 0) System.out.print(".");

            Card hand[] = permuter.nextElement();

            short value = EvalBy5.valueOf(hand);
//            int value = Eval7Faster.valueOf(
//                            hand[0], hand[1], hand[2], hand[3], hand[4]);
            frequency[ HandRank.fromValue(value).ordinal() ]++;
        }

        LOG.info(Arrays.toString(frequency));
        LOG.info("took: " + (System.currentTimeMillis() - start));
    }
}

