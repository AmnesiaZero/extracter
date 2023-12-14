package org.example.controllers;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.example.DAO.DataSource;
import org.example.DAO.DocumentDAO;
import org.example.DAO.PageDAO;
import org.example.models.Document;
import org.example.models.Page;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.stream.Stream;


public class PageController {
    public PageDAO pageDAO;

    public DocumentDAO documentDAO;

    public PageController() throws Exception {
        this.pageDAO = new PageDAO(new DataSource());
        this.documentDAO = new DocumentDAO(new DataSource());
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
                            .filter(subDirectoryPath -> Integer.parseInt(subDirectoryPath.getFileName().toString()) > lastBookId)
                            .filter(subDirectoryPath -> subDirectoryPath.getFileName().toString().matches("\\d+"))
                            .forEach(subDirectoryPath -> {
                                String file = subDirectoryPath.toString() + "/" + subDirectoryPath.getFileName() + ".pdf";
                                Path filePath = Paths.get(file);
                                if (!Files.exists(filePath)) {
                                    return;
                                }
                                try {
                                    loadFile(filePath);
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

    public void loadFile(Path filePath) throws Exception {
        String fileName = filePath.getFileName().toString().replace(".pdf", "");
        int bookId = Integer.parseInt(fileName);
        if (FileUtils.sizeOf(filePath.toFile()) > 10000000) {
            Document document = new Document(bookId);
            documentDAO.create(document);
            return;
        }
        try (PDDocument document = PDDocument.load(filePath.toFile())) {
            int totalPages = document.getNumberOfPages();
            PDFTextStripper textStripper = new PDFTextStripper();
            if (totalPages > 0) {
                // Разбиваем документ на отдельные страницы и извлекаем текст
                for (int pageNum = 1; pageNum < totalPages; pageNum++) {
                    textStripper.setStartPage(pageNum);
                    textStripper.setEndPage(pageNum);
                    String pageText = textStripper.getText(document);
                    String cleanedText = pageText.replaceAll("[^\\p{L}\\p{Nd} ]", "");
                    cleanedText = Normalizer.normalize(cleanedText, Normalizer.Form.NFD)
                            .replaceAll("\\p{M}", "");
                    if (cleanedText.isEmpty()) {
                        return;
                    }
                    Page page = new Page(bookId, pageNum, cleanedText);
                    pageDAO.create(page);
                }
            }
        } catch (Exception e) {
            Document document = new Document(bookId);
            documentDAO.create(document);
            e.printStackTrace();
        }
    }
}

