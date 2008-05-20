package ao.simple.rules;

import ao.bucket.Bucket;
import ao.simple.KuhnCard;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 */
public class KuhnSequencer
{
    private final Bucket ROOT;

    public KuhnSequencer()
    {
//        Map<KuhnCard, Collection<Bucket>> seq =
//                new EnumMap<KuhnCard, Collection<Bucket>>(
//                        KuhnCard.class);
//
//        for (KuhnCard[] pockets :
//                newh Permuter<KuhnCard>(KuhnCard.values(), 2))
//        {
//            Bucket bucket =
//                    new KuhnJbs(pockets[0], pockets[1]);
//
//            Collection<Bucket> subSeq = seq.get( pockets[0] );
//            if (subSeq == null)
//            {
//                subSeq = new ArrayList<Bucket>();
//                seq.put( pockets[0], subSeq );
//            }
//            subSeq.add( bucket );
//        }
//
//        Collection<Bucket> rootKids = new ArrayList<Bucket>();
//        for (Map.Entry<KuhnCard, Collection<Bucket>> e :
//                seq.entrySet())
//        {
//            rootKids.add(
//                    new KuhnJbs(null, e.getValue()));
//        }
//        ROOT = new KuhnJbs(null, rootKids);

        Collection<Bucket> kids = new ArrayList<Bucket>();
        for (KuhnCard c : KuhnCard.values())
        {
            kids.add( new KuhnBucket(c) );
        }
        ROOT = new KuhnBucket(null, kids);
    }

    public Bucket root()
    {
        return ROOT;
    }

//    public Bucket sequence(KuhnCard first,
//                           KuhnCard last)
//    {
//        return null;
//    }
}
