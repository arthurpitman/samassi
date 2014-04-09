/*
 * Copyright (C) 2012 - 2014 Arthur Pitman
 *
 * Algorithm based on "Fast approximation of geodesic distance between points specified by Web Mercator coordinates" by Richard P. Curnow
 * Contact: Richard Curnow at rc@rc0.org.uk
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
 * Calculates distance between two points, trading accuracy for speed.
 * <p>
 * Based on "Fast approximation of geodesic distance between points specified by Web Mercator coordinates" by Richard P. Curnow.
 * <p/>
 * Contact rc@rc0.org.uk or visit http://about.me/rc0 for more information.
 */
public class DistanceEstimator {

   private static final double A = 6378137;
   private static final double A_PI_2 = 2 * Math.PI * A;

   private static final double J0 = 0.99330562;
   private static final double J2 = 0.18663111;
   private static final double J4 = -1.45510549;
   private static final double K2 = 19.42975297;
   private static final double K4 = 74.22319781;


   /**
	* Calculates the approximate distance between two MapPoints.
	* @param point1
	* @param point2
	* @param useZ
	* @return
	*/
   public static double get(MapPoint point1, MapPoint point2, boolean useZ) {
	   double x1 = ((double) point1.getX()) / MapPoint.BASE;
	   double y1 = ((double) point1.getY()) / MapPoint.BASE;

	   double x2 = ((double) point2.getX()) / MapPoint.BASE;
	   double y2 = ((double) point2.getY()) / MapPoint.BASE;

	   double deltaX = x1 - x2;
	   double deltaY = y1 - y2;
	   double v = (y1 + y2) / 2 - 0.5;
	   double v2 = v * v;
	   double v4 = v2 * v2;
	   double deltaYDash = (J0 + J2 * v2 + J4 * v4) * deltaY;

	   double distance = A_PI_2 * Math.sqrt(deltaX * deltaX + deltaYDash * deltaYDash) / (1 + K2 * v2 + K4 * v4);

	   if (useZ) {
		   double elevationDelta = (double)(point1.getZ() - point2.getZ()) / MapPoint.MILLIMETERS_PER_METER;
		   distance = Math.sqrt(distance * distance + elevationDelta * elevationDelta);
	   }

	   return distance;
   }

}