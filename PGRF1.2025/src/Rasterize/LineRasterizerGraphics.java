package Rasterize;

import raster.RasterBufferedrImage;

import java.awt.*;

public class LineRasterizerGraphics extends LineRasterizer {

    public LineRasterizerGraphics(RasterBufferedrImage raster) {
        super(raster);
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2, Color color) {
        Graphics g = raster.getImage().getGraphics();
        g.setColor(Color.RED);
        g.drawLine(x1, y1, x2, y2);
    }
}
