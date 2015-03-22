package mtk.eon.drawing;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import mtk.eon.utils.draw.LinkCrossing;
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
					if (fig.getStartPoint().equals(fixedOldPoint))
						fig.setStartPoint(fixedNewPoint);
					else if (((Link) fig).getEndPoint().equals(fixedOldPoint))
						((Link) fig).setEndPoint(fixedNewPoint);
				}
			}
			redraw();
		}
	}

	private boolean isLinkAlreadyExist() {
		if (isEnoughNodesForAddLink()) {
			Link link = (Link) list.get(0);
			for (int i = 1; i < list.size(); i++) {
				Figure fig = list.get(i);
				if (fig instanceof Link)
					if (link.equals(fig))
						return true;
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
		Link link = new Link(startPoint, endPoint, "temp");
		for (int i = 0; i < list.size(); i++) {
			Figure fig = list.get(i);
			if (fig instanceof Link) {
				LinkCrossing linkCrossing = new LinkCrossing((Link) fig, link);
				if (linkCrossing.areCrossing()) {
					list.remove(i);
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
					Vector2F fixedPoint = fixLinkPoint(list.get(temp)
							.getStartPoint());
					list.remove(temp);
					for (int i = 0; i < list.size(); i++) {
						Figure fig = list.get(i);
						if (fig instanceof Link) {
							if (fig.getStartPoint().equals(fixedPoint)
									|| ((Link) fig).getEndPoint()
											.equals(fixedPoint)) {
								list.remove(i);
								i--;
						}
					}
				}
				
				redraw();
			}
		}
	}

	public void deleteElementsFromRectangle(Vector2F startPoint,
			Vector2F endPoint) {
		Vector2F leftUpCorner = findLeftUpCorner(startPoint, endPoint);
		Vector2F rightDownCorner = findRightDownCorner(startPoint, endPoint);	
		for (int i = 0; i < list.size(); i++) {
			Figure fig = list.get(i);
			Vector2F checkedPoint=fig.getStartPoint();
			boolean isInside=pointIsInsideRectangle(leftUpCorner, rightDownCorner, checkedPoint);
			if (isInside) {
				list.remove(i);
				i--;
				if (fig instanceof Node)
					nodeAmmount--;
				else
					linkAmmount--;
			} else if (fig instanceof Link) {
				boolean areCrossing=isLinkCrossRectangle((Link)fig,leftUpCorner,rightDownCorner);
				checkedPoint=((Link)fig).getEndPoint();
				isInside=pointIsInsideRectangle(leftUpCorner, rightDownCorner, checkedPoint);
				if(isInside||areCrossing)
				{
					list.remove(i);
					i--;
					linkAmmount--;
				}
			}
		}
		redraw();
	}

	private boolean pointIsInsideRectangle(Vector2F leftUpCorner,Vector2F rightDownCorner, Vector2F checkedPoint)
	{
		boolean isBelowLeftLine= checkedPoint.getX() > leftUpCorner.getX();
		boolean isBelowRightLine=checkedPoint.getX() < rightDownCorner.getX();
		boolean isBelowUpLine=checkedPoint.getY() > leftUpCorner.getY();
		boolean isUnderDownLine=checkedPoint.getY() < rightDownCorner.getY();
		boolean isInside=isBelowLeftLine&&isBelowRightLine&&isBelowUpLine&&isUnderDownLine;
		return isInside;
	}
	private boolean isLinkCrossRectangle(Link link,Vector2F leftUpCorner,Vector2F rightDownCorner)
	{
		float LUCX=leftUpCorner.getX();
		float LUCY=leftUpCorner.getY();
		float RDCX=rightDownCorner.getX();
		float RDCY=rightDownCorner.getY();
		Link rectSide1 = new Link(new Vector2F(LUCX,LUCY), new Vector2F(RDCX,LUCY), "rectSide1");
		Link rectSide2 = new Link(new Vector2F(LUCX,LUCY), new Vector2F(LUCX,RDCY), "rectSide2");
		Link rectSide3 = new Link(new Vector2F(RDCX,LUCY), new Vector2F(RDCX,RDCY), "rectSide3");
		Link rectSide4 = new Link(new Vector2F(LUCX,RDCY), new Vector2F(RDCX,RDCY), "rectSide4");
		LinkCrossing LC1 = new LinkCrossing(link, rectSide1);
		LinkCrossing LC2 = new LinkCrossing(link, rectSide2);
		LinkCrossing LC3 = new LinkCrossing(link, rectSide3);
		LinkCrossing LC4 = new LinkCrossing(link, rectSide4);
		return LC1.areCrossing() || LC2.areCrossing() || LC3.areCrossing() || LC4.areCrossing();
	}
	private Vector2F findLeftUpCorner(Vector2F vec1,Vector2F vec2)
	{
		return new Vector2F(Math.min(vec1.getX(),
				vec2.getX()), Math.min(vec1.getY(), vec2.getY()));
	}
	private Vector2F findRightDownCorner(Vector2F vec1,Vector2F vec2)
	{
		return new Vector2F(Math.max(vec1.getX(),
				vec2.getX()), Math.max(vec1.getY(), vec2.getY()));	
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
