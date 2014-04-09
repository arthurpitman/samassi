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

import android.graphics.Matrix;

import com.arthurpitman.samassi.MapPoint;
import com.arthurpitman.samassi.MapRect;
import com.arthurpitman.samassi.view.MapView;


/**
 * Encapsulates the current projection of a MapView.
 * <p>
 * This class is only intended to be used internally.
 */
public class MapProjection {

	/** Look-up table for converting zoom units to scale. */
	private final static float[] zoomScales;



	/** x coordinate of the pixel focus. */
	private float pixelFocusX;

	/** y coordinate of the pixel focus. */
	private float pixelFocusY;

	/** x coordinate of the map focus. */
	private int mapFocusX;

	/** y coordinate of the map focus. */
	private int mapFocusY;

	/** Rotation of the map in degrees. */
	private float mapRotation;

	/** Zoom of map. */
	private int zoomPoints;



	/** Map associated with this projection. */
	private Map map;

	/** Width of the MapView. */
	private int width = 1;

	/** Height of the MapView. */
	private int height = 1;



	/** Raw map clip x minimum. */
	private long rawMapClipMinX;

	/** Raw map clip x minimum. */
	private long rawMapClipMinY;

	/** Raw map clip x maximum. */
	private long rawMapClipMaxX;

	/** Raw map clip y maximum. */
	private long rawMapClipMaxY;

	/** Map clip x minimum. */
	private int mapClipMinX;

	/** Map clip y minimum. */
	private int mapClipMinY;

	/** Map clip x maximum. */
	private int mapClipMaxX;

	/** Map clip y maximum. */
	private int mapClipMaxY;



	/**	Transformation matrix. */
	private Matrix transformMatrix = new Matrix();

	/** Inverse transformation matrix.	*/
	private Matrix inverseMatrix = new Matrix();

	/**	Zoom level. */
	private int zoom;

	/**	Scale at current zoom level. */
	private float zoomScale;

	/**	Shift map coordinates to pixel coordinates at the current zoom level. */
	private int pixelShift = 0;

	/**	Shift map coordinates to tile coordinates at the current zoom level. */
	private int tileShift = 0;

	/** Size of tiles as a power of 2. */
	private int tileSizePower = 8;

	/** Number of tiles in each dimension at this zoom level. */
	private int tileCount;



	/**	Temporary storage for 1 point. */
	private float[] tempPoint = new float[2];

	/**	Temporary storage for 4 points. */
	private float[] tempPoint4 = new float[8];


	static {
		// calculate lookup table
		zoomScales = new float[MapView.ZOOM_MULTIPLIER];
		for (int i = 0; i < MapView.ZOOM_MULTIPLIER; i++) {
			zoomScales[i] = (float) Math.pow(2, ((float) i) / MapView.ZOOM_MULTIPLIER);
		}
	}


	/*
	 * ========================================
	 * FIELDS FOR MAP VIEW
	 * ========================================
	 */


	/**
	 * Gets the pixel focus x component.
	 * @return
	 */
	public float getPixelFocusX() {
		return pixelFocusX;
	}


	/**
	 * Sets the pixel focus x component.
	 * @param pixelFocusX
	 */
	public void setPixelFocusX(float pixelFocusX) {
		this.pixelFocusX = pixelFocusX;
	}


	/**
	 * Gets the pixel focus y component.
	 * @return
	 */
	public float getPixelFocusY() {
		return pixelFocusY;
	}


	/**
	 * Sets the pixel focus y component.
	 * @param pixelFocusY
	 */
	public void setPixelFocusY(float pixelFocusY) {
		this.pixelFocusY = pixelFocusY;
	}


	/**
	 * Gets the map focus x component.
	 * @return
	 */
	public int getMapFocusX() {
		return mapFocusX;
	}


	/**
	 * Sets the map focus x component.
	 * @param mapFocusX
	 */
	public void setMapFocusX(int mapFocusX) {
		this.mapFocusX = mapFocusX;
	}


	/**
	 * Gets the map focus y component.
	 * @return
	 */
	public int getMapFocusY() {
		return mapFocusY;
	}


	/**
	 * Sets the map focus y component.
	 * @param mapFocusY
	 */
	public void setMapFocusY(int mapFocusY) {
		this.mapFocusY = mapFocusY;
	}


	/**
	 * Gets the map rotation in degrees.
	 * @return
	 */
	public float getMapRotation() {
		return mapRotation;
	}


	/**
	 * Sets the map rotation in degrees.
	 * @param mapRotation
	 */
	public void setMapRotation(float mapRotation) {
		this.mapRotation = mapRotation % 360;
	}


	/**
	 * Gets the zoom points.
	 * @return
	 */
	public int getZoomPoints() {
		return zoomPoints;
	}


	/**
	 * Sets the zoom points.
	 * @param zoomPoints
	 */
	public void setZoomPoints(int zoomPoints) {
		if (map == null) {
			this.zoomPoints = zoomPoints;
		} else {
			this.zoomPoints = Math.min(Math.max(zoomPoints, map.getMinimumZoom() * MapView.ZOOM_MULTIPLIER), map.getMaximumZoom() * MapView.ZOOM_MULTIPLIER);
		}

	}


	/*
	 * ========================================
	 * FIELDS FOR SET UP
	 * ========================================
	 */


	/**
	 * Sets the size of the projected area in pixels.
	 * @param width
	 * @param height
	 */
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}


	/**
	 * Gets the Map associated with this projection.
	 * @return
	 */
	public Map getMap() {
		return map;
	}


	/**
	 * Set the Map associated with this projection.
	 * @param map
	 */
	public void setMap(Map map) {
		this.map = map;
	}


	/*
	 * ========================================
	 * CLIP REGION
	 * ========================================
	 */


	/**
	 * Gets the raw map clip minimum x component.
	 * @return
	 */
	public long getRawMapClipMinX() {
		return rawMapClipMinX;
	}


	/**
	 * Gets the raw map clip minimum y component.
	 * @return
	 */
	public long getRawMapClipMinY() {
		return rawMapClipMinY;
	}


	/**
	 * Gets the raw map clip maximum x component.
	 * @return
	 */
	public long getRawMapClipMaxX() {
		return rawMapClipMaxX;
	}


	/**
	 * Gets the raw map clip maximum y component.
	 * @return
	 */
	public long getRawMapClipMaxY() {
		return rawMapClipMaxY;
	}


	/**
	 * Gets the map clip minimum x component.
	 * @return
	 */
	public int getMapClipMinX() {
		return mapClipMinX;
	}


	/**
	 * Gets the map clip minimum y component.
	 * @return
	 */
	public int getMapClipMinY() {
		return mapClipMinY;
	}


	/**
	 * Gets the map clip maximum x component.
	 * @return
	 */
	public int getMapClipMaxX() {
		return mapClipMaxX;
	}


	/**
	 * Gets the map clip maximum y component.
	 * @return
	 */
	public int getMapClipMaxY() {
		return mapClipMaxY;
	}


	/*
	 * ========================================
	 * COMPUTED FIELDS
	 * ========================================
	 */


	/**
	 * Gets the current transformation matrix.
	 * @return
	 */
	public Matrix getTransformMatrix() {
		return transformMatrix;
	}


	/**
	 * Gets the inverse transformation matrix.
	 * @return
	 */
	public Matrix getInverseMatrix() {
		return inverseMatrix;
	}


	/**
	 * Gets the zoom level.
	 * @return
	 */
	public int getZoom() {
		return zoom;
	}


	/**
	 * Gets the zoom scale.
	 * @return
	 */
	public float getZoomScale() {
		return zoomScale;
	}


	/**
	 * Gets the tile size power.
	 * @return
	 */
	public int getTileSizePower() {
		return tileSizePower;
	}


	/**
	 * Gets the pixel shift in bits.
	 * @return
	 */
	public int getPixelShift() {
		return pixelShift;
	}


	/**
	 * Gets the tiles shift in bits.
	 * @return
	 */
	public int getTileShift() {
		return tileShift;
	}


	/**
	 * Gets the tile count at the current zoom level.
	 * @return
	 */
	public int getTileCount() {
		return tileCount;
	}


	/*
	 * ========================================
	 * MANIPULATION METHODS
	 * ========================================
	 */


	/**
	 * Recalculates the projection.
	 */
	public void project() {
		zoom = zoomPoints / MapView.ZOOM_MULTIPLIER;
		zoomScale = zoomScales[zoomPoints % MapView.ZOOM_MULTIPLIER];
		tileCount = 1 << zoom;
		tileShift = 30 - zoom;
		pixelShift = tileShift - tileSizePower;

		transformMatrix.reset();
		transformMatrix.preTranslate(pixelFocusX, pixelFocusY);
		transformMatrix.preRotate((float)mapRotation);
		transformMatrix.preScale(zoomScale, zoomScale);

		transformMatrix.invert(inverseMatrix);

		// calculate extents
		tempPoint4[0] = 0;
		tempPoint4[1] = 0;
		tempPoint4[2] = width;
		tempPoint4[3] = 0;
		tempPoint4[4] = 0;
		tempPoint4[5] = height;
		tempPoint4[6] = width;
		tempPoint4[7] = height;
		inverseMatrix.mapPoints(tempPoint4);

		rawMapClipMinX = ((long)Math.min(tempPoint4[0], Math.min(tempPoint4[2], Math.min(tempPoint4[4], tempPoint4[6])))
				<< pixelShift) + mapFocusX;
		mapClipMinX = (int)(rawMapClipMaxX & MapPoint.MASK);

		rawMapClipMinY = ((long)Math.min(tempPoint4[1], Math.min(tempPoint4[3], Math.min(tempPoint4[5], tempPoint4[7])))
				<< pixelShift) + mapFocusY;
		mapClipMinY = (int)(rawMapClipMaxY & MapPoint.MASK);

		rawMapClipMaxX = ((long)Math.max(tempPoint4[0], Math.max(tempPoint4[2], Math.max(tempPoint4[4], tempPoint4[6])))
				<< pixelShift) + mapFocusX;
		mapClipMaxX = (int)(rawMapClipMaxX & MapPoint.MASK);

		rawMapClipMaxY = ((long)Math.max(tempPoint4[1], Math.max(tempPoint4[3], Math.max(tempPoint4[5], tempPoint4[7])))
				<< pixelShift) + mapFocusY;
		mapClipMaxY = (int)(rawMapClipMaxY & MapPoint.MASK);
	}


	/**
	 * Focuses on the specified pixel.
	 * @param x
	 * @param y
	 */
	public void focusOnPixel(float x, float y) {
		tempPoint[0] = x;
		tempPoint[1] = y;
		inverseMatrix.mapPoints(tempPoint);

		mapFocusX = mapFocusX + ((int)tempPoint[0] << pixelShift);
		mapFocusY = mapFocusY + ((int)tempPoint[1] << pixelShift);
		pixelFocusX = x;
		pixelFocusY = y;
	}


	/**
	 * Focuses on the specified map point.
	 * @param mapX
	 * @param mapY
	 */
	public void focusOnPoint(int mapX, int mapY) {
		tempPoint[0] = (float)((mapX - mapFocusX) >> pixelShift);
		tempPoint[1] = (float)((mapY - mapFocusY) >> pixelShift);
		transformMatrix.mapPoints(tempPoint);

		pixelFocusX = tempPoint[0];
		pixelFocusY = tempPoint[1];
		mapFocusX = mapX;
		mapFocusY = mapY;
	}


	/**
	 * Converts a map point to pixel coordinates.
	 * <p>
	 * Note: the map wraps around on both x and y.
	 * @param mapX
	 * @param mapY
	 * @param pixelPoint
	 */
	public void toPixelCoordinates(int mapX, int mapY, float[] pixelPoint) {
		pixelPoint[0] = (float)((mapX - mapFocusX) >> pixelShift);
		pixelPoint[1] = (float)((mapY - mapFocusY) >> pixelShift);
		transformMatrix.mapPoints(pixelPoint);
	}


	/**
	 * Converts pixel coordinates to a map point.
	 * <p>
	 * Note: the map wraps around on both x and y.
	 * @param pixelX
	 * @param pixelY
	 * @param mapPoint
	 */
	public void toMapPoint(float pixelX, float pixelY, int[] mapPoint) {
		tempPoint[0] = pixelX;
		tempPoint[1] = pixelY;
		inverseMatrix.mapPoints(tempPoint);

		mapPoint[0] = (mapFocusX + ((int)tempPoint[0] << pixelShift)) & MapPoint.MASK;
		mapPoint[1] = (mapFocusY + ((int)tempPoint[1] << pixelShift)) & MapPoint.MASK;
	}


	/**
	 * Centers the map on the specified map point.
	 * @param mapX
	 * @param mapY
	 */
	public void centerOn(int mapX, int mapY ) {
		pixelFocusX = width / 2.0f;
		pixelFocusY = height / 2.0f;
		mapFocusX = mapX;
		mapFocusY = mapY;
	}


	/**
	 * Tests if the projection overlaps with the map bounds.
	 * @return
	 */
	public boolean overlapsWithBounds() {
		MapRect b = map.getBounds();
		return MapRect.contains(b.getMinX(), b.getMinY(), b.getMaxX(), b.getMaxY(), mapClipMinX, mapClipMinY)
				|| MapRect.contains(b.getMinX(), b.getMinY(), b.getMaxX(), b.getMaxY(), mapClipMaxX,mapClipMinY)
				|| MapRect.contains(b.getMinX(), b.getMinY(), b.getMaxX(), b.getMaxY(), mapClipMinX,mapClipMaxY)
				|| MapRect.contains(b.getMinX(), b.getMinY(), b.getMaxX(), b.getMaxY(), mapClipMaxX,mapClipMaxY);
	}
}