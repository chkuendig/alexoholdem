package ao.holdem.history.persist;

import ao.holdem.history.PlayerHandle;
import com.google.inject.name.Named;
import com.wideplay.warp.persist.dao.Finder;

/**
 * see
 *  http://www.wideplay.com/dynamicfinders
 */
public interface PlayerHandleAccess
{
    @Finder(query="from PlayerHandle where name = :name")
    PlayerHandle find(@Named("name") String name);
}
