package com.mpcmaid.audio;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.AudioFileFormat.Type;

/**
 * Represents an audio sample
 * 
 * @author cyrille martraire
 */
public class Sample {

	private final byte[] bytes;

	private final AudioFormat format;

	private final int frameLength;

	public static Sample open(final File file) throws Exception {
		final AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
		return open(audioStream);
	}

	protected static Sample open(final InputStream is) throws Exception {
		final AudioInputStream audioStream = AudioSystem.getAudioInputStream(is);
		return open(audioStream);
	}

	private static Sample open(final AudioInputStream audioStream) throws LineUnavailableException, IOException {
		final int frameLength = (int) audioStream.getFrameLength();
		if (frameLength > 44100 * 8 * 2) {
			throw new IllegalArgumentException("The audio file is too long (must be shorter than 4 bars at 50BPM)");
		}
		final AudioFormat format = audioStream.getFormat();
		final int frameSize = (int) format.getFrameSize();
		final byte[] bytes = new byte[frameLength * frameSize];
		final int result = audioStream.read(bytes);
		if (result < 0) {
			return null;
		}

		audioStream.close();

		return new Sample(bytes, format, frameLength);
	}

	public void play() throws Exception {
		SamplePlayer.getInstance().play(this);
	}

	public void save(File file) throws Exception {
		final Type fileType = AudioFileFormat.Type.WAVE;
		final AudioInputStream stream = new AudioInputStream(new ByteArrayInputStream(bytes), format, frameLength);
		AudioSystem.write(stream, fileType, file);
	}

	public Sample(byte[] buffer, AudioFormat format, int frameLength) {
		this.bytes = buffer;
		this.format = format;
		this.frameLength = frameLength;
	}

	public int[][] asSamples() {
		final int numChannels = format.getChannels();
		final int[][] toReturn = new int[numChannels][frameLength];
		int sampleIndex = 0;
		for (int t = 0; t < bytes.length;) {
			for (int channel = 0; channel < numChannels; channel++) {
				int low = (int) bytes[t];
				t++;
				int high = (int) bytes[t];
				t++;
				int sample = (high << 8) + (low & 0x00ff);
				toReturn[channel][sampleIndex] = sample;
			}
			sampleIndex++;
		}
		return toReturn;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public AudioFormat getFormat() {
		return format;
	}

	public int getFrameLength() {
		return frameLength;
	}

	/**
	 * 
	 */
	public Sample subRegion(int from, int to) {
		final int frameLength = to - from;
		final int frameSize = (int) format.getFrameSize();
		final byte[] region = new byte[frameLength * frameSize];
		for (int i = 0; i < region.length; i++) {
			region[i] = bytes[from * frameSize + i];
		}
		return new Sample(region, format, region.length);
	}

	public String toString() {
		return "Sample " + format + " (" + frameLength + " frames)";
	}

}
