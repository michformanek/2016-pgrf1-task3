package geometry;

import java.util.function.Function;

import annotations.NotNull;
import rasterdata.RasterImage;
import transforms.Mat4;
import transforms.Point3D;

public interface Renderer<VertexType, ConnectivityType, PixelType> {
	@NotNull RasterImage<PixelType> render(
			@NotNull Solid<VertexType, ConnectivityType> solid, 
			@NotNull PixelType pixelValue,
			@NotNull Mat4 finalMatrix,
			@NotNull RasterImage<PixelType> img,
			@NotNull Function<VertexType, Point3D> toPoint3D);
}
