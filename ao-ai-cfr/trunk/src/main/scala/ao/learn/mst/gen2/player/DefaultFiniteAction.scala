package ao.learn.mst.gen2.player

/**
 * User: ao
 * Date: 01/05/12
 * Time: 11:10 PM
 */
class DefaultFiniteAction(index : Int) extends FiniteAction(index){
  override def toString =
    "[" + index + "]"
}

object DefaultFiniteAction
{
  def sequence(numberOfActions: Int): Set[FiniteAction] =
    (for (i <- 0 until numberOfActions)
      yield new DefaultFiniteAction(i)
    ).toSet
}