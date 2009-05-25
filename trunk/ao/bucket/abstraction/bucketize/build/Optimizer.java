package ao.bucket.abstraction.bucketize.build;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

import java.io.File;

/**
 * User: Cross Creek Marina
 * Date: 24-May-2009
 * Time: 8:18:36 PM
 */
public class Optimizer
{
    public static void main(String[] args) {
        try {
            // Create a problem with 4 variables and 0 constraints
            LpSolve solver = LpSolve.makeLp(0, 4);

            solver.setBinary();

            // add constraints
            solver.strAddConstraint("3 2 2 1", LpSolve.LE, 4);
            solver.strAddConstraint("0 4 3 1", LpSolve.GE, 3);

            // set objective function
            solver.strSetObjFn("2 3 -2 3");

            // solve the problem
            solver.solve();

            // print solution
            System.out.println("Value of objective function: " + solver.getObjective());
            double[] var = solver.getPtrVariables();
            for (int i = 0; i < var.length; i++) {
                System.out.println("Value of var[" + i + "] = " + var[i]);
            }

            // delete the problem and free memory
            solver.deleteLp();
        }
        catch (LpSolveException e) {
            e.printStackTrace();
        }
    }


    //--------------------------------------------------------------------
    private static final String GLPK_COMMAND =
            "lib/glpk/glpsol";


    //--------------------------------------------------------------------
    // Uses pure integer proramming to optimize the best combination
    //  of sub-bucket counts.  I.e. minimizing the sum of errors.
    //
    // the contents of the output are in the form
    //   1 .. n (i.e. one based)
    public byte[] optimize(
            double errors[][],
            char   numBuckets)
    {

        return null;
    }


    //--------------------------------------------------------------------
    private void forumateProblem(
            File   into,
            double errors[][],
            char   numBuckets)
    {
        
    }
}
