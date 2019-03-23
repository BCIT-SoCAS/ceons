package ca.bcit.drawing;

import ca.bcit.utils.draw.DashedDrawing;
import ca.bcit.utils.geom.Vector2F;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for (drawing) Nodes on the map
 */
public class Node extends Figure {
	
	private static final LinearGradient nodeFill = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,	new Stop[] {new Stop(0, Color.MAGENTA), new Stop(0.5f, Color.PURPLE)});
	
	public static float imageSize = 45;
	private int Regens = 100;
	private Map<String, Boolean> nodeGroups = new HashMap<>();

	public Node(Node node) {
		startPoint = node.startPoint.clone();
		name = node.name;
		loadImage();
	}


	/**
	 * Old method to draw nodes by index number instead of node name.
	 * @param startPoint 		the coordinates of Node
	 * @param number			index number of the Node
	 */
	public Node(Vector2F startPoint, int number) {

		super(new Vector2F(startPoint.getX() - imageSize / 2, startPoint.getY()
				- imageSize / 2), "Node" + number);
		loadImage();
	}

	/**
	 * Older method to draw non-updating nodes.
	 * The color used will be the same as the last node drawn.
	 * Default color is green.
	 * @param startPoint 		the coordinates of Node
	 * @param _name				name of the node passed in
	 */
	public Node(Vector2F startPoint, String _name) {
		super(new Vector2F(startPoint.getX() - imageSize / 2, startPoint.getY() - imageSize / 2), _name);
		loadImage();
	}

	/**
	 * Draw a Node on the given coordinates, the color will be based on the Regens percentage passed in
	 *
	 * @param startPoint		the coordinates of Node
	 * @param _name				name of the node passed in
	 * @param Regens			the percentage of free regenerators of the Node
	 * @param nodeGroups        the type of the node in question
	 */
	public Node(Vector2F startPoint, String _name, int Regens, Map nodeGroups) {
		super(new Vector2F(startPoint.getX() - imageSize / 2, startPoint.getY() - imageSize / 2), _name);
		this.Regens = Regens;
		this.nodeGroups = nodeGroups;
		loadImage();
	}

	/**
	 * The number returned is a percentage, used only for coloring the node
	 * @return the percentage currently stored in this node.
	 */
	public int getRegens() {
		return this.Regens;
	}

	/**
	 * Getter Method for nodeGroups variable
	 */
	public Map getNodeGroups() {
		return this.nodeGroups;
	}

	/**
	 * Setter Method for nodeGroups variable
	 * @param groupName
	 * @param value
	 */
	public void setNodeGroup(String groupName, Boolean value) {
		this.nodeGroups.put(groupName, value);
	}

    @Override
    public int getInfo() {
        return this.getRegens();
    }

	/**
	 * Setter Method to change the max number of regenerators for the specific Node
	 * @param regens number of regenerators for the particular node
	 */
    public void setRegens(int regens) {
		this.Regens = regens;
	}

	/**
	 * Method to draw the specific node on the map, with the correct color.
	 * @param gc GraphicsContext object with methods to draw with
	 */
	@Override
	public void draw(GraphicsContext gc) {
		int[] rgb = getColor();

		// node outline
		Boolean isReplica = this.nodeGroups.get("replicas");
		Boolean isInternational = this.nodeGroups.get("international");
		if (Boolean.TRUE.equals(isReplica) && Boolean.TRUE.equals(isInternational)) {
			gc.setFill(Color.web("#448ef6"));
			gc.fillOval(startPoint.getX() - imageSize / 8f, startPoint.getY() - imageSize / 8f, imageSize + imageSize / 4f, imageSize + imageSize / 4f);
			gc.setFill(Color.GRAY);
			gc.fillOval(startPoint.getX() - imageSize / 16f, startPoint.getY() - imageSize / 16f, imageSize + imageSize / 8f, imageSize + imageSize / 8f);
		} else if (Boolean.TRUE.equals(isReplica)) {
			gc.setFill(Color.GRAY);
			gc.fillOval(startPoint.getX() - imageSize / 16f, startPoint.getY() - imageSize / 16f, imageSize + imageSize / 8f, imageSize + imageSize / 8f);
		} else if (Boolean.TRUE.equals(isInternational)) {
			gc.setFill(Color.web("#448ef6"));
			gc.fillOval(startPoint.getX() - imageSize / 16f, startPoint.getY() - imageSize / 16f, imageSize + imageSize / 8f, imageSize + imageSize / 8f);
		} else {
			gc.setFill(Color.WHITE);
			gc.fillOval(startPoint.getX() - imageSize / 16f, startPoint.getY() - imageSize / 16f, imageSize + imageSize / 8f, imageSize + imageSize / 8f);
		}
		
		gc.setFill(Color.web("rgb(" + rgb[0] + ',' + rgb[1] + ',' + rgb[2] + ')'));
		gc.fillOval(startPoint.getX(), startPoint.getY(), imageSize, imageSize);

		// node center color
		gc.setFill(Color.WHITE);
		gc.fillOval(startPoint.getX() + imageSize / 8f, startPoint.getY() + imageSize / 8f, imageSize - imageSize / 4f, imageSize - imageSize / 4f);

		float fill = 0;
		gc.setFill(Color.hsb(120.0 + fill * 180, 0.5 + 0.5 * fill, 1  - 0.5 * fill));
		gc.fillOval(getCenterPoint().getX() - imageSize * (3f / 8f) * fill, getCenterPoint().getY() - imageSize * (3f  / 8f) * fill, imageSize * (6f / 8f) * fill, imageSize * (6f / 8f) * fill);

		// node name
		gc.setFill(Color.BLACK);
		gc.fillText(this.getName(), getCenterPoint().getX() - imageSize / 2f , getCenterPoint().getY());
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

	/**
	 * Private method used in drawing Nodes
	 * @return the coordinate of the center pixel of the Node
	 */
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
