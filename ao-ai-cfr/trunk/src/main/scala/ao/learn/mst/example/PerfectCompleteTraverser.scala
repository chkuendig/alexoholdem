package ao.learn.mst.example

import xml.{Node, PrettyPrinter, NodeSeq}
import ao.learn.mst.gen2.game.{ExtensiveGameTerminal, ExtensiveGameDecision, ExtensiveGameNode, ExtensiveGame}
import ao.learn.mst.gen2.player.RationalPlayer


/**
 * User: ao
 * Date: 11/03/12
 * Time: 9:44 PM
 */

object PerfectCompleteTraverser
    extends App
{
  //--------------------------------------------------------------------------------------------------------------------
  val game : ExtensiveGame =
    new PerfectCompleteGame

  println("rational player count = " +
    game.rationalPlayerCount)

  val xmlResult =
    displayNode(
      game.gameTreeRoot)

  val formatter = new PrettyPrinter(120, 4)
  println( formatter.format(xmlResult) )

  
  //--------------------------------------------------------------------------------------------------------------------
  def displayNode(
      node : ExtensiveGameNode) : xml.Node =
    node match {
      case decision : ExtensiveGameDecision =>
        <decision player={ decision.player.index.toString }>
          <information-set>
            {
              decision.informationSet.getClass.getSimpleName
            }
          </information-set>

          <actions>
            {
              for (action <- decision.actions) yield
                <action index={ action.index.toString }>
                  {
                    displayNode(
                      decision.child(action))
                  }
                </action>
            }
          </actions>
        </decision>

      case terminal : ExtensiveGameTerminal =>
        <terminal>
          {
            def outcomeXml(player : RationalPlayer, utility : Double) =
              <outcome player={ player.index.toString }>
                { utility }
              </outcome>

            for ((player, utility) <- terminal.payoff.outcomes)
              yield outcomeXml(player, utility)
          }
        </terminal>
    }
}
