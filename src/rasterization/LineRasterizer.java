package rasterization;

import annotations.NotNull;
import rasterdata.RasterImage;

public interface LineRasterizer<PixelType> {
	/**
	 * Returns a raster image with a rasterized line described in a right-handed (y axis inverted with respect to raster image row axis) normalized coordinate system 
	 * @param img raster image to form background to the line, must not be null
	 * @param x1 x-coordinate of the start point, normalized to [-1,1]
	 * @param y1 y-coordinate of the start point, normalized to [-1,1]
	 * @param x2 x-coordinate of the end point, normalized to [-1,1]
	 * @param y2 y-coordinate of the end point, normalized to [-1,1]
	 * @param pixel pixel value to store in each line pixel, must not be null
	 * @return raster image with the line rasterized over the contents of the given image, never null
	 */
	@NotNull RasterImage<PixelType> drawLine(@NotNull RasterImage<PixelType> img, 
			double x1, double y1, double x2, double y2, @NotNull PixelType pixel);
}
