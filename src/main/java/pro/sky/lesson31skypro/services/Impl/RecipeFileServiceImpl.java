package pro.sky.lesson31skypro.services.Impl;

import jakarta.annotation.PostConstruct;
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
import java.nio.file.StandardOpenOption;
import java.util.Map;

@Service("recipeFileService")
public class RecipeFileServiceImpl implements FileService {
    @Value("${path.to.file}")
    private String dataFilePathIngredient;
    @Value("${name.of.recipes.file}")
    private String dataFileNameRecipe;
    private Path path;

    @PostConstruct
    private void init() {
        path = Path.of(dataFilePathIngredient, dataFileNameRecipe);
    }

    @Override
    public boolean saveToFile(String json) {
        try {
            cleanDataFile();
            Files.writeString(Path.of(dataFilePathIngredient, dataFileNameRecipe), json);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public String readFromFile() {
        if (Files.exists(Path.of(dataFilePathIngredient, dataFileNameRecipe))) {
            try {
                return Files.readString(Path.of(dataFilePathIngredient, dataFileNameRecipe));
            } catch (IOException e) {
//            throw new RuntimeException(e);
                throw new FileProcessingException("не удалось прочитать фал");
            }
        } else {
            return "{}";
        }
    }

    @Override
    public boolean cleanDataFile() {
        try {
            Path path = Path.of(dataFilePathIngredient, dataFileNameRecipe);
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
        return new File(dataFilePathIngredient + "/" + dataFileNameRecipe);
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
            throw new FileProcessingException("Файл сохранить не удалось.");
        }
    }

    @Override
    public InputStreamResource exportTxtFile(Map<Integer, Recipes> recipeMap) throws FileNotFoundException, IOException {
        Path path = this.createAllRecipesFile("allRecipes");
        for (Recipes recipe : recipeMap.values()) {
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
                writer.append(" Название рецепта: ");
                writer.append(recipe.getNameRecipe());
                writer.append("\n Время приготовления: ");
                writer.append(String.valueOf(recipe.getCookingTime()));
                writer.append(" ");
                writer.append("\n Ингредиенты: ");
                writer.append(String.valueOf(recipe.getIngredients()));
                writer.append("\n Шаги приготовления: ");
                writer.append(String.valueOf(recipe.getStepsCooking()));
            }
        }

        File file = path.toFile();
        return new InputStreamResource(new FileInputStream(file));
    }

    private Path createAllRecipesFile(String suffix) throws IOException {
        if (Files.exists(Path.of(dataFileNameRecipe, suffix))) {
            Files.delete(Path.of(dataFileNameRecipe, suffix));
            Files.createFile(Path.of(dataFileNameRecipe, suffix));
            return Path.of(dataFileNameRecipe, suffix);
        }
        return Files.createFile(Path.of(dataFileNameRecipe, suffix));
    }

    @Override
    public Path getPath() {
        return path;
    }
}
