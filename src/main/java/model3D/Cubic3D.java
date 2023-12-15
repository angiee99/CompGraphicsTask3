package model3D;

import transforms.Cubic;
import transforms.Mat4;
import transforms.Point3D;

import java.util.ArrayList;
import java.util.List;

public class Cubic3D extends Object3D{
    private Cubic cubic;
    private int smooth;
    public Cubic3D(int smooth, Mat4 cubicType, Point3D p1, Point3D p2,
                   Point3D p3, Point3D p4,
                   Mat4 modelMat, int color) {
        super( new ArrayList<>(),  new ArrayList<>(), modelMat, color);
        this.smooth = smooth;
        cubic = new Cubic(cubicType, p1, p2, p3, p4);
        computeVertices();
    }
    public void computeVertices(){
        List<Point3D> vertices = new ArrayList<>();
        List<Integer> indices=new ArrayList<>();

        for (int i = 0; i < smooth; i++) {
            vertices.add(cubic.compute((double) i / smooth));
            if (i != 0) {
                indices.add(i - 1);
                indices.add(i);
            }
        }

        this.setVertexBuffer(vertices);
        this.setIndexBuffer(indices);
    }

    public int getSmooth() {
        return smooth;
    }

    public void setSmooth(int smooth) {
        this.smooth = smooth % 10_000;
    }
}
