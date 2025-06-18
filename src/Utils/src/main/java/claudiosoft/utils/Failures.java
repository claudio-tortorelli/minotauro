package claudiosoft.utils;

/**
 *
 * @author claudio.tortorelli
 */
public class Failures {

    private static int nFailures = 0;

    public static int getFailures() {
        return nFailures;
    }

    public static synchronized void addFailure() {
        nFailures++;
    }
}
