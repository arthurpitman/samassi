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

import java.util.ArrayList;
import java.util.List;

import com.arthurpitman.samassi.MapPoint;
import com.arthurpitman.samassi.MapRect;


/**
 * Represents a map.
 * <p>
 * The class offers various methods for changing the map's properties, such as its layers, bounds and zoom constraints.
 */
public class Map {

	private MapRect bounds = new MapRect(0, 0, MapPoint.BASE - 1, MapPoint.BASE - 1);
	private List<MapLayer> layers = new ArrayList<MapLayer>();
	private int maximumZoom = 21;
	private int minimumZoom = 0;
	private int tileSize = 256;


	/**
	 * Creates a new Map.
	 */
	public Map() {
	}


	/**
	 * Gets the layers.
	 * @return
	 */
	public List<MapLayer> getLayers() {
		return layers;
	}


	/**
	 * Gets the bounds.
	 * @return
	 */
	public MapRect getBounds() {
		return bounds;
	}


	/**
	 * Sets the bounds.
	 * @param bounds
	 */
	public void setBounds(MapRect bounds) {
		this.bounds = bounds;
	}


	/**
	 * Gets the minimum zoom level.
	 * @return
	 */
	public int getMinimumZoom() {
		return minimumZoom;
	}


	/**
	 * Sets the minimum zoom level.
	 * @param minimumZoom
	 */
	public void setMinimumZoom(int minimumZoom) {
		this.minimumZoom = minimumZoom;
	}


	/**
	 * Gets the maximum zoom level.
	 * @return
	 */
	public int getMaximumZoom() {
		return maximumZoom;
	}


	/**
	 * Sets the maximum zoom level.
	 * @param maximumZoom
	 */
	public void setMaximumZoom(int maximumZoom) {
		this.maximumZoom = maximumZoom;
	}


	/**
	 * Gets the tile size in pixels.
	 * @return
	 */
	public int getTileSize() {
		return tileSize;
	}


	/**
	 * Sets the tile size in pixels.
	 * @param tileSize
	 */
	public void setTileSize(int tileSize) {
		this.tileSize = tileSize;
	}
}