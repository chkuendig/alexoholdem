package ao.learn.mst.example

import imperfect.complete.ImperfectCompleteGame
import xml.{PrettyPrinter}
import ao.learn.mst.gen2.game.{ExtensiveGameTerminal, ExtensiveGameDecision, ExtensiveGameNode, ExtensiveGame}
import ao.learn.mst.gen2.solve.ExpectedValue
import ao.learn.mst.cfr._
import ao.learn.mst.gen2.info.TraversingInformationSetIndexer


/**
 * User: ao
 * Date: 11/03/12
 * Time: 9:44 PM
 */

object ExtensiveGameSolver
    extends App
{
  //--------------------------------------------------------------------------------------------------------------------
  val formatter = new PrettyPrinter(120, 4)


  //--------------------------------------------------------------------------------------------------------------------
  val game : ExtensiveGame =
//    PerfectCompleteGame
    ImperfectCompleteGame

  val extensiveGameRoot =
    game.gameTreeRoot

  println("rational player count = " +
    game.rationalPlayerCount)

  //-------------------------------------------------------
  val extensiveGameViewRoot =
    displayExtensiveGameNode(
      extensiveGameRoot)

//  println("Full extensive game")
//  println(formatter.format(extensiveGameViewRoot))


  //-------------------------------------------------------
//  val firstPlayerView =
//    PlayerViewBuilder.expand(
//      game,
//      RationalPlayer(0))
//
//  println("First player's view")
//  println(formatter.format(
//    displayPlayerViewNode( firstPlayerView )))
//
//  val secondPlayerView =
//    PlayerViewBuilder.expand(
//      game,
//      RationalPlayer(1))
//
//  println("Second player's view")
//  println(formatter.format(
//    displayPlayerViewNode( secondPlayerView )))


  //-------------------------------------------------------
  val equilibriumApproximationIterations = 100 * 1000
  println("\n\n\nCalculating Equalibrium: " +
    equilibriumApproximationIterations)


  val informationSetIndex =
    TraversingInformationSetIndexer.index( game )

  val strategyProfile =
    new StrategyProfile( informationSetIndex )

  println( strategyProfile )

  val minimizer = new CfrMinimizer()
  for (i <- 1 to equilibriumApproximationIterations) {
    minimizer.walkTree(
      game, informationSetIndex, strategyProfile)
  }

  println("\n\n\n\n\n")
  println( strategyProfile )

//  println("\nFirst player's view")
//  println(formatter.format(
//    displayPlayerViewNode( firstPlayerView )))
//
//  println("\nSecond player's view")
//  println(formatter.format(
//    displayPlayerViewNode( secondPlayerView )))

  
  //--------------------------------------------------------------------------------------------------------------------
  def displayExtensiveGameNode(
      node : ExtensiveGameNode) : xml.Node =
    node match {
      case decision : ExtensiveGameDecision =>
        <decision player={ decision.player.index.toString }>
          <information-set>
            { decision.informationSet.getClass.getSimpleName }
          </information-set>

          <actions>
            {
              for (action <- decision.actions) yield
                <action index={ action.index.toString }>
                  { displayExtensiveGameNode(
                      decision.child(action)) }
                </action>
            }
          </actions>
        </decision>

      case terminal : ExtensiveGameTerminal =>
        <terminal>
          { displayExpectedValue( terminal.payoff ) }
        </terminal>
    }


  //--------------------------------------------------------------------------------------------------------------------
  def displayPlayerViewNode(
      node : PlayerViewNode) : xml.Node =
  {
    node match {
      case terminal : TerminalNode =>
        <terminal>
          { displayExpectedValue( terminal.outcome ) }
        </terminal>

      case opponent : OpponentNode =>
        <opponent>
          { displayPlayerViewKids( opponent.kids ) }
        </opponent>

      case proponent : ProponentNode =>
        <proponent>
          <informationSet>{ proponent.informationSet }</informationSet>
          <averageStrategy>{ proponent.averageStrategy() }</averageStrategy>
          { displayPlayerViewKids( proponent.kids ) }
        </proponent>

      case chance: ChanceNode =>
        throw new UnsupportedOperationException

      case other =>
        throw new IllegalArgumentException(other.toString)
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  def displayExpectedValue(
      expectedValue : ExpectedValue) : xml.Node =
    <expectedValue>
      {
      for ((player, utility) <- expectedValue.outcomes)
      yield
        <outcome player={ player.index.toString }>
          { utility }
        </outcome>
      }
    </expectedValue>
  
  def displayExpectedValue(
      expectedValue : Seq[Double]) : xml.Node =
    <expectedValue>
      {
        for ((utility, player) <- expectedValue.zipWithIndex)
          yield
            <outcome player={ player.toString }>
              { utility }
            </outcome>
      }
    </expectedValue>

  def displayPlayerViewKids(
      kids : Seq[PlayerViewNode]) : xml.Node =
    <children>
      {
        for ((child, index) <- kids.zipWithIndex)
          yield <child actionIndex={ index.toString }>
            { displayPlayerViewNode(child) }
          </child>
      }
    </children>


//  def displayExpectedValue(
//                            expectedValue : ExpectedValue) : xml.Node =
//  {
//
//  }
}
