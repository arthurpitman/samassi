/*
 * Copyright (C) 2012, 2013 Arthur Pitman
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

import android.graphics.Canvas;
import android.os.SystemClock;


/**
 * Base class for map layers.
 */
public abstract class MapLayer {

	protected boolean debugging;


	/**
	 * Gets the debugging flag.
	 * @return
	 */
	public boolean isDebugging() {
		return debugging;
	}


	/**
	 * Sets the debugging flag.
	 * @param debugging
	 */
	public void setDebugging(boolean debugging) {
		this.debugging = debugging;
	}


	/**
	 * Override this method to render the map layer on the canvas.
	 * @param canvas the Canvas to render onto.
	 * @param projection the current MapProjection.
	 * @param frameMillis the milliseconds of the frame, see {@link SystemClock#uptimeMillis()}.
	 * @param invalidateRunnable Runnable for invalidating the MapView.
	 * @return <code>true</code> if rendering completed, <code>false</code> if it should be re-attempted later.
	 */
	public abstract boolean render(Canvas canvas, MapProjection projection, long frameMillis, Runnable invalidateRunnable);


	/**
	 * Override this method to handle click events on the map layer.
	 * @param x map x coordinate.
	 * @param y map y coordinate.
	 * @return <code>true</code> if the click event was handled by the layer, <code>false</code> if it should be passed to lower layers.
	 */
	public abstract boolean onClick(int x, int y);
}