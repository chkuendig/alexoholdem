package ao.ai.opp_model.input;

import ao.ai.opp_model.classifier.raw.Classifier;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;

/**
 *
 */
public interface InputPlayerFactory
{
    //--------------------------------------------------------------------
    public InputPlayer newInstance(
                            HandHistory  history,
                            Classifier   addTo,
                            PlayerHandle player,
                            boolean      publishActions);
}
