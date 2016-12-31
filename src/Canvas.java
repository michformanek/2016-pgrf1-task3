import annotations.NotNull;
import annotations.Nullable;
import geometry.*;
import geometry.Renderer;
import rasterdata.Presentable;
import rasterdata.RasterImage;
import rasterdata.RasterImageBuf;
import rasterization.LineRasterizer;
import rasterization.LineRasterizerDDA;
import transforms.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimal GUI for drawing pixels
 * @author PGRF FIM UHK
 * @version 2016
 */

public class Canvas {

	private final @NotNull JFrame frame;
	private final @NotNull JPanel panel;
	private @NotNull RasterImage<Integer> img;
	private final @NotNull Presentable<Graphics> imagePresenter;
	private final @NotNull LineRasterizer<Integer> liner;
	private final @NotNull Solid cube;
	private final @NotNull Solid curve;
	private final @NotNull Solid tetrahedron;
	private final @NotNull Renderer<Point3D, Connectivity, Integer> renderer;
	private	@NotNull Camera camera;
	private final @NotNull List<Axis> axisList;
	private boolean perspective;
	private boolean drawCube;
	private int lastX,lastY;

	private Canvas(final int width, final int height) {
		axisList = new ArrayList<>();
		cube = new Cube();
		curve = new Curve();
		tetrahedron = new TetrahedronTriangle();
		liner = new LineRasterizerDDA<>();
		renderer = new RendererEdgesTriangles<>(liner);
		camera = new Camera(new Vec3D(50, 50,50), Math.PI / 2.0, 0, Math.PI / 4.0, true);

		lastX = -1;
		lastY = -1;


		axisList.add(new Axis(new Point3D(0,0,0),new Point3D(0,0,1)));
		axisList.add(new Axis(new Point3D(0,0,0),new Point3D(0,1,0)));
		axisList.add(new Axis(new Point3D(0,0,0),new Point3D(	1,0,0)));

		frame = new JFrame();
		frame.setTitle("UHK FIM PGRF : Canvas");
		frame.setResizable(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		final @NotNull RasterImageBuf<Integer> tempImage =
			new RasterImageBuf<>(width, height, BufferedImage.TYPE_INT_RGB,pixel -> pixel, pixel -> pixel);
		img = tempImage;
		imagePresenter = tempImage;

		panel = new JPanel();
		panel.setPreferredSize(new Dimension(width, height));

		panel.addMouseMotionListener(new Mouse());

		frame.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_A:
						camera = camera.left(1);
						break;
					case KeyEvent.VK_D:
						camera = camera.right(1);
						break;
					case KeyEvent.VK_W:
						camera = camera.forward(1);
						break;
					case KeyEvent.VK_S:
						camera = camera.backward(1);
						break;
					case KeyEvent.VK_Q:
						perspective = !perspective;
						break;
					case KeyEvent.VK_E:
						drawCube = !drawCube;
						break;
					default:
						break;
				}
				draw();
			}
		});

		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}

	private void clear() {
 		img = img.fill(0x2f2f2f);
	}

	private void present() {
		final @Nullable Graphics graphics = panel.getGraphics(); 
		if (graphics != null)
			imagePresenter.present(graphics);
	}

	private void draw() {
		clear();
		Mat4 cubeMatrix = new Mat4Scale(10,10,10).mul(new Mat4RotXYZ(Math.PI/3, Math.PI/8, Math.PI/5)).mul(new Mat4Transl(10,10,10));
		Mat4 tetrahedronMatrix = new Mat4Scale(5, 5, 5).mul(new Mat4Transl(25,25,2));
		Mat4 axisMatrix= new Mat4Scale(40, 40, 40);

		Mat4 viewMatrix = camera.getViewMatrix();
		Mat4 projectionMatrix = calculateProjectionMatrix();

		img = renderer.render(axisList.get(0),Color.red.getRGB(),calculateMatrix(axisMatrix,viewMatrix,projectionMatrix),img,point3D -> point3D);
		img = renderer.render(axisList.get(1),Color.green.getRGB(),calculateMatrix(axisMatrix,viewMatrix,projectionMatrix),img,point3D -> point3D);
		img = renderer.render(axisList.get(2), Color.blue.getRGB(),calculateMatrix(axisMatrix,viewMatrix,projectionMatrix),img, point3D -> point3D);

		img = renderer.render(cube,0xff00ff,calculateMatrix(cubeMatrix,viewMatrix,projectionMatrix),img, point -> point);
		img = renderer.render(curve,Color.cyan.getRGB(),calculateMatrix(cubeMatrix,viewMatrix,projectionMatrix),img, point -> point);
		img = renderer.render(tetrahedron, 0xababab,calculateMatrix(tetrahedronMatrix,viewMatrix,projectionMatrix),img,point3D -> point3D);

		present();
	}

	private Mat4 calculateProjectionMatrix() {
		Mat4 projectionMatrix;
		if (perspective) {
			projectionMatrix = new Mat4PerspRH(Math.PI / 3, (double) img.getHeight() / img.getWidth(), 0.1, 200);
		}else {
			projectionMatrix = new Mat4OrthoRH(200, 200, 0.1, 200);
		}
		return projectionMatrix;
	}

	private void start() {
		draw();
		present();
	}

	public static void main(String[] args) {
		final Canvas canvas = new Canvas(800, 600);
		SwingUtilities.invokeLater(() -> {
			SwingUtilities.invokeLater(() -> {
				SwingUtilities.invokeLater(() -> {
					SwingUtilities.invokeLater(canvas::start);
				});
			});
		});
	}

	private Mat4 calculateMatrix(final @NotNull Mat4 solidMatrix, final @NotNull Mat4 viewMatrix, final @NotNull Mat4 projectionMatrix){
		return solidMatrix.mul(viewMatrix).mul(projectionMatrix);
	}

	private class Mouse extends MouseAdapter {

		@Override
		public void mouseDragged(MouseEvent e){
				if (lastX == -1) {
					lastX = e.getX();
					lastY = e.getY();
				}

				if (e.getX() != lastX) {
					camera = camera.addAzimuth((lastX - e.getX()) * 0.01f);
					lastX = e.getX();
				}
				if (e.getY() != lastY) {
					camera = camera.addZenith((e.getY() - lastY) * 0.01f);

					if (camera.getZenith() > Math.PI / 2.0)
						camera = camera.addZenith(-(camera.getZenith() - Math.PI / 2.0));

					if (camera.getZenith() < -(Math.PI / 2.0))
						camera = camera.addZenith((Math.PI / 2.0 + camera.getZenith()));

					lastY = e.getY();
				}
		draw();
		}
	}
}