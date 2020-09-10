package com.dcits;

import com.dcits.bean.TranFieldBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: huangth tel:13246649002
 * Date: 2020/9/2 17:44
 * @Version:
 * @Description:
 */
public class TestDemo {

    public static void main(String[] args) {

        Demo demo = new Demo();
        demo.setName("aaa");
        System.out.println(demo.getAge());
        System.out.println(demo.getName());

    }

    private static void iteratorList() {
        TranFieldBean b1 = new TranFieldBean();
        b1.setLfFieldNm("a");
        b1.setLfFieldZhNm("a");

        TranFieldBean b2 = new TranFieldBean();
        b2.setLfFieldNm("b");
        b2.setLfFieldZhNm("b");

        TranFieldBean b3 = new TranFieldBean();
        b3.setLfFieldNm("c");
        b3.setLfFieldZhNm("c");

        List<TranFieldBean> list = new ArrayList<>();
        list.add(b1);
        list.add(b2);
        list.add(b3);

        for (TranFieldBean bean: list) {
            System.out.println(bean);
        }
    }

    static class Demo {

        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

}
