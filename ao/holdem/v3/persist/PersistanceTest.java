package ao.holdem.v3.persist;

import ao.holdem.v3.model.Avatar;
import ao.holdem.v3.model.act.descrete.Action;
import ao.holdem.v3.model.hand.Hand;

import java.util.Arrays;

/**
 *
 */
public class PersistanceTest
{
    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        HoldemDb    db    = new HoldemDb("hand_db");
        HoldemViews views = new HoldemViews(db);
        HoldemDao   dao   = new HoldemDao(db, views);
        
        persist(  dao );
        retrieve( dao );

        db.close();
    }

    private static void retrieve(HoldemDao dao)
    {
        for (Hand h : dao.retrieve(
                            Avatar.local("dealer")))
        {
            System.out.println(h);
        }
    }

    private static void persist(HoldemDao dao)
    {
        Avatar dealer   = Avatar.local("dealer");
        Avatar opponent = Avatar.local("opponent");

        Hand hand = new Hand(Arrays.asList(opponent, dealer));

        // preflop
        hand.addAction(opponent, Action.BET);
        hand.addAction(dealer,   Action.CALL);

        // on flop
        hand.addAction(opponent, Action.CHECK);
        hand.addAction(dealer,   Action.CHECK);

        // on turn
        hand.addAction(opponent, Action.CHECK);
        hand.addAction(dealer,   Action.CHECK);

        // on river
        hand.addAction(opponent, Action.RAISE_ALL_IN);
        hand.addAction(dealer,   Action.FOLD);

        dao.presist( hand );
    }
}
