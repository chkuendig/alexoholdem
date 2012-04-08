package ao.learn.mst.cfr

import ao.learn.mst.gen2.info.{InformationSet, InformationSetIndex}


/**
 * Date: 07/04/12
 * Time: 10:36 PM
 */
class StrategyProfile(
    private val informationSetIndex : InformationSetIndex)
{
  //--------------------------------------------------------------------------------------------------------------------
  private val visitCount            = new Array[Long         ]( informationSetIndex.informationSetCount )
  private val regretSums            = new Array[Array[Double]]( informationSetIndex.informationSetCount )
  private val actionProbabilitySums = new Array[Array[Double]]( informationSetIndex.informationSetCount )
  private val reachProbabilitySum   = new Array[Double       ]( informationSetIndex.informationSetCount )
  
  
  //--------------------------------------------------------------------------------------------------------------------
//  var visitCount            = 0
//  val regretSums            = new Array[Double]( kids.length )
//  val actionProbabilitySums = new Array[Double]( kids.length )
//  var reachProbabilitySum   = 0.0


  //--------------------------------------------------------------------------------------------------------------------
  private def isInformationSetInitialized(informationSet: InformationSet) : Boolean =
    isInformationSetInitialized( informationSetIndex.indexOf(informationSet) )
  private def isInformationSetInitialized(informationSet: Int) : Boolean =
    (regretSums( informationSet ) != null)

  private def childCount(informationSet: InformationSet): Int =
    childCount( informationSetIndex.indexOf(informationSet) )
  private def childCount(informationSet: Int): Int =
    regretSums( informationSet ).length

  private def initializeInformationSet(informationSet: Int, childCount: Int)
  {
    if (! isInformationSetInitialized( informationSet )) {
      regretSums( informationSet ) = new Array[Double](childCount)
      actionProbabilitySums( informationSet ) = new Array[Double](childCount)
    }
  }
  
  
  //--------------------------------------------------------------------------------------------------------------------
  def positiveRegretStrategy(informationSet: InformationSet, childCount: Int) : Seq[Double] =
    positiveRegretStrategy( informationSetIndex.indexOf(informationSet), childCount )
  
  private def positiveRegretStrategy(informationSet: Int, childCount: Int) : Seq[Double] =
  {
    initializeInformationSet(informationSet, childCount)
    
    val cumRegret = positiveCumulativeCounterfactualRegret(informationSet)

    if (cumRegret <= 0)
    {
      Seq.fill(childCount)(1.0 / childCount)
    }
    else
    {
      positiveCounterfactualRegret(informationSet).map(_ / cumRegret)
    }
  }

  private def positiveCounterfactualRegret(informationSet: Int) : Seq[Double] =
    regretSums( informationSet ).map(math.max(0, _))

  private def positiveCumulativeCounterfactualRegret(informationSet: Int) : Double =
    positiveCounterfactualRegret(informationSet).sum


  //--------------------------------------------------------------------------------------------------------------------
  def update(
      informationSet       : InformationSet,
      counterfactualRegret : Seq[Double],
      reachProbability     : Double)
  {
    update(informationSetIndex.indexOf(informationSet),
           counterfactualRegret,
           reachProbability)
  }
  
  private def update(
      informationSet       : Int,
      counterfactualRegret : Seq[Double],
      reachProbability     : Double)
  {
    val currentPositiveRegretStrategy =
      positiveRegretStrategy( informationSet, counterfactualRegret.length )
    
    for (action <- 0 until counterfactualRegret.size) {
      actionProbabilitySums( informationSet )( action ) +=
        reachProbability * currentPositiveRegretStrategy( action )
    }

    reachProbabilitySum( informationSet ) += reachProbability

    for (action <- 0 until counterfactualRegret.size) {
      regretSums( informationSet )( action ) += counterfactualRegret( action )
    }

    visitCount( informationSet ) += 1
  }


  //--------------------------------------------------------------------------------------------------------------------
  def averageStrategy(informationSet : InformationSet, childCount: Int): Seq[Double] =
    averageStrategy( informationSetIndex.indexOf(informationSet), childCount )

  private def averageStrategy(informationSet : Int, childCount: Int): Seq[Double] = {
    initializeInformationSet(informationSet, childCount)
    
    actionProbabilitySums( informationSet ).map(_ / reachProbabilitySum( informationSet ))
  }


  //--------------------------------------------------------------------------------------------------------------------
  override def toString: String =
  {
    val buffer = new StringBuilder
    for (informationSet <- informationSetIndex.informationSets)
    {
      buffer.append(informationSet)
      
      if (isInformationSetInitialized( informationSet ))
      {
        buffer.append(": ")
              .append(averageStrategy( informationSet, childCount(informationSet) ))
      }
      else
      {
        buffer.append(": Not calculated")
      }
      
      buffer.append("\n")
    }

    buffer.toString()
  }
}
