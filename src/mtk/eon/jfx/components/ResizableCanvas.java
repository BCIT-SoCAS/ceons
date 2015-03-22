package mtk.eon.jfx.components;

import mtk.eon.drawing.*;
import mtk.eon.jfx.DrawingState;
import mtk.eon.jfx.controllers.MainWindowController;
import mtk.eon.utils.draw.DashedDrawing;
import mtk.eon.utils.draw.Rotation;
import mtk.eon.utils.draw.Zooming;
import mtk.eon.utils.geom.Vector2F;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class ResizableCanvas extends Canvas {
	private FigureControl list;
	private FigureControl listBeforeChanges;
	private boolean isDrawingLink;
	private DrawingState state;
	//private int scrollNumber = 0;
	private MainWindowController parent;
	private Vector2F startTempPoint;
	private Vector2F endTempPoint;
	private Rotation rotation;

	public ResizableCanvas() {
		list = new FigureControl(this);
		setOnMouseClicked((e)->canvasOnMouseClicked(e));
		setOnMouseDragged((e)->canvasOnMouseDragged(e));
		setOnMousePressed((e)->canvasOnMousePressed(e));
		setOnMouseReleased((e)->canvasOnMouseReleased(e));
		setOnScroll((e)->canvasOnMouseScroll(e));
		
	}

	private void canvasOnMousePressed(MouseEvent e) {
		System.out.println("Press");
		Vector2F pressedPoint = new Vector2F((float) e.getX(),(float) e.getY());
		if(isLinkAddingState())
		{
			addLink(pressedPoint);
		}
		else if (isLinkDeleteState() || isFewElementsDeleteState()
				|| isRotationAroundCenterChose()
				|| isRotationAroundNodeChose())
		{
			startTempPoint=pressedPoint;
			if (isRotationAroundNodeChose())
				{
					
					Figure activeRotateNode=list.get(list.findClosestNode(startTempPoint));
					Vector2F centerPoint =((Node)activeRotateNode).getStartPoint();
					rotation = new Rotation(centerPoint,listBeforeChanges);
				}
			else if(isRotationAroundCenterChose())
				{
					Vector2F centerPoint=new Vector2F((float)getHeight()/2,(float)getWidth()/2);
					rotation=new Rotation(centerPoint,listBeforeChanges);
				}
		}
		else if (isClickingState())
		{
				Figure temp = findClosestElement(pressedPoint);
				setSelectedFigure(temp);
				//Odkomentować w razie jak zostaną dodane Propertiesy
				//loadProperties(temp);
		}
	}

	private void canvasOnMouseScroll(ScrollEvent e) {
			Zooming zooming=new Zooming(listBeforeChanges);
			//Odkomentować w razie jak zostaną dodane Propertiesy
			//parent.loadProperties(null,list);
			if (e.getDeltaY() > 0)
			//	scrollNumber++;
				list=new FigureControl(zooming.zoom(true));
			else
				list=new FigureControl(zooming.zoom(false));
			//	scrollNumber--;
			list.redraw();
	}
	
	private void canvasOnMouseReleased(MouseEvent e)
	{
		Vector2F releasedPoint=new Vector2F((float) e.getX(), (float) e.getY());
		 if(isLinkAddingState())
		 {
		     list.changeLinkEndPointAfterDrag(releasedPoint);
		     setSelectedFigure(null);
	     }
		 else if(isRotationAroundCenterChose() || isRotationAroundNodeChose())
		 {
			 listBeforeChanges=new FigureControl(list);
		 }else if(isNodeDeleteState())
		 {
			 deleteNode(releasedPoint);
		 }
		 else if(isFewElementsDeleteState()){
			 list.deleteElementsFromRectangle(startTempPoint, endTempPoint);
		 }else if(isLinkDeleteState()) {
			 list.deleteLinks(startTempPoint, endTempPoint);
		 }	
		 updateListBeforeChanges();
	}
	
	 private void canvasOnMouseClicked(MouseEvent e)
	 {
		 Vector2F clickedPoint = new Vector2F((float) e.getX(), (float) e.getY());
		 if (isNodeAddingState()) {
			 setSelectedFigure(null);
			 addNode(clickedPoint);
		 } else if (isClickingState()) {
			 if (isDrawingLink)
				 isDrawingLink = false;
		 }
		 updateListBeforeChanges();
	 }

	private void canvasOnMouseDragged(MouseEvent e)
	{
		Vector2F draggedPoint = new Vector2F((float) e.getX(), (float) e.getY());
	    if(isLinkAddingState()){
	    	list.changeLastLinkEndPoint(draggedPoint);
	        isDrawingLink = true;
	    } else if (isClickingState()) {
	        if(list.getSelectedFigure() instanceof Node)
	    		list.changeNodePoint(list.getSelectedFigure(), draggedPoint); 
	        } else if (isLinkDeleteState()) {
	            endTempPoint = draggedPoint;
	            list.redraw();
	            DashedDrawing.drawDashedLine(getGraphicsContext2D(), startTempPoint, endTempPoint);
	        } else if (isFewElementsDeleteState()) {
	            endTempPoint = draggedPoint;
	            list.redraw();
	            DashedDrawing.drawDashedRectangle(getGraphicsContext2D(), startTempPoint, endTempPoint);
	        } else {
	            if (isRotationAroundCenterChose() || isRotationAroundNodeChose()) {
	                endTempPoint = draggedPoint;
	                list=rotation.rotate(startTempPoint, endTempPoint);
	                list.redraw();
	                startTempPoint=endTempPoint;
	            }
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
		listBeforeChanges=new FigureControl(list);
		//scrollNumber=0;
	}
	private void setState(DrawingState chosenState )
	{
		state=chosenState;
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

	private void addNode(Vector2F vec2F) {
		list.add(new Node(vec2F, list.getNodeAmmount()));
		System.out.println("NodeSizeAdd"+Node.imageSize);
		System.out.println("////////////////////////////////////////////////////////////////////////////////");
	}

	private void addLink(Vector2F vec2F) {
		list.add(new Link(vec2F, vec2F, list.getLinkAmmount()));
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

	private boolean isRotationAroundCenterChose() {
		return state == DrawingState.rotateAroundCenter;
	}

	private boolean isRotationAroundNodeChose() {
		return state == DrawingState.rotateAroundNode;
	}
	private void setSelectedFigure(Figure temp)
	{
	    list.setSelectedFigure(temp);
	}
	private void loadProperties(Figure temp)
	{
		parent.loadProperties(temp, list);
	}
	private void updateListBeforeChanges()
	{
		listBeforeChanges=new FigureControl(list);
		if(list.elementsAmmount()>0){
		Figure fig=list.get(list.elementsAmmount()-1);
		System.out.println("update"+((Node)fig).imageSize);
		}Zooming.clearScrollNumber();
	}
	
}
