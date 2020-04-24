package com.mpcmaid.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.mpcmaid.audio.Marker;
import com.mpcmaid.audio.Markers;
import com.mpcmaid.audio.Sample;
import com.mpcmaid.audio.Slicer;
import com.mpcmaid.pgm.Pad;
import com.mpcmaid.pgm.Program;

/**
 * A panel dedicated to display a waveform. It actually contains a Slicer that
 * must be initialized before using any delegate method.
 * 
 * @author cyrille martraire
 */
public class WaveformPanel extends JPanel {

	private static final long serialVersionUID = -1728002082575891020L;

	private static final int AVERAGE_ENERGY_WINDOW = 43;

	private static final int OVERLAP_RATIO = 1;

	private static final int WINDOW_SIZE = 1024;

	private static final int MIDI_PPQ = 96;

	public static final String DEFAULT_FILE_DETAILS = "Drag and Drop a sample file below";

	private Slicer slicer;

	private Markers markers;

	private File file;

	public WaveformPanel() {
		super();
		setBorder(BorderFactory.createEtchedBorder());
	}

	/**
	 * Set the audio file and initialize the Slicer for this file
	 */
	public void setAudioFile(File file) throws Exception {
		this.file = file;
		this.slicer = new Slicer(Sample.open(file), WINDOW_SIZE, OVERLAP_RATIO, AVERAGE_ENERGY_WINDOW);
		this.markers = slicer.getMarkers();
		slicer.extractMarkers();
		repaint();
	}

	public File getFile() {
		return file;
	}

	public String getFileName() {
		return file == null ? " No sample yet" : file.getName();
	}

	public String getFileDetails() {
		if (slicer == null) {
			return DEFAULT_FILE_DETAILS;
		}
		return getFileName() + "     (" + getFrameLength() + " samples)" + markers.displayTempo();
	}

	public boolean isReady() {
		return slicer != null;
	}

	public Slicer getSlicer() {
		return slicer;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (slicer == null) {
			return;
		}

		final int[][] channels = slicer.getChannels();
		for (int i = 0; i < channels.length && i < 2; i++) {
			final int[] samples = channels[i];
			printWaveform(samples, i, g);
		}
	}

	private void printWaveform(int[] values, final int channel, Graphics g) {
		final int width = (int) (getWidth() - 50);
		final int fullHeight = 180;
		final int height = fullHeight * 4 / 5;

		final int x0 = 20;
		final int y0 = channel * fullHeight + fullHeight / 2;

		g.setColor(Color.BLACK);
		g.drawLine(x0, y0, width - x0, y0);

		if (values.length == 0) {
			return;
		}

		final int n = values.length;
		final double minX = 0;
		final double maxX = values.length;

		final double minY = Short.MIN_VALUE;
		final double maxY = Short.MAX_VALUE;

		final double xScale = width / (maxX - minX);
		final double yScale = -height / (maxY - minY);

		int oldX = x0;
		int oldY = y0;
		g.setColor(Color.GRAY);
		final int increment = (int) (n / (4 * width));
		for (int t = 0; t < n; t += increment) {
			final int x = x0 + (int) Math.round(t * xScale);
			final int y = y0 + (int) Math.round(values[t] * yScale);
			g.drawLine(oldX, oldY, x, y);
			oldX = x;
			oldY = y;
		}

		Iterator<Marker> it = markers.getMarkers().iterator();
		final int selectedMarkerIndex = markers.getSelectedMarkerIndex();
		int markerIndex = 0;
		while (it.hasNext()) {
			Marker marker = it.next();
			final int location = marker.getLocation();

			final int x = x0 + (int) Math.round(location * xScale);
			g.setColor(Color.RED);
			final int yPointer = y0 + height / 2;
			if (selectedMarkerIndex == markerIndex) {
				// print pointer
				g.fillPolygon(new Polygon(new int[] { x, x + 5, x - 5 }, new int[] { yPointer, yPointer + 7,
						yPointer + 7 }, 3));
			}
			g.drawLine(x, y0 - height / 2, x, yPointer);
			markerIndex++;
		}

	}

	// ---- delegate methods to the Slicer or Markers + repaint for every
	// mutator
	public void setSensitivity(final int sensitivity) {
		slicer.setSensitivity(sensitivity);
		repaint();
	}

	public void selectMarker(final int shift) {
		markers.selectMarker(shift);
		repaint();
	}

	public void selectClosestMarker(final int mouseX) {
		// get the location (in samples) from the mouse position
		final int width = (int) (getWidth() - 50);
		final int x0 = 20;
		final double minX = 0;
		final double maxX = slicer.getChannels()[0].length;
		final double xScale = width / (maxX - minX);
		final int position = (int) Math.round((mouseX - x0) / xScale);
		markers.selectClosestMarker(position);
		repaint();
	}

	public void nudgeMarker(final int ticks) {
		markers.nudgeMarker(ticks, slicer);
		repaint();
	}

	public void deleteSelectedMarker() {
		markers.deleteSelectedMarker();
		repaint();
	}

	public void insertMarker() {
		markers.insertMarker();
		repaint();
	}

	public Marker getSelectedMarker() {
		return markers.getSelectedMarker();
	}

	// ---- delegate methods to the Slicer or Markers accessors

	public int getSensitivity() {
		return slicer.getSensitivity();
	}

	public int getSelectedMarkerLocation() {
		return markers.getSelectedMarkerLocation();
	}

	public int getLocation(final int markerIndex) {
		return markers.getLocation(markerIndex);
	}

	public Sample getSelectedSlice() {
		return slicer.getSelectedSlice();
	}

	public Sample getSlice(final int markerIndex) {
		return slicer.getSlice(markerIndex);
	}

	public int getFrameLength() {
		return slicer.getFrameLength();
	}

	public boolean hasBeat() {
		return markers.hasBeat();
	}

	public float getTempo() {
		return markers.getTempo(8);
	}

	public float getTempo(final int numberOfBeats) {
		return markers.getTempo(numberOfBeats);
	}

	public float getDuration() {
		return markers.getDuration();
	}

	public static final String shortName(String name) {
		final int indexOf = name.lastIndexOf('.');
		if (indexOf != -1) {
			return name.substring(0, indexOf);
		}
		return name;
	}

	public static final String prefixProposal(String name, final int maxLen) {
		final int indexOf = name.indexOf('.');
		String prefix = name;
		if (indexOf != -1) {
			prefix = name.substring(0, indexOf);
		}
		prefix = prefix.length() > maxLen ? prefix.substring(0, maxLen) : prefix;
		return prefix;
	}

	public void export(String prefix) throws Exception {
		final String name = prefixProposal(prefix, 10);
		final File path = file.getParentFile();
		final List<File> slices = slicer.exportSlices(path, name);
		final File midiFile = new File(path, name + ".mid");
		markers.exportMidiSequence(midiFile, MIDI_PPQ);

		// final File file = new File("chromatic.pgm");
		final Program pgm = Program.open(getClass().getResourceAsStream("chromatic.pgm"));
		for (int i = 0; i < slices.size() && i < 64; i++) {
			final File slice = slices.get(i);
			final Pad pad = pgm.getPad(i);
			final String sampleName = shortName(slice.getName());
			final com.mpcmaid.pgm.Layer layer0 = pad.getLayer(0);
			layer0.setSampleName(sampleName);
			layer0.setLevel(100);
		}
		pgm.save(new File(path, name + ".pgm"));
	}

	public String toString() {
		return "WaveformPanel: " + slicer;
	}

}
