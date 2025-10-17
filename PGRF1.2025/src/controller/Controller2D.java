package controller;


import Rasterize.LineRasterizer;
import Rasterize.LineRasterizerTrivial;
import model.Line;
import model.Point;
import model.Polygon;
import view.Panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Controller2D {
    private final Panel panel;

    private Color primaryColor;
    private Color secondaryColor;

    private final LineRasterizer lineRasterizer;

    //Ukládání objektů
    ArrayList<Line> lines;

    private Polygon polygon = new Polygon();
    int choosenIndex;


    //Výběr kreslení
    boolean drawLine;
    boolean drawPolygon;
    boolean drawColorTransitionLine;
    boolean drawStraightLine;
    boolean editing;

    //Pomocné
    private Point startPoint;
    private Point endPoint;

    public Controller2D(Panel panel) {
        this.panel = panel;
        this.primaryColor = new Color(255,0,0);
        lines = new ArrayList<>();

        drawLine = true;
        drawColorTransitionLine = false;
        drawPolygon = false;
        drawStraightLine = false;
        editing = false;

        initListeners();
        panel.repaint();


        lineRasterizer = new LineRasterizerTrivial(panel.getRaster());
        //lineRasterizer = new LineRasterizerColorTransition(panel.getRaster());
        //lineRasterizer = new LineRasterizerGraphics(panel.getRaster());

    }

    //Metoda abychom dokola nedávali všechno false
    private void allFalse(){
        drawLine = false;
        drawPolygon = false;
        drawColorTransitionLine = false;
        drawStraightLine = false;
        editing = false;
    }

    //Zjistí jestli je mimo panel
    private boolean isOutOfBounderies(int x, int y){
        if(x<0 || y<0 || x>panel.getWidth()-1 || y>panel.getHeight()-1) {
            return true;
        }
        return false;
    }

    private boolean isOutOfBounderies(Point p){
        if(isOutOfBounderies(p.getX(), p.getY())){
            return true;
        }

        return false;
    }

    private boolean isOutOfBounderiesHeight(int y){
        if(y<0 || y>panel.getHeight()-1) {
            return true;
        }

        return false;
    }

    private boolean isOutOfBounderiesWidth(int x){
        if(x<0 || x>panel.getWidth()-1) {
            return true;
        }
        return false;
    }

    //Vyčistění plátna
    private void clearCanvas(){
        lines.clear();
        polygon =  new Polygon();


        panel.repaint();
        panel.revalidate();
        drawScene();
    }

    //Vybírání barvy
    private void colorChooser(){
        JColorChooser chooser = new JColorChooser(primaryColor);
        
        Color tmpColor = primaryColor;
        primaryColor = JColorChooser.showDialog(chooser,"Vyber si barvu",primaryColor);
        if(primaryColor == null){
            primaryColor = tmpColor;
        }
    }

    private Point makeStraightLine(int x1, int y1, int x2, int y2){


        //Svisle a vodorovně
        if(Math.abs(x1 - x2) < Math.abs(y1 - y2)){
            float alpha = Math.abs((float)x1 - x2) /  Math.abs((float)y1 - y2);

            if(alpha < 0.225){
                return new Point(x1, y2, primaryColor);
            }
        }
        else{
            float alpha = Math.abs((float)y1 - y2) /  Math.abs((float)x1 - x2);

            if(alpha < 0.225){
                return new Point(x2, y1, primaryColor);
            }
        }


        //Diagonálně
        double c = Math.pow(((double)x1 - x2),2) +  Math.pow(((double)y1 - y2),2);

        int a =(int) Math.round(Math.sqrt(c/2));

        int x;
        int y;

        if (x2 > x1) x = x1 + a;
        else x = x1 - a;

        if (y2 > y1) y = y1 +  a;
        else y = y1 - a;


        //Zkontroluje jestli je mimo panel
        if (isOutOfBounderiesWidth(x)){
            if (x2 > x1) {
                x = panel.getWidth() - 1;

                if (y2 > y1) y = y1 + x - x1;
                else y = y1 - (x - x1);

            }
            else{
                x = 0;

                if (y2 > y1) y = y1 + x1;
                else y = y1 - x1;
            }
        }
        else if (isOutOfBounderiesHeight(y)){

            if (y2 > y1){
                y = panel.getHeight() - 1;
                if (x2 > x1) x = x1 + y - y1;
                else x = x1 - (y - y1);
            }
            else{
                y = 0;
                if (x2 > x1) x = x1 + y1;
                else x = x1 - y1;
            }
        }

        return new Point(Math.abs(x), Math.abs(y), primaryColor);
    }


    //Vykreslí uložené elementy
    private void drawScene(){
        panel.getRaster().clear();


        //vykreslení přímek
        if(lines.size() > 0){
            for(int i = 0; i < lines.size(); i++){
                if(lines.get(i).isSameColor()){
                    lineRasterizer.rasterize(lines.get(i), lines.get(i).getStartPoint().getColor());
                }
            }
        }


        //Vykreslení Polygonu
        if (polygon.lastIndex() > 1){

        for(int i = 0; i <= polygon.lastIndex(); i++){

            Point p1 = polygon.getPoint(i);

            if (i == polygon.lastIndex()) {
                Point p2 = polygon.getPoint(0);
                lineRasterizer.rasterize(p1,p2,p1.getColor());
            }
            else{
                Point p2 = polygon.getPoint(i+1);
                lineRasterizer.rasterize(p1,p2,p1.getColor());
            }
        }
        }

        panel.repaint();
    }

    private void initListeners(){
        panel.addMouseListener(new MouseAdapter() {
           @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();



                if(SwingUtilities.isLeftMouseButton(e)){

                    if (drawPolygon){
                        if (editing){
                            choosenIndex = polygon.getIndex(x, y);
                        }
                        else if (!isOutOfBounderies(x, y)){
                            polygon.addPoint(new Point(e.getX(), e.getY(), primaryColor));
                        }
                    }
                    else if (drawLine){

                        if (editing && lines.size() > 0){
                            for (int i = 0; i < lines.size(); i++){

                                int choosenPoint = lines.get(i).getChoosenPoint(x,y);
                                if (choosenPoint == 0){
                                    choosenIndex = i;
                                    startPoint = lines.get(i).getEndPoint();
                                }
                                else if (choosenPoint == 1){
                                    choosenIndex = i;
                                    startPoint = lines.get(i).getStartPoint();
                                }
                            }


                        }
                        else if (!isOutOfBounderies(x, y)){
                            startPoint = new Point(e.getX(), e.getY(), primaryColor);
                        }

                    }
                }

                //Mazaní
                if(SwingUtilities.isRightMouseButton(e)){
                    if (!isOutOfBounderies(x, y)){
                        if(drawPolygon){
                            polygon.deletePoint(polygon.getIndex(x,y));
                        }
                        else if(drawLine){
                            for (int i = 0; i < lines.size(); i++){
                                 if (lines.get(i).getChoosenPoint(x,y) >= 0){
                                     choosenIndex = i;
                                 }
                            }
                                 if(choosenIndex != -1 && choosenIndex < lines.size()){
                                     lines.remove(choosenIndex);
                                 }
                        }
                    }

                }

               drawScene();
               super.mousePressed(e);
           }

           @Override
            public void mouseReleased(MouseEvent e) {

               int x = e.getX();
               int y = e.getY();

               if(SwingUtilities.isLeftMouseButton(e)){
                   if(drawLine && startPoint != null){
                        if (!isOutOfBounderies(x,y)){
                           if (drawStraightLine){
                               endPoint = makeStraightLine(startPoint.getX(), startPoint.getY(), x,y);
                           }else endPoint = new Point(x,y, primaryColor);
                       }


                        if (editing && lines.size() > 0){
                            lines.set(choosenIndex, new Line(startPoint, endPoint));
                        }
                        else {
                            lines.add(new Line(startPoint, endPoint));
                        }
                   }
               }



               choosenIndex = -1;
               drawScene();
               super.mouseReleased(e);
           }
        });


        //Výběr pomocí kláves
        //Q - Kreslení přímek
        //T - Kreslení více barevných přímek
        //Držení Shift - Vodorovné, svislé a diagonály

        //R - Kreslení polygonu
        //E - Editování polygonu

        //I - výběr barvy
        panel.addKeyListener(new KeyAdapter() {


            @Override
            public void keyPressed(KeyEvent e) {

                if(e.getKeyCode() == KeyEvent.VK_R){
                    allFalse();
                    drawPolygon = true;
                }
                else if(e.getKeyCode() == KeyEvent.VK_E){
                    editing = !editing;
                }
                else if(e.getKeyCode() == KeyEvent.VK_Q){
                    allFalse();
                    drawLine = true;
                }
                else if(e.getKeyCode() == KeyEvent.VK_T){
                    if(drawLine & !drawColorTransitionLine){
                        drawColorTransitionLine = true;
                    }
                    else {
                        drawColorTransitionLine = false;
                    }
                }
                else if(e.getKeyCode() == KeyEvent.VK_I){
                    colorChooser();
                }
                else if(drawLine && e.getKeyCode() == KeyEvent.VK_SHIFT){
                    drawStraightLine = true;
                }
                else if(e.getKeyCode() == KeyEvent.VK_C){
                    clearCanvas();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_SHIFT){
                    drawStraightLine = false;
                }
            }
        });




        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {


                if(SwingUtilities.isLeftMouseButton(e)){
                    int x = e.getX();
                    int y = e.getY();

                    if(drawLine){
                         if (!isOutOfBounderies(x, y) && startPoint != null) {
                            drawScene();

                            if (drawStraightLine){
                                endPoint = makeStraightLine(startPoint.getX(), startPoint.getY(), x,y);
                            }
                            else{
                                endPoint = new Point(x, y, primaryColor);
                            }

                            if (editing && lines.size() > 0){
                                lines.set(choosenIndex, new Line(startPoint, endPoint));
                            }
                            if (startPoint.getColor() == endPoint.getColor()) {
                                lineRasterizer.rasterize(startPoint,endPoint,startPoint.getColor());
                            }
                        }
                    }
                    else if(drawPolygon){
                        if (editing && choosenIndex > -1){
                        drawScene();
                        polygon.setPoint(x,y,primaryColor, choosenIndex);
                        }
                    }
                }
                panel.repaint();
            }
        });
    }


}
