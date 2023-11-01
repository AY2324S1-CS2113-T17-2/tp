package seedu.cafectrl.data;

import seedu.cafectrl.data.dish.Dish;
import seedu.cafectrl.data.dish.Ingredient;
import seedu.cafectrl.ui.ErrorMessages;
import seedu.cafectrl.ui.Ui;

import java.util.ArrayList;

public class Pantry {
    private ArrayList<Ingredient> pantryStock;
    private Ui ui;

    //@@author NaychiMin
    public Pantry(Ui ui, ArrayList<Ingredient> pantryStock) {
        this.ui = ui;
        this.pantryStock = pantryStock;
    }

    //@@author ShaniceTang
    public Pantry(Ui ui) {
        this.ui = ui;
        this.pantryStock = new ArrayList<>();
    }

    /**
     * Retrieves the current pantry stock from storage, which may include reading from a file (pantry.txt).
     *
     * @return An ArrayList of Ingredient objects representing the current pantry stock.
     */
    public ArrayList<Ingredient> getPantryStock() {
        return pantryStock;
    }

    /**
     * Adds or updates an ingredient in the pantry stock based on its name and quantity.
     *
     * @param name The name of the ingredient to add or update.
     * @param qty The quantity of the ingredient (e.g., "100g").
     * @param unit The unit of measurement for the quantity.
     * @return The Ingredient object that was added or updated in the pantry stock.
     */
    public Ingredient addIngredientToStock (String name, int qty, String unit) {
        pantryStock = getPantryStock(); //get latest pantry stock from pantry.txt
        int ingredientIndex = getIndexOfIngredient(name);

        //if ingredient exists in pantry, add quantity of that ingredient
        if (ingredientIndex != -1) {
            return addIngredientQuantity(qty, ingredientIndex, unit);
        }

        //else, add new ingredient to pantry
        Ingredient ingredient = new Ingredient(name, qty, unit);
        pantryStock.add(ingredient);
        return ingredient;
    }

    /**
     * Updates an ingredient's quantity in the pantry stock or adds a new ingredient if it doesn't exist.
     *
     * @param qty            The quantity of the ingredient (e.g., "100g").
     * @param ingredientIndex The index of the ingredient in the pantry stock (-1 if not found).
     * @return The Ingredient object that was added or updated in the pantry stock.
     */
    private Ingredient addIngredientQuantity(int qty, int ingredientIndex, String unit) {
        Ingredient ingredient = pantryStock.get(ingredientIndex);
        if (!unit.equalsIgnoreCase(ingredient.getUnit())) {
            throw new RuntimeException(ErrorMessages.UNIT_NOT_MATCHING
                + "\nUnit used previously: " + ingredient.getUnit());
        }
        qty += ingredient.getQty(); //adds new qty to current qty
        ingredient.setQty(qty);
        return ingredient;
    }

    /**
     * Gets the index of an ingredient in the pantry stock based on its name (case-insensitive comparison).
     *
     * @param name The name of the ingredient to search for.
     * @return The index of the ingredient in the pantry stock or -1 if not found.
     */
    private int getIndexOfIngredient(String name) {
        for (int i = 0; i < pantryStock.size(); i++) {
            String ingredientName = pantryStock.get(i).getName().trim();
            if (name.equalsIgnoreCase(ingredientName)) {
                return i;
            }
        }
        return -1;
    }

    //@@author NaychiMin
    /**
     * Decreases the stock of ingredients based on the given dish order.
     *
     * @param dishIngredients Array of ingredients used to make the dish order.
     */
    public boolean isDishCooked(ArrayList<Ingredient> dishIngredients) {
        //for each ingredient that is used in the dish, update the stock of ingredient left.
        for (Ingredient dishIngredient : dishIngredients) {
            Ingredient usedIngredientFromStock = getIngredient(dishIngredient);
            if (usedIngredientFromStock == null) {
                return false;
            }
            int stockQuantity = usedIngredientFromStock.getQty();
            int usedQuantity = dishIngredient.getQty();
            int finalQuantity = stockQuantity - usedQuantity;
            if (finalQuantity < 0) {
                return false;
            }
            usedIngredientFromStock.setQty(finalQuantity);
        }
        return true;
    }

    /**
     * Retrieves the ingredient used in the ordered dish from pantryStock.
     *
     * @param dishIngredient The ingredient used in the ordered dish.
     * @return The corresponding ingredient in pantryStock.
     */
    private Ingredient getIngredient(Ingredient dishIngredient) {
        return pantryStock.stream()
                .filter(ingredient -> ingredient.getName().equals(dishIngredient.getName()))
                .findFirst()
                .orElse(null);
    }
    //@@author NaychiMin
    /**
     * Checks the availability of dishes based on ingredient stock.
     */
    public void calculateDishAvailability(Menu menu) {
        int menuSize = menu.getSize();
        for (int i = 0; i < menuSize; i++) {
            Dish dish = menu.getDishFromId(i);
            ui.showToUser("Dish: " + dish.getName());
            int numberOfDishes = calculateMaxDishes(dish, menu);
            ui.showDishAvailability(numberOfDishes);
            if (i != menuSize - 1) {
                ui.printLine();
            }
        }
    }
    //@@author
    /**
     * Calculates the number of dishes that can be prepared with the available ingredients.
     *
     * @param dish The dish being ordered.
     */
    public int calculateMaxDishes(Dish dish, Menu menu) {
        int maxNumofDish = Integer.MAX_VALUE;
        ArrayList<Ingredient> dishIngredients = retrieveIngredientsForDish(dish.getName(), menu);

        for (Ingredient dishIngredient : dishIngredients) {
            int numOfDish = calculateMaxDishForEachIngredient(dishIngredient);
            maxNumofDish = Math.min(numOfDish, maxNumofDish);

            if (numOfDish == 0) {
                handleRestock(dishIngredient);
            }
        }

        return maxNumofDish;
    }

    /**
     * Calculates the number of dishes that can be prepared with the provided ingredients.
     *
     * @param dishIngredient The ingredient used in the ordered dish.
     * @return The number of dishes that can be prepared.
     */
    private int calculateMaxDishForEachIngredient(Ingredient dishIngredient) {
        Ingredient usedIngredientFromStock = getIngredient(dishIngredient);
        if (usedIngredientFromStock == null) {
            return 0;
        }

        int currentQuantity = usedIngredientFromStock.getQty();
        int usedQuantity = dishIngredient.getQty();
        return currentQuantity / usedQuantity;
    }

    /**
     * Handles the case when restocking is required for a specific ingredient.
     *
     * @param dishIngredient The ingredient for which restocking is needed.
     */
    private void handleRestock(Ingredient dishIngredient) {
        String dishIngredientName = dishIngredient.getName();
        Ingredient stockIngredient = getIngredient(dishIngredient);

        int currentQuantity = (stockIngredient == null) ? 0 : stockIngredient.getQty();
        String unit = dishIngredient.getUnit();
        String neededIngredient = dishIngredient.toString();
        ui.showNeededRestock(dishIngredientName, currentQuantity, unit, neededIngredient);
    }

    /**
     * Retrieves the ingredients for a specific ordered dish.
     *
     * @param orderedDish The name of the ordered dish.
     * @return The list of ingredients for the ordered dish.
     */
    public ArrayList<Ingredient> retrieveIngredientsForDish(String orderedDish, Menu menu) {
        ArrayList<Ingredient> dishIngredients = new ArrayList<>();

        //retrieving the ingredients for orderedDish
        for (Dish dish : menu.getMenuItemsList()) {
            if (dish.getName().equals(orderedDish)) {
                dishIngredients.addAll(dish.getIngredients());
                break;
            }
        }
        return dishIngredients;
    }
}


