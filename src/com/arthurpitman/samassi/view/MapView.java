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

package com.arthurpitman.samassi.view;

import java.util.List;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.arthurpitman.common.TouchFilter;
import com.arthurpitman.common.TouchListener;
import com.arthurpitman.samassi.MapPoint;
import com.arthurpitman.samassi.map.Map;
import com.arthurpitman.samassi.map.MapLayer;
import com.arthurpitman.samassi.map.MapProjection;
import com.arthurpitman.samassi.map.MapViewpoint;


/**
 * Map UI component.
 */
public class MapView extends View implements TouchListener  {

	/*
	 * ========================================
	 * FIELDS
	 * ========================================
	 */


	/** Number of zoom points per zoom level. */
	public final static int ZOOM_MULTIPLIER = 64;

	/** Minimum delay between map redraw. */
	private final static int REDRAW_DELTA = 100;

	/** Natural log of 2. */
	private final static double LOG_OF_TWO = 0.69314718055994530941723212145818;


	/** Map background paint. */
	private Paint backgroundPaint;

	/** Touch event filter helper class. */
	private TouchFilter touchFilter;


	/** Shared projection. */
	private MapProjection projection = new MapProjection();

	/** Map associated with this MapView. */
	private Map map;


	/** Flag for enabling click gestures. */
	private boolean mapClickable = true;

	/** Flag for enabling drag gestures. */
	private boolean mapDragable = true;

	/** Flag for enabling zoom gestures. */
	private boolean mapZoomable = true;

	/** Flag for enabling rotate gestures. */
	private boolean mapRotatable = true;


	/** Flag indicating if a gesture is active. */
	private boolean gestureActive = false;

	/** Flag indicating if a drag gesture is active. */
	private boolean dragGestureActive = false;

	/** Flag indicating if a rotation gesture is active. */
	private boolean rotateGestureActive = false;

	/** Flag indicating if a zoom gesture is active. */
	private boolean zoomGestureActive = false;


	/** Start touch x map coordinate. */
	int startTouchMapX;

	/** Start touch y map coordinate. */
	int startTouchMapY;

	/** Start touch x pixel coordinate. */
	private float startTouchPixelX;

	/** Start touch y pixel coordinate. */
	private float startTouchPixelY;

	/** Start touch zoom points. */
	private int startTouchZoomPoints;

	/** Start touch distance */
	private double startTouchDistance;

	/** Start touch map rotation. */
	private float startTouchMapRotation;

	/** Start touch angle. */
	private float startTouchAngle;

	/** Temporary storage for last touch map point. */
	int[] lastTouchMapPoint = new int[2];

	/** Temporary storage for new touch map point. */
	int[] newTouchMapPoint = new int[2];


	/** Animator for snap to rotation. */
	private ObjectAnimator snapRotationAnimator;

	/** Animator for snap to zoom. */
	private ObjectAnimator snapZoomAnimator;


	/** Reusable Runnable for repainting the view */
	private Runnable invalidateRunnable = new Runnable() {
		public void run() {
			invalidate();
		}
	};


	/*
	 * ========================================
	 * CONSTRUCTION / INITIALIZATION
	 * ========================================
	 */


	/**
	 * Creates a new MapView.
	 * @param context
	 */
	public MapView(Context context) {
		super(context);
		initializeView();
	}


	/**
	 * Creates a new MapView.
	 * @param context
	 * @param attrs
	 */
	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeView();
	}


	/**
	 * Performs shared initialization for the MapView.
	 */
	private final void initializeView() {
		touchFilter = new TouchFilter(this);

		backgroundPaint = new Paint();
		backgroundPaint.setARGB(255, 247, 248, 246);

		TimeInterpolator interpolator = new DecelerateInterpolator();
		snapRotationAnimator = ObjectAnimator.ofFloat(this, "mapRotation", 0);
		snapRotationAnimator.setDuration(150);
		snapRotationAnimator.setInterpolator(interpolator);
		snapRotationAnimator.addListener(snapRotationAnimationListener);

		snapZoomAnimator = ObjectAnimator.ofInt(this, "mapZoomPoints", 0);
		snapZoomAnimator.setDuration(150);
		snapRotationAnimator.setInterpolator(interpolator);
		snapRotationAnimator.addListener(snapZoomAnimationListener);
	}


	/*
	 * ========================================
	 * FRONT END METHODS
	 * ========================================
	 */


	/**
	 * Gets the map associated with this MapView.
	 * @return
	 */
	public Map getMap() {
		return map;
	}


	/**
	 * Sets the map associated with this MapView.
	 * @param map
	 */
	public void setMap(Map map) {
		this.map = map;
		projection.setMap(map);
	}


	/**
	 * Gets the map focus.
	 * @return
	 */
	public MapPoint getMapFocus() {
		return new MapPoint(projection.getMapFocusX(), projection.getMapFocusY());
	}


	/**
	 * Sets the map focus.
	 * @param mapFocus
	 */
	public void setMapFocus(MapPoint mapFocus) {
		projection.setMapFocusX(mapFocus.getX() & MapPoint.MASK);
		projection.setMapFocusY(mapFocus.getY() & MapPoint.MASK);
		projection.project();
		invalidate();
	}


	/**
	 * Sets the map focus x component.
	 * @param mapFocusX
	 */
	public void setMapFocusX(int mapFocusX) {
		projection.setMapFocusX(mapFocusX & MapPoint.MASK);
		projection.project();
		invalidate();
	}


	/**
	 * Sets the map focus y component.
	 * @param mapFocusY
	 */
	public void setMapFocusY(int mapFocusY) {
		projection.setMapFocusY(mapFocusY & MapPoint.MASK);
		projection.project();
		invalidate();
	}


	/**
	 * Gets the map zoom points.
	 * @return
	 */
	public int getMapZoomPoints() {
		return projection.getZoomPoints();
	}


	/**
	 * Sets the map zoom points.
	 * @param zoomPoints
	 */
	public void setMapZoomPoints(int zoomPoints) {
		projection.setZoomPoints(zoomPoints);
		projection.project();
		invalidate();
	}


	/**
	 * Gets the map rotation in degrees.
	 * @return
	 */
	public float getMapRotation() {
		return projection.getMapRotation();
	}


	/**
	 * Sets the map rotation in degrees.
	 * @param mapRotation
	 */
	public void setMapRotation(float mapRotation) {
		projection.setMapRotation(mapRotation);
		projection.project();
		invalidate();
	}


	/**
	 * Moves the map focus, updating the pixel focus.
	 * @param mapFocus
	 */
	public void focusOnMap(MapPoint mapFocus) {
		projection.focusOnPoint(mapFocus.getX(), mapFocus.getY());
		projection.project();
	}


	/**
	 * Moves the pixel focus, updating the map focus.
	 * @param x
	 * @param y
	 */
	public void focusOnPixel(float x, float y) {
		projection.focusOnPixel(x, y);
		projection.project();
	}


	/**
	 * Moves the pixel focus to the center of the MapView.
	 */
	public void focusOnCenter() {
		projection.focusOnPixel(getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f);
		projection.project();
	}


	/**
	 * Gets the mapClickable flag.
	 * @return
	 */
	public boolean isMapClickable() {
		return mapClickable;
	}


	/**
	 * Sets the mapClickable flag.
	 * @param mapClickable
	 */
	public void setMapClickable(boolean clickable) {
		this.mapClickable = clickable;
	}


	/**
	 * Gets the mapDragable flag.
	 * @return
	 */
	public boolean isMapDragable() {
		return mapDragable;
	}


	/**
	 * Sets the mapDragable flag.
	 * @param dragable
	 */
	public void setMapDragable(boolean dragable) {
		this.mapDragable = dragable;
	}


	/**
	 * Gets the mapZoomable flag.
	 * @return
	 */
	public boolean isMapZoomable() {
		return mapZoomable;
	}


	/**
	 * Sets the mapZoomable flag.
	 * @param zoomable
	 */
	public void setMapZoomable(boolean zoomable) {
		this.mapZoomable = zoomable;
	}


	/**
	 * Gets the mapRotatable flag.
	 * @return
	 */
	public boolean isMapRotatable() {
		return mapRotatable;
	}


	/**
	 * Sets the mapRotatable flag.
	 * @param rotatable
	 */
	public void setMapRotatable(boolean rotatable) {
		this.mapRotatable = rotatable;
	}


	/**
	 * Gets the gestureActive flag.
	 * @return
	 */
	public boolean isGestureActive() {
		return gestureActive;
	}


	/**
	 * Saves the {@code MapView}'s current viewpoint in a {@code MapViewpoint}.
	 * @return
	 */
	public MapViewpoint saveViewpoint() {
		return new MapViewpoint(
				projection.getPixelFocusX(),
				projection.getPixelFocusY(),
				projection.getMapFocusX(),
				projection.getMapFocusY(),
				projection.getMapRotation(),
				projection.getZoomPoints());
	}


	/**
	 * Restores the {@code MapView}'s viewpoint from a {@code MapViewpoint}.
	 * @param viewpoint
	 */
	public void restoreViewpoint(MapViewpoint viewpoint) {
		projection.setPixelFocusX(viewpoint.getPixelFocusX());
		projection.setPixelFocusY(viewpoint.getPixelFocusY());
		projection.setMapFocusX(viewpoint.getMapFocusX());
		projection.setMapFocusY(viewpoint.getMapFocusY());
		projection.setMapRotation(viewpoint.getMapRotation());
		projection.setZoomPoints(viewpoint.getMapZoomPoints());
		projection.project();
		invalidate();
	}


	/*
	 * ========================================
	 * OVERRIDING VIEW METHODS
	 * ========================================
	 */


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		projection.setSize(getMeasuredWidth(), getMeasuredHeight());
		invalidate();
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return touchFilter.onTouchEvent(event);
	}


	@Override
	protected void onDraw(Canvas canvas) {
		long frameMillis = SystemClock.uptimeMillis();
		canvas.drawPaint(backgroundPaint);

		// if no map, nothing to do
		if (map == null) {
			return;
		}

		// render all layers
		boolean success = true;
		List<MapLayer> layers = map.getLayers();
		if (layers != null) {
			for (MapLayer layer : layers) {
				success &= layer.render(canvas, projection, frameMillis, invalidateRunnable);
			}
		}

		// redraw in a while if incomplete
		if (!success) {
			postDelayed(invalidateRunnable, REDRAW_DELTA);
		}
	}


	@Override
	public boolean canScrollHorizontally(int direction) {
		return mapDragable;
	}


	@Override
	public boolean canScrollVertically(int direction) {
		return mapDragable;
	}


	/*
	 * ========================================
	 * GESTURE SUPPORT
	 * ========================================
	 */


	@Override
	public void onSingleTouchStart(float x, float y, long eventTime) {
		dragGestureActive = false;
		startTouchPixelX = x;
		startTouchPixelY = y;
		projection.toMapPoint(x, y, lastTouchMapPoint);
		startTouchMapX = lastTouchMapPoint[0];
		startTouchMapY = lastTouchMapPoint[1];
	}


	@Override
	public void onSingleTouchMove(float x, float y, long eventTime) {
		if (mapDragable && !dragGestureActive && (Math.abs(x - startTouchPixelX) > 8) || (Math.abs(y - startTouchPixelY) > 8)) {
			dragGestureActive = true;
		}

		if (dragGestureActive) {
			projection.toMapPoint(x, y, newTouchMapPoint);
			projection.setMapFocusX((projection.getMapFocusX() - newTouchMapPoint[0] + lastTouchMapPoint[0]) & MapPoint.MASK);
			projection.setMapFocusY((projection.getMapFocusY() - newTouchMapPoint[1] + lastTouchMapPoint[1]) & MapPoint.MASK);
			projection.project();
			invalidate();

			projection.toMapPoint(x, y, lastTouchMapPoint);
		}
	}


	@Override
	public void onSingleTouchEnd(long eventTime) {
		if (!dragGestureActive && mapClickable && (map != null)) {
			// send click event to layers
			List<MapLayer> layers = map.getLayers();
			if (layers != null) {
				for (MapLayer layer : layers) {
					if (layer.onClick(startTouchMapX, startTouchMapY)) {
						break;
					}
				}
			}

			projection.focusOnPixel(startTouchPixelX, startTouchPixelY);
			projection.project();
		}

		dragGestureActive = false;
	}


	@Override
	public void onDualTouchStart(float x1, float y1, float x2, float y2, long eventTime) {
		double xDiff = x1 - x2;
		double yDiff = y1 - y2;

		startTouchPixelX = (x1 + x2) / 2;
		startTouchPixelY = (y1 + y2) / 2;
		startTouchDistance = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
		startTouchZoomPoints = projection.getZoomPoints();
		startTouchAngle = (float)(Math.atan2(yDiff, xDiff) * 180 / Math.PI);
		startTouchMapRotation = projection.getMapRotation();

		if (rotateGestureActive) {
			snapRotationAnimator.cancel();
			rotateGestureActive = false;
		}
		if (zoomGestureActive) {
			snapZoomAnimator.cancel();
			zoomGestureActive = false;
		}

		updateGestureActive();
	}


	@Override
	public void onDualTouchMove(float x1, float y1, float x2, float y2, long eventTime) {
		double xDiff = x1 - x2;
		double yDiff = y1 - y2;
		double touchDistance = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
		float touchAngle = (float)(Math.atan2(yDiff, xDiff) * 180 / Math.PI);

		if (!zoomGestureActive && mapZoomable && (startTouchDistance > 0) && (touchDistance > 0)
				&& ((touchDistance / startTouchDistance >  1.05) || (touchDistance / startTouchDistance <  0.95))) {
			zoomGestureActive = true;
		}

		if (!rotateGestureActive && mapRotatable && (startTouchDistance > 0) && (touchDistance > 0)
				&& (Math.abs(touchAngle - startTouchAngle) > 5)) {
			rotateGestureActive = true;
		}

		if (zoomGestureActive || rotateGestureActive) {
			if (!gestureActive) {
				gestureActive = true;
				projection.focusOnPixel(startTouchPixelX, startTouchPixelY);
			}

			if (zoomGestureActive) {
				if (touchDistance > 0) {
					double scale = touchDistance / startTouchDistance;
					int zoomDelta = (int) (Math.log(scale) / LOG_OF_TWO * ZOOM_MULTIPLIER);
					projection.setZoomPoints(startTouchZoomPoints + zoomDelta);
				}
			}

			if (rotateGestureActive) {
				projection.setMapRotation(startTouchMapRotation + touchAngle - startTouchAngle);
			}

			projection.project();
			invalidate();
		}
	}


	@Override
	public void onDualTouchEnd(long eventTime) {
		if (rotateGestureActive) {
			float rotation = projection.getMapRotation();
			float snapRotation = Math.round(rotation / 10) * 10;

			if (rotation != snapRotation) {
				snapRotationAnimator.setFloatValues(rotation, snapRotation);
				snapRotationAnimator.start();
			} else {
				rotateGestureActive = false;
				updateGestureActive();
			}
		}

		if (zoomGestureActive) {
			int zoomPoints = projection.getZoomPoints();
			int snapZoomPoints = startTouchZoomPoints;

			if (Math.abs(zoomPoints - startTouchZoomPoints) > (ZOOM_MULTIPLIER / 4)) {
				if (zoomPoints > startTouchZoomPoints) {
					snapZoomPoints = (int)Math.ceil((double)zoomPoints / ZOOM_MULTIPLIER) * ZOOM_MULTIPLIER;
				} else {
					snapZoomPoints = (int)Math.floor((double)zoomPoints / ZOOM_MULTIPLIER) * ZOOM_MULTIPLIER;
				}
			}

			if (zoomPoints != snapZoomPoints) {
				snapZoomAnimator.setIntValues(zoomPoints, snapZoomPoints);
				snapZoomAnimator.start();
			} else {
				zoomGestureActive = false;
				updateGestureActive();
			}
		}
	}


	/**
	 * Animation listener for snap rotation animation.
	 */
	Animator.AnimatorListener snapRotationAnimationListener = new Animator.AnimatorListener() {
		public void onAnimationCancel(Animator animation) {}
		public void onAnimationRepeat(Animator animation) {}
		public void onAnimationStart(Animator animation) {}

		public void onAnimationEnd(Animator animation) {
			rotateGestureActive = false;
			updateGestureActive();
		}
	};


	/**
	 * Animation listener for snap to zoom animation
	 */
	Animator.AnimatorListener snapZoomAnimationListener = new Animator.AnimatorListener() {
		public void onAnimationCancel(Animator animation) {}
		public void onAnimationRepeat(Animator animation) {}
		public void onAnimationStart(Animator animation) {}

		public void onAnimationEnd(Animator animation) {
			zoomGestureActive = false;
			updateGestureActive();
		}
	};


	/**
	 * Updates the gestureActive flag
	 */
	private void updateGestureActive() {
		gestureActive = dragGestureActive || zoomGestureActive || rotateGestureActive;
	}
}