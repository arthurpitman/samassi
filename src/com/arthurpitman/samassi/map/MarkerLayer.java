/*
 * Copyright (C) 2012 - 2014 Arthur Pitman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arthurpitman.samassi.map;

import java.util.ArrayList;

import android.graphics.Canvas;


/**
 * A {@link MapLayer} for displaying markers.
 */
public class MarkerLayer extends MapLayer {

	/** The {@code Marker}'s. */
	private ArrayList<Marker> markers = new ArrayList<Marker>();


	/**
	 * Creates a new {@code MarkerLayer}.
	 */
	public MarkerLayer() {
		super();
	}


	/**
	 * Gets the markers.
	 * @return
	 */
	public ArrayList<Marker> getMarkers() {
		return markers;
	}


	@Override
	public boolean render(Canvas canvas, MapProjection projection,
			long frameMillis, Runnable invalidateRunnable) {
		for (Marker marker : markers) {
			marker.render(canvas, projection);
		}
		return true;
	}


	@Override
	public boolean onClick(int x, int y) {
		// TODO add click support
		return false;
	}
}