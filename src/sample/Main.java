//Надо пофиксить: если перемещаем вершину, то нужно изменить ее координаты в списке vertexList//СДЕЛАНО
//После того, как нажимаем Delete, нельзя добавлять вершины. Надо исправить
//Сделала несколько кружков, удалила, нажала New, потому что иначе не добавляются новые. Почему-то все можно перемещать, а первый нет.//ПОФИКШЕНО
//Доделать Refresh. Исправила с красного на синий, ибо после нажатия рефреша и продолжения работы цвет остается красный.
//Доделать удаление любого кружка
//По окончании почистить код и Sample
//Изменила перемещение кружков(теперь они не двигаются). Их перемещения мешает рисолвать стрелки
package sample;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.*;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Main extends Application {
    //----------------------Model---------------------//
    public static ArrayList<Vertex> vertexList = new ArrayList<Vertex>();
    public static ArrayList<ILine> lineList = new ArrayList<ILine>();
    public static ArrayList<Risochka> lineListRis1 = new ArrayList<Risochka>();
    public static ArrayList<Risochka> lineListRis2 = new ArrayList<Risochka>();
    public static Vertex vertex;
    public static ILine line;
    public int drag; //для определения перемещения

    public static int i = 0;   //счетчик вершин
    int increm=-1;
//----------------------ModelEND---------------------//


    public double getSampleWidth() {
        return 500;
    }

    public double getSampleHeight() {
        return 500;
    }

    private Group root;

    private javafx.scene.control.Button NewFile;
    private javafx.scene.control.Button Refresh;
    private javafx.scene.control.Button Drag;
    private Parent zoomPane;
    private ImageView image;
    private VBox layout;
    private double initX;
    private double initY;
    private Point2D dragAnchor;
    private ToolBar toolbar;
    private double zoom=1;
    private double delta;
    private double dx=0;
    private double dy=0;
    private double width=852;
    private double height=531;

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Graph Editor");
        root = new Group();
        //
        zoomPane = createZoomPane(root);
        toolbar = new ToolBar();


        layout = new VBox();
        layout.getChildren().setAll(zoomPane,toolbar);

        VBox.setVgrow(zoomPane, Priority.ALWAYS);       ///////////
        Scene scene = new Scene(layout,width,height);


        primaryStage.setScene(scene);
        primaryStage.show();
        //


        scene.getStylesheets().add(Main.class.getResource("/sample/Style.css").toExternalForm());




        image = new ImageView(getClass().getResource("11-1.jpg").toExternalForm());
        root.getChildren().addAll(image);


//-----------------------------TOOLBAR-------------------------------------------//

        toolbar.getItems().add(NewFile = new javafx.scene.control.Button("New"));

        toolbar.getItems().add(Refresh = new javafx.scene.control.Button("Refresh"));
        toolbar.getItems().add(Drag = new javafx.scene.control.Button("Drag"));

        toolbar.setId("toolbar");

        toolbar.setMinHeight(50);
        toolbar.setMaxHeight(50);
        System.out.println("Toolbar "+toolbar.getHeight());
        NewFile.setId("NewFile");


        Refresh.setId("Refresh");
        Drag.setId("Drag");

        root.setId("All");




        NewFile.setMinSize(70, 30);



        Refresh.setMinSize(70, 30);
        Drag.setMinSize(70, 30);


//-----------------------------TOOLBAR_end-------------------------------------------//



        image.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                //when mouse is pressed, store initial position


                    dragAnchor = new Point2D(me.getSceneX(), me.getSceneY());
                    final Circle circleBig = createCircle("Circle", javafx.scene.paint.Color.BLUE, 30);

                    circleBig.setTranslateX(me.getSceneX());
                    circleBig.setTranslateY(me.getSceneY());

                    System.out.println(circleBig.getTranslateX());
                    System.out.println(circleBig.getTranslateY());

                    vertex = new Vertex(++i, circleBig.getTranslateX(), circleBig.getTranslateY(), 30);
                    vertexList.add(vertex);
                    System.out.println(vertex.getNum() + " " + vertex.getX() + " " + vertex.getY());
                    root.getChildren().add(circleBig);
                    circleBig.setId("Circle");



            }

        });
        Refresh.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                Refresh();
                System.out.println("Completed");
            }
        });
        NewFile.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                image.toFront();
                toolbar.toFront();
                vertexList = new ArrayList<Vertex>();
                increm=-1;
                lineList=new ArrayList<ILine>();
            }
        });
        Drag.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {

                    if (drag == 1) {
                        drag = 0;
                        Drag.setText("Drag Off");
                    } else {
                        drag = 1;
                        Drag.setText("Drag On");
                    }
                    Refresh();

            }
        });
    }


    private void DeleteC(MouseEvent me) {


        for (int i = vertexList.size() - 1; i >= 0; i--) {
            if (Math.pow((me.getSceneX() - vertexList.get(i).getX()), 2) + Math.pow((me.getSceneY() - vertexList.get(i).getY()), 2) < Math.pow(vertexList.get(i).getRadius(), 2)) {

                for (int j = lineList.size() - 1; j >= 0; j--) {
                    if (lineList.get(j).getStartVertex() == vertexList.get(i) || lineList.get(j).getEndVertex() == vertexList.get(i)) {
                        lineList.remove(lineList.get(j));
                        increm=increm-1;
                    }

                }
                vertexList.remove(vertexList.get(i));
                Refresh();
            }
        }


    }

    private void Refresh() {
        image.toFront();
        toolbar.toFront();
        root.getChildren().clear();
        root.getChildren().addAll(image);

        for (int j = 0; j < lineList.size(); j++) {
            final ILine line = new ILine();
            /*final Risochka line1 = new Risochka();
            final Risochka line2 = new Risochka();*/
           if (lineList.get(j).getStartVertex()!=lineList.get(j).getEndVertex()) {
                line.setStartX(lineList.get(j).getStartVertex().getX());
                line.setStartY(lineList.get(j).getStartVertex().getY());
                line.setEndX(lineList.get(j).getEndVertex().getX());
                line.setEndY(lineList.get(j).getEndVertex().getY());
                line.setFill(null);
                line.setStroke(javafx.scene.paint.Color.RED);
                line.setStrokeWidth(2);


                Double k = Math.atan((lineList.get(j).getEndVertex().getY() - lineList.get(j).getStartVertex().getY()) / (lineList.get(j).getEndVertex().getX() - lineList.get(j).getStartVertex().getX()));
                try {
                    if (lineList.get(j).getStartVertex() != lineList.get(j).getEndVertex()) {
                        if (lineList.get(j).getStartVertex().getX() > lineList.get(j).getEndVertex().getX()) {
                            line.setEndX(lineList.get(j).getEndVertex().getX() + lineList.get(j).getEndVertex().getRadius() * Math.cos(k));
                            line.setEndY(lineList.get(j).getEndVertex().getY() + lineList.get(j).getEndVertex().getRadius() * Math.sin(k));
                        } else {
                            line.setEndX(lineList.get(j).getEndVertex().getX() - lineList.get(j).getEndVertex().getRadius() * Math.cos(k));
                            line.setEndY(lineList.get(j).getEndVertex().getY() - lineList.get(j).getEndVertex().getRadius() * Math.sin(k));
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                int leva = 0;
                int pravo = 0;
                for (int i = lineListRis1.size() - 1; i >= 0; i--) {
                    if (lineList.get(j) == lineListRis1.get(i).getLine()) {
                        for (int l = lineListRis2.size() - 1; l >= 0; l--) {
                            if (lineList.get(j) == lineListRis2.get(l).getLine()) {
                                CreateRis(lineList.get(j).getEndX(), lineList.get(j).getEndY(), lineList.get(j), lineListRis1.get(i), lineListRis2.get(l));
                                leva = i;
                                pravo = l;
                                break;

                            }
                        }
                    }
                }


                root.getChildren().addAll(lineList.get(j), lineListRis1.get(leva), lineListRis2.get(pravo));
            }
        }
        for (int j = 0; j < vertexList.size(); j++) {
            final Circle circle = createCircle("Circle", javafx.scene.paint.Color.BLUE, vertexList.get(j).getRadius());
            circle.setTranslateX(vertexList.get(j).getX());
            circle.setTranslateY(vertexList.get(j).getY());
            root.getChildren().add(circle);
            System.out.println("Success!");
        }

    }

    private Circle createCircle(final String name, final javafx.scene.paint.Color color, int radius) {


            int X;
            int Y;
            //create a circle with desired name,  color and radius
            final Circle circle = new Circle(radius, new RadialGradient(0, 0, 0.2, 0.3, 1, true, CycleMethod.NO_CYCLE, new Stop[]{
                    new Stop(0, javafx.scene.paint.Color.rgb(250, 250, 255)),
                    new Stop(1, color)
            }));
            //add a shadow effect
        System.out.println("Zoom=" + RoundZoom(zoom, 2));

            circle.setEffect(new InnerShadow(7, color.darker().darker()));
            //change a cursor when it is over circle
            circle.setCursor(Cursor.HAND);
            //add a mouse listeners
            circle.setOnMouseClicked(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent me) {
                    // showOnConsole("Clicked on" + name + ", " + me.getClickCount() + "times");
                    //the event will be passed only to the circle which is on front
                    me.consume();
                }
            });


//создаем новую линии:основную и две для уголка
                if (this.drag == 1) {

                    Drag(circle);
                } else {


                        circle.setOnMousePressed(new EventHandler<MouseEvent>() {

                            public void handle(MouseEvent me) {

                                //правая кнопка мыши
                                if (me.getButton() == MouseButton.SECONDARY) {
                                    DeleteC(me);

                                    System.out.println("delete");
                                } else {
                                    increm++;
                                    //final Line line = new Line();
                                    final Risochka line1 = new Risochka();
                                    final Risochka line2 = new Risochka();
                                    final ILine lin = new ILine();
                                    final Double x = me.getSceneX();
                                    final Double y = me.getSceneY();
                                    lin.setStartX(me.getSceneX());
                                    lin.setStartY(me.getSceneY());
                                    lin.setEndX(me.getSceneX());
                                    lin.setEndY(me.getSceneY());
                                    lin.onDragEnteredProperty();
                                    lin.setFill(null);
                                    lin.setStroke(javafx.scene.paint.Color.RED);
                                    lin.setStrokeWidth(2);
                                    //ЛАПКА для СТРЕЛКИ
                                    lin.setCursor(Cursor.HAND);
                                    line1.setCursor(Cursor.HAND);
                                    line2.setCursor(Cursor.HAND);
                                    lin.setOnMousePressed(new EventHandler<MouseEvent>() {
                                        public void handle(MouseEvent me) {
                                            DeletePointer(me);
                                        }
                                    });


//рисуем линию и уголок в след за курсором
                                    circle.setOnMouseDragged(new EventHandler<MouseEvent>() {
                                        public void handle(MouseEvent me) {

                                            lin.setEndX(me.getSceneX());
                                            lin.setEndY(me.getSceneY());
                                            CreateRis(me.getSceneX(), me.getSceneY(), lin, line1, line2);


                                        }
                                    });

//создаем стрелку
                                    circle.setOnMouseReleased(new EventHandler<MouseEvent>() {
                                        public void handle(MouseEvent me) {

                                            CreateArrow(me, lin, line1, line2, x, y);

                                        }
                                    });         // show the line shape;
                                    root.getChildren().add(line1);
                                    root.getChildren().add(line2);
                                    lineList.add(lin);
                                    line1.setLine(lin);
                                    line2.setLine(lin);
                                    lineListRis1.add(line1);
                                    lineListRis2.add(line2);
                                    root.getChildren().add(lineList.get(increm));
                                }
                            }

                        });

                }


            circle.setOnMouseEntered(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent me) {
                    //change the z-coordinate of the circle
                    circle.toFront();

                    //showOnConsole("Mouse entered " + name);
                }
            });


            circle.setOnMouseReleased(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent me) {
                    //showOnConsole("Mouse released above " +name);
                }
            });


            return circle;

    }

    private void Drag(final Circle circle) {
        System.out.println("Zoom="+RoundZoom(zoom, 2));

            circle.setOnMouseDragged(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent me) {

                    double dragX = me.getSceneX() - dragAnchor.getX();
                    double dragY = me.getSceneY() - dragAnchor.getY();
                    //calculate new position of the circle
                    double newXPosition = initX + dragX;
                    double newYPosition = initY + dragY;

                    double FirstX = circle.getTranslateX();
                    double FirstY = circle.getTranslateY();
                    int NumberOfCircle = 0;
                    //if new position do not exceeds borders of the rectangle, translate to this position
                    for (int j = 0; j < vertexList.size(); j++) {
                        if ((FirstX == vertexList.get(j).getX()) && (FirstY == vertexList.get(j).getY()))
                            NumberOfCircle = j;
                    }

                    if ((newXPosition >= circle.getRadius()) && (newXPosition <= 1200 - circle.getRadius())) {
                        circle.setTranslateX(newXPosition);
                        vertexList.get(NumberOfCircle).setX(newXPosition);
                    }
                    if ((newYPosition >= circle.getRadius()) && (newYPosition >= circle.getRadius()) && (newYPosition <= 500 - circle.getRadius())) {
                        circle.setTranslateY(newYPosition);
                        vertexList.get(NumberOfCircle).setY(newYPosition);
                    }
                    Proverka(circle.getTranslateX(), circle.getTranslateY());

                }
            });
            circle.setOnMousePressed(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent me) {
                    //when mouse is pressed, store initial position
                    initX = circle.getTranslateX();
                    initY = circle.getTranslateY();
                    dragAnchor = new Point2D(me.getSceneX(), me.getSceneY());

                    //showOnConsole("Mouse pressed above " + name);
                }
            });

    }

    //функция создания стрелки:использую два цикла:1)для проверки,лежит ли конец стрелки в круге, вычисления точки пересечения с кругом.
// 2)вычисление для начала стрелки
    private void CreateArrow(MouseEvent me,  ILine lin, Risochka line1, Risochka line2, Double x, Double y) {
        for (Vertex vert : vertexList) {

            if (Math.pow((me.getSceneX() - vert.getX()), 2) + Math.pow((me.getSceneY() - vert.getY()), 2) < Math.pow(vert.getRadius(), 2)) {


                for (Vertex vert1 : vertexList) {
                    if (Math.pow((lin.getStartX() - vert1.getX()), 2) + Math.pow((lin.getStartY() - vert1.getY()), 2) < Math.pow(vert.getRadius(), 2)) {
                        lin.setStartX(vert1.getX());
                        lin.setStartY(vert1.getY());
                        lin.setStartVertex(vert1);
                        //lineList.add(lin);
                        Double k = Math.atan((vert1.getY() - vert.getY()) / (vert1.getX() - vert.getX()));
                        if (vert1.getX() > vert.getX()) {
                            lin.setEndX(vert.getX() + vert.getRadius() * Math.cos(k));
                            lin.setEndY(vert.getY() + vert.getRadius() * Math.sin(k));

                        } else {
                            lin.setEndX(vert.getX() - vert.getRadius() * Math.cos(k));
                            lin.setEndY(vert.getY() - vert.getRadius() * Math.sin(k));


                        }
                        CreateRis(lin.getEndX(), lin.getEndY(), lin, line1, line2);
                        lin.setEndVertex(vert);
                        break;
                    }

                }
                break;
            } else {

                lin.setEndX(x);
                lin.setEndY(y);
                line1.setStartX(x);
                line1.setEndX(x);
                line1.setStartY(y);
                line1.setEndY(y);
                line2.setStartX(x);
                line2.setEndX(x);
                line2.setStartY(y);
                line2.setEndY(y);
            }

        }
    }

    //рисование самого уголка
    private void CreateRis(Double X, Double Y, ILine lin, Risochka _line1, Risochka _line2) {

        double beta = Math.atan2(Y - lin.getStartY(), X - lin.getStartX()); //{ArcTan2 ищет арктангенс от x/y что бы неопределенностей не
        //  возникало типа деления на ноль}
        double alfa = Math.PI / 15;// {угол между основной осью стрелки и рисочки в конце}
        int r1 = 20; //{длинна риски}

        int x1 = (int) Math.round(X - r1 * Math.cos(beta + alfa));
        int y1 = (int) Math.round(Y - r1 * Math.sin(beta + alfa));
        int x2 = (int) Math.round(X - r1 * Math.cos(beta - alfa));
        int y2 = (int) Math.round(Y - r1 * Math.sin(beta - alfa));


        _line1.setStartX(X);
        _line1.setStartY(Y);
        _line1.setEndX(x1);
        _line1.setEndY(y1);
        _line1.onDragEnteredProperty();
        _line1.setFill(null);
        _line1.setStroke(javafx.scene.paint.Color.RED);
        _line1.setStrokeWidth(2);


        _line2.setStartX(X);
        _line2.setStartY(Y);
        _line2.setEndX(x2);
        _line2.setEndY(y2);
        _line2.onDragEnteredProperty();
        _line2.setFill(null);
        _line2.setStroke(javafx.scene.paint.Color.RED);
        _line2.setStrokeWidth(2);



    }
    private void Refresh1(Vertex ver) {





        for (int j = lineList.size() - 1; j >= 0; j--) {
            lineList.get(j).onDragEnteredProperty();
            lineList.get(j).setFill(null);
            lineList.get(j).setStroke(javafx.scene.paint.Color.RED);
            lineList.get(j).setStrokeWidth(2);


           if (lineList.get(j).getStartVertex() == ver) {

                lineList.get(j).setStartY(ver.getY());
                lineList.get(j).setStartX(ver.getX());
                //rasschitat setEnd x i y po formule strelki

               Double kol = Math.atan((lineList.get(j).getEndVertex().getY() - lineList.get(j).getStartVertex().getY()) / (lineList.get(j).getEndVertex().getX() - lineList.get(j).getStartVertex().getX()));
               if (lineList.get(j).getEndVertex().getX() < lineList.get(j).getStartVertex().getX()) {
                   lineList.get(j).setEndX(lineList.get(j).getEndVertex().getX() + lineList.get(j).getEndVertex().getRadius() * Math.cos(kol));
                   lineList.get(j).setEndY(lineList.get(j).getEndVertex().getY() + lineList.get(j).getEndVertex().getRadius() * Math.sin(kol));

               } else {
                   lineList.get(j).setEndX(lineList.get(j).getEndVertex().getX() - lineList.get(j).getEndVertex().getRadius() * Math.cos(kol));
                   lineList.get(j).setEndY(lineList.get(j).getEndVertex().getY() - lineList.get(j).getEndVertex().getRadius() * Math.sin(kol));


               }


                for (int i = lineListRis1.size() - 1; i >= 0; i--) {
                    if (lineList.get(j)==lineListRis1.get(i).getLine()){
                        for (int k = lineListRis2.size() - 1; k >= 0; k--) {
                            if (lineList.get(j) == lineListRis2.get(k).getLine()) {
                                CreateRis(lineList.get(j).getEndX(), lineList.get(j).getEndY(), lineList.get(j), lineListRis1.get(i), lineListRis2.get(k));
                                break;
                            }
                        }
                    }
                }

            }


            if (lineList.get(j).getEndVertex() == ver) {
                for (int i = lineListRis1.size() - 1; i >= 0; i--) {
                    if (lineList.get(j) == lineListRis1.get(i).getLine()) {
                        for (int k = lineListRis2.size() - 1; k >= 0; k--) {
                            if (lineList.get(j) == lineListRis2.get(k).getLine()) {
                                CreateRis(lineList.get(j).getEndX(), lineList.get(j).getEndY(), lineList.get(j), lineListRis1.get(i), lineListRis2.get(k));
                                break;
                            }
                        }
                    }
                }

                Double k = Math.atan((lineList.get(j).getEndVertex().getY() - lineList.get(j).getStartVertex().getY()) / (lineList.get(j).getEndVertex().getX() - lineList.get(j).getStartVertex().getX()));
                try {
                    if (lineList.get(j).getStartVertex() != lineList.get(j).getEndVertex()) {
                        if (lineList.get(j).getStartVertex().getX() > lineList.get(j).getEndVertex().getX()) {

                            lineList.get(j).setEndX(ver.getX() + ver.getRadius() * Math.cos(k));
                            lineList.get(j).setEndY(ver.getY() + ver.getRadius() * Math.sin(k));


                        } else {

                            lineList.get(j).setEndX(ver.getX() - ver.getRadius() * Math.cos(k));
                            lineList.get(j).setEndY(ver.getY() - ver.getRadius() * Math.sin(k));


                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

        }




    }
    private void Proverka(double x,double y) {
        for (int i = vertexList.size() - 1; i >= 0; i--) {
            if (Math.pow((x - vertexList.get(i).getX()), 2) + Math.pow((y - vertexList.get(i).getY()), 2) < Math.pow(vertexList.get(i).getRadius(), 2)) {
                Refresh1(vertexList.get(i));
                break;
            }

        }
    }


    private void DeletePointer (MouseEvent me){
        for (int j = lineList.size() - 1; j >= 0; j--) {
            /*System.out.print("me.getSceneX()");
            System.out.print(me.getSceneX());
            System.out.print("me.getSceneY()");
            System.out.print(me.getSceneY());
            System.out.print("lineList.StartX");
            System.out.print(lineList.get(j).getStartVertex().getX());
            System.out.print("lineList.StartY");
            System.out.print(lineList.get(j).getStartVertex().getY());
            System.out.print("lineList.EndY");
            System.out.print(lineList.get(j).getEndVertex().getY());
            System.out.print("lineList.EndX");
            System.out.print(lineList.get(j).getEndVertex().getX());*/
            System.out.print("привет!");
            System.out.println((Math.round((me.getSceneX()-lineList.get(j).getStartX())*(lineList.get(j).getEndY()-lineList.get(j).getStartY())) - Math.round((lineList.get(j).getEndX()-lineList.get(j).getStartX()) * (me.getSceneY()-lineList.get(j).getStartY()))));
            if( Math.abs(Math.round((me.getSceneX()-lineList.get(j).getStartX())*(lineList.get(j).getEndY()-lineList.get(j).getStartY())) - Math.round((lineList.get(j).getEndX()-lineList.get(j).getStartX()) * (me.getSceneY()-lineList.get(j).getStartY())))<500 )

            {

                lineList.remove(lineList.get(j));
                increm=increm-1;
                System.out.println("delete");
                Refresh();}
        }


    }
    private Parent createZoomPane(final Group group) {   ////////////
        final double SCALE_DELTA = 1.2;
        final StackPane zoomPane = new StackPane();

        zoomPane.getChildren().add(group);


            zoomPane.setOnScroll(new EventHandler<ScrollEvent>() {
                @Override
                public void handle(ScrollEvent event) {
                    event.consume();
                    delta=event.getDeltaY();
                    System.out.println("delta"+delta);

                    if (event.getDeltaY() == 0) {
                        return;
                    }
                    double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA : 1 / SCALE_DELTA;

                if ((group.getScaleX() > 0.7)&&(group.getScaleX() < 4)){
                    group.setScaleX(group.getScaleX() * scaleFactor);
                    group.setScaleY(group.getScaleY() * scaleFactor);

                    zoom = group.getScaleX();

                    if (group.getScaleX() < 0.7){
                        group.setScaleX(group.getScaleX() * SCALE_DELTA);
                        group.setScaleY(group.getScaleY() * SCALE_DELTA);
                        zoom = group.getScaleX();
                    }else
                        if (group.getScaleX() > 4){
                            group.setScaleX(group.getScaleX() * (1/SCALE_DELTA));
                            group.setScaleY(group.getScaleY() * (1/SCALE_DELTA));
                            zoom = group.getScaleX();
                        }
                }
                   /* for (Vertex vert : vertexList) {
                        System.out.println("Vertex:" + vert.getX() + " " + vert.getY());
                    }*/


                }
            });


        zoomPane.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override public void changed(ObservableValue<? extends Bounds> observable, Bounds oldBounds, Bounds bounds) {
                zoomPane.setClip(new Rectangle(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight()));
            }
        });


        return zoomPane;
    }
    double RoundZoom(double d, int dz)
    {
        double dd=Math.pow(10,dz);
        return Math.round(d*dd)/dd;
    }




    public static void main(String[] args) {
        launch(args);
    }
}
