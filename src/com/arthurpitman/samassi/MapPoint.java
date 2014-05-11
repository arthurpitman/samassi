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

package com.arthurpitman.samassi;


/**
 * Represents a point on the Earth's surface in Web Mercator projection.
 * <p>
 * x and y are mapped to values between 0 and DENOMINATOR, z is altitude in millimeters.
 */
public class MapPoint {

	public static final double DEGREES_PER_RADIAN = 180 / Math.PI;

	public static final int MILLIMETERS_PER_METER = 1000;

	/* Base value: i.e. v = int_val / BASE. */
	public static final int BASE = 1 << 30;

	/** Mask for maintaining integer values. */
	public static final int MASK = 0x3FFFFFFF;

	/** x value between 0 and BASE: [-180 degrees, +180 degrees]. */
	private final int x;

	/** y value between 0 and BASE: [85.05 degrees, -85.05 degrees]. */
	private final int y;

	/** Altitude in millimeters above the WGS84 ellipsoid. */
	private final int z;


	/**
	 * Creates a new MapPoint with the specified x and y values.
	 * @param x
	 * @param y
	 */
	public MapPoint(int x, int y) {
		this.x = x;
		this.y = y;
		this.z = 0;
	}


	/**
	 * Creates a new MapPoint with the specified x, y and z values.
	 * @param x
	 * @param y
	 * @param z
	 */
	public MapPoint(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}


	/**
	 * Gets the x component.
	 * @return
	 */
	public int getX() {
		return x;
	}


	/**
	 * Gets the y component.
	 * @return
	 */
	public int getY() {
		return y;
	}


	/**
	 * Gets the z component.
	 * @return
	 */
	public int getZ() {
		return z;
	}


	/**
	 * Converts a point in the Spherical Mercator projection to WGS84 datum.
	 * @return the resulting GeoPoint.
	 */
	public GeoPoint toGeoPoint() {
		return new GeoPoint(
			((double)(x) / BASE * 360.0) - 180.0,
			Math.atan(Math.sinh(Math.PI - (2.0 * Math.PI * (double)(y) / BASE))) * DEGREES_PER_RADIAN,
			(double)(z) / MILLIMETERS_PER_METER);
	}


	/**
	 * Converts a MapPoint integer component to a double component.
	 * @param v
	 * @return
	 */
	public static double toMapPointDouble(int v) {
		return ((double) v) / BASE;
	}


	/**
	 * Converts a MapPoint double component to an integer component.
	 * @param v
	 * @return
	 */
	public static int toMapPointInt(double v) {
		return Math.min((int)Math.round(v * BASE), MASK);
	}


	@Override
	public String toString() {
		return "(x: " + x + ", y: " + y + ", z: " + z + ")";
	}
}