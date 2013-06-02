package ao.learn.mst.gen3.strategy

/**
 * 01/06/13 6:04 PM
 */
trait CfrStrategyBuffer
{
  def bufferUpdate(
    informationSetIndex : Int,
    actionRegret        : Seq[Double],
    reachProbability    : Double)

  def commit(
    cfrStrategyProfileBuilder : CfrStrategyProfile)
}
