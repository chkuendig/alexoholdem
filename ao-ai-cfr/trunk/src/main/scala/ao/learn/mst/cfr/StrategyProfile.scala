package ao.learn.mst.cfr

import ao.learn.mst.gen2.info.{InformationSet, InformationSetIndex}
import scala.{Int, Double}


/**
 * Date: 07/04/12
 * Time: 10:36 PM
 */
class StrategyProfile(
    private val informationSetIndex : InformationSetIndex)
{
  //--------------------------------------------------------------------------------------------------------------------
  private val epsilon: Double = 1e-7


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


  def visitCount(informationSet: InformationSet): Long =
    visitCount(informationSetIndex.indexOf( informationSet ))

  def reachProbabilitySum(informationSet: InformationSet): Double =
    reachProbabilitySum(informationSetIndex.indexOf( informationSet ))

  private def getRegretSums(informationSet: InformationSet): Seq[Double] =
    regretSums(informationSetIndex.indexOf( informationSet ))

  private def getActionProbabilitySums(informationSet: InformationSet): Seq[Double] =
    actionProbabilitySums(informationSetIndex.indexOf( informationSet ))


  //--------------------------------------------------------------------------------------------------------------------
  private def isInformationSetInitialized(informationSet: InformationSet) : Boolean =
    isInformationSetInitialized( informationSetIndex.indexOf(informationSet) )

  private def isInformationSetInitialized(informationSet: Int) : Boolean =
    regretSums( informationSet ) != null


  private def childCount(informationSet: InformationSet): Int =
    childCount( informationSetIndex.indexOf(informationSet) )

  private def childCount(informationSet: Int): Int =
    regretSums( informationSet ).length


  private def initializeInformationSetIfRequired(informationSet: Int, childCount: Int)
  {
    if (! isInformationSetInitialized( informationSet )) {
      regretSums( informationSet ) = new Array[Double](childCount)
      actionProbabilitySums( informationSet ) = new Array[Double](childCount)
    }
  }
  
  
  //--------------------------------------------------------------------------------------------------------------------
  def positiveRegretStrategy(informationSet: InformationSet, childCount: Int) : Seq[Double] =
    positiveRegretStrategy( informationSetIndex.indexOf(informationSet), childCount )

  // verified against Leduc CFR train.get_probability (line 450)
  private def positiveRegretStrategy(informationSet: Int, childCount: Int) : Seq[Double] =
  {
    initializeInformationSetIfRequired(informationSet, childCount)

    val positiveCounterfactualRegret: Seq[Double] =
      regretSums(informationSet).map(math.max(0, _))

    val positiveRegretSum =
      positiveCounterfactualRegret.sum

    if (positiveRegretSum <= epsilon)
    {
      Seq.fill(childCount)(1.0 / childCount)
    }
    else
    {
      positiveCounterfactualRegret.map(_ / positiveRegretSum)
    }
  }


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
      // Corresponds to line 682 in train.cpp,
      //  over there the addend is something called "delta_regret".
      regretSums( informationSet )( action ) += counterfactualRegret( action )
    }

    visitCount( informationSet ) += 1
  }


  //--------------------------------------------------------------------------------------------------------------------
  def averageStrategy(informationSet : InformationSet, childCount: Int): Seq[Double] =
    averageStrategy(
      informationSetIndex.indexOf(informationSet),
      childCount)

  private def averageStrategy(informationSet : Int, childCount: Int): Seq[Double] = {
    initializeInformationSetIfRequired(informationSet, childCount)
    
    actionProbabilitySums( informationSet ).map(_ / reachProbabilitySum( informationSet ))
  }


  //--------------------------------------------------------------------------------------------------------------------
  override def toString: String =
  {
    var buffer = ""
    for (informationSet <- informationSetIndex.informationSets)
    {
      buffer += informationSet
      
      if (isInformationSetInitialized( informationSet ))
      {
        val informationSetStrategy =
          averageStrategy(
            informationSet,
            childCount(informationSet) )

        val strategyDescription =
          (for (action <- informationSetIndex.actionsOf(informationSet).toList)
            yield {
              val probabilityPercentage =
                100 * informationSetStrategy( action.index )

              action + " %" + probabilityPercentage.formatted("%.3f")
            }
          ).mkString(", ")

        val informationSetRegretSums = getRegretSums( informationSet )
        val informationSetActionProbabilitySums = getActionProbabilitySums( informationSet )

        buffer +=
          ":\t" + strategyDescription +
          " | regret sums = " + informationSetRegretSums.mkString("/") +
          " | act pob sums = " + informationSetActionProbabilitySums.mkString("/")
      }
      else
      {
        buffer += ":\tNot calculated"
      }

      buffer += " | visits = " + visitCount( informationSet ) +
                " / reach sum = " + reachProbabilitySum( informationSet )

      buffer += "\n"
    }

    buffer
  }
}
