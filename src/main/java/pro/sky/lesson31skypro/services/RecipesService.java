package pro.sky.lesson31skypro.services;

import pro.sky.lesson31skypro.model.Recipes;

import java.util.Collection;
import java.util.Map;

public interface RecipesService {
    int addRecipe(Recipes recipe);

    Recipes getRecipe(int id);

    Collection<Recipes> getAllRecipe();

    Recipes editRecipe(int id, Recipes recipes);

    Recipes removeRecipe(int id);

    Map<Integer, Recipes> getRecipesMap();
}
