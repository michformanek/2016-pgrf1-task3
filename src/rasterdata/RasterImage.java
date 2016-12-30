package rasterdata;

import java.util.Optional;

import annotations.NotNull;

/**
 * Represents raster image of pixels of the given type
 * 
 * @author vanekja1
 *
 * @param <PixelType>
 *            Type parameter specifying the pixel type
 */
public interface RasterImage<PixelType> {
	/**
	 * Returns value of the pixel at the given column and row address if valid,
	 * empty Optional otherwise
	 * 
	 * @param c
	 *            column address
	 * @param r
	 *            row address
	 * @return pixel value, empty Optional if address is invalid, not null
	 */
	@NotNull
	Optional<PixelType> getPixel(int c, int r);

	/**
	 * Returns a RasterImage with the value of the pixel at the given column and
	 * row address set to the given value
	 * 
	 * @param c
	 *            column address
	 * @param r
	 *            row address
	 * @param pixel
	 *            value to be set to the pixel, must not be null
	 * @return RasterImage with pixel set to given value, this if address is
	 *         invalid
	 */
	@NotNull
	RasterImage<PixelType> withPixel(int c, int r, @NotNull PixelType pixel);

	/**
	 * Returns the number of rows of this raster image
	 * 
	 * @return number of rows
	 */
	int getHeight();

	/**
	 * Returns the number of columns of this raster image
	 * 
	 * @return number of columns
	 */
	int getWidth();
}
