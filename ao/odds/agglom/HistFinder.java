package ao.odds.agglom;

import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;

/**
 * Created by IntelliJ IDEA.
 * User: iscott
 * Date: Sep 30, 2008
 * Time: 12:29:11 PM
 * To change this template use File | Settings | File Templates.
 */
public interface HistFinder
{
    public OddHist compute(Hole hole,
                           Community community,
                           int       activeOpponents);
}
