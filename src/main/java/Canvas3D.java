import model3D.Cube;
import model3D.Object3D;
import model3D.Scene;
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
    private LinerDDAII liner; // make a local variable in drawscene
    private Renderer3D renderer;

    private Scene scene;
    private Object3D cube;

    private Vec3D viewPos;
    private Vec2D anchorPoint;
    private int x, y;
    private Camera cam;
    private Mat4PerspRH projectionMatrix;
    private Mat4OrthoRH orthMatrix;
    private Color red, green, grey, yellow, purple;


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

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                //TODO test
                if(e.getWheelRotation() < 0) {// moved up
                    cam = cam.forward(0.1);
                }
                else {
                    cam = cam.backward(0.1);
                }
            }
        });
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_LEFT)
                    cube.setModelMat(cube.getModelMat().mul(new Mat4Transl(0.05, 0, 0)));
                if(e.getKeyCode() == KeyEvent.VK_RIGHT)
                    cube.setModelMat(cube.getModelMat().mul(new Mat4Transl(-0.05, 0, 0)));
                if(e.getKeyCode() == KeyEvent.VK_UP)
                    cube.setModelMat(cube.getModelMat().mul(new Mat4Transl(0, 0, 0.05)));
                if(e.getKeyCode() == KeyEvent.VK_DOWN)
                    cube.setModelMat(cube.getModelMat().mul(new Mat4Transl(0, 0, -0.05)));
                if(e.getKeyCode() ==KeyEvent.VK_C)
                    cube.setModelMat(cube.getModelMat().mul(new Mat4Transl(0, 0.05, 0)));
                if(e.getKeyCode() ==KeyEvent.VK_F)
                    cube.setModelMat(cube.getModelMat().mul(new Mat4Transl(0, -0.05, 0)));

                // rotations
                if(e.getKeyCode() == KeyEvent.VK_X ){
                    cube.setModelMat(cube.getModelMat().mul(new Mat4RotX(0.1)));
                }
                if(e.getKeyCode() == KeyEvent.VK_Y ){
                    cube.setModelMat(cube.getModelMat().mul(new Mat4RotY(0.1)));
                }
                if(e.getKeyCode() == KeyEvent.VK_Z ){
                    cube.setModelMat(cube.getModelMat().mul(new Mat4RotZ(0.1)));
                }

                if(e.getKeyCode() == KeyEvent.VK_A){
                    cam = cam.right(0.1);
                }
                if(e.getKeyCode() == KeyEvent.VK_D){
                    cam = cam.left(0.1);
                }
                if(e.getKeyCode() == KeyEvent.VK_W){
                    cam = cam.forward(0.1);
                }
                if(e.getKeyCode() == KeyEvent.VK_S){
                    cam = cam.backward(0.1);
                }


                drawScene();
            }
        });
    }
    /**
     * initiates variables for helping objects, colors, anchor point
     */
    private void setupCanvas(){
        liner = new LinerDDAII();
        renderer = new Renderer3D();
        anchorPoint = new Vec2D(2, 4);
        x = img.getWidth()/2;
        y = img.getHeight()/2;

        red = new Color(255, 0, 0);
        green= new Color(0, 155, 20);
        grey = new Color(47, 47, 47);
        yellow = new Color(255, 255, 0);
        purple = new Color(235, 25, 230);
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
        projectionMatrix = new Mat4PerspRH(Math.PI/3, 1, 0.1,200);
        orthMatrix = new Mat4OrthoRH(img.getWidth() / 40.0, img.getHeight() / 40.0, -200, 200);
        
        cube = new Cube(new Mat4Identity(), green.getRGB());
        ArrayList<Object3D> objects = new ArrayList<>();
        objects.add(cube);
        scene = new Scene(objects);
    }

    // could be like my change method
    public void change(Runnable change){
        clear();
        change.run();
        drawScene();
    }

    public void drawScene() {
        clear();
        renderer.render(img, scene, liner,
                cam.getViewMatrix(),
                projectionMatrix);

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
