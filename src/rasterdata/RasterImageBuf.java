package rasterdata;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.function.Function;

import annotations.NotNull;

public class RasterImageBuf<PixelType> implements RasterImage<PixelType>, 
		Presentable<Graphics> {
	private final @NotNull BufferedImage img;
	private final @NotNull Function<Integer, PixelType> toPixelType;
	private final @NotNull Function<PixelType, Integer> fromPixelType;
	
	public RasterImageBuf(final int width, final int height, 
			final int imageType, 
			final @NotNull Function<Integer, PixelType> toPixelType,
			final @NotNull Function<PixelType, Integer> fromPixelType) {
		img = new BufferedImage(width, height, imageType);
		this.toPixelType = toPixelType;
		this.fromPixelType = fromPixelType;
	}
	
	@Override
	public @NotNull Optional<PixelType> getPixel(final int c, final int r) {
		if (c < 0 || r < 0 || c >= getWidth() || r >= getHeight())
			return Optional.empty();
		return Optional.of(toPixelType.apply(img.getRGB(c, r)));
	}

	@Override
	public RasterImage<PixelType> withPixel(final int c, final int r, final @NotNull PixelType pixel) {
		if(!(c < 0 || r < 0 || c >= getWidth() || r >= getHeight())) {
			img.setRGB(c, r, fromPixelType.apply(pixel));
		}
		return this;
	}

	@Override
	public int getHeight() {
		return img.getHeight();
	}

	@Override
	public int getWidth() {
		return img.getWidth();
	}

	@Override
	public @NotNull Graphics present(final @NotNull Graphics device) {
		device.drawImage(img, 0, 0, null);
		return device;
	}

}
