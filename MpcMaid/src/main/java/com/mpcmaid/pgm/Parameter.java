package com.mpcmaid.pgm;

/**
 * Represents the definition of a parameter, including its readable name, its
 * possible value or value range, its type and integer index.
 * 
 * @author cyrille martraire
 */
public class Parameter {

	public static interface Type {

		Object get(BaseElement element, Parameter parameter);

		void set(BaseElement element, Parameter parameter, Object value);

		boolean validate(Object o);

		String toolTip();
	}

	public static final class TextType implements Type {

		public int getStringLength() {
			return 16;
		}

		public void validate(String string, String explanation) {
			if (validate(string)) {
				throw new IllegalArgumentException(explanation + ": " + string + " is too long (16 chars max)");
			}
		}

		public boolean validate(Object o) {
			final String string = (String) o;
			return string.length() > 16;
		}

		public Object get(BaseElement element, Parameter parameter) {
			return element.getBuffer().getString(parameter.getOffset());
		}

		public void set(BaseElement element, Parameter parameter, Object value) {
			final String string = (String) value;
			validate(string, parameter.getLabel());
			element.getBuffer().setString(parameter.getOffset(), string);
		}

		public String toolTip() {
			return "Max length of 16 characters";
		}

		public String toString() {
			return "LabelType";
		}
	}

	public static abstract class NumberType implements Type {
		private final Range range;

		protected NumberType(Range range) {
			this.range = range;
		}

		public Range getRange() {
			return range;
		}

		protected void validate(Integer value, String explanation) {
			if (!validate(value)) {
				final String msg = "Invalid " + explanation + " value: " + value + "; must be in " + range;
				throw new IllegalArgumentException(msg);
			}
		}

		public boolean validate(Object value) {
			final Number v = (Number) value;
			return range.contains(v.doubleValue());
		}

		public Object get(BaseElement element, Parameter parameter) {
			final byte b = element.getBuffer().getByte(parameter.getOffset());
			return new Integer(b);
		}

		public void set(BaseElement element, Parameter parameter, Object value) {
			final Integer i = (Integer) value;
			final int val = i.intValue();
			validate(i, parameter.getLabel());
			element.getBuffer().setByte(parameter.getOffset(), val);
		}

		public String toolTip() {
			return "Min = " + getRange().getLow() + ", max = " + getRange().getHigh();
		}

		public String toString() {
			return " range=" + range;
		}
	}

	public static final class IntType extends NumberType {

		protected IntType(Range range) {
			super(range);
		}

	}

	public static final class OffIntType extends NumberType {

		protected OffIntType(Range range) {
			super(range);
		}

	}

	public static final class TuningType extends NumberType {

		protected TuningType(Range range) {
			super(range);
		}

		public boolean validate(Object value) {
			final Number d = (Number) value;
			return super.validate(d);
		}

	}

	public static final class RangeType extends NumberType {

		protected RangeType(Range range) {
			super(range);
		}

		public Object get(BaseElement element, Parameter parameter) {
			return element.getBuffer().getRange(parameter.getOffset());
		}

		public void set(BaseElement element, Parameter parameter, Object value) {
			final Range r = (Range) value;
			element.getBuffer().setRange(parameter.getOffset(), r);
		}

	}

	public static final class EnumType implements Type {

		private final String[] values;

		protected EnumType(String[] values) {
			this.values = values;
		}

		public String[] getValues() {
			return values;
		}

		public Object get(BaseElement element, Parameter parameter) {
			final byte b = element.getBuffer().getByte(parameter.getOffset());
			return new Integer(b);
		}

		public void set(BaseElement element, Parameter parameter, Object value) {
			final Integer i = (Integer) value;
			final int val = i.intValue();
			validate(i, parameter.getLabel());
			element.getBuffer().setByte(parameter.getOffset(), val);
		}

		private void validate(Integer value, String explanation) {
			if (!validate(value)) {
				final String msg = "Invalid " + explanation + " value: " + value;
				throw new IllegalArgumentException(msg);
			}
		}

		public boolean validate(Object o) {
			final Integer v = (Integer) o;
			final int value = v.intValue();
			return value >= 0 && value < values.length;
		}

		public String toolTip() {
			return null;
		}
	}

	private final String label;

	private final Type type;

	private final int index;

	protected Parameter(final String label, final int index, Type type) {
		this.label = label;
		this.index = index;
		this.type = type;
	}

	public static final Parameter range(final String label, final int index, final Range valueRange) {
		return new Parameter(label, index, new RangeType(valueRange));
	}

	public static final Parameter range(final String label, final int index, final int min, final int max) {
		return range(label, index, new Range(min, max));
	}

	public static final Parameter integer(final String label, final int index, final Range valueRange) {
		return new Parameter(label, index, new IntType(valueRange));
	}

	public static final Parameter integer(final String label, final int index, final int min, final int max) {
		return integer(label, index, new Range(min, max));
	}

	public static final Parameter intOrOff(final String label, final int index, final int min, final int max) {
		return new Parameter(label, index, new OffIntType(new Range(min, max)));
	}

	public static final Parameter decimal(final String label, final int index, final Range valueRange) {
		return new Parameter(label, index, new TuningType(valueRange));
	}

	public static final Parameter string(final String label, final int index, final int stringLength) {
		return new Parameter(label, index, new TextType());
	}

	public static final Parameter enumType(final String label, final int index, final String[] possibleValues) {
		return new Parameter(label, index, new EnumType(possibleValues));
	}

	public String getLabel() {
		return label;
	}

	public Type getType() {
		return type;
	}

	public int getIndex() {
		return index;
	}

	public int getOffset() {
		return index;
	}

	public String toString() {
		return "Parameter " + label;
	}

}
