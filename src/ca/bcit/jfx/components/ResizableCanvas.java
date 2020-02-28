package ca.bcit.jfx.components;

import ca.bcit.Settings;
import ca.bcit.drawing.Figure;
import ca.bcit.drawing.FigureControl;
import ca.bcit.drawing.Link;
import ca.bcit.drawing.Node;
import ca.bcit.jfx.DrawingState;
import ca.bcit.jfx.controllers.MainWindowController;
import ca.bcit.net.NetworkNode;
import ca.bcit.utils.geom.Vector2F;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.Map;

public class ResizableCanvas extends Canvas {
    public FigureControl list;
    private boolean isDrawingLink;
    private DrawingState state;
    private static MainWindowController parent;
    double orgX = 0.0, orgY = 0.0, newX = 0.0, newY = 0.0;
    double clickedX, clickedY;
    ArrayList<Vector2F> nodePositions;

    public ResizableCanvas() {
        list = new FigureControl(this);
        nodePositions = new ArrayList<>();
        setOnMouseClicked(this::canvasOnMouseClicked);
        setOnMouseDragged(this::canvasOnMouseDragged);
        setOnMousePressed(this::canvasOnMousePressed);
        setOnMouseReleased(this::canvasOnMouseReleased);
    }

    public static MainWindowController getParentController() {
        return parent;
    }

    private void canvasOnMousePressed(MouseEvent e) {
        Vector2F pressedPoint = new Vector2F((float) e.getX(), (float) e.getY());
        if (isLinkAddingState())
            addLink(pressedPoint);
        else if (isClickingState() && !list.isEmpty()) {
            Figure closestElement = findClosestElement(pressedPoint);
            setSelectedFigure(closestElement);
            loadProperties(closestElement);
        }
        else if (isDraggingState()) {
            clickedX = e.getX();
            clickedY = e.getY();
            for (int i = 0 ; i < list.getNodeAmount() + list.getLinkAmount() ; i++) {
                Vector2F nodePosition = new Vector2F(list.get(i).getStartPoint().getX(), list.get(i).getStartPoint().getY());
                nodePositions.add(nodePosition);
            }
        }
    }

    private void canvasOnMouseClicked(MouseEvent e) {
        Vector2F clickedPoint = new Vector2F((float) e.getX(), (float) e.getY());
        if (isNodeAddingState()) {
            setSelectedFigure(null);
            addNode(clickedPoint);
        }
        else if (isClickingState()) {
            if (isDrawingLink)
                isDrawingLink = false;
        }
        else if (isNodeMarkReplicaState())
			markNode(clickedPoint, "replicas");
		else if (isNodeMarkInternationalState())
			markNode(clickedPoint, "international");
    }

    private void canvasOnMouseDragged(MouseEvent e) {
        if (isDraggingState()) {
            double moveX = e.getX() - clickedX;
            double moveY = e.getY() - clickedY;
            newX = orgX + moveX;
            newY = orgY + moveY;
            parent.map.getGraphicsContext2D().clearRect(0,0, parent.map.getWidth(), parent.map.getHeight());
            parent.map.getGraphicsContext2D().drawImage(parent.mapImage, newX, newY, parent.map.getWidth(), parent.map.getHeight());

            Settings.topLeftCornerXCoordinate = (float) -newX;
            Settings.topLeftCornerYCoordinate = (float) -newY;

            parent.graph.list.redraw();
        }
    }

    private void canvasOnMouseReleased(MouseEvent e) {
        if (isDraggingState()) {
            orgX = newX;
            orgY = newY;
            nodePositions.clear();
        }
    }

    public void init(MainWindowController parent) {
        this.parent = parent;
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    public void changeState(DrawingState chosenState) {
        setState(chosenState);
        setSelectedFigure(null);
    }

    private void setState(DrawingState chosenState) {
        state = chosenState;
	}

	private boolean isNodeMarkReplicaState() {
        return state == DrawingState.nodeMarkReplicaState;
	}
	
	private boolean isNodeMarkInternationalState() {
        return state == DrawingState.nodeMarkInternationalState;
	}
	
    private boolean isClickingState() {
        return state == DrawingState.clickingState;
    }

    private boolean isNodeAddingState() {
        return state == DrawingState.nodeAddingState;
    }

    private boolean isLinkAddingState() {
        return state == DrawingState.linkAddingState;
    }

    /**
     * Draw a Node on the given coordinates from the parameter vec2F
     *
     * @param vec2F the coordinates of Node
     */
    public void addNode(Vector2F vec2F) {
        list.add(new Node(vec2F, list.getNodeAmount()));
    }

    public void addNetworkNode(NetworkNode n) {
        list.add(n.getFigure());
    }

    /**
     * Clear all drawn figure on the canvas
     */
    public void resetCanvas() {
        list.deleteElementsFromRectangle(new Vector2F(0, 0), new Vector2F((float) getWidth(), (float) getHeight()));
    }

    private void addLink(Vector2F vec2F) {
        list.add(new Link(vec2F, vec2F, list.getLinkAmount()));
    }

    /**
     * Draw a Link between two Nodes given the coordinates of the Nodes
     *
     * @param startPoint coordinates of the first Node
     * @param endPoint coordinates of the second Node
     * @param freeSpacePercentage percentage of the slice being free
     */
    public void addLink(Vector2F startPoint, Vector2F endPoint, double freeSpacePercentage, int length) {
        list.add(new Link(startPoint, endPoint, list.getLinkAmount(), freeSpacePercentage, length));
    }

    private Figure findClosestElement(Vector2F vec2F) {
        return list.findClosestElement(vec2F);
    }

	private void markNode(Vector2F clickedPoint, String groupName) {
        list.markNode(clickedPoint, groupName);
	}
	
    private boolean isDraggingState() {
        return state == DrawingState.draggingState;
    }

    private void setSelectedFigure(Figure temp) {
        list.setSelectedFigure(temp);
    }

    /**
     * Load either a link or node
     * @param temp either a link or node
     *
     */
    private void loadProperties(Figure temp) {
        parent.loadProperties(temp, list);
    }
}

