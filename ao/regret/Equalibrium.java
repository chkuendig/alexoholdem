package ao.regret;

import ao.holdem.model.act.AbstractAction;

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
        InfoSet infoA = rA.info();

        if (rA.isPlayerNode())
        {
            // computer positive counterfactual regret
            //  for each available action at I(rA)

            //Regret
            for (AbstractAction act : infoA.acts())
            {
                InfoNode childA = rA.child(act);
                InfoNode childB = rB.child(act);

            }
        }
    }

}
