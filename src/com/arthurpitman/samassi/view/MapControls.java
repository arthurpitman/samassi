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

package com.arthurpitman.samassi.view;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.arthurpitman.samassi.R;


/**
 * Displays a set of controls to assist devices without multi-touch.
 */
public class MapControls extends LinearLayout {

	private MapView mapView;

	private ObjectAnimator zoomAnimator;
	private ObjectAnimator rotateAnimator;


	/**
	 * Creates a new MapControls.
	 * @param context
	 */
	public MapControls(Context context) {
		this(context, null);
	}


	/**
	 * Creates a new MapControls.
	 * @param context
	 * @param attrs
	 */
	public MapControls(Context context, AttributeSet attrs) {
		super(context, attrs);

		setOrientation(LinearLayout.HORIZONTAL);
		setGravity(Gravity.CENTER_VERTICAL);

		LayoutInflater.from(context).inflate(R.layout.view_map_controls, this, true);

		ImageButton zoomInButton = (ImageButton) findViewById(R.id.zoom_in_button);
		zoomInButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickZoomIn();
			}
		});

		ImageButton zoomOutButton = (ImageButton) findViewById(R.id.zoom_out_button);
		zoomOutButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickZoomOut();
			}
		});

		ImageButton rotateCwButton = (ImageButton) findViewById(R.id.rotate_cw_button);
		rotateCwButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickRotateCW();
			}
		});

		ImageButton rotateCcwButton = (ImageButton) findViewById(R.id.rotate_ccw_button);
		rotateCcwButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickRotateCCW();
			}
		});
	}


	/**
	 * Gets the associated {@link MapView}.
	 * @return
	 */
	public MapView getMapView() {
		return mapView;
	}


	/**
	 * Sets the associated {@link MapView}.
	 * @param mapView
	 */
	public void setMapView(MapView mapView) {
		this.mapView = mapView;

		TimeInterpolator interpolator = new DecelerateInterpolator();

		rotateAnimator = ObjectAnimator.ofFloat(mapView, "mapRotation", 0);
		rotateAnimator.setDuration(150);
		rotateAnimator.setInterpolator(interpolator);

		zoomAnimator = ObjectAnimator.ofInt(mapView, "mapZoomPoints", 0);
		zoomAnimator.setDuration(150);
		zoomAnimator.setInterpolator(interpolator);
	}


	/**
	 * Conducts clockwise rotation.
	 */
	private void onClickRotateCW() {
		if (mapView == null)
			return;

		if (mapView.isGestureActive() || rotateAnimator.isStarted())
			return;

		mapView.focusOnCenter();

		float mapRotation = mapView.getMapRotation();
		float targetMapRotation = Math.round(mapRotation / 10) * 10 + 10;

		rotateAnimator.setFloatValues(mapRotation, targetMapRotation);
		rotateAnimator.start();
	}


	/**
	 * Conducts counter-clockwise rotation.
	 */
	private void onClickRotateCCW() {
		if (mapView == null)
			return;

		if (mapView.isGestureActive() || rotateAnimator.isStarted())
			return;

		mapView.focusOnCenter();

		float mapRotation = mapView.getMapRotation();
		float targetMapRotation = Math.round(mapRotation / 10) * 10 - 10;

		rotateAnimator.setFloatValues(mapRotation, targetMapRotation);
		rotateAnimator.start();
	}


	/**
	 * Zooms in.
	 */
	private void onClickZoomIn() {
		if (mapView == null)
			return;

		if (mapView.isGestureActive() || zoomAnimator.isStarted())
			return;

		mapView.focusOnCenter();

		int mapZoomPoints = mapView.getMapZoomPoints();
		int targetMapZoomPoints = (Math.round((float)mapZoomPoints / MapView.ZOOM_MULTIPLIER) + 1) * MapView.ZOOM_MULTIPLIER;

		zoomAnimator.setIntValues(mapZoomPoints, targetMapZoomPoints);
		zoomAnimator.start();
	}


	/**
	 * Zooms out.
	 */
	private void onClickZoomOut() {
		if (mapView == null)
			return;

		if (mapView.isGestureActive() || zoomAnimator.isStarted())
			return;

		mapView.focusOnCenter();

		int mapZoomPoints = mapView.getMapZoomPoints();
		int targetMapZoomPoints = (Math.round((float)mapZoomPoints / MapView.ZOOM_MULTIPLIER) - 1) * MapView.ZOOM_MULTIPLIER;

		zoomAnimator.setIntValues(mapZoomPoints, targetMapZoomPoints);
		zoomAnimator.start();
	}
}