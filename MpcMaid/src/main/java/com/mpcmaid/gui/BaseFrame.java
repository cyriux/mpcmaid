package com.mpcmaid.gui;

/*

 File: MyApp.java

 Abstract: Simple Swing app demonstrating how to use the Apple EAWT 
 APIs by way of reflection, allowing a single codebase to build and run
 on platforms without those APIs installed.

 Version: 2.0

 Disclaimer: IMPORTANT:  This Apple software is supplied to you by 
 Apple Inc. ("Apple") in consideration of your agreement to the
 following terms, and your use, installation, modification or
 redistribution of this Apple software constitutes acceptance of these
 terms.  If you do not agree with these terms, please do not use,
 install, modify or redistribute this Apple software.

 In consideration of your agreement to abide by the following terms, and
 subject to these terms, Apple grants you a personal, non-exclusive
 license, under Apple's copyrights in this original Apple software (the
 "Apple Software"), to use, reproduce, modify and redistribute the Apple
 Software, with or without modifications, in source and/or binary forms;
 provided that if you redistribute the Apple Software in its entirety and
 without modifications, you must retain this notice and the following
 text and disclaimers in all such redistributions of the Apple Software. 
 Neither the name, trademarks, service marks or logos of Apple Inc. 
 may be used to endorse or promote products derived from the Apple
 Software without specific prior written permission from Apple.  Except
 as expressly stated in this notice, no other rights or licenses, express
 or implied, are granted by Apple herein, including but not limited to
 any patent rights that may be infringed by your derivative works or by
 other works in which the Apple Software may be incorporated.

 The Apple Software is provided by Apple on an "AS IS" basis.  APPLE
 MAKES NO WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 THE IMPLIED WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS
 FOR A PARTICULAR PURPOSE, REGARDING THE APPLE SOFTWARE OR ITS USE AND
 OPERATION ALONE OR IN COMBINATION WITH YOUR PRODUCTS.

 IN NO EVENT SHALL APPLE BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL
 OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 INTERRUPTION) ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION,
 MODIFICATION AND/OR DISTRIBUTION OF THE APPLE SOFTWARE, HOWEVER CAUSED
 AND WHETHER UNDER THEORY OF CONTRACT, TORT (INCLUDING NEGLIGENCE),
 STRICT LIABILITY OR OTHERWISE, EVEN IF APPLE HAS BEEN ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.

 Copyright C 2003-2007 Apple, Inc., All Rights Reserved

 */

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import apple.dts.samplecode.osxadapter.OSXAdapter;

import com.mpcmaid.audio.SamplePlayer;

/**
 * Main Frame subclass, to be subclassed again on each action method (open,
 * saveAs.. etc)
 * 
 * @author cyrille martraire
 */
public class BaseFrame extends JFrame implements ActionListener {

	protected static int windowCounter = -1;

	protected JDialog aboutBox, prefs;

	protected JMenu fileMenu, helpMenu;

	protected JMenuItem newMI, openMI, closeMi, saveMI, saveAsMI, exportMI, optionsMI, quitMI;

	protected JMenuItem docsMI, supportMI, aboutMI;

	// Check that we are on Mac OS X. This is crucial to loading and using the
	// OSXAdapter class.
	public static boolean MAC_OS_X = (System.getProperty("os.name").toLowerCase().startsWith("mac os x"));

	// Ask AWT which menu modifier we should be using.
	protected final static int MENU_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

	public BaseFrame() {
		super("MPCMaid");
	}

	public void init() {
		initMenuBar();
		initComponents();
	}

	protected void initMenuBar() {
		JMenu fileMenu = new JMenu("File");
		JMenuBar mainMenuBar = new JMenuBar();
		mainMenuBar.add(fileMenu = new JMenu("File"));

		fileMenu.add(newMI = new JMenuItem("New"));
		newMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, MENU_MASK));
		newMI.addActionListener(this);

		fileMenu.add(openMI = new JMenuItem("Open..."));
		openMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, MENU_MASK));
		openMI.addActionListener(this);

		fileMenu.add(closeMi = new JMenuItem("Close"));
		closeMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, MENU_MASK));
		closeMi.addActionListener(this);

		fileMenu.addSeparator();
		fileMenu.add(saveMI = new JMenuItem("Save"));
		saveMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, MENU_MASK));
		saveMI.addActionListener(this);

		fileMenu.add(saveAsMI = new JMenuItem("Save As..."));
		saveAsMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.SHIFT_DOWN_MASK | MENU_MASK));
		saveAsMI.addActionListener(this);

		fileMenu.add(exportMI = new JMenuItem("Export..."));
		exportMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, MENU_MASK));
		exportMI.addActionListener(this);

		// Quit/prefs menu items are provided on Mac OS X; only add your own on
		// other platforms
		if (!MAC_OS_X) {
			fileMenu.addSeparator();
			fileMenu.add(optionsMI = new JMenuItem("Preferences"));
			optionsMI.addActionListener(this);

			fileMenu.addSeparator();
			fileMenu.add(quitMI = new JMenuItem("Quit"));
			quitMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, MENU_MASK));
			quitMI.addActionListener(this);
		}

		addMenus(mainMenuBar);

		mainMenuBar.add(helpMenu = new JMenu("Help"));
		helpMenu.add(docsMI = new JMenuItem("Online Documentation"));
		docsMI.addActionListener(this);
		// helpMenu.addSeparator();
		// helpMenu.add(supportMI = new JMenuItem("Technical Support"));
		// About menu item is provided on Mac OS X; only add your own on other
		// platforms
		if (!MAC_OS_X) {
			helpMenu.addSeparator();
			helpMenu.add(aboutMI = new JMenuItem("About MPCMaid"));
			aboutMI.addActionListener(this);
		}

		setJMenuBar(mainMenuBar);

		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				close();
			}

		});
	}

	protected void addMenus(JMenuBar mainMenuBar) {
	}

	protected void initComponents() {
		if (windowCounter++ < 0) {
			setTitle("Untitled");
		} else {
			setTitle("Untitled-" + windowCounter);
		}

		// Determine the offset value and stagger new windows
		// (with a reset every ten windows). A somewhat
		// unscientific mechanism, but it works well enough.
		int offset = 0;
		if ((windowCounter % 10) > 0) {
			offset = ((windowCounter) % 10) * 20 + 20;
			this.setLocation(new Double(getLocation().getX() + offset - 20).intValue(), new Double(getLocation().getY()
					+ offset).intValue());
		}

		setSize(900, 500);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);

		loadInitialDocument();
		makeGUI();
		makeAboutDialog();
		makePrefsDialog();

		// Set up our application to respond to the Mac OS X application menu
		registerForMacOSXEvents();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	}

	protected void loadInitialDocument() {
	}

	public void makePrefsDialog() {

	}

	public void makeAboutDialog() {

	}

	protected void makeGUI() {
		JButton play = new JButton("Play1");

		final Container contentPane = getContentPane();
		contentPane.setLayout(new FlowLayout(2));
		contentPane.add(play);
		play.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				SamplePlayer.getInstance().play(new File("clap 1.WAV"));
			}

		});
		JButton play2 = new JButton("Play2");
		contentPane.add(play2);
		play2.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				SamplePlayer.getInstance().play(new File("bullshit1.wav"));
			}

		});
	}

	// Generic registration with the Mac OS X application menu
	// Checks the platform, then attempts to register with the Apple EAWT
	// See OSXAdapter.java to see how this is done without directly referencing
	// any Apple APIs
	private void registerForMacOSXEvents() {
		if (MAC_OS_X) {
			try {
				// Generate and register the OSXAdapter, passing it a hash of
				// all the methods we wish to
				// use as delegates for various
				// com.apple.eawt.ApplicationListener methods
				OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("quit", (Class[]) null));
				OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("about", (Class[]) null));
				OSXAdapter.setPreferencesHandler(this, getClass().getDeclaredMethod("preferences", (Class[]) null));
				OSXAdapter.setFileHandler(this, getClass().getDeclaredMethod("loadFile", new Class[] { String.class }));
			} catch (Exception e) {
				System.err.println("Error while loading the OSXAdapter:");
				e.printStackTrace();
			}
		}
	}

	// General info dialog; fed to the OSXAdapter as the method to call when
	// "About OSXAdapter" is selected from the application menu
	public void about() {
		aboutBox.setLocation((int) this.getLocation().getX() + 22, (int) this.getLocation().getY() + 22);
		aboutBox.setVisible(true);
	}

	// General preferences dialog; fed to the OSXAdapter as the method to call
	// when
	// "Preferences..." is selected from the application menu
	public void preferences() {
		prefs.setLocation((int) this.getLocation().getX() + 22, (int) this.getLocation().getY() + 22);
		prefs.setVisible(true);
	}

	public void help() {
	}

	// General quit handler; fed to the OSXAdapter as the method to call when a
	// system quit event occurs
	// A quit event is triggered by Cmd-Q, selecting Quit from the application
	// or Dock menu, or logging out
	public boolean quit() {
		int option = JOptionPane.showConfirmDialog(this,
				"Close all application windows?", "Quit?", JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			dispose();
			System.exit(0);
			return true;
		} else {
			return false;
		}
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == quitMI) {
			quit();
		} else if (source == optionsMI) {
			preferences();
		} else if (source == aboutMI) {
			about();
		} else if (source == newMI) {
			newWindow();
		} else if (source == openMI) {
			open();
		} else if (source == closeMi) {
			close();
		} else if (source == saveMI) {
			save();
		} else if (source == saveAsMI) {
			saveAs();
		} else if (source == exportMI) {
			export();
		} else if (source == docsMI) {
			help();
		}
	}

	public void newWindow() {
		new BaseFrame().show();
	}

	public void open() {
	}

	public void close() {
		checkOnClose();
		setVisible(false);

		windowCounter--;
		if (windowCounter < 0) {
			System.exit(0);
		}

	}

	protected void checkOnClose() {

	}

	public void save() {
		System.out.println(getTitle());
		System.out.println("Save...");
	}

	public void saveAs() {
		System.out.println(getTitle());
		System.out.println("Save As...");
	}

	public void export() {
		System.out.println(getTitle());
		System.out.println("Export...");
	}

	public void loadFile(String path) {
		// only supported if installed via an app bundle
		final File file = new File(path);
		System.out.println("load: " + path);
	}

	public String toString() {
		return "PgmFrame";
	}
}