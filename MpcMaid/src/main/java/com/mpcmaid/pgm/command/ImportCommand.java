package com.mpcmaid.pgm.command;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import com.mpcmaid.pgm.Sample;
import com.mpcmaid.pgm.SampleCommand;
import com.mpcmaid.pgm.Sample.Status;

/**
 * Imports files and processes them into Sample objects for later use.
 * 
 * Subclasses must call importFiles() to process the imported files before use.
 * 
 * @author cyrille martraire
 */
public abstract class ImportCommand implements SampleCommand {

	private final List files;

	private final Status errorPolicy;

	private int importCount;

	private int renameCount;

	protected int rejectedCount;

	public ImportCommand(Status errorPolicy, List files) {
		this.errorPolicy = errorPolicy;
		this.files = files;
	}

	public boolean hasError() {
		return renameCount != 0 || rejectedCount != 0;
	}

	/**
	 * @return A message to inform about anomalies that may have happened
	 */
	public String getReport() {
		if (!hasError()) {
			return "Imported " + importCount + " files";
		}
		String msg = "Imported " + importCount + " files, of which ";
		if (renameCount != 0) {
			msg += renameCount + " have been renamed (name too long)";
		}
		if (rejectedCount != 0) {
			if (renameCount != 0) {
				msg += ", and ";
			}
			msg += rejectedCount + " have been ignored (invalid format)";
		}
		return msg;
	}

	/**
	 * Adds the Sample instances built from the given files (ignoring files with
	 * an extension other than .wav, renaming file names too long)
	 */
	protected void importFiles() {
		Iterator it = files.iterator();
		while (it.hasNext()) {
			final File file = (File) it.next();
			importCount++;
			final Sample sample = Sample.importFile(file, 16, errorPolicy, true, renameCount);
			if (sample == null || sample.isRejected()) {
				rejectedCount++;
			} else {
				// increments the rename count if needed
				if (sample.isRenamed()) {
					renameCount++;
				}
				addSample(sample);
			}
		}
	}

	/**
	 * @pattern TemplateMethod
	 */
	protected abstract void addSample(final Sample sample);

	public String toString() {
		return getReport();
	}

}
