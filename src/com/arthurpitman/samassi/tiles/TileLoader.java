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

package com.arthurpitman.samassi.tiles;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;


/**
 * Loads and caches bitmap tiles using a background thread.
 */
public class TileLoader {


	/**
	 * Least-recently-used bitmap cache.
	 */
	private static class TileCache extends android.util.LruCache<Long, Bitmap> {

		private static final int X_SHIFT = 32;
		private static final int Y_SHIFT = 8;
		private static final int X_MASK = 0xFFFFFF;
		private static final int Y_MASK = 0xFFFFFF;
		private static final int Z_MASK = 0xFF;


		/**
		 * Creates a new {@link TileCache} of the specified size.
		 * @param maxSizeBytes
		 */
		public TileCache(int maxSizeBytes) {
			super(maxSizeBytes);
		}


		/**
		 * Gets a tile.
		 * @param x
		 * @param y
		 * @param z
		 * @return
		 */
		public Bitmap get(int x, int y, int z) {
			return get(toKey(x, y, z));
		}


		/**
		 * Puts a tile.
		 * @param x
		 * @param y
		 * @param z
		 * @param tile
		 */
		public void put(int x, int y, int z, Bitmap tile) {
			put(toKey(x, y, z), tile);
		}


		@Override
		protected int sizeOf(Long key, Bitmap value) {
			return value.getByteCount();
		}


		/**
		 * Converts the specified x, y and z tile coordinates to a key.
		 * @param x
		 * @param y
		 * @param z
		 * @return the key.
		 */
		private static final long toKey(int x, int y, int z) {
			return (((long)x & X_MASK) << X_SHIFT) | (((long)y & Y_MASK) << Y_SHIFT) | ((long)z & Z_MASK);
		}
	}


	/**
	 * Internal Handler for loading tiles on a worker thread.
	 */
	private static class LoaderHandler extends Handler {
		private WeakReference<TileLoader> outer;


		/**
		 * Creates a new WorkerHandler.
		 * @param mbTileSource the outer class.
		 * @param looper the looper to create the handler on.
		 */
		public LoaderHandler(TileLoader loader, Looper looper) {
			super(looper);
			outer = new WeakReference<TileLoader>(loader);
		}


		@Override
		public void handleMessage(Message message) {
			TileLoader loader = outer.get();
			if (loader == null) {
				return;
			}

			TileSource tileSource = loader.tileSource;
			if (tileSource == null) {
				return;
			}

			int x = message.what;
			int y = message.arg1;
			int z = message.arg2;
			Runnable callback = (Runnable) message.obj;

			if (z >= 0) {
				Bitmap b = tileSource.getTile(x, y, z);
				if (b == null) {
					loader.cache.put(x, y, z, loader.emptyTile);
				} else {
					loader.cache.put(x, y, z, b);
				}
			}

			if (callback != null) {
				loader.callbackHandler.post(callback);
			}
		}
	}


	private TileCache cache;
	private LoaderHandler workerHandler;
	private Handler callbackHandler;
	private volatile TileSource tileSource;
	private Bitmap emptyTile;


	/**
	 * Creates a new {@link TileLoader}.
	 * @param cacheSize
	 * @param handlerThread
	 */
	public TileLoader(int cacheSize, HandlerThread handlerThread) {
		cache = new TileCache(cacheSize);
		workerHandler = new LoaderHandler(this, handlerThread.getLooper());
		callbackHandler = new Handler();
		emptyTile = Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8);
	}


	/**
	 * Gets the {@code TileSource} associated with this {@code TileLoader}.
	 * @return
	 */
	public TileSource getTileSource() {
		return tileSource;
	}


	/**
	 * Sets the {@code TileSource} associated with this {@code TileLoader}.
	 * @param tileSource
	 */
	public void setTileSource(TileSource tileSource) {
		cache.evictAll();
		this.tileSource = tileSource;
	}


	/**
	 * Gets a tile from the cache, if available.
	 * @param x tile x coordinate.
	 * @param y tile y coordinate.
	 * @param z tile z coordinate.
	 * @return the tile bitmap or {@code null} if not available.
	 */
	public Bitmap getTile(int x, int y, int z) {
		return cache.get(x, y, z);
	}


	/**
	 * Requests that a tile be loaded into the cache on the background thread.
	 * @param x tile x coordinate.
	 * @param y tile y coordinate.
	 * @param z tile z coordinate.
	 * @param callback called one the tile has been loaded.
	 */
	public void requestTile(int x, int y, int z, Runnable callback) {
		workerHandler.sendMessage(workerHandler.obtainMessage(x, y, z, callback));
	}


	/**
	 * Gets an empty tile.
	 * @return
	 */
	public Bitmap getEmptyTile() {
		return emptyTile;
	}


	/**
	 * Clears all tile bitmaps from the cache.
	 */
	public void clearCache() {
		cache.evictAll();
	}
}