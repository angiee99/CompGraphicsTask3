package model3D;

import transforms.Mat4;
import transforms.Point3D;

import java.util.List;
import java.util.stream.Stream;

public class Cube extends Object3D{

    public Cube(Mat4 modelMat, int color) {
        super(
                List.of(
                        new Point3D(-1, -1, -1),
                        new Point3D(1, -1, -1),
                        new Point3D(1, 1, -1),
                        new Point3D(-1, 1, -1),

                        new Point3D(-1, -1, 1),
                        new Point3D(1, -1, 1),
                        new Point3D(1, 1, 1),
                        new Point3D(-1, 1, 1)
                        )
                ,
                Stream.iterate(0, i -> i +1)
                        .limit(4)
                        .flatMap(i -> Stream.of(i, (i+1)%4, i, i +4, i+4, (i+1)%4 +4))
                        .toList(),
                modelMat, color);
    }
}
