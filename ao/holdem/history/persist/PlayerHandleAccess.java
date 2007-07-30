package ao.holdem.history.persist;

import ao.holdem.history.PlayerHandle;
import com.google.inject.name.Named;
import com.wideplay.warp.persist.dao.Finder;
import com.wideplay.warp.persist.dao.MaxResults;

import java.util.List;

/**
 * see
 *  http://www.wideplay.com/dynamicfinders
 */
public interface PlayerHandleAccess
{
    @Finder(query="from PlayerHandle where name = :player_name")
    PlayerHandle find(@Named("player_name") String name);

    /**
     * @return lits of player handles sorted in decreasing
     *          order by how many hands they played.
     * @param max how many results do you wanna see.
     */
    @Finder(query="select p " +
                  "from PlayerHandle p " +
                  "order by p.hands.size desc ")
    List<PlayerHandle> byPrevalence(@MaxResults int max);
}
