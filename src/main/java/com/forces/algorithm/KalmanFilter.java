package com.forces.algorithm;

public class KalmanFilter {
    
    private double[][] state;
    private double[][] covariance;
    private double[][] processNoise;
    private double[][] measurementNoise;

    public KalmanFilter() {
        state = new double[4][1];
        
        covariance = new double[][] {
            {1000, 0, 0, 0},
            {0, 1000, 0, 0},
            {0, 0, 1000, 0},
            {0, 0, 0, 1000}
        };
        
        processNoise = new double[][] {
            {0.1, 0, 0, 0},
            {0, 0.1, 0, 0},
            {0, 0, 0.5, 0},
            {0, 0, 0, 0.5}
        };
        
        measurementNoise = new double[][] {
            {10, 0},
            {0, 10}
        };
    }

    public void update(double latitude, double longitude, double deltaTime) {
        predict(deltaTime);
        double[][] measurement = {{latitude}, {longitude}};
        correct(measurement);
    }

    private void predict(double dt) {
        double[][] F = {
            {1, 0, dt, 0},
            {0, 1, 0, dt},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
        };
        
        state = matrixMultiply(F, state);
        double[][] FP = matrixMultiply(F, covariance);
        double[][] FPFt = matrixMultiply(FP, transpose(F));
        covariance = matrixAdd(FPFt, processNoise);
    }

    private void correct(double[][] measurement) {
        double[][] H = {
            {1, 0, 0, 0},
            {0, 1, 0, 0}
        };
        
        double[][] PHt = matrixMultiply(covariance, transpose(H));
        double[][] HPHt = matrixMultiply(matrixMultiply(H, covariance), transpose(H));
        double[][] S = matrixAdd(HPHt, measurementNoise);
        double[][] K = matrixMultiply(PHt, inverse2x2(S));
        
        double[][] Hx = matrixMultiply(H, state);
        double[][] innovation = matrixSubtract(measurement, Hx);
        double[][] Ky = matrixMultiply(K, innovation);
        state = matrixAdd(state, Ky);
        
        double[][] I = identity(4);
        double[][] KH = matrixMultiply(K, H);
        double[][] IKH = matrixSubtract(I, KH);
        covariance = matrixMultiply(IKH, covariance);
    }

    public double[] predictFuturePosition(double secondsAhead) {
        double x = state[0][0];
        double y = state[1][0];
        double vx = state[2][0];
        double vy = state[3][0];
        
        double futureX = x + vx * secondsAhead;
        double futureY = y + vy * secondsAhead;
        
        return new double[] {futureX, futureY};
    }

    public double[] getState() {
        return new double[] {
            state[0][0],
            state[1][0],
            state[2][0],
            state[3][0]
        };
    }

    private double[][] matrixMultiply(double[][] A, double[][] B) {
        int m = A.length;
        int n = B[0].length;
        int p = B.length;
        double[][] result = new double[m][n];
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < p; k++) {
                    result[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return result;
    }

    private double[][] matrixAdd(double[][] A, double[][] B) {
        int m = A.length;
        int n = A[0].length;
        double[][] result = new double[m][n];
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = A[i][j] + B[i][j];
            }
        }
        return result;
    }

    private double[][] matrixSubtract(double[][] A, double[][] B) {
        int m = A.length;
        int n = A[0].length;
        double[][] result = new double[m][n];
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = A[i][j] - B[i][j];
            }
        }
        return result;
    }

    private double[][] transpose(double[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;
        double[][] result = new double[n][m];
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                result[j][i] = matrix[i][j];
            }
        }
        return result;
    }

    private double[][] identity(int size) {
        double[][] result = new double[size][size];
        for (int i = 0; i < size; i++) {
            result[i][i] = 1.0;
        }
        return result;
    }

    private double[][] inverse2x2(double[][] matrix) {
        double a = matrix[0][0];
        double b = matrix[0][1];
        double c = matrix[1][0];
        double d = matrix[1][1];
        
        double det = a * d - b * c;
        
        if (Math.abs(det) < 1e-10) {
            return identity(2);
        }
        
        return new double[][] {
            {d / det, -b / det},
            {-c / det, a / det}
        };
    }
}