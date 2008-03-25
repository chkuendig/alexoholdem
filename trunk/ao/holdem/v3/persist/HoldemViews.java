package ao.holdem.v3.persist;

import ao.holdem.v3.model.Avatar;
import ao.holdem.v3.model.hand.HandId;
import ao.holdem.v3.model.hand.Replay;
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
    private StoredMap/*<HandId, Hand>*/         hands;
    private StoredMap/*<Avatar, List<HandId>>*/ avatars;


    //--------------------------------------------------------------------
    public HoldemViews(HoldemDb db)
    {
        EntryBinding  handKeyBinding   = HandId.BINDING;
        EntityBinding handValueBinding = Replay.BINDING;

        EntryBinding avatarKeyBinding   = Avatar.BINDING;
        EntryBinding avatarValueBinding = HandId.BINDING;

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
