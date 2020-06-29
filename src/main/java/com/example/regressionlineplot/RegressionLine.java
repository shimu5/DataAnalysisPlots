package com.example.regressionlineplot;


import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class RegressionLine {

	private RealMatrix b, estimatedValue, predictedY;
	
	public RegressionLine(double[][] xDataArray, double[][] yDataArray) throws  Exception {
		ApplyNormalizeEquation(MatrixUtils.createRealMatrix(xDataArray),MatrixUtils.createRealMatrix(yDataArray));
	}
	
	private void ApplyNormalizeEquation(RealMatrix xMatrix , RealMatrix yMatrix) throws Exception {
		LUDecomposition lowerUpperDecomposition = new LUDecomposition(xMatrix.transpose().multiply(xMatrix));
		if (lowerUpperDecomposition.getDeterminant() == 0.0) throw new Exception("Matrix is singular when not inversed");
		else
		{
			b = lowerUpperDecomposition.getSolver().getInverse().multiply(xMatrix.transpose().multiply(yMatrix));
		}
		estimatedValue = xMatrix.multiply(b);
		System.out.println("Predicted values - "+estimatedValue.toString());
		//System.out.println("Coefficients - "+b.toString()+b.);
	}

	public RealMatrix getb()
	{
		return b;
	}

	public RealMatrix getEstimatedValue()
	{
		return estimatedValue;
	}

	public RealMatrix PredictedValuesWithInteraction(double[][] x_arr){

		RealMatrix m = MatrixUtils.createRealMatrix(x_arr);
		predictedY = m.multiply(b);

		return predictedY;
	}

	public RealMatrix PredictedValuesWithoutInteraction(double[][] x_arr){
		RealMatrix m = MatrixUtils.createRealMatrix(x_arr);
		predictedY = m.multiply(b);
		return predictedY;
	}

	public RealMatrix getPredictedY()
	{
		return predictedY;
	}

}