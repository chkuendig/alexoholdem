package ao;

import org.apache.log4j.Logger;

/**
 * User: alex
 * Date: 18-Jun-2009
 * Time: 12:00:36 AM
 */
public class Infrastructure
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(Infrastructure.class);

    private Infrastructure() {}


    //--------------------------------------------------------------------
    private static String workingDirectory = "";


    //--------------------------------------------------------------------
    public static void setWorkingDirectory(
            String newWorkingDirectory)
    {
        workingDirectory = newWorkingDirectory;
    }

    /**
     * @return can be used like:
     *          new File(workingDirectory() + "relative/path");
     */
    public static String workingDirectory()
    {
        return workingDirectory;
    }

    public static String path(String relativeComponent)
    {
        return workingDirectory + relativeComponent;
    }
}
