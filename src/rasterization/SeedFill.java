package rasterization;

import annotations.NotNull;
import rasterdata.RasterImage;

public interface SeedFill<PixelType> {
	@NotNull RasterImage<PixelType> fill(@NotNull RasterImage<PixelType> img, 
			int c, int r, 
			@NotNull PixelType areaValue, @NotNull PixelType newValue);
}
