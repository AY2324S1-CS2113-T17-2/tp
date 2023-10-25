package seedu.cafectrl.parser;

import org.junit.jupiter.api.Test;
import seedu.cafectrl.OrderList;
import seedu.cafectrl.command.AddDishCommand;
import seedu.cafectrl.command.Command;
import seedu.cafectrl.command.DeleteDishCommand;
import seedu.cafectrl.command.IncorrectCommand;
import seedu.cafectrl.command.ListIngredientCommand;
import seedu.cafectrl.data.Menu;
import seedu.cafectrl.data.Pantry;
import seedu.cafectrl.data.dish.Dish;
import seedu.cafectrl.data.dish.Ingredient;
import seedu.cafectrl.ui.Messages;
import seedu.cafectrl.ui.Ui;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Junit test for Parser.java
 */
class ParserTest {
    @Test
    public void parseCommand_validCommand_successfulCommandParse() {
        ArrayList<Dish> menuItems = new ArrayList<>();
        menuItems.add(new Dish("Chicken Rice",
                new ArrayList<>(Arrays.asList(new Ingredient("Rice", 50, "g"),
                        new Ingredient("Chicken", 100, "g"))), 8.0F));
        menuItems.add(new Dish("Chicken Sandwich",
                new ArrayList<>(Arrays.asList(new Ingredient("Lettuce", 100, "g"),
                        new Ingredient("Chicken", 50, "g"))), 5.0F));
        Menu menu = new Menu(menuItems);
        Ui ui = new Ui();
        Pantry pantry = new Pantry(ui);
        OrderList orderList = new OrderList();

        String userInput = "list_ingredients 1";
        Command result = Parser.parseCommand(menu, userInput, ui, pantry, orderList);

        assertTrue(result instanceof ListIngredientCommand);

        ListIngredientCommand listIngredientCommand = (ListIngredientCommand) result;
        int index = listIngredientCommand.index;
        assertEquals(1, index);
    }

    @Test
    public void parseCommand_missingIndex_returnsErrorMessage() {
        Menu menu = new Menu();
        Ui ui = new Ui();
        Pantry pantry = new Pantry(ui);
        OrderList orderList = new OrderList();
        String userInput = "list_ingredients";
        Command result = Parser.parseCommand(menu, userInput, ui, pantry, orderList);

        assertTrue(result instanceof IncorrectCommand);

        IncorrectCommand incorrectCommand = (IncorrectCommand) result;
        String feedbackToUser = incorrectCommand.feedbackToUser;
        assertEquals(Messages.MISSING_ARGUMENT_FOR_LIST_INGREDIENTS, feedbackToUser);
    }

    @Test
    public void parseCommand_invalidIndex_returnsErrorMessage() {
        Menu menu = new Menu();
        Ui ui = new Ui();
        Pantry pantry = new Pantry(ui);
        OrderList orderList = new OrderList();
        String userInput = "list_ingredients a";
        Command result = Parser.parseCommand(menu, userInput, ui, pantry, orderList);

        assertTrue(result instanceof IncorrectCommand);

        IncorrectCommand incorrectCommand = (IncorrectCommand) result;
        String feedbackToUser = incorrectCommand.feedbackToUser;
        assertEquals(Messages.MISSING_ARGUMENT_FOR_LIST_INGREDIENTS, feedbackToUser);
    }

    @Test
    public void parseCommand_indexOutOfBounds_returnsErrorMessage() {
        Menu menu = new Menu();
        Ui ui = new Ui();
        Pantry pantry = new Pantry(ui);
        OrderList orderList = new OrderList();
        String userInput = "list_ingredients 1";
        Command result = Parser.parseCommand(menu, userInput, ui, pantry, orderList);

        assertTrue(result instanceof IncorrectCommand);

        IncorrectCommand incorrectCommand = (IncorrectCommand) result;
        String feedbackToUser = incorrectCommand.feedbackToUser;
        assertEquals(Messages.INVALID_DISH_INDEX, feedbackToUser);
    }

    @Test
    public void parseCommand_validDeleteCommand_successfulCommandParse() {
        ArrayList<Dish> menuItems = new ArrayList<>();
        menuItems.add(new Dish("Chicken Rice",
                new ArrayList<>(Arrays.asList(new Ingredient("Rice", 50, "g"),
                        new Ingredient("Chicken", 100, "g"))), 8.0F));
        menuItems.add(new Dish("Chicken Sandwich",
                new ArrayList<>(Arrays.asList(new Ingredient("Lettuce", 100, "g"),
                        new Ingredient("Chicken", 50, "g"))), 5.0F));
        Menu menu = new Menu(menuItems);
        Ui ui = new Ui();
        Pantry pantry = new Pantry(ui);
        OrderList orderList = new OrderList();

        String userInput = "delete 1";
        Command result = Parser.parseCommand(menu, userInput, ui, pantry, orderList);

        assertTrue(result instanceof DeleteDishCommand);

        DeleteDishCommand deleteDishCommand = (DeleteDishCommand) result;
        int index = deleteDishCommand.index;
        assertEquals(1, index);
    }

    @Test
    public void parseCommand_missingDeleteIndex_returnsErrorMessage() {
        Menu menu = new Menu();
        Ui ui = new Ui();
        Pantry pantry = new Pantry(ui);
        OrderList orderList = new OrderList();
        String userInput = "delete";
        Command result = Parser.parseCommand(menu, userInput, ui, pantry, orderList);

        assertTrue(result instanceof IncorrectCommand);

        IncorrectCommand incorrectCommand = (IncorrectCommand) result;
        String feedbackToUser = incorrectCommand.feedbackToUser;
        assertEquals(Messages.MISSING_ARGUMENT_FOR_DELETE, feedbackToUser);
    }

    @Test
    public void parseCommand_invalidDeleteIndex_returnsErrorMessage() {
        Menu menu = new Menu();
        Ui ui = new Ui();
        Pantry pantry = new Pantry(ui);
        OrderList orderList = new OrderList();
        String userInput = "delete a";
        Command result = Parser.parseCommand(menu, userInput, ui, pantry, orderList);

        assertTrue(result instanceof IncorrectCommand);

        IncorrectCommand incorrectCommand = (IncorrectCommand) result;
        String feedbackToUser = incorrectCommand.feedbackToUser;
        assertEquals(Messages.MISSING_ARGUMENT_FOR_DELETE, feedbackToUser);
    }

    @Test
    public void parseCommand_deleteIndexOutOfBounds_returnsErrorMessage() {
        Menu menu = new Menu();
        Ui ui = new Ui();
        Pantry pantry = new Pantry(ui);
        OrderList orderList = new OrderList();
        String userInput = "delete 1";
        Command result = Parser.parseCommand(menu, userInput, ui, pantry, orderList);

        assertTrue(result instanceof IncorrectCommand);

        IncorrectCommand incorrectCommand = (IncorrectCommand) result;
        String feedbackToUser = incorrectCommand.feedbackToUser;
        assertEquals(Messages.INVALID_DISH_INDEX, feedbackToUser);
    }

    @Test
    void parseCommand_unrecognisedInput_unknownCommand() {
        Menu menu = new Menu();
        Dish testDish = new Dish("Chicken Rice", 2.50F);
        menu.addDish(testDish);
        String testUserInput = "random input";

        ArrayList<String> actualOutput = new ArrayList<>();
        Ui ui = new Ui() {
            @Override
            public void showToUser(String... message) {
                actualOutput.addAll(Arrays.asList(message));
            }
        };
        Pantry pantry = new Pantry(ui);
        OrderList orderList = new OrderList();

        Command commandReturned = Parser.parseCommand(menu, testUserInput, ui, pantry, orderList);
        commandReturned.execute();
        assertEquals(Messages.UNKNOWN_COMMAND_MESSAGE, actualOutput.get(0));
    }

    @Test
    void parseCommand_missingArgumentsForEditPrice_missingArgMsg() {
        Menu menu = new Menu();
        Dish testDish = new Dish("Chicken Rice", 2.50F);
        menu.addDish(testDish);
        String testUserInput = "edit_price index/1";

        ArrayList<String> actualOutput = new ArrayList<>();
        Ui ui = new Ui() {
            @Override
            public void showToUser(String... message) {
                actualOutput.addAll(Arrays.asList(message));
            }
        };
        Pantry pantry = new Pantry(ui);
        OrderList orderList = new OrderList();

        Command commandReturned = Parser.parseCommand(menu, testUserInput, ui, pantry, orderList);
        commandReturned.execute();
        assertEquals(Messages.MISSING_ARGUMENT_FOR_EDIT_PRICE, actualOutput.get(0));
    }

    @Test
    void parseCommand_invalidDishIndexForEditPrice_invalidIndexForEditPrice() {
        Menu menu = new Menu();
        Dish testDish = new Dish("Chicken Rice", 2.50F);
        menu.addDish(testDish);
        String testUserInput = "edit_price index/2 price/3";

        ArrayList<String> actualOutput = new ArrayList<>();
        Ui ui = new Ui() {
            @Override
            public void showToUser(String... message) {
                actualOutput.addAll(Arrays.asList(message));
            }
        };
        Pantry pantry = new Pantry(ui);
        OrderList orderList = new OrderList();

        Command commandReturned = Parser.parseCommand(menu, testUserInput, ui, pantry, orderList);
        commandReturned.execute();
        assertEquals(Messages.INVALID_DISH_INDEX, actualOutput.get(0));
    }

    @Test
    void parseCommand_validDishInputForAddDish_dishAddedToMenu() {
        Menu menu = new Menu();
        Ui ui = new Ui();
        Pantry pantry = new Pantry(ui);
        OrderList orderList = new OrderList();
        String testDishInputWithOneIngredient = "add name/Christmas Ham price/50.00 ingredient/Ham qty/1000g";
        Command outputCommand = Parser.parseCommand(menu, testDishInputWithOneIngredient, ui, pantry, orderList);
      
        //Test for correct Command type returned
        assertTrue(outputCommand instanceof AddDishCommand);
        //Test for 1 Dish added to Menu
        outputCommand.execute();
        assertEquals(1, menu.getSize());
        //Test for correct parsing of dish arguments
        Dish getOutputDish = menu.getDishFromId(0);
        assertEquals("Christmas Ham", getOutputDish.getName()); // Dish name test
        assertEquals((float) 50.0, getOutputDish.getPrice()); //Dish price test
        assertEquals("[Ham - 1000g]", getOutputDish.getIngredients().toString()); //Dish ingredients test
    }

    @Test
    void parseCommand_invalidDishInputForAddDish_noDishAddedToMenu() {
        Menu menu = new Menu();
        Ui ui = new Ui();
        Pantry pantry = new Pantry(ui);
        OrderList orderList = new OrderList();
        //input name/ argument wrongly
        String testDishInputWithOneIngredient = "add named/Christmas Ham price/50.00 ingredient/Ham qty/1kg";
        Command outputCommand = Parser.parseCommand(menu, testDishInputWithOneIngredient, ui, pantry, orderList);
        //Test for incorrect Command type returned
        assertFalse(outputCommand instanceof AddDishCommand);
        //Test for no dish added in menu
        outputCommand.execute();
        assertEquals(0, menu.getSize());
    }
}
