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

package com.arthurpitman.samassi.geometry;

import com.arthurpitman.samassi.MapPoint;


/**
 * A collection of geometry functions for <code>MapPoint</code>s.
 * <p/>
 * Note: the routines currently do not handle wrap-around issues;
 * Consider re-projecting the geometries where appropriate first.
 */
public class GeometryTools {

	/**
	 * Finds parameter t for the point T on segment AB such that PT is is perpendicular to AB.
	 * @param p
	 * @param a
	 * @param b
	 * @return A value [0,1] if T is on AB.
	 */
	public static double findClosestTOnSegment(MapPoint p, MapPoint a, MapPoint b) {
		double pX = MapPoint.toMapPointDouble(p.getX());
		double pY = MapPoint.toMapPointDouble(p.getY());

		double aX = MapPoint.toMapPointDouble(a.getX());
		double aY = MapPoint.toMapPointDouble(a.getY());

		double bX = MapPoint.toMapPointDouble(b.getX());
		double bY = MapPoint.toMapPointDouble(b.getY());

		// Vector AP = P - A:
		double apX = pX - aX;
	 	double apY = pY - aY;

		// Vector AB = B - A;
		double abX = bX - aX;
		double abY = bY - aY;

		// t = (AP * AB) / (AB * AB)
		return (apX * abX + apY * abY) / (abX * abX + abY * abY);
	}


	/**
	 * Interpolates two MapPoints.
	 * @param p1
	 * @param p2
	 * @param t
	 * @return
	 */
	public static MapPoint interpolate(MapPoint p1, MapPoint p2, double t) {
		double t2 = 1 - t;

		return new MapPoint((int)Math.round(p1.getX() * t2 + p2.getX() * t),
				(int)Math.round(p1.getY() * t2 + p2.getY() * t),
				(int)Math.round(p1.getZ() * t2 + p2.getZ() * t));
	}
}