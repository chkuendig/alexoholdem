package ao.learn.mst.gen2.info

/**
 * User: ao
 * Date: 07/04/12
 * Time: 7:52 PM
 */

trait InformationSetIndex
{
  //--------------------------------------------------------------------------------------------------------------------
  def informationSetCount: Int

  def indexOf(informationSet: InformationSet): Int

  def informationSets: Traversable[InformationSet]
}
