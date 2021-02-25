package org.test;

import org.entity.Station;
import org.entity.Station1;
import org.junit.Test;

import static java.lang.Thread.sleep;

/**
 * @author : ZXW
 * @version : 1.0
 * @date : 2021-01-06 9:56
 * @Description :
 */
public class MyTest {

    static volatile int test01_num = 10;

    @Test
    public void test01() throws  InterruptedException{
        Station station1 = new Station();
        Station station2 = new Station();
        Station station3 = new Station();


        // 让每一个站台对象各自开始工作

        try {
            station1.start();

            station2.start();

            station3.start();
            station2.join();
            station1.join();
            station3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test02() {
        //实例化站台对象，并为每一个站台取名字
        Station1 station1=new Station1("窗口1");
        Station1 station2=new Station1("窗口2");
        Station1 station3=new Station1("窗口3");

        // 让每一个站台对象各自开始工作

        try {
            station1.start();

            station2.start();

            station3.start();
            station2.join();
            station1.join();
            station3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }
}
