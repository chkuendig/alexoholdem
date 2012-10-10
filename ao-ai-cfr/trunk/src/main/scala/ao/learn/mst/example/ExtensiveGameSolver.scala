package ao.learn.mst.example

import imperfect.complete.ImperfectCompleteGame
import incomplete.IncompleteGame
import ocp.adapt.OcpGame
import perfect.complete.PerfectCompleteGame
import rps.RockPaperScissorsGame
import slot.specific.bin.DeterministicBinaryBanditGame
import slot.specific.k.MarkovBanditGame
import xml.PrettyPrinter
import ao.learn.mst.gen2.solve.ExpectedValue
import ao.learn.mst.gen2.info.TraversingInformationSetIndexer
import ao.learn.mst.cfr._
import ao.learn.mst.gen2.game._
import zerosum.ZeroSumGame
import ao.learn.mst.example.kuhn.adapt.KuhnGame
import org.joda.time.{Duration, LocalTime, DateTime}


/**
 * User: ao
 */

object ExtensiveGameSolver
    extends App
{
  //--------------------------------------------------------------------------------------------------------------------
  val formatter = new PrettyPrinter(120, 4)


  //--------------------------------------------------------------------------------------------------------------------
  val game : ExtensiveGame =
//    DeterministicBinaryBanditGame
//    new MarkovBanditGame(16)
//    RockPaperScissorsGame
//    PerfectCompleteGame
//    ImperfectCompleteGame
//    IncompleteGame // does not randomize?
//    ZeroSumGame
    KuhnGame
//    OcpGame

  val extensiveGameRoot =
    game.treeRoot

  println("rational player count = " +
    game.rationalPlayerCount)

  //-------------------------------------------------------
  val extensiveGameViewRoot =
    displayExtensiveGameNode(
      extensiveGameRoot)

  val showFullGame = true
  val showGameViews = true

  if (showFullGame)
  {
    println("Full extensive game")
    println(formatter.format(extensiveGameViewRoot))
  }


  //-------------------------------------------------------
  if (showGameViews)
  {
//      val firstPlayerView =
//        PlayerViewBuilder.expand(
//          game, RationalPlayer(0))
//
//      println("First player's view")
//      println(formatter.format(
//        displayPlayerViewNode( firstPlayerView )))
//
//      val secondPlayerView =
//        PlayerViewBuilder.expand(
//          game, RationalPlayer(1))
//
//      println("Second player's view")
//      println(formatter.format(
//        displayPlayerViewNode( secondPlayerView )))
  }



  //--------------------------------------------------------------------------------------------------------------------
  val equilibriumApproximationIterations = 100 * 1000 * 1000
  println("\n\n\nCalculating Equalibrium: " +
    equilibriumApproximationIterations)


  val informationSetIndex =
    TraversingInformationSetIndexer.index( game )

  val strategyProfile =
    new StrategyProfile( informationSetIndex )

  println( strategyProfile )

  val minimizer =
//    new CfrMinimizer()
    new ChanceSampledCfrMinimizer()

  val startTime = System.currentTimeMillis()
  for (i <- 1 to equilibriumApproximationIterations) {
    if (i % (100 * 1000) == 0) {
      println(i + " " + new Duration(startTime, System.currentTimeMillis()))
      println(strategyProfile)
    }

    minimizer.reduceRegret(
      game, informationSetIndex, strategyProfile)
//    println( strategyProfile )
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
        <decision player={ decision.player.index.toString } name={ decision.toString }>
          <information-set>
            {
//              decision.informationSet.getClass.getSimpleName
              decision.informationSet.toString
            }
          </information-set>

          <actions>
            {
              for (action <- decision.actions) yield
                <action index={ action.index.toString } name={ action.toString }>
                  { displayExtensiveGameNode(
                      decision.child(action)) }
                </action>
            }
          </actions>
        </decision>

      case chance : ExtensiveGameChance =>
        <chance name={ chance.toString }>
          {
            for (action <- chance.probabilityMass.actionProbabilities) yield
              <outcome
                  index={ action._1.index.toString }
                  name={ action._1.toString }
                  probability={ action._2.toString }>
                {
                  displayExtensiveGameNode(
                    chance.child( action._1 ))
                }
              </outcome>
          }
        </chance>

      case terminal : ExtensiveGameTerminal =>
        <terminal name={ terminal.toString }>
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
        <chance>
          { displayPlayerViewKids( chance.kids ) }
        </chance>

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
