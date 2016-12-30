package geometry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import annotations.NotNull;
import transforms.Point3D;

public class Cube implements Solid<Point3D, Connectivity> {
	private final @NotNull List<Point3D> vertices;
	private final @NotNull List<Integer> indices;
	private final @NotNull List<Part<Connectivity>> parts;
	
	public Cube() {
		vertices = new ArrayList<>();
		vertices.add(new Point3D(0,0,0));
		vertices.add(new Point3D(1,0,0));
		vertices.add(new Point3D(1,1,0));
		vertices.add(new Point3D(0,1,0));
		vertices.add(new Point3D(0,0,1));
		vertices.add(new Point3D(1,0,1));
		vertices.add(new Point3D(1,1,1));
		vertices.add(new Point3D(0,1,1));
		
		indices = new ArrayList<>();
//		for (int i = 0; i < 4; i++) {
		IntStream.rangeClosed(0, 3).forEach((final int i) -> {
			indices.add(i); indices.add((i + 1) % 4);
			indices.add(i); indices.add(i + 4);
			indices.add(i + 4); indices.add((i + 1) % 4 + 4);
		});
		assert(indices.size() == 24);
		parts = new ArrayList<>();
		parts.add(new Part<Connectivity>(0, 12, Connectivity.EDGE_LIST));
	}

	@Override
	public @NotNull List<Point3D> getVertices() {
		return vertices;
	}

	@Override
	public @NotNull List<Integer> getIndices() {
		return indices;
	}

	@Override
	public @NotNull List<Part<Connectivity>> getParts() {
		return parts;
	}

}
