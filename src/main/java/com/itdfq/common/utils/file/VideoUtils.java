package com.itdfq.common.utils.file;

import com.itdfq.common.Exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.platform.commons.util.StringUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

/**
 * @author duanfangqin 2022/11/30 14:26
 * @implNote
 */
@Slf4j
public class VideoUtils {

    /**
     *视频分片
     *
     * @param file 分片文件
     * @param splitNum 分几片
     * @param currentDir 分片保存位置
     * @param splitSize 按照多大进行分片 单位字节
     * @param suffix 后缀名，不传默认取文件的后缀名 例如： .mp4
     * @throws Exception
     */
    public static void videoSharding(File file, int splitNum, String currentDir, long splitSize, String suffix) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        FileChannel inputChannel = fis.getChannel();
        FileOutputStream fos;
        FileChannel outputChannel;
        File currentDirFile = new File(currentDir);
        if (!currentDirFile.exists()) {
            currentDirFile.mkdirs();
        }
        if (StringUtils.isBlank(suffix)) {
            String absolutePath = file.getAbsolutePath();
            int i1 = absolutePath.lastIndexOf(".");
            suffix = absolutePath.substring(i1);
        }
        long startPoint = 0;
        for (int i = 0; i < splitNum; i++) {
            String splitFileName = currentDir + i;
            File splitFile = new File(splitFileName+suffix);
            splitFile.createNewFile();
            fos = new FileOutputStream(splitFileName+suffix);
            outputChannel = fos.getChannel();
            inputChannel.transferTo(startPoint, splitSize, outputChannel);
            startPoint += splitSize;
            outputChannel.close();
            fos.close();
        }
        inputChannel.close();
        fis.close();
    }


    /**
     * 获取加密的md5 字符串
     * @param file
     * @return
     */
    public static String getFileMD5(File file)throws Exception {
        if (!file.isFile()) {
            throw new BizException("file不能为空");
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            log.info("视频Md5加密失败,name:{},msg:{}",file.getName(),e.getMessage(),e);
            throw new BizException(e.getMessage());
        }
        return bytesToHexString(digest.digest());
    }



    public static String bytesToHexString(byte[] src) throws BizException {
        try {
            StringBuilder stringBuilder = new StringBuilder("");
            if (src == null || src.length <= 0) {
                return null;
            }
            for (int i = 0; i < src.length; i++) {
                int v = src[i] & 0xFF;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    stringBuilder.append(0);
                }
                stringBuilder.append(hv);
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            throw new BizException("字节转换字符串失败");
        }
    }

    public static void copyInputStreamToFile(InputStream inputStream, File file) throws BizException {
        try {
            FileOutputStream outputStream = FileUtils.openOutputStream(file);
            try {
                IOUtils.copy(inputStream, outputStream);
                outputStream.close();
            } finally {
                IOUtils.closeQuietly(outputStream);
            }
        } catch (IOException e) {
            throw new BizException("IO写入异常" + e.getMessage());
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }


    public static Boolean deleteFile(File file) {
        //判断文件不为null或文件目录存在
        if (file == null || !file.exists()) {
            log.error("删除文件失败");
            return false;
        }
        //获取目录下子文件
        File[] files = file.listFiles();
        //遍历该目录下的文件对象
        for (File f : files) {
            //判断子目录是否存在子目录,如果是文件则删除
            if (f.isDirectory()) {
                //递归删除目录下的文件
                deleteFile(f);
            } else {
                //文件删除
                f.delete();
                //打印文件名
                log.warn("删除文件：{}",f.getAbsolutePath());
            }
        }
        //文件夹删除
        file.delete();
        log.warn("删除目录：{}",file.getAbsolutePath());
        return true;
    }
}
