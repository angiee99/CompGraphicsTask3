package model3D;

import transforms.Mat4;
import transforms.Point3D;

import java.util.List;

public class Object3D {
    protected  List<Point3D> vertexBuffer;
    protected List<Integer> indexBuffer;
    protected Mat4 modelMat;
    protected final int color;
    protected boolean isHidden;

    public Object3D(List<Point3D> vertexBuffer, List<Integer> indexBuffer, Mat4 modelMat, int color) {
        this.vertexBuffer = vertexBuffer;
        this.indexBuffer = indexBuffer;
        this.modelMat = modelMat;
        this.color = color;
        this.isHidden = false;
    }

    public List<Point3D> getVertexBuffer() {
        return vertexBuffer;
    }

    public List<Integer> getIndexBuffer() {
        return indexBuffer;
    }

    public Mat4 getModelMat() {
        return modelMat;
    }

    public int getColor() {
        return color;
    }

    public void setModelMat(Mat4 modelMat) {
        this.modelMat = modelMat;
    }

    public void setVertexBuffer(List<Point3D> vertexBuffer) {
        this.vertexBuffer = vertexBuffer;
    }

    public void setIndexBuffer(List<Integer> indexBuffer) {
        this.indexBuffer = indexBuffer;
    }

    public void changeVisibility(){
        isHidden = !isHidden;
    }

    public boolean isHidden() {
        return isHidden;
    }
}
