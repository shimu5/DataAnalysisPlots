package com.example.regressionlineplot;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class ScatterRegressLinePlotMultipleX_v_2 extends Application {
    private static double min_X, max_X, Step_X, min_Y, max_Y, Step_Y;
    List<double[][]> DataArray;
    static OLSMultipleLinearRegression regression;
    double[] coefficients;
    List<Integer> observation;
    List<Double> predictArr;

    public static void main(String[] args) throws Exception {
        launch();
    }

    public static void passDataToRegressionAPI(List<double[][]> DataSeries) throws Exception {
        int x_row = DataSeries.get(0).length;
        int x_col = DataSeries.size();

        double[][] Array_x = new double[x_row][x_col];
        double[] Array_y = new double[x_row];

        for (int i = 0; i < DataSeries.size(); i++)
        {
            double[][] each_row= DataSeries.get(i);
            for (int j = 0; j <each_row.length; j++)
            {
                Array_x[j][i] = each_row[j][0];
                Array_y[j] = each_row[j][1];
            }
        }

        regression = new OLSMultipleLinearRegression();
        regression.newSampleData(Array_y, Array_x);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //When we have multiple X and and one Y
        //When we have multiple X and and one Y
        double[][] data = {
                {25,820},{10,980},{30,930},{60,1230},{40,800},{70,1210},{53,1380},{80,1400},{65,1350},{85,1430},{120,1400}
        };

        double[][] data2 = {
                {35,820},{11,980},{40,930},{80,1230},{40,800},{71,1210},{50,1380},{84,1400},{67,1350},{90,1430},{100,1400}

        };

        double[][] data3 = {
                {37,820},{90,980},{50,930},{90,1230},{50,800},{72,1210},{52,1380},{85,1400},{66,1350},{89,1430},{130,1400}
        };

       // List<double[][]> DataArray = new ArrayList<double[][]>();
        //DataArray.add(data);
        //DataArray.add(data2);
        DataArray = Arrays.asList(data,data2,data3);
        passDataToRegressionAPI(DataArray); //To find out coefficients


        int extend_min_x = 0, extend_max_x=170, step_x=50, step_y=50 ;
        min_X=extend_min_x; max_X=extend_max_x;  Step_X=step_x;
        CalcExtrapolatePredictValues(extend_min_x,extend_max_x,step_x);

        min_Y= getMin(predictArr);
        max_Y= getMax(predictArr);

        System.out.println(min_Y+" --- "+max_Y);

        Pane pane = new Pane();
        pane.getChildren().addAll( multipleSeriesScatterChart(DataArray),createRegressionLineChart(DataArray));
        primaryStage.setScene(new Scene(pane, 500, 400));
        primaryStage.setTitle("Linear Regression");
        primaryStage.show();

    }
    public void CalcExtrapolatePredictValues(int extend_min_x,int extend_max_x,int step_x){
        observation = iterateStream(extend_min_x, extend_max_x, step_x );
        predictArr = new ArrayList<>();
        coefficients = new double[regression.estimateRegressionParameters().length];
        coefficients = regression.estimateRegressionParameters();
        for (int valueX : observation) {
            double predictY = coefficients[0];
            for(int i=1; i<coefficients.length;i++){
                predictY = predictY + (coefficients[i] * valueX);
            }
            predictArr.add(predictY);
        }
    }


    private List<Integer> iterateStream(int from, int limit,int step) {
        limit= limit+step;
        return IntStream.iterate(from, i -> i+step) // next int
                .limit(limit/step) // only numbers in range
                .boxed()
                .collect(toList());
    }


    public ScatterChart<Number, Number> multipleSeriesScatterChart(List<double[][]> DataArray) {

        NumberAxis xAxisName = new NumberAxis(0, max_X, Step_X);
        NumberAxis yAxisName = new NumberAxis(0, 1800, 50);

        ObservableList<XYChart.Series<Number,Number>> seriesList = FXCollections.observableArrayList();
        DataArray.forEach(f->{
            ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
            XYChart.Series series = new XYChart.Series();
            for (int row = 0; row < f.length; row++) {
                XYChart.Data rowdata = new XYChart.Data();
                rowdata.setYValue(f[row][1]);
                rowdata.setXValue(f[row][0]);
                data.add(rowdata);

            }
            series.setData(data);
            seriesList.add(series);

        });

        ScatterChart<Number, Number> scatterplot = new ScatterChart<>(xAxisName, yAxisName);
        scatterplot.setData(seriesList);
        /// scatterplot.setOpacity(2.5);
        return scatterplot;
    }


    // This function gets the fitted points from regressionline class and draws
    // a fitted line on the scatter plot.
    public LineChart<Number, Number> createRegressionLineChart(List<double[][]> DataArray) {
        NumberAxis xAxisName = new NumberAxis(0, max_X,Step_X );
        NumberAxis yAxisName = new NumberAxis(0, 1800, 100);
        ObservableList<XYChart.Series<Number,Number>> seriesList = FXCollections.observableArrayList();

        DataArray.forEach(f->{
            ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
            XYChart.Series series = new XYChart.Series();
            System.out.println("observation size"+observation.size());
            for (int row = 0; row < observation.size(); row++) {
                XYChart.Data rowdata = new XYChart.Data();
                //System.out.println(observation.get(row)+" -- "+predictArr.get(row));
                rowdata.setXValue(observation.get(row));
                rowdata.setYValue(predictArr.get(row));
                data.add(rowdata);
            }

            series.setData(data);
            seriesList.add(series);

        });

        LineChart<Number, Number> lineplot = new LineChart<>(xAxisName, yAxisName);
        //lineplot.getData().add(seriesList);
        lineplot.setData(seriesList);
        lineplot.setOpacity(0.4);
        return lineplot;

    }

    public void setMinX(double[][] Array_x){

        System.out.println(Array_x[0].length+ "- "+ Array_x.length);
        double m = Array_x[0][0];
        for(int r=0;r<Array_x.length;r++){           //
            for(int k=0;k<Array_x[0].length;k++)        //
                if(Array_x[r][k] < m){           // finds a min value
                    m = Array_x[r][k];           //

                }
        }
        min_X = m;
    }

    static public double setMaxX(double[][] Array_x){
        double m = Array_x[0][0];
        for(int r=0;r< Array_x.length; r++){           //
            for(int k=0;k< Array_x[0].length; k++)        //
                if(Array_x[r][k] > m){           // finds a max value
                    m = Array_x[r][k];           //
                }
        }
        return m;
    }

    public double getMin(List<Double> arr) {
        double min = arr.get(0);
        for (double i : arr){
            min = min < i ? min : i;
        }
       return min;
    }

    public double getMax(List<Double> arr) {
        double max = arr.get(0);
        for (double i : arr){
            max = max > i ? max : i;
        }
        return max;
    }

}
