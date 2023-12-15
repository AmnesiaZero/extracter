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
        DataSource dataSource = new DataSource();
        this.pageDAO = new PageDAO(dataSource);
        this.documentDAO = new DocumentDAO(dataSource);
    }

    public void split() {
        try {
            int lastBookId = pageDAO.getLastBook();
            int lasDirectory = lastBookId / 1000 + 1;
            String sourceDir = "/tmp/drive/ALLPDFS2";
            int currentDirectory = lasDirectory;
            int errorCount = 0;
            int directoriesCount = 0;
            int currentBook;
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
                if(directoriesCount>0){
                    currentBook = (currentDirectory-1) * 1000;
                }
                else {
                    currentBook = lastBookId;
                }
                for(int i = currentBook;i<currentDirectory*1000;i++){
                    Path subDirectoryPath = Paths.get(directoryPath + "/" + i);
                    if (!Files.exists(subDirectoryPath)) {
                        continue;
                    }
                    String file = subDirectoryPath + "/" + i + ".pdf";
                    Path filePath = Paths.get(file);
                    if (!Files.exists(filePath)) {
                        continue;
                    }
                    try {
                        loadFile(filePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                directoriesCount++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadFile(Path filePath) throws Exception {
        String fileName = filePath.getFileName().toString().replace(".pdf", "");
        int bookId = Integer.parseInt(fileName);
        if (FileUtils.sizeOf(filePath.toFile()) > 1042880) {
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
        }
        catch (Exception e) {
            Document document = new Document(bookId);
            documentDAO.create(document);
            e.printStackTrace();
        }
    }
}

