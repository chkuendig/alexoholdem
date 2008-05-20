package ao.regret;

import ao.regret.node.*;

/**
 *
 */
public class Equalibrium
{
    /**
     * @param rA an information set tree for player 1.
     * @param rB an information set tree for player 2.
     * @param b A joint bucket sequence.
     * @param pA probability of player 1 playing to reach the node.
     * @param pB probability of player 2 playing to reach the node.
     */
    public void approximate(InfoNode            rA,
                            InfoNode            rB,
                            JointBucketSequence b,
                            double              pA,
                            double              pB)
    {
        //InfoSet infoA = rA.info();

        if (rA instanceof ProponentNode)
        {
            // computer positive counterfactual regret
            //  for each available action at I(rA)

            //Regret
//            for (AbstractAction act : infoA.acts())
//            {
//                InfoNode childA = rA.child(act);
//                InfoNode childB = rB.child(act);
//
//            }
        }
        else if (rB instanceof ProponentNode)
        {
            // rA is an apponent node
            // do opposite of above
        }
        else if (rA instanceof BucketNode)
        {
            BucketNode bucketA = (BucketNode) rA;
            BucketNode bucketB = (BucketNode) rB;

            PlayerNode nextA = bucketA.accordingTo(b);
            PlayerNode nextB = bucketB.accordingTo(b);

            
        }
        else if (rA instanceof TerminalNode)
        {
            TerminalNode terminalA = (TerminalNode) rA;
            TerminalNode terminalB = (TerminalNode) rB;

            double expectedValue =
                    terminalA.expectedValue( terminalB );
        }
        else
        {
            System.out.println("WTF?!!");
        }
    }

}
