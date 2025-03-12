package com.itdfq.common.utils.image;

import com.itdfq.common.Exception.BizException;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 图片工具
 * @author dfq 2024/11/5 11:57
 * @implNote
 */
@Slf4j
public class ImageUtils {

    static {
        nu.pattern.OpenCV.loadLocally();
    }

    /**
     * 判断图片中是否有表格
     * @param path
     * @return
     */
    public static boolean haveTable(String path) {
//         // 读取图像
//         Mat image = Imgcodecs.imread(path, Imgcodecs.IMREAD_GRAYSCALE);
//         if (image.empty()) {
//             throw new BizException("Could not open or find the image!");
//         }
// // 二值化
//         Mat binaryImage = new Mat();
//         Imgproc.adaptiveThreshold(image, binaryImage, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);
//
//         // 寻找轮廓
//         List<MatOfPoint> contours = new ArrayList<>();
//         Mat hierarchy = new Mat();
//         Imgproc.findContours(binaryImage, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
//
//         // 筛选轮廓以确定表格
//         boolean tableDetected = false;
//         for (MatOfPoint contour : contours) {
//             Rect rect = Imgproc.boundingRect(contour);
//             double aspectRatio = (double) rect.width / rect.height;
//             if (aspectRatio > 0.8 && aspectRatio < 1.2 && rect.area() > 1000) {
//                 tableDetected = true;
//                 break;
//             }
//         }
//
//         if (tableDetected) {
//             log.info("img:[{}],Table detected in the image.", path);
//             return true;
//         } else {
//             log.info("img:[{}],No table detected in the image.", path);
//             return false;
//         }

        // 读取图片并转换为灰度图像
        Mat src = Imgcodecs.imread(path, Imgcodecs.IMREAD_GRAYSCALE);
        if (src.empty()) {
            System.err.println("无法读取图片: " + path);
            return false;
        }

        // 应用自适应阈值以获取二值图像
        Mat binary = new Mat();
        Imgproc.adaptiveThreshold(src, binary, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);

        // 检测水平线条
        Mat horizontal = binary.clone();
        int horizontalSize = horizontal.cols() / 30;
        Mat horizontalStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(horizontalSize, 1));
        Imgproc.erode(horizontal, horizontal, horizontalStructure);
        Imgproc.dilate(horizontal, horizontal, horizontalStructure);

        // 检测垂直线条
        Mat vertical = binary.clone();
        int verticalSize = vertical.rows() / 30;
        Mat verticalStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, verticalSize));
        Imgproc.erode(vertical, vertical, verticalStructure);
        Imgproc.dilate(vertical, vertical, verticalStructure);

        // 合并水平和垂直线条
        Mat mask = new Mat();
        Core.add(horizontal, vertical, mask);

        // 寻找轮廓
        List<MatOfPoint> contours = new java.util.ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // 检查是否找到任何轮廓
        boolean hasTable = !contours.isEmpty();

        // 释放资源
        src.release();
        binary.release();
        horizontal.release();
        vertical.release();
        mask.release();
        hierarchy.release();

        return hasTable;

    }

    public static void haveTable2(String path){

        Mat source_image = Imgcodecs.imread(path);
        //灰度处理
        Mat gray_image = new Mat(source_image.height(), source_image.width(), CvType.CV_8UC1);
        Imgproc.cvtColor(source_image,gray_image,Imgproc.COLOR_RGB2GRAY);

        //二值化
        Mat thresh_image = new Mat(source_image.height(), source_image.width(), CvType.CV_8UC1);
        // C 负数，取反色，超过阈值的为黑色，其他为白色
        Imgproc.adaptiveThreshold(gray_image, thresh_image,255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY,7,-2);

        //克隆一个 Mat，用于提取水平线
        Mat horizontal_image = thresh_image.clone();

        //克隆一个 Mat，用于提取垂直线
        Mat vertical_image = thresh_image.clone();

        /*
         * 求水平线
         * 1. 根据页面的列数（可以理解为宽度），将页面化成若干的扫描区域
         * 2. 根据扫描区域的宽度，创建一根水平线
         * 3. 通过腐蚀、膨胀，将满足条件的区域，用水平线勾画出来
         *
         * scale 越大，识别的线越多，因为，越大，页面划定的区域越小，在腐蚀后，多行文字会形成一个块，那么就会有一条线
         * 在识别表格时，我们可以理解线是从页面左边 到 页面右边的，那么划定的区域越小，满足的条件越少，线条也更准确
         */
        int scale = 10;
        int horizontalsize = horizontal_image.cols() / scale;
        // 为了获取横向的表格线，设置腐蚀和膨胀的操作区域为一个比较大的横向直条
        Mat horizontalStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(horizontalsize, 1));
        // 先腐蚀再膨胀 new Point(-1, -1) 以中心原点开始
        // iterations 最后一个参数，迭代次数，越多，线越多。在页面清晰的情况下1次即可。
        Imgproc.erode(horizontal_image, horizontal_image, horizontalStructure, new Point(-1, -1),1);
        Imgproc.dilate(horizontal_image, horizontal_image, horizontalStructure, new Point(-1, -1),1);
        // saveImage("out-table/2-horizontal.png",horizontal_image);

        // 求垂直线
        scale = 30;
        int verticalsize = vertical_image.rows() / scale;
        Mat verticalStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, verticalsize));
        Imgproc.erode(vertical_image, vertical_image, verticalStructure, new Point(-1, -1),1);
        Imgproc.dilate(vertical_image, vertical_image, verticalStructure, new Point(-1, -1),1);
        // saveImage("out-table/3-vertical.png",vertical_image);

        /*
         * 合并线条
         * 将垂直线，水平线合并为一张图
         */
        Mat mask_image = new Mat();
        Core.add(horizontal_image,vertical_image,mask_image);
        // saveImage("out-table/4-mask.png",mask_image);

        /*
         * 通过 bitwise_and 定位横线、垂直线交汇的点
         */
        Mat points_image = new Mat();
        Core.bitwise_and(horizontal_image, vertical_image, points_image);
        // saveImage("out-table/5-points.png",points_image);

        /*
         * 通过 findContours 找轮廓
         *
         * 第一个参数，是输入图像，图像的格式是8位单通道的图像，并且被解析为二值图像（即图中的所有非零像素之间都是相等的）。
         * 第二个参数，是一个 MatOfPoint 数组，在多数实际的操作中即是STL vectors的STL vector，这里将使用找到的轮廓的列表进行填充（即，这将是一个contours的vector,其中contours[i]表示一个特定的轮廓，这样，contours[i][j]将表示contour[i]的一个特定的端点）。
         * 第三个参数，hierarchy，这个参数可以指定，也可以不指定。如果指定的话，输出hierarchy，将会描述输出轮廓树的结构信息。0号元素表示下一个轮廓（同一层级）；1号元素表示前一个轮廓（同一层级）；2号元素表示第一个子轮廓（下一层级）；3号元素表示父轮廓（上一层级）
         * 第四个参数，轮廓的模式，将会告诉OpenCV你想用何种方式来对轮廓进行提取，有四个可选的值：
         *      CV_RETR_EXTERNAL （0）：表示只提取最外面的轮廓；
         *      CV_RETR_LIST （1）：表示提取所有轮廓并将其放入列表；
         *      CV_RETR_CCOMP （2）:表示提取所有轮廓并将组织成一个两层结构，其中顶层轮廓是外部轮廓，第二层轮廓是“洞”的轮廓；
         *      CV_RETR_TREE （3）：表示提取所有轮廓并组织成轮廓嵌套的完整层级结构。
         * 第五个参数，见识方法，即轮廓如何呈现的方法，有三种可选的方法：
         *      CV_CHAIN_APPROX_NONE （1）：将轮廓中的所有点的编码转换成点；
         *      CV_CHAIN_APPROX_SIMPLE （2）：压缩水平、垂直和对角直线段，仅保留它们的端点；
         *      CV_CHAIN_APPROX_TC89_L1  （3）or CV_CHAIN_APPROX_TC89_KCOS（4）：应用Teh-Chin链近似算法中的一种风格
         * 第六个参数，偏移，可选，如果是定，那么返回的轮廓中的所有点均作指定量的偏移
         */
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask_image,contours,hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE,new Point(0,0));


        List<MatOfPoint> contours_poly = contours;
        Rect[] boundRect = new Rect[contours.size()];

        LinkedList<Mat> tables = new LinkedList<Mat>();

        //循环所有找到的轮廓-点
        for(int i=0 ; i< contours.size(); i++){

            MatOfPoint point = contours.get(i);
            MatOfPoint contours_poly_point = contours_poly.get(i);

            /*
             * 获取区域的面积
             * 第一个参数，InputArray contour：输入的点，一般是图像的轮廓点
             * 第二个参数，bool oriented = false:表示某一个方向上轮廓的的面积值，顺时针或者逆时针，一般选择默认false
             */
            double area = Imgproc.contourArea(contours.get(i));
            //如果小于某个值就忽略，代表是杂线不是表格
            if(area < 20){
                System.out.println("不是表格,面积："+area);
                break;
            }


            /*
             * approxPolyDP 函数用来逼近区域成为一个形状，true值表示产生的区域为闭合区域。比如一个带点幅度的曲线，变成折线
             *
             * MatOfPoint2f curve：像素点的数组数据。
             * MatOfPoint2f approxCurve：输出像素点转换后数组数据。
             * double epsilon：判断点到相对应的line segment 的距离的阈值。（距离大于此阈值则舍弃，小于此阈值则保留，epsilon越小，折线的形状越“接近”曲线。）
             * bool closed：曲线是否闭合的标志位。
             */
            Imgproc.approxPolyDP(new MatOfPoint2f(point.toArray()),new MatOfPoint2f(contours_poly_point.toArray()),3,true);

            //为将这片区域转化为矩形，此矩形包含输入的形状
            boundRect[i] = Imgproc.boundingRect(contours_poly.get(i));

            // 找到交汇处的的表区域对象
            Mat table_image = points_image.submat(boundRect[i]);

            List<MatOfPoint> table_contours = new ArrayList<MatOfPoint>();
            Mat joint_mat = new Mat();
            Imgproc.findContours(table_image, table_contours,joint_mat, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
            //从表格的特性看，如果这片区域的点数小于4，那就代表没有一个完整的表格，忽略掉
            if (table_contours.size() < 4) {
                System.out.println("不是完整的表格");
                break;
            }
            System.out.println("可能是完整的表格2");

            // //保存图片
            // tables.addFirst(source_image.submat(boundRect[i]).clone());
            //
            // //将矩形画在原图上
            // Imgproc.rectangle(source_image, boundRect[i].tl(), boundRect[i].br(), new Scalar(0, 255, 0), 1, 8, 0);

        }

        // for(int i=0; i< tables.size(); i++ ){
        //
        //     //拿到表格后，可以对表格再次处理，比如 OCR 识别等
        //     saveImage("out-table/6-table-"+(i+1)+".png",tables.get(i));
        // }
        //
        // saveImage("out-table/7-source.png",source_image);

    }
    private static void saveImage(String path,Mat image){

        // String outPath =  File.separator + path;
        String outPath =  path;

        File file = new File(outPath);
        //目录是否存在
        dirIsExist(file.getParent());

        Imgcodecs.imwrite(outPath,image);

    }

    private static void dirIsExist(String dirPath){
        File dir = new File(dirPath);
        if(!dir.exists()){
            dir.mkdirs();
        }
    }

    public static String getEnglishString(String path) {
        File imageFile = new File(path);

        // 创建 Tesseract 实例
        ITesseract instance = new Tesseract();
        // 设置 Tesseract 数据路径（tessdata 文件夹）
        // 请确保这里的路径是包含 tessdata 文件夹的目录
        // 获取resource目录下的文件
        instance.setDatapath("./src/main/resources/tessdata");
        // 设置识别语言（英文）
        instance.setLanguage("eng");
        try {
            // 进行 OCR 识别
            String result = instance.doOCR(imageFile);
            // // 检查识别结果中是否包含英文字符
            // if (result.matches(".*[a-zA-Z]+.*")) {
            //     System.out.println("The image contains English characters.");
            // } else {
            //     System.out.println("No English characters detected in the image.");
            // }
            return result;
        } catch (Exception e) {
            log.error("img:[{}],Failed to recognize the image.", path);
            return "";
        }
    }

    public static void main(String[] args) {
        String path = "src/main/resources/size.jpg";
        System.out.println("*************"+path+"******************");
        // System.out.println(getEnglishString(path));
        System.out.println("------------");
        System.out.println(haveTable(path));
        haveTable2(path);

        String path2 = "src/main/resources/size1.jpg";
        System.out.println("****************"+path2+"****************");
        // System.out.println(getEnglishString(path2));
        System.out.println("------------");
        System.out.println(haveTable(path2));
        haveTable2(path2);

        String path3 = "src/main/resources/size2.jpg";
        System.out.println("**************"+path3+"*****************");
        // System.out.println(getEnglishString(path3));
        System.out.println("------------");
        System.out.println(haveTable(path3));
        haveTable2(path3);

        String path4 = "src/main/resources/size3.jpg";
        System.out.println("**************"+path4+"*****************");
        // System.out.println(getEnglishString(path4));
        System.out.println("------------");
        System.out.println(haveTable(path4));
        haveTable2(path4);

        String path5 = "src/main/resources/size5.jpg";
        System.out.println("**************"+path5+"*****************");
        // System.out.println(getEnglishString(path5));
        System.out.println("------------");
        System.out.println(haveTable(path5));
        haveTable2(path5);




    }
}
