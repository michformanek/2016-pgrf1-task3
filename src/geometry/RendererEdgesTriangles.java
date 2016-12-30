package geometry;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import annotations.NotNull;
import rasterdata.RasterImage;
import rasterization.LineRasterizer;
import transforms.Point3D;
import transforms.Vec3D;

public class RendererEdgesTriangles<VertexType, PixelType> implements Renderer<VertexType, Connectivity, PixelType> {

	private final @NotNull LineRasterizer<PixelType> liner;

	public RendererEdgesTriangles(LineRasterizer<PixelType> lineRasterizer) {
		this.liner = lineRasterizer;
	}

	@Override
	public @NotNull RasterImage<PixelType> render(
			final @NotNull Solid<VertexType, Connectivity> solid, 
			final @NotNull PixelType pixelValue,
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
								pixelValue, image, toPoint3D,
								part.getStartIndex(), part.getPrimitiveCount());
					case TRIANGLE_LIST: 
						return renderTriangleList(
								solid.getVertices(), solid.getIndices(),
								pixelValue, image, toPoint3D,
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
			final @NotNull RasterImage<PixelType> img, 
			final @NotNull Function<VertexType, Point3D> toPoint3D,
			final int startIndex, final int primitiveCount) {
		return IntStream.iterate(startIndex, i -> i + 2)
				.limit(primitiveCount)
				.boxed()	// z IntStream delame Stream<Integer>,// abychom meli nasledujici funkci reduce... TODO: Je tam na to udělátko ;)
				.reduce(img, 
					(final @NotNull RasterImage<PixelType> image, final Integer i) -> 
						renderEdge(
							toPoint3D.apply(vertices.get(indices.get(i))),
							toPoint3D.apply(vertices.get(indices.get(i + 1))),
							pixelValue, image
						),
					(lhs, rhs) -> rhs // ignoruje se pri sekvencnim streamu
				);
	} 

	public @NotNull RasterImage<PixelType> renderTriangleList(
			final @NotNull List<VertexType> vertices,
			final @NotNull List<Integer> indices,
			final @NotNull PixelType pixelValue,
			final @NotNull RasterImage<PixelType> img, 
			final @NotNull Function<VertexType, Point3D> toPoint3D,
			final int startIndex, final int primitiveCount) {
		// zde doplnit prochazeni po trech vrcholech - 3x volat renderEdge
		// renderEdge(..., pixelValue, renderEdge(..., pixelValue, renderEdge(..., pixelValue, image)))

		//TODO
		return img;
	}

	private @NotNull RasterImage<PixelType> renderEdge(
			final @NotNull Point3D p1,
			final @NotNull Point3D p2,
			final @NotNull PixelType pixelValue,
			final @NotNull RasterImage<PixelType> img) {
		// zde doplnit orezani w, dehomogenizaci, viewport transformaci a vykresleni Linerem

		// 4D - 3D -> dehomogenizace
		final Vec3D a = p1.dehomog().orElse(new Vec3D()); //Asi by to chtelo nejakou rozumnou vychozi hodnotu, nebo vyhodit vyjimku -> invalid argument?
		final Vec3D b = p2.dehomog().orElse(new Vec3D());


		return liner.drawLine(img,a.getX(),a.getY(),b.getX(),b.getY(),pixelValue);
	} 
}
