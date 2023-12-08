package model3D;

import transforms.Mat4;
import transforms.Mat4Identity;
import transforms.Point3D;

import java.util.List;

public class Axis extends Object3D{
    /**
     * creates a normalized axis
     * @param endPoint
     * @param color
     */
    public Axis(Point3D endPoint, int color) {
        super(List.of(new Point3D(0, 0, 0), endPoint),
              List.of(0, 1), new Mat4Identity(), color);
    }
}
