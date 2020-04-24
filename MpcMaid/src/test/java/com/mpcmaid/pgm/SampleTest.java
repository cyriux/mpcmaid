package com.mpcmaid.pgm;

import java.io.File;

import junit.framework.TestCase;

public class SampleTest extends TestCase {

	public void test_importFile_invalidExtension() throws Exception {
		// not a wave file!
		File invalidFile = new File("test.pgm");
		Sample sample = Sample.importFile(invalidFile, 16, Sample.RENAMED, true, 0);
		assertNull(sample);
	}

	public void test_importFile_ok() throws Exception {
		final File dir = new File("dummydir");

		// valid file
		File fileOk = new File("chh.wav");
		Sample sample = Sample.importFile(fileOk, 16, Sample.RENAMED, true, 0);
		assertEquals(Sample.OK, sample.getStatus());
		assertEquals(fileOk, sample.getActualFile());
		assertEquals(dir, sample.getDestinationFile(dir).getParentFile());

		assertEquals(sample.getName(), sample.getOriginalName());
		assertEquals(sample.getOriginalName(), sample.getSampleName() + ".wav");

		assertFalse(sample.isRenamed());
		assertTrue(sample.getName().endsWith(".wav"));
		assertTrue(sample.getOriginalName().endsWith(".wav"));
		assertFalse(sample.getSampleName().endsWith(".wav"));
	}

	public void test_importFile_fileTooLong() throws Exception {
		final File dir = new File("dummydir");

		// valid file, name too long before the extension + RENAME policy
		File fileTooLong = new File("chh45678901234567.wav");
		Sample sample = Sample.importFile(fileTooLong, 16, Sample.RENAMED, true, 12);
		assertEquals(Sample.RENAMED, sample.getStatus());
		assertEquals(fileTooLong, sample.getActualFile());
		assertEquals(dir, sample.getDestinationFile(dir).getParentFile());

		assertFalse(sample.getName().equals(sample.getOriginalName()));
		assertFalse(sample.getOriginalName().equals(sample.getSampleName()));

		assertTrue(sample.isRenamed());

		assertTrue(sample.getName().endsWith(".wav"));
		assertTrue(sample.getOriginalName().endsWith(".wav"));
		assertFalse(sample.getSampleName().endsWith(".wav"));

		// make sure the file ends with the rename count
		assertTrue(sample.getSampleName().endsWith("12"));
	}

	public void test_findFile_notFound() throws Exception {
		final File path = getPath();

		final File dir = new File("dummydir");

		Sample sample = Sample.findFile("xxx", path);
		assertNotNull(sample);
		assertEquals(Sample.NOT_FOUND, sample.getStatus());

		assertNull(sample.getActualFile());
		assertNull(sample.getDestinationFile(dir));
		assertNull(sample.getOriginalName());

		assertNull(sample.getName());
		assertEquals("xxx", sample.getSampleName());

		assertFalse(sample.isRenamed());
		assertFalse(sample.isValid());
	}

	private File getPath() {
		final File file = new File(getClass().getResource("chh.wav").getFile());
		final File path = file.getParentFile();
		return path;
	}

	public void test_findFile_ok() throws Exception {
		final File path = getPath();
		final File dir = new File("dummydir");
		final File file = new File(getClass().getResource("chh.wav").getFile());

		Sample sample = Sample.findFile("chh", path);
		assertNotNull(sample);
		assertEquals(Sample.OK, sample.getStatus());
		assertEquals(file, sample.getActualFile());
		assertEquals(dir, sample.getDestinationFile(dir).getParentFile());

		assertEquals(sample.getName(), sample.getOriginalName());
		assertEquals(sample.getOriginalName(), sample.getSampleName() + ".wav");

		assertFalse(sample.isRenamed());
		assertTrue(sample.getName().endsWith(".wav"));
		assertTrue(sample.getOriginalName().endsWith(".wav"));
		assertFalse(sample.getSampleName().endsWith(".wav"));
	}
}
