/**
 *
 */
package indago.ui.progress;

/**
 * @author jug
 */
public interface ProgressListener {

	public void resetProgress( String mesage, int maxProgress );

	public void setTotalProgressSteps( int maxProgress );

	public void hasProgressed();

	public void hasProgressed( String message );

	public void hasCompleted();
}
