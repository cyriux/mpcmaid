package com.mpcmaid.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.centerkey.utils.BareBonesBrowserLaunch;
import com.mpcmaid.pgm.Profile;
import com.mpcmaid.pgm.Program;

/**
 * Main frame class for the application.
 * 
 * @author cyrille martraire
 */
public final class MainFrame extends BaseFrame {

	private static final int FRAME_WIDTH = 900;

	private static final int FRAME_HEIGHT = 520;

	private static final String HELP_URL = "http://mpcmaid.sourceforge.net/";

	private static final String UNTITLED_TEMPLATE = "Untitled.pgm";

	private static final int FILE_MAX_LENGTH = 16;

	private static final Font SMALL_FONT = new Font("Verdana", Font.PLAIN, 8);

	private static final Font MEDIUM_FONT = new Font("Verdana", Font.PLAIN, 10);

	protected int selectedPad = 0;

	private final Program program;

	private File pgmFile;

	protected ProgramPanel programEditor;

	protected JPanel audioEditor;

	public MainFrame() {
		this(untitledProgram());
	}

	public MainFrame(File pgmFile) {
		this(Program.open(pgmFile), pgmFile);
		setPgmFile(pgmFile);
	}

	public MainFrame(Program program) {
		this(program, null);
	}

	public MainFrame(Program program, File file) {
		super();
		this.program = program;
		setPgmFile(file);
		init();
	}

	public File getPgmFile() {
		return pgmFile;
	}

	public void setPgmFile(File pgmFile) {
		if (pgmFile == null) {
			return;
		}
		this.pgmFile = pgmFile;
		setTitle(pgmFile.getName());
	}

	public int getSelectedPad() {
		return selectedPad;
	}

	public Program getProgram() {
		return program;
	}

	private final static Program untitledProgram() {
		final InputStream resourceStream = Program.class.getResourceAsStream(UNTITLED_TEMPLATE);
		final Program untitled = Program.open(resourceStream);
		return untitled;
	}

	protected void addMenus(JMenuBar mainMenuBar) {
		final JMenu editMenu = new JMenu("Edit");
		mainMenuBar.add(editMenu);
		final JMenuItem removeSamples = new JMenuItem("Remove all samples");
		editMenu.add(removeSamples);

		removeSamples.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				programEditor.removeAllSamples();
			}

		});

		final JMenuItem setChromatic = new JMenuItem("Set Chromatic Note Layout");
		editMenu.add(setChromatic);

		setChromatic.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				programEditor.setChromatic();
			}

		});

		final JMenuItem batchCreate = new JMenuItem("Batch Create Programs");
		editMenu.add(batchCreate);

		batchCreate.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				batchCreate();

			}

		});

		final JMenuItem copyPadSettings = new JMenuItem("Copy Settings to All Pads");
		editMenu.add(copyPadSettings);

		copyPadSettings.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				copySettingsToAllPads();

			}

		});

	}

	protected void makeGUI() {
		makeMain();
	}

	public void newWindow() {
		final MainFrame newFrame = new MainFrame();
		newFrame.show();
	}

	public void open() {
		final FileDialog openDialog = new FileDialog(this);
		openDialog.setDirectory(Preferences.getInstance().getOpenPath());
		openDialog.setMode(FileDialog.LOAD);
		openDialog.setFilenameFilter(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String[] supportedFiles = { "PGM", "pgm" };
				for (int i = 0; i < supportedFiles.length; i++) {
					if (name.endsWith(supportedFiles[i])) {
						return true;
					}
				}
				return false;
			}
		});
		openDialog.setVisible(true);
		Preferences.getInstance().setOpenPath(openDialog.getDirectory());
		if (openDialog.getDirectory() != null && openDialog.getFile() != null) {
			String filePath = openDialog.getDirectory() + openDialog.getFile();
			System.out.println(filePath);
			final File pgmFile = new File(filePath);
			final MainFrame newFrame = new MainFrame(pgmFile);
			newFrame.show();
		}
	}

	public void save() {
		if (pgmFile == null) {
			saveAs();
			return;
		}
		program.save(pgmFile);
	}

	public void saveAs() {
		final FileDialog saveDialog = new FileDialog(this);
		saveDialog.setDirectory(Preferences.getInstance().getSavePath());
		saveDialog.setMode(FileDialog.SAVE);
		saveDialog.setFilenameFilter(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String[] supportedFiles = { "PGM", "pgm" };
				for (int i = 0; i < supportedFiles.length; i++) {
					if (name.endsWith(supportedFiles[i])) {
						return true;
					}
				}
				return false;
			}
		});
		saveDialog.setVisible(true);
		Preferences.getInstance().setSavePath(saveDialog.getDirectory());
		String filename = saveDialog.getFile();
		if (saveDialog.getDirectory() != null && filename != null) {
			if (!filename.toUpperCase().endsWith(".PGM")) {
				filename += ".PGM";
			}
			String filePath = saveDialog.getDirectory() + filename;
			System.out.println(filePath);
			final File file = new File(filePath);
			setPgmFile(file);
			program.save(pgmFile);
		}
	}

	public void export() {
		final JFileChooser saveDialog = new JFileChooser(Preferences.getInstance().getSavePath());
		saveDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		// final FileDialog saveDialog = new FileDialog(this);
		saveDialog.setDialogType(JFileChooser.SAVE_DIALOG);
		saveDialog.setDialogTitle("Choose the directory where to save the PGM file and each sample file");

		saveDialog.showDialog(this, "Export");
		final File dir = saveDialog.getSelectedFile();
		if (dir == null) {
			return;
		}
		if (!dir.exists()) {
			dir.mkdir();
		}

		Preferences.getInstance().setSavePath(dir.getAbsolutePath());

		if (dir != null) {
			// save PGM file to the directory with the same name
			final File pgmExportFile = new File(dir, dir.getName() + ".PGM");
			System.out.println(pgmExportFile);
			setPgmFile(pgmExportFile);
			program.save(pgmFile);

			// save each sample
			programEditor.exportSamples(dir);
		}
	}

	protected void batchCreate() {
		final JFileChooser batchDialog = new JFileChooser(Preferences.getInstance().getSavePath());
		batchDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		// final FileDialog saveDialog = new FileDialog(this);
		batchDialog.setDialogType(JFileChooser.OPEN_DIALOG);
		batchDialog.setDialogTitle("Choose the base directory to browse recursively");

		batchDialog.showDialog(this, "Batch Create Program");
		final File dir = batchDialog.getSelectedFile();
		if (dir != null) {
			try {
				programEditor.batchCreate(dir);
			} catch (Exception e) {
				e.printStackTrace();// error occurred
			}
		}
	}

	protected void copySettingsToAllPads() {
		programEditor.copySettingsToAllPads();
	}

	public boolean quit() {
		return super.quit();
	}

	protected void checkOnClose() {
	}

	public void help() {
		BareBonesBrowserLaunch.openURL(HELP_URL);
	}

	public void loadFile(String path) {
	}

	public void makeAboutDialog() {
		// set up a simple about box
		aboutBox = new JDialog(this, "About MPCMaid");
		aboutBox.setSize(400, 422);
		aboutBox.setResizable(false);
		aboutBox.getContentPane().setLayout(new BorderLayout());

		try {
			final JLabel imageLabel = new JLabel();
			final BufferedImage currentImage = ImageIO.read(getClass().getResourceAsStream("mpcmaidlogo400_400.png"));
			imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
			imageLabel.setVerticalAlignment(SwingConstants.CENTER);
			imageLabel.setOpaque(true);

			imageLabel.setIcon(new ImageIcon(currentImage));
			// imageLabel.setBackground((Color)
			// colors[colorComboBox.getSelectedIndex()]);
			imageLabel.setText("");

			aboutBox.getContentPane().add(imageLabel, BorderLayout.NORTH);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// aboutBox.getContentPane().add(new JLabel("MPCMaid", JLabel.CENTER));
		final JLabel notice = new JLabel("\u00A92009 Cyrille Martraire (cyrille.martraire.com)", JLabel.CENTER);
		// notice.setPreferredSize(new Dimension(400, 25));
		// aboutBox.getContentPane().add(notice, BorderLayout.CENTER);

		// final JLabel notice2 = new JLabel("Logo design: Yunshan Xia",
		// JLabel.CENTER);
		// notice.setPreferredSize(new Dimension(400, 25));
		// aboutBox.getContentPane().add(notice2, BorderLayout.SOUTH);

	}

	public void about() {
		aboutBox.setLocation((int) this.getLocation().getX() + 250, (int) this.getLocation().getY() + 22);
		aboutBox.setVisible(true);
	}

	public void makePrefsDialog() {
		// Preferences dialog
		prefs = new JDialog(this, "MPCMaid Preferences");
		prefs.setSize(600, 200);
		prefs.setResizable(false);
		final Container contentPane = prefs.getContentPane();
		contentPane.setLayout(new BorderLayout(10, 10));

		final JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));

		// profile selection
		final JLabel profileLabel = new JLabel("<html>MPC Profile (not effective for current windows)</html>",
				JLabel.RIGHT);
		final JComboBox profileCombo = new JComboBox(
				new String[] { Profile.MPC1000.getName(), Profile.MPC500.getName() });
		profileCombo.setEditable(false);
		final Preferences preferences = Preferences.getInstance();
		final String currentProfile = preferences.getProfile().getName();
		profileCombo.setSelectedItem(currentProfile);
		profileCombo.setAlignmentX(RIGHT_ALIGNMENT);
		panel.add(profileLabel);
		panel.add(profileCombo);
		profileCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				final String profileName = (String) profileCombo.getSelectedItem();
				final Profile profile = Profile.getProfile(profileName);
				preferences.setProfile(profile);
			}

		});

		// audition mode
		final JLabel auditionLabel = new JLabel(
				"<html>Audition mode(select which sample to play when pressing a pad)</html>", JLabel.RIGHT);
		final JComboBox auditionCombo = new JComboBox(ProgramSamples.AUDITION_MODES);
		auditionCombo.setEditable(false);
		final int currentAudition = preferences.getAuditionSamples();
		auditionCombo.setSelectedIndex(currentAudition);
		auditionCombo.setAlignmentX(RIGHT_ALIGNMENT);
		panel.add(auditionLabel);
		panel.add(auditionCombo);
		auditionCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				final int audition = auditionCombo.getSelectedIndex();
				preferences.setAuditionSamples(audition);
			}

		});

		// contentPane.add(new JLabel("MPC Maid Preferences", JLabel.CENTER),
		// BorderLayout.NORTH);
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("User Preferences", panel);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
	}

	public void preferences() {
		prefs.setLocation((int) this.getLocation().getX() + 150, (int) this.getLocation().getY() + 22);
		prefs.setVisible(true);
	}

	public String toString() {
		return "MpcMaidFrame pgm: " + program + " pgmFile: " + pgmFile + " current pad: " + selectedPad;
	}

	private void makeMain() {
		// MAKE GUI
		final JTabbedPane main = new JTabbedPane();
		main.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));

		// PROGRAM EDITOR
		final Profile profile = Preferences.getInstance().getProfile();
		programEditor = new ProgramPanel(program, profile, pgmFile);

		audioEditor = new JPanel(new BorderLayout(10, 10));
		// audioEditor.setPreferredSize(new Dimension(900, 500));
		audioEditor.setFocusable(true);

		final JLabel fileLabel = new JLabel(WaveformPanel.DEFAULT_FILE_DETAILS, JLabel.CENTER);
		audioEditor.add(fileLabel, BorderLayout.NORTH);
		final WaveformPanel waveformePanel = new WaveformPanel();
		audioEditor.add(waveformePanel, BorderLayout.CENTER);

		final JSlider sensitivitySlider = new JSlider(JSlider.VERTICAL, 50, 200, 130);
		sensitivitySlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				final JSlider source = (JSlider) e.getSource();
				if (waveformePanel.isReady() && !source.getValueIsAdjusting()) {
					final int sensitivity = source.getValue();
					waveformePanel.setSensitivity(sensitivity);
				}

			}

		});
		audioEditor.add(sensitivitySlider, BorderLayout.EAST);
		sensitivitySlider.setPreferredSize(new Dimension(25, 40));
		sensitivitySlider.setPaintTicks(true);
		final JPanel leftFiller = new JPanel();
		leftFiller.setPreferredSize(new Dimension(10, 40));
		audioEditor.add(leftFiller, BorderLayout.WEST);
		sensitivitySlider.setFocusable(false);

		// BOTTOM ZONE
		final JPanel bottom = new JPanel(new BorderLayout());
		final JLabel explanations = new JLabel(
				"Shortcuts: Left-Right: Prev-Next Marker  \t  Alt/Shift+Left/Right: Nudge Marker  \t  Space: Play Marker  \t  Backspace/Delete: Delete Marker \t  Enter: Insert Marker",
				JLabel.CENTER);
		explanations.setFont(MEDIUM_FONT);
		bottom.add(explanations, BorderLayout.SOUTH);
		final JLabel currentMarkerLocation = new JLabel(" - ", JLabel.CENTER);
		bottom.add(currentMarkerLocation, BorderLayout.NORTH);
		JPanel exportZone = new JPanel();
		exportZone.setFocusable(false);
		bottom.add(exportZone, BorderLayout.CENTER);
		final JLabel exportLabel = new JLabel("Files Prefix ", JLabel.RIGHT);
		exportZone.add(exportLabel);
		final JTextField exportPrefix = new JTextField("Slice");
		exportZone.add(exportPrefix);
		exportPrefix.setColumns(FILE_MAX_LENGTH - 2);
		final JButton exportSlicesButton = new JButton("Export");
		exportZone.add(exportSlicesButton);
		exportSlicesButton.setFocusable(false);
		exportSlicesButton.enable(false);
		exportSlicesButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					if (waveformePanel.isReady()) {
						waveformePanel.export(exportPrefix.getText());
					}
				} catch (IOException io) {
					JOptionPane.showMessageDialog(waveformePanel, io.getMessage(), "Error saving file",
							JOptionPane.ERROR_MESSAGE);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

		});

		audioEditor.add(bottom, BorderLayout.SOUTH);
		waveformePanel.setTransferHandler(new FileDragHandler() {

			protected void process(File file) {
				try {
					waveformePanel.setAudioFile(file);
					final String name = file.getName();
					exportPrefix.setText(WaveformPanel.prefixProposal(file.getName(), FILE_MAX_LENGTH - 2));
					fileLabel.setText(waveformePanel.getFileDetails());
					exportSlicesButton.enable(waveformePanel.hasBeat());
					audioEditor.repaint();
				} catch (IllegalArgumentException e) {
					JOptionPane.showMessageDialog(waveformePanel, e.getMessage(), "Unsupported file",
							JOptionPane.ERROR_MESSAGE);
				} catch (UnsupportedAudioFileException e) {
					JOptionPane.showMessageDialog(waveformePanel, "The file format is not supported by the MPC",
							"Unsupported file", JOptionPane.ERROR_MESSAGE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

		main.addTab("Program Editor", programEditor);
		main.addTab("Chop Slices", audioEditor);

		waveformePanel.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent ke) {
				if (!waveformePanel.isReady()) {
					return;
				}
				switch (ke.getKeyCode()) {
					case KeyEvent.VK_LEFT: {
						if (ke.isShiftDown()) {
							waveformePanel.nudgeMarker(-1000);
						} else if (ke.isAltDown()) {
							waveformePanel.nudgeMarker(-100);
						} else {
							waveformePanel.selectMarker(-1);
						}
						final int location = waveformePanel.getSelectedMarkerLocation();
						currentMarkerLocation.setText("Marker location: " + location + " samples");
						break;
					}
					case KeyEvent.VK_RIGHT: {
						if (ke.isShiftDown()) {
							waveformePanel.nudgeMarker(+1000);
						} else if (ke.isAltDown()) {
							waveformePanel.nudgeMarker(+100);
						} else {
							waveformePanel.selectMarker(+1);
						}
						final int location = waveformePanel.getSelectedMarkerLocation();
						currentMarkerLocation.setText("Marker location: " + location + " samples");
						break;
					}
					case KeyEvent.VK_BACK_SPACE:
					case KeyEvent.VK_DELETE:
					{
						waveformePanel.deleteSelectedMarker();
						final int location = waveformePanel.getSelectedMarkerLocation();
						currentMarkerLocation.setText("Marker location: " + location + " samples");
						break;
					}
					case KeyEvent.VK_ENTER: {
						waveformePanel.insertMarker();
						final int location = waveformePanel.getSelectedMarkerLocation();
						currentMarkerLocation.setText("Marker location: " + location + " samples");
						break;
					}
					case KeyEvent.VK_SPACE: {
						try {
							waveformePanel.getSelectedSlice().play();
						} catch (Throwable e) {
							e.printStackTrace();
						}
						break;
					}
				}
			}

			public void keyReleased(KeyEvent e) {
			}

			public void keyTyped(KeyEvent e) {
			}

		});

		// FOCUS SLICER ON CLICK
		waveformePanel.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if (!waveformePanel.isReady()) {
					return;
				}
				super.mouseReleased(e);
				waveformePanel.requestFocusInWindow();
				waveformePanel.selectClosestMarker(e.getX());
				final int location = waveformePanel.getSelectedMarkerLocation();
				currentMarkerLocation.setText("Marker location: " + location + " samples");
			}

		});
		audioEditor.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {
				super.mouseReleased(e);
				waveformePanel.requestFocusInWindow();
			}

		});
		// NUDGE WITH MOUSE WHEEL
		waveformePanel.addMouseWheelListener(new MouseWheelListener() {

			public void mouseWheelMoved(MouseWheelEvent e) {
				if (!waveformePanel.isReady()) {
					return;
				}
				waveformePanel.nudgeMarker(-100 * e.getWheelRotation());
				final int location = waveformePanel.getSelectedMarkerLocation();
				currentMarkerLocation.setText("Marker location: " + location + " samples");
			}

		});

		// SUPPORT DROP PGM FILES
		main.setTransferHandler(new FileDragHandler() {

			protected void process(File file) {
				final MainFrame newFrame = new MainFrame(file);
				newFrame.show();
			}

		});

		getContentPane().add(main);
		pack();
	}

}
