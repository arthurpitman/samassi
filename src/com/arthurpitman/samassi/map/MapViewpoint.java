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


/**
 * Encapsulates a map viewpoint.
 */
public class MapViewpoint {

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


	/**
	 * Creates a new {@code MapViewpoint}.
	 * @param pixelFocusX
	 * @param pixelFocusY
	 * @param mapFocusX
	 * @param mapFocusY
	 * @param mapRotation
	 * @param zoomPoints
	 */
	public MapViewpoint(float pixelFocusX, float pixelFocusY, int mapFocusX, int mapFocusY, float mapRotation, int zoomPoints) {
		this.pixelFocusX = pixelFocusX;
		this.pixelFocusY = pixelFocusY;
		this.mapFocusX = mapFocusX;
		this.mapFocusY = mapFocusY;
		this.mapRotation = mapRotation;
		this.zoomPoints = zoomPoints;
	}


	/**
	 * Gets the pixel focus X.
	 * @return
	 */
	public float getPixelFocusX() {
		return pixelFocusX;
	}


	/**
	 * Sets the pixel focus X.
	 * @param pixelFocusX
	 */
	public void setPixelFocusX(float pixelFocusX) {
		this.pixelFocusX = pixelFocusX;
	}


	/**
	 * Gets the pixel focus Y.
	 * @return
	 */
	public float getPixelFocusY() {
		return pixelFocusY;
	}


	/**
	 * Sets the pixel focus Y.
	 * @param pixelFocusY
	 */
	public void setPixelFocusY(float pixelFocusY) {
		this.pixelFocusY = pixelFocusY;
	}


	/**
	 * Gets the map focus X.
	 * @return
	 */
	public int getMapFocusX() {
		return mapFocusX;
	}


	/**
	 * Sets the map focus X.
	 * @param mapFocusX
	 */
	public void setMapFocusX(int mapFocusX) {
		this.mapFocusX = mapFocusX;
	}


	/**
	 * Gets the map focus Y.
	 * @return
	 */
	public int getMapFocusY() {
		return mapFocusY;
	}


	/**
	 * Sets the map focus Y.
	 * @param mapFocusY
	 */
	public void setMapFocusY(int mapFocusY) {
		this.mapFocusY = mapFocusY;
	}


	/**
	 * Gets the map rotation.
	 * @return
	 */
	public float getMapRotation() {
		return mapRotation;
	}


	/**
	 * Sets the map rotation.
	 * @param mapRotation
	 */
	public void setMapRotation(float mapRotation) {
		this.mapRotation = mapRotation;
	}


	/**
	 * Gets the map zoom points.
	 * @return
	 */
	public int getMapZoomPoints() {
		return zoomPoints;
	}


	/**
	 * Sets the map zoom points.
	 * @param zoomPoints
	 */
	public void setMapZoomPoints(int zoomPoints) {
		this.zoomPoints = zoomPoints;
	}
}