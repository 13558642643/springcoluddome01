package org.pool;

import java.util.concurrent.*;

/**
 * @author : ZXW
 * @version : 1.0
 * @date : 2021-01-06 11:14
 * @Description :
 */

interface Foo {
    //    public void sayHello();
    public int add(int x, int y);

    default int div(int x, int y) {
        return x / y;
    }

    default int div1(int x, int y) {
        return x / y;
    }

    public static int sub(int x, int y) {
        return x - y;
    }

    public static int sub1(int x, int y) {
        return x - y;
    }

}
public class TestPool  {



    public static void main(String[] args) {
        Foo foo = null;
        foo = (int x, int y) -> {
            System.out.println("add method");
            return x + y;
        };
        System.out.println(foo.add(1, 3));
        System.out.println(foo.div(10, 2));
        System.out.println(Foo.sub(10,3));
    }
}
