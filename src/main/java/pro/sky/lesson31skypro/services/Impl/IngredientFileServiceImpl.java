package pro.sky.lesson31skypro.services.Impl;

import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pro.sky.lesson31skypro.exception.FileProcessingException;
import pro.sky.lesson31skypro.model.Recipes;
import pro.sky.lesson31skypro.services.FileService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Service("IngredientFileService")
public class IngredientFileServiceImpl implements FileService {
    @Value("${path.to.file}")
    private String dataFilePathIngredient;
    @Value("${name.of.ingredients.file}")
    private String dataFileNameIngredient;
    private Path path;

    private void init() {
        path = Path.of(dataFilePathIngredient, dataFileNameIngredient);
    }

    @Override
    public boolean saveToFile(String json) {
        try {
            cleanDataFile();
            Files.writeString(Path.of(dataFilePathIngredient, dataFileNameIngredient), json);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public String readFromFile() {
        if (Files.exists(Path.of(dataFilePathIngredient, dataFileNameIngredient))) {
            try {
                return Files.readString(Path.of(dataFilePathIngredient, dataFileNameIngredient));
            } catch (IOException e) {
                throw new FileProcessingException("не удалось прочитать файл");
            }
        } else {
            return "{}";
        }
    }

    @Override
    public boolean cleanDataFile() {
        try {
            Path path = Path.of(dataFilePathIngredient, dataFileNameIngredient);
            Files.deleteIfExists(path);
            Files.createFile(path);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public File getDataFile() {
        return new File(dataFilePathIngredient + "/" + dataFileNameIngredient);
    }

    @Override
    public InputStreamResource exportFile() throws FileNotFoundException {
        File file = getDataFile();
        return new InputStreamResource(new FileInputStream(file));
    }

    @Override
    public void importFile(MultipartFile file) throws FileNotFoundException {
        cleanDataFile();
        FileOutputStream fos = new FileOutputStream(getDataFile());
        try {
            IOUtils.copy(file.getInputStream(), fos);
        } catch (IOException e) {
            throw new FileProcessingException("Файл не сохранён.");
        }
    }

    @Override
    public InputStreamResource exportTxtFile(Map<Integer, Recipes> recipeMap) throws FileNotFoundException, IOException {
        return null;
    }

    @Override
    public Path getPath() {
        return path;
    }
}
