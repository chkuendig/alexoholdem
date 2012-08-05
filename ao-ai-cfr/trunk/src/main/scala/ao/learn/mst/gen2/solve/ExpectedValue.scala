package ao.learn.mst.gen2.solve

import ao.learn.mst.gen2.player.RationalPlayer
import scala.Predef._
import ao.learn.mst.gen2.player.RationalPlayer
import org.specs2.internal.scalaz.Digit._0


//----------------------------------------------------------------------------------------------------------------------
/**
 * "Each terminal (leaf) node of the game tree has an n-tuple of payoffs,
 *  meaning there is one payoff for each player at the end of every possible play".
 *
 * @param outcomes payoffs
 */
case class ExpectedValue(outcomes : Map[RationalPlayer, Double])


object ExpectedValue {
  def apply(values: Seq[Double]):ExpectedValue = {
    val valuesWithIndex: Seq[(Double, Int)] = values.zipWithIndex

    val playersWithValues: Seq[(RationalPlayer, Double)] =
      valuesWithIndex.map(vi => (RationalPlayer(vi._2), vi._1))

    ExpectedValue(
      Map[RationalPlayer, Double]()
        ++ playersWithValues)
  }
}