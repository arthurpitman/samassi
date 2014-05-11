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

package com.arthurpitman.samassi.tiles.mbtiles;

import com.arthurpitman.samassi.GeoPoint;
import com.arthurpitman.samassi.tiles.TileSourceMetadata;


/**
 * Metadata for a {@link MbTileSource}.
 */
public class MbTileSourceMetadata extends TileSourceMetadata {

	private final String name;
	private final String description;
	private final String version;
	private final String attibution;
	private final String template;


	/**
	 * Creates a new {@link MbTileSourceMetadata} with the specified attributes.
	 * @param mapBounds
	 * @param mapCenter
	 * @param minimumZoom
	 * @param maximumZoom
	 * @param tileWidth
	 * @param name
	 * @param description
	 * @param version
	 * @param attibution
	 * @param template
	 */
	public MbTileSourceMetadata(GeoPoint[] mapBounds, GeoPoint mapCenter,
			int minimumZoom, int maximumZoom, int tileWidth, String name,
			String description, String version, String attibution,
			String template) {
		super(mapBounds, mapCenter, minimumZoom, maximumZoom, tileWidth);

		this.name = name;
		this.description = description;
		this.version = version;
		this.attibution = attibution;
		this.template = template;
	}


	/**
	 * Gets the name.
	 * @return
	 */
	public String getName() {
		return name;
	}


	/**
	 * Gets the description.
	 * @return
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * Gets the version.
	 * @return
	 */
	public String getVersion() {
		return version;
	}


	/**
	 * Gets the attribution.
	 * @return
	 */
	public String getAttibution() {
		return attibution;
	}


	/**
	 * Gets the template.
	 * @return
	 */
	public String getTemplate() {
		return template;
	}
}