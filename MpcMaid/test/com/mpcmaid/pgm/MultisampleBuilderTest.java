package com.mpcmaid.pgm;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import com.mpcmaid.pgm.MultisampleBuilder.Slot;

public class MultisampleBuilderTest extends TestCase {

	public void testNoteName() throws Exception {
		final List filenames = collectFileNames(1);

		final int commonIndex = MultisampleBuilder.longuestPrefix(filenames);
		// System.out.println(commonIndex);

		int i = 35;
		Iterator it = filenames.iterator();
		while (it.hasNext()) {
			String word = (String) it.next();
			final String variablePart = word.substring(commonIndex);
			final int note = MultisampleBuilder.extractNote(variablePart);
			// System.out.println(note);
			assertEquals(i++, note);
		}
	}

	public void testExtractNote() throws Exception {
		final int note = MultisampleBuilder.extractNote("ROH 40 E 1");
		assertEquals(40, note);
	}

	public void testAssignment() throws Exception {
		final List filenames = collectFileNames(4);
		final List samples = toSamples(filenames);

		final MultisampleBuilder builder = new MultisampleBuilder();
		final Slot[] multisample = builder.assign(samples);

		for (int i = 0; i < multisample.length; i++) {
			Slot slot = multisample[i];
			System.out.println(slot);
		}
	}

	private static List collectFileNames(int step) {
		final List list = new ArrayList();
		for (int i = 0; i < 64; i += step) {
			final int k = 35 + i;
			final String noteName = MultisampleBuilder.noteName(k);

			final String spacer = noteName.length() == 2 ? " " : "";
			final String fileName = "WLS C" + k + noteName + (spacer) + "#" + i;
			// System.out.println(fileName);
			list.add(fileName);
			final int note = MultisampleBuilder.extractNote(fileName);
			assertEquals(k, note);
		}
		return list;
	}

	protected final static List toSamples(final List fileNames) {
		final List list = new ArrayList();
		final Iterator it = fileNames.iterator();
		while (it.hasNext()) {
			final String fileName = (String) it.next();
			final Sample sample = toSample(fileName);
			System.out.println(sample);
			list.add(sample);
		}
		return list;
	}

	protected final static Sample toSample(final String fileName) {
		return Sample.importFile(new File(fileName + ".WAV"), 16, Sample.OK, false, 0);
	}
}
