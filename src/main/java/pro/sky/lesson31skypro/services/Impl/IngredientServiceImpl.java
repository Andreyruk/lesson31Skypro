package pro.sky.lesson31skypro.services.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import pro.sky.lesson31skypro.exception.FileProcessingException;
import pro.sky.lesson31skypro.model.Ingredients;
import pro.sky.lesson31skypro.services.FileService;
import pro.sky.lesson31skypro.services.IngredientService;

import java.util.Collection;
import java.util.TreeMap;

@Service
public class IngredientServiceImpl implements IngredientService {
    private final FileService fileService;

    public IngredientServiceImpl(@Qualifier("IngredientFileService") FileService fileService) {
        this.fileService = fileService;
    }

    private TreeMap<Integer, Ingredients> ingredientsMap = new TreeMap<>();

    private static int id = 0;

    @Override
    public int addIngredient(Ingredients ingredient) {
        if (!ingredientsMap.containsValue(ingredient)) {
            throw new NotFoundException("Ингредиент не добавлен");
        }
        ingredientsMap.put(++id, ingredient);
        saveToFileIngredients();
        return id;
    }

    @Override
    public Ingredients getIngredient(int id) {
        if (!ingredientsMap.containsKey(id)) {
            throw new NotFoundException("Ингредиент с заданным id не найден");
        }
        return ingredientsMap.get(id);
    }

    @Override
    public Collection<Ingredients> getAllIngredient() {
        return ingredientsMap.values();
    }

    @Override
    public Ingredients editIngredients(int id, Ingredients ingredient) {
        if (!ingredientsMap.containsKey(id)) {
            throw new NotFoundException("Ингредиент с заданным id не найден");
        }
        saveToFileIngredients();
        return ingredientsMap.put(id, ingredient);
    }

    @Override
    public Ingredients removeIngredients(int id) {
        if (!ingredientsMap.containsKey(id)) {
            throw new NotFoundException("Ингредиент с заданным id отсутствует");
        }
        saveToFileIngredients();
        return ingredientsMap.remove(id);
    }

    @PostConstruct
    private void initIngredients() throws FileProcessingException {
        readFromFileIngredients();
    }

    private void readFromFileIngredients() throws FileProcessingException {
        try {
            String json = fileService.readFromFile();
            ingredientsMap = new ObjectMapper().readValue(json, new TypeReference<TreeMap<Integer, Ingredients>>() {
            });
        } catch (JsonProcessingException e) {
            throw new FileProcessingException("не удалось прочитать файл");
        }
    }

    private void saveToFileIngredients() throws FileProcessingException {
        try {
            String json = new ObjectMapper().writeValueAsString(ingredientsMap);
            fileService.saveToFile(json);
        } catch (JsonProcessingException e) {
            throw new FileProcessingException("не удалось сохранить файл");
        }
    }
}
