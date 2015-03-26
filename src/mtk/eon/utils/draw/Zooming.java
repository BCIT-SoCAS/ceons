package mtk.eon.utils.draw;

import mtk.eon.drawing.FigureControl;
import mtk.eon.drawing.FigureControlFloatMatrixConv;
import mtk.eon.drawing.Node;
import mtk.eon.utils.geom.FloatMatrix;

public class Zooming {
	private FigureControl list;
	static private float factory=1.0f;
	static private float nodeFactory=1.0f;
	public Zooming(FigureControl list) {
		this.list = new FigureControl(list);	
	}

	public FigureControl zoom(boolean enlarge) {
		changeNodeSize(calculateNodeSize());
		if(factory>0.05f)
		{
			if(enlarge){
				factory+=0.01f;
				nodeFactory+=0.01f;
			}
			else{
				factory-=0.01f;
				nodeFactory-=0.01f;
			}
		}
		else
		{
			if(enlarge){
				factory+=0.01f;
				nodeFactory+=0.01f;
			}
		}
		FloatMatrix rotateTable = new FloatMatrix(new float[][] {{1.001f, 0f}, {0f, 1.001f}});
		FigureControlFloatMatrixConv conv=new FigureControlFloatMatrixConv(list);
		FloatMatrix figuresTable = conv.convertFigureControlToFloatMatrix();
		rotateTable = rotateTable.multiply(factory);
		figuresTable = figuresTable.multiply(rotateTable);
		FigureControl temp = conv.convertFloatMatrixToFigureControl(figuresTable);
		return temp;
	}

	private void changeNodeSize(float newNodeSize) {
		Node.changeNodeSize(newNodeSize);
	}

	private float calculateNodeSize() {
		return Node.getStartNodeSize()*nodeFactory;
	}

	public static void clearFactory()
	{
		factory=1.0f;
	}
}
