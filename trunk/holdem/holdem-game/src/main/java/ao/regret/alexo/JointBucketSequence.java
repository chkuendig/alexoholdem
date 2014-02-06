package ao.regret.alexo;

import ao.simple.alexo.card.AlexoCard;
import ao.simple.alexo.card.AlexoCardSequence;
import ao.simple.alexo.state.AlexoRound;

/**
 * 
 */
public class JointBucketSequence
{
    //--------------------------------------------------------------------
    private static final AlexoBucket ROOT =
            new AlexoSequencer().root();


    //--------------------------------------------------------------------
    private final AlexoCardSequence CARDS;


    //--------------------------------------------------------------------
    public JointBucketSequence()
    {
        this( new AlexoCardSequence() );
    }

    public JointBucketSequence(AlexoCardSequence cards)
    {
        CARDS = cards;
    }


    //--------------------------------------------------------------------
    public AlexoBucket bucket(
            boolean    firstToAct,
            AlexoRound round)
    {
        AlexoCard hole = CARDS.hole(firstToAct);

        for (AlexoBucket holeBucket : ROOT.nextBuckets())
        {
            if (! holeBucket.holeEquals(hole)) continue;
            if (round == AlexoRound.PREFLOP)
            {
                return holeBucket;
            }

            for (AlexoBucket flopBucket : holeBucket.nextBuckets())
            {
                if (! flopBucket.flopEquals(
                        CARDS.community().flop() )) continue;
                if (round == AlexoRound.FLOP)
                {
                    return flopBucket;
                }

                for (AlexoBucket turnBucket : flopBucket.nextBuckets())
                {
                    if (turnBucket.turnEquals(
                            CARDS.community().turn()))
                    {
                        return turnBucket;
                    }
                }
            }
        }
        return null;
    }
}
