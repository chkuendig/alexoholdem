package ao.holdem.v3.persist;

import ao.holdem.v3.model.Avatar;
import ao.holdem.v3.model.hand.Hand;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.collections.TransactionWorker;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class HoldemDao
{
    //--------------------------------------------------------------------
    private HoldemDb  db;
    private StoredMap hands;
    private StoredMap avatars;


    //--------------------------------------------------------------------
    public HoldemDao(HoldemDb database, HoldemViews views)
    {
        db      = database;
        hands   = views.hands();
        avatars = views.avatars();
    }


    //--------------------------------------------------------------------
    public void presist(final Hand hand)
    {
        db.atomic(new TransactionWorker() {
            public void doWork() throws Exception {
                hands.put(hand.id(), hand);

                for (Avatar avatar : hand.players())
                {
                    avatars.put(avatar, hand.id());
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    public List<Hand> retrieve(Avatar withPlayer)
    {
        List<Hand> playerHands = new ArrayList<Hand>();

        for (Object handId : avatars.duplicates(withPlayer))
        {
            playerHands.add(
                    (Hand) hands.get( handId ));
        }

        return playerHands;
    }

    public void printByPrevalence(int howMany)
    {

    }
}
