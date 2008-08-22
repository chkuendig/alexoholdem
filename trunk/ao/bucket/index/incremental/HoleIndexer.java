package ao.bucket.index.incremental;

import ao.bucket.index.iso_cards.IsoHole;
import ao.bucket.index.iso_case.HoleCase;
import ao.holdem.model.card.Hole;

/**
 * Date: Aug 21, 2008
 * Time: 7:22:03 PM
 */
public class HoleIndexer
{
    //--------------------------------------------------------------------
    public int indexOf(Hole hole)
    {
        IsoHole  isoHole = hole.isomorphism();
        HoleCase isoCase = isoHole.holeCase();

        int offset = 0;
        for (HoleCase.Type type : HoleCase.Type.values())
        {
            if (type == isoCase.type())
            {
                return offset + isoCase.subIndex();
            }
            offset += type.members();
        }

        return -1;
    }
}
