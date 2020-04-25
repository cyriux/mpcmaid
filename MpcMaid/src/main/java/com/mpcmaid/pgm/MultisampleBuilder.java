package com.mpcmaid.pgm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Finds out a configuration of a multisample program from a mere set of sample
 * files names.
 * 
 * Uses the sample filenames to guess their pitch, then assigns each sample to
 * one or several pads so that to rebuild a full chromatic scale (from note 35
 * to note 98).
 * 
 * @author cyrille martraire
 */
public class MultisampleBuilder {

	private final int firstNote = 35;

	private final int padNumber = 64;

	private final Collection<String> warnings = new ArrayList<>();

	private static final String[] NOTES = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };

	private static final String[] NOTES_BIS = { "C ", "C#", "D ", "D#", "E ", "F ", "F#", "G ", "G#", "A ", "A#", "B " };

	/**
	 * @param sampleNames
	 *            A list of String of the sample names without extension
	 */
	public Slot[] assign(List<Sample> samples) {
		try {
			return asssignBare(samples);
		} catch (Exception e) {
			e.printStackTrace();
			return new Slot[64];
		}
	}

	/**
	 * Converts a list of samples into a list of filenames without extension
	 */
	private static List<String> sampleNames(List<Sample> samples) {
		final List<String> list = new ArrayList<>();
		Iterator<Sample> it = samples.iterator();
		while (it.hasNext()) {
			final Sample sample = (Sample) it.next();
			final String sampleName = sample.getSampleName();
			System.out.println(sampleName);
			list.add(sampleName);
		}
		return list;
	}

	public Collection<String> getWarnings() {
		return warnings;
	}

	private Slot[] asssignBare(List<Sample> samples) {
		if (samples.size() < 2) {
			return null;
		}

		final List<String> sampleNames = sampleNames(samples);
		final int commonIndex = longuestPrefix(sampleNames);
		if (commonIndex == 0) {
			return null;
		}

		// build slots, returns a sorted list
		final List<Slot> slots = collectSlots(samples, commonIndex);
		if (slots.size() <= 2) {
			return null;
		}

		// interpolate notes in between
		final Slot[] multisample = new Slot[64];
		Slot last = null;
		Iterator<Slot> it = slots.iterator();
		while (it.hasNext()) {
			final Slot slot = it.next();
			final int note = slot.getNote();

			// fill exact slot first, to be sure it wins
			multisample[note - firstNote] = slot;

			// cross note is half way between previous sample slot and this
			// sample slot
			int crossNote = last == null ? firstNote - 1 : ((note + last.getNote()) / 2);

			if (last != null) {
				// fill from previous slot till cross note
				for (int transposeUp = last.getNote() + 1; transposeUp <= crossNote; transposeUp++) {
					final int index = transposeUp - firstNote;
					if (multisample[index] == null) {
						final Slot transposed = last.transpose(transposeUp);
						if (Math.abs(transposed.getTuning()) <= 36) {
							multisample[index] = transposed;
						}
					}
				}
			}

			// fill from cross note till current slot
			for (int transposeDown = crossNote + 1; transposeDown < note; transposeDown++) {
				final int index = transposeDown - firstNote;
				if (multisample[index] == null) {
					final Slot transposed = slot.transpose(transposeDown);
					if (Math.abs(transposed.getTuning()) <= 36) {
						multisample[index] = transposed;
					}
				}
			}

			last = slot;
		}

		// finish till the end of the scale
		for (int transposeUp = last.getNote() + 1; transposeUp < firstNote + padNumber; transposeUp++) {
			final int index = transposeUp - firstNote;
			if (multisample[index] == null) {
				final Slot transposed = last.transpose(transposeUp);
				if (Math.abs(transposed.getTuning()) <= 36) {
					multisample[index] = transposed;
				}
			}
		}

		return multisample;
	}

	protected List<Slot> collectSlots(final List<Sample> samples, int commonIndex) {
		final List<Slot> slots = new ArrayList<>();
		Iterator<Sample> it = samples.iterator();
		while (it.hasNext()) {
			final Sample sample = it.next();

			final String word = sample.getSampleName();
			final String variablePart = word.substring(commonIndex);
			final int note = extractNote(variablePart);

			if (note != -1 && note >= firstNote && note <= firstNote + padNumber) {
				slots.add(new Slot(sample, note, 0));
			} else {
				warnings.add("File: " + word
						+ " is not consistently named, will be ignored when building the multisamples");
			}
		}
		Collections.sort(slots);
		return slots;
	}

	protected static int longuestPrefix(final List<String> words) {
		int commonIndex = 16;// max
		String last = null;
		Iterator<String> it = words.iterator();
		while (it.hasNext()) {
			String word = it.next();
			if (last != null) {
				final int index = longuestPrefix(commonIndex, word, last);
				if (index < commonIndex) {
					commonIndex = index;
				}
			}
			last = word;
		}
		return commonIndex;
	}

	private static int longuestPrefix(int index, String word, String last) {
		for (int i = 0; i < index && i < word.length() && i < last.length(); i++) {
			if (word.charAt(i) != last.charAt(i)) {
				return i;
			}
		}
		return index;
	}

	public static String noteName(final int note) {
		final int chromatic = (note - 24) % 12;
		final int octave = (note - 24) / 12;
		final String noteName = NOTES[chromatic] + octave;
		return noteName;
	}

	public static int extractNote(final String noteName) {
		for (int i = NOTES.length - 1; i >= 0; i--) {
			String candidate = NOTES_BIS[i];
			int indexOf = noteName.lastIndexOf(candidate);
			if (indexOf == -1) {
				candidate = NOTES[i];
				indexOf = noteName.lastIndexOf(candidate);
			}
			if (indexOf != -1) {
				final char octaveDigit = noteName.charAt(indexOf + candidate.length());
				int octave = 3;
				if (Character.isDigit(octaveDigit)) {
					octave = Integer.parseInt("" + octaveDigit);
				}
				return 24 + octave * 12 + i;
			}
		}
		return -1;
	}

	public String toString() {
		return "MultisampleBuilder first note: " + noteName(firstNote) + ", " + padNumber + " pads";
	}

	/**
	 * Represents one sample used in a multisample program, including the
	 * chromatic note it is supposed to play and the tuning required to play
	 * this note
	 * 
	 * @author cyrille martraire
	 */
	public class Slot implements Comparable<Slot> {

		private final Object source;

		private final int note;

		private final double tuning;

		public Slot(Object source, int note, double tuning) {
			this.source = source;
			this.note = note;
			this.tuning = tuning;
		}

		public Slot transpose(final int anotherNote) {
			final int requiredTuning = anotherNote - note;
			return new Slot(source, anotherNote, requiredTuning);
		}

		public Object getSource() {
			return source;
		}

		public int getNote() {
			return note;
		}

		public double getTuning() {
			return tuning;
		}

		public int compareTo(Slot o) {
			final Slot other = o;
			return note - other.note;
		}

		/**
		 * @return true if this Slot is equal to the given Slot
		 */
		public boolean equals(Object arg0) {
			if (!(arg0 instanceof Slot)) {
				return false;
			}
			final Slot other = (Slot) arg0;
			if (this == other) {
				return true;
			}
			return note == other.note;
		}

		public int hashCode() {
			return note;
		}

		public String toString() {
			return "Slot " + note + " " + source + " tuning=" + tuning;
		}

	}
}
