import model3D.*;
import rasterization.RasterBI;
import rasterops.rasterize.LinerDDAII;
import renderer.Renderer3D;
import transforms.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * @author PGRF FIM UHK
 * @version 2023.b
 */

public class Canvas3D {
    private JPanel panel;
    private RasterBI img;
    private Renderer3D renderer;

    private Scene scene;
    private Object3D cube, pyramid, prism;
    private Object3D Ox, Oy, Oz;
    private int activeObj;
    private Vec3D viewPos;
    private Vec2D anchorPoint;
    private int x, y;
    private Camera cam;
    private Mat4 chosenProjection;
    private Mat4PerspRH perspectiveMatrix;
    private Mat4OrthoRH orthMatrix;

    private Cubic3D ferguson, bezier, coons;
    private Bicubic3D fergusonBicubic;
    private Cosin cosin;
    private Color red, green, blue, greenish, grey, yellow, purple, azure;

    public Canvas3D(int width, int height) {
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setTitle("UHK FIM PGRF : " + this.getClass().getName());
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        panel = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                present(g);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        panel.requestFocus();
        panel.requestFocusInWindow();

        img = new RasterBI(width, height);
        setupCanvas();
        clear();
        initScene();
        drawScene();

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                x = e.getX();
                y = e.getY();
            }
        });
        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                cam = cam.addAzimuth( (double) ( x - e.getX() )/200)
                         .addZenith( (double) ( y- e.getY())/200 );
                x = e.getX();
                y = e.getY();
                drawScene();
            }
        });
        panel.addMouseWheelListener(e -> {
            if(e.getWheelRotation() < 0) {// moved up
                cam = cam.backward(0.1);
            }
            else {
                cam = cam.forward(0.1);
            }
            drawScene();
        });
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                Object3D current = scene.getObjects().get(activeObj);
                //translations
                if(e.getKeyCode() == KeyEvent.VK_LEFT)
                    current.setModelMat(current.getModelMat().mul(new Mat4Transl(0.05, 0, 0)));
                if(e.getKeyCode() == KeyEvent.VK_RIGHT)
                    current.setModelMat(current.getModelMat().mul(new Mat4Transl(-0.05, 0, 0)));
                if(e.getKeyCode() == KeyEvent.VK_UP)
                    current.setModelMat(current.getModelMat().mul(new Mat4Transl(0, 0, 0.05)));
                if(e.getKeyCode() == KeyEvent.VK_DOWN)
                    current.setModelMat(current.getModelMat().mul(new Mat4Transl(0, 0, -0.05)));
                if(e.getKeyCode() ==KeyEvent.VK_M)
                    current.setModelMat(current.getModelMat().mul(new Mat4Transl(0, 0.05, 0)));
                if(e.getKeyCode() ==KeyEvent.VK_L)
                    current.setModelMat(current.getModelMat().mul(new Mat4Transl(0, -0.05, 0)));

                // rotations
                if(e.getKeyCode() == KeyEvent.VK_X ){
                    current.setModelMat(current.getModelMat().mul(new Mat4RotX(0.1)));
                }
                if(e.getKeyCode() == KeyEvent.VK_Y ){
                    current.setModelMat(current.getModelMat().mul(new Mat4RotY(0.1)));
                }
                if(e.getKeyCode() == KeyEvent.VK_Z ){
                    current.setModelMat(current.getModelMat().mul(new Mat4RotZ(0.1)));
                }

                // scale
                if(e.getKeyCode() == KeyEvent.VK_EQUALS ){
                    current.setModelMat(current.getModelMat().mul(new Mat4Scale(1.2)));
                }
                if(e.getKeyCode() == KeyEvent.VK_MINUS ){
                    current.setModelMat(current.getModelMat().mul(new Mat4Scale(0.8)));
                }

                // camera control
                if(e.getKeyCode() == KeyEvent.VK_A){
                    cam = cam.left(0.1);
                }
                if(e.getKeyCode() == KeyEvent.VK_D){
                    cam = cam.right(0.1);
                }
                if(e.getKeyCode() == KeyEvent.VK_W){
                    cam = cam.forward(0.1);
                }
                if(e.getKeyCode() == KeyEvent.VK_S){
                    cam = cam.backward(0.1);
                }

                //changing projection mode
                if(e.getKeyCode() == KeyEvent.VK_O){
                    chosenProjection = orthMatrix;
                }
                if(e.getKeyCode() == KeyEvent.VK_P){
                    chosenProjection = perspectiveMatrix;
                }

                // changing the active object3D
                if(e.getKeyCode() == KeyEvent.VK_PERIOD){
                    activeObj = (activeObj +1) % scene.getObjects().size();
                    if(activeObj <= 2 && activeObj >=0){
                        activeObj = 3;
                    }
                }

                // parametrizing the smoothness of cubic
                if(e.getKeyCode() == KeyEvent.VK_F && e.isAltDown()){
                    ferguson.setSmooth(ferguson.getSmooth() /2);
                    ferguson.computeVertices();
                }
                else if(e.getKeyCode() == KeyEvent.VK_F && e.isShiftDown()){
                    ferguson.setSmooth(ferguson.getSmooth() *2);
                    ferguson.computeVertices();
                }
                else if(e.getKeyCode() == KeyEvent.VK_F ){
                    ferguson.changeVisibility();
                }

                if(e.getKeyCode() == KeyEvent.VK_B && e.isAltDown()){
                    bezier.setSmooth(bezier.getSmooth() /2);
                    bezier.computeVertices();
                }
                else if(e.getKeyCode() == KeyEvent.VK_B && e.isShiftDown()){
                    bezier.setSmooth(bezier.getSmooth() *2);
                    bezier.computeVertices();
                }
                else if(e.getKeyCode() == KeyEvent.VK_B ){
                    bezier.changeVisibility();
                }

                if(e.getKeyCode() == KeyEvent.VK_N && e.isAltDown()){
                    coons.setSmooth(coons.getSmooth() /2);
                    coons.computeVertices();
                }
                else if(e.getKeyCode() == KeyEvent.VK_N && e.isShiftDown()){
                    coons.setSmooth(coons.getSmooth() *2);
                    coons.computeVertices();
                }
                else if(e.getKeyCode() == KeyEvent.VK_N ){
                    coons.changeVisibility();
                }

                // hiding/showing objects
                if(e.getKeyCode() == KeyEvent.VK_C ){
                    cube.changeVisibility();
                }
                if(e.getKeyCode() == KeyEvent.VK_G ){
                    pyramid.changeVisibility();
                }
                if(e.getKeyCode() == KeyEvent.VK_R ){
                    prism.changeVisibility();
                }

                if(e.getKeyCode() == KeyEvent.VK_H){
                    fergusonBicubic.changeVisibility();
                }
                if(e.getKeyCode() == KeyEvent.VK_T){
                    cosin.changeVisibility();
                }

                drawScene();
            }
        });
    }
    /**
     * initiates variables for helping objects, colors, anchor point
     */
    private void setupCanvas(){
        renderer = new Renderer3D();
        anchorPoint = new Vec2D(2, 4);
        x = img.getWidth()/2;
        y = img.getHeight()/2;

        red = new Color(255, 0, 0);
        green = new Color(0, 255, 0);
        blue = new Color(0, 0, 255);

        greenish = new Color(0, 155, 20);
        grey = new Color(47, 47, 47);
        yellow = new Color(255, 255, 0);
        purple = new Color(235, 25, 230);
        azure = new Color(0, 200, 215);
    }
    /**
     *  initiates the scene, camera, projection and otrhogonal matrices, and 3D objects
     */
    public void initScene() {
        viewPos = new Vec3D(2, 4, 3);

        cam = new Camera()
                .withPosition(viewPos)
                .withAzimuth(getAzimuthToOrigin(viewPos))
                .withZenith(getZenithToOrigin(viewPos));

        perspectiveMatrix = new Mat4PerspRH(Math.PI/3, 1, 0.1,200);
        orthMatrix = new Mat4OrthoRH(img.getWidth() / 40.0, img.getHeight() / 40.0, -200, 200); // mb change
        chosenProjection = perspectiveMatrix;

        cube = new Cube(new Mat4Identity(), greenish.getRGB());
        pyramid = new Pyramid(new Mat4Transl(0, 0, 0.5), purple.getRGB());
        prism = new TriangularPrism(new Mat4Transl(-2.5, 1, 0), yellow.getRGB());
        Ox = new Axis(new Point3D(2, 0, 0), red.getRGB());
        Oy = new Axis(new Point3D(0, 2, 0), green.getRGB());
        Oz = new Axis(new Point3D(0, 0, 2), blue.getRGB());

        ArrayList<Object3D> objects = new ArrayList<>();
        objects.add(Ox);
        objects.add(Oy);
        objects.add(Oz);
        objects.add(cube);
        objects.add(pyramid);
        objects.add(prism);
        activeObj = 3;

        ferguson = new Cubic3D(20, Cubic.FERGUSON,
                cube.getVertexBuffer().get(6),
                cube.getVertexBuffer().get(2),
                cube.getVertexBuffer().get(6),
                cube.getVertexBuffer().get(2),
                new Mat4Identity(),
                yellow.getRGB()
                );
        bezier = new Cubic3D(20, Cubic.BEZIER,
                cube.getVertexBuffer().get(0),
                cube.getVertexBuffer().get(5),
                cube.getVertexBuffer().get(6),
                cube.getVertexBuffer().get(2),
                new Mat4Identity(),
                azure.getRGB()
        );
        coons = new Cubic3D(20, Cubic.COONS,
                new Point3D(-1,-6,1),
                new Point3D(-0.5,-4,0),
                new Point3D(1,-4,1),
                new Point3D(2,-6,2),
                new Mat4Identity(),
                red.getRGB()
        );

        fergusonBicubic = new Bicubic3D(50, 50, Cubic.FERGUSON,
                getBicubicPoints(), new Mat4Transl(1, 0, 1), azure.getRGB());

        objects.add(ferguson);
        objects.add(bezier);
        objects.add(coons);
        objects.add(fergusonBicubic);

        cosin = new Cosin(1, 2, 200, new Mat4Identity(), azure.getRGB());
        objects.add(cosin);
        scene = new Scene(objects);
    }
    private Point3D[] getBicubicPoints(){
        Point3D[] list = new Point3D[16];
        list[0] = cube.getVertexBuffer().get(6);
        list[1] =  cube.getVertexBuffer().get(2);
        list[2] =  cube.getVertexBuffer().get(6);
        list[3] =  cube.getVertexBuffer().get(2);

        list[4] = cube.getVertexBuffer().get(6).mul(new Mat4Transl(0, 1, 0));
        list[5] =  cube.getVertexBuffer().get(2).mul(new Mat4Transl(0, 1, 0));
        list[6] =  cube.getVertexBuffer().get(6).mul(new Mat4Transl(0, 1, 0));
        list[7] =  cube.getVertexBuffer().get(2).mul(new Mat4Transl(0, 1, 0));

        list[8] = cube.getVertexBuffer().get(6).mul(new Mat4Transl(0, 1, 2));
        list[9] =  cube.getVertexBuffer().get(2).mul(new Mat4Transl(0, 1, 2));
        list[10] =  cube.getVertexBuffer().get(6).mul(new Mat4Transl(0, 1, 2));
        list[11] =  cube.getVertexBuffer().get(2).mul(new Mat4Transl(0, 1, 2));


        list[12] = cube.getVertexBuffer().get(6).mul(new Mat4Transl(1, 1, 0));
        list[13] =  cube.getVertexBuffer().get(2).mul(new Mat4Transl(1, 1, 0));
        list[14] =  cube.getVertexBuffer().get(6).mul(new Mat4Transl(1, 1, 0));
        list[15] =  cube.getVertexBuffer().get(2).mul(new Mat4Transl(1, 1, 0));

        return list;
    }

    public void drawScene() {
        clear();
        renderer.render(img, scene, new LinerDDAII(),
                cam.getViewMatrix(),
                chosenProjection);


        panel.repaint();
        img.present(panel.getGraphics());
    }


    private double getAzimuthToOrigin(final Vec3D observerPos){
        final  Vec3D v = observerPos.opposite();
        final double alpha = v.ignoreZ().normalized()
                .map(vNorm -> Math.acos(vNorm.dot(new Vec2D(1,0))))
                .orElse(0.0);
        return (v.getY() > 0) ? alpha : Math.PI*2- alpha;
    }

    private double getZenithToOrigin(final Vec3D observerPos){
        final  Vec3D v = observerPos.opposite();
        final double alpha = v.normalized()
                .map(vNorm-> Math.acos(vNorm.dot(new Vec3D(0,0,1))))
                .orElse(Math.PI/2);
        return Math.PI/2 - alpha;
    }
    /**
     * Clears the canvas and resets all the structures saved
     */
    public void clearCanvas() {
        clear();
        img.present(panel.getGraphics());
    }
    /**
     * Paints the canvas in its background color
     */
    public void clear() {
        img.clear(grey.getRGB());
    }


    public void present(Graphics graphics) {
        img.present(graphics);
    }


    public void start() {
        drawScene();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Canvas3D(800, 600).start());
    }

}
