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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * Implements a static r-tree, loaded from a flat file format. Useful for fast spatial queries of static data.
 * <p>
 * To keep the object count low, arrays are used.
 */
public class StaticRTree {
	/** The number of entries in the r-tree. */
	private int size;

	/** The id of each entry. */
	private int[] ids;

	/** The offsets of each entry. Used to address a secondary table*/
	private int[] offsets;

	/** The type of each entry. */
	private int[] types;

	/** The bounds (minX, minY, maxX, maxY) of each entry. */
	private int[] bounds;

	/** The sibling of each entry. */
	private int[] siblings;

	/** The parent of each entry. */
	private int[] parents;


	/**
	 * Creates a new StaticRTree from the specified file.
	 * <p>
	 * Note, the file must be big-endian as the class uses DataInputStream.
	 * @param file the file containing the r-tree.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public StaticRTree(File file) throws FileNotFoundException, IOException {
		DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
		try {
			size = dataInputStream.readInt();
			ids = readIntArray(dataInputStream, size);
			offsets = readIntArray(dataInputStream, size);
			types = readIntArray(dataInputStream, size);
			bounds = readIntArray(dataInputStream, size * 4);
			siblings = readIntArray(dataInputStream, size);
			parents = readIntArray(dataInputStream, size);
		} finally {
			dataInputStream.close();
		}
	}


	/**
	 * Reads an integer array of the the specified length.
	 * @param dataInputStream the DataInputStream to read from.
	 * @param length the length of the array.
	 * @return the array.
	 * @throws IOException
	 */
	private int[] readIntArray(DataInputStream dataInputStream, int length) throws IOException {
		int[] array = new int[length];
		for (int i = 0; i < length; i++) {
			array[i] = dataInputStream.readInt();
		}
		return array;
	}


	/**
	 * Queries the tree for nodes within a given rectangle.
	 * @param queryRect region of interest.
	 * @param queryType type restriction, -1 for none.
	 * @param results buffer for result indexs (NOT ids!), null to count results.
	 * @param maxResults maximum number of results to retrieve.
	 * @return the number of results retrieved.
	 */
	public int query(MapRect queryRect, int queryType, int[] results, int maxResults) {
		return queryRec(queryRect, queryType, results, maxResults, 0, 0);
	}


	/**
	 * Performs the query recursively.
	 * @param queryRect
	 * @param queryType
	 * @param results
	 * @param maxResults
	 * @param index
	 * @param resultsIndex
	 * @return
	 */
	private int queryRec(MapRect queryRect, int queryType, int[] results, int maxResults, int index, int resultsIndex) {
		if ((results != null) && (resultsIndex >= maxResults)) {
			return resultsIndex;
		}

		// only continue processing if the node intersects the region of interest
		if (!MapRect.intersects(queryRect.getMinX(), queryRect.getMinY(), queryRect.getMaxX(), queryRect.getMaxY(),
				bounds[index * 4], bounds[index * 4 + 1], bounds[index * 4 + 2], bounds[index * 4 + 3])) {
			return resultsIndex;
		}

		// save result if the type matches query type or no query type is specified
		if ((queryType == -1) || (types[index] == queryType)) {
			if (results != null) {
				results[resultsIndex] = index;
			}
			resultsIndex++;
		}

		// iterate through all children
		int childIndex = index + 1;
		if ((childIndex < size) && (parents[childIndex] == index)) {
			while (true) {
				resultsIndex = queryRec(queryRect, queryType, results, maxResults, childIndex, resultsIndex);
				childIndex = siblings[childIndex];
				if (childIndex == 0) {
					break;
				}
			}
		}

		return resultsIndex;
	}


	/**
	 * Gets the ID of the specified index.
	 * @param index
	 * @return
	 */
	public int getId(int index) {
		return ids[index];
	}


	/**
	 * Gets the offset of the specified index.
	 * @param index
	 * @return
	 */
	public int getOffset(int index) {
		return offsets[index];
	}


	/**
	 * Gets the type of the specified index.
	 * @param index
	 * @return
	 */
	public int getType(int index) {
		return types[index];
	}


	/**
	 * Gets the bounds of the specified index.
	 * @param index
	 * @return
	 */
	public MapRect getBounds(int index) {
		return new MapRect(bounds[index * 4], bounds[index * 4 + 1], bounds[index * 4 + 2], bounds[index * 4 + 3]);
	}


	/**
	 * Gets the min point, i.e. (minX, minY), of the specified index.
	 * @param index
	 * @return
	 */
	public MapPoint getPoint(int index) {
		return new MapPoint(bounds[index * 4], bounds[index * 4 + 1]);
	}


	/**
	 * Gets the size of the tree.
	 * @return
	 */
	public int getSize() {
		return size;
	}

}