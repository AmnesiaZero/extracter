package org.example;


import com.google.gson.Gson;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.Tika;
import org.example.controllers.ResponseController;
import org.example.models.Page;
import org.example.models.PageCollection;
import org.example.models.Response;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ResponseController responseController = new ResponseController();
        String response = responseController.request();
        try (FileWriter file = new FileWriter("D:\\docs\\output\\" + "output.txt")) {
            file.write(response);
            file.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}