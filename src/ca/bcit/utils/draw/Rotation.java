package ca.bcit.utils.draw;

import ca.bcit.drawing.FigureControl;
import ca.bcit.drawing.FigureControlFloatMatrixConv;
import ca.bcit.utils.geom.FloatMatrix;
import ca.bcit.utils.geom.Vector2F;

import java.awt.geom.Line2D;

public class Rotation {
	private final Vector2F rotationPoint;
	private final FigureControl list;
	private float rotationArc = 0;

	public Rotation(Vector2F rotationPoint, FigureControl list) {
		this.rotationPoint = rotationPoint;
		this.list = new FigureControl(list);
	}

	public FigureControl rotate(Vector2F startPoint, Vector2F endPoint) {
		if(!list.isEmpty())
		{
			float actualArc = calculateArc(startPoint, endPoint);
			if (isClockwiseRotation(startPoint, endPoint)) {
				rotationArc += actualArc;
			} else {
				rotationArc -= actualArc;
			}
			FigureControlFloatMatrixConv conv=new FigureControlFloatMatrixConv(list,rotationPoint);
			FloatMatrix figuresTable = conv.convertFigureControlToFloatMatrix();
			FloatMatrix rotationTable = new FloatMatrix(
				new float[][] {{(float) Math.cos(rotationArc), (float) -Math.sin(rotationArc)}, {(float) Math.sin(rotationArc),	(float) Math.cos(rotationArc)}});
			figuresTable = figuresTable.multiply(rotationTable);
			FigureControl temp = conv.convertFloatMatrixToFigureControl(figuresTable);
			rotationArc %= 2 * Math.PI;
			return temp;
		}
		return list;
	}

	private float calculateArc(Vector2F startPoint, Vector2F endPoint) {
		float sideA = startPoint.distance(rotationPoint);
		float sideB = endPoint.distance(rotationPoint);
		float sideC = startPoint.distance(endPoint);
		float cosArc = (sideA * sideA + sideB * sideB - sideC * sideC) / (2 * sideA * sideB);
		//Łapało NaN tu prawdopodobnie jest problem
		if(cosArc<-1 || cosArc>1)
			return 0;
		return (float) Math.acos(cosArc);
	}

	private boolean isClockwiseRotation(Vector2F startPoint, Vector2F endPoint) {
		float x1 = startPoint.getX();
		float y1 = startPoint.getY();
		float x2 = endPoint.getX();
		float y2 = endPoint.getY();
		float centerX = rotationPoint.getX();
		float centerY = rotationPoint.getY();
		int clockwiseRotation = Line2D.relativeCCW(centerX, centerY, x1, y1, x2, y2);
		return clockwiseRotation == 1;
	}

}
