package ao.learn.mst.gen3.strategy.impl

import ao.learn.mst.gen3.strategy.{ExtensiveStrategyProfile, CfrStrategyProfile}
import ao.learn.mst.gen3.ProbabilityMass

/**
 * 01/06/13 6:08 PM
 */
class ArrayCfrStrategyProfile(private val informationSetCount:Int)
    extends CfrStrategyProfile
{
  //--------------------------------------------------------------------------------------------------------------------
  private val epsilon: Double = 1e-7


  //--------------------------------------------------------------------------------------------------------------------
  private val regretSums            = new Array[Array[Double]]( informationSetCount )
  private val actionProbabilitySums = new Array[Array[Double]]( informationSetCount )
  private val reachProbabilitySum   = new Array[Double       ]( informationSetCount )


  //--------------------------------------------------------------------------------------------------------------------
  def toExtensiveStrategyProfile: ExtensiveStrategyProfile = {
    val probabilities:Seq[Seq[Double]] = Seq() ++
      (0 until informationSetCount).map(averageStrategyOrEmpty(_))

    SeqExtensiveStrategyProfile(probabilities)
  }

  private def averageStrategyOrEmpty(informationSetIndex : Int): Seq[Double] = {
    if (isInformationSetInitialized(informationSetIndex)) {
      averageStrategy(informationSetIndex)
    } else {
      Seq.empty
    }
  }

  // Corresponds to train.cpp get_normalized_average_probability line 489
  private def averageStrategy(informationSetIndex : Int): Seq[Double] = {
    // In train.cpp, see the following relevant (lines 504 .. 509):
    //  /* compute sum */
    //  double sum = 0;
    //  for(int i=0; i<leduc::NUM_ACTIONS; ++i) {
    //
    //    sum += average_probability[u.get_id()][bucket][i];
    //  }
    //
    // Where average_probability is the equivalent of actionProbabilitySums.
    //
    // Then this sum is used to normalize relative to siblings (train.cpp line 516):
    //  probability[i] = average_probability[u.get_id()][bucket][i]/sum;

    // Here we are dividing by sum of reach probabilities for the information set.
    // note: should it be normalized in relation to siblings instead? (would that make a difference?)

    actionProbabilitySums(informationSetIndex)
      .map(_ / reachProbabilitySum(informationSetIndex))
  }


  //--------------------------------------------------------------------------------------------------------------------
  def positiveRegretStrategy(informationSetIndex: Int, actionCount: Int): ProbabilityMass = {
    val positiveRegretStrategy: Seq[Double] =
      if (isInformationSetInitialized(informationSetIndex)) {
        definedPositiveRegretStrategy(informationSetIndex, actionCount)
      } else {
        defaultRegretStrategy(actionCount)
      }

    ProbabilityMass(positiveRegretStrategy)
  }

  // verified against Leduc CFR train.get_probability (line 450)
  private def definedPositiveRegretStrategy(informationSet: Int, actionCount: Int) : Seq[Double] =
  {
    // Corresponds to train.cpp:
    // 465: /* compute sum of positive regret */
    // 466: double sum = 0;
    // 467: for(int i=0; i<leduc::NUM_ACTIONS; ++i) {
    // 468:
    // 469:   sum += max(0., regret[u.get_id()][bucket][i]);
    // 470: }
    val positiveCounterfactualRegret: Seq[Double] =
      regretSums(informationSet).map(math.max(0.0, _))

    val positiveRegretSum:Double =
      positiveCounterfactualRegret.sum

    // compute probability as the proportion of positive regret
    if (positiveRegretSum > epsilon)
    {
      positiveCounterfactualRegret
        .map(_ / positiveRegretSum)
        .padTo(actionCount, 0.0)
    }
    else
    {
      defaultRegretStrategy(actionCount)
    }
  }

  private def defaultRegretStrategy(actionCount: Int):Seq[Double] =
    Seq.fill(actionCount)(1.0 / actionCount)


  //--------------------------------------------------------------------------------------------------------------------
  private def isInformationSetInitialized(informationSetIndex:Int):Boolean =
    informationSetIndex < informationSetCount &&
      regretSums(informationSetIndex) != null


  //--------------------------------------------------------------------------------------------------------------------
  def update(
      informationSetIndex: Int,
      actionRegret: Seq[Double],
      reachProbability: Double)
  {
    val actionCount = actionRegret.length

    initializeInformationSetIfRequired(
      informationSetIndex, actionCount)

    val currentPositiveRegretStrategy: ProbabilityMass =
      positiveRegretStrategy(informationSetIndex, actionCount)

    // See train.cpp (average the strategy for the player):
    // 651: for(int i=0; i<3; ++i) {
    // 652:
    // 653:   average_probability[i] += reach[player]*probability[i];
    // 654: }
    for (action <- 0 until actionCount)
    {
      actionProbabilitySums( informationSetIndex )( action ) +=
        reachProbability * currentPositiveRegretStrategy.probabilities( action )
    }

    // technically not necessary because we can weigh
    //  by sum of parent's child (i.e. sibling + self)
    reachProbabilitySum( informationSetIndex ) += reachProbability

    val counterfactualRegret =
      actionRegret.map(_ * reachProbability)

    for (action <- 0 until actionCount) {
      // Corresponds to line 682 in train.cpp,
      //  over there the addend is called "delta_regret".
      regretSums( informationSetIndex )( action ) +=
        counterfactualRegret( action )

      // note that the explanation on:
      //  http://pokerai.org/pf3/viewtopic.php?f=3&t=2662
      // suggests that it should be:
      //   math.max(0, actionRegret( action ))
      // which appears to be incorrect.
    }
  }

  private def initializeInformationSetIfRequired(
      informationSetIndex: Int, actionCount: Int)
  {
    if (informationSetIndex < 0 || informationSetIndex > informationSetCount) {
      throw new IllegalArgumentException(
        "Index " + informationSetIndex + " out of range out of " + informationSetCount)
    }

    if (! isInformationSetInitialized(informationSetIndex)) {
      actionProbabilitySums(informationSetIndex) = new Array[Double](actionCount)
      regretSums(informationSetIndex) = new Array[Double](actionCount)
    } else {
      val currentActionCount = actionProbabilitySums(informationSetIndex).length
      if (currentActionCount != actionCount) {
        throw new IllegalArgumentException("Mismatched action count: " +
          informationSetIndex + " | " + currentActionCount + " vs " + actionCount)
      }
    }
  }
}
