package com.mpcmaid.pgm;

/**
 * Represents the filter1 section of a pad
 * 
 * @author cyrille martraire
 */
public final class PadFilter1 extends BaseElement {

	// 0="Off", 1="Lowpass", 2="Bandpass", 3="Highpass"
	public static final Parameter FILTER1_TYPE = Parameter.enumType("Type", 0x71, new String[] { "Off", "Lowpass",
			"Bandpass", "Highpass" });

	public static final Parameter FILTER1_FREQ = Parameter.integer("Frequency", 0x72, 0, 100);

	public static final Parameter FILTER1_RES = Parameter.integer("Resonance", 0x73, 0, 100);

	public static final Parameter FILTER1_VELOCITY_TO_FREQ = Parameter.integer("Velocity to Freq.", 0x78, 0, 100);

	// 0="0dB", 1="-6dB", 2="-12dB"
	public static final Parameter FILTER_ATTN = Parameter.enumType("Pre-attenuation", 0x94, new String[] { "0dB",
			"-6dB", "-12dB" });

	public final static Parameter[] PARAMETERS = { FILTER1_TYPE, FILTER1_FREQ, FILTER1_RES, FILTER1_VELOCITY_TO_FREQ,
			FILTER_ATTN };

	protected PadFilter1(final Pad parent) {
		super(parent);
	}

	public Parameter[] getParameters() {
		return PARAMETERS;
	}

	public String toString() {
		return getParent().toString() + " Filter1";
	}
}