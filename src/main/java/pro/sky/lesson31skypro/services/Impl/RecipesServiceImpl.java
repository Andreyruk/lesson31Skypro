package pro.sky.lesson31skypro.services.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import pro.sky.lesson31skypro.exception.FileProcessingException;
import pro.sky.lesson31skypro.model.Recipes;
import pro.sky.lesson31skypro.services.FileService;
import pro.sky.lesson31skypro.services.RecipesService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Service
public class RecipesServiceImpl implements RecipesService {
    private final FileService fileService;
    private Map<Integer, Recipes> recipesMap = new TreeMap<>();
    private static int id;

    public RecipesServiceImpl(@Qualifier("recipeFileService") FileService fileService) {
        this.fileService = fileService;
    }

    public Map<Integer, Recipes> getRecipesMap() {
        return recipesMap;
    }

    @Override
    public int addRecipe(Recipes recipe) {
        recipesMap.put(++id, recipe);
        saveToFileRecipes();
        return id;
    }

    @Override
    public Recipes getRecipe(int id) {
        if (!recipesMap.containsKey(id)) {
            throw new NotFoundException("Рецепт с заданным id не найден");
        }
        return recipesMap.get(id);
    }

    @Override
    public Collection<Recipes> getAllRecipe() {
        return recipesMap.values();
    }

    @Override
    public Recipes editRecipe(int id, Recipes recipes) {
        if (!recipesMap.containsKey(id)) {
            throw new NotFoundException("Рецепт с заданным id не найден");
        }
        saveToFileRecipes();
        return recipesMap.put(id, recipes);
    }

    @Override
    public Recipes removeRecipe(int id) {
        if (!recipesMap.containsKey(id)) {
            throw new NotFoundException("Рецепт с заданным id отсутствует");
        }
        saveToFileRecipes();
        return recipesMap.remove(id);
    }

    @PostConstruct
    private void initRecipes() throws FileProcessingException {
        readFromFileRecipes();
    }

    private void readFromFileRecipes() throws FileProcessingException {
        try {
            String json = fileService.readFromFile();
            recipesMap = new ObjectMapper().readValue(json, new TypeReference<HashMap<Integer, Recipes>>() {
            });
        } catch (JsonProcessingException e) {
            throw new FileProcessingException("Файл не прочитан");
        }
    }

    private void saveToFileRecipes() throws FileProcessingException {
        try {
            String json = new ObjectMapper().writeValueAsString(recipesMap);
            fileService.saveToFile(json);
        } catch (JsonProcessingException e) {
            throw new FileProcessingException("Файл не сохранён");
        }
    }
}
