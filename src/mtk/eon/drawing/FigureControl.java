package mtk.eon.drawing;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import mtk.eon.utils.draw.LinkCrossing;
import mtk.eon.utils.geom.FloatMatrix;
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
				Vector2F p = list.get(findClosestNode(temp.getStartPoint()))
						.getStartPoint();
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

		double closestDistance = calculateDistance(0, temp);
		int actualClosestNode = 0;
		for (int i = 0; i < list.size(); i++) {
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

	private double calculateDistance(int idx, Vector2F p) {
		Figure temp = list.get(idx);
		return Math.sqrt(Math.pow(p.getX() - temp.getStartPoint().getX(), 2)
				+ Math.pow(p.getY() - temp.getStartPoint().getY(), 2));
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
			System.out.println("change end point");
			Vector2F temp = list.get(findClosestNode(p)).getStartPoint();
			temp = fixLinkPoint(temp);
			Link link = ((Link) list.get(0));
			link.setEndPoint(temp);
			if (temp.equals(link.startPoint) || isLinkAlreadyExist()) {
				//list.remove(0);
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
		if (closestDistance > 30)
			return null;
		return list.get(closestElement);
	}

	public ObservableList<String> generateNodeConnections(Figure temp) {
		ObservableList<String> data = FXCollections.observableArrayList();
		Vector2F fixedPoint = fixLinkPoint(temp.getStartPoint());
		for (Figure fig : list) {
			if (fig instanceof Link) {
				if (fig.getStartPoint().equals(fixedPoint)) {
					Vector2F p = ((Link) fig).getEndPoint();
					Figure n = findNodeAtPoint(p);
					if (n != null)
						data.add(n.getName());
				} else if (((Link) fig).getEndPoint().equals(fixedPoint)) {
					Vector2F p = fig.getStartPoint();
					Figure n = findNodeAtPoint(p);
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

	public void drawOutline(Figure fig) {
		drawOutline(fig, Color.BLACK);
	}

	public void drawOutline(Figure fig, Color color) {
		redraw();
		fig.drawOutline(canvas.getGraphicsContext2D(), color);
	}

	public boolean containsFigureWithName(String name) {
		if (findFigureByName(name) != null)
			return true;
		return false;
	}

	public void changeNodePoint(Figure node, Vector2F vec2f) {
		vec2f = fitPointToCanva(vec2f);
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
			for (int i = 0; i < list.size() - 1; i++) {
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
	}

	public Figure getSelectedFigure() {
		return selectedFigure;
	}

	public FloatMatrix allElementsToMatrix(String name) {
		Figure temp = findFigureByName(name);
		if (temp == null)
			return allElementsToMatrix();
		else {
			return allElementsToMatrix(temp.getStartPoint());
		}
	}

	public FloatMatrix allElementsToMatrix() {
		Vector2F vec2F = new Vector2F((float) canvas.getWidth() / 2,
				(float) canvas.getHeight() / 2);
		return allElementsToMatrix(vec2F);
	}

	public FloatMatrix allElementsToMatrix(Vector2F rotatePoint) {
		float table[][] = new float[nodeAmmount + linkAmmount * 2][2];
		int actualFigure = 0;
		float dx = rotatePoint.getX();
		float dy = rotatePoint.getY();
		for (Figure fig : list) {
			table[actualFigure][0] = fig.getStartPoint().getX() - dx;
			table[actualFigure][1] = fig.getStartPoint().getY() - dy;
			if (fig instanceof Link) {
				table[actualFigure][0] = table[actualFigure][0]
						- Node.imageSize / 2;
				table[actualFigure][1] = table[actualFigure][1]
						- Node.imageSize / 2;
				actualFigure++;
				table[actualFigure][0] = ((Link) fig).getEndPoint().getX() - dx
						- Node.imageSize / 2;
				table[actualFigure][1] = ((Link) fig).getEndPoint().getY() - dy
						- Node.imageSize / 2;
			}
			actualFigure++;
		}
		return new FloatMatrix(table);
	}

	public void matrixToList(FloatMatrix mat, String nodeName) {
		Figure temp = findFigureByName(nodeName);
		if (temp == null)
			matrixToList(mat);
		else {
			matrixToList(mat, temp.getStartPoint());
		}
	}

	public void matrixToList(FloatMatrix mat) {
		Vector2F vec2F = new Vector2F((float) canvas.getWidth() / 2,
				(float) canvas.getHeight() / 2);
		matrixToList(mat, vec2F);
	}

	public void matrixToList(FloatMatrix mat, Vector2F rotationPoint) {
		int actualFigure = 0;
		float dx = rotationPoint.getX();
		float dy = rotationPoint.getY();
		for (Figure fig : list) {
			Vector2F temp = mat.getRow(actualFigure);
			Vector2F fixedVector = new Vector2F((int) (temp.getX() + dx),
					(int) (temp.getY() + dy));
			fig.setStartPoint(fixedVector);
			if (fig instanceof Link) {
				fixedVector = list.get(findClosestNode(fixedVector))
						.getStartPoint();
				fig.setStartPoint(new Vector2F(fixedVector.getX()
						+ Node.imageSize / 2, fixedVector.getY()
						+ Node.imageSize / 2));
				actualFigure++;
				temp = mat.getRow(actualFigure);
				fixedVector = new Vector2F((int) (temp.getX() + dx),
						(int) (temp.getY() + dy));
				fixedVector = list.get(findClosestNode(fixedVector))
						.getStartPoint();
				((Link) fig).setEndPoint(new Vector2F(
						(int) (fixedVector.getX() + Node.imageSize / 2),
						(int) (fixedVector.getY() + Node.imageSize / 2)));
			}
			actualFigure++;
		}
		redraw();
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
			if (calculateDistance(temp, clickedPoint) < 30) {
				Vector2F fixedPoint = fixLinkPoint(list.get(temp)
						.getStartPoint());
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
				list.remove(temp);
				redraw();
			}
		}
	}

	public void deleteElementsFromRectangle(Vector2F startPoint,
			Vector2F endPoint) {
		Vector2F temp1 = new Vector2F(Math.min(startPoint.getX(),
				endPoint.getX()), Math.min(startPoint.getY(), endPoint.getY()));
		Vector2F temp2 = new Vector2F(Math.max(startPoint.getX(),
				endPoint.getX()), Math.max(startPoint.getY(), endPoint.getY()));
		startPoint = temp1;
		endPoint = temp2;
		Link rectSide1 = new Link(new Vector2F(startPoint.getX(),
				startPoint.getY()), new Vector2F(endPoint.getX(),
				startPoint.getY()), "rectSide1");
		Link rectSide2 = new Link(new Vector2F(startPoint.getX(),
				startPoint.getY()), new Vector2F(startPoint.getX(),
				endPoint.getY()), "rectSide2");
		Link rectSide3 = new Link(new Vector2F(endPoint.getX(),
				startPoint.getY()), new Vector2F(endPoint.getX(),
				endPoint.getY()), "rectSide3");
		Link rectSide4 = new Link(new Vector2F(startPoint.getX(),
				endPoint.getY()),
				new Vector2F(endPoint.getX(), endPoint.getY()), "rectSide4");

		for (int i = 0; i < list.size(); i++) {
			Figure fig = list.get(i);
			if (fig.getStartPoint().getX() > startPoint.getX()
					&& fig.getStartPoint().getX() < endPoint.getX()
					&& fig.getStartPoint().getY() > startPoint.getY()
					&& fig.getStartPoint().getY() < endPoint.getY()) {
				list.remove(i);
				i--;
				if (fig instanceof Node)
					nodeAmmount--;
			} else if (fig instanceof Link) {
				LinkCrossing LC1 = new LinkCrossing((Link) fig, rectSide1);
				LinkCrossing LC2 = new LinkCrossing((Link) fig, rectSide2);
				LinkCrossing LC3 = new LinkCrossing((Link) fig, rectSide3);
				LinkCrossing LC4 = new LinkCrossing((Link) fig, rectSide4);
				boolean areCrossing = LC1.areCrossing() || LC2.areCrossing()
						|| LC3.areCrossing() || LC4.areCrossing();
				if ((((Link) fig).getEndPoint().getX() > startPoint.getX()
						&& ((Link) fig).getEndPoint().getX() < endPoint.getX()
						&& ((Link) fig).getEndPoint().getY() > startPoint
								.getY() && ((Link) fig).getEndPoint().getY() < endPoint
						.getY()) || areCrossing)
					;
				{
					list.remove(i);
					i--;
					linkAmmount--;
				}
			}
		}
		redraw();
	}

	public int elementsAmmount() {
		return list.size();
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
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
}
