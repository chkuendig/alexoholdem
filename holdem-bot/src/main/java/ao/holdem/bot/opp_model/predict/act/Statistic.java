package ao.holdem.bot.opp_model.predict.act;


import ao.ai.classify.decision.impl.input.raw.example.Context;

/**
 *
 */
public interface Statistic
{
    public Context nextActContext();
}
