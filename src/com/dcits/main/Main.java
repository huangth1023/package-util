package com.dcits.main;

import com.dcits.bulid.Build;

import java.io.File;

public class Main {

    public static void main(String[] args) {

        String path = System.getProperty("user.dir") + File.separator +"file" + File.separator + "2-九江银行开放平台项目_金信_字段映射_V1.1.0.xlsx";

        Build build = new Build();
        build.build(path);


    }
}
