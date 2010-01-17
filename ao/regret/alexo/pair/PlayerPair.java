package ao.regret.alexo.pair;

import ao.regret.InfoNode;
import ao.regret.alexo.JointBucketSequence;
import ao.regret.alexo.node.BucketNode;
import ao.regret.alexo.node.PlayerNode;
import ao.regret.alexo.node.ProponentNode;
import ao.regret.alexo.node.TerminalNode;
import ao.simple.alexo.AlexoAction;

import java.util.EnumMap;
import java.util.Map;

/**
 *
 */
public class PlayerPair implements InfoPair
{
    //--------------------------------------------------------------------
    private final PlayerNode FIRST;
    private final PlayerNode LAST;


    //--------------------------------------------------------------------
    public PlayerPair(PlayerNode first,
                      PlayerNode last)
    {
        FIRST = first;
        LAST  = last;
    }


    //--------------------------------------------------------------------
    public double proponentProbability(AlexoAction ofAction)
    {
        return proponent().probabilityOf( ofAction );
    }

    public boolean proponentIsInformed()
    {
        return proponent().isInformed();
    }

    public boolean proponentIsFirst()
    {
        return FIRST instanceof ProponentNode;
    }

    private ProponentNode proponent()
    {
        return (ProponentNode)(
                    proponentIsFirst()
                    ? FIRST : LAST);
    }


    //--------------------------------------------------------------------
    public InfoPair child(AlexoAction action)
    {
        InfoNode firstChild = FIRST.child(action);
        InfoNode lastChild  = LAST.child( action);

        if (firstChild instanceof BucketNode)
        {
            return new BucketPair(((BucketNode) firstChild),
                                  ((BucketNode) lastChild));
        }
        else if (firstChild instanceof PlayerNode)
        {
            return new PlayerPair((PlayerNode) firstChild,
                                  (PlayerNode) lastChild);
        }
        else
        {
            return new TerminalPair((TerminalNode) firstChild,
                                    (TerminalNode) lastChild);
        }
    }


    //--------------------------------------------------------------------
//    public double approximate(
//            JointBucketSequence b,
//            double              pA,
//            double              pB)
//    {
//        return approximate(b, pA, pB, 0);
//    }
    public double approximate(
            JointBucketSequence b,
            double              pA,
            double              pB,
            double              aggression)
    {
        boolean       pFirst    = proponentIsFirst();
        ProponentNode proponent = (ProponentNode)
                                    (pFirst ? FIRST : LAST);

        double                   expectedValue = 0;
        Map<AlexoAction, Double> expectation   =
                new EnumMap<AlexoAction, Double>(AlexoAction.class);
        for (AlexoAction act : AlexoAction.VALUES)
        {
            if (! proponent.actionAvalable( act )) continue;

            double actProb = proponent.probabilityOf(act);
            if (actProb == 0 && proponent.isInformed())
            {
                expectation.put(act, 0.0);
                continue;
            }

//            double val =
//                    (child(act).approximate(b,
//                        pA * (pFirst ? actProb : 1.0),
//                        pB * (pFirst ? 1.0 : actProb),
//                        aggression)
//                     * (1.0 + aggression))
//                    * (pFirst ? 1 : -1);
            double val =
                    child(act).approximate(b,
                        pA * (pFirst ? actProb : 1.0),
                        pB * (pFirst ? 1.0 : actProb),
                        aggression)
                    * (pFirst ? 1 : -1);

            expectation.put(act, val);
            expectedValue += actProb * val;
        }

        Map<AlexoAction, Double> counterfactualRegret  =
                new EnumMap<AlexoAction, Double>(AlexoAction.class);
        for (AlexoAction act : AlexoAction.VALUES)
        {
            if (! expectation.containsKey( act )) continue;

            double cRegret =
                    (expectation.get(act) - expectedValue) * pB;
            counterfactualRegret.put(act, cRegret);
        }
        proponent.add( counterfactualRegret );

        return (pFirst ? 1 : -1) * expectedValue;
    }
}
