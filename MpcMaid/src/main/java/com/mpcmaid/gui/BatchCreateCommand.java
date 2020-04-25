package com.mpcmaid.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mpcmaid.pgm.Layer;
import com.mpcmaid.pgm.Program;
import com.mpcmaid.pgm.Sample;

/**
 * A command that walks folders of sample files and creates a listing program
 * automatically.
 * 
 * @pattern Command
 * 
 * @author cyrille martraire
 */
public class BatchCreateCommand {

	private final File root;

	private final Program pgm;

	private final int padNumber;

	private int pgmPostfix = 0;

	private int importCount;

	private int renameCount;

	private int rejectedCount;

	public BatchCreateCommand(File root, Program pgm, final int padNumber) {
		this.root = root;
		this.pgm = pgm;
		this.padNumber = padNumber;
	}

	public void batchCreate(File path) {
		final File[] memberFiles = path.listFiles();
		boolean hasPgm = false;
		for (int i = 0; i < memberFiles.length; i++) {
			final File file = memberFiles[i];
			if (file.isDirectory()) {
				batchCreate(file);
			} else if (file.getName().toLowerCase().endsWith(".pgm")) {
				hasPgm = true;
			}
		}
		if (hasPgm) {
			return;
		}
		createPaginatedPrograms(path, memberFiles);
	}

	private void createPaginatedPrograms(File path, final File[] memberFiles) {
		// create as many programs needed to fill the m pads
		List<File> list = new ArrayList<>();
		int page = 1;

		// constructs the folder name by escaping the path name plus a number to
		// ensure uniqueness
		final String name = path.getName();
		final String listingName = Utils.escapeName(name, 14, false, pgmPostfix);
		if (!listingName.equals(name)) {
			pgmPostfix++;
		}

		for (int i = 0; i < memberFiles.length; i++) {
			final File file = memberFiles[i];
			if (!file.isDirectory() && file.getName().toLowerCase().endsWith(".wav")) {
				list.add(file);
			}
			if (list.size() == padNumber) {
				createProgram(pgm, new File(path, listingName + page + ".pgm"), list);
				page++;
				list.clear();
			}

		}
		createProgram(pgm, new File(path, listingName + page + ".pgm"), list);
	}

	private void createProgram(Program templatePgm, File newPgmFile, List<File> files) {
		if (files.isEmpty()) {
			return;
		}
		// duplicate program used as a template
		final Program newPgm = new Program(templatePgm);

		// import files and assign them to each pad (first layer) in turn
		Iterator<File> it = files.iterator();
		int i = 0;
		while (it.hasNext()) {
			final File file = it.next();
			importCount++;
			final Sample sample = Sample.importFile(file, 16, Sample.RENAMED, true, renameCount);
			if (sample == null || sample.isRejected()) {
				rejectedCount++;
			} else {
				// increments the rename count if needed
				if (sample.isRenamed()) {
					renameCount++;
				}

				// next pad
				final Layer layer = newPgm.getPad(i++).getLayer(0);
				layer.setSampleName(sample.getSampleName());
			}
		}

		// save the new program
		newPgm.save(newPgmFile);
	}

	public void execute() {
		batchCreate(root);
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

	public String toString() {
		return "BatchCreateCommand";
	}

}
