import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.mpcmaid.gui.BaseFrame;
import com.mpcmaid.gui.MainFrame;
import com.mpcmaid.gui.Preferences;

/**
 * Dummy main class with short name
 * 
 * @author cyrille martraire
 */
public final class MPCMaid {

	// splash screen
	private static JWindow screen = null;

	public static void showSplash() {
		screen = new JWindow();
		final URL resource = MainFrame.class.getResource("mpcmaidlogo400_400.png");
		screen.getContentPane().add(new JLabel(new ImageIcon(resource)));
		screen.setLocationRelativeTo(null);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = screen.getPreferredSize();
		screen.setLocation((int) -40, -20);
		screen
				.setLocation(screenSize.width / 2 - (labelSize.width / 2), screenSize.height / 2
						- (labelSize.height / 2));

		screen.pack();
		screen.setVisible(true);
	}

	public static void hideSplash() {
		if (screen == null) {
			return;
		}
		screen.setVisible(false);
		screen = null;
	}

	public static void main(String[] args) {
		makeAsNativeAsPossible();
		showSplash();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				final BaseFrame baseFrame = new MainFrame();

				// wait to show the splash
				if (screen != null) {
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				//baseFrame.setVisible(true);
				baseFrame.show();

				hideSplash();
			}
		});
	}

	public static final boolean isMacOsX() {
		return System.getProperty("mrj.version") != null;
	}

	public static final void makeAsNativeAsPossible() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		try {
			if (isMacOsX()) {
				System.setProperty("apple.laf.useScreenMenuBar", "true");
				System.setProperty("com.apple.mrj.application.apple.menu.about.name", "MPC Maid");
			}
		} catch (Exception e) {
		}
		try {
			Preferences.getInstance().load();
		} catch (Exception ignore) {
		}
	}

	public String toString() {
		return "MPCMaid";
	}

}
