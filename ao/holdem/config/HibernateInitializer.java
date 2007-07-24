package ao.holdem.config;

import com.google.inject.Inject;
import com.wideplay.warp.persist.PersistenceService;

/**
 *
 */
public class HibernateInitializer
{
    @Inject
    HibernateInitializer(PersistenceService service)
    {
		service.start();
	}
}
