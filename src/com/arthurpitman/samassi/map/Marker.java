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

import com.arthurpitman.samassi.MapPoint;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;


/**
 * Represents a map marker.
 */
public class Marker {

	/** The {@code Bitmap} associated with the {@code Marker}. */
	private Bitmap bitmap;
	
	/** The {@code Marker}'s location. */	
	private MapPoint location;
	
	float[] pixelPoint = new float[2];
	Paint paint;
	private float xOffset;
	private float yOffset;
	
	
	/**
	 * Creates a new {@code Marker}.
	 */
	public Marker() {
		paint = new Paint();
		paint.setAntiAlias(true);		
	}
	
	
	/**
	 * Gets the {@code Bitmap} of the {@code Marker}.
	 * @return
	 */
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	
	/**
	 * Sets the {@code Bitmap} of the {@code Marker}.
	 * @param bitmap
	 */
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
		xOffset = bitmap.getWidth() / 2;
		yOffset = bitmap.getHeight();
	}
	
	
	/**
	 * Gets the location of the {@code Marker}.
	 * @return
	 */
	public MapPoint getLocation() {
		return location;
	}
	
	
	/**
	 * Sets the location of the {@code Marker}.
	 * @param location
	 */
	public void setLocation(MapPoint location) {
		this.location = location;
	}
	
	
	/**
	 * Renders the marker on the map canvas.
	 * @param canvas
	 * @param projection
	 */
	public void render(Canvas canvas, MapProjection projection) {
		if ((location != null) && (bitmap != null)) {
			projection.toPixelCoordinates(location.getX(), location.getY(), pixelPoint);
			canvas.drawBitmap(bitmap, pixelPoint[0] - xOffset, pixelPoint[1] - yOffset, paint);
		}
	}
}