package com.mpcmaid.pgm;

import java.util.Set;

/**
 * A byte buffer with its reading and writing methods taking care of the char
 * arrays, the little-endian shorts etc.
 * 
 * @author cyrille martraire
 */
public abstract class BaseElement implements Element {

	private final BaseElement parent;

	private final Buffer buffer;

	private final int offset;

	private final int length;

	private int elementIndex;

	/**
	 * Convenience constructor for the root element
	 */
	protected BaseElement(Buffer buffer) {
		this(null, 0, 0, 0, buffer);
	}

	/**
	 * Convenience constructor for elements with absolute and index-less
	 * addressing
	 */
	protected BaseElement(BaseElement parent) {
		this(parent, 0, 0, 0);
	}

	protected BaseElement(BaseElement parent, int offset, int elementIndex, int length) {
		this(parent, offset, elementIndex, length, new RelativeBuffer(parent.getBuffer(), offset + elementIndex
				* length));
	}

	private BaseElement(BaseElement parent, int offset, int elementIndex, int length, Buffer buffer) {
		this.parent = parent;
		this.offset = offset;
		this.elementIndex = elementIndex;
		this.length = length;
		this.buffer = buffer;
	}

	protected Buffer getParentBuffer() {
		return parent.getBuffer();
	}

	protected Buffer getBuffer() {
		return buffer;
	}

	public Element getParent() {
		return parent;
	}

	public abstract Parameter[] getParameters();

	public Object get(Parameter parameter) {
		return parameter.getType().get(this, parameter);
	}

	public void set(Parameter parameter, Object value) {
		parameter.getType().set(this, parameter, value);
	}

	public int getElementIndex() {
		return elementIndex;
	}

	public void setNextIndex() {
		setElementIndex(elementIndex + 1);
	}

	public void setElementIndex(int elementIndex) {
		this.elementIndex = elementIndex;
		final RelativeBuffer rb = (RelativeBuffer) getBuffer();
		rb.setOffset(offset + elementIndex * length);
	}

	public byte getByte(int index) {
		return buffer.getByte(index);
	}

	public short getShort(int index) {
		return buffer.getShort(index);
	}

	public String getString(int offset) {
		return buffer.getString(offset);
	}

	public void setByte(int index, int value) {
		buffer.setByte(index, value);
	}

	public void setShort(int index, short value) {
		buffer.setShort(index, value);
	}

	public void setString(int offset, String string) {
		buffer.setString(offset, string);
	}

	public Range getRange(int index) {
		return buffer.getRange(index);
	}

	public void setRange(int index, Range value) {
		buffer.setRange(index, value);
	}

	/**
	 * Copies every parameter value of the given source element into this
	 * element, except for the given set of parameters to ignore
	 */
	public void copyFrom(BaseElement source, Set ignoreParams) {
		final Parameter[] ps = getParameters();
		for (int i = 0; i < ps.length; i++) {
			final Parameter parameter = ps[i];
			if (ignoreParams == null || !ignoreParams.contains(parameter)) {
				final Object value = source.get(parameter);
				set(parameter, value);
			}
		}
	}
}
