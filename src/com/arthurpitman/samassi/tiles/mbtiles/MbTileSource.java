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

import java.io.File;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.arthurpitman.samassi.GeoPoint;
import com.arthurpitman.samassi.tiles.TileSource;


/**
 * TileSource implementation for MbTiles (MapBox maps).
 */
public class MbTileSource implements TileSource {

	private static final String METADATA_BOUNDS = "bounds";
	private static final String METADATA_CENTER = "center";
	private static final String METADATA_MIN_ZOOM = "minzoom";
	private static final String METADATA_MAX_ZOOM = "maxzoom";
	private static final String METADATA_NAME = "name";
	private static final String METADATA_DESCRIPTION = "description";
	private static final String METADATA_VERSION = "version";
	private static final String METADATA_ATTRIBUTION = "attribution";
	private static final String METADATA_TEMPLATE = "template";

	private SQLiteDatabase database;


	/**
	 * Creates a new MbTileSource.
	 * @param databaseFile
	 */
	public MbTileSource(File databaseFile) {
		database = SQLiteDatabase.openDatabase(databaseFile.getPath(), null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
	}


	@Override
	public Bitmap getTile(int x, int y, int z) {
		Bitmap result = null;
		int maxTiles = 1 << z;

		// convert coordinates to strings, note that y coordinate is flipped
		String[] tileSpec = new String[3];
		tileSpec[0] = Integer.toString(x);
		tileSpec[1] = Integer.toString(maxTiles - y - 1);
		tileSpec[2] = Integer.toString(z);

		Cursor c = database.rawQuery("select tile_data from tiles where tile_column=? and tile_row=? and zoom_level=?", tileSpec);

		if (c.moveToFirst()) {
			byte[] blobBytes = c.getBlob(c.getColumnIndex("tile_data"));
			result = BitmapFactory.decodeByteArray(blobBytes, 0, blobBytes.length);
		}
		c.close();
		return result;
	}


	/**
	 * Gets the {@link MbTileSourceMetadata} stored in this MbTileSource.
	 * @return
	 */
	public MbTileSourceMetadata getMetadata() {
		// temporary storage
		String name = null;
		String description = null;
		String version = null;
		String attibution = null;
		String template = null;

		GeoPoint[] mapBounds = null;
		GeoPoint mapCenter = null;
		int minimumZoom = 0;
		int maximumZoom = 0;

		// query metadata, stored as a series of name value pairs
		Cursor c = database.rawQuery("select name, value from metadata", null);
		int keyIndex = c.getColumnIndex("name");
		int valueIndex = c.getColumnIndex("value");
		if (c.moveToFirst()) {
			do {
				String key = c.getString(keyIndex);
				if (key.equals(METADATA_MIN_ZOOM)) {
					minimumZoom = c.getInt(valueIndex);
				} else if (key.equals(METADATA_MAX_ZOOM)) {
					maximumZoom = c.getInt(valueIndex);
				} else if (key.equals(METADATA_CENTER)) {
					String[] centerElements = c.getString(valueIndex).split(",");
					if (centerElements.length == 3) {
						mapCenter = new GeoPoint(Double.parseDouble(centerElements[0]), Double.parseDouble(centerElements[1]), 0);
					}
				} else if (key.equals(METADATA_BOUNDS)) {
					String[] boundElements = c.getString(valueIndex).split(",");
					if (boundElements.length == 4) {
						mapBounds = new GeoPoint[2];
						mapBounds[0] = new GeoPoint(Double.parseDouble(boundElements[0]), Double.parseDouble(boundElements[1]), 0);
						mapBounds[1] = new GeoPoint(Double.parseDouble(boundElements[2]), Double.parseDouble(boundElements[3]), 0);
					}
				} else if (key.equals(METADATA_NAME)) {
					name = c.getString(valueIndex);
				} else if (key.equals(METADATA_DESCRIPTION)) {
					description = c.getString(valueIndex);
				} else if (key.equals(METADATA_VERSION)) {
					version = c.getString(valueIndex);
				} else if (key.equals(METADATA_ATTRIBUTION)) {
					attibution = c.getString(valueIndex);
				} else if (key.equals(METADATA_TEMPLATE)) {
					template = c.getString(valueIndex);
				}
			} while (c.moveToNext());
		}
		c.close();

		return new MbTileSourceMetadata(mapBounds, mapCenter, minimumZoom, maximumZoom, 256,
				name, description, version, attibution, template);
	}


	/**
	 * Closes the {@code MbTileSource}.
	 */
	public void close() {
		if (database != null) {
			database.close();
			database = null;
		}
	}
}