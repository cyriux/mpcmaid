package com.mpcmaid.audio;

import java.io.IOException;

import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import junit.framework.TestCase;

public class SlicerTest extends TestCase {
	private static final int AVERAGE_ENERGY_WINDOW = 43;

	private static final int OVERLAP_RATIO = 1;

	private static final int WINDOW_SIZE = 1024;

	private static final int MIDI_PPQ = 96;

	protected void setUp() throws Exception {
		final Sample sample = Sample.open(getClass().getResourceAsStream("myLoop.WAV"));
		slicer = new Slicer(sample, WINDOW_SIZE, OVERLAP_RATIO, AVERAGE_ENERGY_WINDOW);
		slicer.extractMarkers();
		markers = slicer.getMarkers();
		System.out.println(slicer);
	}

	private Slicer slicer;

	private Markers markers;

	public void testMarkers() {
		assertEquals("Slicer: 3.8424037s (169450 samples), 9 markers", slicer.toString());
		System.out.println(markers);

		assertEquals(9, markers.size());

		assertFalse(markers.isUnset());

		Marker marker = markers.getSelectedMarker();
		assertEquals(0, marker.getLocation());
		System.out.println(marker);

		markers.selectMarker(4);
		assertEquals(4, markers.getSelectedMarkerIndex());
		assertEquals(79872, markers.getSelectedMarkerLocation());
		assertEquals(79872, markers.getSelectedMarker().getLocation());

		assertEquals(0, markers.getRangeFrom(0).getFrom());

		final LocationRange range3 = markers.getRangeFrom(3);
		System.out.println(range3);
		final LocationRange range4 = markers.getRangeFrom(4);
		System.out.println(range4);

		assertEquals(3.8424037, markers.getDuration(), 0.000001);
		assertEquals(124.92, markers.getTempo(), 0.01);

		// remove
		markers.deleteSelectedMarker();
		assertEquals(8, markers.size());
		assertEquals(3, markers.getSelectedMarkerIndex());

		final LocationRange range3bis = markers.getRangeFrom(3);
		assertEquals(range3.getFrom(), range3bis.getFrom());
		assertEquals(range4.getTo(), range3bis.getTo());

		// last range extends up to the Frame Length
		final LocationRange range7 = markers.getRangeFrom(7);
		assertEquals(169450, range7.getTo());
		System.out.println(range7);

		final int[] midiTicks = { 0, 32, 97, 129, 190, 222, 288, 320, 381, 413, 478, 510, 575, 607, 673, 705, 705 };
		try {
			final Sequence midiSequence = markers.exportMidiSequence(null, MIDI_PPQ);
			final Track track = midiSequence.getTracks()[0];
			assertEquals(17, track.size());
			for (int i = 0; i < track.size(); i++) {
				// System.out.println(track.get(i).getTick());
				assertEquals(midiTicks[i], track.get(i).getTick());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals(3, markers.getSelectedMarkerIndex());
		markers.insertMarker();
		assertEquals(4, markers.getSelectedMarkerIndex());
		assertEquals(73727, markers.getLocation(4));
	}

}
