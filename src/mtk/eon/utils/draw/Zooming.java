package mtk.eon.utils.draw;

import mtk.eon.drawing.FigureControl;
import mtk.eon.drawing.Node;
import mtk.eon.utils.geom.FloatMatrix;

public class Zooming {
	
	FigureControl list;

	public Zooming(FigureControl list) {
		this.list = new FigureControl(list);
	}

	public FigureControl zoom(int scrollNumber) {
		FloatMatrix rotateTable = new FloatMatrix(new float[][] {{1.001f, 0f}, {0f, 1.001f}});
		FloatMatrix figuresTable = list.allElementsToMatrix();
		float factory = (float) (1.0 + (float) scrollNumber / 100);
		rotateTable = rotateTable.multiply(factory);
		if (scrollNumber != 0) {
			changeNodeSize(factory);
			figuresTable = figuresTable.multiply(rotateTable);
			FigureControl temp = new FigureControl(list);
			temp.matrixToList(figuresTable);
			return temp;
		} else {
			restoreDefaultNodeSize();
			return new FigureControl(list);
		}

	}

	private void changeNodeSize(float factory) {
		Node.changeNodeSize(factory);
	}

	private void restoreDefaultNodeSize() {
		Node.restoreDefaultNodeSize();
	}

	public void checkList(FigureControl list) {
		if (!(this.list.equals(list)))
			this.list = new FigureControl(list);
	}
}
