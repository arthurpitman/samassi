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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Canvas.EdgeType;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.arthurpitman.samassi.tiles.TileLoader;
import com.arthurpitman.samassi.view.MapView;


/**
 * Map layer composed of tiles.
 */
public class TileLayer extends MapLayer {
	/** Paint used for drawing tile bitmaps. */
	private Paint tilePaint;

	/** Debug paint used to make missing tiles more visible. */
	private Paint missingTilePaint;

	/** Debug paint used to draw tile grid. */
	private Paint debugTileGridPaint;

	/** Debug paint used to draw text inside of tiles. */
	private Paint debugTextPaint;

	/** The {@link TileLoader} used by this layer. */
	private TileLoader tileLoader;

	/** Reusable temporary source rectangle. */
	Rect tileSourceRect;

	/** Reusable temporary destination rectangle. */
	RectF tileDestRect;


	/**
	 * Creates a new TileLayer.
	 */
	public TileLayer() {
		tileSourceRect = new Rect();
		tileDestRect = new RectF();

		tilePaint = new Paint();
		tilePaint.setAntiAlias(true);
		tilePaint.setFilterBitmap(true);

		debugTileGridPaint = new Paint();
		debugTileGridPaint.setARGB(255, 0, 0, 255);
		debugTileGridPaint.setStyle(Paint.Style.STROKE);

		debugTextPaint = new Paint();
		debugTextPaint.setARGB(255, 0, 0, 255);
		debugTextPaint.setStyle(Paint.Style.FILL);

		missingTilePaint = new Paint();
		missingTilePaint.setARGB(255, 255, 128, 128);
		missingTilePaint.setStyle(Paint.Style.FILL);
	}


	/**
	 * Gets the TileLoader associated with this TileLayer.
	 * @return the TileLoader.
	 */
	public TileLoader getTileLoader() {
		return tileLoader;
	}


	/**
	 * Sets the TileLoader associated with this TileLoader.
	 * @param tileLoader
	 */
	public void setTileLoader(TileLoader tileLoader) {
		this.tileLoader = tileLoader;
	}


	@Override
	public boolean render(Canvas canvas, MapProjection projection, long frameMillis, Runnable invalidateRunnable) {
		// allow drawing in transformed space
		canvas.save();
		canvas.concat(projection.getTransformMatrix());

		// cache variables
		int zoom = projection.getZoom();
		int zoomOffsetPoints = projection.getZoomPoints() % MapView.ZOOM_MULTIPLIER;
		boolean downscale = zoomOffsetPoints != 0;
		int tileCount = projection.getTileCount();
		int tileSize = 1 << projection.getTileSizePower();
		float halfTileSize = tileSize / 2.0f;
		tileSourceRect.set(0, 0, tileSize, tileSize);
		int tileShift = projection.getTileShift();
		int pixelShift = projection.getPixelShift();

		// calculate tile extents
		int startTileX = (int)Math.floor(projection.getRawMapClipMinX() >> tileShift);
		int startTileY = (int)Math.floor(projection.getRawMapClipMinY() >> tileShift);
		int endTileX = (int)Math.ceil(projection.getRawMapClipMaxX() >> tileShift);
		int endTileY = (int)Math.ceil(projection.getRawMapClipMaxY() >> tileShift);

		while (endTileX < startTileX)
			endTileX += tileCount;

		while (endTileY < startTileY)
			endTileY += tileCount;

		// calculate where to start drawing
		float xStart = (float)(startTileX * tileSize - (projection.getMapFocusX() >> pixelShift));
		float yStart = (float)(startTileY * tileSize - (projection.getMapFocusY() >> pixelShift));

		float y = yStart;
		boolean requested = false;
		for (int gridY = startTileY; gridY <= endTileY; gridY++) {
			float x = xStart;
			for (int gridX = startTileX; gridX <= endTileX; gridX++) {

				// only try to draw tiles that are actually visible
				if (!canvas.quickReject(x, y, x + tileSize, y + tileSize, EdgeType.BW)) {

					if (debugging) {
						canvas.drawRect(x, y, x + tileSize, y + tileSize, missingTilePaint);
					}

					// wrap around tiles on X
					int tx = gridX % tileCount;
					if (tx < 0)
						tx += tileCount;

					// wrap around tiles on Y
					int ty = gridY % tileCount;
					if (ty < 0)
						ty += tileCount;

					// get the tile and draw it or request it if unavailable
					boolean missing = false;
					tilePaint.setAlpha(255);
					Bitmap tileBitmap = tileLoader.getTile(tx, ty, zoom);
					if (tileBitmap != null) {
						canvas.drawBitmap(tileBitmap, x, y, tilePaint);
					} else {
						missing = true;
						tileLoader.requestTile(tx, ty, zoom, null);
						requested = true;
					}

					if (downscale || missing) {
						if (downscale && !missing) {
							tilePaint.setAlpha(zoomOffsetPoints * 256 / MapView.ZOOM_MULTIPLIER);
						}

						// T0
						tileBitmap = tileLoader.getTile(tx * 2, ty * 2, zoom + 1);
						if (tileBitmap != null) {
							tileDestRect.set(x, y, x + halfTileSize, y + halfTileSize);
							canvas.drawBitmap(tileBitmap, tileSourceRect, tileDestRect,  tilePaint);
						} else if (downscale) {
							tileLoader.requestTile(tx * 2, ty * 2, zoom + 1, null);
							requested = true;
						}

						// T1
						tileBitmap = tileLoader.getTile(tx * 2 + 1, ty * 2, zoom + 1);
						if (tileBitmap != null) {
							tileDestRect.set(x + halfTileSize, y, x + tileSize, y + halfTileSize);
							canvas.drawBitmap(tileBitmap, tileSourceRect, tileDestRect,  tilePaint);
						} else if (downscale) {
							tileLoader.requestTile(tx * 2 + 1, ty * 2, zoom + 1, null);
							requested = true;
						}

						// T2
						tileBitmap = tileLoader.getTile(tx * 2, ty * 2 + 1, zoom + 1);
						if (tileBitmap != null) {
							tileDestRect.set(x, y + halfTileSize, x + halfTileSize, y + tileSize);
							canvas.drawBitmap(tileBitmap, tileSourceRect, tileDestRect,  tilePaint);
						} else if (downscale) {
							tileLoader.requestTile(tx * 2, ty * 2 + 1, zoom + 1, null);
							requested = true;
						}

						// T3
						tileBitmap = tileLoader.getTile(tx * 2 + 1, ty * 2 + 1, zoom + 1);
						if (tileBitmap != null) {
							tileDestRect.set(x + halfTileSize, y + halfTileSize, x + tileSize, y + tileSize);
							canvas.drawBitmap(tileBitmap, tileSourceRect, tileDestRect,  tilePaint);
						} else if (downscale) {
							tileLoader.requestTile(tx * 2 + 1, ty * 2 + 1, zoom + 1, null);
							requested = true;
						}
					}

					// draw debug grid
					if (debugging) {
						canvas.drawRect(x, y, x + tileSize, y + tileSize, debugTileGridPaint);
						canvas.drawText("(" + gridX + "," + gridY + "," + zoom + ")", x + tileSize / 2, y + tileSize / 2, debugTextPaint);
					}
				}
				x += tileSize;
			}
			y += tileSize;
		}

		if (requested) {
			tileLoader.requestTile(0, 0, -1, invalidateRunnable);
		}

		canvas.restore();
		return true;
	}


	@Override
	public boolean onClick(int x, int y) {
		// TileLayers can't be clicked.
		return false;
	}
}