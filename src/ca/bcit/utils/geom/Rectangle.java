package ca.bcit.utils.geom;


import ca.bcit.drawing.Figure;
import ca.bcit.drawing.Link;
import ca.bcit.drawing.Node;

public class Rectangle {
	private Vector2F leftUpCorner;
	private Vector2F rightDownCorner;
	
	public Rectangle(Vector2F vec1,Vector2F vec2)
	{
		findAndSetLeftUpCorner(vec1,vec2);
		findAndSetRightDownCorner(vec1,vec2);
	}
	
	private void findAndSetLeftUpCorner(Vector2F vec1,Vector2F vec2)
	{
		leftUpCorner=new Vector2F(Math.min(vec1.getX(),
				vec2.getX()), Math.min(vec1.getY(), vec2.getY()));
	}
	private void findAndSetRightDownCorner(Vector2F vec1,Vector2F vec2)
	{
		rightDownCorner=new Vector2F(Math.max(vec1.getX(),
				vec2.getX()), Math.max(vec1.getY(), vec2.getY()));	
	}
	public boolean isFigureCrossOrIsInsideRectangle(Figure fig)
	{
		if(fig instanceof Node)
			return isNodeCrossOrInsideRectangle((Node)fig);
		return isLinkCrossOrInsideRectangle((Link)fig);
	}
	private boolean isLinkCrossOrInsideRectangle(Link fig) {
		Vector2F linkStartPoint=fig.getStartPoint();
		Vector2F linkEndPoint=fig.getEndPoint();
		if(pointIsInsideRectangle(linkStartPoint))
			return true;
		if(pointIsInsideRectangle(linkEndPoint))
			return true;
		return isLinkCrossRectangle(fig);
	}

	private boolean isNodeCrossOrInsideRectangle(Node fig) {
		boolean anyCornerInside=isAnyNodeCornerInsideRectangle(fig);
		if(anyCornerInside)
			return true;
		return isAnyNodeSideCrossRectangle(fig);
		 
	}
	private boolean isAnyNodeSideCrossRectangle(Node fig) {
		Vector2F leftNodeCorner=fig.getStartPoint();
		float leftNodeCornerX=leftNodeCorner.getX();
		float leftNodeCornerY=leftNodeCorner.getY();
		float nodeSize=Node.getNodeSize();
		LineSegment rectLeftSideLine=new LineSegment(leftUpCorner,new Vector2F(leftUpCorner.getX(),rightDownCorner.getY()));
		LineSegment rectDownSideLine=new LineSegment(new Vector2F(leftUpCorner.getX(),rightDownCorner.getY()),rightDownCorner);
		LineSegment rectRightSideLine=new LineSegment(new Vector2F(rightDownCorner.getX(),leftUpCorner.getY()),rightDownCorner);
		LineSegment rectUpSideLine=new LineSegment(leftUpCorner,new Vector2F(rightDownCorner.getX(),leftUpCorner.getY()));
		LineSegment nodeLeftSideLine=new LineSegment(leftNodeCorner, new Vector2F(leftNodeCornerX,leftNodeCornerY+nodeSize));
		LineSegment nodeDownSideLine=new LineSegment(new Vector2F(leftNodeCornerX,leftNodeCornerY+nodeSize),new Vector2F(leftNodeCornerX+nodeSize,leftNodeCornerY+nodeSize));
		LineSegment nodeRightSideLine=new LineSegment(new Vector2F(leftNodeCornerX+nodeSize,leftNodeCornerY+nodeSize),new Vector2F(leftNodeCornerX+nodeSize,leftNodeCornerY));
		LineSegment nodeUpSideLine=new LineSegment(leftNodeCorner,new Vector2F(leftNodeCornerX+nodeSize,leftNodeCornerY));
		if(rectLeftSideLine.areCrossing(nodeLeftSideLine))
			return true;
		if(rectLeftSideLine.areCrossing(nodeRightSideLine))
			return true;
		if(rectLeftSideLine.areCrossing(nodeDownSideLine))
			return true;
		if(rectLeftSideLine.areCrossing(nodeUpSideLine))
			return true;
		if(rectRightSideLine.areCrossing(nodeLeftSideLine))
			return true;
		if(rectRightSideLine.areCrossing(nodeRightSideLine))
			return true;
		if(rectRightSideLine.areCrossing(nodeDownSideLine))
			return true;
		if(rectRightSideLine.areCrossing(nodeUpSideLine))
			return true;
		if(rectDownSideLine.areCrossing(nodeLeftSideLine))
			return true;
		if(rectDownSideLine.areCrossing(nodeRightSideLine))
			return true;
		if(rectDownSideLine.areCrossing(nodeDownSideLine))
			return true;
		if(rectDownSideLine.areCrossing(nodeUpSideLine))
			return true;
		if(rectUpSideLine.areCrossing(nodeLeftSideLine))
			return true;
		if(rectUpSideLine.areCrossing(nodeRightSideLine))
			return true;
		if(rectUpSideLine.areCrossing(nodeDownSideLine))
			return true;
		return rectUpSideLine.areCrossing(nodeUpSideLine);

	}

	private boolean isAnyNodeCornerInsideRectangle(Node fig)
	{
		Vector2F leftUpNodeCorner=fig.getStartPoint();
		float nodeSize=Node.getNodeSize();
		float xNodePoint=leftUpNodeCorner.getX();
		float yNodePoint=leftUpNodeCorner.getY();
		Vector2F rightUpNodeCorner=new Vector2F(xNodePoint+nodeSize,yNodePoint);
		Vector2F leftDownNodeCorner=new Vector2F(xNodePoint,yNodePoint+nodeSize);
		Vector2F rightDownNodeCorner=new Vector2F(xNodePoint+nodeSize,yNodePoint+nodeSize);
		return 	   pointIsInsideRectangle(leftUpNodeCorner)
				|| pointIsInsideRectangle(rightUpNodeCorner)
				|| pointIsInsideRectangle(leftDownNodeCorner)
				|| pointIsInsideRectangle(rightDownNodeCorner);
	}
	private boolean pointIsInsideRectangle(Vector2F checkedPoint)
	{
		boolean isBelowLeftLine	=checkedPoint.getX() > leftUpCorner.getX();
		boolean isBelowRightLine=checkedPoint.getX() < rightDownCorner.getX();
		boolean isBelowUpLine	=checkedPoint.getY() > leftUpCorner.getY();
		boolean isUnderDownLine	=checkedPoint.getY() < rightDownCorner.getY();
		return isBelowLeftLine && isBelowRightLine && isBelowUpLine&& isUnderDownLine;
	}
	private boolean isLinkCrossRectangle(Link link)
	{
		float LUCX=leftUpCorner.getX();
		float LUCY=leftUpCorner.getY();
		float RDCX=rightDownCorner.getX();
		float RDCY=rightDownCorner.getY();
		LineSegment rectSide1 = new LineSegment(new Vector2F(LUCX,LUCY), new Vector2F(RDCX,LUCY));
		LineSegment rectSide2 = new LineSegment(new Vector2F(LUCX,LUCY), new Vector2F(LUCX,RDCY));
		LineSegment rectSide3 = new LineSegment(new Vector2F(RDCX,LUCY), new Vector2F(RDCX,RDCY));
		LineSegment rectSide4 = new LineSegment(new Vector2F(LUCX,RDCY), new Vector2F(RDCX,RDCY));
		LineSegment linkLS=new LineSegment(link.getStartPoint(),link.getEndPoint());
		return linkLS.areCrossing(rectSide1)
			|| linkLS.areCrossing(rectSide2)
			|| linkLS.areCrossing(rectSide3)
			|| linkLS.areCrossing(rectSide4); 
	}
}
