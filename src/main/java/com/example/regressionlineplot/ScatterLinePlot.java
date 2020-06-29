package com.example.regressionlineplot;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class ScatterLinePlot extends Application {
    static RegressionLine regressionline;
    List<double[][]> DataArray;

    public static void main(String[] args) throws Exception {
        launch();
    }

    /* Prachi's code
    public static void passDataToRegressionAPI(double[][][] DataArray) throws Exception {

        double[][] Array_x = new double[DataArray.length][DataArray[0][0].length];
        double[][] Array_y = new double[DataArray.length][1];
        IntStream.range(0, DataArray.length).forEach(i -> {
            IntStream.range(0, DataArray[0][0].length).forEach(j -> Array_x[i][j] = DataArray[i][0][j]);

            Array_y[i][0] = DataArray[i][1][0];
            System.out.println(Array_y[i][0]);
        });
        regressionline = new RegressionLine(Array_x, Array_y);
    }*/

    public static void passDataToRegressionAPI(List<double[][]> DataSeries) throws Exception {
        int x_row = DataSeries.get(0).length;
        int x_col = DataSeries.size();

        double[][] Array_x = new double[x_row][(x_col+1)];
        double[][] Array_y = new double[x_row][1];

        for (int i = 0; i < DataSeries.size(); i++)
        {
            double[][] each_row= DataSeries.get(i);
            for (int j = 0; j <each_row.length; j++)
            {
                if(i==0){
                    Array_x[j][i] = 1;
                    Array_x[j][i+1] = each_row[j][0];
                }
                else{
                    Array_x[j][i+1] = each_row[j][0];
                }
                if(i==0)
                    Array_y[j][0] = each_row[j][1];
            }
        }
        regressionline = new RegressionLine(Array_x, Array_y);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        /*double[][][] DataArray = {{{1.0, 1.00}, {1.00}},
                {{1.0, 2.00}, {2.00}},
                {{1.0, 3.00}, {1.30}},
                {{1.0, 4.00}, {3.75}},
                {{1.0, 5.00}, {2.25}}};*/

        //When we have multiple X and and one Y
        double[][] data = {
                {25,820},{10,980},{30,930},{60,1230},{40,800},{70,1210},{53,1380},{80,1400},{65,1350},{85,1430},{120,1400}
        };

        double[][] data2 = {
                {35,820},{11,980},{40,930},{80,1230},{40,800},{71,1210},{50,1380},{84,1400},{67,1350},{90,1430},{100,1400}

        };

        /*double[][] data3 = {
                {37,820},{90,980},{50,930},{90,1230},{50,800},{72,1210},{52,1380},{85,1400},{66,1350},{89,1430},{130,1400}
        };*/

        //DataArray = Arrays.asList(data,data2,data3);
        DataArray = Arrays.asList(data,data2);
        //ScatterChart<Number, Number> numberNumberScatterChart = scatterChart(data);
        ScatterChart<Number, Number> numberNumberScatterChart = multipleSeriesScatterChart(DataArray);
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(numberNumberScatterChart);
        primaryStage.setScene(new Scene(anchorPane));
        primaryStage.show();

        passDataToRegressionAPI(DataArray);


/**
        passDataToRegressionAPI(DataArray);
        Pane pane = new Pane();
        pane.getChildren().addAll(createRegressionScatterChart(DataArray), createRegressionLineChart(DataArray));
        primaryStage.setScene(new Scene(pane, 500, 400));
        primaryStage.setTitle("Linear Regression");
        primaryStage.show();

 **/
    }

    // Regression plot has scatter plot over which the regression line is fitted
    // This function creates the scatter plot of the data
   /* public ScatterChart<Number, Number> createRegressionScatterChart(double[][][] DataArray) {
        XYChart.Series<Number, Number>scatterplotseries = new XYChart.Series<Number, Number>();
        NumberAxis xAxisName = new NumberAxis();
        xAxisName.setLabel("X-axis");
        NumberAxis yAxisName = new NumberAxis();
        yAxisName.setLabel("Y-axis");

        IntStream.range(0,DataArray.length).forEach(i ->{
            scatterplotseries.getData().add(new XYChart.Data<Number, Number>(DataArray[i][0][1],DataArray[i][1][0]));
            //System.out.println(DataArray[i][1][0]);
        });

        ScatterChart<Number, Number> scatterplot = new ScatterChart<>(xAxisName, yAxisName);
        scatterplot.getData().add(scatterplotseries);
        scatterplot.setOpacity(2.5);
        return scatterplot;
    }

    public ScatterChart<Number, Number> scatterChart(double[][] DataArray) {
        XYChart.Series<Number, Number>scatterplotseries = new XYChart.Series<Number, Number>();
        NumberAxis xAxisName = new NumberAxis();
        xAxisName.setLabel("X-axis");
        NumberAxis yAxisName = new NumberAxis();
        yAxisName.setLabel("Y-axis");




        XYChart.Series series = new XYChart.Series();
        ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
        for (int row = 0; row < DataArray.length; row++) {
            XYChart.Data rowdata = new XYChart.Data();
            rowdata.setYValue(DataArray[row][0]);
            rowdata.setXValue(DataArray[row][1]);
            data.add(rowdata);

        }
        series.setData(data);


        ScatterChart<Number, Number> scatterplot = new ScatterChart<>(xAxisName, yAxisName);
        //ObservableList<XYChart.Series<Number,Number>> sdata = FXCollections.observableArrayList();
        //sdata.add(series);
        scatterplot.getData().add(series);
       // scatterplot.setData(sdata);
        scatterplot.setOpacity(2.5);
        return scatterplot;
    }*/


    public ScatterChart<Number, Number> multipleSeriesScatterChart(List<double[][]> DataArray) {
        XYChart.Series<Number, Number>scatterplotseries = new XYChart.Series<Number, Number>();
        NumberAxis xAxisName = new NumberAxis();
        xAxisName.setLabel("X-axis");
        NumberAxis yAxisName = new NumberAxis();
        yAxisName.setLabel("Y-axis");


        ObservableList<XYChart.Series<Number,Number>> seriesList = FXCollections.observableArrayList();
        DataArray.forEach(f->{
            ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
            XYChart.Series series = new XYChart.Series();
            for (int row = 0; row < f.length; row++) {
                XYChart.Data rowdata = new XYChart.Data();
                rowdata.setYValue(f[row][0]);
                rowdata.setXValue(f[row][1]);
                data.add(rowdata);
            }
            series.setData(data);
            seriesList.add(series);

        });



        ScatterChart<Number, Number> scatterplot = new ScatterChart<>(xAxisName, yAxisName);
        //ObservableList<XYChart.Series<Number,Number>> sdata = FXCollections.observableArrayList();
        //sdata.add(series);
        scatterplot.setData(seriesList);
       // scatterplot.setData(sdata);
        scatterplot.setOpacity(2.5);
        return scatterplot;
    }

    // This function gets the fitted points from regressionline class and draws
    // a fitted line on the scatter plot.
    public LineChart<Number, Number> createRegressionLineChart(double[][][] DataArray) {
        XYChart.Series<Number, Number>regressionlineseries = new XYChart.Series<Number, Number>();
        NumberAxis xAxisName = new NumberAxis();
        xAxisName.setLabel("X-axis");
        NumberAxis yAxisName = new NumberAxis();
        yAxisName.setLabel("Y-axis");
        System.out.println("-"+regressionline.getEstimatedValue());

        IntStream.range(0,DataArray.length).forEach(i ->
                regressionlineseries.getData().add(new XYChart.Data<Number, Number>(DataArray[i][0][1],regressionline.getEstimatedValue().getEntry(i, 0))));
        LineChart<Number, Number> lineplot = new LineChart<>(xAxisName, yAxisName);
        for(int i=0;i<DataArray.length;i++){
            System.out.println("x,y="+DataArray[i][0][1]+", "+regressionline.getEstimatedValue().getEntry(i, 0));
        }
        lineplot.getData().add(regressionlineseries);
        lineplot.setOpacity(0.4);
        return lineplot;
    }

}
