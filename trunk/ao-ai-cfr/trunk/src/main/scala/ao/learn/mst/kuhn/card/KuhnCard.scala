package ao.learn.mst.kuhn.card

/**
 * Date: 20/09/11
 * Time: 12:10 PM
 */
object KuhnCard extends Enumeration
{
  type KuhnCard = Value

  val Queen = Value("Q")
  val King  = Value("K")
  val Ace   = Value("A")
}