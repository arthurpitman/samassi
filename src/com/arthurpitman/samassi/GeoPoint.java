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
 * Represents a point on the Earth's surface in WGS84 datum.
 * <p/>
 * Latitude and longitude are specified in degrees, altitude in meters.
 */
public class GeoPoint {

	/** Radians per degree constant. */
	public static final double RADIANS_PER_DEGREE = Math.PI / 180;

	/** Longitude in degrees relative to the Prime Meridian, east is positive. */
	private final double longitude;

	/** Latitude in degrees relative to the equator, northern hemisphere is positive. */
	private final double latitude;

	/** Altitude in meters above the WGS84 ellipsoid. */
	private final double altitude;


	/**
	 * Creates a new GeoPoint with the specified longitude and latitude.
	 * @param longitude
	 * @param latitude
	 */
	public GeoPoint(double longitude, double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = 0;
	}


	/**
	 * Creates a new GeoPoint with the specified longitude, latitude and altitude.
	 * @param longitude
	 * @param latitude
	 * @param altitude
	 */
	public GeoPoint(double longitude, double latitude, double altitude) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = altitude;
	}


	/**
	 * Gets the longitude.
	 * @return
	 */
	public double getLongitude() {
		return longitude;
	}


	/**
	 * Gets the latitude.
	 * @return
	 */
	public double getLatitude() {
		return latitude;
	}


	/**
	 * Gets the altitude.
	 * @return
	 */
	public double getAltitude() {
		return altitude;
	}


	/**
	 * Converts a point in the WGS84 datum to a map point in the Spherical Mercator projection.
	 * @return the resulting MapPoint.
	 */
	public MapPoint toMapPoint() {
		double latitudeRadians = latitude * RADIANS_PER_DEGREE;
		return new MapPoint(
			Math.min((int)Math.round((longitude + 180.0) / 360.0 * MapPoint.BASE), MapPoint.MASK),
			Math.min((int)Math.round((1.0 - Math.log(Math.tan(latitudeRadians) + 1.0 / Math.cos(latitudeRadians)) / Math.PI) / 2.0 * MapPoint.BASE), MapPoint.MASK),
			(int)Math.round(altitude * MapPoint.MILLIMETERS_PER_METER));
	}


	@Override
	public String toString() {
		return "(Lon: " + longitude + ", Lat: " + latitude + ", Alt: " + altitude + ")";
	}
}