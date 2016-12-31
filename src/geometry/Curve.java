package geometry;

import annotations.NotNull;
import transforms.Cubic;
import transforms.Point3D;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/**
 * Created by Michal Formanek on 31.12.16.
 */
public class Curve implements Solid<Point3D,Connectivity> {

    private final @NotNull List<Point3D> vertices;
    private final @NotNull List<Integer> indices;
    private final @NotNull List<Part<Connectivity>> parts;

    public Curve() {
        Cubic cubic = new Cubic(Cubic.BEZIER, new Point3D(0,0,0), new Point3D(3,0,3), new Point3D(1,1,1),
                new Point3D(1,1,1));

vertices = new ArrayList<>();
        for (double i = 0; i <= 1; i += 0.01) {
            vertices.add(cubic.compute(i));
        }
        vertices.add(cubic.compute(1.0));

        indices = new ArrayList<>();
        for (int i = 0; i < vertices.size() - 1; i++) {
            indices.add(i);
            indices.add(i + 1);
        }

        parts = new ArrayList<>();
        parts.add(new Part<Connectivity>(0, indices.size()/2 , Connectivity.EDGE_LIST));

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
