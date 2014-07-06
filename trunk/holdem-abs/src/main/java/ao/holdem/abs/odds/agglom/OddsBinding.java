package ao.holdem.abs.odds.agglom;

import ao.holdem.abs.odds.Odds;
import ao.holdem.persist.GenericBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 *
 */
public enum OddsBinding
{;
    public static final Binding BINDING = new Binding();

    public static class Binding extends GenericBinding<Odds> {
        public Odds read(TupleInput tupleInput) {
            return new Odds(tupleInput.readLong(),
                    tupleInput.readLong(),
                    tupleInput.readLong());
        }

        public void write(Odds o, TupleOutput tupleOutput) {
            tupleOutput.writeLong( o.winOdds() );
            tupleOutput.writeLong( o.loseOdds() );
            tupleOutput.writeLong( o.splitOdds() );
        }
    }
}
