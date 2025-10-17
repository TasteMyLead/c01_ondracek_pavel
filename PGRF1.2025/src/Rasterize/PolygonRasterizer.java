package Rasterize;

import model.Polygon;

public class PolygonRasterizer {
    private LineRasterizer lineRasterizer;

    public PolygonRasterizer(LineRasterizer lineRasterizer){
        this.lineRasterizer = lineRasterizer;

    }

    public void setLineRasterizer(LineRasterizer lineRasterizer){
        this.lineRasterizer = lineRasterizer;
    }

    public void rasterize(Polygon polygon){
        //Pokud je méně jak 3 pointy, nevykresluju


    }
}
