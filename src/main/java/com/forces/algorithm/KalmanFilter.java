package com.forces.algorithm;

public class KalmanFilter {
    
    // מטריצות המצב
    private double[][] state;           // [x, y, vx, vy] - מיקום ומהירות
    private double[][] covariance;      // מטריצת אי-וודאות
    private double[][] processNoise;    // רעש תהליך
    private double[][] measurementNoise; // רעש מדידה

    public KalmanFilter() {
        // אתחול המצב הראשוני
        state = new double[4][1];  // x, y, vx, vy
        
        // מטריצת אי-וודאות ראשונית
        covariance = new double[][] {
            {1000, 0, 0, 0},
            {0, 1000, 0, 0},
            {0, 0, 1000, 0},
            {0, 0, 0, 1000}
        };
        
        // רעש תהליך (כמה אנחנו בטוחים במודל)
        processNoise = new double[][] {
            {0.1, 0, 0, 0},
            {0, 0.1, 0, 0},
            {0, 0, 0.5, 0},
            {0, 0, 0, 0.5}
        };
        
        // רעש מדידה (כמה אנחנו בטוחים ב-GPS)
        measurementNoise = new double[][] {
            {10, 0},
            {0, 10}
        };
    }

    // עדכון עם מדידה חדשה
    public void update(double latitude, double longitude, double deltaTime) {
        // 1. Prediction Step
        predict(deltaTime);
        
        // 2. Measurement Update
        double[][] measurement = {{latitude}, {longitude}};
        correct(measurement);
    }

    // שלב חיזוי
    private void predict(double dt) {
        // מטריצת מעבר מצב: x(t+1) = F * x(t)
        // x_new = x + vx * dt
        // y_new = y + vy * dt
        // vx_new = vx
        // vy_new = vy
        
        double[][] F = {
            {1, 0, dt, 0},
            {0, 1, 0, dt},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
        };
        
        // חיזוי המצב: x = F * x
        state = matrixMultiply(F, state);
        
        // חיזוי אי-וודאות: P = F * P * F^T + Q
        double[][] FP = matrixMultiply(F, covariance);
        double[][] FPFt = matrixMultiply(FP, transpose(F));
        covariance = matrixAdd(FPFt, processNoise);
    }

    // שלב תיקון
    private void correct(double[][] measurement) {
        // מטריצת מדידה: z = H * x
        // אנחנו מודדים רק את המיקום (לא את המהירות)
        double[][] H = {
            {1, 0, 0, 0},
            {0, 1, 0, 0}
        };
        
        // חישוב Kalman Gain: K = P * H^T * (H * P * H^T + R)^-1
        double[][] PHt = matrixMultiply(covariance, transpose(H));
        double[][] HPHt = matrixMultiply(matrixMultiply(H, covariance), transpose(H));
        double[][] S = matrixAdd(HPHt, measurementNoise);
        double[][] K = matrixMultiply(PHt, inverse2x2(S));
        
        // עדכון המצב: x = x + K * (z - H * x)
        double[][] Hx = matrixMultiply(H, state);
        double[][] innovation = matrixSubtract(measurement, Hx);
        double[][] Ky = matrixMultiply(K, innovation);
        state = matrixAdd(state, Ky);
        
        // עדכון אי-וודאות: P = (I - K * H) * P
        double[][] I = identity(4);
        double[][] KH = matrixMultiply(K, H);
        double[][] IKH = matrixSubtract(I, KH);
        covariance = matrixMultiply(IKH, covariance);
    }

    // חיזוי מיקום עתידי
    public double[] predictFuturePosition(double secondsAhead) {
        // x_future = x_current + vx * time
        // y_future = y_current + vy * time
        
        double x = state[0][0];
        double y = state[1][0];
        double vx = state[2][0];
        double vy = state[3][0];
        
        double futureX = x + vx * secondsAhead;
        double futureY = y + vy * secondsAhead;
        
        return new double[] {futureX, futureY};
    }

    // קבלת המצב הנוכחי
    public double[] getState() {
        return new double[] {
            state[0][0],  // latitude
            state[1][0],  // longitude
            state[2][0],  // velocity_lat
            state[3][0]   // velocity_lon
        };
    }

    // פעולות מטריצות
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
        // הופכי מטריצה 2x2
        double a = matrix[0][0];
        double b = matrix[0][1];
        double c = matrix[1][0];
        double d = matrix[1][1];
        
        double det = a * d - b * c;
        
        if (Math.abs(det) < 1e-10) {
            // מטריצה סינגולרית - החזר מטריצת זהות
            return identity(2);
        }
        
        return new double[][] {
            {d / det, -b / det},
            {-c / det, a / det}
        };
    }
}
