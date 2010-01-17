package ao.holdem.persist;

import ao.holdem.model.Avatar;
import ao.holdem.model.replay.Replay;
import com.sleepycat.bind.EntityBinding;
import com.sleepycat.bind.EntryBinding;
import com.sleepycat.collections.StoredEntrySet;
import com.sleepycat.collections.StoredMap;

/**
 *
 */
public class HoldemViews
{
    //--------------------------------------------------------------------
    private StoredMap/*<UniqueId, Hand>*/         hands;
    private StoredMap/*<Avatar, List<UniqueId>>*/ avatars;


    //--------------------------------------------------------------------
    public HoldemViews(HoldemDb db)
    {
        EntryBinding  handKeyBinding   = UniqueId.BINDING;
        EntityBinding handValueBinding = Replay.BINDING;

        EntryBinding avatarKeyBinding   = Avatar.BINDING;
        EntryBinding avatarValueBinding = UniqueId.BINDING;

        hands =
            new StoredMap(db.handDb(),
                          handKeyBinding, handValueBinding, true);
        avatars =
            new StoredMap(db.avatarDb(),
                          avatarKeyBinding, avatarValueBinding, true);
    }


    //--------------------------------------------------------------------
    public StoredMap hands()
    {
        return hands;
    }
    public StoredMap avatars()
    {
        return avatars;
    }

    public final StoredEntrySet handEntrySet()
    {
        return (StoredEntrySet) hands.entrySet();
    }
    public final StoredEntrySet avatarEntrySet()
    {
        return (StoredEntrySet) avatars.entrySet();
    }
}
