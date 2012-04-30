package ao.learn.mst.kuhn.card

import KuhnCard._
import ao.learn.mst.kuhn.state.KuhnPosition._


//---------------------------------------------------------------------------------------------------------------------
case class KuhnCardSequence(
    first : KuhnCard, last : KuhnCard)
{
  //--------------------------------------------------------------------------
  def hands = (first, last)


  //--------------------------------------------------------------------------
  def winner =
    if (first > last) FirstToAct else LastToAct
}