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

import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;


public class ScatterRegressLinePlotSingleXwithExtrapolation_v1_1 extends Application {
    //static RegressionLine regressionline;
    private static double min_X, max_X, min_Y, max_Y;
    List<double[][]> DataArray;
    static OLSMultipleLinearRegression regression;
    double[] coefficients;
    int series_col_x;
    List<Integer> predictor;
    double[] predictArr;
    int x_row, x_col;

    public static void main(String[] args) throws Exception {
        launch();
    }
    public void passDataToRegressionAPI(List<double[][]> DataSeries) throws Exception {
        x_row = DataSeries.get(0).length;
        x_col = DataSeries.size();

        double[][] Array_x = new double[x_row][x_col];
        double[] Array_y = new double[x_row];

        System.out.println(x_row+ "..."+x_col);
        for (int i = 0; i < DataSeries.size(); i++)
        {
            double[][] each_row= DataSeries.get(i);
            for (int j = 0; j <each_row.length; j++)
            {
                Array_x[j][i] = each_row[j][0];
                Array_y[j] = each_row[j][1];
            }
        }
       /* double[][] Array_x = new double[x_row][(x_col+1)];
        double[] Array_y = new double[x_row];

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
                 Array_y[j] = each_row[j][1];
                System.out.println(j+" - "+Array_y[j]);
            }
        }*/


        regression = new OLSMultipleLinearRegression();
        regression.newSampleData(Array_y, Array_x);
        //regression.setNoIntercept(true);
        //regression.newSampleData(Array_y, Array_x);

 // In the case of extrapolation of x, min and max bound will take from user Interface/user
        int extend_min_x = 0, extend_max_x=170, min_Y=0, step_x=10, step_y=50 ;

        min_X=extend_min_x;
        max_X=extend_max_x;

        extrapolateX(extend_min_x,extend_max_x,step_x);

        calculatePredictedFromX();


        /*Below line of code used to set min and max for X and Y axis Bound values
       // setMinX(Array_x);
      //  setMaxX(Array_x);*/
        DoubleSummaryStatistics stat = Arrays.stream(Array_y).summaryStatistics();
        double minY_Observed   = stat.getMin();
        double maxY_Observed = stat.getMax();
        DoubleSummaryStatistics stat1 = Arrays.stream(predictArr).summaryStatistics();
        double minY_predicted   = stat1.getMin();
        double maxY_predicted = stat1.getMax();
       // min_Y= (minY_Observed<minY_predicted)?minY_Observed:minY_predicted;
        max_Y= (maxY_Observed>maxY_predicted)?maxY_Observed:maxY_predicted;

    }

    public void extrapolateX(int extend_min_x,int extend_max_x,int step_x){
        predictor = iterateStream(extend_min_x, extend_max_x, step_x );
    }
    private List<Integer> iterateStream(int from, int limit,int step) {
        limit= limit+step;
        return IntStream.iterate(from, i -> i+step) // next int
                .limit(limit/step) // only numbers in range
                .boxed()
                .collect(toList());
    }

    public void calculatePredictedFromX(){
        coefficients = new double[2];
        coefficients = regression.estimateRegressionParameters();
        int psize= predictor.size();
        predictArr = new double[psize];
            for (int row = 0; row < psize; row++) {
                int x =predictor.get(row);
                double Y_pred= coefficients[0]+(x*coefficients[1] );
                predictArr[row] = Y_pred;
                System.out.println(predictArr[row]);
            }

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //When we have multiple X and and one Y
        double[][] data = {
                {25,820},{10,980},{30,930},{60,1230},{40,800},{70,1210},{53,1380},{80,1400},{65,1350},{85,1430},{120,1400}
        };

        List<double[][]> DataArray = new ArrayList<double[][]>();
        DataArray.add(data);


        passDataToRegressionAPI(DataArray);

        Pane pane = new Pane();
        pane.getChildren().addAll( multipleSeriesScatterChart(DataArray),createRegressionLineChart(DataArray));
        primaryStage.setScene(new Scene(pane, 500, 400));
        primaryStage.setTitle("Linear Regression");
        primaryStage.show();

    }

    public ScatterChart<Number, Number> multipleSeriesScatterChart(List<double[][]> DataArray) {

        NumberAxis xAxisName = new NumberAxis(min_X, max_X, 10);
        NumberAxis yAxisName = new NumberAxis(min_Y, max_Y, 50);

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
        NumberAxis xAxisName = new NumberAxis(min_X, max_X, 10);
        NumberAxis yAxisName = new NumberAxis(min_Y, max_Y, 50);

        ObservableList<XYChart.Series<Number,Number>> seriesList = FXCollections.observableArrayList();
        coefficients = new double[2];
        coefficients = regression.estimateRegressionParameters();

        series_col_x =1;


            ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
            XYChart.Series series = new XYChart.Series();
            for (int row = 0; row < predictor.size (); row++) {
                int x = predictor.get (row);
                XYChart.Data rowdata = new XYChart.Data();
                rowdata.setXValue(x);
                rowdata.setYValue(predictArr[row]);
                data.add(rowdata);
            }
            series.setData(data);
            seriesList.add(series);

        LineChart<Number, Number> lineplot = new LineChart<>(xAxisName, yAxisName);
        lineplot.setData(seriesList);
        lineplot.setOpacity(0.4);
        return lineplot;

    }


    public void setMinX(double[][] Array_x){

        double m = Array_x[0][0];
        for(int r=0;r<Array_x.length;r++){           //
            for(int k=0;k<Array_x[0].length;k++)        //
                if(Array_x[r][k] < m){           // finds a min value
                    m = Array_x[r][k];           //

                }
        }
        min_X = m;
    }

    public void setMaxX(double[][] Array_x){
        double m = Array_x[0][0];
        for(int r=0;r< Array_x.length; r++){           //
            for(int k=0;k< Array_x[0].length; k++)        //
                if(Array_x[r][k] > m){           // finds a max value
                    m = Array_x[r][k];           //
                }
        }
        max_X = m;
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
