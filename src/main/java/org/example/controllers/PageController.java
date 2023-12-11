package org.example.controllers;

import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.example.DAO.DataSource;
import org.example.DAO.PageDAO;
import org.example.models.Page;
import org.example.models.PageCollection;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


public class PageController {
    PageDAO pageDAO;

    public PageController() throws Exception {
        this.pageDAO = new PageDAO(new DataSource());
    }

    public void split() {
        try {
            int lastBookId = pageDAO.getLastBook();
            int lasDirectory = lastBookId / 1000 + 1;
            String sourceDir = "/tmp/drive/ALLPDFS2";
            int currentDirectory = lasDirectory;
            int errorCount = 0;
            while (true) {
                Path directoryPath = Paths.get(sourceDir + "/" + currentDirectory);
                if (!Files.exists(directoryPath)) {
                    errorCount++;
                    if (errorCount > 100) {
                        throw new Exception("Программа отработала или возникла ошибка. Последняя директория - " + directoryPath);
                    }
                    currentDirectory++;
                    continue;
                }
                errorCount = 0;
                try (Stream<Path> subDirectoryStream = Files.list(directoryPath)) {
                    subDirectoryStream
                            .forEach(subDirectoryPath -> {
                                try (Stream<Path> subFileStream = Files.list(subDirectoryPath)) {
                                    subFileStream
                                            .filter(Files::isRegularFile)
                                            .filter(file -> file.getFileName().toString().contains(".pdf"))
                                            .filter(file -> file.getFileName().toString().replace(".pdf", "").matches("\\d+"))
                                            .filter(file -> Integer.parseInt(file.getFileName().toString().replace(".pdf", "")) > lastBookId)
                                            .forEach(this::loadFile);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                currentDirectory++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadFile(Path filePath) {
        try (PDDocument document = PDDocument.load(filePath.toFile())) {
            String fileName = filePath.getFileName().toString().replace(".pdf", "");
            int totalPages = document.getNumberOfPages();
            if (totalPages > 0) {
                Splitter splitter = new Splitter();
                List<PDDocument> pages = splitter.split(document);
                List<Page> newPages = new ArrayList<>();
                int i = 1;
                for (PDDocument pdf : pages) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    String text = stripper.getText(pdf);
                    if (text.isEmpty()) {
                        continue;
                    }
                    String normalizedText = Normalizer.normalize(text, Normalizer.Form.NFKC)
                            .replaceAll("\\p{InCombiningDiacriticalMarks}+", " ")
                            .replaceAll("[\\r\\n\\\\\\\\/]+", " ");
                    pdf.close();
                    int bookId = Integer.parseInt(fileName);
                    newPages.add(new Page(bookId, i, normalizedText));
                    i++;
                }
                PageCollection pageCollection = new PageCollection(newPages);
                pageCollection.store();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

