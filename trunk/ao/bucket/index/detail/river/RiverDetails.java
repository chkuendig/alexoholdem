package ao.bucket.index.detail.river;

import ao.bucket.index.canon.flop.Flop;
import ao.bucket.index.canon.hole.CanonHole;
import ao.bucket.index.canon.river.River;
import ao.bucket.index.canon.turn.Turn;
import ao.bucket.index.detail.flop.FlopDetailFlyweight.CanonFlopDetail;
import ao.bucket.index.detail.flop.FlopDetails;
import ao.bucket.index.detail.turn.TurnRivers;
import ao.bucket.index.enumeration.HandEnum;
import ao.util.misc.Filter;
import ao.util.misc.Traverser;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: Jan 9, 2009
 * Time: 12:44:39 PM
 */
public class RiverDetails
{
//    //--------------------------------------------------------------------
//    // slow but precise
//    public static void lookup(
//            int canonTurn, Traverser<CanonDetail> visit)
//    {
//        CanonTurnDetail turn = TurnDetails.lookup( canonTurn );
//        CanonFlopDetail flop =
//                FlopDetails.containing( canonTurn );
//        CanonHoleDetail hole = flop.holeDetail();
//        Turn turnSeq  = turn(hole, flop, turn);
//
//        int      turnShortcut  = Eval7Faster.shortcutFor(
//                 flop.a(), flop.b(), flop.c(), turn.example());
//        int      hTurnShortcut = shortcut(hole, flop, turn);
//        Set<Card> usedCards    = EnumSet.of(
//                hole.example().a(), hole.example().b(),
//                flop.a(), flop.b(), flop.c(), turn.example());
//
//        Set<Long> seen = new HashSet<Long>();
//        for (Card r : Card.VALUES)
//        {
//            if (usedCards.contains( r )) continue;
//
//            River river = turnSeq.addRiver( r );
//
//            Long longIndex = river.canonIndex();
//            if (seen.contains( longIndex )) continue;
//
//            seen.add( longIndex );
//            usedCards.add( r );
//
//            short val = Eval7Faster.fastValueOf(hTurnShortcut, r);
//            visit.traverse(
//                    new CanonRiverDetail(
//                            nonLossProb(val, usedCards,
//                                        Eval7Faster.nextShortcut(
//                                                turnShortcut, r)),
//                            river.canonIndex()));
//
//            usedCards.remove( r );
//        }
//    }
//    private static double nonLossProb(
//            short val, Set<Card> usedCards, int riverShortcut)
//    {
//        int win = 0, lose = 0, split = 0;
//        for (int i = 1; i < Card.VALUES.length; i++) {
//            Card oppHoleA = Card.VALUES[ i ];
//            if (usedCards.contains( oppHoleA )) continue;
//
//            int shortRiverHoleA =
//                    Eval7Faster.nextShortcut(riverShortcut, oppHoleA);
//            for (int j = 0; j < i; j++) {
//                Card oppHoleB = Card.VALUES[ j ];
//                if (usedCards.contains( oppHoleB )) continue;
//
//                short oppVal = Eval7Faster.fastValueOf(
//                                shortRiverHoleA, oppHoleB);
//                if (val > oppVal) {
//                    win++;
//                } else if (val < oppVal) {
//                    lose++;
//                } else {
//                    split++;
//                }
//            }
//        }
//        return (win + (double)split/2)
//               / (win + lose + split);
//    }


//    //--------------------------------------------------------------------
//    // fast but imprecise
//    public static void lookup(
//            int canonTurn, Traverser<CanonDetail> visit)
//    {
//        CanonTurnDetail turn = TurnDetails.lookup( canonTurn );
//        CanonFlopDetail flop =
//                FlopDetails.containing( canonTurn );
//        CanonHoleDetail hole = flop.holeDetail();
//
//        Turn turnSeq  = turn(hole, flop, turn);
//        int  shortcut = shortcut(hole, flop, turn);
//
//        Set<Long> seen = new HashSet<Long>();
//        for (Card r : remainder(hole, flop, turn))
//        {
//            River river = turnSeq.addRiver( r );
//
//            Long longIndex = river.canonIndex();
//            if (! seen.contains( longIndex ))
//            {
//                seen.add( longIndex );
//
//                short strength = RiverStrengths.lookup(
//                        Eval7Faster.fastValueOf(shortcut, r));
//                visit.traverse(
//                        new CanonRiverDetail(
//                                strength, river.canonIndex()));
//            }
//        }
//    }
//
//
//    //--------------------------------------------------------------------
//    // fast but imprecise
//    public static Collection<CanonRiverDetail> lookup(int canonTurn)
//    {
//        CanonTurnDetail turn = TurnDetails.lookup( canonTurn );
//        CanonFlopDetail flop =
//                FlopDetails.containing( canonTurn );
//        CanonHoleDetail hole = flop.holeDetail();
//
//        Turn turnSeq  = turn(hole, flop, turn);
//        int  shortcut = shortcut(hole, flop, turn);
//
//        Map<Long, CanonRiverDetail> details =
//                new HashMap<Long, CanonRiverDetail>();
//        for (Card r : remainder(hole, flop, turn))
//        {
//            River river = turnSeq.addRiver( r );
//
//            if (! details.containsKey( river.canonIndex() ))
//            {
//                short strength = RiverStrengths.lookup(
//                        Eval7Faster.fastValueOf(shortcut, r));
//                details.put(
//                        river.canonIndex(),
//                        new CanonRiverDetail(
//                                strength, river.canonIndex()));
//            }
//        }
//
//        return details.values();
//    }
//
//
//    //--------------------------------------------------------------------
////    private static void display(
////            CanonHole hole, CanonFlopDetail flop, CanonTurnDetail turn,
////            Card river)
////    {
////        System.out.println(Arrays.toString(new Card[]{
////                hole.a(), hole.b(),
////                flop.a(), flop.b(), flop.c(),
////                turn.example(), river
////        }));
////    }
//
//    private static Turn turn(
//            CanonHoleDetail hole, CanonFlopDetail flop, CanonTurnDetail turn)
//    {
//        return hole.canon().addFlop(
//                        flop.a(), flop.b(), flop.c()
//                ).addTurn(turn.example());
//    }
//
//    private static Iterable<Card> remainder(
//            CanonHoleDetail hole, CanonFlopDetail flop, CanonTurnDetail turn)
//    {
//        Hole concreteHole = hole.example();
//        return EnumSet.complementOf(EnumSet.of(
//                concreteHole.a(), concreteHole.b(),
//                flop.a(), flop.b(), flop.c(),
//                turn.example()));
//    }
//
//    private static int shortcut(
//            CanonHoleDetail hole, CanonFlopDetail flop, CanonTurnDetail turn)
//    {
//        Hole concreteHole = hole.example();
//        return Eval7Faster.shortcutFor(
//                    concreteHole.a(), concreteHole.b(),
//                    flop.a(), flop.b(), flop.c(),
//                    turn.example());
//    }


    //--------------------------------------------------------------------
    public static List<River> examplesOf(long canonRiver)
    {
        final long acceptRiver = canonRiver;
        final int  acceptTurn = TurnRivers.turnFor( acceptRiver );
        CanonFlopDetail flopDetail = FlopDetails.containing(acceptTurn);
        final int  acceptFlop = (int) flopDetail.canonIndex();
        final int  acceptHole = (int) flopDetail.holeDetail().canonIndex();

        final List<River> examples = new ArrayList<River>();
        HandEnum.rivers(
                new Filter<CanonHole>() {
                    public boolean accept(CanonHole canonHole) {
                        return canonHole.canonIndex() == acceptHole;
                    }
                },
                new Filter<Flop>() {
                    public boolean accept(Flop flop) {
                        return flop.canonIndex() == acceptFlop;
                    }
                },
                new Filter<Turn>() {
                    public boolean accept(Turn turn) {
                        return turn.canonIndex() == acceptTurn;
                    }
                },
                new Filter<River>() {
                    public boolean accept(River river) {
                        return river.canonIndex() == acceptRiver;
                    }
                },
                new Traverser<River>() {
            public void traverse(River river) {
                examples.add( river );
            }
        });
        return examples;
    }
}
