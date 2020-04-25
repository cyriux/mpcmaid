package com.mpcmaid.pgm;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

public class ProgramInOutTest extends TestCase {

	public void testFileInOut() throws Exception {

		// final File testFile = new File(get
		// assertTrue(testFile.exists());
		Program pgm = Program.open(getClass().getResourceAsStream("test.pgm"));

		final Pad pad = pgm.getPad(0);

		final Layer layer = pad.getLayer(0);
		for (int i = 0; i < 4; i++) {
			pad.setElementIndex(i);
			System.out.println(pad);
			for (int j = 0; j < 4; j++) {
				layer.setElementIndex(j);
				System.out.println(layer);
				System.out.println(layer.getSampleName());
			}
		}
	}

	/**
	 * Copies every parameter of the given source pad but those contained in the
	 * Set ignoreParams, for every pad between fromPad and toPad.
	 * 
	 * @return A Collection of every impacted pad
	 */
	public Collection<Pad> copyPadParameters(Program pgm, Pad sourcePad, final int fromPad, final int toPad, Set<Parameter> ignoreParams) {
		final Collection<Pad> impactedPads = new ArrayList<>();
		for (int i = fromPad; i < toPad; i++) {
			final Pad pad = pgm.getPad(i);
			pad.copyFrom(sourcePad, ignoreParams);
		}
		return impactedPads;
	}

	public void testCopyFrom() throws Exception {
		Program pgm = Program.open(getClass().getResourceAsStream("test.pgm"));
		final Pad currentlySelectedPad = pgm.getPad(0);

		copyPadParameters(pgm, currentlySelectedPad, 3, 12, null);

		pgm.save(new File("copyFrom1.pgm"));

		final HashSet<Parameter> ignoreParams = new HashSet<>();
		ignoreParams.add(Pad.PAD_MIDI_NOTE_VALUE);
		pgm = Program.open(getClass().getResourceAsStream("test.pgm"));
		copyPadParameters(pgm, currentlySelectedPad, 3, 12, ignoreParams);
		pgm.save(new File("copyFrom_note.pgm"));

		ignoreParams.clear();
		pgm = Program.open(getClass().getResourceAsStream("test.pgm"));
		ignoreParams.add(Layer.SAMPLE_NAME);
		copyPadParameters(pgm, currentlySelectedPad, 3, 12, ignoreParams);
		pgm.save(new File("copyFrom_sample.pgm"));

		ignoreParams.clear();
		pgm = Program.open(getClass().getResourceAsStream("test.pgm"));
		ignoreParams.add(Layer.TUNING);
		copyPadParameters(pgm, currentlySelectedPad, 3, 12, ignoreParams);
		pgm.save(new File("copyFrom_tuning.pgm"));
	}

}
