package ao.learn.mst.example

import ao.learn.mst.gen2.game.ExtensiveGame
import ao.learn.mst.example.kuhn.adapt.KuhnGame

/**
 * 25/04/13 7:58 PM
 */
object ExtensiveGameTournament
  extends App
{
  val game : ExtensiveGame =
    KuhnGame

  val extensiveGameRoot =
    game.treeRoot


}
