import model3D.Cube;
import model3D.Object3D;
import rasterization.RasterBI;
import rasterops.rasterize.LinerDDAII;
import renderer.WiredRenderer;
import solid.Solid;
import transforms.Mat4Identity;
import transforms.Mat4Transl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author PGRF FIM UHK
 * @version 2023.b
 */

public class Canvas3D {
    private JPanel panel;
    private RasterBI img;
    private LinerDDAII liner;
    private WiredRenderer wiredRenderer;

    private Object3D cube;
    private Color red, green, grey, yellow, purple;


    public Canvas3D(int width, int height) {
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setTitle("UHK FIM PGRF : " + this.getClass().getName());
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        img = new RasterBI(width, height);
        setupCanvas();


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

        clear();
        initScene();

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                if(e.getKeyCode() == KeyEvent.VK_LEFT)
//                    cube.setModel(cube.getModel().mul(new Mat4Transl(-0.2, 0, 0)));
                if(e.getKeyCode() == KeyEvent.VK_RIGHT)
//                    cube.setModel(cube.getModel().mul(new Mat4Transl(0.2, 0, 0)));

                drawScene();
            }
        });


    }

    /**
     *  initiates the scene and its objects
     */
    public void initScene() {
        cube = new Cube(new Mat4Identity(), red.getRGB() );
    }

    // could be like my change method
    public void drawScene() {
        clear();

//        wiredRenderer.render(cube);

        panel.repaint();
    }
    /**
     * initiates variables for string objects, colors, anchor points
     */
    private void setupCanvas(){
        liner = new LinerDDAII();
        wiredRenderer = new WiredRenderer(liner, img); //tODO change to my renderer

        red = new Color(255, 0, 0);
        green= new Color(0, 155, 20);
        grey = new Color(47, 47, 47);
        yellow = new Color(255, 255, 0);
        purple = new Color(235, 25, 230);
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
//    public void clear(int color) {
//        img.setClearColor(color);
//        img.clear();
//
//    }

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
