package ao.learn.mst.gen

import ao.learn.mst.gen2.solve.ExtensiveUtilityRange

/**
 * http://en.wikipedia.org/wiki/Extensive-form_game
 * "Following the presentation from Hart (1992), an n-player extensive-form game thus consists of the following:"
 *
 *
 * http://poker.cs.ualberta.ca/publications/abourisk.msc.pdf
 *  2.1.1 Definitions -> Definition 1 [19, p. 200]
 *    a finite extensive game with imperfect information has the following components...
 */
trait ExtensiveGameX
{
  //--------------------------------------------------------------------------------------------------------------------
  /** "A finite set of n (rational) players" */
  def rationalPlayerCount : Int

  /** Utility range for each (rational) player */
  def utilityRanges : Seq[ExtensiveUtilityRange]

  /** "A rooted tree, called the game tree" */
  def gameTreeRoot : ExtensiveGameNodeX
}