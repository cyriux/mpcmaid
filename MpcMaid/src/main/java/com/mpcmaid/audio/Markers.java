package com.mpcmaid.audio;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import com.mpcmaid.midi.MidiSequenceBuilder;

/**
 * Represents the list of markers of slices, along with the currently selected
 * marker
 * 
 * @author cyrille martraire
 */
public class Markers {

	public static final int NONE = -1;

	private final List<Marker> markers = new ArrayList<Marker>();

	private int selectedMarker = 0;

	private int maxLocation = 0;

	private int samplingRate = 0;

	/**
	 * @param maxLocation
	 *            The Frame Length
	 */
	public void clear(int maxLocation, int samplingRate) {
		markers.clear();
		selectedMarker = 0;
		this.maxLocation = maxLocation;
		this.samplingRate = samplingRate;
	}

	public void add(int adjustedZc) {
		markers.add(new Marker(adjustedZc));
	}

	public List<Marker> getMarkers() {
		return markers;
	}

	public int size() {
		return markers.size();
	}

	public void selectMarker(final int shift) {
		if (isUnset()) {
			return;
		}
		int sel = selectedMarker;
		sel += shift;
		if (sel < 0) {
			sel = markers.size() - 1;
		}
		selectedMarker = sel % markers.size();
	}

	/**
	 * Select the closest marker to given location.
	 * @param location
	 *           location in samples
	 */
	public void selectClosestMarker(final int location) {
		if (isUnset()) {
			return;
		}
		int distance = Math.abs(markers.get(0).getLocation() - location);
		int idx = 0;
		for (int c = 1; c < markers.size(); c++) {
			int cdistance = Math.abs(markers.get(c).getLocation() - location);
			if (cdistance < distance) {
				idx = c;
				distance = cdistance;
			}
		}
		selectedMarker = idx;
	}

	public void nudgeMarker(final int ticks, Slicer adjustor) {
		final Marker marker = getSelectedMarker();
		int max = maxLocation;
		int min = 0;
		if (marker != null) {
			if (markers.size() > selectedMarker + 1) {
				final Marker nextMarker = (Marker) markers.get(selectedMarker + 1);
				max = nextMarker.getLocation();
			}
			if (selectedMarker > 0) {
				final Marker previousMarker = (Marker) markers.get(selectedMarker - 1);
				min = previousMarker.getLocation();
			}
			int location = marker.move(ticks);
			if (marker.getLocation() + ticks >= max) {
			    location = max;
			}
			if (marker.getLocation() + ticks <= min) {
				location = min;
			}
			final int excursion = ticks / 2;
			final int adjustedZc = adjustor.adjustNearestZeroCrossing(location, excursion);
			marker.setLocation(adjustedZc);
		}
	}

	public Marker getSelectedMarker() {
		final Marker marker = (Marker) markers.get(selectedMarker);
		return marker;
	}

	public boolean isUnset() {
		return markers.size() == 0;
	}

	public void deleteSelectedMarker() {
		if (selectedMarker > 0) {
			markers.remove(selectedMarker);
			selectedMarker--;
		}
	}

	public void insertMarker() {
		final int markerIndex = getSelectedMarkerIndex();
		final LocationRange range = getRangeFrom(markerIndex);
		markers.add(markerIndex, new Marker(range.getMidLocation()));
		Collections.sort(markers);
		selectedMarker++;
	}

	public int getSelectedMarkerIndex() {
		return selectedMarker;
	}

	public int getSelectedMarkerLocation() {
		return getLocation(selectedMarker);
	}

	public float getDuration() {
		final float duration = (float) maxLocation / samplingRate;
		return duration;
	}

	public int getLocation(final int markerIndex) {
		if (markerIndex < 0) {
			return 0;
		}
		if (markerIndex >= markers.size()) {
			return maxLocation;
		}
		final Marker marker = (Marker) markers.get(markerIndex);
		if (marker == null) {
			return 0;
		}
		return marker.getLocation();
	}

	public LocationRange getRangeFrom(final int markerIndex) {
		Collections.sort(markers);

		final int from = getLocation(markerIndex);
		final int to = getLocation(markerIndex + 1);
		return new LocationRange(from, to);
	}

	/**
	 * @param ppq
	 *            The MIDI PPQ
	 */
	public int getLocationInTicks(final int markerIndex, final int ppq) {
		final double tempoBps = (double) getTempo() / 60.0;
		final int location = getLocation(markerIndex);
		return (int) Math.round(ppq * tempoBps * location / samplingRate);
	}

	public String displayTempo() {
		final float tempo = getTempo();
		final String format = "   Estimated tempo:  ##0.##BPM";
		return tempo > 0 ? new DecimalFormat(format).format(tempo) : "";
	}

	public boolean hasBeat() {
		return getTempo() != NONE;
	}

	public float getTempo() {
		return getTempo(8);
	}

	public float getTempo(final int numberOfBeats) {
		final float duration = getDuration();
		final float tempo = 60 * numberOfBeats / duration;
		if (tempo > 250 || tempo < 40) {
			return NONE;
		}
		return tempo;
	}

	/**
	 * Exports the location of each slice into a MIDI file
	 */
	public Sequence exportMidiSequence(final File file, final int ppq) throws IOException {
		final double tempoBps = (double) getTempo() / 60.0;
		final MidiSequenceBuilder builder = new MidiSequenceBuilder(ppq);
		final Track track = builder.getTrack();
		final int n = markers.size();
		int startTick = 0;
		int key = 35;
		for (int i = 0; i < n; i++) {
			final int location = getLocation(i);
			startTick = (int) Math.round(ppq * tempoBps * location / samplingRate);
			int tickLength = 32;
			builder.addNote(track, startTick, tickLength, key++);
		}
		if (file != null) {
			builder.save(file);
		}
		return builder.getSequence();
	}

	public String toString() {
		return "Markers: " + markers.size() + " markers, selected: " + selectedMarker;
	}
}
