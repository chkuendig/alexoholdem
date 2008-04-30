package ao.bucket.index.iso_case;

import ao.holdem.model.card.Hole;

/**
 *
 */
public enum HoleCase
{
    PAIR, SUITED, UNSUITED;

    public static HoleCase newInstance(Hole hole)
    {
        return hole.paired()
               ? PAIR
               : hole.suited()
                 ? SUITED
                 : UNSUITED;
    }
}
