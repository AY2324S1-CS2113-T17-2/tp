package seedu.cafectrl.parser;

import seedu.cafectrl.Order;
import seedu.cafectrl.OrderList;
import seedu.cafectrl.command.AddDishCommand;
import seedu.cafectrl.command.Command;
import seedu.cafectrl.command.DeleteDishCommand;
import seedu.cafectrl.command.EditPriceCommand;
import seedu.cafectrl.command.ExitCommand;
import seedu.cafectrl.command.HelpCommand;
import seedu.cafectrl.command.IncorrectCommand;
import seedu.cafectrl.command.ListIngredientCommand;
import seedu.cafectrl.command.ListMenuCommand;
import seedu.cafectrl.command.AddOrderCommand;
import seedu.cafectrl.command.ViewTotalStockCommand;
import seedu.cafectrl.command.BuyIngredientCommand;

import seedu.cafectrl.Order;
import seedu.cafectrl.ui.Messages;
import seedu.cafectrl.data.Menu;
import seedu.cafectrl.data.dish.Dish;
import seedu.cafectrl.data.dish.Ingredient;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.ParseException;

/**
 * Parse everything received from the users on terminal
 * into a format that can be interpreted by other core classes
 */
public class Parser {
    public static final Pattern COMMAND_ARGUMENT_FORMAT = Pattern.compile("(?<commandWord>\\S+)\\s?(?<arguments>.*)");

    // Command Argument Patterns
    public static final String INGREDIENT_ARGUMENT_STRING = "ingredient/(?<name>[A-Za-z0-9\\s]+) "
            + "qty/(?<qty>[A-Za-z0-9\\s]+)";
    public static final String INGREDIENT_DIVIDER_REGEX = ", ";
    public static final String INGREDIENT_DIVIDER_STRING = ",";
    public static final String INGREDIENT_NAME_REGEX_GROUP_LABEL = "name";
    public static final String INGREDIENT_QTY_REGEX_GROUP_LABEL = "qty";
    public static final int DISH_NAME_MATCHER_GROUP_NUM = 1;
    public static final int PRICE_MATCHER_GROUP_NUM = 2;
    public static final int INGREDIENT_LIST_MATCHER_GROUP_NUM = 4;
    public static final int ORDER_QTY_MATCHER_GROUP_NUM = 2;
    private static final String ADD_ARGUMENT_STRING = "name/([A-Za-z0-9\\s]+) "
            + "price/([+-]?(?=\\.\\d|\\d)(?:\\d+)?(?:\\.?\\d*))(?:[Ee]([+-]?\\d+))? "
            + "(ingredient/[A-Za-z0-9\\s]+ qty/[A-Za-z0-9\\s]+"
            + "(?:, ingredient/[A-Za-z0-9\\s]+ qty/[A-Za-z0-9\\s]+)*)";
    private static final String ADD_ORDER_ARGUMENT_STRING = "name/([A-Za-z0-9\\s]+) "
            + "qty/([A-Za-z0-9\\s]+)";
    private static final String LIST_INGREDIENTS_ARGUMENT_STRING = "(\\d+)";
    private static final String DELETE_ARGUMENT_STRING = "(\\d+)";
    private static final String EDIT_PRICE_ARGUMENT_STRING = "index/(\\d+) price/(\\d+(\\.\\d+)?)";

    /**
     * Parse userInput and group it under commandWord and arguments
     * use commandWord to find the matching command and prepare the command
     *
     * @param userInput full user input
     * @param menu The arraylist object created that stores current tasks
     * @return command requested by the user
     */
    public static Command parseCommand(Menu menu, String userInput, Ui ui, Pantry pantry, OrderList orderList) {
        final Matcher matcher = COMMAND_ARGUMENT_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand("Incorrect command format!");
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        switch (commandWord) {

        case AddDishCommand.COMMAND_WORD:
            return prepareAdd(arguments);

        case DeleteDishCommand.COMMAND_WORD:
            return prepareDelete(menu, arguments);

        case ListIngredientCommand.COMMAND_WORD:
            return prepareListIngredient(menu, arguments);

        case ListMenuCommand.COMMAND_WORD:
            return prepareListMenu();

        case EditPriceCommand.COMMAND_WORD:
            return prepareEditPriceCommand(menu, arguments);

        case ViewTotalStockCommand.COMMAND_WORD:
            return prepareViewTotalStock();

        case BuyIngredientCommand.COMMAND_WORD:
            return prepareBuyIngredient(arguments);

        case HelpCommand.COMMAND_WORD:
            return prepareHelpCommand();

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case AddOrderCommand.COMMAND_WORD:
            return prepareOrder(menu, arguments, ui, pantry, orderList);

        default:
            return new IncorrectCommand(Messages.UNKNOWN_COMMAND_MESSAGE);
        }
    }

    /** All prepareCommand Classes */
    private static Command prepareListMenu() {
        return new ListMenuCommand();
    }

    /**
     * Parse argument in the context of edit price command
     * @param menu menu of the current session
     * @param arguments string that matches group arguments
     * @return new EditDishCommand
     */
    private static Command prepareEditPriceCommand(Menu menu, String arguments) {
        Pattern editDishArgumentsPattern = Pattern.compile(EDIT_PRICE_ARGUMENT_STRING);
        Matcher matcher = editDishArgumentsPattern.matcher(arguments);

        // Checks whether the overall pattern of edit price arguments is correct
        if (!matcher.find()) {
            return new IncorrectCommand(Messages.MISSING_ARGUMENT_FOR_EDIT_PRICE);
        }

        try {
            int dishIndexGroup = 1;
            int newPriceGroup = 2;
            int dishIndex = Integer.parseInt(matcher.group(dishIndexGroup));
            float newPrice = Float.parseFloat(matcher.group(newPriceGroup));

            // Check whether the dish index is valid
            if (!menu.isValidDishIndex(dishIndex)) {
                return new IncorrectCommand(Messages.INVALID_DISH_INDEX);
            }
            return new EditPriceCommand(dishIndex, newPrice);
        } catch (IllegalArgumentException e) {
            return new IncorrectCommand(Messages.WRONG_ARGUMENT_TYPE_FOR_EDIT_PRICE);
        }
    }

    /**
     * Parses the user input text into ingredients to form a <code>Dish</code> that is added to the <code>Menu</code>
     * @param arguments
     * @return new AddDishCommand
     */
    private static Command prepareAdd(String arguments) {
        final Pattern addArgumentPatter = Pattern.compile(ADD_ARGUMENT_STRING);
        Matcher matcher = addArgumentPatter.matcher(arguments);

        try {
            // Checks whether the overall pattern of add arguments is correct
            if (!matcher.matches()) {
                return new IncorrectCommand(Messages.INVALID_ADD_DISH_FORMAT_MESSAGE
                        + AddDishCommand.MESSAGE_USAGE);
            }

            // To retrieve specific arguments from arguments
            String dishName = matcher.group(DISH_NAME_MATCHER_GROUP_NUM);
            float price = Float.parseFloat(matcher.group(PRICE_MATCHER_GROUP_NUM));
            String ingredientsListString = matcher.group(INGREDIENT_LIST_MATCHER_GROUP_NUM);

            checkNegativePrice(price);

            ArrayList<Ingredient> ingredients =  ingredientParsing(ingredientsListString);

            Dish dish = new Dish(dishName, ingredients, price);

            return new AddDishCommand(dish);
        } catch (IllegalArgumentException e) {
            return new IncorrectCommand(Messages.INVALID_ADD_DISH_FORMAT_MESSAGE
                    + AddDishCommand.MESSAGE_USAGE);
        }
    }

    /** to be removed once the new regex is implemented because new regex only allows positive prices*/
    private static void checkNegativePrice(float price) throws IllegalArgumentException {
        if (price < 0) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Parses the user's input text ingredients into <code>Ingredient</code> objects that is added into
     * list of ingredients for the <code>Dish</code> object that is going to be added to the <code>Menu</code>
     * @param ingredientsListString user's input string of ingredients
     * @return Ingredient objects that consists of the dish
     * @throws IllegalArgumentException if the input string of ingredients is in an incorrect format.
     */
    private static ArrayList<Ingredient> ingredientParsing(String ingredientsListString)
            throws IllegalArgumentException {
        String[] ingredientListInputText = {ingredientsListString};
        ArrayList<Ingredient> ingredients = new ArrayList<>();

        //check if there is more than 1 ingredient
        if (ingredientsListString.contains(INGREDIENT_DIVIDER_STRING)) {
            //split the whole string of ingredients into separate individual ingredients
            ingredientListInputText = ingredientsListString.split(INGREDIENT_DIVIDER_REGEX);
        }

        //Parsing each ingredient
        for (String inputIngredientText: ingredientListInputText) {
            final Pattern ingredientPattern = Pattern.compile(INGREDIENT_ARGUMENT_STRING);
            Matcher ingredientMatcher = ingredientPattern.matcher(inputIngredientText);

            if (!ingredientMatcher.matches()) {
                throw new IllegalArgumentException();
            }

            String ingredientName = ingredientMatcher.group(INGREDIENT_NAME_REGEX_GROUP_LABEL);
            String ingredientQty = ingredientMatcher.group(INGREDIENT_QTY_REGEX_GROUP_LABEL);

            int qty = extractQty(ingredientQty);
            String unit = extractUnit(ingredientQty);

            Ingredient ingredient = new Ingredient(ingredientName, qty, unit);

            ingredients.add(ingredient);
        }

        return ingredients;
    }


    /**
    * Parses arguments in the context of the ListIngredient command.
    * @param menu menu of the current session
    * @param arguments string that matches group arguments
    * @return the prepared command
    */
    private static Command prepareListIngredient(Menu menu, String arguments) {
        final Pattern prepareListPattern = Pattern.compile(LIST_INGREDIENTS_ARGUMENT_STRING);
        Matcher matcher = prepareListPattern.matcher(arguments.trim());

        if (!matcher.matches()) {
            return new IncorrectCommand(Messages.MISSING_ARGUMENT_FOR_LIST_INGREDIENTS);
        }

        int dishIndex = Integer.parseInt(matcher.group(1));

        if (!menu.isValidDishIndex(dishIndex)) {
            return new IncorrectCommand(Messages.INVALID_DISH_INDEX);
        }

        return new ListIngredientCommand(dishIndex);
    }

    /**
     * Parses arguments in the context of the Delete command.
     *
     * @param menu menu of the current session
     * @param arguments string that matches group arguments
     * @return DeleteDishCommand if command is valid, IncorrectCommand otherwise
     */
    private static Command prepareDelete(Menu menu, String arguments) {
        Pattern deleteDishArgumentsPattern = Pattern.compile(DELETE_ARGUMENT_STRING);
        Matcher matcher = deleteDishArgumentsPattern.matcher(arguments.trim());

        // Checks whether the overall pattern of delete price arguments is correct
        if (!matcher.matches()) {
            return new IncorrectCommand(Messages.MISSING_ARGUMENT_FOR_DELETE);
        }

        int listIndexArgGroup = 1;
        int dishIndex = Integer.parseInt(matcher.group(listIndexArgGroup));

        if (!menu.isValidDishIndex(dishIndex)) {
            return new IncorrectCommand(Messages.INVALID_DISH_INDEX);
        }

        return new DeleteDishCommand(dishIndex);
    }

    private static Command prepareViewTotalStock() {
        return new ViewTotalStockCommand();
    }

    private static Command prepareBuyIngredient(String arguments) {
        Pattern buyIngredientArgumentsPattern = Pattern.compile(INGREDIENT_ARGUMENT_STRING);
        Matcher matcher = buyIngredientArgumentsPattern.matcher(arguments.trim());

        if (!matcher.matches()) {
            return new IncorrectCommand(Messages.MISSING_ARGUMENT_FOR_BUY_INGREDIENT);
        }

        String ingredientName = matcher.group(INGREDIENT_NAME_REGEX_GROUP_LABEL);
        String ingredientQty = matcher.group(INGREDIENT_QTY_REGEX_GROUP_LABEL);

        int qty = extractQty(ingredientQty);
        String unit = extractUnit(ingredientQty);

        try {
            return new BuyIngredientCommand(ingredientName, qty, unit);
        } catch (Exception e) {
            return new IncorrectCommand(Messages.INVALID_ARGUMENT_FOR_BUY_INGREDIENT);
        }
    }

    private static Command prepareHelpCommand() {
        return new HelpCommand();
    }

    /**
     * Parses arguments in the context of the Delete command.
     *
     * @param menu menu of the current session
     * @param arguments string that matches group arguments
     * @return AddOrderCommand if command is valid, IncorrectCommand otherwise
     */
    private static Command prepareOrder(Menu menu, String arguments, Ui ui, Pantry pantry, OrderList orderList) {
        final Pattern addOrderArgumentPatter = Pattern.compile(ADD_ORDER_ARGUMENT_STRING);
        Matcher matcher = addOrderArgumentPatter.matcher(arguments);

        // Checks whether the overall pattern of add order arguments is correct
        if (!matcher.matches()) {
            return new IncorrectCommand(Messages.INVALID_ADD_ORDER_FORMAT_MESSAGE
                    + AddOrderCommand.MESSAGE_USAGE);
        }

        try {
            // To retrieve specific arguments from arguments
            String dishName = matcher.group(DISH_NAME_MATCHER_GROUP_NUM);
            int dishQty = Integer.parseInt(matcher.group(ORDER_QTY_MATCHER_GROUP_NUM));

            Dish orderedDish = menu.getDishFromName(dishName);
            if (orderedDish == null) {
                return new IncorrectCommand(Messages.DISH_NOT_FOUND);
            }

            Order order = new Order(orderedDish, dishQty);

            return new AddOrderCommand(order, ui, pantry, orderList);
        } catch (Exception e) {
            return new IncorrectCommand(Messages.INVALID_ADD_ORDER_FORMAT_MESSAGE
                    + AddOrderCommand.MESSAGE_USAGE + e.getMessage());
        }
    }

    /**
     * Parses the given arguments string to identify task index number.
     *
     * @param userInput arguments string to parse as index number
     * @param command expected String name of the command called
     * @return the parsed index number
     * @throws ParseException if no region of the args string could be found for the index
     * @throws NumberFormatException the args string region is not a valid number
     */
    private static int parseArgsAsDisplayedIndex(String userInput, String command)
            throws ParseException, NumberFormatException {
        String formattedString = userInput.replace(command, "").trim();
        return Integer.parseInt(formattedString);
    }

    /**
     * Extracts the quantity (numeric part) from a given string containing both quantity and unit.
     * @param qty A string containing both quantity and unit (e.g., "100g").
     * @return An integer representing the extracted quantity.
     */
    public static int extractQty(String qty) {
        return Integer.parseInt(qty.replaceAll("[^0-9]", ""));
    }

    /**
     * Extracts the unit (non-numeric part) from a given string containing both quantity and unit.
     * @param qty A string containing both quantity and unit (e.g., "100g").
     * @return A string representing the extracted unit.
     */
    public static String extractUnit(String qty) {
        return qty.replaceAll("[0-9]", "");
    }
}
