package com.bjpowernode.p2p;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 测试定时器
 */
public class TestTimer {
    public static void main(String[] args) {

        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("------我执行了----");
            }
        },1000*5,5*1000);
    }
}
