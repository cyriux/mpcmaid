package com.mpcmaid.pgm;

/**
 * Represents the filter2 section of a pad
 * 
 * @author cyrille martraire
 */
public final class PadFilter2 extends BaseElement {

	// 0="Off", 1="Lowpass", 2="Bandpass", 3="Highpass", 4="Link"
	public static final Parameter FILTER2_TYPE = Parameter.enumType("Type", 0x79, new String[] { "Off", "Lowpass",
			"Bandpass", "Highpass", "Link" });

	public static final Parameter FILTER2_FREQ = Parameter.integer("Frequency", 0x7A, 0, 100);

	public static final Parameter FILTER2_RES = Parameter.integer("Resonance", 0x7B, 0, 100);

	public static final Parameter FILTER2_VELOCITY_TO_FREQ = Parameter.integer("Velocity to Freq.", 0x80, 0, 100);

	public final static Parameter[] PARAMETERS = { FILTER2_TYPE, FILTER2_FREQ, FILTER2_RES, FILTER2_VELOCITY_TO_FREQ };

	protected PadFilter2(final Pad parent) {
		super(parent);
	}

	public Parameter[] getParameters() {
		return PARAMETERS;
	}

	public String toString() {
		return getParent().toString() + " Filter2";
	}
}