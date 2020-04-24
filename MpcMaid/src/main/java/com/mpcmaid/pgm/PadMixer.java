package com.mpcmaid.pgm;

/**
 * Represents the mixer and FX section of a pad
 * 
 * @author cyrille martraire
 */
public final class PadMixer extends BaseElement {

	public static final Parameter MIXER_LEVEL = Parameter.integer("Level", 0x8F, 0, 100);

	// 0 to 49=Left, 50=Center, 51 to 100=Right
	public static final Parameter MIXER_PAN = Parameter.integer("Pan", 0x90, 0, 100);

	// 0="Stereo", 1="1-2", 2="3-4"
	public static final Parameter OUTPUT = Parameter.enumType("Output", 0x91, new String[] { "Stereo", "1-2", "3-4" });

	// 0="Off", 1="1", 2="2"
	public static final Parameter FX_SEND = Parameter.enumType("FX Send", 0x92, new String[] { "Off", "1", "2" });

	public static final Parameter FX_SEND_LEVEL = Parameter.integer("FX Send Level", 0x93, 0, 100);

	public final static Parameter[] PARAMETERS = { MIXER_LEVEL, MIXER_PAN, OUTPUT, FX_SEND, FX_SEND_LEVEL };

	protected PadMixer(final Pad parent) {
		super(parent);
	}

	public Parameter[] getParameters() {
		return PARAMETERS;
	}

	public String toString() {
		return getParent().toString() + " Mixer";
	}

}