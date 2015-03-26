package mtk.eon.drawing;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import mtk.eon.utils.geom.LineSegment;
import mtk.eon.utils.geom.Rectangle;
import mtk.eon.utils.geom.Vector2F;

public class FigureControl {
	private ArrayList<Figure> list = new ArrayList<Figure>();
	private int nodeAmmount;
	private int linkAmmount;
	private Canvas canvas;
	Figure selectedFigure;

	public FigureControl(Canvas _canvas) {
		canvas = _canvas;
		nodeAmmount = 0;
		linkAmmount = 0;
	}

	public ArrayList<Figure> getList() {
		return list;
	}

	public FigureControl(FigureControl temp) {
		for (Figure fig : temp.list) {
			if (fig instanceof Node) {
				list.add(new Node((Node) fig));
			} else if (fig instanceof Link) {
				list.add(new Link((Link) fig));
			}
		}
		nodeAmmount = temp.nodeAmmount;
		linkAmmount = temp.linkAmmount;
		canvas = temp.canvas;
		selectedFigure = temp.selectedFigure;
	}

	public void add(Figure temp) {

		if (temp instanceof Node) {
			list.add(temp);
			nodeAmmount++;
		} else {
			if (temp instanceof Link && isEnoughNodesForAddLink()) {
				int closestNodeId=findClosestNode(temp.getStartPoint());
				Figure closestNode=list.get(closestNodeId);
				Vector2F p = closestNode.getStartPoint();
				p = fixLinkPoint(p);
				temp.setStartPoint(p);
				list.add(0, temp);
				linkAmmount++;
			}
		}
		redraw();
	}

	private Vector2F fixLinkPoint(Vector2F p) {
		return new Vector2F(p.getX() + Node.imageSize / 2, p.getY()
				+ Node.imageSize / 2);
	}

	private Vector2F fixNodePoint(Vector2F p) {
		return new Vector2F(p.getX() - Node.imageSize / 2, p.getY()
				- Node.imageSize / 2);
	}

	public int findClosestNode(Vector2F temp) {
		if(!isEmpty()){
			//gdyby bylo zero to by obliczył odleglosc od linka,gdyz linki sa jako pierwsze na liscie
			int actualClosestNode = findFirstNode();
			double closestDistance = calculateDistance(actualClosestNode, temp);
			for (int i = actualClosestNode; i < list.size(); i++) {
				if (list.get(i) instanceof Node) {
					double actualDistance = calculateDistance(i, temp);
					if (actualDistance < closestDistance) {
						closestDistance = actualDistance;
						actualClosestNode = i;
					}
				}
			}
			return actualClosestNode;
		}
		return -1;
	}

	private int findFirstNode() {
		for(int i=0;i<list.size();i++)
		{
			if(list.get(i) instanceof Node)
			{
				return i;
			}
		}
		return -1;
	}

	private double calculateDistance(int idx, Vector2F p) {
		Figure temp = list.get(idx);
		return temp.calculateDistanceFromPoint(p);
	}

	public void redraw() {
		clearCanvas();
		for (Figure fig : list) {
			fig.draw(canvas.getGraphicsContext2D());
		}
		if (selectedFigure != null) {
			selectedFigure.drawOutline(canvas.getGraphicsContext2D());
		}
	}

	public void remove(Figure temp) {
		list.remove(temp);
		redraw();
	}

	public void changeLastLinkEndPoint(Vector2F p) {
		if (isEnoughNodesForAddLink()) {
			((Link) list.get(0)).setEndPoint(p);
			redraw();
		}
	}

	public void changeLinkEndPointAfterDrag(Vector2F p) {
		if (isEnoughNodesForAddLink()) {
			Vector2F temp = list.get(findClosestNode(p)).getStartPoint();
			temp = fixLinkPoint(temp);
			Link link = ((Link) list.get(0));
			link.setEndPoint(temp);
			if (temp.equals(link.startPoint) || isLinkAlreadyExist()) {
				list.remove(0);
				linkAmmount--;
			}
			redraw();
		}
	}

	public void clearCanvas() {
		canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(),
				canvas.getHeight());
	}

	public void clear() {
		clearCanvas();
		list.clear();
		nodeAmmount = 0;
		linkAmmount = 0;
	}

	public int getNodeAmmount() {
		return nodeAmmount;
	}

	public Figure findClosestElement(Vector2F p) {
		double closestDistance = list.get(0).calculateDistanceFromPoint(p);
		int closestElement = 0;
		for (int i = 1; i < list.size(); i++) {
			double actualDistance = list.get(i).calculateDistanceFromPoint(p);
			if (closestDistance > actualDistance) {
				closestElement = i;
				closestDistance = actualDistance;
			}
		}
		if (closestDistance > Node.imageSize)
			return null;
		return list.get(closestElement);
	}

	public ObservableList<String> generateNodeConnections(Figure temp) {
		ObservableList<String> data = FXCollections.observableArrayList();
		Vector2F fixedPoint = fixLinkPoint(temp.getStartPoint());
		for (Figure fig : list) {
			if (fig instanceof Link) {
				Vector2F linkStartPoint=fig.getStartPoint();
				Vector2F linkEndPoint=((Link)fig).getEndPoint();				
				if (linkStartPoint.equals(fixedPoint)) {
					Figure n = findNodeAtPoint(linkEndPoint);
					if (n != null)
						data.add(n.getName());
				} else if (linkEndPoint.equals(fixedPoint)) {
					Figure n = findNodeAtPoint(linkStartPoint);
					if (n != null)
						data.add(n.getName());
				}
			}
		}
		return data;
	}

	public Figure findNodeAtPoint(Vector2F p) {
		Vector2F fixedPoint = fixNodePoint(p);
		for (Figure fig : list) {
			if (fig.getStartPoint().equals(fixedPoint))
				return fig;
		}
		return null;
	}

	public int getLinkAmmount() {
		return linkAmmount;
	}

	public Figure findFigureByName(String name) {
		for (Figure fig : list) {
			if (fig.equalsByName(name))
				return fig;
		}
		return null;
	}

	public boolean containsFigureWithName(String name) {
		if (findFigureByName(name) != null)
			return true;
		return false;
	}

	public void changeNodePoint(Figure node, Vector2F vec2f) {
		vec2f = fitPointToCanva(vec2f);
		vec2f=fixNodePoint(vec2f);
		changeLinksPoint(node, vec2f);
		node.setStartPoint(vec2f);
		redraw();
	}

	private void changeLinksPoint(Figure node, Vector2F vec2f) {
		if (isEnoughNodesForAddLink()) {
			Vector2F fixedOldPoint = fixLinkPoint(node.getStartPoint());
			Vector2F fixedNewPoint = fixLinkPoint(vec2f);
			for (Figure fig : list) {
				if (fig instanceof Link) {
					if (fig.getStartPoint().distance(fixedOldPoint)<=1)
						fig.setStartPoint(fixedNewPoint);
					else if (((Link) fig).getEndPoint().distance(fixedOldPoint)<=1)
						((Link) fig).setEndPoint(fixedNewPoint);
				}
			}
			redraw();
		}
	}

	private boolean isLinkAlreadyExist() {
		if (isEnoughNodesForAddLink()) {
			Link link = (Link) list.get(0);
			Vector2F startPoint=link.getStartPoint();
			Vector2F endPoint=link.getEndPoint();
			for (int i = 1; i < list.size(); i++) {
				Figure fig = list.get(i);
				if (fig instanceof Link)
				{
					Link tempLink=(Link)fig;
					Vector2F startTempPoint=tempLink.getStartPoint();
					Vector2F endTempPoint=tempLink.getEndPoint();
					if((startTempPoint.distance(startPoint)<=1&&endTempPoint.distance(endPoint)<=1) ||(startTempPoint.distance(endPoint)<=1 &&endTempPoint.distance(startPoint)<=1))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	public Figure get(int idx) {
		return list.get(idx);
	}

	private Vector2F fitPointToCanva(Vector2F vec2F) {
		float x = vec2F.getX();
		float y = vec2F.getY();
		if (x > canvas.getWidth())
			x = (float) canvas.getWidth() - Node.imageSize;
		else if (x <= 0)
			x = Node.imageSize / 4;
		if (y > canvas.getHeight())
			y = (float) canvas.getHeight() - Node.imageSize;
		else if (y <= 0)
			y = Node.imageSize / 4;
		return new Vector2F(x, y);
	}

	public void setSelectedFigure(Figure _selectedFigure) {
		selectedFigure = _selectedFigure;	
		redraw();
	}
	public Figure getSelectedFigure() {
		return selectedFigure;
	}
	public ObservableList<String> getNodeList() {
		ObservableList<String> data = FXCollections.observableArrayList();
		for (Figure fig : list) {
			if (fig instanceof Node)
				data.add(fig.getName());
		}
		return data;
	}
	public void deleteLinks(Vector2F startPoint, Vector2F endPoint) {
		LineSegment line=new LineSegment(startPoint,endPoint);
		for (int i = 0; i < list.size(); i++) {
			Figure fig = list.get(i);
			if (fig instanceof Link) {
				if (line.areCrossing((Link)fig)) {
					list.remove(i);
					linkAmmount--;
					i--;
				}
			}
		}
		redraw();
	}

	public void deleteNode(Vector2F clickedPoint) {
		if (!isEmpty()) {
			int temp = (findClosestNode(clickedPoint));
			if (calculateDistance(temp, clickedPoint) < Node.imageSize/2) 
				{
					Figure fig=list.get(temp);
					Vector2F startPoint=fig.getStartPoint();
					Vector2F fixedPoint=fixLinkPoint(startPoint);
					list.remove(temp);
					nodeAmmount--;
					for (int i = 0; i < list.size(); i++) {
						fig = list.get(i);
						if (fig instanceof Link) {
							if (fixedPoint.distance(fig.startPoint)<=1 || fixedPoint.distance(((Link)fig).getEndPoint())<=1)
							{
								list.remove(i);
								i--;
								linkAmmount--;
							}
					}
				}
				
				redraw();
			}
		}
	}

	public void deleteElementsFromRectangle(Vector2F startPoint,
			Vector2F endPoint) {
		Rectangle rec=new Rectangle(startPoint,endPoint);
		for (int i = 0; i < list.size(); i++) {
			Figure fig = list.get(i);
			if(rec.isFigureCrossOrIsInsideRectangle(fig))
			{
				if(fig instanceof Node)
				{
					nodeAmmount--;
				}
				else
				{
					linkAmmount--;
				}
				list.remove(i);
				i--;
			}
		}
		redraw();
		
	}

	public int elementsAmmount() {
		return list.size();
	}
	public boolean isEmpty() {
		if (list.size() == 0)
			return true;
		return false;
	}

	public boolean isEnoughNodesForAddLink() {
		if (nodeAmmount > 1)
			return true;
		return false;
	}
	public Canvas getCanvas()
	{
		return canvas;
	}
}
