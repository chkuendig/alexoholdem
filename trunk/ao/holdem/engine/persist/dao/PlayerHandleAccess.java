package ao.holdem.engine.persist.dao;

import ao.holdem.engine.persist.PlayerHandle;
import com.google.inject.name.Named;
//import com.wideplay.warp.persist.dao.Finder;
//import com.wideplay.warp.persist.dao.MaxResults;

import java.util.List;

/**
 * see
 *  http://www.wideplay.com/dynamicfinders
 */
public interface PlayerHandleAccess
{
//    @Finder(query="from PlayerHandle " +
//                  "where domain = :player_domain and " +
//                        "name   = :player_name")
    PlayerHandle find(
            @Named("player_domain") String domain,
            @Named("player_name")   String name);

    /**
     * @return lits of player handles sorted in decreasing
     *          order by how many hands they played.
     * @param max how many results do you wanna see.
     */
//    @Finder(query="select p " +
//                  "from PlayerHandle p " +
//                  "order by p.hands.size desc ")
    List<PlayerHandle> byPrevalence(/*@MaxResults*/ int max);

//    @Finder(query="select e " +
//                  "from Event e " +
//                  "where e.player = :player and " +
//                        "e.hand   = :hand")
//    List<Event> action(@Named("player") PlayerHandle of,
//                       @Named("hand")   HandHistory in);
}
