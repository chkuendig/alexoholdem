package ao.regret.holdem.pair;

/**
 * Date: Jan 18, 2009
 * Time: 7:10:54 PM
 */
public class PlayerPair //implements InfoPair
{
//    //--------------------------------------------------------------------
//    private final PlayerNode DEALER;
//    private final PlayerNode DEALEE;
//
//
//    //--------------------------------------------------------------------
//    public PlayerPair(PlayerNode dealer,
//                      PlayerNode dealee)
//    {
//        DEALER = dealer;
//        DEALEE = dealee;
//    }
//
//
//    //--------------------------------------------------------------------
//    public double proponentProbability(AbstractAction ofAction)
//    {
//        return proponent().probabilityOf( ofAction );
//    }
//
//    public boolean proponentIsInformed()
//    {
//        return proponent().isInformed();
//    }
//
//    public boolean dealerIsProponent()
//    {
//        return DEALER instanceof ProponentNode;
//    }
//
//    private ProponentNode proponent()
//    {
//        return (ProponentNode)(
//                    dealerIsProponent()
//                    ? DEALER : DEALEE);
//    }
//
//
//    //--------------------------------------------------------------------
//    public InfoPair child(AbstractAction action)
//    {
//        InfoNode dealerChild = DEALER.child( action );
//        InfoNode dealeeChild = DEALEE.child( action );
//
//        if (dealerChild instanceof BucketNode)
//        {
//            return new BucketPair(((BucketNode) dealerChild),
//                                  ((BucketNode) dealeeChild));
//        }
//        else if (dealeeChild instanceof PlayerNode)
//        {
//            return new PlayerPair((PlayerNode) dealerChild,
//                                  (PlayerNode) dealeeChild);
//        }
//        else
//        {
//            return new TerminalPair((TerminalNode) dealerChild,
//                                    (TerminalNode) dealeeChild);
//        }
//    }
//
//
//    //--------------------------------------------------------------------
//    public double approximate(
//            JointBucketSequence b,
//            double              pDealer,
//            double              pDealee,
//            double              aggression)
//    {
//        boolean       dealerProp = dealerIsProponent();
//        ProponentNode proponent  = proponent();
//
//        double   expectedValue = 0;
//        double[] expectation   =
//                new double[ AbstractAction.VALUES.length ];
//        for (AbstractAction act : AbstractAction.VALUES)
//        {
//            if (! proponent.actionAvalable( act ))
//            {
//                expectation[ act.ordinal() ] = Double.NaN;
//                continue;
//            }
//
//            double actProb = proponent.probabilityOf(act);
//            if (actProb == 0 && proponent.isInformed())
//            {
////                expectation[ act.ordinal() ] = 0.0;
//                continue;
//            }
//
//            double val =
//                    child(act).approximate(b,
//                        pDealer * (dealerProp ? actProb : 1.0),
//                        pDealee * (dealerProp ? 1.0 : actProb),
//                        aggression)
//                    * (dealerProp ? 1 : -1);
//
//            expectation[ act.ordinal() ] = val;
//            expectedValue += actProb * val;
//        }
//
//        double[] counterfactualRegret =
//                new double[ AbstractAction.VALUES.length ];
//        for (AbstractAction act : AbstractAction.VALUES)
//        {
//            if (Double.isNaN( expectation[ act.ordinal() ] )) continue;
//
//            // todo: confirm that * pDealee makes sense
//            double cRegret =
//                    (expectation[ act.ordinal() ] - expectedValue) * pDealee;
//            counterfactualRegret[ act.ordinal() ] = cRegret;
//        }
//        proponent.add( counterfactualRegret );
//
//        return (dealerProp ? 1 : -1) * expectedValue;
//    }
}
