// Copyright 2015 Sebastian Kuerten
//
// This file is part of mercator-image.
//
// mercator-image is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// mercator-image is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with mercator-image. If not, see <http://www.gnu.org/licenses/>.

package de.topobyte.mercator.image;

import de.topobyte.adt.geo.BBox;
import de.topobyte.geomath.WGS84;
import de.topobyte.jgs.transform.CoordinateTransformer;

/**
 * An image that shows a part of the world using Mercator projection. It is
 * constructed from a defining bounding box and some pixel width and height. The
 * MercatorImage is constructed such that the whole defining bounding box is
 * visible within the image. That means that the image may actually represent a
 * bigger bounding box than the one that was used at construction time. This is
 * called the visible bounding box. The bounding box used for object creation is
 * called the defining bounding box.
 * 
 * The MercatorImage implements the CoordinateTransformer interface and thereby
 * transforms lon/lat coordinates to pixel coordinates on the image.
 * 
 * @author Sebastian Kuerten (sebastian@topobyte.de)
 */
public class MercatorImage implements CoordinateTransformer
{

	private int width;
	private int height;
	private double mlon1;
	private double mlat1;
	private double mlon2;
	private double mlat2;

	// worldsize is the size of the Mercator square that coordinates need to be
	// projected on
	private double worldsize;
	// sx, sy are the coordinates on the upper left corner of the image in image
	// coordniates [0..worldsize]
	private double sx, sy;

	/**
	 * Create a new MercatorImage with the given size and positional
	 * information.
	 * 
	 * @param bbox
	 *            the bbox to cover.
	 * @param width
	 *            the width of the image.
	 * @param height
	 *            the height of the image.
	 */
	public MercatorImage(BBox bbox, int width, int height)
	{
		this(bbox.getLon1(), bbox.getLat1(), bbox.getLon2(), bbox.getLat2(),
				width, height);
	}

	/**
	 * Create a new MercatorImage with the given size and positional
	 * information.
	 * 
	 * @param lon1
	 *            leftmost longitude.
	 * @param lat1
	 *            top latitude.
	 * @param lon2
	 *            rightmost longitude.
	 * @param lat2
	 *            bottom latitude.
	 * @param width
	 *            the width of the image in pixels.
	 * @param height
	 *            the height of the image in pixels.
	 */
	public MercatorImage(double lon1, double lat1, double lon2, double lat2,
			int width, int height)
	{
		this.width = width;
		this.height = height;
		mlon1 = lon1;
		mlat1 = lat1;
		mlon2 = lon2;
		mlat2 = lat2;

		/*
		 * check order of lat / lon pairs
		 */

		if (mlon1 > mlon2) {
			double tmp = mlon1;
			mlon1 = mlon2;
			mlon2 = tmp;
		}
		if (mlat1 < mlat2) {
			double tmp = mlat1;
			mlat1 = mlat2;
			mlat2 = tmp;
		}

		// calculate scale value, make sure that the whole bbox fits into the
		// image
		double x1 = WGS84.lon2merc(mlon1);
		double x2 = WGS84.lon2merc(mlon2);
		double y1 = WGS84.lat2merc(mlat1);
		double y2 = WGS84.lat2merc(mlat2);
		double xs = x2 - x1;
		double ys = y2 - y1;

		double scaleX = width / xs;
		double scaleY = height / ys;
		this.worldsize = Math.min(scaleX, scaleY);

		// we might need some offset values if bbox and width/height do not
		// align perfectly
		this.sx = WGS84.lon2merc(mlon1, worldsize);
		this.sy = WGS84.lat2merc(mlat1, worldsize);

		double px1 = WGS84.lon2merc(mlon1, worldsize) - sx;
		double px2 = WGS84.lon2merc(mlon2, worldsize) - sx;
		double py1 = WGS84.lat2merc(mlat1, worldsize) - sy;
		double py2 = WGS84.lat2merc(mlat2, worldsize) - sy;
		double dx = px2 - px1;
		double dy = py2 - py1;
		if (dx < width) {
			sx -= (width - dx) / 2;
		}
		if (dy < height) {
			sy -= (height - dy) / 2;
		}
	}

	@Override
	public double getX(double lon)
	{
		return WGS84.lon2merc(lon, worldsize) - sx;
	}

	@Override
	public double getY(double lat)
	{
		return WGS84.lat2merc(lat, worldsize) - sy;
	}

	/**
	 * Get the bounding box that was used to create this image. Note that this
	 * may be different from the visible bounding box.
	 * 
	 * @return the defining bounding box
	 */
	public BBox getDefiningBoundingBox()
	{
		return new BBox(mlon1, mlat1, mlon2, mlat2);
	}

	/**
	 * Get the bounding box that is really visible on the image. This box may
	 * differ from the input bounding box, because the image contains the whole
	 * input bounding box and may thus show an actual bounding box that is
	 * bigger.
	 * 
	 * @return the visible bounding box
	 */
	public BBox getVisibleBoundingBox()
	{
		double lon1 = WGS84.merc2lon(sx, worldsize);
		double lon2 = WGS84.merc2lon(sx + width, worldsize);
		double lat1 = WGS84.merc2lat(sy, worldsize);
		double lat2 = WGS84.merc2lat(sy + height, worldsize);
		return new BBox(lon1, lat1, lon2, lat2);
	}

	/**
	 * @return the width of the image in pixels.
	 */
	public int getWidth()
	{
		return width;
	}

	/**
	 * @return the height of the image in pixels.
	 */
	public int getHeight()
	{
		return height;
	}

	/**
	 * @return the leftmost longitude of the defining bounding box.
	 */
	public double getLon1()
	{
		return mlon1;
	}

	/**
	 * @return the top latitude of the defining bounding box.
	 */
	public double getLat1()
	{
		return mlat1;
	}

	/**
	 * @return the rightmost longitude of the defining bounding box.
	 */
	public double getLon2()
	{
		return mlon2;
	}

	/**
	 * @return the bottom latitude of the defining bounding box.
	 */
	public double getLat2()
	{
		return mlat2;
	}

	/**
	 * The image shows a part of the whole world, which is projected onto a
	 * Mercator square whose size can be obtained using
	 * <code>{@link #getWorldSize()}</code>. Get the relative position of the
	 * visible part of the image on this Mercator square in image space units.
	 * 
	 * @return the x coordinate in image space where the specified leftmost
	 *         coordinate is.
	 */
	public double getImageSx()
	{
		return sx;
	}

	/**
	 * The image shows a part of the whole world, which is projected onto a
	 * Mercator square whose size can be obtained using
	 * <code>{@link #getWorldSize()}</code>. Get the relative position of the
	 * visible part of the image on this Mercator square in image space units.
	 * 
	 * @return the y coordinate in image space where the specified top
	 *         coordinate is.
	 */
	public double getImageSy()
	{
		return sy;
	}

	/**
	 * The image shows a part of the whole world which is projected onto a
	 * Mercator square, whose size can be obtained using this method.
	 * 
	 * @return the internal scale used. This is equivalent to the Mercator
	 *         worldsize, i.e. the size in pixels of an image that would show
	 *         the whole world.
	 */
	public double getWorldSize()
	{
		return worldsize;
	}

}
