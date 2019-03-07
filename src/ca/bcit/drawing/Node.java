package ca.bcit.drawing;

import ca.bcit.utils.draw.DashedDrawing;
import ca.bcit.utils.geom.Vector2F;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

public class Node extends Figure {
	
	private static final LinearGradient nodeFill = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,	new Stop[] {new Stop(0, Color.MAGENTA), new Stop(0.5f, Color.PURPLE)});
	
	public static float imageSize = 45;
	private int Regens = 100;

	public Node(Node node) {
		startPoint = node.startPoint.clone();
		name = node.name;
		loadImage();
	}

	public Node(Vector2F startPoint, int number) {

		super(new Vector2F(startPoint.getX() - imageSize / 2, startPoint.getY()
				- imageSize / 2), "Node" + number);
		loadImage();
	}

	public Node(Vector2F startPoint, String _name) {
		super(new Vector2F(startPoint.getX() - imageSize / 2, startPoint.getY() - imageSize / 2), _name);
		loadImage();
	}

	/**
	 * Draw a Node on the given coordinates
	 *
	 * @param startPoint		the coordinates of Node
	 * @param Regens			the percentage of free regenerators of the Node
	 */
	public Node(Vector2F startPoint, String _name, int Regens) {
		super(new Vector2F(startPoint.getX() - imageSize / 2, startPoint.getY() - imageSize / 2), _name);
		this.Regens = Regens;
		loadImage();
	}

	public int getRegens() {
		return this.Regens;
	}

	public void setRegens(int regens) {
		this.Regens = regens;
		String tempStr = "Node Figure: " + this.getName() + ", Free Regenerators: " + this.getRegens();
		System.out.println(tempStr);
	}

	@Override
	public void draw(GraphicsContext gc) {
		int[] rgb = getColor();

		gc.setFill(Color.WHITE);
		gc.fillOval(startPoint.getX() - imageSize / 16f, startPoint.getY() - imageSize / 16f, imageSize + imageSize / 8f, imageSize + imageSize / 8f);
		gc.setFill(Color.web("rgb(" + rgb[0] + ',' + rgb[1] + ',' + rgb[2] + ')'));
		gc.fillOval(startPoint.getX(), startPoint.getY(), imageSize, imageSize);
		gc.setFill(Color.WHITE);
		gc.fillOval(startPoint.getX() + imageSize / 8f, startPoint.getY() + imageSize / 8f, imageSize - imageSize / 4f, imageSize - imageSize / 4f);
		float fill = 0;
		gc.setFill(Color.hsb(120.0 + fill * 180, 0.5 + 0.5 * fill, 1  - 0.5 * fill));
		gc.fillOval(getCenterPoint().getX() - imageSize * (3f / 8f) * fill, getCenterPoint().getY() - imageSize * (3f  / 8f) * fill, imageSize * (6f / 8f) * fill, imageSize * (6f / 8f) * fill);
	}

	/**
	 * Get the correct RGB gradient for the node based on regenerators available
	 *
	 */
	private int[] getColor() {
		int[] rgb = new int[3];
		if (this.Regens > 50) {
			rgb[0] = (50 - (this.Regens - 50)) * 5;
			rgb[1] = 255;
		} else {
			rgb[0] = 255;
			rgb[1] = this.Regens * 5;
		}
		return rgb;
	}

	@Override
	public boolean equals(Object obj) {
		Node temp = (Node) obj;
		return (temp.getStartPoint().equals(startPoint) && temp.name
				.equals((name)));
	}

	protected void loadImage() {}

	private Vector2F getCenterPoint() {
		float x = startPoint.getX() + imageSize / 2;
		float y = startPoint.getY() + imageSize / 2;
		return new Vector2F(x, y);
	}

	@Override
	protected void drawOutline(GraphicsContext gc, Color color) {
		DashedDrawing.drawDashedCircle(gc, new Vector2F(startPoint.getX()
				+ imageSize / 2, startPoint.getY() + imageSize / 2),
				imageSize / 2 + 3, Color.GRAY);
	}

	public static void changeNodeSize(float newNodeSize) {
		imageSize = newNodeSize;
		if (imageSize < 12)
			imageSize = 12;
		else if (imageSize > 64)
			imageSize = 64;

	}

	public static float getStartNodeSize() {
		return 64;
	}

	public static float getNodeSize() {
		return imageSize;
	}

	@Override
	protected double calculateDistanceFromPoint(Vector2F p) {
		return getCenterPoint().distance(p);
	}
}
