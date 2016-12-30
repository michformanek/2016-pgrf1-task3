package rasterization;

import annotations.NotNull;
import rasterdata.RasterImage;

/**
 * Created by Michal Formanek on 18.11.16.
 */
public class LineRasterizerDDA<PixelType> implements LineRasterizer<PixelType> {

    @Override
    public RasterImage<PixelType> drawLine(@NotNull final RasterImage<PixelType> img, final double x1, final double y1, final double x2, final double y2, @NotNull final PixelType pixel) {
        RasterImage<PixelType> result = img;
        final double c1 = (img.getWidth() - 1) * 0.5 * (x1 + 1);
        final double r1 = (img.getHeight() - 1) * 0.5 * (1 - y1);
        final double c2 = (img.getWidth() - 1) * 0.5 * (x2 + 1);
        final double r2 = (img.getHeight() - 1) * 0.5 * (1 - y2);

        int pixelCount = (int) (Math.max(Math.abs(c2 - c1), Math.abs(r2 - r1)) + 1);
        double dx, dy;
        if (Math.abs(c2 - c1) > Math.abs(r2 - r1)) {
            dx = Math.signum(c2 - c1);
            dy = dx * (r2 - r1) / (c2 - c1);

        } else {
            dy = Math.signum(r2 - r1);
            dx = dy * (c2 - c1) / (r2 - r1);
        }
        double x = c1, y = r1;
        for (int i = 0; i < pixelCount; i++) {
            result = result.withPixel((int) x, (int) y, pixel);
            x += dx;
            y += dy;
        }

        return result;
    }
}
