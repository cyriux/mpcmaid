package com.mpcmaid.pgm;

import java.util.Set;

/**
 * Represents a pad out of the 64 pads on the Mpc1000
 * 
 * @author cyrille martraire
 */
public final class Pad extends BaseElement {

	// /0-127 m=Pad Number (0 to 63). Value of character is the MIDI note number
	// associated with Pad Number m ; m + 0x2918
	public static final Parameter PAD_MIDI_NOTE_VALUE = Parameter.enumType("Note", 0x2918, notes());

	// 0="Poly", 1="Mono"
	private static final Parameter VOICE_OVERLAP = Parameter.enumType("Voice Overlap", 0x62, new String[] { "Poly",
			"Mono" });

	// 0="Off", 1 to 32
	private static final Parameter MUTE_GROUP = Parameter.intOrOff("Mute Group", 0x63, 0, 32);

	private final static Parameter[] PARAMETERS = { PAD_MIDI_NOTE_VALUE, VOICE_OVERLAP, MUTE_GROUP };

	// ----
	private static final int PAD_SECTION = 0x14 + 4;// start of pad section

	private static final int PAD_LENGTH = 0xA4;// length of pad section

	protected Pad(final Program parent, int padIndex) {
		super(parent, PAD_SECTION, padIndex, PAD_LENGTH);
		Program.assertIn(0, padIndex, 63, "pad");
	}

	public Pad copy() {
		return new Pad((Program) getParent(), getPadIndex());

	}

	public Parameter[] getParameters() {
		return PARAMETERS;
	}

	public int getPadIndex() {
		return getElementIndex();
	}

	public PadEnvelope getEnvelope() {
		return new PadEnvelope(this);
	}

	public BaseElement getFilter(int i) {
		return (i == 0) ? (BaseElement) getFilter1() : (BaseElement) getFilter2();
	}

	public PadFilter1 getFilter1() {
		return new PadFilter1(this);
	}

	public PadFilter2 getFilter2() {
		return new PadFilter2(this);
	}

	public PadMixer getMixer() {
		return new PadMixer(this);
	}

	public BaseElement[] getPages() {
		return new BaseElement[] { getEnvelope(), getFilter1(), getFilter2(), getMixer() };
	}

	public Layer getLayer(final int layerIndex) {
		return new Layer(this, layerIndex);
	}

	public short getVoiceOverlap() {
		return getByte(VOICE_OVERLAP.getOffset());
	}

	public short getPadMidiNote() {
		return getParentBuffer().getByte(PAD_MIDI_NOTE_VALUE.getOffset() + getPadIndex());
	}

	public void setPadMidiNote(final int midiNote) {
		Program.assertIn(0, midiNote, 127, "Pad MidiNote");
		getParentBuffer().setByte(PAD_MIDI_NOTE_VALUE.getOffset() + getPadIndex(), midiNote);
	}

	// public short getMidiNotePad() {
	// return parent.getParameter(MIDI_NOTE_PAD_VALUE + pad);
	// }
	//
	// public void setMidiNotePad(final short midiNote) {
	// assertIn(0, midiNote, 64, "MidiNote Pad");
	// parent.setParameter(MIDI_NOTE_PAD_VALUE + pad, (byte) midiNote);
	// }

	public Object get(Parameter parameter) {
		if (parameter.equals(PAD_MIDI_NOTE_VALUE)) {
			return Integer.valueOf(getPadMidiNote() - 35);
		}
		return super.get(parameter);
	}

	public void set(Parameter parameter, Object value) {
		if (parameter.equals(PAD_MIDI_NOTE_VALUE)) {
			setPadMidiNote((short) (((Integer) value).shortValue() + 35));
			return;
		}
		super.set(parameter, value);
	}

	/**
	 * Copies every parameter from the given source element, ignoring parameters given in the Set ignoreParams
	 */
	public void copyFrom(BaseElement source, Set<Parameter> ignoreParams) {
		final Pad sourcePad = (Pad) source;

		super.copyFrom(sourcePad, ignoreParams);

		final BaseElement[] pages = getPages();
		final BaseElement[] sourcePages = sourcePad.getPages();
		for (int j = 0; j < pages.length; j++) {
			final BaseElement page = pages[j];
			page.copyFrom(sourcePages[j], ignoreParams);
		}
		for (int j = 0; j < getLayerNumber(); j++) {
			Layer layer = getLayer(j);
			final Layer sourceLayer = sourcePad.getLayer(j);
			layer.copyFrom(sourceLayer, ignoreParams);
		}
	}

	public int getLayerNumber() {
		return 4;
	}

	private final static String[] notes() {
		final String[] noteNames = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
		final String[] notes = new String[64];
		for (int i = 0; i < notes.length; i++) {
			final int k = 35 + i;
			final int chromatic = (k - 24) % 12;
			final int octave = (k - 24) / 12;
			notes[i] = "(" + k + ") - " + noteNames[chromatic] + octave;
		}
		return notes;
	}

	public String toString() {
		return "Pad" + (getPadIndex() + 1);
	}

}