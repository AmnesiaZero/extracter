package org.example.controllers;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.example.models.Page;
import org.example.models.PageCollection;
import org.example.utils.Util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class PageController {

    public void split() {
        try {
            //8 уровней
            String sourceDir = "../../../../../../../../tmp/Scenarios"; //при импорте на сервер поменять
            String destinationDir = "../../../../../../../../tmp/Scenarios";
            Path path = Paths.get(destinationDir);
            Files.createDirectory(path);
            File[] directories = new File(sourceDir).listFiles(File::isDirectory);
            assert directories != null;
            for (File directory:directories) {
                Set<String> files = Util.listFiles(directory.getAbsolutePath());
                for (String file : files) {
                    String filePath = sourceDir + file;
                    File oldFile = new File(filePath);
                    String fileName = oldFile.getName().replace(".pdf", "");
                    if (oldFile.exists()) {
                        File newFile = new File(destinationDir);
                        if (!newFile.exists()) {
                            newFile.mkdir();
                        }

                        PDDocument document = PDDocument.load(oldFile);

                        int totalPages = document.getNumberOfPages();
                        System.out.println("Total Pages: " + totalPages);
                        if (totalPages > 0) {
                            Splitter splitter = new Splitter();
                            List<PDDocument> Pages = splitter.split(document);
                            Iterator<PDDocument> iterator = Pages.listIterator();

                            //Saving each page as an individual document
                            int i = 1;
                            List<Page> pages = new ArrayList<>();
                            while (iterator.hasNext()) {
                                PDDocument pd = iterator.next();
                                String pagePath = destinationDir + "\\" + fileName + "_" + i + ".pdf";
                                pd.save(pagePath);
                                System.out.println("Page " + i + ", Extracted to : " + pagePath);
                                PDDocument doc = PDDocument.load(new File(pagePath));
                                PDFTextStripper stripper = new PDFTextStripper();
                                String content = Util.clearString(stripper.getText(doc));
                                doc.close();
                                int bookId = Integer.parseInt(fileName);
                                pages.add(new Page(bookId,i, content));
                                i++;
                            }
                            FileUtils.cleanDirectory(new File(destinationDir));
                            PageCollection pageCollection = new PageCollection(pages);
                            pageCollection.store();
                        } else {
                            System.out.println("Файл " + fileName + " пустой");
                        }
                    } else {
                        System.out.println("Файла " + fileName + "не существует");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
