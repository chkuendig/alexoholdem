package ao.learn.mst.kuhn.adapt

import ao.learn.mst.gen.player.ExtensivePlayer
import ao.learn.mst.gen.ExtensiveGameNodeX
import ao.learn.mst.example.kuhn.play.KuhnPlayer
import ao.learn.mst.gen.act.ExtensiveAction

/**
 * Date: 15/11/11
 * Time: 1:29 AM
 */
class KuhnExtensivePlayer(delegate : KuhnPlayer)
    extends ExtensivePlayer
{
  //--------------------------------------------------------------------------------------------------------------------
  def act(state: ExtensiveGameNodeX) : ExtensiveAction = {
    val kuhnNode = state.asInstanceOf[KuhnExtensiveGameNodeX]
    ExtensiveAction(delegate.act( kuhnNode.delegate ).id)
  }
}