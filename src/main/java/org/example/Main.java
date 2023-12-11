package org.example;


import org.example.controllers.PageController;


public class Main {
    public static void main(String[] args) throws Exception {
        System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.NoOpLog");
        PageController pageController = new PageController();
        pageController.split();
    }
}