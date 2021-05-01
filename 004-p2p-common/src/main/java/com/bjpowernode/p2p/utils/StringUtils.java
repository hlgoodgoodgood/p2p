package com.bjpowernode.p2p.utils;

public class StringUtils {

    //生成随机数
    public static String generateCode(int length){
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0;i<length;i++){
            stringBuilder.append(Math.round(Math.random()*9));
        }
        return stringBuilder.toString();
    }


}
