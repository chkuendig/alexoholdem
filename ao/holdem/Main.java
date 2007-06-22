package ao.holdem;


import ao.holdem.bots.RandomBot;
import ao.holdem.bots.SimpleBot;
import ao.holdem.def.bot.BotFactory;
import ao.holdem.def.bot.BotProvider;
import ao.holdem.def.model.card.Card;
import ao.holdem.def.model.card.eval7.FastEval7;
import ao.holdem.def.model.card.eval7.UniqueIndex;
import ao.holdem.def.model.cards.Hand;
import ao.holdem.def.state.display.OutcomeStepRender;
import ao.holdem.def.state.domain.BettingRound;
import ao.holdem.def.state.domain.Decider;
import ao.holdem.def.state.domain.Opposition;
import ao.holdem.game.Holdem;
import ao.holdem.game.Outcome;
import ao.holdem.game.impl.HoldemImpl;
import ao.holdem.net.OverTheWireState;
import ao.util.stats.Combiner;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;


/**
 *
 */
public class Main
{
    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        // configure log4j logging
        BasicConfigurator.configure();

        
        // throw off number sequence
//        Rand.nextInt();
//        Rand.nextDouble();

        for (int i = 0; i < 100; i++)
        {
            runHoldemGame();
//            runNetCode();
//            runPoker();
        }
    }


    //--------------------------------------------------------------------
    public static void runNetCode()
    {
        try
        {
            doRunNetCode();
        }
        catch (Exception e)
        {
            throw new Error( e );
        }
    }

    public static void doRunNetCode() throws Exception
    {
        DatagramSocket readSocket  = new DatagramSocket(9978);
        DatagramSocket writeSocket = new DatagramSocket(9979);

        System.out.println("sending response.");
        send( writeSocket );

//        System.out.println("recieved: " + recieveString(readSocket));
        System.out.println("recieved: " +
                           new OverTheWireState(recieveString(readSocket)));

        System.out.println("sending response.");
        send( writeSocket );

//        System.out.println("recieved: " + recieveString(readSocket));
        System.out.println("recieved: " +
                           new OverTheWireState(recieveString(readSocket)));

        System.out.println("sending response.");
        send( writeSocket );

        readSocket.close();
        writeSocket.close();
    }

    private static String recieveString(
            DatagramSocket from) throws IOException
    {
        byte readBuffer[] = new byte[2048];
        DatagramPacket packet =
                new DatagramPacket(readBuffer, readBuffer.length);
        from.receive( packet );
        return new String( packet.getData() ).trim();
    }

    private static void send(
            DatagramSocket to) throws IOException
    {
        byte[] buf = "Hello World".getBytes();

//        to.get
        InetAddress    address = InetAddress.getByName("127.0.0.1");
        DatagramPacket packet  =
                new DatagramPacket(buf, buf.length,
                                   address, 4445);
        to.send(packet);
    }


    //--------------------------------------------------------------------
    public static void runHoldemGame()
    {
        BotProvider provider = new BotProvider();

        provider.add(
                BotFactory.Impl.newInstance(
                        EnumSet.allOf(Decider.class),
                        EnumSet.allOf(Opposition.class),
                        EnumSet.allOf(BettingRound.class),
                        RandomBot.class));
        provider.add(
                BotFactory.Impl.newInstance(
                        EnumSet.allOf(Decider.class),
                        EnumSet.allOf(Opposition.class),
                        EnumSet.allOf(BettingRound.class),
                        SimpleBot.class));

        Holdem holdem = new HoldemImpl();
        holdem.configure(4, provider);

        for (Outcome.Step step : holdem.play().log())
        {
            OutcomeStepRender.display( step );
        }
    }


    //----------------------------------------------------------
    public static void runPoker()
    {
        for (int i = 0; i < 10; i++)
        {
//            doRun52c7();
//            doRunHandsOf5();
            doRunHandsOf7();
        }
    }
    public static void doRun52c7()
    {
        Combiner<Card> permuter =
                new Combiner<Card>(Card.values(), 7);
        BitSet mapped = new BitSet();

        while (permuter.hasMoreElements())
        {
            long mask = 0;
            for (Card card : permuter.nextElement())
            {
                mask |= (1L << card.ordinal());
            }

            int index = UniqueIndex.index52c7(mask);
            if (index >= 133784560)
            {
                System.out.println("OUT OF BOUNDS " + mask + " with " + index);
            }
            if (mapped.get(index))
            {
                System.out.println("COLLISION AT " + mask + " for " + index);
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

        int frequency[] = new int[ Hand.HandRank.values().length ];
        for (int c0 = 1; c0 < 53; c0++) {
            for (int c1 = c0 + 1; c1 < 53; c1++) {
                for (int c2 = c1 + 1; c2 < 53; c2++) {
                    for (int c3 = c2 + 1; c3 < 53; c3++) {
                        for (int c4 = c3 + 1; c4 < 53; c4++) {
                            for (int c5 = c4 + 1; c5 < 53; c5++) {
                                for (int c6 = c5 + 1; c6 < 53; c6++) {
//        while (combiner.hasMoreElements())
//        {
            if (count % 10000000 == 0)
            {
                long delta = (System.currentTimeMillis() - start);
                System.out.println();
                System.out.println("took: " + delta);

                total += delta;
                start  = System.currentTimeMillis();
            }
            if (count++ % 1000000  == 0) System.out.print(".");

            short value =
            FastEval7.handValue(c0, c1, c2, c3, c4, c5, c6);
//            FastEval7.handValue(combiner.nextElement());
//            Lookup7.handValue(combiner.nextElement());
//            Lookup7.handValue(cards[c1], cards[c2], cards[c3], cards[c4],
//                              cards[c5], cards[c6], cards[c7]);
//            new Hand( combiner.nextElement() ).value();

            frequency[ Hand.HandRank.fromValue(value).ordinal() ]++;
        }}}}}}}
//        }
        System.out.println("total: " + total);
        System.out.println(Arrays.toString(frequency));
    }
    public static void doRunHandsOf5()
    {
        Combiner<Card> permuter =
                new Combiner<Card>(Card.values(), 5 );

        long start = System.currentTimeMillis();

        int count       = 0;
        int frequency[] = new int[ Hand.HandRank.values().length ];
        while (permuter.hasMoreElements())
        {
            if (count   % 10000000 == 0) System.out.println();
            if (count++ % 1000000  == 0) System.out.print(".");

            int value = Card.handValue(permuter.nextElement());
            frequency[ Hand.HandRank.fromValue(value).ordinal() ]++;
        }

        System.out.println(Arrays.toString(frequency));
        System.out.println("took: " + (System.currentTimeMillis() - start));

//        System.out.println(Card.handValue(
//                Card.ACE_OF_HEARTS,
//                Card.KING_OF_HEARTS,
//                Card.QUEEN_OF_HEARTS,
//                Card.TEN_OF_HEARTS,
//                Card.SEVEN_OF_HEARTS));
//
//        System.out.println(Card.handValue(
//                Card.ACE_OF_HEARTS,
//                Card.KING_OF_HEARTS,
//                Card.QUEEN_OF_HEARTS,
//                Card.TEN_OF_HEARTS,
//                Card.SIX_OF_HEARTS));
//
//        Hand a = new Hand(Card.FIVE_OF_HEARTS,
//                          Card.SIX_OF_HEARTS,
//                          Card.ACE_OF_HEARTS,
//                          Card.KING_OF_HEARTS,
//                          Card.QUEEN_OF_HEARTS,
//                          Card.TEN_OF_HEARTS,
//                          Card.TWO_OF_HEARTS);
//        Hand b = new Hand(Card.SEVEN_OF_HEARTS,
//                          Card.NINE_OF_SPADES,
//                          Card.ACE_OF_HEARTS,
//                          Card.KING_OF_HEARTS,
//                          Card.QUEEN_OF_HEARTS,
//                          Card.TEN_OF_HEARTS,
//                          Card.TWO_OF_HEARTS);
//        System.out.println(a.compareTo( b ));
    }
}


