package ao.holdem.abs.bucket;


import ao.holdem.ai.abs.card.CardAbstraction;
import ao.holdem.abs.bucket.v2.PercentileImperfectAbstractionBuilder;
import ao.holdem.ai.odds.OddsBy5;
import ao.holdem.model.card.canon.hole.CanonHole;
import ao.holdem.model.card.sequence.CardSequence;
import org.junit.Test;

public class PercentileImperfectAbstractionBuilderTest
{
    @Test
    public void testHole() {
        CardAbstraction abstraction = PercentileImperfectAbstractionBuilder.loadOrBuildAndSave(
                //20, 40, 40, 40);
                20, 30, 30, 50);

        for (int i = 0; i < CanonHole.CANONS; i++) {
            CanonHole hole = CanonHole.create(i);

            int bucket = abstraction.indexInRound(
                    new CardSequence(hole.reify()),
                    OddsBy5.INSTANCE);
            System.out.println(hole.a() + "\t" + hole.b() + "\t" + bucket);
//            System.out.println(hole.b() + "\t" + hole.a() + "\t" + bucket);
        }
    }
}
