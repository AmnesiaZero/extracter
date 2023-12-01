package org.example.controllers;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.example.DAO.DataSource;
import org.example.DAO.PageDAO;
import org.example.models.Page;
import org.example.models.PageCollection;
import org.example.models.Response;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResponseController {
    Response response;

    public String request() {
        String json = "";
        try {
            String sourceDir = "D:\\docs\\98\\145639844398.pdf";
            String destinationDir = "D:\\docs\\output\\";
            File oldFile = new File(sourceDir);
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
                    List<String> errors = new ArrayList();
                    List<Page> pages = new ArrayList<>();
                    while (iterator.hasNext()) {
                        PDDocument pd = iterator.next();
                        String pagePath = destinationDir + fileName + "_" + i + ".pdf";
                        pd.save(pagePath);
                        System.out.println("Page " + i + ", Extracted to : " + pagePath);
                        PDDocument doc = PDDocument.load(new File(pagePath));
                        PDFTextStripper stripper = new PDFTextStripper();
                        String content = stripper.getText(doc);
                        doc.close();
                        if (content.isEmpty()) {
                            errors.add("Страница " + i + " не извлечена");
                        }
                        pages.add(new Page(i, content));
                        i++;
                    }
                    FileUtils.cleanDirectory(new File("D:\\docs\\output"));
                    response = new Response(true,"Страницы успешно извлечены",fileName,pages,errors);
                    PageCollection pageCollection =  new PageCollection(pages);
                    pageCollection.store();
                    json = success("Страницы успешно извлечены",fileName,pages,errors);
                } else {
                    return error("Файл " + fileName + " пустой");
                }
            } else {
                return error("Файла " + fileName + "не существует");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }
    public String error(String message)
    {
        Gson errorJson = new Gson();
        Response response = new Response(false,message);
        return errorJson.toJson(response);
    }

    public String success(String message,String title,List<Page> pages,List<String> errors)
    {
        Gson errorJson = new Gson();
        Response response = new Response(true,message,title,pages,errors);
        return errorJson.toJson(response);
    }

}
