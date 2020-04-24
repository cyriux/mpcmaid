package com.mpcmaid.pgm.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mpcmaid.pgm.Layer;
import com.mpcmaid.pgm.MultisampleBuilder;
import com.mpcmaid.pgm.Pad;
import com.mpcmaid.pgm.Program;
import com.mpcmaid.pgm.Sample;
import com.mpcmaid.pgm.SampleMatrix;
import com.mpcmaid.pgm.MultisampleBuilder.Slot;
import com.mpcmaid.pgm.Sample.Status;

/**
 * Imports sample files to create a multisample program
 * 
 * @author cyrille martraire
 */
public class MultiSampleCommand extends ImportCommand {

	private final List samples = new ArrayList();

	private final Program pgm;

	public MultiSampleCommand(Status errorPolicy, List files, Program pgm) {
		super(errorPolicy, files);
		this.pgm = pgm;
	}

	protected void addSample(Sample sample) {
		samples.add(sample);
	}

	public Object execute(SampleMatrix matrix) {
		// process raw files (rename, reject etc)
		importFiles();

		final MultisampleBuilder builder = new MultisampleBuilder();
		final Slot[] multisample = builder.assign(samples);

		rejectedCount += builder.getWarnings().size();

		// print
		for (int i = 0; i < multisample.length; i++) {
			Slot slot = multisample[i];
			System.out.println(slot);
		}

		// assign
		final Collection impactedPads = new ArrayList();
		for (int i = 0; i < multisample.length; i++) {
			Slot slot = multisample[i];
			if (slot != null) {
				final Pad pad = pgm.getPad(i);
				final Layer layer = pad.getLayer(0);
				final Sample sample = (Sample) slot.getSource();

				matrix.set(layer, sample);

				layer.setSampleName(sample.getSampleName());
				layer.setTuning(slot.getTuning());
				pad.setPadMidiNote(slot.getNote());
				layer.setNoteOn();

				impactedPads.add(pad);
			}
		}

		return impactedPads;
	}

}
