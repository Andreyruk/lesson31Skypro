package pro.sky.lesson31skypro.services;

import pro.sky.lesson31skypro.model.Ingredients;

import java.util.Collection;

public interface IngredientService {
    int addIngredient(Ingredients ingredient);

    Ingredients getIngredient(int id);

    Collection<Ingredients> getAllIngredient();

    Ingredients editIngredients(int id, Ingredients ingredient);

    Ingredients removeIngredients(int id);
}
