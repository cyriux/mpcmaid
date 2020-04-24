package com.mpcmaid.pgm;

import java.io.File;

import com.mpcmaid.pgm.Pad;
import com.mpcmaid.pgm.PadMixer;
import com.mpcmaid.pgm.Program;
import com.mpcmaid.pgm.Range;
import com.mpcmaid.pgm.Layer;
import com.mpcmaid.pgm.Slider;

import junit.framework.TestCase;

public class ProgramTest extends TestCase {

	public void testRead() throws Exception {
		Program pgm = Program.open(getClass().getResourceAsStream("test.pgm"));

		// read sample name
		Layer padLayer = pgm.getPad(4).getLayer(0);
		padLayer.getSampleName();

		final int padNumber = pgm.getPadNumber();
		for (int i = 0; i < padNumber; i++) {
			final Pad pad = pgm.getPad(i);
			System.out.println(pad + " fxSendLevel=" + pad.getMixer().get(PadMixer.FX_SEND_LEVEL) + " note: "
					+ pad.getPadMidiNote());

			final int sampleNumber = pad.getLayerNumber();
			for (int j = 0; j < sampleNumber; j++) {
				final Layer sample = pad.getLayer(j);

				final String playModeLabel = (sample.isOneShot()) ? "One Shot" : "Note On";
				System.out.println(sample + ": " + sample.getSampleName() + " level=" + sample.getLevel()
						+ " playMode=" + playModeLabel + " tuning=" + sample.getTuning());
			}

		}

		// change the tuning
		assertEquals(0, padLayer.getTuning(), 0);
		padLayer.setTuning(-0.5);
		final String sampleName = "tomlow";
		padLayer.setSampleName(sampleName);
		final String sampleName2 = padLayer.getSampleName();
		System.out.println(sampleName + "--" + sampleName2 + "--");
		assertEquals(sampleName.trim(), sampleName2);
		assertEquals(-0.5, padLayer.getTuning(), 0);

		final Slider slider1 = pgm.getSlider(0);
		slider1.setSliderParameter(4);
		assertEquals(new Range(9, 99), slider1.get(Slider.ATTACK_RANGE));
		slider1.set(Slider.ATTACK_RANGE, new Range(22, 78));// attack
		assertEquals(new Range(22, 78), slider1.get(Slider.ATTACK_RANGE));

		// save
		pgm.save(new File("test_midified.PGM"));
	}
}
