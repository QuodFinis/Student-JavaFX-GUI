package com.example.studentdatabase;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;

public class StudentsDatabaseApp extends Application {
    Integer N;
    Integer M;

    double startAngle;
    String Title, filename, URL;
    Scanner input;
    Boolean isPiechart;

    List<String> piechartInputs = new ArrayList();
    List<String> sqlInputs = new ArrayList();

    public VBox addLeftVBox(double widthLeftCanvas, double heightLeftCanvas, TilePane TP, MyColor color){
        VBox VB = new VBox();
        VB.setPrefWidth(widthLeftCanvas);
        VB.setPadding(new Insets(5));

        Label lblMyColorPalette = new Label("MyColor Palette");
        lblMyColorPalette.setPrefWidth(widthLeftCanvas);
        lblMyColorPalette.setTextFill(MyColor.WHITE.getJavaFXColor());
        lblMyColorPalette.setBackground(new Background(new BackgroundFill(
                Optional.ofNullable(color).orElse(MyColor.GREY).getJavaFXColor(), CornerRadii.EMPTY, Insets.EMPTY)));

        VB.getChildren().addAll(lblMyColorPalette, TP);

        return VB;
    }

    public HBox addTopHBox(double widthTopCanvas, double heightTopCanvas, double widthCenterCanvas,
                           double heightCenterCanvas, double widthRightCanvas, BorderPane BP, MyColorPalette CP, TilePane TP)
            throws FileNotFoundException {

        HBox HB = new HBox();
        HB.setPrefWidth(widthTopCanvas);
        HB.setPadding(new Insets(5, 5, 5, 5));

        String [] nameImages = new String [] { "rectangle", "circle", "intersection", "piechart", "SQL"};
        String pathFile = "C:\\Users\\mehed\\Documents\\School\\Sem4 MK3\\Software Design Lab\\Frequency\\src\\main\\resources\\com\\example\\frequency\\";

        Deque<MyShape> stackMyShapes = new ArrayDeque<MyShape>();
        for (String nameImage : nameImages){
            String nameFile = pathFile + nameImage + ".jpg";
            ImageView geometricImage = new ImageView(new Image(new FileInputStream(nameFile), heightTopCanvas, heightTopCanvas,true,false));

            geometricImage.setOnMouseClicked(e -> {
                switch (nameImage) {
                    case "rectangle":
                        dialogRectangle(widthCenterCanvas, heightCenterCanvas, BP, CP, TP, stackMyShapes);
                        break;

                    case "circle":
                        dialogCircle(widthCenterCanvas, heightCenterCanvas, BP, CP, TP, stackMyShapes);
                        break;

                    case "intersection":
                        dialogIntersection(widthCenterCanvas, heightCenterCanvas, BP, CP, TP, stackMyShapes);
                        break;

                    case "piechart":
                        dialogBookPiechart(widthCenterCanvas, heightCenterCanvas, widthRightCanvas, BP);
                        break;

                    case "SQL":
                        dialogSQL(widthCenterCanvas, heightCenterCanvas, widthRightCanvas, BP);
                }
            });
            HB.getChildren().add(geometricImage);
        }
        return HB;
    }

    public static Canvas addCenterCanvas(double widthCenterCanvas, double heightCenterCanvas,
                                         MyShape S1, MyShape S2, MyColor color){
        return S1.drawIntersectMyShapes(widthCenterCanvas, heightCenterCanvas, S1, S2, color);
    }

    public void dialogRectangle(double widthCenterCanvas, double heightCenterCanvas, BorderPane BP, MyColorPalette CP,
                                TilePane TP, Deque<MyShape> stackMyShapes) {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("My Rectangle");
        dialog.setHeaderText(null);


        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);


        GridPane gridDialog = new GridPane();
        gridDialog.setHgap(10);
        gridDialog.setVgap(10);
        gridDialog.setPadding(new Insets(20, 100, 10, 10));

        TextField xPTLC = new TextField();
        TextField yPTLC = new TextField();
        TextField width = new TextField();
        TextField height = new TextField();

        gridDialog.add(new Label("Top Left Corner Point"), 0, 0);
        gridDialog.add(xPTLC, 1, 0);
        gridDialog.add(new Label("X-Coordinate as fraction of canvas width"), 2, 0);
        gridDialog.add(yPTLC, 1, 1);
        gridDialog.add(new Label("Y-Coordinate as fraction of canvas height"), 2, 1);
        gridDialog.add(new Label("Width"), 0, 2);
        gridDialog.add(width, 1, 2);
        gridDialog.add(new Label("Width as fraction of canvas width"), 2, 2);
        gridDialog.add(new Label("Height"), 0, 3);
        gridDialog.add(height, 1, 3);
        gridDialog.add(new Label("Height as fraction of canvas height"), 2, 3);

        dialog.getDialogPane().setContent(gridDialog);


        Platform.runLater(() -> xPTLC.requestFocus());


        List<String> geometricImageInputs =  new ArrayList();
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                geometricImageInputs.add(xPTLC.getText());
                geometricImageInputs.add(yPTLC.getText());
                geometricImageInputs.add(width.getText());
                geometricImageInputs.add(height.getText());
                return geometricImageInputs;
            }
            return null;
        });

        Optional<List<String>> Result = dialog.showAndWait();
        Canvas CV = new Canvas(widthCenterCanvas, heightCenterCanvas);
        GraphicsContext GC = CV.getGraphicsContext2D();

        Result.ifPresent(event -> {
            MyPoint pTLC = new MyPoint(Double.parseDouble(geometricImageInputs.get(0)) * widthCenterCanvas,
                    Double.parseDouble(geometricImageInputs.get(1)) * heightCenterCanvas, null);
            double w = Double.parseDouble(geometricImageInputs.get(2)) * widthCenterCanvas;
            double h = Double.parseDouble(geometricImageInputs.get(3)) * heightCenterCanvas;


            TP.setOnMouseClicked(e -> {

                MyColor color = CP.getColorPicked();
                MyRectangle R = new MyRectangle(pTLC, w, h, color);

                GC.clearRect(0, 0, widthCenterCanvas, heightCenterCanvas);
                R.draw(GC);
                R.getMyBoundingRectangle().stroke(GC);

                stackMyShapes.push(R);
            });
            BP.setCenter(CV);
        });
    }

    public void dialogCircle(double widthCenterCanvas, double heightCenterCanvas, BorderPane BP, MyColorPalette CP,
                             TilePane TP, Deque<MyShape> stackMyShapes) {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("My Circle");
        dialog.setHeaderText(null);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane gridDialog = new GridPane();
        gridDialog.setHgap(10);
        gridDialog.setVgap(10);
        gridDialog.setPadding(new Insets(20, 100, 10, 10));

        TextField xCenter = new TextField();
        TextField yCenter = new TextField();
        TextField radius = new TextField();

        gridDialog.add(new Label("Center"), 0, 0);
        gridDialog.add(xCenter, 1, 0);
        gridDialog.add(new Label("X-Coordinate as fraction of canvas width"), 2, 0);
        gridDialog.add(yCenter, 1, 1);
        gridDialog.add(new Label("Y-Coordinate as fraction of canvas height"), 2, 1);
        gridDialog.add(new Label("Radius"), 0, 2);
        gridDialog.add(radius, 1, 2);
        gridDialog.add(new Label("Radius as fraction of canvas height"), 2, 2);

        dialog.getDialogPane().setContent(gridDialog);

        Platform.runLater(() -> xCenter.requestFocus());

        List<String> geometricImageInputs =  new ArrayList();
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                geometricImageInputs.add(xCenter.getText());
                geometricImageInputs.add(yCenter.getText());
                geometricImageInputs.add(radius.getText());

                return geometricImageInputs;
            }
            return null;
        });

        Optional<List<String>> Result = dialog.showAndWait();
        Canvas CV = new Canvas(widthCenterCanvas, heightCenterCanvas);
        GraphicsContext GC = CV.getGraphicsContext2D();

        Result.ifPresent(event -> {
            MyPoint pTLC = new MyPoint(Double.parseDouble(geometricImageInputs.get(0)) * widthCenterCanvas,
                    Double.parseDouble(geometricImageInputs.get(1)) * heightCenterCanvas, null);
            double rad = Double.parseDouble(geometricImageInputs.get(2)) * heightCenterCanvas;

            TP.setOnMouseClicked(e -> {
                MyColor color = CP.getColorPicked();
                MyCircle C = new MyCircle(pTLC, rad, color);

                GC.clearRect(0, 0, widthCenterCanvas, heightCenterCanvas);
                C.draw(GC);
                C.getMyBoundingRectangle().stroke(GC);

                stackMyShapes.push(C);
            });
            BP.setCenter(CV);
        });
    }

    public void dialogIntersection(double widthCenterCanvas, double heightCenterCanvas, BorderPane BP,
                                   MyColorPalette CP, TilePane TP, Deque<MyShape> stackMyShapes) {
        Dialog dialog = new Dialog<>();
        dialog.setTitle("Intersection of 2 MyShape Objects");
        dialog.setHeaderText(null);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane gridDialog = new GridPane();
        gridDialog.setHgap(10);
        gridDialog.setVgap(10);
        gridDialog.setPadding(new Insets(20, 100, 10, 10));

        gridDialog.add(new Label("Draw the intersection of the last two MyShape objects"), 0, 0);

        dialog.getDialogPane().setContent(gridDialog);
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                TP.setOnMouseClicked(e -> {
                    MyColor color = CP.getColorPicked();
                    String tileId = color.toString();

                    for (Node tile : TP.getChildren()) {
                        if (tile.getId() == tileId) {
                            Pane centerPane = new Pane();

                            if (stackMyShapes.size() == 0) {
                                break;
                            }
                            MyShape S1 = stackMyShapes.pop();
                            MyShape S2 = stackMyShapes.pop();
                            centerPane.getChildren().add(addCenterCanvas(widthCenterCanvas, heightCenterCanvas,
                                    S1, S2, color));
                            BP.setCenter(centerPane);
                            break;
                        }
                    }
                });
            }
        });
    }

    public void dialogBookPiechart(double widthCenterCanvas, double heightCenterCanvas, double widthRightCanvas, BorderPane BP) {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Pie Chart");
        dialog.setHeaderText(null);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane gridDialog = new GridPane();
        gridDialog.setHgap(10);
        gridDialog.setVgap(10);
        gridDialog.setPadding(new Insets(20, 150, 10, 10));

        TextField numberEvents = new TextField();
        TextField totalNumberEvents = new TextField();
        TextField startingAngle = new TextField();

        ComboBox title = new ComboBox();
        title.getItems().addAll("Moby Dick");

        gridDialog.add(new Label("Display"), 0, 0);
        gridDialog.add(numberEvents, 1, 0);
        gridDialog.add(new Label("Number of character slices to display"), 2, 0);
        gridDialog.add(new Label("Total"), 0, 1);
        gridDialog.add(totalNumberEvents, 1, 1);
        gridDialog.add(new Label("Max number of characters [26]"), 2, 1);
        gridDialog.add(new Label("Starting Angle"), 0, 2);
        gridDialog.add(startingAngle, 1, 2);
        gridDialog.add(new Label("Starting angle of first slice [in degrees]"), 2, 2);
        gridDialog.add(new Label("Title"), 0, 3);
        gridDialog.add(title, 1, 3);
        gridDialog.add(new Label("Title of book"), 2, 3);

        dialog.getDialogPane().setContent(gridDialog);

        Platform.runLater(() -> numberEvents.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == dialogButton.OK) {
                piechartInputs.add(numberEvents.getText());
                piechartInputs.add(totalNumberEvents.getText());
                piechartInputs.add(startingAngle.getText());
                piechartInputs.add(title.getValue().toString());

                return piechartInputs;
            }
            return null;
        });

        Optional<List<String>> Result = dialog.showAndWait();
        Canvas centerCanvas = new Canvas(widthCenterCanvas, heightCenterCanvas);
        GraphicsContext GC = centerCanvas.getGraphicsContext2D();

        Result.ifPresent(event -> {
            this.N = Integer.parseInt(piechartInputs.get(0));
            this.M = Integer.parseInt(piechartInputs.get(1));
            this.startAngle = Double.parseDouble(piechartInputs.get(2));
            this.Title = piechartInputs.get(3);
            this.filename = "C:\\Users\\mehed\\Documents\\School\\Sem4 MK3\\Software Design Lab\\Frequency\\src\\main\\resources\\com\\example\\frequency\\" + Title + ".txt";


            openFile();
            String w = readFile();
            closeFile();

            HistogramAlphaBet H = new HistogramAlphaBet(w);

            Canvas CV = addBookCanvasPieChart(widthCenterCanvas, heightCenterCanvas, H);
            BP.setAlignment(CV, Pos. TOP_CENTER); BP.setCenter(CV);
            BP.setCenter(CV);
            BP.setRight(addBookCanvasLegend(widthRightCanvas, heightCenterCanvas, H));
        });
    }

    public Canvas addBookCanvasPieChart(double widthCanvas, double heightCanvas, HistogramAlphaBet H){
        Canvas CV = new Canvas(widthCanvas, heightCanvas);
        GraphicsContext GC = CV.getGraphicsContext2D();

        MyPoint center = new MyPoint(0.5 * widthCanvas, 0.5 * heightCanvas, null);
        double diameterPieChart = 0.75 * Math.min(widthCanvas, heightCanvas);

        MyColor colorCanvas = MyColor.GREY;


        HistogramAlphaBet.MyPieChart pieChart = H.new MyPieChart(N, M, center, diameterPieChart, diameterPieChart, startAngle);


        System.out.println("\nPie Chart");
        pieChart.slices.forEach((K, V) -> System.out.println(K +  ": " + pieChart.slices.get(K)));


        double sumOfAngles = 0.0;
        for (Character key : pieChart.slices.keySet()){
            sumOfAngles += pieChart.slices.get(key).getArcAngle();
        }
        System.out.println("\nSum of Angles: " + sumOfAngles);


        pieChart.draw(GC);

        double xText = 5.0; double yText = 20.0;
        GC.setStroke(MyColor.LINEN.getJavaFXColor());
        GC.setFont(Font.font ("Georgia", FontPosture.ITALIC, 15));
        GC.strokeText(Title, xText, yText);

        return CV;
    }

    public Canvas addBookCanvasLegend(double widthCanvas, double heightCanvas, HistogramAlphaBet H){

        String information;

        Canvas CV = new Canvas(widthCanvas, heightCanvas);
        GraphicsContext GC = CV.getGraphicsContext2D();

        MyColor colorLeftCanvas = MyColor.LINEN;
        GC.setFill(colorLeftCanvas.getJavaFXColor());
        GC.fillRect(0.0, 0.0, widthCanvas, heightCanvas);

        double xText = 20; double yText = 0.03625 * heightCanvas;
        MyColor colorStroke = MyColor.GRAY;
        GC.setStroke(colorStroke.invertColor());
        GC.setFont(Font.font ("Calibri", 13));
        GC.strokeText("Frequency: Cumulative " + H.getCumulativeFrequency(), xText, yText);

        Map<Character, Integer> sortedFrequency = H.sortDownFrequency();

        double yStep = yText;
        for(Character K : sortedFrequency.keySet()){
            yText += yStep;
            information = K + ":\t" + sortedFrequency.get(K);
            GC.strokeText(information, xText, yText);
        }

        return CV;
    }

    public void dialogSQLPiechart(double widthCenterCanvas, double heightCenterCanvas, double widthRightCanvas, HistogramAlphaBet H, BorderPane BP) {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Pie Chart");
        dialog.setHeaderText(null);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane gridDialog = new GridPane();
        gridDialog.setHgap(10);
        gridDialog.setVgap(10);
        gridDialog.setPadding(new Insets(20, 150, 10, 10));

        TextField numberEvents = new TextField();
        TextField totalNumberEvents = new TextField();
        TextField startingAngle = new TextField();

        ComboBox title = new ComboBox();
        title.getItems().addAll("CSC 22100 Spring 2021");
        title.setValue("CSC 22100 Spring 2021");

        gridDialog.add(new Label("Display"), 0, 0);
        gridDialog.add(numberEvents, 1, 0);
        gridDialog.add(new Label("Total"), 2, 0);
        gridDialog.add(totalNumberEvents, 3, 0);
        gridDialog.add(new Label("StartingAngle"), 0, 1);
        gridDialog.add(startingAngle, 1, 1);
        gridDialog.add(new Label("Title"), 0, 2);
        gridDialog.add(title, 1, 2);

        dialog.getDialogPane().setContent(gridDialog);

        Platform.runLater(() -> numberEvents.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                sqlInputs.add(numberEvents.getText());
                sqlInputs.add(totalNumberEvents.getText());
                sqlInputs.add(startingAngle.getText());
                sqlInputs.add(title.getValue().toString());
                return sqlInputs;
            }
            return null;
        });

        Optional<List<String>> Result = dialog.showAndWait();
        Canvas centerCanvas = new Canvas(widthCenterCanvas, heightCenterCanvas);
        GraphicsContext GC = centerCanvas.getGraphicsContext2D();

        Result.ifPresent(event -> {
            this.N = Integer.parseInt(sqlInputs.get(4));
            this.M = Integer.parseInt(sqlInputs.get(5));
            this.startAngle = Double.parseDouble(sqlInputs.get(6));
            this.Title = sqlInputs.get(7);

            Canvas CV = addSQLCanvasPieChart(widthCenterCanvas, heightCenterCanvas, H);
            BP.setAlignment(CV, Pos. TOP_CENTER); BP.setCenter(CV);
            BP.setCenter(CV);
            BP.setRight(addSQLCanvasLegend(widthRightCanvas, heightCenterCanvas, H));
        });


    }

    public Canvas addSQLCanvasPieChart(double widthCanvas, double heightCanvas, HistogramAlphaBet H) {

        Canvas CV = new Canvas(widthCanvas, heightCanvas);
        GraphicsContext GC = CV.getGraphicsContext2D();

        // Center point of the canvas
        MyPoint center = new MyPoint(0.5 * widthCanvas, 0.5 * heightCanvas, null);

        // Build the pie chart
        HistogramAlphaBet.MyPieChart pieChart = H.new MyPieChart(N, M, center, 0.5 * widthCanvas,
                0.5 * heightCanvas, startAngle);
        Map<Character, Slice> slices = pieChart.getMyPieChart();

        // Output the pie chart
        System.out.println("\nPie Chart");
        slices.forEach((K, V) -> System.out.println(K + ": " + slices.get(K)));

        // Check for the sum of slice angles -- must be equal to 360.0
        double sumOfAngles = 0.0;
        for (Character key : slices.keySet()) {
            sumOfAngles += slices.get(key).getArcAngle();
        }
        System.out.println("\nSum of Angles: " + sumOfAngles);

        // Draw the pie chart
        pieChart.draw(GC);

        return CV;
    }

    public Canvas addSQLCanvasLegend(double widthCanvas, double heightCanvas, HistogramAlphaBet H){

        String information;

        Canvas CV = new Canvas(widthCanvas, heightCanvas);
        GraphicsContext GC = CV.getGraphicsContext2D();


        MyColor colorLeftCanvas = MyColor.LINEN;
        GC.setFill(colorLeftCanvas.getJavaFXColor());
        GC.fillRect(0.0, 0.0, widthCanvas, heightCanvas);


        double xText = 20; double yText = 0.03625 * heightCanvas;
        MyColor colorStroke = MyColor.GRAY;
        GC.setStroke(colorStroke.invertColor());
        GC.setFont(Font.font ("Calibri", 13));
        GC.strokeText("Frequency: Cumulative " + H.getCumulativeFrequency(), xText, yText);

        Map<Character, Integer> sortedFrequency = H.sortDownFrequency();

        double yStep = yText;
        for(Character K : sortedFrequency.keySet()){
            yText += yStep;
            information = K + ":\t" + sortedFrequency.get(K);
            GC.strokeText(information, xText, yText);
        }

        return CV;
    }

    public void dialogSQL(double widthCenterCanvas, double heightCenterCanvas, double widthRightCanvas, BorderPane BP) {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("SQL");
        dialog.setHeaderText(null);

        ToggleGroup groupSQL = new ToggleGroup();

        RadioButton radioMySQL = new RadioButton("MySQL");
        radioMySQL.setToggleGroup(groupSQL);

        TextField userName = new TextField();
        TextField passWord = new TextField();
        ComboBox<String> schema = new ComboBox<>();

        schema.getItems().addAll("Students");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane gridDialog = new GridPane();
        gridDialog.setHgap(10);
        gridDialog.setVgap(10);
        gridDialog.setPadding(new Insets(20, 150, 10, 10));

        gridDialog.add(new Label("DBMS"), 0, 1);
        gridDialog.add(radioMySQL, 1, 1);

        gridDialog.add(new Label("Schema"), 0, 2);
        gridDialog.add(schema, 1, 2);

        gridDialog.add(new Label("Username"), 0, 4);
        gridDialog.add(userName, 1, 4);

        gridDialog.add(new Label("Password"), 0, 5);
        gridDialog.add(passWord, 1, 5);

        dialog.getDialogPane().setContent(gridDialog);

        Platform.runLater(() -> radioMySQL.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String DBMS = ((RadioButton) groupSQL.getSelectedToggle()).getText();
                switch (DBMS) {
                    case "MySQL":
                        URL = "jdbc:mysql://localhost:3306/Students";
                        break;
                }
                System.out.println(URL);

                sqlInputs.add(URL);
                sqlInputs.add(userName.getText());
                sqlInputs.add(passWord.getText());
                sqlInputs.add(schema.getValue());
                return sqlInputs;
            }
            return null;
        });

        Optional<List<String>> Result = dialog.showAndWait();

        Result.ifPresent(event -> {
            String url = sqlInputs.get(0);
            String username = sqlInputs.get(1);
            String password = sqlInputs.get(2);

            System.out.println(url + " " + " " + password);

            String Schema = sqlInputs.get(3);
            this.Title = "Database: " + Schema;

            switch (Schema) {
                case "Students":
                    filename = "C:\\Users\\mehed\\Documents\\School\\Sem4 MK3\\Software Design Lab\\StudentDatabase\\src\\main\\resources\\com\\example\\studentdatabase\\Schedule.txt";
                    HistogramAlphaBet H = MySQLStudentsDatabase(url, username, password, filename);
                    dialogSQLPiechart(widthCenterCanvas, heightCenterCanvas, widthRightCanvas, H, BP);
                    break;
            }
        });
    }

    public HistogramAlphaBet MySQLStudentsDatabase(String url, String username, String password, String filename) {
        StudentsDatabase DB = new StudentsDatabase(url, username, password);

        String nameTable;
        nameTable = StudentsDatabaseInterface.SCHEMA + ".Schedule";
        try {
            StudentsDatabase.Schedule schedule = DB.new Schedule(filename, nameTable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String nameToTable = StudentsDatabaseInterface.SCHEMA + ".Courses";
        String nameFromTable = StudentsDatabaseInterface.SCHEMA + ".Schedule";
        try {
            StudentsDatabase.Courses courses = DB.new Courses(nameToTable, nameFromTable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        nameTable = StudentsDatabaseInterface.SCHEMA + ".Students";
        try {
            StudentsDatabase.Students students = DB.new Students(nameTable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        nameTable = StudentsDatabaseInterface.SCHEMA + ".Classes";
        try {
            StudentsDatabase.Classes classes = DB.new Classes(nameTable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        nameToTable = StudentsDatabaseInterface.SCHEMA + ".AggregateGrades";
        nameFromTable = StudentsDatabaseInterface.SCHEMA + ".Classes";
        StudentsDatabase.AggregateGrades aggregateGrades;
        try {
            aggregateGrades = DB.new AggregateGrades(nameToTable, nameFromTable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Map<Character, Integer> AG = aggregateGrades.getAggregateGrades(nameToTable);

        return new HistogramAlphaBet(AG);
    }

    public void openFile() {
        try {
            input = new Scanner(Paths.get(filename));
        }
        catch (IOException ioException) {
            System.err.println("File is not found");
        }
    }

    public String readFile() {
        String w = "";

        try {

            while (input.hasNext()) {
                w += input.nextLine().replaceAll("[^a-zA-Z]", "").toLowerCase();
            }
        }
        catch (NoSuchElementException elementException) {
            System.err.println("Invalid input! Terminating...");
        }
        catch (IllegalStateException stateException) {
            System.err.println("Error processing file! Terminating...");
        }
        return w;
    }

    public void closeFile() {
        if (input != null) input.close();
    }

    @Override
    public void start(Stage stage) throws FileNotFoundException {
        BorderPane BP = new BorderPane();

        double widthCanvas = 800.0;
        double heightCanvas = 600.0;

        double widthLeftCanvas = 0.3 * widthCanvas;
        double heightTopCanvas = 0.15 * heightCanvas;
        double widthRightCanvas = 0.25 * widthCanvas;

        double widthCenterCanvas = widthCanvas - widthLeftCanvas;
        double heightCenterCanvas = heightCanvas - heightTopCanvas;

        MyColorPalette CP = new MyColorPalette(widthLeftCanvas, heightCenterCanvas);
        TilePane TP = CP.getPalette();

        BP.setTop(addTopHBox(widthCanvas, heightTopCanvas, widthCenterCanvas, heightCenterCanvas, widthRightCanvas, BP, CP, TP));
        BP.setLeft(addLeftVBox(widthLeftCanvas, heightCenterCanvas, TP, MyColor.BLACK));

        Canvas centerCanvas = new Canvas(widthCenterCanvas, heightCenterCanvas);
        BP.setCenter(centerCanvas);

        Scene SC = new Scene(BP, widthCanvas, heightCanvas, null);
        stage.setTitle("MyShape!");
        stage.setScene(SC);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
