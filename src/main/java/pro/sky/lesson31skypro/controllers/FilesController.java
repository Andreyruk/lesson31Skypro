package pro.sky.lesson31skypro.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;
import pro.sky.lesson31skypro.services.FileService;
import pro.sky.lesson31skypro.services.RecipesService;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/files")
@Tag(name = "Files", description = "CRUD - операции для работы с файлами")
public class FilesController {
    private final FileService ingredientFileService;
    private final FileService recipesFileService;
    private final RecipesService recipeService;

    public FilesController(@Qualifier("IngredientFileService") FileService ingredientFileService,
                           @Qualifier("recipeFileService") FileService recipesFileService, RecipesService recipeService) {
        this.ingredientFileService = ingredientFileService;
        this.recipesFileService = recipesFileService;
        this.recipeService = recipeService;
    }

    @GetMapping("/ingredient/export")
    @Operation(description = "Экспорт файла ингредиентов")
    public ResponseEntity<InputStreamResource> downloadIngredientFile() throws IOException {
        InputStreamResource inputStreamResource = ingredientFileService.exportFile();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .contentLength(Files.size(ingredientFileService.getPath()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Ingredients.json\"")
                .body(inputStreamResource);
    }

    @PostMapping(value = "/ingredient/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(description = "Импорт файла ингредиентов")
    public ResponseEntity<Void> uploadIngredientFile(@RequestParam MultipartFile file) throws IOException {
        ingredientFileService.importFile(file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/recipe/export")
    @Operation(description = "Экспорт файла рецептов")
    public ResponseEntity<InputStreamResource> downloadRecipeFile() throws IOException {
        InputStreamResource inputStreamResource = recipesFileService.exportFile();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .contentLength(Files.size(ingredientFileService.getPath()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename =\"Recipes.json\"")
                .body(inputStreamResource);
    }

    @PostMapping(value = "/recipe/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(description = "Импорт файла рецептов")
    public ResponseEntity<Void> uploadRecipeFile(@RequestParam MultipartFile file) throws IOException {
        recipesFileService.importFile(file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/recipe/exporttxt")
    @Operation(description = "Экспорт файла рецептов в формате txt")
    public ResponseEntity<InputStreamResource> downloadRecipeFileTxt() throws IOException {
        InputStreamResource inputStreamResource = recipesFileService.exportTxtFile(recipeService.getRecipesMap());
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(Files.size(recipesFileService.getPath()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename =\"AllRecipes.txt\"")
                .body(inputStreamResource);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) errors).getField();
            String errorsMessage = ((FieldError) errors).getDefaultMessage();
            errors.put(fieldName, errorsMessage);
        });
        return errors;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public String handleNotFoundException(NotFoundException notFoundException) {
        return notFoundException.getMessage();
    }
}
