package com.beansgalaxy.backpacks;

public class CommonClass {

    public static void init() {
        Constants.register();
        Constants.LOG.info("Initializing Beans' Backpacks Common");
    }

    public static void test() {
        for (int i = 0; i < 20; i++)
            System.out.println("TEST");
    }



}