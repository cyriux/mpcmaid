package com.mpcmaid.pgm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Represents an MPC1000 Program (*.pgm file). Note that a program only declares
 * how to deal with samples, it does not contain any actual sound.
 * 
 * The MPC1000 file format used was provided by Stephen Norum on his website:
 * http://mybunnyhug.com/fileformats/pgm/ Many thanks for sharing this format
 * Stephen!
 * 
 * @author cyrille martraire
 */
public class Program extends BaseElement {

	// ------------- HEADER ------
	private static final short FILE_LENGTH = 0x2A04;

	private static final int FILE_SIZE = 0x00; // value is always FILE_LENGTH;

	private static final int FILE_TYPE = 0x04;// set to FILE_VERSION

	private static final String FILE_VERSION = "MPC1000 PGM 1.00";

	// ------------ MIDI ---------------
	// 0-64 n=MIDI Note Number (0 to 127). Value is the Pad Number (0 to 63)
	// associated with the MIDI note number, n. Value is 64 for unassigned MIDI
	// Note Numbers.
	private static final int MIDI_NOTE_PAD_VALUE = 0x2918; // n + 0x2958

	public final static Parameter MIDI_PROGRAM_CHANGE = Parameter.intOrOff("MIDI Program Change", 0x29D8, 0, 128);

	public Program(byte[] bytes) {
		this(new ByteBuffer(bytes));
	}

	public Program(final ByteBuffer buffer) {
		super(buffer);
	}

	public Program(final Program other) {
		super(new ByteBuffer((ByteBuffer) other.getBuffer()));
	}

	public Parameter[] getParameters() {
		return new Parameter[] { MIDI_PROGRAM_CHANGE };
	}

	protected static final void assertIn(final int min, final int value, final int max, String param) {
		if (value < min || value > max) {
			final String msg = "Invalid " + param + " value: " + value + "; must be in [" + min + ".." + max + "]";
			throw new IllegalArgumentException(msg);
		}
	}

	public Pad getPad(final int pad) {
		return new Pad(this, pad);
	}

	public int getPadNumber() {
		return 64;
	}

	public Slider getSlider(final int slider) {
		assertIn(0, slider, 1, "Slider");
		return new Slider(this, slider);
	}

	public int getSlideNumber() {
		return 2;
	}

	public short getMidiProgramChange() {
		return getByte(MIDI_PROGRAM_CHANGE.getOffset());
	}

	public void setMidiProgramChange(final short midiProgramChange) {
		assertIn(0, midiProgramChange, 128, "MidiProgramChange");
		setByte(MIDI_PROGRAM_CHANGE.getOffset(), midiProgramChange);
	}

	public static Program open(final File file) {
		try {
			final ByteBuffer buffer = ByteBuffer.open(new FileInputStream(file), FILE_LENGTH);
			return new Program(buffer);
		} catch (Exception e) {
			throw new IllegalArgumentException("The file " + file.getName() + " is not a valid MPC1000 pgm file.");
		}
	}

	public static Program open(final InputStream is) {
		try {
			final ByteBuffer buffer = ByteBuffer.open(is, FILE_LENGTH);
			return new Program(buffer);
		} catch (Exception e) {
			throw new IllegalArgumentException("Resource is not a valid MPC1000 pgm file.");
		}
	}

	public void save(final File file) {
		try {
			final ByteBuffer byteBuffer = (ByteBuffer) getBuffer();
			byteBuffer.save(new FileOutputStream(file));
		} catch (Exception e) {
			throw new IllegalArgumentException("The file " + file.getName() + " is not a valid MPC1000 pgm file.");
		}
	}

	public String toString() {
		return "Program";
	}

}
