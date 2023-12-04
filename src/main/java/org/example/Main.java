package org.example;


import org.example.controllers.PageController;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

public class Main {
    public static void main(String[] args) {
        PageController pageController = new PageController();
        pageController.split();
    }
}