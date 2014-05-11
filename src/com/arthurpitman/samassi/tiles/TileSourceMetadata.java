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

package com.arthurpitman.samassi.tiles;

import com.arthurpitman.samassi.GeoPoint;


/**
 * Represents metadata associated with a TileSource.
 * <p>
 * Fields are based on those provided by mbtiles files.
 */
public class TileSourceMetadata {
	private final GeoPoint[] mapBounds;
	private final GeoPoint mapCenter;
	private final int minimumZoom;
	private final int maximumZoom;
	private final int tileWidth;


	/**
	 * Creates a new TileSourceMetadata with the specified attributes.
	 * @param mapBounds
	 * @param mapCenter
	 * @param minimumZoom
	 * @param maximumZoom
	 * @param tileWidth
	 */
	public TileSourceMetadata(GeoPoint[] mapBounds, GeoPoint mapCenter, int minimumZoom, int maximumZoom, int tileWidth) {
		this.mapBounds = mapBounds;
		this.mapCenter = mapCenter;
		this.minimumZoom = minimumZoom;
		this.maximumZoom = maximumZoom;
		this.tileWidth = tileWidth;
	}


	/**
	 * Gets the tile width in pixels.
	 * @return
	 */
	public int getTileWidth() {
		return tileWidth;
	}


	/**
	 * Gets the map bounds.
	 * @return
	 */
	public GeoPoint[] getMapBounds() {
		return mapBounds;
	}


	/**
	 * Gets the center of the map.
	 * @return
	 */
	public GeoPoint getMapCenter() {
		return mapCenter;
	}


	/**
	 * Gets the minimum zoom level supported by the TileSource.
	 * @return
	 */
	public int getMinimumZoom() {
		return minimumZoom;
	}


	/**
	 * Gets the maximum zoom level supported by the TileSource.
	 * @return
	 */
	public int getMaximumZoom() {
		return maximumZoom;
	}
}