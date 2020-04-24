package com.mpcmaid.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mpcmaid.pgm.Layer;
import com.mpcmaid.pgm.Pad;
import com.mpcmaid.pgm.Program;
import com.mpcmaid.pgm.Sample;
import com.mpcmaid.pgm.SampleMatrix;

/**
 * Represents the collection of every sample used in a program
 * 
 * @author cyrille martraire
 */
public class ProgramSamples extends SampleMatrix {

	private File path;

	public final static String[] AUDITION_MODES = { "Play only first available sample", "Do not audition samples",
			"Play sample 1", "Play sample 2", "Play sample 3", "Play sample 4" };

	public ProgramSamples() {
	}

	public File getPath() {
		return path;
	}

	public void setPath(File path) {
		this.path = path;
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public void set(final Program program, File programFile) {
		if (programFile == null) {
			clear();
			return;
		}
		setPath(programFile);
		final int n = program.getPadNumber();
		for (int i = 0; i < n; i++) {
			set(program.getPad(i));
		}
	}

	public void set(final Pad pad) {
		final int layerNumber = pad.getLayerNumber();
		for (int i = 0; i < layerNumber; i++) {
			set(pad.getLayer(i));
		}
	}

	public void set(final Layer layer) {
		final String sampleName = layer.getSampleName();
		if (sampleName.trim().length() == 0) {
			remove(layer);
			return;
		}
		if (path == null) {
			return;
		}
		final Sample sample = Sample.findFile(sampleName, path);
		if (sample.isValid()) {
			set(layer, sample);
		}
	}

	public void play(Pad pad) {
		// disgusting static reference, to improve for testability
		final int auditionSamples = Preferences.getInstance().getAuditionSamples();

		if (auditionSamples == 1) {
			return;// do not audition
		}
		if (auditionSamples == 0) {
			final int layerNumber = pad.getLayerNumber();
			for (int i = 0; i < layerNumber; i++) {
				final Layer layer = pad.getLayer(i);
				if (get(layer) != null) {
					play(layer);
					if (auditionSamples == 0) {
						return;// play only first available
					}
					// 0 play each in sequence: cannot do yet
				}
			}
			return;
		}
		if (auditionSamples >= 2 && auditionSamples <= 6) {
			play(pad.getLayer(auditionSamples - 2));// play predefined
		}
	}

	public void play(final Layer layer) {
		try {
			final Sample sample = get(layer);
			if (sample == null) {
				return;
			}
			sample.play();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void remove(final Layer layer) {
		layer.setSampleName("");
		set(layer, null);
	}

	public Collection<Pad> removeAllSamples(Program program) {
		final List<Pad> impactedPads = new ArrayList<>();
		final int n = program.getPadNumber();
		for (int i = 0; i < n; i++) {
			final Pad pad = program.getPad(i);
			final int m = pad.getLayerNumber();
			for (int j = 0; j < m; j++) {
				final Layer layer = pad.getLayer(j);
				remove(layer);
			}
			impactedPads.add(pad);
		}
		return impactedPads;
	}

	public String toString() {
		return super.toString() + " path: " + path.getAbsolutePath();
	}

}
