package ca.bcit.drawing;

import ca.bcit.utils.geom.Rectangle;
import ca.bcit.utils.geom.Vector2F;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class FigureControl {
	private final ArrayList<Figure> list = new ArrayList<>();
	private int nodeAmount;
	private int linkAmount;
	private final Canvas canvas;
	private Figure selectedFigure;

	public FigureControl(Canvas _canvas) {
		canvas = _canvas;
		nodeAmount = 0;
		linkAmount = 0;
	}

	public ArrayList<Figure> getList() {
		return list;
	}

	public FigureControl(FigureControl temp) {
		for (Figure fig : temp.list)
			if (fig instanceof Node)
				list.add(new Node((Node) fig));
			else if (fig instanceof Link)
				list.add(new Link((Link) fig));

		nodeAmount = temp.nodeAmount;
		linkAmount = temp.linkAmount;
		canvas = temp.canvas;
		selectedFigure = temp.selectedFigure;
	}

	/**
	 * Draw a new Figure (Node or Link) on the canvas
	 *
	 * @param temp		the figure object. It needs to be either node or link.
	 */
	public void add(Figure temp) {

		if (temp instanceof Node) {
			list.add(temp);
			nodeAmount++;
		}
		else if (temp instanceof Link && isEnoughNodesForAddLink()) {
			int closestNodeId=findClosestNode(temp.getStartPoint());
			Figure closestNode=list.get(closestNodeId);
			Vector2F p = closestNode.getStartPoint();
			p = fixLinkPoint(p);
			temp.setStartPoint(p);
			list.add(0, temp);
			linkAmount++;
		}

		redraw();
	}

	private Vector2F fixLinkPoint(Vector2F p) {
		return new Vector2F(p.getX() + Node.imageSize / 2, p.getY() + Node.imageSize / 2);
	}

	private Vector2F fixNodePoint(Vector2F p) {
		return new Vector2F(p.getX() - Node.imageSize / 2, p.getY() - Node.imageSize / 2);
	}

	public int findClosestNode(Vector2F temp) {
		if(!isEmpty()){
			int actualClosestNode = findFirstNode();
			double closestDistance = calculateDistance(actualClosestNode, temp);
			for (int i = actualClosestNode; i < list.size(); i++)
				if (list.get(i) instanceof Node) {
					double actualDistance = calculateDistance(i, temp);
					if (actualDistance < closestDistance) {
						closestDistance = actualDistance;
						actualClosestNode = i;
					}
				}

			return actualClosestNode;
		}
		return -1;
	}

	private int findFirstNode() {
		for(int i=0;i<list.size();i++)
			if(list.get(i) instanceof Node)
				return i;

		return -1;
	}

	private double calculateDistance(int idx, Vector2F p) {
		Figure temp = list.get(idx);
		return temp.calculateDistanceFromPoint(p);
	}

	public void redraw() {
		clearCanvas();
		for (Figure fig : list)
			fig.draw(canvas.getGraphicsContext2D());

		if (selectedFigure != null)
			selectedFigure.drawOutline(canvas.getGraphicsContext2D());
	}

	public void remove(Figure temp) {
		list.remove(temp);
		redraw();
	}

	private void clearCanvas() {
		canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	public int getNodeAmount() {
		return nodeAmount;
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
		ObservableList<Figure> data = FXCollections.observableArrayList();
		ObservableList<String> returnedData = FXCollections.observableArrayList();
		Vector2F fixedPoint = fixLinkPoint(temp.getStartPoint());
		for (Figure fig : list) {
			if (fig instanceof Link) {
				Vector2F linkStartPoint = fig.getStartPoint();
				Vector2F linkEndPoint = ((Link) fig).getEndPoint();
				if (linkStartPoint.equals(fixedPoint)) {
					Figure n = findNodeAtPoint(linkEndPoint);
					if (n != null && !data.contains(n))
						data.add(n);
				} else if (linkEndPoint.equals(fixedPoint)) {
					Figure n = findNodeAtPoint(linkStartPoint);
					if (n != null && !data.contains(n))
						data.add(n);
				}
			}
		}
		Comparator<Figure> comparator = Comparator.comparingInt(Figure::getNodeNum);
		data.sort(comparator);
		for(Figure f : data)
			returnedData.add(f.getName());

		return returnedData;
	}

	public Figure findNodeAtPoint(Vector2F p) {
		Vector2F fixedPoint = fixNodePoint(p);
		for (Figure fig : list)
			if (fig.getStartPoint().equals(fixedPoint))
				return fig;

		return null;
	}

	public int getLinkAmount() {
		return linkAmount;
	}

	public Figure findFigureByName(String name) {
		for (Figure fig : list)
			if (fig.equalsByName(name))
				return fig;

		return null;
	}

	public boolean containsFigureWithName(String name) {
		return findFigureByName(name) != null;
	}

	public void changeNodePoint(Figure node, Vector2F vec2f) {
		vec2f = fitPointToCanvas(vec2f);
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

	public Figure get(int idx) {
		return list.get(idx);
	}

	private Vector2F fitPointToCanvas(Vector2F vec2F) {
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

	public void deleteNode(Vector2F clickedPoint) {
		if (!isEmpty()) {
			int temp = (findClosestNode(clickedPoint));
			if (calculateDistance(temp, clickedPoint) < Node.imageSize/2) {
				Figure fig=list.get(temp);
				Vector2F startPoint=fig.getStartPoint();
				Vector2F fixedPoint=fixLinkPoint(startPoint);
				list.remove(temp);
				nodeAmount--;
				for (int i = 0; i < list.size(); i++) {
					fig = list.get(i);
					if (fig instanceof Link)
						if (fixedPoint.distance(fig.startPoint)<=1 || fixedPoint.distance(((Link)fig).getEndPoint())<=1) {
							list.remove(i);
							i--;
							linkAmount--;
						}
				}
				
				redraw();
			}
		}
	}

	/**
	 * Mark node as international or data center(replica)
	 *	
	 */
	public void markNode(Vector2F clickedPoint, String groupName) {
		if (!isEmpty()) {
			int temp = (findClosestNode(clickedPoint));

			if (calculateDistance(temp, clickedPoint) < Node.imageSize/2) {
				Figure fig=list.get(temp);
				Node node = (Node) fig;
				node.setNodeGroup(groupName, true);
			}
			
			redraw();
		}
	}

	/**
	 * Remove all groups from a node
	 *
	 */
	public void unmarkNode(Vector2F clickedPoint) {
		if (!isEmpty()) {
			int temp = (findClosestNode(clickedPoint));

			if (calculateDistance(temp, clickedPoint) < Node.imageSize/2) {
				Figure fig=list.get(temp);
				Node node = (Node) fig;
				Map nodeGroups = node.getNodeGroups();
				for (Object key : nodeGroups.keySet())
					node.setNodeGroup(key.toString(), false);
			}
			
			redraw();
		}
	}

	public void deleteElementsFromRectangle(Vector2F startPoint, Vector2F endPoint) {
		Rectangle rec = new Rectangle(startPoint,endPoint);
		for (int i = 0; i < list.size(); i++) {
			Figure fig = list.get(i);
			if(rec.isFigureCrossOrIsInsideRectangle(fig)) {
				if(fig instanceof Node)
					nodeAmount--;
				else
					linkAmount--;

				list.remove(i);
				i--;
			}
		}
		redraw();
		
	}

	public boolean isEmpty() {
		return list.size() == 0;
	}

	private boolean isEnoughNodesForAddLink() {
		return nodeAmount > 1;
	}
}
