package ao.learn.mst.cfr

import ao.learn.mst.gen.ExtensiveGameNodeX
import ao.learn.mst.gen.history.{ExtensiveHistory, ExtensiveInformationPartition}


//----------------------------------------------------------------------------------------------------------------------
class CfrMinimizer
{
  //--------------------------------------------------------------------------------------------------------------------
  def walkTree(root : ExtensiveGameNodeX)
  {
    walkTree(
      PlayerViewBuilder.expand(root, 0),
      PlayerViewBuilder.expand(root, 1),
      new JointBucketSequence,
      1.0,
      1.0)
  }

  def walkTree(
      firstPlayerNode             : PlayerViewNode,
      lastPlayerNode              : PlayerViewNode,
      joinBucketSequence          : JointBucketSequence,
      firstPlayerReachProbability : Double,
      lastPlayerReachProbability  : Double)
  {
    firstPlayerNode match {
      case _ : DecisionNode =>
        walkProponent(
          firstPlayerNode,
          lastPlayerNode,
          joinBucketSequence,
          firstPlayerReachProbability,
          lastPlayerReachProbability)

      case bucket : ChanceNode => {

      }

      case terminal : TerminalNode => {

      }
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  private def walkProponent(
      firstPlayerNode             : PlayerViewNode,
      lastPlayerNode              : PlayerViewNode,
      joinBucketSequence          : JointBucketSequence,
      firstPlayerReachProbability : Double,
      lastPlayerReachProbability  : Double)
  {
//    val (opponent, proponent) =
//          List(firstPlayerNode, lastPlayerNode)
//            .sortBy(_.isInstanceOf[ProponentNode])

//    val proponentInformationPartition : ExtensiveInformationPartition =
//      ExtensiveInformationPartition(ExtensiveHistory(
//        ))
  }
}