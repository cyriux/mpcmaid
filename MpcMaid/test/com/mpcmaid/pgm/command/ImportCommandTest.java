package com.mpcmaid.pgm.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import com.mpcmaid.pgm.Sample;
import com.mpcmaid.pgm.SampleMatrix;
import com.mpcmaid.pgm.Sample.Status;

public class ImportCommandTest extends TestCase {

	public static final class SampleList extends ImportCommand {
		private final List list = new ArrayList();

		public SampleList(Status errorPolicy, List files) {
			super(errorPolicy, files);
			importFiles();
		}

		protected void addSample(Sample sample) {
			list.add(sample);
		}

		public Sample[] getSamples() {
			return (Sample[]) list.toArray(new Sample[list.size()]);
		}

		// unused
		public Object execute(SampleMatrix matrix) {
			return null;
		}

		public int size() {
			return list.size();
		}

		public String getReport() {
			return super.getReport();
		}

		public boolean hasError() {
			return super.hasError();
		}

	}

	public void testSampleList_allOK() {
		File fileOk = new File("chh.wav");

		SampleList list = new SampleList(Sample.RENAMED, Arrays.asList(new File[] { fileOk }));
		assertEquals(1, list.size());
		assertTrue(list.getSamples()[0].isValid());
		assertFalse(list.hasError());
		System.out.println(list.getReport());
	}

	public void testSampleList_1renamed() {
		File fileOk = new File("chh.wav");
		File fileTooLong = new File("chh45678901234567.wav");

		SampleList list = new SampleList(Sample.RENAMED, Arrays.asList(new File[] { fileTooLong, fileOk }));
		assertEquals(2, list.size());
		assertTrue(list.getSamples()[1].isValid());
		assertTrue(list.getSamples()[0].isRenamed());
		assertTrue(list.hasError());
		System.out.println(list.getReport());
	}

	public void testSampleList_1rejected() {
		File fileOk = new File("chh.wav");
		File fileTooLong = new File("chh45678901234567.wav");

		SampleList list = new SampleList(Sample.REJECTED, Arrays.asList(new File[] { fileTooLong, fileOk }));
		assertEquals(1, list.size());
		assertTrue(list.getSamples()[0].isValid());
		assertTrue(list.hasError());
		System.out.println(list.getReport());
	}

}
