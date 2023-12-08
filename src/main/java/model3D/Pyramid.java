package model3D;

import transforms.Mat4;
import transforms.Point3D;

import java.util.List;

public class Pyramid extends Object3D{
    public Pyramid(Mat4 modelMat, int color) {

        super(
                List.of(
                new Point3D(-1, -1, -1),
                new Point3D(1, -1, -1),
                new Point3D(1, 1, -1),
                new Point3D(-1, 1, -1),
                new Point3D(0, 0, 1)),
                List.of(0, 1, 1, 2, 2, 3, 3, 0, 3, 4, 0, 4, 1, 4, 2, 4),
                modelMat, color);
    }
}
