package ao.holdem.ai.abstraction.bucketize.build;

import ao.util.time.Stopwatch;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * Date: 24-May-2009
 * Time: 8:18:36 PM
 */
public class Optimizer
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(Optimizer.class);

    public static void main(String[] args) {
//        double errors[][] =
//                {{3, 2, 1},
//                 {4, 3, 2},
//                 {5, 4, 3.1}};

        int holes        = 5;
        int flopsPerHole = 5;

        double errors[][] = new double[holes][flopsPerHole * 2];
        for (int i = 0; i < errors.length; i++)
        {
            for (int j = 0; j < errors[i].length; j++)
            {
                errors[i][j] = Math.random() * 10;
            }
        }

        long before     = System.currentTimeMillis();
        int  solution[] = optimize(errors, holes * flopsPerHole);
        LOG.info(
                "took " + (System.currentTimeMillis() - before));
        LOG.info(Arrays.toString(solution));
    }


    //-------------------------------------------------------------------
    // Uses pure integer programming to optimize the best combination
    //  of sub-bucket counts.  I.e. minimizing the sum of errors.
    //
    // the contents of the output are in the form
    //   1 .. n (i.e. one based)
    public static int[] optimize(
            int    parentBucketReachPaths[],
            int    subBucketCounts       [][],
            double subBucketingErrors    [][],
            int    nBuckets)
    {
        LpSolve solver = null;
        try
        {
            solver = LpSolve.makeLp(0, 0);
            solver.setVerbose(LpSolve.IMPORTANT);

            return doOptimize(solver,
                              parentBucketReachPaths,
                              subBucketCounts,
                              subBucketingErrors,
                              nBuckets);
        }
        catch (LpSolveException e)
        {
            throw new Error( e );
        }
        finally
        {
            if (solver != null) {
                solver.deleteLp();
            }
        }
    }

    private static int[] doOptimize(
            LpSolve solver,
            int     parentBucketReachPaths[],
            int     subBucketCounts       [][],
            double  subBucketingErrors    [][],
            int     nBuckets) throws LpSolveException
    {
        LOG.debug("optimizing for " + nBuckets);
        Stopwatch timer = new Stopwatch();

        validate(parentBucketReachPaths,
                 subBucketCounts,
                 subBucketingErrors);

        int nVars = 0;
        for (double[] bucketErr : subBucketingErrors) {
            nVars += bucketErr.length;
        }

        // add variables, objective function is sum of errors (WORKS)
        for (int i = 0; i < subBucketingErrors.length; i++)
        {
            for (double err : subBucketingErrors[i])
            {
                solver.addColumn(new double[]{
                        parentBucketReachPaths[i] * err});
            }
        }

        for (int i = 1; i <= nVars; i++) {
            solver.setInt(i, true);
        }

        // total buckets <= nBuckets (WORKS)
        double totalBuckets[]        = new double[nVars + 1];
        int    nextBucketGranularity = 0;
        for (int[] subBucketCount : subBucketCounts) {
            for (int count : subBucketCount) {
                totalBuckets[ ++nextBucketGranularity ] = count;
            }
        }
        solver.addConstraint(totalBuckets, LpSolve.LE, nBuckets);

        // offsets into matrix
        int cumulativeOffset   = 0;
        int subBucketOffsets[] = new int[ subBucketCounts.length ];
        for (int i = 0; i < (subBucketCounts.length - 1); i++) {
            cumulativeOffset += subBucketCounts[i].length;
            subBucketOffsets[ i + 1 ] = cumulativeOffset;
        }

        // for each parent bucket, only one sub-bucketing (FAILS)
        for (int i = 0; i < subBucketCounts.length; i++) {
            double singleBucketing[] = new double[nVars + 1];
            for (int j = 0; j < subBucketCounts[i].length; j++) {
                singleBucketing[ subBucketOffsets[i] + j + 1 ] = 1;
            }
            solver.addConstraint(singleBucketing, LpSolve.EQ, 1);
        }

        // set objective function
//        solver.strSetObjFn("2 3 -2 3");

        solver.setMinim();

//        solver.setScaling(LpSolve.SCALE_GEOMETRIC |
//                          LpSolve.SCALE_EQUILIBRATE |
//                          LpSolve.SCALE_INTEGERS);

        // PRESOLVE_REDUCEGCD PRESOLVE_PROBEFIX
        //   PRESOLVE_PROBEREDUCE PRESOLVE_IMPLIEDSLK PRESOLVE_SENSDUALS
//        solver.setPresolve(
//                LpSolve.PRESOLVE_SENSDUALS,
//                solver.getPresolveloops());
        solver.solve();

        int    solution[] = new int[ parentBucketReachPaths.length ];
        double var     [] = solver.getPtrVariables();
//        System.out.println(Arrays.toString(var));

        int nextVar = 0;
        for (int i = 0; i < subBucketCounts.length; i++) {
            for (int j = 0; j < subBucketCounts[i].length; j++) {
                solution[ i ] += subBucketCounts[i][j] * var[ nextVar++ ];
            }
        }

//        LOG.debug(
//                "Value of objective function: " + solver.getObjective());
        LOG.debug("done, took " + timer);
        return solution;
    }


    //--------------------------------------------------------------------
    public static int[] optimize(
            double subBucketingErrors[][],
            int    nBuckets)
    {
        int parentBucketReachPaths[] =
                new int[ subBucketingErrors.length ];

        for (int i = 0; i < parentBucketReachPaths.length; i++)
        {
            parentBucketReachPaths[i] = 1;
        }

        return optimize(parentBucketReachPaths,
                        subBucketingErrors,
                        nBuckets);
    }
    public static int[] optimize(
            int    parentBucketReachPaths[],
            double subBucketingErrors    [][],
            int    nBuckets)
    {
        int subBucketCounts[][] =
                new int[ subBucketingErrors.length ][];

        for (int i = 0; i < parentBucketReachPaths.length; i++)
        {
            subBucketCounts[i] = new int[ subBucketingErrors[i].length ];
            for (byte j = 0; j < subBucketingErrors[i].length; j++)
            {
                subBucketCounts[i][j] = (byte)(j + 1);
            }
        }

        return optimize(parentBucketReachPaths,
                        subBucketCounts,
                        subBucketingErrors,
                        nBuckets);
    }

    private static void validate(
            int    parentBucketReachPaths[],
            int    subBucketCounts[][],
            double subBucketingErrors[][])
    {
        assert parentBucketReachPaths.length == subBucketCounts.length &&
               parentBucketReachPaths.length == subBucketingErrors.length;

        for (int i = 0; i < parentBucketReachPaths.length; i++)
        {
            assert subBucketCounts[i].length == subBucketingErrors[i].length;
        }
    }
}
