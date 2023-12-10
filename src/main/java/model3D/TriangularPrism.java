package model3D;

import transforms.Mat4;
import transforms.Point3D;

import java.util.List;

public class TriangularPrism extends  Object3D{
    public TriangularPrism(Mat4 modelMat, int color) {
        super(List.of(
                new Point3D(-1, -1, -1),
                new Point3D(1, -1, -1),
                new Point3D(1, 1, -1),


                new Point3D(-1, -1, 1),
                new Point3D(1, -1, 1),
                new Point3D(1, 1, 1)

                ),
                List.of(
                        0, 1, 1, 2, 2, 0,
                        // Top triangle
                        3, 4, 4, 5, 5, 3,
                        // Connecting edges
                        0, 3, 1, 4, 2, 5
                ),
                modelMat, color);
    }
}
