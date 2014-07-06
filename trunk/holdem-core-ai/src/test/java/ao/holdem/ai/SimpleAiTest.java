package ao.holdem.ai;


import ao.ai.cfr.state.ArrayStrategyAccumulator;
import ao.ai.cfr.state.ArrayTable;
import ao.ai.cfr.state.AverageStrategy;
import ao.ai.cfr.state.MixedStrategy;
import ao.holdem.abs.ViewActionAbstraction;
import ao.holdem.abs.card.CentroidStrengthAbstraction;
import ao.holdem.ai.abs.CompoundStateAbstraction;
import ao.holdem.ai.abs.StateAbstraction;
import ao.holdem.ai.abs.act.ActionAbstraction;
import ao.holdem.ai.abs.act.BasicActionView;
import ao.holdem.ai.abs.card.CardAbstraction;
import ao.holdem.ai.odds.OddsBy5;
import ao.holdem.model.Round;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class SimpleAiTest
{
    public static void main(String[] args) throws IOException {
        File dir = new File("C:/-/proj/OpenHoldem-strat");

        InputStream cardAbsIn = new FileInputStream(new File(dir, "hsp-20-30-30-50.bin"));
        CardAbstraction cardAbstraction = CentroidStrengthAbstraction.load(cardAbsIn);
        cardAbsIn.close();

        System.out.println(cardAbstraction.count(Round.PREFLOP));

        ActionAbstraction actionAbstraction = ViewActionAbstraction.build(
                BasicActionView.VIEW);

        StateAbstraction stateAbstraction = new CompoundStateAbstraction(
                cardAbstraction, actionAbstraction,
                OddsBy5.INSTANCE);

        System.out.println(stateAbstraction.size());


        ArrayTable strategyStore = new ArrayTable();
        InputStream strategyIn = new FileInputStream(new File(dir, "strategy.bin"));
        strategyStore.read(strategyIn);
        strategyIn.close();

        MixedStrategy strategy = new AverageStrategy(
                new ArrayStrategyAccumulator(strategyStore));

        System.out.println(Arrays.toString(strategyStore.get(0)));
        System.out.println(Arrays.toString(strategy.probabilities(0, 3)));
    }
}
