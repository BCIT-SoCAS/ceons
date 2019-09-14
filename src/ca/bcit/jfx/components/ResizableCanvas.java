package ca.bcit.jfx.components;

import ca.bcit.drawing.Figure;
import ca.bcit.drawing.FigureControl;
import ca.bcit.drawing.Link;
import ca.bcit.drawing.Node;
import ca.bcit.jfx.DrawingState;
import ca.bcit.jfx.controllers.MainWindowController;
import ca.bcit.net.NetworkNode;
import ca.bcit.utils.draw.DashedDrawing;
import ca.bcit.utils.draw.Zooming;
import ca.bcit.utils.geom.Vector2F;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.util.ArrayList;
import java.util.Map;

public class ResizableCanvas extends Canvas {
    public FigureControl list;
    private FigureControl listBeforeChanges;
    private boolean isDrawingLink;
    private DrawingState state;
    private static MainWindowController parent;
    private Vector2F startTempPoint;
    private Vector2F endTempPoint;
    double orgX = 0.0, orgY = 0.0, newX = 0.0, newY = 0.0;
    double clickedX, clickedY, newClickedX, newClickedY;
    ArrayList<Vector2F> nodePositions;

    public ResizableCanvas() {
        list = new FigureControl(this);
        nodePositions = new ArrayList<Vector2F>();
        setOnMouseClicked(this::canvasOnMouseClicked);
        setOnMouseDragged(this::canvasOnMouseDragged);
        setOnMousePressed(this::canvasOnMousePressed);
        setOnMouseReleased(this::canvasOnMouseReleased);
    }

    public static MainWindowController getParentController() {
        return parent;
    }

    private void canvasOnMousePressed(MouseEvent e) {
        updateListBeforeChanges();
        Vector2F pressedPoint = new Vector2F((float) e.getX(), (float) e.getY());
        if (isLinkAddingState()) {
            addLink(pressedPoint);
        } else if (isLinkDeleteState() || isFewElementsDeleteState()) {
            startTempPoint = pressedPoint;
        } else if (isClickingState() && !list.isEmpty()) {
            Figure closestElement = findClosestElement(pressedPoint);
            setSelectedFigure(closestElement);
            loadProperties(closestElement);
        } else if (isDraggingState()) {
            clickedX = e.getX();
            clickedY = e.getY();
            for (int i = 0; i < list.getNodeAmount()+list.getLinkAmount(); i++) {
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
        } else if (isClickingState()) {
            if (isDrawingLink)
                isDrawingLink = false;
        } else if (isNodeMarkReplicaState()) {
			markNode(clickedPoint, "replicas");
		} else if (isNodeMarkInternationalState()) {
			markNode(clickedPoint, "international");
		}
        updateListBeforeChanges();
    }

    private void canvasOnMouseDragged(MouseEvent e) {
        Vector2F draggedPoint = new Vector2F((float) e.getX(), (float) e.getY());
        if (isDraggingState()) {
            double moveX = e.getX() - clickedX;
            double moveY = e.getY() - clickedY;
            newX = orgX + moveX;
            newY = orgY + moveY;
            parent.map.getGraphicsContext2D().clearRect(0,0, parent.map.getWidth(), parent.map.getHeight());
            parent.map.getGraphicsContext2D().drawImage(parent.mapImage, newX, newY, parent.map.getWidth()*parent.currentScale, parent.map.getHeight()*parent.currentScale);

            for (int i = 0; i < list.getNodeAmount()+list.getLinkAmount(); i++) {
                if (list.get(i) instanceof Node) {
                    double nodeX = nodePositions.get(i).getX();
                    double nodeY = nodePositions.get(i).getY();
                    double x = nodeX + moveX;
                    double y = nodeY + moveY;
                    list.changeNodePoint(list.get(i), new Vector2F((float)x,(float)y));
                }
            }
        }
    }

    private void canvasOnMouseReleased(MouseEvent e) {
        if (isDraggingState()) {
            orgX = newX;
            orgY = newY;
            nodePositions.clear();
        }
    }

    public void zoom(double oldScale, double newScale) {
        if (!list.isEmpty()) {
            updateListBeforeChanges();
            listBeforeChanges.setSelectedFigure(null);
            Zooming zooming=new Zooming(listBeforeChanges);
            boolean enlarge = newScale > oldScale;
            list=new FigureControl(zooming.zoom(newScale, enlarge));
            list.redraw();
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
        listBeforeChanges = new FigureControl(list);
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
	
	private boolean isNodeUnmarkState() {
        return state == DrawingState.nodeUnmarkState;
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

    /**
     * Draw a Node on the given coordinates from the parameter vec2F
     *
     * @param vec2F the coordinates of Node
     * @param name  name of the node
     */
    public void addNode(Vector2F vec2F, String name) {
        System.out.println(vec2F.getX());
        list.add(new Node(vec2F, name));
    }

    public void addNetworkNode(NetworkNode n) {
        list.add(n.getFigure());
    }

    /**
     * Draw a Node on the given coordinates from the parameter vec2F
     *
     * @param vec2F  the coordinates of Node
     * @param name   name of the node
     * @param Regens percentage regenerator remaining
     */
    public void addNode(Vector2F vec2F, String name, int Regens, Map nodeGroups) {
        list.add(new Node(vec2F, name, Regens, nodeGroups));
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
     * @param start_vec2F coordinates of the first Node
     * @param end_vec2F   coordinates of the second Node
     */
    public void addLink(Vector2F start_vec2F, Vector2F end_vec2F) {
        list.add(new Link(start_vec2F, end_vec2F, list.getLinkAmount()));
    }

    /**
     * Draw a Link between two Nodes given the coordinates of the Nodes
     *
     * @param start_vec2F coordinates of the first Node
     * @param end_vec2F   coordinates of the second Node
     * @param Percentage  percentage of the slice being free
     */
    public void addLink(Vector2F start_vec2F, Vector2F end_vec2F, int Percentage, int length) {
        list.add(new Link(start_vec2F, end_vec2F, list.getLinkAmount(), Percentage, length));
    }

    private Figure findClosestElement(Vector2F vec2F) {
        return list.findClosestElement(vec2F);
    }

    private boolean isNodeDeleteState() {
        return state == DrawingState.nodeDeleteState;
	}
	
	private void markNode(Vector2F clickedPoint, String groupName) {
        list.markNode(clickedPoint, groupName);
	}
	
	private void unmarkNode(Vector2F clickedPoint) {
        list.unmarkNode(clickedPoint);
    }

    private boolean isLinkDeleteState() {
        return state == DrawingState.linkDeleteState;
    }

    private void deleteNode(Vector2F clickedPoint) {
        list.deleteNode(clickedPoint);
    }

    private boolean isFewElementsDeleteState() {
        return state == DrawingState.fewElementsDeleteState;
    }

    private boolean isDraggingState() {
        return state == DrawingState.draggingState;
    }

    private void setSelectedFigure(Figure temp) {
        list.setSelectedFigure(temp);
    }

    /**
     * Load either a link or node
     * @param temp        either a link or node
     *
     */
    private void loadProperties(Figure temp) {
        parent.loadProperties(temp, list);
    }


    private void updateListBeforeChanges() {
        listBeforeChanges = new FigureControl(list);
        Zooming.clearFactory();
    }
}
