package mtk.eon.drawing;

import mtk.utilities.DashedDrawing;
import mtk.geom.Vector2F;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Node extends Figure {
    static float imageSize=24;

    public Node(Node node)
    {
        startPoint=node.startPoint.clone();
        name=node.name;
        loadImage();
    }
    public Node(Vector2F startPoint,int number) {

        super(new Vector2F(startPoint.getX()-imageSize/2,startPoint.getY()-imageSize/2),"Node"+number);
        loadImage();
        }
    public Node(Vector2F startPoint,String _name)
    {
        super(new Vector2F(startPoint.getX()-imageSize/2,startPoint.getY()-imageSize/2),_name);
        loadImage();
    }
    @Override
    public void draw(GraphicsContext gc) {
        loadImage();
        gc.drawImage(image, startPoint.getX(), startPoint.getY());
    }
    @Override
    public boolean equals(Object obj) {
        Node temp= (Node)obj;
        return (temp.getStartPoint().equals(startPoint) && temp.name.equals((name)));
    }
    
    protected void loadImage() {
        image = new Image("mtk/eon/drawing/circle_image.jpg",imageSize,imageSize,true,false);
    }
    public Vector2F getCenterPoint()
    {
    	float x=startPoint.getX()+imageSize/2;
    	float y=startPoint.getY()+imageSize/2;
    	return new Vector2F(x,y);
    }
    @Override
    protected void drawOutline(GraphicsContext gc, Color color) {
        DashedDrawing.drawDashedCircle(gc,new Vector2F(startPoint.getX()+imageSize/2,startPoint.getY()+imageSize/2),imageSize/2+3,Color.GRAY);
    }
    public static void changeNodeSize(float factory)
    {
        System.out.println("factory"+factory);
        restoreDefaultNodeSize();
        imageSize=(int)(imageSize*factory);
        if(imageSize<12)
            imageSize=12;
        else if(imageSize>24)
            imageSize=24;

    }
    
    public static void restoreDefaultNodeSize() {
        imageSize=24;
    }
    public static float getNodeSize()
    {
        return imageSize;
    }
	@Override
	protected double calculateDistanceFromPoint(Vector2F p) {
		return Math.sqrt(Math.pow(p.getX()-startPoint.getX(),2)+Math.pow(p.getY()-startPoint.getY(),2));
	}
}
