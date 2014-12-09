package sample;

import javafx.scene.shape.Line;

/**
 * Created by Даша on 03.12.2014.
 */
public class Risochka  extends Line {
    private double StX;
    private double StY;
    private double EX;
    private double EY;
    private ILine line;
    public Risochka(/*double x1,double y1,double x2,double y2*/){
       /* this.StX=x1;
        this.StY=y1;
        this.EX=x2;
        this.EY=y2;*/
    }

   public double getStX(){
        return this.StX;
    }
    public double getStY(){
        return this.StY;
    }
    public double getEX(){
        return this.EX;
    }
    public double getEY(){
        return this.EY;
    }
    public ILine getLine() {
        return  this.line;
    }

    public void setStX(double X) {
        this.StX = X;
    }
    public void setStY(double Y) {
        this.StY = Y;
    }
    public void setEX(double X) {
        this.EX = X;
    }
    public void setEY(double Y) {
        this.EY = Y;
    }
    public void setLine(ILine line) {
        this.line = line;
    }

}
