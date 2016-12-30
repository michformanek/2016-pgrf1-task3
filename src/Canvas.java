import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.swing.*;

import annotations.NotNull;
import annotations.Nullable;
import geometry.*;
import geometry.Renderer;
import rasterdata.Presentable;
import rasterdata.RasterImage;
import rasterdata.RasterImageBuf;
import rasterization.LineRasterizer;
import rasterization.LineRasterizerDDA;
import rasterization.SeedFill;
import rasterization.SeedFill4In;
import transforms.Camera;
import transforms.Point3D;

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
	private final @NotNull Cube cube;
	private final @NotNull Renderer<Point3D, Connectivity, Integer> renderer;
	private final @NotNull Camera camera;
	private final @NotNull List<Axis> axisList;

	private Canvas(final int width, final int height) {
		axisList = new ArrayList<>();
		cube = new Cube();
		liner = new LineRasterizerDDA<>();
		renderer = new RendererEdgesTriangles<>(liner);
		camera = new Camera();

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
		
		final @NotNull MouseAdapter mouseAdapter = new MouseAdapter() {
			private int xStart, yStart;
			@Override
			public void mousePressed(MouseEvent e) {
				xStart = e.getX();
				yStart = e.getY();
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					final @NotNull SeedFill<Integer> seedFiller = 
							new SeedFill4In<>();
					final @NotNull Optional<Integer> pixel = 
							img.getPixel(e.getX(), e.getY());
					pixel.ifPresent(pix -> img = seedFiller.fill(img, e.getX(), e.getY(), pix, 0xffff00));
					present();
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				final double x1 = 2.0 * xStart / (panel.getWidth() - 1) - 1;
				final double y1 = 1 - 2.0 * yStart / (panel.getHeight() - 1);
				final double x2 = 2.0 * e.getX() / (panel.getWidth() - 1) - 1;
				final double y2 = 1 - 2.0 * e.getY() / (panel.getHeight() - 1);
				img = liner.drawLine(img, x1, y1, x2, y2, 0xff00ff);
				present();
			}
		};
		
		panel.addMouseListener(mouseAdapter);
		panel.addMouseMotionListener(mouseAdapter);

		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}

	private void clear(final int color) {
		/*
		final Graphics gr = img.getGraphics();
		gr.setColor(new Color(color));
		gr.fillRect(0, 0, img.getWidth(), img.getHeight());
		*/
	}

	private void present() {
		final @Nullable Graphics graphics = panel.getGraphics(); 
		if (graphics != null)
			imagePresenter.present(graphics);
	}

	private void draw() {
		clear(0x2f2f2f);
		img = img.withPixel(10, 10, 0xffff00);
		img = liner.drawLine(img, -0.9, 0.9, 0.9, 0.8, 0xff00ff);
		img = renderer.render(cube,0xff00ff,img, point -> point);

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

}