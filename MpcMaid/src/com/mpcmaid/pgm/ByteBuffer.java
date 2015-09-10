package com.mpcmaid.pgm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A byte buffer with its reading and writing methods taking care of the char
 * arrays, the little-endian shorts etc.
 * 
 * @author cyrille martraire
 */
public class ByteBuffer implements Buffer {

	private final byte[] bytes;

	private static final int LOW = 0; // RANGE LOW

	private static final int HIGH = 1; // RANGE HIGH

	public ByteBuffer(final int length) {
		this(new byte[length]);
	}

	public ByteBuffer(byte[] bytes) {
		this.bytes = bytes;
	}

	public ByteBuffer(ByteBuffer other) {
		this(other.bytes.length);
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = other.bytes[i];
		}
	}

	public String getString(final int offset) {
		for (int i = 0; i < 16; i++) {
			char ch = (char) bytes[offset + i];
			if (ch == 0) {
				return new String(bytes, offset, i + 1).trim();
			}
		}
		return new String(bytes, offset, 16).trim();
	}

	public void setString(final int offset, String string) {
		if (string.length() > 16) {
			throw new IllegalArgumentException("sampleName: " + string + " is too long (16 chars max)");
		}
		for (int i = 0; i < 16; i++) {
			bytes[offset + i] = 0;
		}
		for (int i = 0; i < string.length(); i++) {
			final char ch = string.charAt(i);
			bytes[offset + i] = (byte) ch;
		}
	}

	public int getInt(final int index) {
		return ((bytes[index + 3] & 0xff) << 24) | ((bytes[index + 2] & 0xff) << 16) | ((bytes[index + 1] & 0xff) << 8)
				| ((bytes[index + 0] & 0xff));
	}

	public void setInt(final int index, final int value) {
		bytes[index] = (byte) (value & 0x00FF);
		bytes[index + 1] = (byte) ((value >> 8) & 0x000000FF);
		bytes[index + 2] = (byte) ((value >> 16) & 0x000000FF);
		bytes[index + 3] = (byte) ((value >> 24) & 0x000000FF);
	}

	public short getShort(final int index) {
		int low = bytes[index] & 0xff;
		int high = bytes[index + 1] & 0xff;
		return (short) (high << 8 | low);
	}

	public void setShort(final int index, final short value) {
		bytes[index] = (byte) value;
		bytes[index + 1] = (byte) (value >> 8);
	}

	public byte getByte(final int index) {
		return bytes[index];
	}

	public void setByte(final int index, final int value) {
		bytes[index] = (byte) value;
	}

	public Range getRange(int index) {
		return new Range(getByte((index + LOW)), getByte((index + HIGH)));
	}

	public void setRange(int index, Range range) {
		setByte((index + LOW), range.getLow());
		setByte((index + HIGH), range.getHigh());
	}

	public static ByteBuffer open(final InputStream fis, final int length) throws IOException {
		final byte[] bytes = new byte[length];
		try {
			fis.read(bytes);
			fis.close();
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				throw e;
			}
		}
		return new ByteBuffer(bytes);
	}

	public void save(final OutputStream fos) throws IOException {
		try {
			fos.write(bytes);
			fos.close();
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				throw e;
			}
		}
	}

	public String toString() {
		return "ByteBuffer (" + bytes.length + " bytes long)";
	}

}
