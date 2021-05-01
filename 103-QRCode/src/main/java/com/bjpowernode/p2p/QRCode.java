package com.bjpowernode.p2p;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * 生成二维码
 */
public class QRCode {
    public static void main(String[] args) throws Exception {
        Map<EncodeHintType,Object> map= new HashMap<EncodeHintType, Object>();
        map.put(EncodeHintType.CHARACTER_SET,"UTF-8");
        //创建一个矩阵对象
        BitMatrix bitMatrix = new MultiFormatWriter().encode("https://img10.360buyimg.com/imgzone/jfs/t1/93296/32/12978/324954/5e537cc2E0f098932/1a4dac5ecc78b967.jpg", BarcodeFormat.QR_CODE, 200, 200, map);

        //文件系统获取路径
        Path path= FileSystems.getDefault().getPath("D:\\course\\06-p2p","xx.jpg");
        //将矩阵对象转换为二维码图片
        MatrixToImageWriter.writeToPath(bitMatrix,"jpg",path);
        System.out.println("生成成功");
    }
}
