package com.mpcmaid.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.mpcmaid.pgm.Element;
import com.mpcmaid.pgm.Layer;
import com.mpcmaid.pgm.Pad;
import com.mpcmaid.pgm.Parameter;
import com.mpcmaid.pgm.Profile;
import com.mpcmaid.pgm.Program;
import com.mpcmaid.pgm.Range;
import com.mpcmaid.pgm.Sample;
import com.mpcmaid.pgm.Slider;
import com.mpcmaid.pgm.Parameter.OffIntType;
import com.mpcmaid.pgm.command.ExportCommand;
import com.mpcmaid.pgm.command.MultiSampleCommand;
import com.mpcmaid.pgm.command.SimpleAssignCommand;

/**
 * @pattern Composite target=BindingCapable
 * @pattern Composite target=JComponent
 * 
 * @author cyrille martraire
 */
public class ProgramPanel extends JPanel implements BindingCapable {

	private static final String[] ASSIGN_CHOICES2 = { "Cancel", "Sample", "Pad", "Multisample" };

	private static final String[] ASSIGN_CHOICES1 = { "Cancel", "Sample", "Pad" };

	private static final Font SMALL_FONT = new Font("Verdana", Font.PLAIN, 9);

	private static final Font MEDIUM_FONT = new Font("Verdana", Font.PLAIN, 10);

	private static final Font FONT = new Font("Verdana", Font.BOLD, 11);

	protected final Profile profile;

	private final File pgmFile;

	private final Program pgm;

	private final JButton[] padButtons = new JButton[64];

	private final JTabbedPane params = new JTabbedPane();

	private final ProgramSamples samples;

	private Pad currentlySelectedPad = null;

	public ProgramPanel(Program program, Profile profile, File pgmFile) {
		this.pgm = program;
		this.profile = profile;
		this.pgmFile = pgmFile;

		this.samples = new ProgramSamples();
		this.samples.set(program, pgmFile == null ? null : pgmFile.getParentFile());
		make();
	}

	public Profile getProfile() {
		return profile;
	}

	public Program getProgram() {
		return pgm;
	}

	protected void make() {
		setLayout(new BorderLayout(10, 10));
		// setPreferredSize(new Dimension(950, 500));

		final Pad selectedPad = pgm.getPad(0);
		params.setFont(SMALL_FONT);
		params.setPreferredSize(new Dimension(450, 520));
		params.addTab("Samples", makeSampleArea(selectedPad));
		params.addTab("Params", makePadArea(selectedPad));
		params.addTab("Envelope", makePadArea(selectedPad.getEnvelope()));
		params.addTab("Filters", makeFiltersArea(selectedPad));
		params.addTab("Mixer", makePadArea(selectedPad.getMixer()));
		params.addTab("Sliders", makeSlidersArea());

		add(params, BorderLayout.EAST);

		final JPanel padArea = new JPanel();
		padArea.setLayout(new BoxLayout(padArea, BoxLayout.Y_AXIS));
		final JTabbedPane banks = new JTabbedPane();
		banks.setPreferredSize(new Dimension(430, 410));
		final int bankNumber = 4;
		final int colNumber = profile.getColNumber();
		final int rowNumber = profile.getRowNumber();
		final int bankSize = profile.getPadNumber();
		for (int k = 0; k < bankNumber; k++) {
			final JPanel pads = new JPanel(new GridLayout(rowNumber, colNumber, 10, 10));
			final int bankOffset = bankSize * k;
			for (int i = 0; i < rowNumber; i++) {
				for (int j = 0; j < colNumber; j++) {
					final int padId = bankOffset + (rowNumber - 1 - i) * colNumber + j;

					selectedPad.setElementIndex(padId);
					final JButton button = new JButton();
					padButtons[padId] = button;
					refreshPadButton(selectedPad);
					pads.add(button);

					button.setFont(SMALL_FONT);
					button.setAlignmentX(LEFT_ALIGNMENT);
					button.setAlignmentY(TOP_ALIGNMENT);

					final PadListener listener = new PadListener() {

						public void actionPerformed(ActionEvent arg0) {
							samples.play(selectedPad);
						}

						public void focusGained(FocusEvent e) {
							selectPad(selectedPad, padId, button);
						}

						protected void process(List files) {
							selectPad(selectedPad, padId, button);
							assignSounds(selectedPad, files);
							load();
						}

					};
					button.addActionListener(listener);
					button.addFocusListener(listener);
					button.setTransferHandler(listener);
				}
			}
			final char ch = (char) ('A' + k);
			banks.addTab("     " + String.valueOf(ch) + "     ", pads);
		}
		final JButton lastSelected = padButtons[0];
		lastSelected.setSelected(true);
		selectedPad.setElementIndex(0);// restore selected to 0
		currentlySelectedPad = selectedPad;
		padArea.add(banks);

		// MIDI program change combo
		final Widget.OffIntegerField progChangeCombo = new Widget.OffIntegerField(pgm, Program.MIDI_PROGRAM_CHANGE,
				WidgetPanel.enumerate(Program.MIDI_PROGRAM_CHANGE, ""));
		padArea.add(progChangeCombo);
		add(padArea, BorderLayout.CENTER);
	}

	private void refreshPadButton(final Pad pad) {
		final String htmlLabel = htmlSamples(pad);
		final JButton button = padButtons[pad.getElementIndex()];
		if (button != null) {
			button.setText(htmlLabel);
		}
	}

	public void assignSounds(Pad pad, List files) {
		if (files.size() < 2) {
			doAssignSamples(pad, files, false);
			return;
		}

		final String[] choices = ASSIGN_CHOICES2;
		int response = JOptionPane.showOptionDialog(this,
				"Each dropped file will be assigned to a pad or sample layer. ", "Assign samples to locations",
				JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, "Pad");
		switch (response) {
		case 0: {
			return;
		}
		case 1: {
			doAssignSamples(pad, files, false);
			break;
		}
		case 2: {
			doAssignSamples(pad, files, true);
			break;
		}
		case 3: {
			if (!samples.isEmpty()) {
				int confirm = JOptionPane.showConfirmDialog(this,
						"This will overwrite existing samples, are you sure?", "Confirm overwrite",
						JOptionPane.OK_CANCEL_OPTION);
				if (confirm != JOptionPane.OK_OPTION) {
					return;
				}
			}
			doMultisample(files);
			break;
		}
		}
	}

	private void doMultisample(List files) {
		final MultiSampleCommand command = new MultiSampleCommand(Sample.RENAMED, files, pgm);
		final List impactedPads = (List) command.execute(samples);

		refreshImpactedPads(impactedPads);

		if (command.hasError()) {
			String msg = "Multisample creation complete: " + command.getReport();
			JOptionPane.showMessageDialog(this, msg);
		}

	}

	private void doAssignSamples(Pad pad, List files, final boolean perPad) {
		final SimpleAssignCommand command = new SimpleAssignCommand(Sample.RENAMED, files, pad, perPad);
		final Collection impactedPads = (Collection) command.execute(samples);

		refreshImpactedPads(impactedPads);

		if (command.hasError()) {
			String msg = command.getReport();
			JOptionPane.showMessageDialog(this, msg);
		}
	}

	public void batchCreate(File path) {
		final BatchCreateCommand command = new BatchCreateCommand(path, pgm, profile.getPadNumber() * 4);
		command.execute();
		String msg = "Batch Create Program: \n" + command.getReport();
		JOptionPane.showMessageDialog(this, msg);
	}

	private void refreshImpactedPads(final Collection impactedPads) {
		Iterator it = impactedPads.iterator();
		while (it.hasNext()) {
			refreshPadButton((Pad) it.next());
		}
	}

	public void copySettingsToAllPads() {
		int confirm = JOptionPane
				.showConfirmDialog(
						this,
						"This will overwrite every parameter of every pad \n(except the sample name, tuning and midi note) with the parameters \nof the currently selected pad, are you sure?",
						"Copy Settings to All Pads - Confirm overwrite", JOptionPane.OK_CANCEL_OPTION);
		if (confirm != JOptionPane.OK_OPTION) {
			return;
		}
		try {
			final HashSet ignoreParams = new HashSet();
			ignoreParams.add(Pad.PAD_MIDI_NOTE_VALUE);
			ignoreParams.add(Layer.SAMPLE_NAME);
			ignoreParams.add(Layer.TUNING);

			final Collection impactedPads = new ArrayList();
			for (int i = 0; i < 4 * profile.getPadNumber(); i++) {
				final Pad pad = pgm.getPad(i);
				pad.copyFrom(currentlySelectedPad, ignoreParams);
			}
			refreshImpactedPads(impactedPads);
		} catch (Exception e) {
			e.printStackTrace();// error occurred
		}
	}

	private void selectPad(final Pad selectedPad, final int padId, final JButton button) {
		final JButton lastSelected = padButtons[selectedPad.getElementIndex()];
		lastSelected.setSelected(false);

		selectedPad.setElementIndex(padId);

		currentlySelectedPad = selectedPad;// remember currently selected pad

		button.setSelected(true);
		// System.out.println("Pad " + padId + " pressed!");
		refreshParamsArea();

	}

	private void refreshParamsArea() {
		final Component[] widgets = params.getComponents();
		for (int i = 0; i < widgets.length; i++) {
			if (widgets[i] instanceof BindingCapable) {
				BindingCapable component = (BindingCapable) widgets[i];
				component.load();
			}
		}
	}

	public void removeAllSamples() {
		final Collection impactedPads = samples.removeAllSamples(pgm);
		refreshImpactedPads(impactedPads);
		// selectPad(selectedPad, padId, button);
	}

	public void setChromatic() {
		final Collection impactedPads = new ArrayList();
		final int padNumber = pgm.getPadNumber();
		for (int i = 0; i < padNumber; i++) {
			final Pad pad = pgm.getPad(i);
			pad.setPadMidiNote(35 + i);
			impactedPads.add(pad);
		}
		refreshImpactedPads(impactedPads);
	}

	public void exportSamples(File dir) {
		final ExportCommand command = new ExportCommand(dir);
		command.execute(samples);
		if (command.hasError()) {
			final String msg = (String) command.getReport();
			JOptionPane.showMessageDialog(this, msg);
		}
	}

	private final static String htmlSamples(Pad padElement) {
		final StringBuffer out = new StringBuffer("<html>");
		for (int i = 0; i < padElement.getLayerNumber(); i++) {
			final Layer sample = padElement.getLayer(i);
			final String sampleName = shorten(sample.getSampleName());
			if (i > 0) {
				out.append("<p>");
			}
			out.append(sampleName);
		}
		out.append("</html>");
		return out.toString();
	}

	private static String shorten(String name) {
		if (name.length() > 11) {
			return name.substring(0, 5) + "..." + name.substring(10);
		}
		return name;
	}

	private JTabbedPane makeSlidersArea() {
		final JTabbedPane sliders = new JTabbedPane();
		sliders.setFont(SMALL_FONT);
		sliders.setPreferredSize(new Dimension(200, 400));
		for (int i = 0; i < profile.getSliderNumber(); i++) {
			sliders.addTab("Slider" + (i + 1), makeSliderArea(pgm, i));
		}
		return sliders;
	}

	private Component makeSliderArea(Program pgm, int i) {
		final Slider slider = pgm.getSlider(i);
		final WidgetPanel area = new WidgetPanel(slider) {

			public void make() {
				add(new JLabel(""));
				super.make();
			}

			protected void makeEnumParameter(Parameter parameter) {
				add(new Widget.ComboField(slider, parameter));
				add(new JLabel("Slider Ranges", JLabel.CENTER));
			}

			protected void makeOffIntParameter(Parameter parameter) {
				final OffIntType type = (OffIntType) parameter.getType();
				final String[] values = offPads(type, profile.getPadNumber());
				add(new Widget.OffIntegerField(slider, parameter, values));
			}

		};
		area.make();
		// widgets.add(area);//no pad-specific
		return area;
	}

	/**
	 * @return A Component that is also BindingCapable
	 * 
	 *         When we select another pad, call the setElement() method to
	 *         update the view
	 */
	private Component makePadArea(Element element) {
		final WidgetPanel area = new WidgetPanel(element) {

			public void make() {
				// add(new JLabel(""));
				super.make();
			}

		};
		area.make();
		return area;
	}

	/**
	 * @return A Component that is also BindingCapable
	 * 
	 *         Make the Widget panel and keep a reference to it in a collection
	 *         so that we can collectively call setElement(), load() and save()
	 *         on them
	 * 
	 *         When we select another pad, call the setElement() method to
	 *         update the view
	 */
	private Component makeSampleArea(final Pad pad) {
		final int samplesNb = pad.getLayerNumber();
		final SamplePanel layersArea = new SamplePanel(pad, new GridLayout(2, 2, 10, 10));
		for (int i = 0; i < samplesNb && i < 4; i++) {
			final Layer layer = pad.getLayer(i);
			final int layerIndex = i;
			final WidgetPanel area = new WidgetPanel(layer) {

				public void make() {
					final String layerLabel = "Sample Layer " + (layerIndex + 1);
					final JLabel title = new JLabel(layerLabel, JLabel.CENTER);
					title.setFont(FONT);
					add(title);
					setLayout(new GridLayout(7, 1, 5, 0));
					super.makeParameters();

					// play/remove buttons
					final JPanel layerButtons = new JPanel(new FlowLayout(2, 0, 0));
					final JButton playButton = new JButton("Play");
					playButton.setFont(MEDIUM_FONT);
					layerButtons.add(playButton);
					final JButton removeButton = new JButton("Clear");
					removeButton.setFont(MEDIUM_FONT);
					layerButtons.add(removeButton);
					final ActionListener actionListener = new ActionListener() {

						public void actionPerformed(ActionEvent e) {
							final Object source = e.getSource();
							if (source == playButton) {
								samples.play(layer);
							} else if (source == removeButton) {
								samples.remove(layer);
								refreshPadButton(pad);
								load();
							}
						}

					};
					playButton.addActionListener(actionListener);
					removeButton.addActionListener(actionListener);
					add(layerButtons);
				}

				public void makeDecimalParameter(Parameter parameter) {
					if (parameter.getLabel().startsWith("Tuning")) {
						final Widget.TuningField widget = new Widget.TuningField((Layer) getElement(), parameter);
						addWidget(widget);
					}
				}

			};
			layersArea.add(area);
			area.make();
		}
		return layersArea;
	}

	public void load() {
		final Component[] widgets = params.getComponents();
		for (int i = 0; i < widgets.length; i++) {
			if (widgets[i] instanceof BindingCapable) {
				BindingCapable component = (BindingCapable) widgets[i];
				component.load();

			}
		}
	}

	public void save() {
		final Component[] widgets = params.getComponents();
		for (int i = 0; i < widgets.length; i++) {
			WidgetPanel component = (WidgetPanel) widgets[i];
			component.save();
		}
	}

	/**
	 * @return A Component that is also BindingCapable
	 * 
	 *         When we select another pad, call the setElement() method to
	 *         update the view
	 */
	private Component makeFiltersArea(Pad pad) {
		final JTabbedPane sliders = new JTabbedPane();
		sliders.setFont(SMALL_FONT);
		sliders.setPreferredSize(new Dimension(200, 400));
		for (int i = 0; i < profile.getFilterNumber(); i++) {
			sliders.addTab("Filter" + (i + 1), makePadArea(pad.getFilter(i)));
		}

		return sliders;
	}

	public final static String[] offPads(OffIntType type, int padNumber) {
		final Range range = type.getRange();
		final String[] values = new String[range.getHigh() - range.getLow() + 1];
		for (int i = 0; i < values.length; i++) {
			int j = i - 1;
			values[i] = i == 0 ? "Off" : "Pad " + ((char) ('A' + (j / padNumber))) + ((j % padNumber) + 1);
		}
		return values;
	}

	public String toString() {
		return "ProgramPanel " + getProgram();
	}

	/**
	 * Panel to contain the four widgets (one for each sample layer) for the
	 * selected pad, which is why it is also BindingCapable
	 * 
	 * When we select another pad, call the setElement() method to update the
	 * view
	 * 
	 * @author cyrille martraire
	 */
	protected static final class SamplePanel extends JPanel implements BindingCapable {

		private Pad pad;

		protected SamplePanel(Pad pad, LayoutManager arg0) {
			super(arg0);
			this.pad = pad;
		}

		public void load() {
			final Component[] widgets = getComponents();
			for (int i = 0; i < widgets.length; i++) {
				BindingCapable component = (BindingCapable) widgets[i];
				component.load();
			}
		}

		public void save() {
			final Component[] widgets = getComponents();
			for (int i = 0; i < widgets.length; i++) {
				WidgetPanel component = (WidgetPanel) widgets[i];
				component.save();
			}
		}

		public Element getElement() {
			return pad;
		}

		public String toString() {
			return "SamplePanel for pad: " + pad;
		}
	}

}