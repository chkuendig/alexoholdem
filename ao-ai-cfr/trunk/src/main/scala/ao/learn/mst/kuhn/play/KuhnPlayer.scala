package ao.learn.mst.kuhn.play

import ao.learn.mst.kuhn.action.KuhnAction
import KuhnAction._
import ao.learn.mst.kuhn.state.KuhnState


//----------------------------------------------------------------------------------------------------------------------
trait KuhnPlayer
{
  //--------------------------------------------------------------------------------------------------------------------
  def act(state : KuhnState) : KuhnAction
}