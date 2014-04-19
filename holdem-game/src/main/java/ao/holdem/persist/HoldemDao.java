package ao.holdem.persist;

import ao.holdem.model.Avatar;
import ao.holdem.model.AvatarBinding;
import ao.holdem.model.replay.Replay;
import ao.util.math.rand.Rand;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.collections.TransactionWorker;
import com.sleepycat.je.*;
import org.apache.log4j.Logger;

import java.util.*;

/**
 *
 */
public class HoldemDao
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(HoldemDao.class);


    //--------------------------------------------------------------------
    private HoldemDb                              db;
    private StoredMap/*<UniqueId, Hand>*/         hands;
    private StoredMap/*<Avatar, List<UniqueId>>*/ avatars;


    //--------------------------------------------------------------------
    public HoldemDao(HoldemDb database, HoldemViews views)
    {
        db      = database;
        hands   = views.hands();
        avatars = views.avatars();
    }


    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void persist(final Replay hand)
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


    //--------------------------------------------------------------------
    public List<Replay> retrieve(Avatar withPlayer)
    {
        return retrieve(withPlayer, Integer.MAX_VALUE);
    }

    @SuppressWarnings("unchecked")
    public List<Replay> retrieve(
            Avatar withPlayer, int numLatest)
    {
        List<Replay> playerHands = new ArrayList<>();

        int count = 0;
        for (Object handId : avatars.duplicates(withPlayer))
        {
            if (count++ >= numLatest) break;

            playerHands.add(
                    (Replay) hands.get( handId ));
        }

        return playerHands;
    }


    //--------------------------------------------------------------------
    public void printByPrevalence(int howMany)
    {
        for (Map.Entry<Avatar, Integer> e :
                mostPrevalent(howMany).entrySet())
        {
            LOG.info(e.getKey() + ": " + e.getValue());
        }
    }

    public Map<Avatar, Integer> mostPrevalent(int howMany)
    {
        Map<Avatar, Integer> byAvatar =
                new LinkedHashMap<>();
        for (Map.Entry<Double, Avatar> e :
                safeByHands(howMany).entrySet())
        {
            byAvatar.put(e.getValue(),
                         (int) Math.round(e.getKey()));
        }
        return byAvatar;
    }

    private SortedMap<Double, Avatar> safeByHands(int howMany)
    {
        try
        {
            return byHands(howMany);
        }
        catch (DatabaseException e)
        {
            throw new Error( e );
        }
    }
    private SortedMap<Double, Avatar> byHands(int howMany)
            throws DatabaseException
    {
        SortedMap<Double, Avatar> byHands =
                new TreeMap<>();

        double min    = Long.MIN_VALUE;
        Cursor cursor = db.openAvatarCursor();

        DatabaseEntry foundKey  = new DatabaseEntry();
        DatabaseEntry foundData = new DatabaseEntry();

        while (cursor.getNextNoDup(
                    foundKey,
                    foundData,
                    LockMode.DEFAULT) ==
                      OperationStatus.SUCCESS)
        {
            Avatar avatar =
                    AvatarBinding.INSTANCE.entryToObject(
                            new TupleInput(foundKey.getData()));

            int    count      = cursor.count();
            double fuzzyCount = count + Rand.nextDouble();
            if (min < fuzzyCount ||
                byHands.size() < howMany)
            {
                byHands.put(fuzzyCount, avatar);
                if (byHands.size() > howMany)
                {
                    byHands.remove( byHands.firstKey() );
                }
                min = byHands.firstKey();
            }
        }
        cursor.close();

        return byHands;
    }
}
