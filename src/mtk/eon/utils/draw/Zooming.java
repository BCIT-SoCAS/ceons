package mtk.eon.utils.draw;

import mtk.eon.drawing.FigureControl;
import mtk.eon.drawing.FigureControlFloatMatrixConv;
import mtk.eon.drawing.Node;
import mtk.eon.utils.geom.FloatMatrix;

public class Zooming {
	
	private FigureControl list;
	static private float factory=1.0f;
	static private float nodeFactory=1.0f;
	static boolean somethingChange=false;
	public Zooming(FigureControl list) {
		this.list = new FigureControl(list);
		
	}

	public FigureControl zoom(boolean enlarge) {
			changeNodeSize(calculateNodeSize());
			System.out.println("zmienilem");
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
				//factory=0.05f;
			}
		}
		System.out.println("nodeFactory"+nodeFactory);
		System.out.println("factory"+factory);
		System.out.println("nodeSize"+Node.imageSize);
		FloatMatrix rotateTable = new FloatMatrix(new float[][] {{1.001f, 0f}, {0f, 1.001f}});
		FigureControlFloatMatrixConv conv=new FigureControlFloatMatrixConv(list);
		FloatMatrix figuresTable = conv.convertFigureControlToFloatMatrix();
		System.out.println("Przed zmianami"+'\n'+figuresTable.toString());
		rotateTable = rotateTable.multiply(factory);
		if (factory!=1.0) {
			figuresTable = figuresTable.multiply(rotateTable);
			System.out.println("Po zmianach"+'\n'+figuresTable);
			FigureControl temp = conv.convertFloatMatrixToFigureControl(figuresTable);
			return temp;
		} else {
			//restoreDefaultNodeSize();
			return new FigureControl(list);
		}
	}

	private void changeNodeSize(float factory) {
		Node.changeNodeSize(factory);
	}

	private float calculateNodeSize() {
		System.out.println("calculatedNodeSize"+Node.getStartNodeSize()*nodeFactory);
		return Node.getStartNodeSize()*nodeFactory;
	}

	public static void clearScrollNumber()
	{
		factory=1.0f;
	}
}
