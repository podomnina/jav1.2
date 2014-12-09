package sample;

/**
 * Created by Полина on 14.10.2014.
 */
public class Vertex {

    private double X;
    private double Y;
    private int Radius;
    private int num;

    public Vertex(int num, double X, double Y, int Radius) {
        this.num = num;
        this.X = X;
        this.Y = Y;
        this.Radius = Radius;
    }

    public int getNum() {
        return this.num;
    }
    public double getX(){
        return this.X;
    }
    public double getY(){
        return this.Y;
    }
    public int getRadius(){
        return this.Radius;
    }

    public void setNum(int num) {
        this.num = num;
    }
    public void setX(double X) {
        this.X = X;
    }
    public void setY(double Y) {
        this.Y = Y;
    }
    public void setRadius(int Radius) {
        this.Radius = Radius;
    }

    public void setAll(int num, double X, double Y, int Radius)
    {
        this.num = num;
        this.X = X;
        this.Y = Y;
        this.Radius = Radius;
    }

}

