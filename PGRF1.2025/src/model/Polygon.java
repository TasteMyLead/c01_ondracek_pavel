package model;

import java.awt.*;
import java.util.ArrayList;

public class Polygon {
    private final ArrayList<Point> points;

    public Polygon(){
        this.points = new ArrayList<>();
    }

    public void addPoint(Point point){
        this.points.add(point);
    }

    public Point getPoint(int index){
        return this.points.get(index);
    }

    public int lastIndex(){
        return this.points.size() - 1;
    };

    public void deletePoint(int index){
        if (index >= 0 && index < this.points.size()){
            this.points.remove(index);
        }
    }

    public int getIndex(int x,int y){
        int index = -1;
        for (int i = 0; i < this.points.size(); i++){
                Point point = this.points.get(i);
                if (isClose(x,y,point.getX(),point.getY(),20)){
                    index = i;
                }
        }
        return index;
    }

    public void setPoint(Point point, int index){
        points.set(index, point);
    }
    public void setPoint(int x, int y, Color color,int index){
        setPoint(new Point(x, y, color), index);
    }

    private boolean isClose(int x1, int y1, int x2, int y2, int toleration){
        double distance = Math.sqrt(Math.pow(x2-x1,2) + Math.pow(y2-y1,2));
        if(distance<toleration) return true;
        else return false;
    }



}
