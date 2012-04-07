package ao.learn.mst.cfr

import ao.learn.mst.gen2.solve.ExpectedValue
import ao.learn.mst.gen2.info.InformationSet

/**
 * http://poker.cs.ualberta.ca/publications/NIPS07-cfr.pdf
 * Section A.4
 */

//----------------------------------------------------------------------------------------------------------------------
sealed abstract class PlayerViewNode

sealed trait NonProponent
sealed trait NonOpponent


//----------------------------------------------------------------------------------------------------------------------
class ChanceNode
    extends PlayerViewNode
    with    NonProponent
    with    NonOpponent
{
  def kids : Seq[DecisionNode] = null
}


//----------------------------------------------------------------------------------------------------------------------
class DecisionNode(
  val kids : Seq[/*_ >: */PlayerViewNode])
    extends PlayerViewNode
{

}


//----------------------------------------------------------------------------------------------------------------------
class OpponentNode(
  kids : Seq[PlayerViewNode])
    extends DecisionNode(kids)
    with    NonProponent
{
//  def kids : Seq[NonOpponent] = null
}


//----------------------------------------------------------------------------------------------------------------------
class ProponentNode(
  /*override val */kids  : Seq[PlayerViewNode/*NonProponent*/],
  val informationSet : InformationSet)
    extends DecisionNode(kids)
    with    NonOpponent
{
  //--------------------------------------------------------------------------------------------------------------------
  var visitCount            = 0
  val regretSum             = new Array[Double]( kids.length )
  val actionProbabilitySums = new Array[Double]( kids.length )
  var reachProbabilitySum   = 0.0


  //--------------------------------------------------------------------------------------------------------------------
  def positiveRegretStrategy() : Seq[Double] =
  {
    val cumRegret = positiveCumulativeCounterfactualRegret()

    if (cumRegret <= 0)
    {
      Seq.fill(kids.size)(1.0 / kids.size)
    }
    else
    {
      positiveCounterfactualRegret().map(_ / cumRegret)
    }
  }

  private def positiveCounterfactualRegret() : Seq[Double] =
    regretSum.map(math.max(0, _))

  private def positiveCumulativeCounterfactualRegret() : Double =
    positiveCounterfactualRegret().sum


  //--------------------------------------------------------------------------------------------------------------------
  def update(counterfactualRegret: Seq[Double], reachProbability: Double)
  {
    val currentPositiveRegretStrategy = positiveRegretStrategy()
    for (action <- 0 until counterfactualRegret.size) {
      actionProbabilitySums( action ) += reachProbability * currentPositiveRegretStrategy( action )
    }

    reachProbabilitySum += reachProbability

    for (action <- 0 until counterfactualRegret.size) {
      regretSum( action ) += counterfactualRegret( action )
    }

    visitCount += 1
  }


  //--------------------------------------------------------------------------------------------------------------------
  def averageStrategy(): Seq[Double] =
    actionProbabilitySums.map(_ / reachProbabilitySum)
}


//----------------------------------------------------------------------------------------------------------------------
class TerminalNode(val outcome : Seq[Double]/*ExpectedValue*/)
    extends PlayerViewNode
    with    NonProponent
    with    NonOpponent
{

}