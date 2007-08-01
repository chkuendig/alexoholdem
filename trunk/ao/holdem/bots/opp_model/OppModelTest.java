package ao.holdem.bots.opp_model;

import ao.holdem.history.Event;
import ao.holdem.history.HandHistory;
import ao.holdem.history.PlayerHandle;
import ao.holdem.history.persist.PlayerHandleAccess;
import com.google.inject.Inject;
import com.wideplay.warp.persist.Transactional;

/**
 *
 */
public class OppModelTest
{
    //--------------------------------------------------------------------
    @Inject PlayerHandleAccess playerAccess;


    //--------------------------------------------------------------------
    @Transactional
    public void testOpponentModeling()
    {
//        retrieveMostPrevalent();
        modelOpponet(playerAccess.find("irc", "derek"));
    }


    //--------------------------------------------------------------------
    private void modelOpponet(PlayerHandle p)
    {
        System.out.println("analyzing " + p.getName());

//        AnjiNet net = new AnjiNet();

        for (HandHistory h : p.getHands())
        {
            int myMoves = 0;
            for (Event e : h.getEvents())
            {
                if (p.equals( e.getPlayer() ))
                {
                    myMoves++;
                }
            }
            System.out.println("my moves in hand: " + myMoves);
        }
    }


    //--------------------------------------------------------------------
    /*  irc.sagerbot :: 3156
        irc.leo :: 2691
        irc.greg :: 2604
        irc.perfecto :: 2542
        irc.ab0 :: 2489
        irc.Kai :: 1957
        irc.Lev :: 1896
        irc.derek :: 1869
        irc.EdK :: 1821
        irc.kman :: 1810 */
    private void retrieveMostPrevalent()
    {
        for (PlayerHandle p : playerAccess.byPrevalence(100))
        {
            System.out.println(
                    p.getName() + " :: " + p.getHands().size());
        }
    }

}
