package ao.holdem.bots.opp_model.predict;

/**
 *
 */
public interface RlNet
{
    public void addInput( Input  in);

    public void addOutput(Output out);

    public void reinforce(/* Feedback reward */);
    
}
