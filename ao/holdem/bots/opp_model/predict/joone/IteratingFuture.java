package ao.holdem.bots.opp_model.predict.joone;

import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 */
public class IteratingFuture implements Future
{
    //--------------------------------------------------------------------
    private Future   PARENT;
    private Runnable DELEGET;


    //--------------------------------------------------------------------
    public IteratingFuture(Future parent, Runnable iterate)
    {
        DELEGET = iterate;
    }


    //--------------------------------------------------------------------
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        return PARENT.cancel( mayInterruptIfRunning );
    }

    public boolean isCancelled()
    {
        return PARENT.isCancelled();
    }

    public boolean isDone()
    {
        return PARENT.isDone();
    }

    public Object get()
            throws InterruptedException,
                   ExecutionException
    {
//        return get(500, TimeUnit.MILLISECONDS);
        return null;
    }

    public Object get(long timeout, TimeUnit unit)
            throws InterruptedException,
                   ExecutionException,
                   TimeoutException
    {
        return null;
    }
}
