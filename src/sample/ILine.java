package sample;

import javafx.scene.Node;
import javafx.scene.shape.Line;

/**
 * Created by Полина on 14.10.2014.
 */
public class ILine  extends Line {

    private Vertex startVertex;
    private Vertex endVertex;

    public ILine(){
        this.startVertex = startVertex;
        this.endVertex = endVertex;
    }


    public Vertex getStartVertex(){
        return this.startVertex;
    }
    public Vertex getEndVertex(){
        return this.endVertex;
    }

    public void setStartVertex(Vertex startVertex) {this.startVertex = startVertex;}
    public void setEndVertex(Vertex endVertex) {this.endVertex = endVertex;}

}
