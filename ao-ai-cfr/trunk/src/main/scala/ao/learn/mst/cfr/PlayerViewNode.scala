package ao.learn.mst.cfr

/**
 * http://poker.cs.ualberta.ca/publications/NIPS07-cfr.pdf
 * Section A.4
 */

//----------------------------------------------------------------------------------------------------------------------
sealed abstract class PlayerViewNode

sealed trait NonProponent
sealed trait NonOpponent


//----------------------------------------------------------------------------------------------------------------------
class ChanceNode extends PlayerViewNode with NonProponent with NonOpponent
{
  def kids : Seq[DecisionNode] = null
}


//----------------------------------------------------------------------------------------------------------------------
class DecisionNode extends PlayerViewNode
{

}


//----------------------------------------------------------------------------------------------------------------------
class OpponentNode extends DecisionNode with NonProponent
{
  def kids : Seq[NonOpponent] = null
}


//----------------------------------------------------------------------------------------------------------------------
class ProponentNode extends DecisionNode with NonOpponent
{
  var visitCount : Int = 0

  val regretSum : Seq[Double] = null

  val reachProbabilitySums : Seq[Double] = null
  val totalReachProbability : Double = 0

  def kids : Seq[NonProponent] = null
}


//----------------------------------------------------------------------------------------------------------------------
class TerminalNode extends PlayerViewNode with NonProponent with NonOpponent
{

}