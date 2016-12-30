package geometry;

import annotations.NotNull;
import transforms.Point3D;

import java.util.ArrayList;
import java.util.List;

/**
 * Tetrahedron represented by list of edges
 *
 * Created by Michal Formanek on 30.12.16.
 */
public class TetrahedronEdge implements Solid<Point3D, Connectivity> {

    private final @NotNull List<Point3D> vertices;
    private final @NotNull List<Integer> indices;
    private final @NotNull List<Solid.Part<Connectivity>> parts;

    public TetrahedronEdge() {
        vertices = new ArrayList<>();

        vertices.add(new Point3D(-1, -1, -1));
        vertices.add(new Point3D(1, -1, 1));
        vertices.add(new Point3D(-1, 1, 1));
        vertices.add(new Point3D(1, 1, -1));

        indices = new ArrayList<>();

        indices.add(0); //cyklem?
        indices.add(1);

        indices.add(0);
        indices.add(2);

        indices.add(0);
        indices.add(3);

        indices.add(1);
        indices.add(3);

        indices.add(1);
        indices.add(2);

        indices.add(2);
        indices.add(3);
        assert(indices.size() == 12);
        parts = new ArrayList<>();
        parts.add(new Part<>(0, 6, Connectivity.EDGE_LIST));
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
    public @NotNull List<Solid.Part<Connectivity>> getParts() {
        return parts;
    }


}
