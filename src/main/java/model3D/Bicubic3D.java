package model3D;

import transforms.Bicubic;
import transforms.Cubic;
import transforms.Mat4;
import transforms.Point3D;

import java.util.ArrayList;
import java.util.List;

public class Bicubic3D extends Object3D{
    private Bicubic bicubic;
    public Bicubic3D(int smoothU, int smoothV, Mat4 cubicType, Point3D[] points, Mat4 modelMat, int color) {
        super(new ArrayList<>(), new ArrayList<>(), modelMat, color);
        bicubic = new Bicubic(cubicType, points);
        computeVertices(smoothU, smoothV);
    }

    private void computeVertices(int smoothU, int smoothV) {

        List<Point3D> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        int k = 0;
        for (int i = 0; i < smoothU; i++) {
            for (int j = 0; j < smoothV; j++) {
                vertices.add(bicubic.compute((double) i / smoothU, (double) j / smoothV));
                if (k != 0 ) {
                    indices.add(k - 1);
                    indices.add(k);
                }
                k++;
            }
        }

        this.setVertexBuffer(vertices);
        this.setIndexBuffer(indices);
    }
}
