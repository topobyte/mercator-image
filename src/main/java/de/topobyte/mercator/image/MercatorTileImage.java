package de.topobyte.mercator.image;

import de.topobyte.geomath.WGS84;
import de.topobyte.jgs.transform.CoordinateTransformer;

/**
 * An image tile that shows a part of the world using Mercator projection.
 * 
 * The MercatorTileImage implements the CoordinateTransformer interface and
 * thereby transforms lon/lat coordinates to pixel coordinates on the image.
 * 
 * @author Sebastian Kuerten (sebastian@topobyte.de)
 */
public class MercatorTileImage implements CoordinateTransformer
{

	private int tileZoom;
	private int tileX;
	private int tileY;

	private double lon1;
	private double lat1;
	private double lon2;
	private double lat2;

	private int tileWidth = 256;
	private int tileHeight = 256;

	/**
	 * Create a tile defined by zoom, x and y and a default size of 256x256
	 * pixels.
	 * 
	 * @param zoom
	 *            the zoom level.
	 * @param x
	 *            the x coordinate.
	 * @param y
	 *            the y coordinate.
	 */
	public MercatorTileImage(int zoom, int x, int y)
	{
		this.tileZoom = zoom;
		this.tileX = x;
		this.tileY = y;

		lon1 = WGS84.merc2lon(x, 1 << zoom);
		lat1 = WGS84.merc2lat(y, 1 << zoom);
		lon2 = WGS84.merc2lon(x + 1, 1 << zoom);
		lat2 = WGS84.merc2lat(y + 1, 1 << zoom);
	}

	/**
	 * Create a tile defined by zoom, x and y with a user defined width and
	 * height.
	 * 
	 * @param zoom
	 *            the zoom level.
	 * @param x
	 *            the x coordinate.
	 * @param y
	 *            the y coordinate.
	 * @param tileWidth
	 *            the width of the tile
	 * @param tileHeight
	 *            the height of the tile
	 */
	public MercatorTileImage(int zoom, int x, int y, int tileWidth,
			int tileHeight)
	{
		this(zoom, x, y);
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}

	@Override
	public String toString()
	{
		return String.format("%d %d %d", tileZoom, tileX, tileY);
	}

	@Override
	public double getX(double lon)
	{
		double absx = WGS84.lon2merc(lon, 1 << tileZoom);
		double pos = (absx - tileX) * tileWidth;
		return pos;
	}

	@Override
	public double getY(double lat)
	{
		double absy = WGS84.lat2merc(lat, 1 << tileZoom);
		double pos = (absy - tileY) * tileHeight;
		return pos;
	}

	/**
	 * @return the zoom level.
	 */
	public int getTileZoom()
	{
		return tileZoom;
	}

	/**
	 * @return the x coordinate.
	 */
	public int getTileX()
	{
		return tileX;
	}

	/**
	 * @return the y coordinate.
	 */
	public int getTileY()
	{
		return tileY;
	}

	/**
	 * @return the leftmost longitude.
	 */
	public double getLon1()
	{
		return lon1;
	}

	/**
	 * @return the top latitude.
	 */
	public double getLat1()
	{
		return lat1;
	}

	/**
	 * @return the rightmost longitude.
	 */
	public double getLon2()
	{
		return lon2;
	}

	/**
	 * @return the bottom latitude.
	 */
	public double getLat2()
	{
		return lat2;
	}

	/**
	 * Set zoom level to tileZoom.
	 * 
	 * @param tileZoom
	 *            the new zoom level.
	 */
	public void setTileZoom(int tileZoom)
	{
		this.tileZoom = tileZoom;
	}

	/**
	 * Set the x coordinate.
	 * 
	 * @param tileX
	 *            the new x coordinate.
	 */
	public void setTileX(int tileX)
	{
		this.tileX = tileX;
	}

	/**
	 * Set the y coordinate.
	 * 
	 * @param tileY
	 *            the new y coordinate.
	 */
	public void setTileY(int tileY)
	{
		this.tileY = tileY;
	}

	/**
	 * Get the width of the tile
	 * 
	 * @return the width
	 */
	public int getTileWidth()
	{
		return tileWidth;
	}

	/**
	 * Get the height of the tile
	 * 
	 * @return the height
	 */
	public int getTileHeight()
	{
		return tileHeight;
	}

}
