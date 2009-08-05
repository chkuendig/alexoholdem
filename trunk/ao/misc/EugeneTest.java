package ao.misc;

import ao.holdem.model.card.Card;
import ao.holdem.model.card.Suit;
import ao.util.math.rand.Rand;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Date: Jan 20, 2009
 * Time: 1:42:02 AM
 */
public class EugeneTest
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
        Logger.getLogger(EugeneTest.class);


    //--------------------------------------------------------------------
    public static void main(String[] args)
    {

        for (int opps = 1; opps <= 12; opps++)
        {
            test(opps, 20, 100 * 1000);
        }
    }


    //--------------------------------------------------------------------
    public static void test(
            int opps,
            int trials,
            int repeat)
    {
        int total  = 0;

        for (int trial = 0; trial < trials; trial++)
        {
            List<Card> cards = new ArrayList<Card>(
                    Arrays.asList(Card.values()));

            Card holeA, holeB;
            List<Card> cardPrototype;
            do
            {
                cardPrototype = new ArrayList<Card>( cards );

                holeA = cardPrototype.remove(
                                    Rand.nextInt(cardPrototype.size()) );

                for (int person = 0; person < opps; person++)
                {
                    Card oppCard =
                            cardPrototype.remove(
                                    Rand.nextInt(cardPrototype.size()) );
                }

                holeB = cardPrototype.remove(
                                    Rand.nextInt(cardPrototype.size()) );
            }
            while (holeA.suit() != Suit.DIAMONDS ||
                   holeB.suit() != Suit.DIAMONDS);

            for (int person = 0; person < opps; person++)
            {
                Card oppCard =
                        cardPrototype.remove(
                                Rand.nextInt(cardPrototype.size()) );
            }

            int occured = 0;
            for (int i = 0; i < repeat; i++)
            {
                List<Card> cardPrototypeB =
                        new ArrayList<Card>( cardPrototype );

                Card chance = cardPrototypeB.remove(
                        Rand.nextInt(cardPrototypeB.size()) );

                if (chance.suit() == Suit.DIAMONDS)
                {
                    occured++;
                }
            }
            total += occured;

//            System.out.println(occured);
        }

        double average =
                ((double) total) / (trials * repeat);
        LOG.info(trials + "\t" + repeat + "\t" + opps + "\t" + average);
    }
}
