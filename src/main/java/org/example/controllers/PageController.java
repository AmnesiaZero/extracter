package org.example.controllers;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.text.PDFTextStripper;
import org.example.DAO.DataSource;
import org.example.DAO.PageDAO;
import org.example.models.Page;
import org.example.models.PageCollection;
import org.example.utils.Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PageController {
    PageDAO pageDAO;

    public PageController() throws Exception {
        this.pageDAO = new PageDAO(new DataSource());
    }
    public void split() {
        try {
            int lastBookId = pageDAO.getLastBook();
            int lasDirectory =  lastBookId /10000 + 1;
            String sourceDir = "/tmp/drive/ALLPDFS2";
            File source = new File(sourceDir);
            int count = 1;
            if (source.exists() && source.isDirectory()) {
                File[] files = source.listFiles();
                assert files != null;
                for (File directory : files) {
                    if (directory.isDirectory()) {
                        if(lasDirectory>count){
                            continue;
                        }
                        try (Stream<Path> filesStream = Files.walk(Paths.get(directory.getAbsolutePath()))) {
                                filesStream
                                        .filter(Files::isRegularFile)
                                        .filter(file -> file.toString().toLowerCase().endsWith(".pdf"))
                                        .filter(file -> Integer.parseInt(file.getFileName().toString())<=lastBookId)
                                        .forEach(filePath -> {
                                            try (PDDocument document = PDDocument.load(filePath.toFile())) {
                                                String fileName = filePath.getFileName().toString();
                                                int totalPages = document.getNumberOfPages();
                                                if (totalPages > 0) {
                                                    Splitter splitter = new Splitter();
                                                    List<PDDocument> pages = splitter.split(document);
                                                    List<Page> newPages = new ArrayList<>();
                                                    int i = 1;
                                                    for(PDDocument pdf:pages) {
                                                        PDFTextStripper stripper = new PDFTextStripper();
                                                        String text = stripper.getText(pdf);
                                                        if(text.isEmpty()){
                                                            continue;
                                                        }
                                                        String normalizedText = Normalizer.normalize(text, Normalizer.Form.NFKC)
                                                                .replaceAll("\\p{InCombiningDiacriticalMarks}+", " ")
                                                                .replaceAll("[\\r\\n\\\\\\\\/]+"," ");
                                                        pdf.close();
                                                        int bookId = Integer.parseInt(fileName);
                                                        newPages.add(new Page(bookId, i, normalizedText));
                                                        i++;
                                                    }
                                                    PageCollection pageCollection = new PageCollection(newPages);
                                                    pageCollection.store();
                                                } else {
                                                    System.out.println("Файл " + fileName + " пустой");
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
//                        Files.walk(Paths.get(sourceDir))
//                                .filter(Files::isRegularFile).forEach(filePath -> {
//                                    File newFile = new File(filePath.toString());
//                                    String fileName = newFile.getName().replace(".pdf", "");;
//                                    if(!fileName.contains(".pdf") | Integer.parseInt(fileName)<=lastBookId){
//                                        return;
//                                    }
//                                    try {
//                                        PDDocument document = PDDocument.load(newFile);
//                                        int totalPages = document.getNumberOfPages();
//                                        if (totalPages > 0) {
//                                            Splitter splitter = new Splitter();
//                                            List<PDDocument> pages = splitter.split(document);
//                                            List<Page> newPages = new ArrayList<>();
//                                            int i = 1;
//                                            for(PDDocument pdf:pages) {
//                                                PDFTextStripper stripper = new PDFTextStripper();
//                                                String text = stripper.getText(pdf);
//                                                if(text.isEmpty()){
//                                                    continue;
//                                                }
//                                                String normalizedText = Normalizer.normalize(text, Normalizer.Form.NFKC)
//                                                        .replaceAll("\\p{InCombiningDiacriticalMarks}+", " ")
//                                                        .replaceAll("[\\r\\n\\\\\\\\/]+"," ");
//                                                pdf.close();
//                                                int bookId = Integer.parseInt(fileName);
//                                                newPages.add(new Page(bookId, i, normalizedText));
//                                                i++;
//                                            }
//                                            PageCollection pageCollection = new PageCollection(newPages);
//                                            pageCollection.store();
//                                        } else {
//                                            System.out.println("Файл " + fileName + " пустой");
//                                        }
//                                        document.close();
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                });
                    }
                }
            } else {
                System.out.println("Указанный путь не является директорией или не существует.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

