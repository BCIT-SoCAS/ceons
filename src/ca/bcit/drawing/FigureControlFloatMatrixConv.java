package ca.bcit.drawing;

import ca.bcit.utils.geom.FloatMatrix;
import ca.bcit.utils.geom.Vector2F;

public class FigureControlFloatMatrixConv {
	private final FigureControl list;
	private final Vector2F benchmarkPoint;
	private final int nodeAmmount;
	private final int linkAmmount;
	private FloatMatrix nodeTable;
	private FloatMatrix linkIdx;
	private int nodeSize;

	public FigureControlFloatMatrixConv(FigureControl _list,Vector2F _benchmark) {
		this.list=_list;
		this.benchmarkPoint=_benchmark;
		this.nodeAmmount=list.getNodeAmount();
		this.linkAmmount=list.getLinkAmount();
	}

	public FigureControlFloatMatrixConv(FigureControl _list) {
		this.list=_list;
		this.benchmarkPoint=new Vector2F(((float)_list.getCanvas().getWidth()/2),((float)_list.getCanvas().getHeight()/2));
		this.nodeAmmount=list.getNodeAmount();
		this.linkAmmount=list.getLinkAmount();
	}

	public FloatMatrix convertFigureControlToFloatMatrix() {
		int actualFigure=0;
		nodeSize=Math.round(Node.imageSize/2);
		float nodeArray[][] = new float[nodeAmmount][2];
		for(int i = list.elementsAmount()-1; i>list.elementsAmount()-nodeAmmount-1; i--) {
			Figure fig=list.get(i);
			Vector2F fixedStartPoint=fixPoint(fig.getStartPoint(),true);
			nodeArray[actualFigure][0] = fixedStartPoint.getX();
			nodeArray[actualFigure][1] = fixedStartPoint.getY();
			actualFigure++;
		}
		nodeTable=new FloatMatrix(nodeArray);
		float linkArray[][] = new float[linkAmmount][2];
		for(int i = 0; i<list.elementsAmount()-nodeAmmount; i++) {
			Link link=((Link)list.get(i));
			Vector2F startPoint=link.getStartPoint();
			startPoint=fixPoint(startPoint,true);
			Vector2F fixedStartPoint=fixLinkPoint(startPoint,true);
			int startNodeid=findNodeId(fixedStartPoint);
			Vector2F endPoint=link.getEndPoint();
			endPoint=fixPoint(endPoint,true);
			Vector2F fixedEndPoint=fixLinkPoint(endPoint,true);
			int endNodeid=findNodeId(fixedEndPoint);
			linkArray[i][0]=startNodeid;
			linkArray[i][1]=endNodeid;
		}
		linkIdx = new FloatMatrix(linkArray);
		return nodeTable;
	}

	public FigureControl convertFloatMatrixToFigureControl(FloatMatrix floatMatrixAfterChanges) {
		if(nodeTable.rows()!=floatMatrixAfterChanges.rows())
			return list;

		FigureControl returnList=new FigureControl(list);
		int actualFigure=0;
		for(int i = list.elementsAmount()-1; i>list.elementsAmount()-nodeAmmount-1; i--) {
			Figure fig=returnList.get(i);
			Vector2F startPoint=floatMatrixAfterChanges.getRow(actualFigure);
			Vector2F fixedStartPoint=fixPoint(startPoint,false);
			fig.setStartPoint(fixedStartPoint);
			actualFigure++;
		}

		for(int i = 0; i<list.elementsAmount()-nodeAmmount; i++) {
			Link link=((Link)returnList.get(i));
			Vector2F linkNodes=linkIdx.getRow(i);
			Vector2F startPoint=floatMatrixAfterChanges.getRow(((int)linkNodes.getX()));
			startPoint=fixPoint(startPoint,false);
			Vector2F fixedStartPoint=fixLinkPoint(startPoint,false);
			Vector2F endPoint=floatMatrixAfterChanges.getRow(((int)linkNodes.getY()));
			endPoint=fixPoint(endPoint,false);
			Vector2F fixedEndPoint=fixLinkPoint(endPoint,false);
			link.setStartPoint(fixedStartPoint);
			link.setEndPoint(fixedEndPoint);
		}
		return returnList;
	}

	private Vector2F fixPoint(Vector2F vec,boolean beforeModifications) {
		float dy = benchmarkPoint.getY();
		float dx = benchmarkPoint.getX();
		if(beforeModifications) {
			dx=-dx;
			dy=-dy;
		}
		return new Vector2F(vec.getX() + dx,vec.getY() + dy);
	}

	private Vector2F fixLinkPoint(Vector2F vec,boolean beforeModifications) {
		int nodeSize=-this.nodeSize;
		if(!beforeModifications)
			nodeSize=-nodeSize;
		return new Vector2F(vec.getX()
				+ nodeSize, vec.getY()
				+ nodeSize );
	}

	private int findNodeId(Vector2F vec) {
		int closestRow=0;
		float closestDistance=nodeTable.getRow(0).distance(vec);
		
		for (int i=1;i<nodeTable.rows();i++) {
			Vector2F row=nodeTable.getRow(i);
			if(closestDistance>row.distance(vec)) {
				closestRow=i;
				closestDistance=row.distance(vec);
			}
		}
		return closestRow;
	}
}
