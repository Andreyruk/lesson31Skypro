package pro.sky.lesson31skypro.services;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;
import pro.sky.lesson31skypro.model.Recipes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public interface FileService {
    boolean saveToFile(String json);

    String readFromFile();

    boolean cleanDataFile();

    File getDataFile();

    InputStreamResource exportFile() throws FileNotFoundException;

    void importFile(MultipartFile file) throws FileNotFoundException;

    InputStreamResource exportTxtFile(Map<Integer, Recipes> recipeMap) throws FileNotFoundException, IOException;

    Path getPath();
}
