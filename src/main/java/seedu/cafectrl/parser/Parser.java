package seedu.cafectrl.parser;


import seedu.cafectrl.command.AddDishCommand;
import seedu.cafectrl.command.AddOrderCommand;
import seedu.cafectrl.command.BuyIngredientCommand;
import seedu.cafectrl.command.Command;
import seedu.cafectrl.command.DeleteDishCommand;
import seedu.cafectrl.command.EditPriceCommand;
import seedu.cafectrl.command.ExitCommand;
import seedu.cafectrl.command.HelpCommand;
import seedu.cafectrl.command.IncorrectCommand;
import seedu.cafectrl.command.ListIngredientCommand;
import seedu.cafectrl.command.ListMenuCommand;
import seedu.cafectrl.command.NextDayCommand;
import seedu.cafectrl.command.PreviousDayCommand;
import seedu.cafectrl.command.ShowSalesCommand;
import seedu.cafectrl.command.ShowSalesByDayCommand;
import seedu.cafectrl.command.ViewTotalStockCommand;

import seedu.cafectrl.data.CurrentDate;
import seedu.cafectrl.data.Sales;
import seedu.cafectrl.data.Order;
import seedu.cafectrl.data.OrderList;
import seedu.cafectrl.data.Pantry;
import seedu.cafectrl.parser.exception.ParserException;
import seedu.cafectrl.ui.ErrorMessages;
import seedu.cafectrl.ui.Messages;
import seedu.cafectrl.data.Menu;
import seedu.cafectrl.data.dish.Dish;
import seedu.cafectrl.data.dish.Ingredient;
import seedu.cafectrl.ui.Ui;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse everything received from the users on terminal
 * into a format that can be interpreted by other core classes
 */
public class Parser implements ParserUtil {
    private static final String COMMAND_ARGUMENT_REGEX = "(?<commandWord>\\S+)\\s*(?<arguments>.*)";

    //@@author DextheChik3n
    /** Add Dish Command Handler Patterns*/
    private static final String ADD_ARGUMENT_STRING = "name/(?<dishName>[A-Za-z0-9\\s]+) "
            + "price/(?<dishPrice>.+)\\s+"
            + "(?<ingredients>ingredient/[A-Za-z0-9\\s]+ qty/[A-Za-z0-9\\s]+"
            + "(?:,\\s*ingredient/[A-Za-z0-9\\s]+ qty/[A-Za-z0-9\\s]+)*)";
    private static final String DISH_NAME_MATCHER_GROUP_LABEL = "dishName";
    private static final String PRICE_MATCHER_GROUP_LABEL = "dishPrice";
    private static final String INGREDIENTS_MATCHER_GROUP_LABEL = "ingredients";
    private static final String INGREDIENT_ARGUMENT_STRING = "\\s*ingredient/(?<ingredientName>[A-Za-z0-9\\s]+) "
            + "qty/\\s*(?<ingredientQty>[0-9]+)\\s*(?<ingredientUnit>g|ml)\\s*";
    private static final String INGREDIENT_NAME_REGEX_GROUP_LABEL = "ingredientName";
    private static final String INGREDIENT_QTY_REGEX_GROUP_LABEL = "ingredientQty";
    private static final String INGREDIENT_UNIT_REGEX_GROUP_LABEL = "ingredientUnit";
    private static final String INGREDIENT_DIVIDER_REGEX = ",";

    /** Add Order Command Handler Patterns*/
    private static final int DISH_NAME_MATCHER_GROUP_NUM = 1;
    private static final int ORDER_QTY_MATCHER_GROUP_NUM = 2;
    private static final String ADD_ORDER_ARGUMENT_STRING = "name/([A-Za-z0-9\\s]+) "
            + "qty/([A-Za-z0-9\\s]+)";

    /** The rest of Command Handler Patterns*/
    private static final String LIST_INGREDIENTS_ARGUMENT_STRING = "(\\d+)";
    private static final String DELETE_ARGUMENT_STRING = "(\\d+)";
    private static final String EDIT_PRICE_ARGUMENT_STRING = "dish/(.*)\\sprice/(.*)";
    private static final String BUY_INGREDIENT_ARGUMENT_STRING = "(ingredient/[A-Za-z0-9\\s]+ qty/[A-Za-z0-9\\s]+"
            + "(?:, ingredient/[A-Za-z0-9\\s]+ qty/[A-Za-z0-9\\s]+)*)";
    private static final String SHOW_SALE_BY_DAY_ARGUMENT_STRING = "day/(\\d+)";

    //@@author ziyi105
    /**
     * Parse userInput and group it under commandWord and arguments
     * use commandWord to find the matching command and prepare the command
     *
     * @param menu The arraylist object created that stores current tasks
     * @param userInput The full user input String
     * @param ui The ui object created that handles I/O with the user
     * @param pantry The arraylist object created that stores current ingredients in stock
     * @return command requested by the user
     */
    public Command parseCommand(Menu menu, String userInput, Ui ui,
            Pantry pantry, Sales sales, CurrentDate currentDate) {
        Pattern userInputPattern = Pattern.compile(COMMAND_ARGUMENT_REGEX);
        final Matcher matcher = userInputPattern.matcher(userInput.trim());

        if (!matcher.matches()) {
            return new IncorrectCommand(ErrorMessages.UNKNOWN_COMMAND_MESSAGE, ui);
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        switch (commandWord) {

        case AddDishCommand.COMMAND_WORD:
            return prepareAdd(arguments, menu, ui);

        case DeleteDishCommand.COMMAND_WORD:
            return prepareDelete(menu, arguments, ui);

        case ListIngredientCommand.COMMAND_WORD:
            return prepareListIngredient(menu, arguments, ui);

        case ListMenuCommand.COMMAND_WORD:
            return prepareListMenu(menu, ui);

        case EditPriceCommand.COMMAND_WORD:
            return prepareEditPriceCommand(menu, arguments, ui);

        case ViewTotalStockCommand.COMMAND_WORD:
            return prepareViewTotalStock(ui, pantry);

        case BuyIngredientCommand.COMMAND_WORD:
            return prepareBuyIngredient(arguments, ui, pantry);

        case HelpCommand.COMMAND_WORD:
            return prepareHelpCommand(ui);

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand(ui, pantry);

        case AddOrderCommand.COMMAND_WORD:
            return prepareOrder(menu, arguments, ui, pantry, sales, currentDate);

        case NextDayCommand.COMMAND_WORD:
            return prepareNextDay(ui, sales, currentDate);

        case PreviousDayCommand.COMMAND_WORD:
            return preparePreviousDay(ui, currentDate);

        case ShowSalesCommand.COMMAND_WORD:
            return prepareShowSales(sales, menu, ui);

        case ShowSalesByDayCommand.COMMAND_WORD:
            return prepareShowSalesByDay(arguments, ui, sales, menu);

        default:
            return new IncorrectCommand(ErrorMessages.UNKNOWN_COMMAND_MESSAGE, ui);
        }
    }

    //All prepareCommand Classes
    //@@author Cazh1
    /**
     * Prepares the ListMenuCommand
     *
     * @param menu menu of the current session
     * @param ui ui of the current session
     * @return new ListMenuCommand
     */
    private static Command prepareListMenu(Menu menu, Ui ui) {
        return new ListMenuCommand(menu, ui);
    }

    //@@author ziyi105
    /**
     * Parse argument in the context of edit price command
     * @param menu menu of the current session
     * @param arguments string that matches group arguments
     * @return new EditDishCommand
     */
    private static Command prepareEditPriceCommand(Menu menu, String arguments, Ui ui) {
        Pattern editDishArgumentsPattern = Pattern.compile(EDIT_PRICE_ARGUMENT_STRING);
        Matcher matcher = editDishArgumentsPattern.matcher(arguments);

        // Checks whether the overall pattern of edit price arguments is correct
        if (!matcher.find()) {
            return new IncorrectCommand(ErrorMessages.MISSING_ARGUMENT_FOR_EDIT_PRICE, ui);
        }

        int dishIndexGroup = 1;
        int newPriceGroup = 2;
        int dishIndex;
        float newPrice;

        try {
            String dishIndexText = matcher.group(dishIndexGroup).trim();

            // Check whether the index is empty
            if (dishIndexText.equals("")) {
                return new IncorrectCommand(ErrorMessages.MISSING_DISH_IN_EDIT_PRICE, ui);
            }

            dishIndex = Integer.parseInt(dishIndexText);

            // Check whether the dish index is valid
            if (!menu.isValidDishIndex(dishIndex)) {
                return new IncorrectCommand(ErrorMessages.INVALID_DISH_INDEX, ui);
            }
        } catch (NumberFormatException e) {
            return new IncorrectCommand(ErrorMessages.WRONG_DISH_INDEX_TYPE_FOR_EDIT_PRICE, ui);
        }

        try {
            newPrice = parsePriceToFloat(matcher.group(newPriceGroup).trim());
        } catch (ParserException e) {
            return new IncorrectCommand(e.getMessage(), ui);
        }

        return new EditPriceCommand(dishIndex, newPrice, menu, ui);
    }

    //@@author DextheChik3n
    /**
     * Parses the user input text into ingredients to form a <code>Dish</code> that is added to the <code>Menu</code>
     * @param arguments string that matches group arguments
     * @return new AddDishCommand
     */
    private static Command prepareAdd(String arguments, Menu menu, Ui ui) {
        final Pattern addArgumentPatter = Pattern.compile(ADD_ARGUMENT_STRING);
        Matcher matcher = addArgumentPatter.matcher(arguments);

        try {
            // Checks whether the overall pattern of add arguments is correct
            if (!matcher.matches()) {
                return new IncorrectCommand(ErrorMessages.INVALID_ADD_DISH_FORMAT_MESSAGE
                        + AddDishCommand.MESSAGE_USAGE, ui);
            }

            // To retrieve specific arguments from arguments
            //the dishName needs .trim() because the regex accepts whitespaces in the "name/" argument
            String dishName = matcher.group(DISH_NAME_MATCHER_GROUP_LABEL).trim();
            float price = parsePriceToFloat(matcher.group(PRICE_MATCHER_GROUP_LABEL));
            String ingredientsListString = matcher.group(INGREDIENTS_MATCHER_GROUP_LABEL);

            if (isNameLengthInvalid(dishName)) {
                throw new ParserException(ErrorMessages.INVALID_DISH_NAME_LENGTH_MESSAGE);
            }

            if (isRepeatedDishName(dishName, menu)) {
                throw new ParserException(Messages.REPEATED_DISH_MESSAGE);
            }

            ArrayList<Ingredient> ingredients = parseIngredients(ingredientsListString);

            Dish dish = new Dish(dishName, ingredients, price);

            return new AddDishCommand(dish, menu, ui);
        } catch (NullPointerException e) {
            return new IncorrectCommand(ErrorMessages.NULL_NAME_DETECTED_MESSAGE, ui);
        } catch (Exception e) {
            return new IncorrectCommand(e.getMessage(), ui);
        }
    }

    /**
     * Parses the user's input text ingredients.
     * @param ingredientsListString user's input string of ingredients, multiple ingredients seperated by ',' is allowed
     * @return Ingredient objects that consists of the dish
     * @throws IllegalArgumentException if the input string of ingredients is in an incorrect format.
     * @throws ParserException if the input string does not match the constraints
     */
    private static ArrayList<Ingredient> parseIngredients(String ingredientsListString)
            throws IllegalArgumentException, ParserException {
        String[] inputIngredientList = {ingredientsListString};
        ArrayList<Ingredient> ingredients = new ArrayList<>();

        //check if there is more than 1 ingredient
        if (ingredientsListString.contains(INGREDIENT_DIVIDER_REGEX)) {
            //split the whole string of ingredients into separate individual ingredients
            inputIngredientList = ingredientsListString.split(INGREDIENT_DIVIDER_REGEX);
        }

        //Parsing each ingredient
        for (String inputIngredient: inputIngredientList) {
            final Pattern ingredientPattern = Pattern.compile(INGREDIENT_ARGUMENT_STRING);
            Matcher ingredientMatcher = ingredientPattern.matcher(inputIngredient);

            if (!ingredientMatcher.matches()) {
                throw new ParserException(ErrorMessages.INVALID_ADD_DISH_FORMAT_MESSAGE
                        + AddDishCommand.MESSAGE_USAGE);
            }

            String ingredientName = ingredientMatcher.group(INGREDIENT_NAME_REGEX_GROUP_LABEL).trim();
            String ingredientQtyString = ingredientMatcher.group(INGREDIENT_QTY_REGEX_GROUP_LABEL);
            String ingredientUnit = ingredientMatcher.group(INGREDIENT_UNIT_REGEX_GROUP_LABEL);

            int ingredientQty = Integer.parseInt(ingredientQtyString);

            if (isNameLengthInvalid(ingredientName)) {
                throw new ParserException(ErrorMessages.INVALID_INGREDIENT_NAME_LENGTH_MESSAGE);
            }

            if (isRepeatedIngredientName(ingredientName, ingredients)) {
                continue;
            }

            Ingredient ingredient = new Ingredient(ingredientName, ingredientQty, ingredientUnit);

            ingredients.add(ingredient);
        }

        return ingredients;
    }

    /**
     * Converts text of price to float while also checking if the price input is within reasonable range
     * @param priceText text input for price argument
     * @return price in float format
     * @throws ParserException if price is not within reasonable range
     */
    static float parsePriceToFloat(String priceText) throws ParserException {
        String trimmedPriceText = priceText.trim();

        final Pattern pricePattern = Pattern.compile("^-?[0-9]\\d*(\\.\\d{0,2})?$");
        Matcher priceMatcher = pricePattern.matcher(trimmedPriceText);

        // Check whether price text is empty
        if (priceText.equals("")) {
            throw new ParserException(ErrorMessages.MISSING_PRICE);
        }
        if (!priceMatcher.matches()) {
            throw new ParserException(ErrorMessages.WRONG_PRICE_TYPE_FOR_EDIT_PRICE);
        }

        float price;
        try {
            price = Float.parseFloat(trimmedPriceText);
        } catch (NumberFormatException e) {
            throw new ParserException(ErrorMessages.WRONG_PRICE_TYPE_FOR_EDIT_PRICE);
        }

        // Specify max and min value for price
        float maxPriceValue = (float) 1000000.00;
        float minPriceValue = (float) 0;

        // Check whether the price has up to 2 decimal place
        if (price > maxPriceValue) {
            throw new ParserException(ErrorMessages.LARGE_PRICE_MESSAGE);
        } else if (price < minPriceValue) {
            throw new ParserException(ErrorMessages.NEGATIVE_PRICE_MESSAGE);
        }

        return price;
    }

    /**
     * Checks in the menu if the dish name already exists in the menu.
     * @param inputDishName dish name entered by the user
     * @param menu contains all the existing Dishes
     * @return true if dish name already exists in menu, false otherwise
     * @throws NullPointerException if the input string is null
     */
    static boolean isRepeatedDishName(String inputDishName, Menu menu) throws NullPointerException {
        if (inputDishName == null) {
            throw new NullPointerException();
        }

        for (Dish dish: menu.getMenuItemsList()) {
            String menuDishNameLowerCase = dish.getName().toLowerCase();
            String inputDishNameLowerCase = inputDishName.toLowerCase();

            if (menuDishNameLowerCase.equals(inputDishNameLowerCase)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks in the menu if the dish name already exists in the menu.
     * @param inputName dish name entered by the user
     * @param ingredients contains all the existing Ingredients
     * @return true if ingredient name already exists in menu, false otherwise
     * @throws NullPointerException if the input string is null
     */
    static boolean isRepeatedIngredientName(String inputName, ArrayList<Ingredient> ingredients)
            throws NullPointerException {
        if (inputName == null) {
            throw new NullPointerException();
        }
        for (Ingredient ingredient: ingredients) {
            String ingredientNameLowerCase = ingredient.getName().toLowerCase();
            String inputIngredientNameLowerCase = inputName.toLowerCase();

            if (ingredientNameLowerCase.equals(inputIngredientNameLowerCase)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks the length of the name is too long
     * @param inputName name
     * @return true if the name is more than max character limit set, false otherwise
     * @throws NullPointerException if the input string is null
     */
    static boolean isNameLengthInvalid(String inputName) throws NullPointerException {
        int maxNameLength = 35;

        if (inputName == null) {
            throw new NullPointerException();
        }

        if (inputName.length() > maxNameLength) {
            return true;
        }

        return false;
    }

    //@@author NaychiMin
    /**
    * Parses arguments in the context of the ListIngredient command.
    * @param menu menu of the current session
    * @param arguments string that matches group arguments
    * @return the prepared command
    */
    private static Command prepareListIngredient(Menu menu, String arguments, Ui ui) {
        final Pattern prepareListPattern = Pattern.compile(LIST_INGREDIENTS_ARGUMENT_STRING);
        Matcher matcher = prepareListPattern.matcher(arguments.trim());

        if (!matcher.matches()) {
            return new IncorrectCommand(ErrorMessages.MISSING_ARGUMENT_FOR_LIST_INGREDIENTS, ui);
        }

        int dishIndex = Integer.parseInt(matcher.group(1));

        if (!menu.isValidDishIndex(dishIndex)) {
            return new IncorrectCommand(ErrorMessages.INVALID_DISH_INDEX, ui);
        }

        return new ListIngredientCommand(dishIndex, menu, ui);
    }

    //@@author ShaniceTang
    /**
     * Parses arguments in the context of the Delete command.
     *
     * @param menu menu of the current session
     * @param arguments string that matches group arguments
     * @return DeleteDishCommand if command is valid, IncorrectCommand otherwise
     */
    private static Command prepareDelete(Menu menu, String arguments, Ui ui) {
        Pattern deleteDishArgumentsPattern = Pattern.compile(DELETE_ARGUMENT_STRING);
        Matcher matcher = deleteDishArgumentsPattern.matcher(arguments.trim());

        // Checks whether the overall pattern of delete price arguments is correct
        if (!matcher.matches()) {
            return new IncorrectCommand(ErrorMessages.MISSING_ARGUMENT_FOR_DELETE, ui);
        }

        int listIndexArgGroup = 1;
        int dishIndex = Integer.parseInt(matcher.group(listIndexArgGroup));

        if (!menu.isValidDishIndex(dishIndex)) {
            return new IncorrectCommand(ErrorMessages.INVALID_DISH_INDEX, ui);
        }

        return new DeleteDishCommand(dishIndex, menu, ui);
    }

    private static Command prepareViewTotalStock(Ui ui, Pantry pantry) {
        return new ViewTotalStockCommand(pantry, ui);
    }

    private static Command prepareBuyIngredient(String arguments, Ui ui, Pantry pantry) {
        Pattern buyIngredientArgumentsPattern = Pattern.compile(BUY_INGREDIENT_ARGUMENT_STRING);
        Matcher matcher = buyIngredientArgumentsPattern.matcher(arguments.trim());

        if (!matcher.matches()) {

            return new IncorrectCommand(ErrorMessages.MISSING_ARGUMENT_FOR_BUY_INGREDIENT
                    + BuyIngredientCommand.MESSAGE_USAGE, ui);
        }

        String ingredientsListString = matcher.group(0);

        try {
            ArrayList<Ingredient> ingredients = parseIngredients(ingredientsListString);
            return new BuyIngredientCommand(ingredients, ui, pantry);
        } catch (Exception e) {
            return new IncorrectCommand(ErrorMessages.INVALID_ARGUMENT_FOR_BUY_INGREDIENT
                    + BuyIngredientCommand.MESSAGE_USAGE, ui);
        }
    }

    //@@author ziyi105
    private static Command prepareHelpCommand(Ui ui) {
        return new HelpCommand(ui);
    }

    //@@author Cazh1
    /**
     * Parses arguments in the context of the AddOrder command.
     *
     * @param menu menu of the current session
     * @param arguments string that matches group arguments
     * @param ui
     * @return AddOrderCommand if command is valid, IncorrectCommand otherwise
     */
    private static Command prepareOrder(Menu menu, String arguments, Ui ui,
            Pantry pantry, Sales sales, CurrentDate currentDate) {
        final Pattern addOrderArgumentPatter = Pattern.compile(ADD_ORDER_ARGUMENT_STRING);
        Matcher matcher = addOrderArgumentPatter.matcher(arguments);

        // Checks whether the overall pattern of add order arguments is correct
        if (!matcher.matches()) {
            return new IncorrectCommand(ErrorMessages.INVALID_ADD_ORDER_FORMAT_MESSAGE
                    + AddOrderCommand.MESSAGE_USAGE, ui);
        }

        OrderList orderList = setOrderList(currentDate, sales);

        try {
            // To retrieve specific arguments from arguments
            String dishName = matcher.group(DISH_NAME_MATCHER_GROUP_NUM);
            int dishQty = Integer.parseInt(matcher.group(ORDER_QTY_MATCHER_GROUP_NUM));

            Dish orderedDish = menu.getDishFromName(dishName);
            if (orderedDish == null) {
                return new IncorrectCommand(ErrorMessages.DISH_NOT_FOUND, ui);
            }

            Order order = new Order(orderedDish, dishQty);

            return new AddOrderCommand(order, ui, pantry, orderList, menu);
        } catch (Exception e) {
            return new IncorrectCommand(ErrorMessages.INVALID_ADD_ORDER_FORMAT_MESSAGE
                    + AddOrderCommand.MESSAGE_USAGE + e.getMessage(), ui);
        }
    }

    /**
     * Prepares PreviousDayCommand
     *
     * @param ui ui object of the current session
     * @param currentDate currentDate object of the current session
     * @return PreviousDayCommand if after day 1, IncorrectCommand if before
     */
    private static Command preparePreviousDay(Ui ui, CurrentDate currentDate) {
        int currentDay = currentDate.getCurrentDay();
        if (currentDay == 0) {
            return new IncorrectCommand(Messages.PREVIOUS_DAY_TIME_TRAVEL, ui);
        }
        return new PreviousDayCommand(ui, currentDate);
    }

    /**
     * Prepares NextDayCommand
     *
     * @param ui ui object of the current session
     * @param sales sales object of the current session
     * @param currentDate currentDate object of the current session
     * @return NextDayCommand
     */
    private static Command prepareNextDay(Ui ui, Sales sales, CurrentDate currentDate) {
        return new NextDayCommand(ui, sales, currentDate);
    }

    //@@author NaychiMin
    /**
     * Prepares a command to display all sales items.
     *
     * @param sale The Sales object containing sales data.
     * @param menu The Menu object representing the cafe's menu.
     * @param ui   The Ui object for user interface interactions.
     * @return A ShowSalesCommand instance for viewing all sales items.
     */
    private static Command prepareShowSales(Sales sale, Menu menu, Ui ui) {
        return new ShowSalesCommand(sale, ui, menu);
    }

    /**
     * Prepares a command to display sales items for a specific day.
     *
     * @param arguments The arguments containing the day for which sales are to be displayed.
     * @param ui        The Ui object for user interface interactions.
     * @param sales     The Sales object containing sales data.
     * @param menu      The Menu object representing the cafe's menu.
     * @return A ShowSalesByDayCommand instance for viewing sales items on a specific day.
     */
    private static Command prepareShowSalesByDay(String arguments, Ui ui, Sales sales, Menu menu) {
        final Pattern showSaleByDayPattern = Pattern.compile(SHOW_SALE_BY_DAY_ARGUMENT_STRING);
        Matcher matcher = showSaleByDayPattern.matcher(arguments.trim());

        if (!matcher.matches()) {
            return new IncorrectCommand(ErrorMessages.INVALID_SHOW_SALE_DAY_FORMAT_MESSAGE
                    + ShowSalesByDayCommand.MESSAGE_USAGE, ui);
        }

        try {
            int day = Integer.parseInt(matcher.group(1));
            return new ShowSalesByDayCommand(day, ui, sales, menu);
        } catch (NumberFormatException e) {
            return new IncorrectCommand(ErrorMessages.INVALID_DAY_FORMAT, ui);
        }
    }

    //@@author Cazh1
    /**
     * Sets the orderList according to the Day
     *
     * @param currentDate currentDate object of the current session
     * @param sales sales object of the current session, contains the orderLists
     * @return The respective orderList
     */
    private static OrderList setOrderList(CurrentDate currentDate, Sales sales) {
        int currentDay = currentDate.getCurrentDay();
        return sales.getOrderList(currentDay);
    }

    //@@author ShaniceTang
    /**
     * Extracts the quantity (numeric part) from a given string containing both quantity and unit.
     * @param qty A string containing both quantity and unit (e.g., "100g").
     * @return An integer representing the extracted quantity.
     */
    public static int extractQty(String qty) {
        return Integer.parseInt(qty.replaceAll("[^0-9]", ""));
    }

    //@@author ShaniceTang
    /**
     * Extracts the unit (non-numeric part) from a given string containing both quantity and unit.
     * @param qty A string containing both quantity and unit (e.g., "100g").
     * @return A string representing the extracted unit.
     */
    public static String extractUnit(String qty) {
        return qty.replaceAll("[0-9]", "");
    }
}
