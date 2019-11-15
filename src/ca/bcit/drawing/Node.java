package ca.bcit.drawing;

import ca.bcit.Settings;
import ca.bcit.utils.draw.DashedDrawing;
import ca.bcit.utils.geom.Vector2F;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class Node extends Figure {
	public static float imageSize = 20;
	private int numberOfRegenerators = 100;
	private Map<String, Boolean> nodeGroups = new HashMap<>();

	public Node(Node node) {
		startPoint = node.startPoint.clone();
		name = node.name;
	}

	/**
	 * @deprecated Old method to draw nodes by index number instead of node name.
	 * @param startPoint 		the coordinates of Node
	 * @param number			index number of the Node
	 */
	@Deprecated
	public Node(Vector2F startPoint, int number) {
		super(new Vector2F(startPoint.getX() - getNodeSize() / 2, startPoint.getY() - getNodeSize() / 2), "Node" + number);
	}

	/**
	 * @deprecated Older method to draw non-updating nodes.
	 * The color used will be the same as the last node drawn.
	 * Default color is green.
	 * @param startPoint 		the coordinates of Node
	 * @param name				name of the node passed in
	 */
	@Deprecated
	public Node(Vector2F startPoint, String name) {
		super(new Vector2F(startPoint.getX() - getNodeSize() / 2, startPoint.getY() - getNodeSize() / 2), name);
	}

	/**
	 * Draw a Node on the given coordinates, the color will be based on the Regens percentage passed in
	 *
	 * @param startPoint		the coordinates of Node
	 * @param name				name of the node passed in
	 * @param numberOfRegenerators			the percentage of free regenerators of the Node
	 * @param nodeGroups        the type of the node in question
	 */
	public Node(Vector2F startPoint, String name, int numberOfRegenerators, Map nodeGroups) {
		super(new Vector2F(startPoint.getX() - getNodeSize() / 2, startPoint.getY() - getNodeSize() / 2), name);
		this.numberOfRegenerators = numberOfRegenerators;
		this.nodeGroups = nodeGroups;
	}

	/**
	 * The number returned is a percentage, used only for coloring the node
	 * @return the percentage currently stored in this node.
	 */
	public int getNumberOfRegenerators() {
		return this.numberOfRegenerators;
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
        return this.getNumberOfRegenerators();
    }

	/**
	 * Setter Method to change the max number of regenerators for the specific Node
	 * @param numberOfRegenerators number of regenerators for the particular node
	 */
    public void setNumberOfRegenerators(int numberOfRegenerators) {
		this.numberOfRegenerators = numberOfRegenerators;
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
		float x = startPoint.getX() - Settings.topLeftCornerXCoordinate + getNodeSize()/2 * (Settings.zoomLevel - (float) Settings.ZOOM_MIN_LEVEL);
		float y = startPoint.getY() - Settings.topLeftCornerYCoordinate + getNodeSize()/2 * (Settings.zoomLevel - (float) Settings.ZOOM_MIN_LEVEL);
		if (Boolean.TRUE.equals(isReplica) && Boolean.TRUE.equals(isInternational)) {
			gc.setFill(Color.web("#448ef6"));
			gc.fillOval(x - getNodeSize() / 8f, y - getNodeSize() / 8f, getNodeSize() + getNodeSize() / 4f, getNodeSize() + getNodeSize() / 4f);
			gc.setFill(Color.GRAY);
			gc.fillOval(x - getNodeSize() / 16f, y - getNodeSize() / 16f, getNodeSize() + getNodeSize() / 8f, getNodeSize() + getNodeSize() / 8f);
		}
		else if (Boolean.TRUE.equals(isReplica)) {
			gc.setFill(Color.GRAY);
			gc.fillOval(x - getNodeSize() / 16f, y - getNodeSize() / 16f, getNodeSize() + getNodeSize() / 8f, getNodeSize() + getNodeSize() / 8f);
		}
		else if (Boolean.TRUE.equals(isInternational)) {
			gc.setFill(Color.web("#448ef6"));
			gc.fillOval(x - getNodeSize() / 16f, y - getNodeSize() / 16f, getNodeSize() + getNodeSize() / 8f, getNodeSize() + getNodeSize() / 8f);
		}
		else {
			gc.setFill(Color.WHITE);
			gc.fillOval(x - getNodeSize() / 16f, y - getNodeSize() / 16f, getNodeSize() + getNodeSize() / 8f, getNodeSize() + getNodeSize() / 8f);
		}
		
		gc.setFill(Color.web("rgb(" + rgb[0] + ',' + rgb[1] + ',' + rgb[2] + ')'));
		gc.fillOval(x, y, getNodeSize(), getNodeSize());

		// node center color
		gc.setFill(Color.WHITE);
		gc.fillOval(x + getNodeSize() / 8f, y + getNodeSize() / 8f, getNodeSize() - getNodeSize() / 4f, getNodeSize() - getNodeSize() / 4f);

		float fill = 0;
		gc.setFill(Color.hsb(120.0 + fill * 180, 0.5 + 0.5 * fill, 1  - 0.5 * fill));
		gc.fillOval(getCenterPoint().getX() - getNodeSize() * (3f / 8f) * fill, getCenterPoint().getY() - getNodeSize() * (3f  / 8f) * fill, getNodeSize() * (6f / 8f) * fill, getNodeSize() * (6f / 8f) * fill);
	}
	

	/**
	 * Get the correct RGB gradient for the node based on regenerators available
	 *
	 */
	private int[] getColor() {
		int[] rgb = new int[3];
		if (this.numberOfRegenerators > 50) {
			rgb[0] = (50 - (this.numberOfRegenerators - 50)) * 5;
			rgb[1] = 255;
		}
		else {
			rgb[0] = 255;
			rgb[1] = this.numberOfRegenerators * 5;
		}
		return rgb;
	}

	@Override
	public boolean equals(Object obj) {
		Node temp = (Node) obj;
		return (temp.getStartPoint().equals(startPoint) && temp.name.equals((name)));
	}

	/**
	 * Private method used in drawing Nodes
	 * @return the coordinate of the center pixel of the Node
	 */
	private Vector2F getCenterPoint() {
		float x = startPoint.getX() + getNodeSize() / 2;
		float y = startPoint.getY() + getNodeSize() / 2;
		return new Vector2F(x, y);
	}

	@Override
	protected void drawOutline(GraphicsContext gc, Color color) {
		DashedDrawing.drawDashedCircle(gc, new Vector2F(startPoint.getX() + getNodeSize() / 2, startPoint.getY() + getNodeSize() / 2), getNodeSize() / 2 + 3, Color.GRAY);
	}

	public static float getNodeSize() {
		return imageSize / Settings.zoomLevel;
	}

	@Override
	protected double calculateDistanceFromPoint(Vector2F p) {
		return getCenterPoint().distance(p);
	}
}
