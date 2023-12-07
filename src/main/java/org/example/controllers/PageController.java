package org.example.controllers;

import lombok.extern.log4j.Log4j;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.example.DAO.DataSource;
import org.example.DAO.PageDAO;
import org.example.models.Page;
import org.example.models.PageCollection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Log4j
public class PageController {
    PageDAO pageDAO;

    public PageController() throws Exception {
        this.pageDAO = new PageDAO(new DataSource());
    }

    public void split() throws IOException {
        try {
            int lastBookId = pageDAO.getLastBook();
            int lasDirectory = lastBookId / 10000 + 1;
            String sourceDir = "/tmp/drive/ALLPDFS2";
            File source = new File(sourceDir);
            try (Stream<Path> directoryStream = Files.walk(Paths.get(source.getAbsolutePath()))) {
                directoryStream
                        .filter(Files::isDirectory)
                        .filter(file -> Integer.parseInt(file.getFileName().toString()) >= lasDirectory)
                        .forEach(directoryPath -> {
                            try (Stream<Path> filesStream = Files.walk(directoryPath)) {
                                filesStream
                                        .filter(Files::isRegularFile)
                                        .filter(file -> file.toString().toLowerCase().endsWith(".pdf"))
                                        .filter(file -> Integer.parseInt(file.getFileName().toString()) > lastBookId)
                                        .forEach(filePath -> {
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
                                                } else {
                                                    log.debug("Файл" + fileName + "пустой");
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

