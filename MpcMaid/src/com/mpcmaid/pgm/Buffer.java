package com.mpcmaid.pgm;

public interface Buffer {

	public abstract String getString(final int offset);

	public abstract void setString(final int offset, String string);

	public abstract short getShort(final int index);

	public abstract void setShort(final int index, final short value);

	public abstract byte getByte(final int index);

	public abstract void setByte(final int index, final int value);

	public abstract Range getRange(final int index);

	public abstract void setRange(final int index, final Range value);

}