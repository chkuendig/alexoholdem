package ao.simple.rps;

import ao.util.math.rand.Rand;

import java.util.Arrays;

/**
 * User: alex
 * Date: 16-Apr-2009
 * Time: 7:08:26 PM
 *
 * Rock Paper Scissors doesn't work with CFR because the regret values
 *  for the opponent are not independant between the proponent's actions.
 * So within one trial, the potential choices' regret interferes. 
 *
 */
public class CfrPlayer implements Player
{
    //--------------------------------------------------------------------
    public static void main(String[] args) {
        CfrPlayer a = new CfrPlayer(1000);
        CfrPlayer b = new CfrPlayer(1000);

        System.out.println(a);
        System.out.println(b);

        Judge judge = new Judge();
        int   score = judge.tournament(a, b);

        System.out.println(
                score);
    }


    //--------------------------------------------------------------------
    private double regretA[] = new double[ 3 ];
    private double regretB[] = new double[ 3 ];


    //--------------------------------------------------------------------
    public CfrPlayer(int trials)
    {
        for (int i = 0; i < trials; i++)
        {
            cfrTrial();
        }
    }


    //--------------------------------------------------------------------
    private void cfrTrial()
    {
        double probA[] = probabilities(regretA);
        double aVals[] = new double[ 3 ];

        double expectation = 0;
        for (Hand h : Hand.VALUES)
        {
            double prob = probA[ h.ordinal() ];
            double aVal = cfrSubTrial(h, prob);

            aVals[ h.ordinal() ] = aVal;
            expectation += prob * aVal;
        }

        for (Hand h : Hand.VALUES)
        {
            double cRegret =
                    (aVals[ h.ordinal() ] - expectation);
            regretA[ h.ordinal() ] += cRegret;
        }
    }

    private double cfrSubTrial(Hand handA, double oppReach)
    {
        double probB[] = probabilities(regretB);
        double aVals[] = new double[ 3 ];

        double expectation = 0;
        for (Hand h : Hand.VALUES)
        {
            double prob = probB[ h.ordinal() ];
            double aVal = handA.vs( h ).value();

            aVals[ h.ordinal() ] = aVal;
            expectation += prob * aVal;
        }

        for (Hand h : Hand.VALUES)
        {
            double cRegret =
                    (aVals[ h.ordinal() ] - expectation)
                        * oppReach;

            regretB[ h.ordinal() ] -= cRegret;
        }

        return expectation;
    }


    //--------------------------------------------------------------------
    private double[] probabilities(double regret[])
    {
        double positiveRock     = Math.max(regret[ 0 ], 0);
        double positivePaper    = Math.max(regret[ 1 ], 0);
        double positiveScissors = Math.max(regret[ 2 ], 0);

        double totalPositive =
                positiveRock + positivePaper + positiveScissors;
        if (totalPositive == 0) {
            return new double[]{1.0/3, 1.0/3, 1.0/3};
        } else {
            return new double[]{
                    positiveRock     / totalPositive,
                    positivePaper    / totalPositive,
                    positiveScissors / totalPositive};
        }
    }


    //--------------------------------------------------------------------
    public Hand play() {
        double c = Rand.nextDouble();

        double prob[] = probabilities( regretA );

        if (c < prob[0]) {
            return Hand.ROCK;
        } else {
            c -= prob[0];

            if (c < prob[1]) {
                return Hand.PAPER;
            } else {
                return Hand.SCISSORS;
            }
        }
    }


    //--------------------------------------------------------------------
    @Override public String toString()
    {
        return Arrays.toString(regretA) + " :: " +
               Arrays.toString(probabilities(regretA)) + "\n\t" +
                Arrays.toString(regretB) + " :: " +
                Arrays.toString(probabilities(regretB));
    }
}
