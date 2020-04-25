package com.mpcmaid.pgm;

/**
 * @pattern Decorator target=Buffer forwards every call to setter and getter to
 *          the underlying buffer with a constant offset added to the buffer
 *          index
 * 
 * @author cyrille martraire
 */
public class RelativeBuffer implements Buffer {

	protected final Buffer buffer;

	protected int offset;

	protected RelativeBuffer(Buffer buffer, final int offset) {
		this.buffer = buffer;
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public byte getByte(int index) {
		return buffer.getByte(index + offset);
	}

	public short getShort(int index) {
		return buffer.getShort(index + offset);
	}

	public String getString(int index) {
		return buffer.getString(index + offset);
	}

	public void setByte(int index, int value) {
		buffer.setByte(index + offset, value);
	}

	public void setShort(int index, short value) {
		buffer.setShort(index + offset, value);
	}

	public void setString(int index, String string) {
		buffer.setString(index + offset, string);
	}

	public Range getRange(int index) {
		return buffer.getRange(index + offset);
	}

	public void setRange(int index, Range value) {
		buffer.setRange(index + offset, value);
	}

	public String toString() {
		return "RelativeBuffer on: " + buffer + " offset=" + offset;
	}
}
