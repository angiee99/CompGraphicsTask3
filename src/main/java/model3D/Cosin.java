package model3D;

import transforms.Mat4;
import transforms.Point3D;

import java.util.ArrayList;
import java.util.List;

public class Cosin extends Object3D{
    public Cosin(double height, double period, int smooth, Mat4 modelMat, int color) {
        super(new ArrayList<>(), new ArrayList<>(), modelMat, color);
        computeVerticees(height, period, smooth);
    }

    private void computeVerticees(double height, double period, int smooth) {
        List<Point3D> vertices=new ArrayList<>();
        List<Integer> indices=new ArrayList<>();

        double step=(Math.PI*2/smooth);

        for (int i = 0; i < smooth; i++){
            double x = i*step-Math.PI;
            double z = height + Math.cos(x * period);
            vertices.add(new Point3D(x,0, z));
            if(i!= 0){
                indices.add(i - 1);
                indices.add(i);
            }
        }
        this.vertexBuffer = vertices;
        this.indexBuffer = indices;
    }
}
