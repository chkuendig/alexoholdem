package ao.irc;

import ao.holdem.model.replay.Replay;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class IrcHistorian
{
    //--------------------------------------------------------------------
    public Iterable<Replay> fromSnapshot(String dirName)
    {
        IrcReader r = new IrcReader(new File(dirName));

        Map<String, List<IrcAction>> players = groupPlayers(r);
        Map<Long, IrcRoster>         rosters = indexRosters(r);

        return new IrcHandItr(players, rosters, r.hands());
    }


    //--------------------------------------------------------------------
    private Map<String, List<IrcAction>> groupPlayers(IrcReader r)
    {
        Map<String, List<IrcAction>> players =
                new HashMap<String, List<IrcAction>>();

        for (IrcAction playerAction : r.actions())
        {
            retrieveOrCreate(players, playerAction.name())
                    .add( playerAction );
        }

        return players;
    }

    private List<IrcAction> retrieveOrCreate(
            Map<String, List<IrcAction>> map,
            String                       name)
    {
        List<IrcAction> values = map.get(name);
        if (values == null)
        {
            values = new ArrayList<IrcAction>();
            map.put(name, values);
        }
        return values;
    }


    //--------------------------------------------------------------------
    private Map<Long, IrcRoster> indexRosters(
            IrcReader r)
    {
        Map<Long, IrcRoster> rosters =
                new HashMap<Long, IrcRoster>();

        for (IrcRoster roster : r.roster())
        {
            Long timestamp = roster.timestamp();
            rosters.put(timestamp, roster);
        }

        return rosters;
    }
}
