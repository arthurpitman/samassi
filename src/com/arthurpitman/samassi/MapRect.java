/*
 * Copyright (C) 2012, 2013 Arthur Pitman.
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
 * Represents a rectangle in the in Spherical Mercator projection.
 * <p>
 * Coordinates should be in the range [0,{@link MapPoint#BASE BASE}].
 * <p>
 * Coordinates wrap around in the x dimension, e.g. minX = a, maxX = b where a > b specifies [a,BASE] + [0, b].
 * Note minX = c, maxX = c specifies [c,c] and not [0,c] + [c,BASE].
 * No wrap around is possible in the y dimension.
 *<p>
 * TODO: remove redundant code in intersects and contains methods, investigate Proguard for this.
 */
public class MapRect {

	/** Minimum x value of rectangle. Range: [0, BASE]. */
	private final int minX;

	/** Minimum y value of rectangle. Range: [0, BASE]. */
	private final int minY;

	/** Maximum x value of rectangle. Range: [0, BASE]. */
	private final int maxX;

	/** Maximum y value of rectangle. Range: [0, BASE]. */
	private final int maxY;


	/**
	 * Creates a new MapRect with the specified coordinates.
	 * @param minX minimum x coordinate.
	 * @param minY minimum y coordinate.
	 * @param maxX maximum x coordinate.
	 * @param maxY maximum y coordinate.
	 */
	public MapRect(int minX, int minY, int maxX, int maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}


	/**
	 * Gets the minimum x component.
	 * @return
	 */
	public int getMinX() {
		return minX;
	}


	/**
	 * Gets the minimum y component.
	 * @return
	 */
	public int getMinY() {
		return minY;
	}


	/**
	 * Gets the maximum x component.
	 * @return
	 */
	public int getMaxX() {
		return maxX;
	}


	/**
	 * Gets the maximum y component.
	 * @return
	 */
	public int getMaxY() {
		return maxY;
	}


	/**
	 * Tests if this MapRect intersects with the specified MapRect.
	 * @param other the other MapRect.
	 * @return true if intersection occurs, otherwise false.
	 */
	public boolean intersects(MapRect other)
	{
		// check y dimension first
		if ((maxY < other.minY) || (minY > other.maxY))
			return false;

		// check x dimension, four cases:
		if (minX <= maxX) {
			if (other.minX <= other.maxX) {
				return ((maxX >= other.minX) && (minX <= other.maxX));
			} else {
				return ((maxX >= other.minX) || (minX <= other.maxX));
			}
		} else {
			if (other.minX <= other.maxX) {
				return ((maxX >= other.minX) || (minX <= other.maxX));
			} else {
				return true;
			}
		}
	}


	/**
	 * Tests if this MapRect contains the specified MapPoint.
	 * @param point the MapPoint to test.
	 * @return true if this MapRect contains the specified MapPoint, otherwise false.
	 */
	public boolean contains(MapPoint point)
	{
		int pointX = point.getX();
		int pointY = point.getY();

		// check y dimension first
		if ((pointY < minY) || (pointY > maxY))
			return false;

		// check x dimension, two cases:
		if (minX <= maxX) {
			// standard case
			return (pointX >= minX) && (pointX <= maxX);
		} else {
			// wrap around case
			return ((pointX >= minX) || (pointX <= maxX));
		}
	}


	/**
	 * Tests if this MapRect contains the specified MapRect.
	 * @param other the MercatorRect to test.
	 * @return true if this MapRect contains the specified MapRect, otherwise false.
	 */
	public boolean contains(MapRect other)
	{
		// check y dimension first
		if ((other.minY < minY) || (other.maxY > maxY))
			return false;

		// check x dimension
		if (minX <= maxX) {
			if (other.minX <= other.maxX) {
				return (other.minX >= minX) && (other.maxX <= maxX);
			} else {
				return (minX == 0) && (maxX == MapPoint.BASE);
			}
		} else {
			if (other.minX <= other.maxX) {
				return (other.minX >= minX) || (other.maxX <= maxX);
			} else {
				return (other.minX >= minX) && (other.maxX <= maxX);
			}
		}
	}


	/**
	 * Tests if the first map rectangle intersects the second map rectangle.
	 * @param firstMinX
	 * @param firstMinY
	 * @param firstMaxX
	 * @param firstMaxY
	 * @param secondMinX
	 * @param secondMinY
	 * @param secondMaxX
	 * @param secondMaxY
	 * @return true if the first map rectangle intersects the second, otherwise false.
	 */
	public static boolean intersects(int firstMinX, int firstMinY, int firstMaxX, int firstMaxY,
			int secondMinX, int secondMinY, int secondMaxX, int secondMaxY) {
		// check y dimension first
		if ((firstMaxY < secondMinY) || (firstMinY > secondMaxY))
			return false;

		// check x dimension, four cases:
		if (firstMinX <= firstMaxX) {
			if (secondMinX <= secondMaxX) {
				return ((firstMaxX >= secondMinX) && (firstMinX <= secondMaxX));
			} else {
				return ((firstMaxX >= secondMinX) || (firstMinX <= secondMaxX));
			}
		} else {
			if (secondMinX <= secondMaxX) {
				return ((firstMaxX >= secondMinX) || (firstMinX <= secondMaxX));
			} else {
				return true;
			}
		}
	}


	/**
	 * Tests if the map rectangle contains the specified point.
	 * @param minX
	 * @param minY
	 * @param maxX
	 * @param maxY
	 * @param pointX
	 * @param pointY
	 * @return true if the map rectangle contains the point, otherwise false.
	 */
	public static boolean contains(int minX, int minY, int maxX, int maxY, int pointX, int pointY) {
		// check y dimension first
		if ((pointY < minY) || (pointY > maxY))
			return false;

		// check x dimension, two cases:
		if (minX <= maxX) {
			// standard case
			return (pointX >= minX) && (pointX <= maxX);
		} else {
			// wrap around case
			return ((pointX >= minX) || (pointX <= maxX));
		}
	}


	/**
	 * Tests if the first map rectangle contains the second map rectangle.
	 * @param firstMinX
	 * @param firstMinY
	 * @param firstMaxX
	 * @param firstMaxY
	 * @param secondMinX
	 * @param secondMinY
	 * @param secondMaxX
	 * @param secondMaxY
	 * @return true if the first map rectangle contains the second, otherwise false.
	 */
	public static boolean contains(int firstMinX, int firstMinY, int firstMaxX, int firstMaxY,
			int secondMinX, int secondMinY, int secondMaxX, int secondMaxY) {
		// check y dimension first
		if ((secondMinY < firstMinY) || (secondMaxY > firstMaxY))
			return false;

		// check x dimension
		if (firstMinX <= firstMaxX) {
			if (secondMinX <= secondMaxX) {
				return (secondMinX >= firstMinX) && (secondMaxX <= firstMaxX);
			} else {
				return (firstMinX == 0) && (firstMaxX == MapPoint.BASE);
			}
		} else {
			if (secondMinX <= secondMaxX) {
				return (secondMinX >= firstMinX) || (secondMaxX <= firstMaxX);
			} else {
				return (secondMinX >= firstMinX) && (secondMaxX <= firstMaxX);
			}
		}
	}


	@Override
	public String toString() {
		return "(" + minX +", " + minY + ")-(" + maxX +", " + maxY + ")";
	}
}