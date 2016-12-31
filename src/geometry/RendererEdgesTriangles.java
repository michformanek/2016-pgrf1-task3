package geometry;

import annotations.NotNull;
import rasterdata.RasterImage;
import rasterization.LineRasterizer;
import transforms.Mat4;
import transforms.Point3D;
import transforms.Vec3D;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

public class RendererEdgesTriangles<VertexType, PixelType> implements Renderer<VertexType, Connectivity, PixelType> {

	private final @NotNull LineRasterizer<PixelType> liner;

	public RendererEdgesTriangles(LineRasterizer<PixelType> lineRasterizer) {
		this.liner = lineRasterizer;
	}

	@Override
	public @NotNull RasterImage<PixelType> render(
			final @NotNull Solid<VertexType, Connectivity> solid, 
			final @NotNull PixelType pixelValue,
			final @NotNull Mat4 finalMatrix,
			final @NotNull RasterImage<PixelType> img, 
			final @NotNull Function<VertexType, Point3D> toPoint3D) {
		return solid.getParts().stream()
			.reduce(img, 
					(final @NotNull RasterImage<PixelType> image, 
							final @NotNull Solid.Part<Connectivity> part) -> {
					switch (part.getConnectivityType()) {
					case EDGE_LIST: 
						return renderEdgeList(
								solid.getVertices(), solid.getIndices(),
								pixelValue,finalMatrix, image, toPoint3D,
								part.getStartIndex(), part.getPrimitiveCount());
					case TRIANGLE_LIST: 
						return renderTriangleList(
								solid.getVertices(), solid.getIndices(),
								pixelValue,finalMatrix, image, toPoint3D,
								part.getStartIndex(), part.getPrimitiveCount());
					}
					return image;
				},
				(lhs, rhs) -> rhs	// ignoruje se pri sekvencnim streamu
									// (pro parallelStream() a skutecne
									// immutable RasterImage by nefungovalo)
			);
	}
	public @NotNull RasterImage<PixelType> renderEdgeList(
			final @NotNull List<VertexType> vertices,
			final @NotNull List<Integer> indices,
			final @NotNull PixelType pixelValue,
			final @NotNull Mat4 finalMatrix,
			final @NotNull RasterImage<PixelType> img, 
			final @NotNull Function<VertexType, Point3D> toPoint3D,
			final int startIndex, final int primitiveCount) {
		return IntStream.iterate(startIndex, i -> i + 2)
				.limit(primitiveCount)
				.boxed()	// z IntStream delame Stream<Integer>,// abychom meli nasledujici funkci reduce...
				.reduce(img, 
					(final @NotNull RasterImage<PixelType> image, final Integer i) -> 
						renderEdge(
							toPoint3D.apply(vertices.get(indices.get(i))),
							toPoint3D.apply(vertices.get(indices.get(i + 1))),
							pixelValue, finalMatrix,image
						),
					(lhs, rhs) -> rhs // ignoruje se pri sekvencnim streamu
				);
	} 

	public @NotNull RasterImage<PixelType> renderTriangleList(
			final @NotNull List<VertexType> vertices,
			final @NotNull List<Integer> indices,
			final @NotNull PixelType pixelValue,
			final @NotNull Mat4 finalMatrix,
			final @NotNull RasterImage<PixelType> img, 
			final @NotNull Function<VertexType, Point3D> toPoint3D,
			final int startIndex, final int primitiveCount) {

			return IntStream.iterate(startIndex, i -> i + 3)
				.limit(primitiveCount)
				.boxed()
				.reduce(img,
						(final @NotNull RasterImage<PixelType> image, final Integer i) ->
								renderEdge(
										toPoint3D.apply(vertices.get(indices.get(i))),
										toPoint3D.apply(vertices.get(indices.get(i + 1))),
										pixelValue,finalMatrix, renderEdge(
												toPoint3D.apply(vertices.get(indices.get(i))),
												toPoint3D.apply(vertices.get(indices.get(i + 2))),
												pixelValue, finalMatrix, renderEdge(
														toPoint3D.apply(vertices.get(indices.get(i + 1))),
														toPoint3D.apply(vertices.get(indices.get(i + 2))),
														pixelValue, finalMatrix,image
												)
										)
								),
						(lhs, rhs) -> rhs
				);
	}

	private @NotNull RasterImage<PixelType> renderEdge(
			final @NotNull Point3D p1,
			final @NotNull Point3D p2,
			final @NotNull PixelType pixelValue,
			final @NotNull Mat4 finalMatrix,
			final @NotNull RasterImage<PixelType> img) {

			if (p1.getW() <= 0 || p2.getW() <= 0)
			return img;


		final Vec3D a = p1.mul(finalMatrix).dehomog().orElse(new Vec3D()); //invalid argument?
		final Vec3D b = p2.mul(finalMatrix).dehomog().orElse(new Vec3D());

		if (notInLimits(a) || notInLimits(b))
			return img;

		return liner.drawLine(img,a.getX(),a.getY(),b.getX(),b.getY(),pixelValue);
	}

	private boolean notInLimits(Vec3D vec3D) {
		return (vec3D.getX() < -1 || vec3D.getX() > 1) || (vec3D.getY() < -1 || vec3D.getY() > 1) || (vec3D.getZ() < -1 || vec3D.getZ() > 1);
	}
}
