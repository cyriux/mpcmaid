package com.mpcmaid.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * Drag handler to retrieve files. It is intended to be subclassed to process
 * the dragged list of files or each dragged file
 * 
 * @author cyrille martraire
 */
public class FileDragHandler extends TransferHandler {

	public boolean importData(JComponent c, Transferable data) {
		if (!canImport(c, data.getTransferDataFlavors())) {
			return false;
		}
		try {
			final List files = (List) data.getTransferData(DataFlavor.javaFileListFlavor);
			process(files);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	protected void process(List files) {
		for (int i = 0; i < files.size(); i++) {
			File file = (File) files.get(i);
			process(file);
		}
	}

	protected void process(File file) {
		System.out.println(file.getAbsolutePath());
	}

	public boolean canImport(JComponent c, DataFlavor[] flavors) {
		final DataFlavor fileFlavor = DataFlavor.javaFileListFlavor;
		for (int i = 0; i < flavors.length; i++) {
			if (fileFlavor.equals(flavors[i])) {
				return true;
			}
		}
		return false;
	}
}