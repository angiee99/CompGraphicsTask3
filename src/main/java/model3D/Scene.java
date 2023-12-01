package model3D;

import java.util.List;

public class Scene {
    private final List<Object3D> objects;

    public Scene(List<Object3D> objects) {
        this.objects = objects;
    }

    public List<Object3D> getObjects() {
        return objects;
    }
}
