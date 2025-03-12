package com.itdfq.common.test;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dfq 2024/11/5 14:15
 * @implNote
 */
public class ImageTest1 {
    public static void main(String[] args) {
        // 加载OpenCV库
        nu.pattern.OpenCV.loadShared();

        // 读取图像
        String imagePath = "src/main/resources/size3.jpg";
        Mat image = Imgcodecs.imread(imagePath);

        // 检测表格
        boolean hasTable = detectTable(image);
        System.out.println("Image contains table: " + hasTable);
    }

    public static boolean detectTable(Mat image) {
        // 转换为灰度图像
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // 应用高斯滤波去噪
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(gray, blurred, new Size(5, 5), 0);

        // 使用Canny算法检测边缘
        Mat edges = new Mat();
        Imgproc.Canny(blurred, edges, 50, 150);

        // 查找直线
        Mat lines = new Mat();
        Imgproc.HoughLinesP(edges, lines, 1, Math.PI / 180, 50, 50, 10);

        // 分析直线，查找表格结构
        List<Double> angles = new ArrayList<>();
        for (int i = 0; i < lines.rows(); i++) {
            double[] line = lines.get(0, i);
            if (line == null) {
                continue;
            }
            double x1 = line[0], y1 = line[1], x2 = line[2], y2 = line[3];
            angles.add(Math.toDegrees(Math.atan2(y2 - y1, x2 - x1)));
        }

        // 判断是否存在两组垂直线
        boolean hasVerticalLines = false, hasHorizontalLines = false;
        for (double angle : angles) {
            if (angle > 80 && angle < 100) {
                hasVerticalLines = true;
            } else if (angle < 10 || angle > 170) {
                hasHorizontalLines = true;
            }
        }

        return hasVerticalLines && hasHorizontalLines;
    }

}
