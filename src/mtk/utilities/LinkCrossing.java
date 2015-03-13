package mtk.utilities;

import mtk.eon.drawing.Link;
import mtk.geom.Vector2F;

public class LinkCrossing {

	Link link1;
	Link link2;
	public LinkCrossing(Link _link1,Link _link2)
	{
		link1=_link1;
		link2=_link2;
	}
	public boolean areCrossing() {
	    Vector2F p1=link1.getStartPoint();
	    Vector2F p2=link1.getEndPoint();
	    Vector2F p3=link2.getStartPoint();
	    Vector2F p4=link2.getEndPoint();
	    float sP1=scalarProduct(p1,p3,p2);
	    float sP2=scalarProduct(p1,p4,p2);
	    float sP3=scalarProduct(p3,p1,p4);
	    float sP4=scalarProduct(p3,p2,p4);
	    if(((sP1>0 && sP2<0)||(sP1<0 && sP2>0) )&& ((sP3<0 && sP4>0)||(sP3>0 && sP4<0)))
	    	return true;
	    else if(sP1==0 && liesBetween(p1,p2,p3))
	        return true;
	    else if(sP2==0 && liesBetween(p1,p2,p4))
	        return true;
        else if(sP3==0 && liesBetween(p3,p4,p1))
        	return true;
	    else if(sP4==0 && liesBetween(p3,p4,p2))
	        return true;
	    return false;
	    }
	private float scalarProduct(Vector2F p1,Vector2F p2,Vector2F p3 )
	{
	    return (p2.getX() - p1.getX())*(p3.getY() - p1.getY()) - (p3.getX() - p1.getX())*(p2.getY() - p1.getY());
	}
    private boolean liesBetween(Vector2F p1,Vector2F p2,Vector2F p3)
    {
        return (Math.min(p1.getX(),p2.getX())<=p3.getX() && Math.max(p1.getX(),p2.getX())>=p3.getX());
    }
}
