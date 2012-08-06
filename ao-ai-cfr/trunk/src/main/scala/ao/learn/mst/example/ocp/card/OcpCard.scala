package ao.learn.mst.example.ocp.card

/**
 * Date: 20/09/11
 * Time: 12:10 PM
 */
object OcpCard extends Enumeration
{
  type OcpCard = Value

  val Jack = Value("J")
  val Queen = Value("Q")
  val King = Value("K")
}