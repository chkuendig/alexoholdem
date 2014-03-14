package ao.holdem.bot.main;

import ao.holdem.abs.bucket.abstraction.access.tree.BucketTree;
import ao.holdem.abs.bucket.abstraction.bucketize.build.FastBucketTreeBuilder;
import ao.holdem.abs.bucket.abstraction.bucketize.smart.KMeansBucketizer;
import ao.holdem.bot.regret.HoldemAbstraction;
import ao.holdem.canon.flop.Flop;
import ao.holdem.canon.hole.CanonHole;
import ao.holdem.canon.river.River;
import ao.holdem.canon.turn.Turn;
import ao.holdem.model.Avatar;
import ao.holdem.model.Round;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.chance.ChanceCards;
import ao.holdem.model.card.chance.DeckCards;

/**
 * 21/02/14 6:55 AM
 */
public class BucketTest
{
    public static void main(String[] args)
    {
        int  nHoleBuckets  =   5;
        char nFlopBuckets  =  25;
        char nTurnBuckets  = 125;
        char nRiverBuckets = 625;

        HoldemAbstraction abs = new HoldemAbstraction(
                new FastBucketTreeBuilder(
                        new KMeansBucketizer()),
                nHoleBuckets,
                nFlopBuckets,
                nTurnBuckets,
                nRiverBuckets);

        BucketTree tree = abs.tree(false);

        for (int i = 0; i < 1000; i++) {
            displayRandomBuckets(tree);
        }
    }

    private static void displayRandomBuckets(BucketTree tree) {
        System.out.println();

        ChanceCards cards = new DeckCards();
        Hole holeA = cards.hole(Avatar.local("dealer"));
        Hole holeB = cards.hole(Avatar.local("dealee"));
        Community community = cards.community(Round.RIVER);

        CanonHole canonHoleA = CanonHole.create(holeA);
        CanonHole canonHoleB = CanonHole.create(holeB);

        int holeBucketA = tree.getHole(canonHoleA.canonIndex());
        int holeBucketB = tree.getHole(canonHoleB.canonIndex());

        System.out.println(holeA + "\t" + holeBucketA);
        System.out.println(holeB + "\t" + holeBucketB);

        Flop flopA = canonHoleA.addFlop(community);
        Flop flopB = canonHoleB.addFlop(community);

        int flopBucketA = tree.getFlop(flopA.canonIndex());
        int flopBucketB = tree.getFlop(flopB.canonIndex());

        System.out.println(flopA + "\t" + flopBucketA);
        System.out.println(flopB + "\t" + flopBucketB);

        Turn turnA = flopA.addTurn(community.turn());
        Turn turnB = flopB.addTurn(community.turn());

        int turnBucketA = tree.getTurn(turnA.canonIndex());
        int turnBucketB = tree.getTurn(turnB.canonIndex());

        System.out.println(turnA + "\t" + turnBucketA);
        System.out.println(turnB + "\t" + turnBucketB);

        River riverA = turnA.addRiver(community.river());
        River riverB = turnB.addRiver(community.river());

        int riverBucketA = tree.getRiver(turnA.canonIndex());
        int riverBucketB = tree.getRiver(turnB.canonIndex());

        System.out.println(riverA + "\t" + riverBucketA);
        System.out.println(riverB + "\t" + riverBucketB);
    }
}
