package com.mpcmaid.pgm;

import java.io.File;
import java.io.IOException;

import com.mpcmaid.audio.SamplePlayer;
import com.mpcmaid.gui.Utils;

/**
 * Represents a sample, with its sample name always shorter than 16 chars
 * without the extension, and the corresponding file.
 * 
 * @author cyrille martraire
 */
public final class Sample {

	public static class Status {
		private final String label;

		private Status(String label) {
			this.label = label;
		}

		public String toString() {
			return label;
		}
	}

	public final static Status OK = new Status("OK");

	public final static Status REJECTED = new Status("REJECTED");

	public final static Status IGNORED = new Status("IGNORED");

	public final static Status RENAMED = new Status("RENAMED");

	public final static Status NOT_FOUND = new Status("NOT_FOUND");

	private final File actualFile;

	private final String name;

	private final Status status;

	/**
	 * @return A Sample instances built from the imported file, renamed or null
	 *         if the file name is too long (in the case of importing sample
	 *         files into a program)
	 */
	public static Sample importFile(final File file, final int length, final Status errorStatus, final boolean brutal,
			int renameCount) {
		if (!Utils.hasCorrectExtension(file)) {
			return null;
		}
		final String noExtension = Utils.noExtension(file);
		if (noExtension.length() <= length) {
			return new Sample(file, noExtension, OK);
		}
		// name too long
		if (errorStatus == RENAMED) {
			final String renamed = Utils.escapeName(noExtension, length, brutal, renameCount);
			return new Sample(file, renamed, errorStatus);
		}
		return new Sample(file, noExtension, errorStatus);
	}

	/**
	 * @return A Sample instances built from the sample name and the current
	 *         path of the PGM program (in the case of loading an existing
	 *         program)
	 */
	public static Sample findFile(String sampleName, File path) {
		if (sampleName == null || sampleName.trim().length() == 0) {
			return null;
		}
		File file = new File(path, sampleName + ".wav");
		if (file.exists()) {
			return new Sample(file, sampleName, OK);
		}
		file = new File(path, sampleName + ".WAV");
		if (file.exists()) {
			return new Sample(file, sampleName, OK);
		}
		return new Sample(null, sampleName, NOT_FOUND);
	}

	private Sample(File importedFile, String name, Status status) {
		this.actualFile = importedFile;
		this.name = name;
		this.status = status;
	}

	/**
	 * The original name without the extension
	 */
	public String getOriginalName() {
		return actualFile == null ? null : actualFile.getName();
	}

	public Status getStatus() {
		return status;
	}

	public boolean isRenamed() {
		return status == RENAMED;
	}

	public boolean isRejected() {
		return status == REJECTED;
	}

	public boolean isTooLong() {
		return status == IGNORED;
	}

	public boolean isNotFound() {
		return status == NOT_FOUND;
	}

	public boolean isValid() {
		return status == OK;
	}

	public boolean hasWarning() {
		return isRenamed() || isRejected() || isTooLong() || isNotFound();
	}

	/**
	 * The name with the extension
	 */
	public String getName() {
		if (isRenamed()) {
			return getSampleName() + ".wav";
		}
		if (isRejected() || isNotFound()) {
			return null;
		}
		// IGNORED || OK
		return getOriginalName();
	}

	public File getActualFile() {
		return actualFile;
	}

	/**
	 * The name without the extension
	 */
	public String getSampleName() {
		return name;
	}

	public File getDestinationFile(File dir) throws IOException {
		final String dstName = getName();
		if (actualFile == null || dstName == null) {
			return null;
		}
		return new File(dir, dstName);
	}

	public void copyTo(File dir) throws IOException {
		final File dst = getDestinationFile(dir);
		if (dst == null) {
			return;
		}
		Utils.copy(actualFile, dst);
	}

	public void convertTo(File dir) throws IOException {
		final File dst = getDestinationFile(dir);
		if (dst == null) {
			return;
		}
		Utils.resample(actualFile, dst);
	}

	public void play() {
		if (actualFile != null) {
			SamplePlayer.getInstance().play(actualFile);
		}
	}

	public String toString() {
		return "Sample name: " + name + " " + status + " imported file: " + actualFile;
	}

}
