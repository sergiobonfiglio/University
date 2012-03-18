package target;

import java.awt.Point;

public class Region {

    Point centro;
    int raggioX;
    int raggioY;

    public Region() {
    }

    public Region(Point centro, int rx, int ry) {
        this.centro = centro;
        this.raggioX = rx;
        this.raggioY = ry;
    }

    public Point getCentro() {
        return centro;
    }

    public void setCentro(Point centro) {
        this.centro = centro;
    }

    public int getRaggioX() {
        return raggioX;
    }

    public void setRaggioX(int rx) {
        this.raggioX = rx;
    }

    public int getRy() {
        return raggioY;
    }

    public void setRaggioY(int ry) {
        this.raggioY = ry;
    }

    public float distanzaQuadDalCentroNorm(int x, int y) {
        float nX = getNormalizedX(x);
        float nY = getNormalizedY(y);
        return nX * nX + nY * nY;
    }

    public float getNormalizedX(int x) {

        float nX = (centro.x - x) / ((float) raggioX);

        return nX;
    }

    public float getNormalizedY(int y) {
        float nY = (centro.y - y) / ((float) raggioY);

        return nY;
    }

    public int getDenormalizedX(float x) {
        return (int) (-((x * raggioX) - centro.x));
    }

    public int getDenormalizedY(float y) {
        return (int) (-((y * raggioY) - centro.y));
    }

    @Override
    public String toString() {

        return "(" + centro.x + "," + centro.y + ")" + raggioX + "x" + raggioY;
    }
}
