package com.mpcmaid.pgm.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mpcmaid.pgm.Layer;
import com.mpcmaid.pgm.Pad;
import com.mpcmaid.pgm.Program;
import com.mpcmaid.pgm.Sample;
import com.mpcmaid.pgm.SampleMatrix;
import com.mpcmaid.pgm.Sample.Status;

/**
 * Assigns sample files (provided they are valid, or perhaps renamed during
 * import) to each available pad or each available layer.
 * 
 * @author cyrille martraire
 */
public class SimpleAssignCommand extends ImportCommand {

	private final List<Sample> samples = new ArrayList<>();

	private final Pad selectedPad;

	private final boolean perPad;

	public SimpleAssignCommand(Status errorPolicy, List<File> files, Pad selectedPad, boolean perPad) {
		super(errorPolicy, files);
		this.perPad = perPad;
		this.selectedPad = selectedPad;
	}

	protected void addSample(Sample sample) {
		samples.add(sample);
	}

	public List<Pad> execute(SampleMatrix matrix) {
		final List<Pad> impactedPads = new ArrayList<>();
		final Program program = (Program) selectedPad.getParent();
		final int from = selectedPad.getElementIndex();

		// fills internal list
		importFiles();

		Iterator<Sample> it = samples.iterator();

		final int n = program.getPadNumber();
		for (int i = from; i < n; i++) {
			final Pad pad = program.getPad(i);
			final int m = pad.getLayerNumber();
			for (int j = 0; j < m; j++) {
				final Layer layer = pad.getLayer(j);
				if (matrix.get(layer) == null) {
					// available layer

					if (!it.hasNext()) {
						return impactedPads;
					}
					final Sample sample = (Sample) it.next();
					matrix.set(layer, sample);

					layer.setSampleName(sample.getSampleName());
					impactedPads.add(layer.getPad());

					if (perPad) {
						break;// once per pad only
					}
				}
			}

		}
		return impactedPads;
	}
}
