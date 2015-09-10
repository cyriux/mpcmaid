package com.mpcmaid.pgm.command;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import com.mpcmaid.pgm.Sample;
import com.mpcmaid.pgm.SampleCommand;
import com.mpcmaid.pgm.SampleMatrix;

/**
 * Exports every sample file into the destination directory.
 * 
 * @author cyrille martraire
 */
public final class ExportCommand implements SampleCommand {
	private final File dir;

	private int exported = 0;

	private int expected = 0;

	public ExportCommand(File dir) {
		this.dir = dir;
	}

	public Object execute(SampleMatrix matrix) {
		final Iterator it = matrix.collectAll().iterator();
		while (it.hasNext()) {
			Sample sample = (Sample) it.next();
			expected++;
			try {
				sample.copyTo(dir);
				exported++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return getReport();
	}

	public Object getReport() {
		if (hasError()) {
			return "Exported " + exported + " sample files out of " + expected + " (invalid files or files not found";
		}
		return "Exported every " + exported + " sample files successfully";
	}

	public boolean hasError() {
		return exported != expected;
	}

}
