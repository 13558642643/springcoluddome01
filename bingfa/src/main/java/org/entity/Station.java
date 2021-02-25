package org.entity;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author : ZXW
 * @version : 1.0
 * @date : 2021-01-06 10:15
 * @Description :
 */
public class Station extends Thread {

    ReentrantLock reentrantLock = new ReentrantLock();

    static  int num = 10;

    static Object object = "aa";


    @Override
    public void run() {
        while (num > 0){
        synchronized (object){

                if(num > 0){
                    System.out.println("线程【"+Thread.currentThread().getName()+"】"+num);
                    num--;
                }else {
                    System.out.println("线程【"+Thread.currentThread().getName()+"】已售完");
                }
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
