package raster;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RasterBufferedrImage implements Raster {
    private BufferedImage image;

    public RasterBufferedrImage(int  width, int height) {
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }



    @Override
    public void setPixel(int x, int y, int color) {

        image.setRGB(x, y, color);
    }

    @Override
    public int getPixel(int x, int y) {
        return 0;
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

    @Override
    public void clear() {
        Graphics g = image.getGraphics();
        g.clearRect(0, 0, image.getWidth(), image.getHeight());
    }

    public BufferedImage getImage() {
        return image;
    }
}
