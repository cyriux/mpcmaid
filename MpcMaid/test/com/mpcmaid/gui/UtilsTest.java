package com.mpcmaid.gui;

import junit.framework.TestCase;

public class UtilsTest extends TestCase {

	public static String escapeSampleName(final String name, int length) {
		final String noExt = Utils.noExtension(name);
		final String escapeName = Utils.escapeName(noExt, length, false);
		
		return escapeName;
	}

	public void testEscapeName() {
		assertEquals(escapeSampleName("123456789123456", 16), "123456789123456");

		assertEquals(escapeSampleName("1", 16), "1");

		assertEquals(escapeSampleName("1234567890123456.", 16), "1234567890123456");
		assertEquals(escapeSampleName("1234567890123456.wav", 16), "1234567890123456");
		assertEquals(escapeSampleName("123456789012345.abcd.wav", 16), "123456789012345");
		assertEquals(escapeSampleName("123456789012345", 16), "123456789012345");
		assertEquals(escapeSampleName("123456789012345 ", 16), "123456789012345 ");
		assertEquals(escapeSampleName("123456789012345.", 16), "123456789012345");

		assertEquals(escapeSampleName("123456789012345678.wav", 16), "1234567890123456");
		assertEquals(escapeSampleName("123456789012345_78.wav", 16), "123456789012345");
	}

	public void testPathToListing() {
		assertEquals(escapeSampleName("1234567890123456", 14), "12345678901234");

		assertEquals(escapeSampleName("1", 14), "1");

		assertEquals(escapeSampleName("1234567890123.", 14), "1234567890123");
		assertEquals(escapeSampleName("123456789012", 13), "123456789012");
		assertEquals(escapeSampleName("123456789012 ", 13), "123456789012 ");
		assertEquals(escapeSampleName("123456789012_", 13), "123456789012_");
		assertEquals(escapeSampleName("123456789012 4", 13), "123456789012");
		assertEquals(escapeSampleName("123456789012.4", 13), "123456789012");

		assertEquals(escapeSampleName("1234567890123.12345", 14), "1234567890123");
		assertEquals(escapeSampleName("1234567890123_13245", 14), "1234567890123");
	}

	public void testMultipleRenaming() throws Exception {
		final String[] names = { "TicTacShutUp_click_1_d.wav", "TicTacShutUp_click_1_off_click.wav",
				"TicTacShutUp_light_1.wav" };
		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < names.length; i++) {
				String name = names[i];
				final String noExt = Utils.noExtension(name);
				Utils.escapeName(noExt, 16, true);
			}
		}
	}

}
