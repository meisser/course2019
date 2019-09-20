/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.learning;

public class Matrix {

	public double[][] data;

	public Matrix(double... data) {
		this.data = new double[][] { data };
	}

	public Matrix(int size, boolean identity) {
		this(size, size);
		for (int i = 0; i < size; i++) {
			data[i][i] = 1.0;
		}
	}

	public Matrix(int width, int height) {
		this.data = new double[width][height];
	}

	public Matrix add(Matrix other) {
		Matrix result = new Matrix(getWidth(), getHeight());
		assert other.getWidth() == getWidth();
		assert other.getHeight() == getHeight();
		for (int i = 0; i < getWidth(); i++) {
			for (int j = 0; j < getHeight(); j++) {
				result.data[i][j] = this.data[i][j] + other.data[i][j];
			}
		}
		return result;
	}

	public Matrix subtract(Matrix other) {
		Matrix result = new Matrix(getWidth(), getHeight());
		assert other.getWidth() == getWidth();
		assert other.getHeight() == getHeight();
		for (int i = 0; i < getWidth(); i++) {
			for (int j = 0; j < getHeight(); j++) {
				result.data[i][j] = this.data[i][j] - other.data[i][j];
			}
		}
		return result;
	}

	public Matrix multiply(Matrix other) {
		Matrix result = new Matrix(other.getWidth(), getHeight());
		for (int resx = 0; resx < result.getWidth(); resx++) {
			for (int resy = 0; resy < result.getHeight(); resy++) {
				result.data[resx][resy] = multiply(other, resx, resy);
			}
		}
		return result;
	}

	public Matrix multiply(double factor) {
		Matrix result = new Matrix(getWidth(), getHeight());
		for (int i = 0; i < getWidth(); i++) {
			for (int j = 0; j < getHeight(); j++) {
				result.data[i][j] = this.data[i][j] * factor;
			}
		}
		return result;
	}

	private double multiply(Matrix columnSource, int column, int row) {
		double result = 0.0;
		double[] columnData = columnSource.data[column];
		for (int i = 0; i < columnData.length; i++) {
			result += columnData[i] * this.data[i][row];
		}
		return result;
	}

	public double get(int i, int j) {
		return this.data[i][j];
	}

	public int getWidth() {
		return data.length;
	}

	public int getHeight() {
		return data[0].length;
	}

	public Matrix transpose() {
		Matrix transposed = new Matrix(getHeight(), getWidth());
		for (int i = 0; i < this.data.length; i++) {
			double[] column = this.data[i];
			for (int j = 0; j < column.length; j++) {
				transposed.data[j][i] = column[j];
			}
		}
		return transposed;
	}

	@Override
	public String toString() {
		String s = null;
		for (int i = 0; i < getHeight(); i++) {
			String line = null;
			for (int j = 0; j < getWidth(); j++) {
				if (line == null) {
					line = this.data[j][i] + "";
				} else {
					line += "\t" + this.data[j][i];
				}
			}
			if (s == null){
				s = line;
			} else {
				s += "\n" + line;
			}
		}
		return s;
	}

	public double getSingleValue() {
		assert getHeight() == 1;
		assert getWidth() == 1;
		return data[0][0];
	}

	public static void main(String[] args) {
		Matrix m = new Matrix(1, 2, 3);
		System.out.println(m.multiply(m.transpose()));
		System.out.println(m.add(m));
	}

}
