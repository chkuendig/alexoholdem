package ao.regret.holdem;

import ao.holdem.model.Round;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;

/**
 * User: iscott
 * Date: Jan 6, 2009
 * Time: 4:48:44 PM
 */
public class JointBucketSequence
{
    //--------------------------------------------------------------------


    //--------------------------------------------------------------------
    private final Hole      HOLE_FIRST;
    private final Hole      HOLE_LAST;
    private final Community COMMUNITY;


    //--------------------------------------------------------------------
    public JointBucketSequence(
            Hole      firstToActHole,
            Hole      lastToActHole,
            Community community)
    {
        HOLE_FIRST = firstToActHole;
        HOLE_LAST  = lastToActHole;
        COMMUNITY  = community;
    }


    //--------------------------------------------------------------------
    public HoldemBucket bucket(
            boolean firstToAct,
            Round   round)
    {
        return null;
    }
}
