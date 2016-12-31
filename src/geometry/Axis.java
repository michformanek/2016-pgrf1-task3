package geometry;

import annotations.NotNull;
import transforms.Point3D;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michal Formanek on 30.12.16.
 */
public class Axis implements Solid<Point3D, Connectivity>{
    private final @NotNull List<Point3D> vertices;
    private final @NotNull List<Integer> indices;
    private final @NotNull List<Part<Connectivity>> parts;

    public Axis(Point3D a, Point3D b) {
        vertices = new ArrayList<>();
        vertices.add(a);
        vertices.add(b);

        indices = new ArrayList<>();
        indices.add(0);
        indices.add(1);

        parts = new ArrayList<>();
        parts.add(new Part<>(0, 1, Connectivity.EDGE_LIST));

    }

    @Override
    public List<Point3D> getVertices() {
        return vertices;
    }

    @Override
    public List<Integer> getIndices() {
        return indices;
    }

    @Override
    public List<Part<Connectivity>> getParts() {
        return parts;
    }
}
