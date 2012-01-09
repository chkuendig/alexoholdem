package ao.learn.mst.gen.playout

import ao.learn.mst.gen.ExtensiveGameX
import ao.learn.mst.gen.player.ExtensivePlayer
import ao.learn.mst.kuhn.adapt.{KuhnExtensivePlayer, KuhnExtensiveGameX}
import ao.learn.mst.kuhn.play.impl.KuhnConsolePlayer

/**
 * Date: 15/11/11
 * Time: 1:20 AM
 */
object ExtensivePlayoutRunner
  extends App
{
  //--------------------------------------------------------------------------------------------------------------------
  val firstPlayer : ExtensivePlayer =
    new KuhnExtensivePlayer(new KuhnConsolePlayer)

  val lastPlayer : ExtensivePlayer =
    new KuhnExtensivePlayer(new KuhnConsolePlayer)

  val game : ExtensiveGameX =
    new KuhnExtensiveGameX

  val playout = new ExtensivePlayout

  val outcome = playout.play(
    game, Seq(firstPlayer, lastPlayer))

  println("Outcome is: " + outcome)
}