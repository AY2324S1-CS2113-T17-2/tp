package seedu.cafectrl.command;

import seedu.cafectrl.data.Menu;
import seedu.cafectrl.data.dish.Dish;
import seedu.cafectrl.data.dish.Ingredient;
import seedu.cafectrl.ui.ErrorMessages;
import seedu.cafectrl.ui.Ui;

/**
 * Lists all ingredients used in the selected dish to the user.
 */
public class ListIngredientCommand extends Command {
    public static final String COMMAND_WORD = "list_ingredients";
    public static final String MESSAGE_USAGE = "To list out the ingredients needed "
            + "along with the quantity for a specific dish:\n"
            + "Parameters: DISH_INDEX\n"
            + "Example: " + COMMAND_WORD + " 1";

    protected Ui ui;
    protected Menu menu;


    public ListIngredientCommand(int listIndex, Menu menu, Ui ui) {
        this.index = listIndex;
        this.menu = menu;
        this.ui = ui;
    }

    @Override
    public void execute() {
        try {
            Dish selectedDish = menu.getMenuItemsList().get(index - Ui.OFFSET_LIST_INDEX);
            ui.showIngredientsHeader(selectedDish);

            for (Ingredient ingredient : selectedDish.getIngredients()) {
                ui.formatListIngredient(ingredient.getName(),
                        ingredient.getQty()+ingredient.getUnit());
            }

            ui.showIngredientsEndCap();
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(ErrorMessages.UNLISTED_DISH);
        }
    }

}
