package ao.stats;

import ao.ai.opp_model.decision.attr.AttributePool;
import ao.ai.opp_model.decision.context.HoldemContext;
import ao.holdem.model.Community;
import ao.holdem.model.act.RealAction;
import ao.persist.PlayerHandle;
import ao.state.HandState;
import ao.stats.impl.SpecificStats;

/**
 *
 */
public class PlayerStats implements CumulativeStatistic<PlayerStats>
{
    //--------------------------------------------------------------------
    private PlayerHandle  subject;
    private SpecificStats stats;
    private boolean       captureNext;


    //--------------------------------------------------------------------
    public PlayerStats(PlayerHandle player)
    {
        stats   = new SpecificStats();
        subject = player;
    }
    private PlayerStats(PlayerHandle  copySubject,
                        SpecificStats copyStats,
                        boolean       copyCaptureNext)
    {
        subject     = copySubject;
        stats       = copyStats;
        captureNext = copyCaptureNext;
    }


    //--------------------------------------------------------------------
    public void advance(HandState stateBeforeAct)
    {
        captureNext = stateBeforeAct.nextToAct()
                        .handle().equals( subject );
        if (!captureNext) return;

        stats.advance(stateBeforeAct);
    }

    public void advance(RealAction act, Community communityBeforeAct)
    {
        if (!captureNext) return;

        stats.advance(act, communityBeforeAct);
    }


    //--------------------------------------------------------------------
    public HoldemContext stats(AttributePool pool)
    {
        return stats.stats( pool );
    }


    //--------------------------------------------------------------------
    public PlayerStats prototype()
    {
        return new PlayerStats(subject, stats, captureNext);
    }
}
