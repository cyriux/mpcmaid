package com.mpcmaid.audio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A tool that is given
 * 
 * @author cyrille martraire
 */
public class Slicer {

	private final int windowSize;

	private final int overlapRatio;

	private final int localEnergyWindowSize;

	private final Sample sample;

	// cache
	private int[][] channels;

	private int sensitivity = 130;

	private final Markers markers = new Markers();

	public Slicer(Sample sample, final int windowSize, int overlapRatio, int localEnergyWindowSize) {
		this.sample = sample;
		this.windowSize = windowSize;
		this.overlapRatio = overlapRatio;
		this.localEnergyWindowSize = localEnergyWindowSize;
		channels = sample.asSamples();

		extractMarkers(channels);
	}

	/**
	 * Main method
	 */
	public void extractMarkers() {
		extractMarkers(channels);
	}

	/**
	 * Process L+R samples to compute the energy over a sliding window, then
	 * compare the energy of each window against the average local energy on a
	 * number of neighbor windows; when this comparison is greater enough, we
	 * got a beat.
	 * 
	 * To reduce the number of fake beats, we also use a toggle state so that no
	 * beat can be found immediately after a beat already found.
	 * 
	 * When a beat has been found, its exact location is then adjusted to the
	 * nearest zero-crossing location in the past.
	 * 
	 * For each beat found, a corresponding marker is added to the list of
	 * markers.
	 */
	public void extractMarkers(final int[][] channels) {
		markers.clear(getFrameLength(), (int) sample.getFormat().getFrameRate());

		final int step = windowSize / overlapRatio;

		final long[] energyHistory = energyHistory(channels, windowSize, overlapRatio);
		if (energyHistory == null || energyHistory.length < localEnergyWindowSize) {
			// when nothing is found, insert marker at 0 position
			markers.add(0);
			return;
		}

		boolean lastState = false;
		for (int i = 0; i < energyHistory.length; i++) {
			final long e = energyHistory[i];
			final long localE = localEnergy(i, energyHistory);
			final double C = (double) sensitivity / 100.0;
			if (e > C * localE) {
				// got a beat
				final int location = i * step;
				if (!lastState) {
					final int[] samplesL = getSamplesL(channels);
					final int adjustedZc = nearestZeroCrossing(samplesL, location, windowSize);
					markers.add(adjustedZc);
					lastState = true;
				}
			} else {
				lastState = false;
			}
		}
	}

	protected int[] getSamplesL(final int[][] channels) {
		return channels[0];
	}

	private long localEnergy(int i, long[] energyHistory) {
		final int n = energyHistory.length;
		final int m = localEnergyWindowSize;
		int from = 0;
		int to = m;
		if (i < m) {
			from = 0;
			to = m;
		} else if (i + m < n) {
			from = i;
			to = i + m;
		} else {
			from = n - m;
			to = n;
		}
		long sum = 0;
		for (int j = from; j < to; j++) {
			sum += Math.pow(energyHistory[j], 1);
		}
		return sum / m;
	}

	private long[] energyHistory(final int[][] channels, final int windowSize, final int overlapRatio) {
		final int[] samplesL = getSamplesL(channels);
		final int[] samplesR = channels.length > 1 ? channels[1] : samplesL;
		final int n = samplesL.length;
		final int step = windowSize / overlapRatio;

		final int energyFrameNumber = n / step;
		if (energyFrameNumber < 1) {
			return null;
		}
		final long[] energy = new long[energyFrameNumber];
		int windowIndex = 0;
		// for each sliding window (ignore last broken period window...)
		for (int i = 0; i + windowSize < n; i += step) {
			long sum = 0;
			// for the window size, cumulate energy
			for (int j = 0; j < windowSize; j++) {
				sum += Math.pow(samplesL[i + j], 2);
				sum += Math.pow(samplesR[i + j], 2);
			}
			energy[windowIndex++] = sum;
		}
		return energy;
	}

	private final static int nearestZeroCrossing(final int[] samples, final int index, final int excursion) {
		if (index == 0) {
			return 0;
		}
		int i = index;
		final int min = index - excursion >= 0 ? index - excursion : 0;
		while (!isZeroCross(samples, i) && i > min) {
			i--;
		}
		return i;
	}

	private final static boolean isZeroCross(final int[] samples, final int index) {
		if (index == 0) {
			return true;
		}
		if (index >= samples.length - 1) {
			return true;
		}
		final int a = samples[index - 1];
		final int b = samples[index];
		return a > 0 && b < 0 || a < 0 && b > 0 || a == 0 && b != 0;
	}

	public int[][] getChannels() {
		return channels;
	}

	public Sample getSample() {
		return sample;
	}

	public int getFrameLength() {
		return sample.getFrameLength();
	}

	public void setSensitivity(final int sensitivity) {
		this.sensitivity = sensitivity;
		extractMarkers();
	}

	public int getSensitivity() {
		return sensitivity;
	}

	public int adjustNearestZeroCrossing(int location, final int excursion) {
		final int[] samplesL = channels[0];
		final int adjustedZc = nearestZeroCrossing(samplesL, location, excursion);
		return adjustedZc;
	}

	// ------------------------------

	public Markers getMarkers() {
		return markers;
	}

	public Sample getSelectedSlice() {
		final int markerIndex = markers.getSelectedMarkerIndex();
		return getSlice(markerIndex);
	}

	public Sample getSlice(final int markerIndex) {
		final LocationRange range = markers.getRangeFrom(markerIndex);
		return sample.subRegion(range.getFrom(), range.getTo());
	}

	/**
	 * Exports each slice as defined by the markers into a slice file
	 */
	public List<File> exportSlices(File path, String prefix) throws Exception {
		final List<File> files = new ArrayList<>();
		final int n = markers.size();
		for (int i = 0; i < n; i++) {
			final Sample slice = getSlice(i);
			final File file = new File(path, prefix + i + ".wav");
			files.add(file);
			slice.save(file);
		}
		return files;
	}

	public String toString() {
		return "Slicer: " + markers.getDuration() + "s (" + getFrameLength() + " samples), " + markers.size()
				+ " markers";
	}
}
