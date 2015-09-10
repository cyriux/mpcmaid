package com.mpcmaid.pgm;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a matrix of sample (pads, layers).
 * 
 * @see SampleCommand Commands operate on this matrix
 * 
 * @author cyrille martraire
 */
public class SampleMatrix {

	private final Sample[][] cells = new Sample[64][4];

	public void set(final Layer layer, final Sample sample) {
		cells[layer.getPad().getElementIndex()][layer.getElementIndex()] = sample;
	}

	public Sample get(final Layer layer) {
		return cells[layer.getPad().getElementIndex()][layer.getElementIndex()];
	}

	public int size() {
		int size = 0;
		for (int i = 0; i < cells.length; i++) {
			final Sample[] layers = cells[i];
			for (int j = 0; j < layers.length; j++) {
				if (layers[j] != null) {
					size++;
				}
			}
		}
		return size;
	}

	public void clear() {
		for (int i = 0; i < cells.length; i++) {
			final Sample[] layers = cells[i];
			for (int j = 0; j < layers.length; j++) {
				layers[j] = null;
			}
		}
	}

	public List collectAll() {
		final List list = new ArrayList();
		for (int i = 0; i < cells.length; i++) {
			final Sample[] layers = cells[i];
			for (int j = 0; j < layers.length; j++) {
				final Sample sample = layers[j];
				if (sample != null) {
					list.add(sample);
				}
			}
		}
		return list;
	}

	public String toString() {
		return "SampleMatrix: " + size() + " samples";
	}

}
