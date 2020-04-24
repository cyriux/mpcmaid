package com.mpcmaid.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {

	public static final String EXTENSION = ".WAV";

	/**
	 * @return true if the given file has the expected extension
	 */
	public final static boolean hasCorrectExtension(File file) {
		return file.getName().toUpperCase().endsWith(EXTENSION);
	}

	public static String noExtension(File file) {
		return noExtension(file.getName());
	}

	/**
	 * @return The given name without its extension (the term after the last
	 *         dot)
	 */
	public static String noExtension(final String name) {
		String s = name;
		final int indexOf = s.lastIndexOf('.');
		if (indexOf != -1) {
			return s.substring(0, indexOf);
		}
		return s;
	}

	/**
	 * Shorten the name if it is too long, then ensure its unicity thanks to a
	 * postfix incremental number
	 */
	public static String escapeName(final String name, final int length, final boolean brutal, final int renameCount) {
		String escaped = escapeName(name, length, brutal);
		if (renameCount != -1 && !escaped.equals(name)) {
			if (renameCount > 0) {
				final String postfix = String.valueOf(renameCount);
				if (escaped.length() + postfix.length() <= length) {
					escaped = escaped + postfix;
				} else {
					escaped = escaped.substring(0, length - postfix.length()) + postfix;
				}
			}
		}
		return escaped;
	}

	/**
	 * Shorten the name if it is too long, either brutally, or with some trim so
	 * that it does not end on a space, underscore or dot
	 */
	public static String escapeName(final String name, final int length, final boolean brutal) {
		String s = name;
		if (s.length() <= length) {
			return s;
		}
		s = s.substring(0, length).trim();
		if (brutal) {
			return s;
		}
		String s2 = null;
		while (true) {
			s2 = escapeEnding(s);
			if (s == s2) {
				return s.trim();
			}
			s = s2;
		}
	}

	private final static String escapeEnding(String s) {
		if (s.endsWith(".")) {
			return s.substring(0, s.length() - 1);
		}
		if (s.endsWith(" ")) {
			return s.substring(0, s.length() - 1);
		}
		if (s.endsWith("_")) {
			return s.substring(0, s.length() - 1);
		}
		return s;
	}

	// File utils
	public final static void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	public String toString() {
		return "Utils: ";
	}
}
