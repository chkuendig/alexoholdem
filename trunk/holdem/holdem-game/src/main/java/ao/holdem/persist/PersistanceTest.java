package ao.holdem.persist;

import ao.holdem.model.Avatar;
import ao.holdem.model.Round;
import ao.holdem.model.act.Action;
import ao.holdem.model.card.chance.DeckCards;
import ao.holdem.model.replay.Replay;
import ao.util.math.rand.Rand;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class PersistanceTest
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(HoldemDao.class);


    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        Rand.nextBoolean();
        HoldemDb    db    = new HoldemDb("replay_db");
        HoldemViews views = new HoldemViews(db);
        HoldemDao   dao   = new HoldemDao(db, views);

        LOG.info("persisting random hands");
//        dao.persist( randomHand() );
        for (int i = 0; i < 10000; i++)
        {
            System.out.print(".");
            dao.persist( randomHand() );

            if ((i + 1) % 100 == 0) System.out.println();
        }
        System.out.println();

        LOG.info("retrieving");
        retrieve( dao );

        LOG.info("by prevalence");
        dao.printByPrevalence(5);

        db.close();
    }


    //--------------------------------------------------------------------
    private static void retrieve(HoldemDao dao)
    {
        Avatar prevalent =
                dao.mostPrevalent(1)
                        .keySet().iterator().next();
        long start = System.currentTimeMillis();
        for (Replay h : dao.retrieve(prevalent, 5))
        {
//            System.out.println(h);
        }
        long delta = System.currentTimeMillis() - start;
        LOG.info("took: " + delta);
    }

    private static Replay randomHand()
    {
        List<String> names = new ArrayList<String>(Arrays.asList(
                "a", "b", "c", "d", "e", "f", "g",
                "h", "i", "j", "k", "l", "m", "n"));

        int name = Rand.nextInt( names.size() );
        Avatar dealer   = Avatar.local(names.get( name ));
        names.remove(name);
        Avatar opponent = Avatar.local(Rand.fromList(names));

        Replay hand = new Replay(Arrays.asList(opponent, dealer),
                             new DeckCards(),
                             Rand.fromArray(Round.VALUES));

        for (int i = Rand.nextInt(10) + 5; i >= 0; i--)
        {
            hand.addAction(opponent, Rand.fromArray(Action.VALUES));
            hand.addAction(dealer,   Rand.fromArray(Action.VALUES));
        }

        return hand;
    }
}
