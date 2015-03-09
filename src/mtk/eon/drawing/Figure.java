package mtk.eon.drawing;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import mtk.geom.*;
import javafx.scene.paint.Color;



/**
 * Created by Admin on 2014-12-27.
 */
public abstract class Figure {
    protected Image image;
    protected Vector2F startPoint;
    protected String name;
    protected static final float sizeOfOutline=8;
    public Figure(Vector2F _startPoint, String _name)
    {
        startPoint=_startPoint;
        name=_name;
    }
    public Figure()
    {
        startPoint=new Vector2F(0,0);
        name="";
    }
    public abstract void draw(GraphicsContext gc);

    public Image getImage() {
        return image;
    }

    public Vector2F getStartPoint() {
        return startPoint;
    }
    public void setStartPoint(Vector2F _startPoint)
    {
        startPoint=_startPoint;
    }
    protected abstract void loadImage();
    protected abstract double calculateDistanceFromPoint(Vector2F p);
    protected abstract void drawOutline(GraphicsContext gc, Color color);
    protected void drawOutline(GraphicsContext gc)
    {
        drawOutline(gc,Color.BLACK);
    }
    public String getName()
    {
        return name;
    }
    public void setName(String _name)
    {
        name=_name;
    }
    public boolean equalsByName(String _name)
    {
        return name.equals(_name);
    }
}
