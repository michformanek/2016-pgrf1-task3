package rasterization;

import annotations.NotNull;
import rasterdata.RasterImage;

public class LineRasterizerTrivial<PixelType> implements LineRasterizer<PixelType> {

	@Override
	public @NotNull RasterImage<PixelType> drawLine(final @NotNull RasterImage<PixelType> img, 
			final double x1, final double y1, final double x2, final double y2,
			final @NotNull PixelType pixel) {
		RasterImage<PixelType> result = img; // no final, non functional approach, yuck
		final double c1 = (img.getWidth() - 1) * 0.5 * (x1 + 1); 
		final double r1 = (img.getHeight() - 1) * 0.5 * (1 - y1); 
		final double c2 = (img.getWidth() - 1) * 0.5 * (x2 + 1); 
		final double r2 = (img.getHeight() - 1) * 0.5 * (1 - y2);
		final double k = (r2 - r1) / (c2 - c1);
		final double q = r1 - k * c1;
		for (int c = (int) c1; c <= c2; c++) { // non functional approach, yuck
			result = result.withPixel(c, (int) (k * c + q), pixel); // yuck
		}
		return result;
	}

}
