package mtk.eon.drawing;

import mtk.utilities.*;
import mtk.geom.Vector2F;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;


public class Link extends Figure {
    protected Vector2F endPoint;
    private int length;
    public Link(Link link)
    {
        length=link.length;
        name=link.name;
        startPoint=link.startPoint.clone();
        endPoint=link.endPoint.clone();
        loadImage();
    }
    public Link(Vector2F stPoint, Vector2F _endPoint, int number)
    {
        super(stPoint,"Link"+number);
        endPoint=_endPoint;
        length=0;
        loadImage();
    }
    public Link(Vector2F _startPoint, Vector2F _endPoint, String _name)
    {
        super(_startPoint,_name);
        endPoint=_endPoint;
        length=0;
        loadImage();
    }
    public Link(Vector2F _startPoint, Vector2F _endPoint, String _name, int _length)
    {
        super(_startPoint,_name);
        endPoint=_endPoint;
        length=_length;
        loadImage();
    }
    @Override
    public void draw(GraphicsContext gc) {
        gc.setLineWidth(2);
        gc.setStroke(new ImagePattern(image));
        gc.strokeLine(startPoint.getX(),startPoint.getY(),endPoint.getX(),endPoint.getY());
        gc.setLineWidth(1);
    }
    @Override
    public boolean equals(Object o)
    {
        Link vex=(Link)o;
        return(startPoint.equals(vex.startPoint) && endPoint.equals(vex.endPoint));
    }
    @Override
    protected void loadImage() {
            image = new Image(getClass().getResourceAsStream("line_image.jpg"));
    }

    @Override
    protected void drawOutline(GraphicsContext gc, Color color) {

        int dx=0,dy=5;
        if(Math.abs(endPoint.getX()-startPoint.getX())<50) {
            dx =5;
            dy=0;
        }
        Vector2F newStartPoint = new Vector2F(startPoint.getX()+dx, startPoint.getY()+dy);
        Vector2F newEndPoint=new Vector2F(endPoint.getX()+dx,endPoint.getY()+dy);
        DashedDrawing.drawDashedLine(gc,newStartPoint,newEndPoint,color);
        newStartPoint = new Vector2F(startPoint.getX()-dx, startPoint.getY()-dy);
        newEndPoint=new Vector2F(endPoint.getX()-dx,endPoint.getY()-dy);
        DashedDrawing.drawDashedLine(gc,newStartPoint,newEndPoint,color);
        newStartPoint=new Vector2F(startPoint.getX()-dx,startPoint.getY()-dy);
        newEndPoint=new Vector2F(startPoint.getX()+dx,startPoint.getY()+dy);
        DashedDrawing.drawDashedLine(gc,newStartPoint,newEndPoint,color);
        newEndPoint=new Vector2F(endPoint.getX()+dx,endPoint.getY()+dy);
        newStartPoint=new Vector2F(endPoint.getX()-dx,endPoint.getY()-dy);
        DashedDrawing.drawDashedLine(gc,newStartPoint,newEndPoint,color);
    }
    @Override
    protected double calculateDistanceFromPoint(Vector2F p) {
        //jezeli punkt miesci sie w przedziale x nalezacych do prostej to obliczam jego dlugosc od prostej
        if((p.getX()+5>startPoint.getX() &&p.getX()-5<endPoint.getX()) || (p.getX()+5>endPoint.getX() &&p.getX()-5<startPoint.getX())) {
            float x1 = startPoint.getX() - Node.imageSize / 2;
            float y1 = startPoint.getY() - Node.imageSize / 2;
            float x2 = endPoint.getX() - Node.imageSize / 2;
            float y2 = endPoint.getY() - Node.imageSize / 2;
            //wyznaczenie rownania prostej
            float a = (-y2 + y1) / (x2 - x1);
            float b = -y1 - ((-y2 + y1) / (x2 - x1)) * x1;
            //wyyznaczenie odleglosci punktu od prostej
            double odleglosc = (Math.abs(a * p.getX() + p.getY() + b)) / Math.sqrt(1 + a * a);
            return odleglosc+Node.imageSize / 2;
        }
        //jesli to nie to obliczam jego odleglosc od punktow koncowych
        else
        {
            float dist1=startPoint.distance(p);
            float dist2=endPoint.distance(p);
            if(dist1>dist2)
                return dist2+Node.imageSize;
            else
                return dist1+Node.imageSize;
        }
    }

    @Override
    public void drawOutline(GraphicsContext gc) {
        drawOutline(gc,Color.GRAY);
    }

    public void setEndPoint(Vector2F p)
    {
        endPoint=p;
    }
    public Vector2F getEndPoint()
    {
        return endPoint;
    }
    public int getLength()
    {
        return length;
    }
    public void setLength(int _length)
    {
        length=_length;
    }
}
