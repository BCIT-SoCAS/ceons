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
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class ResizableCanvas extends Canvas {
    private FigureControl list;
    private FigureControl listBeforeChanges;
    private boolean isDrawingLink;
    private DrawingState state;
    private static MainWindowController parent;
    private Vector2F startTempPoint;
    private Vector2F endTempPoint;

    public ResizableCanvas() {
        list = new FigureControl(this);
        setOnMouseClicked(this::canvasOnMouseClicked);
        setOnMouseDragged(this::canvasOnMouseDragged);
        setOnMousePressed(this::canvasOnMousePressed);
        setOnMouseReleased(this::canvasOnMouseReleased);
        setOnScroll(this::canvasOnMouseScroll);
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
            Figure temp = findClosestElement(pressedPoint);
            setSelectedFigure(temp);
            //Odkomentować w razie jak zostaną dodane Propertiesy
            loadProperties(temp);
        }
    }

    private void canvasOnMouseScroll(ScrollEvent e) {
        if (!list.isEmpty()) {
            listBeforeChanges.setSelectedFigure(null);
            Zooming zooming = new Zooming(listBeforeChanges);
            //Odkomentować w razie jak zostaną dodane Propertiesy
            //parent.loadProperties(null,list);
            if (e.getDeltaY() > 0)
                list = new FigureControl(zooming.zoom(true));
            else
                list = new FigureControl(zooming.zoom(false));
            list.redraw();
        }
    }

    private void canvasOnMouseReleased(MouseEvent e) {
        Vector2F releasedPoint = new Vector2F((float) e.getX(), (float) e.getY());
        if (isLinkAddingState()) {
            list.changeLinkEndPointAfterDrag(releasedPoint);
            setSelectedFigure(null);
        } else if (isNodeDeleteState()) {
            deleteNode(releasedPoint);
        } else if (isFewElementsDeleteState()) {
            list.deleteElementsFromRectangle(startTempPoint, endTempPoint);
        } else if (isLinkDeleteState()) {
            list.deleteLinks(startTempPoint, endTempPoint);
        }
        updateListBeforeChanges();
    }

    private void canvasOnMouseClicked(MouseEvent e) {
        Vector2F clickedPoint = new Vector2F((float) e.getX(), (float) e.getY());
        if (isNodeAddingState()) {
            setSelectedFigure(null);
            addNode(clickedPoint);
        } else if (isClickingState()) {
            System.out.println("hi");

            if (isDrawingLink)
                isDrawingLink = false;
        }
        updateListBeforeChanges();
    }

    private void canvasOnMouseDragged(MouseEvent e) {
        Vector2F draggedPoint = new Vector2F((float) e.getX(), (float) e.getY());
        if (isLinkAddingState()) {
            list.changeLastLinkEndPoint(draggedPoint);
            isDrawingLink = true;
        } else if (isClickingState()) {
            if (list.getSelectedFigure() instanceof Node)
                list.changeNodePoint(list.getSelectedFigure(), draggedPoint);
        } else if (isLinkDeleteState()) {
            endTempPoint = draggedPoint;
            list.redraw();
            DashedDrawing.drawDashedLine(getGraphicsContext2D(), startTempPoint, endTempPoint);
        } else if (isFewElementsDeleteState()) {
            endTempPoint = draggedPoint;
            list.redraw();
            DashedDrawing.drawDashedRectangle(getGraphicsContext2D(), startTempPoint, endTempPoint);
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
    public void addNode(Vector2F vec2F, String name, int Regens) {
        list.add(new Node(vec2F, name, Regens));
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
    public void addLink(Vector2F start_vec2F, Vector2F end_vec2F, int Percentage) {
        list.add(new Link(start_vec2F, end_vec2F, list.getLinkAmount(), Percentage));
    }

    private Figure findClosestElement(Vector2F vec2F) {
        return list.findClosestElement(vec2F);
    }

    private boolean isNodeDeleteState() {
        return state == DrawingState.nodeDeleteState;
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

    private void setSelectedFigure(Figure temp) {
        list.setSelectedFigure(temp);
    }

    @SuppressWarnings("unused")

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
