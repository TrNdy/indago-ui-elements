/**
 *
 */
package indago.ui.progress;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * @author jug
 */
public class DialogProgress extends JDialog implements ActionListener, ProgressListener {

	private static final long serialVersionUID = 2961170560625243194L;

	private JButton bHide;

	private int maxProgress;
	private JProgressBar progressBar;

	private final String message;
	private JLabel lblMessage;

	public DialogProgress( final JComponent parent, final String message, final int totalProgressNotificationsToCome ) {
		super( SwingUtilities.windowForComponent( parent ), "Progress..." );
		this.dialogInit();
		this.setModal( false );

		this.message = message;
		this.maxProgress = totalProgressNotificationsToCome;

		buildGui();
		this.pack();

		setKeySetup();
	}

	@Override
	public void setVisible( final boolean show ) {
		final int width = 500;
		final int x = super.getParent().getX() + super.getParent().getWidth() / 2 - width / 2;
		final int y = super.getParent().getY() + super.getParent().getHeight() / 2 - this.getHeight() / 2;
		this.setBounds( x, y, width, this.getHeight() );

		super.setVisible( show );
	}

	private void buildGui() {
		this.rootPane.setLayout( new BorderLayout() );

		lblMessage = new JLabel( this.message );
		lblMessage.setBorder( BorderFactory.createEmptyBorder( 5, 15, 0, 15 ) );
		progressBar = new JProgressBar( 0, this.maxProgress );
		progressBar.setBorder( BorderFactory.createEmptyBorder( 5, 15, 5, 15 ) );

		bHide = new JButton( "hide" );
		bHide.addActionListener( this );
		this.rootPane.setDefaultButton( bHide );
		final JPanel buttonHelper = new JPanel( new FlowLayout( FlowLayout.CENTER, 15, 0 ) );
		buttonHelper.add( bHide );

		this.rootPane.add( lblMessage, BorderLayout.NORTH );
		this.rootPane.add( progressBar, BorderLayout.CENTER );
		this.rootPane.add( buttonHelper, BorderLayout.SOUTH );
	}

	private void setKeySetup() {
		this.rootPane.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put( KeyStroke.getKeyStroke( "ESCAPE" ), "closeAction" );

		this.rootPane.getActionMap().put( "closeAction", new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed( final ActionEvent e ) {
				setVisible( false );
				dispose();
			}
		} );

	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed( final ActionEvent e ) {
		if ( e.getSource().equals( bHide ) ) {
			this.setVisible( false );
			this.dispose();
		}
	}

	/**
	 * @see indago.ui.progress.ProgressListener#hasProgressed()
	 */
	@Override
	public void hasProgressed() {
		final int newVal = progressBar.getValue() + 1;
		progressBar.setValue( newVal );
		if ( newVal >= progressBar.getMaximum() ) {
			this.setVisible( false );
		}
	}

	/**
	 * @see indago.ui.progress.ProgressListener#setTotalProgressSteps(int)
	 */
	@Override
	public void setTotalProgressSteps( final int maxProgress ) {
		this.maxProgress = maxProgress;
	}

	/**
	 * @see indago.ui.progress.ProgressListener#hasProgressed(java.lang.String)
	 */
	@Override
	public void hasProgressed( final String message ) {
		relabel( message );
		hasProgressed();
	}

	/**
	 * @see indago.ui.progress.ProgressListener#resetProgress(java.lang.String,
	 *      int)
	 */
	@Override
	public void resetProgress( final String message, final int maxProgress ) {
		relabel( message );
		this.maxProgress = maxProgress;
		progressBar.setValue( 0 );
		progressBar.setMaximum( maxProgress );
		this.setVisible( true ); // in case it was unshown before
	}

	/**
	 *
	 */
	private void relabel( final String message ) {
		try {
			final Runnable runnable = new Runnable() {

				@Override
				public void run() {
					DialogProgress.this.lblMessage.setText( message );
				}
			};
			if ( SwingUtilities.isEventDispatchThread() ) {
				runnable.run();
			} else {
				SwingUtilities.invokeAndWait( runnable );
			}
		} catch ( final InvocationTargetException e ) {
			e.printStackTrace();
		} catch ( final InterruptedException e ) {
			e.printStackTrace();
		}
	}

	/**
	 * @see indago.ui.progress.ProgressListener#hasCompleted()
	 */
	@Override
	public void hasCompleted() {
		this.setVisible( false );
		this.dispose();
	}
}
