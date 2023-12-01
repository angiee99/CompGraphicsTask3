package renderer;

import model.Line;
import model.Point;
import model3D.Object3D;
import model3D.Scene;
import rasterization.Raster;
import rasterops.rasterize.Liner;
import transforms.Mat4;
import transforms.Point3D;
import transforms.Vec2D;
import transforms.Vec3D;

import java.util.ArrayList;
import java.util.List;

public class Renderer3D {
    public void render(final Raster raster,
                       final Scene scene,
                       final Liner liner,
                       final int color,
                       final Mat4 viewMatrix,
                       final Mat4 projectMatrix){
        //projectMatrix could be either Orthogonal or Perspektivni

        //for each object 3d in scene
        //   transformation martix T = Model * View * Projection
        final Mat4 vp = viewMatrix.mul(projectMatrix);
        for (Object3D object: scene.getObjects()) {
            Mat4 T =  object.getModelMat().mul(vp);

            List<Point3D> trasformedPoints = new ArrayList<>();
            //   transformed points = p -> p * T
            for (Point3D point: object.getVertexBuffer()) {
                trasformedPoints.add(point.mul(T));
            }
            //   Lines l = from index buffer

            for(int i = 0; i < object.getIndexBuffer().size(); i += 2){
                int indexA =  object.getIndexBuffer().get(i);
                int indexB =  object.getIndexBuffer().get(i + 1);

                Point3D start =  object.getVertexBuffer().get(indexA);
                Point3D end =  object.getVertexBuffer().get(indexB);
                //   for each line
                //          clipLine() -> stays in list if 2 points in view (if at least one point z < 0)
                if(start.getZ() < 0 || end.getZ() < 0){
                    continue;
                }
                //          dehomog -> Point3D -> Vec3D
                start.dehomog().ifPresent(
                        p1 -> end.dehomog().ifPresent(
                            p2 -> {
                            //    projection to 2D -> Vec2D (ignore z)
                                Vec2D start2D = p1.ignoreZ();
                                Vec2D end2D = p2.ignoreZ();
                            //   transform to viewport
                                start2D  =  transformToViewport(start2D);
                                end2D = transformToViewport(end2D);
                            //   rasterize with given liner

                }));

            }


        }

    }
    private Vec2D transformToViewport(Vec2D v) {
        return v.mul(new Vec2D(1, -1)) // here -1 for y because we change the smer of Oy
                .add(new Vec2D(1, 1))
                .mul(new Vec2D((800 - 1) / 2., (600 - 1) / 2.)); //
        // /2. because we move <-1;1> -> <0; 2> and then this 2 is not needed
    }

}

