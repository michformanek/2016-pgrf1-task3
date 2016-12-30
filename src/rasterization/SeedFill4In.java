package rasterization;

import java.util.Optional;

import annotations.NotNull;
import rasterdata.RasterImage;

public class SeedFill4In<PixelType> implements SeedFill<PixelType> {
	@Override
	public @NotNull RasterImage<PixelType> fill(
			final @NotNull RasterImage<PixelType> img, final int c, final int r, 
			final @NotNull PixelType areaValue,
			final @NotNull PixelType newValue) {
		return new Object() {
			@NotNull RasterImage<PixelType> fill(
					final @NotNull RasterImage<PixelType> img, final int c, final int r) {
				return img.getPixel(c, r)
						.flatMap((final @NotNull PixelType actualValue) -> {
							if (actualValue.equals(areaValue)) {
								return Optional.of(
									fill(
										fill(
											fill(
												fill(
													img.withPixel(c, r, newValue), 
													c, r - 1), 
												c + 1, r), 
											c, r + 1),
										c - 1, r)
									);
							}
							return Optional.empty();
						})
						.orElse(img);
			}
		}.fill(img, c, r);
	}

}
