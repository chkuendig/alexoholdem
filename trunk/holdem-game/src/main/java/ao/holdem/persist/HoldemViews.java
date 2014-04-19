package ao.holdem.persist;

import ao.holdem.model.Avatar;
import ao.holdem.model.AvatarBinding;
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
    private StoredMap<UniqueId, Replay> hands;
    private StoredMap<Avatar, UniqueId> avatars;


    //--------------------------------------------------------------------
    public HoldemViews(HoldemDb db)
    {
        EntryBinding <UniqueId>  handKeyBinding   = UniqueId.BINDING;
        EntityBinding<Replay>    handValueBinding = Replay  .BINDING;

        EntryBinding<Avatar>   avatarKeyBinding   = AvatarBinding.INSTANCE;
        EntryBinding<UniqueId> avatarValueBinding = UniqueId.BINDING;

        hands = new StoredMap<UniqueId, Replay>(
                db.handDb(),
                handKeyBinding,
                handValueBinding,
                true);

        avatars = new StoredMap<Avatar, UniqueId>(
                db.avatarDb(),
                avatarKeyBinding,
                avatarValueBinding, true);
    }


    //--------------------------------------------------------------------
    /**
     * @return a list of all games by their unique id.  Sorted by the
     *      natural order of UniqueId (i.e. creation time).
     */
    public StoredMap<UniqueId, Replay> hands()
    {
        return hands;
    }

    /**
     * @return multi-map representing the list of hands that
     *      each Avatar played. Sorted by Avatar domain then name.
     */
    public StoredMap<Avatar, UniqueId> avatars()
    {
        return avatars;
    }

    public final StoredEntrySet<UniqueId, Replay> handEntrySet()
    {
        return (StoredEntrySet<UniqueId, Replay>) hands.entrySet();
    }
    public final StoredEntrySet<Avatar, UniqueId> avatarEntrySet()
    {
        return (StoredEntrySet<Avatar, UniqueId>) avatars.entrySet();
    }
}
