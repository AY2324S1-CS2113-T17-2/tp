package seedu.cafectrl.storage;

import seedu.cafectrl.CafeCtrl;
import seedu.cafectrl.data.Menu;
import seedu.cafectrl.data.Pantry;
import seedu.cafectrl.data.Sales;
import seedu.cafectrl.ui.ErrorMessages;
import seedu.cafectrl.ui.Messages;
import seedu.cafectrl.ui.Ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

//@@author ziyi105
/**
 * Handles loading and saving data for menu, orderList, pantryStock
 */
public class Storage {

    private static final Logger logger = Logger.getLogger(CafeCtrl.class.getName());
    private final boolean isHashingEnabled = true;
    private boolean isMenuTampered = false;
    private boolean isOrdersTampered = false;
    private boolean isPantryStockTampered = false;
    private boolean isHashStringTampered = false;
    private boolean isTamperedMessagePrinted = false;
    protected FileManager fileManager;
    protected Ui ui;

    public Storage (Ui ui) {
        this.fileManager = new FileManager(ui);
        this.ui = ui;
    }

    //@@author Cazh1
    private boolean isFileEmpty(ArrayList<String> encodedStringArrayList) {
        return encodedStringArrayList.isEmpty();
    }

    /**
     * Boolean to detect if the text save file has been tampered with
     *
     * @param encodedStringArrayList The arraylist of string read from text save file
     * @return true is the file's hash is not normal or does not match the newly generated hash, false otherwise
     */
    private boolean isFileCorrupted(ArrayList<String> encodedStringArrayList) {
        //Hash string is stored as last in the ArrayList
        int lastIndex = encodedStringArrayList.size() - 1;
        String hashString = encodedStringArrayList.get(lastIndex);

        //Checks if the saved Hash is abnormal
        if (((!hashString.matches("^[0-9]+$")) && (!hashString.matches("^-[0-9]+$"))) ||
                hashString.matches("^0{2,}$")) {
            return true;
        }

        try {
            int fileHash = Integer.parseInt(hashString);
            //Removes the saved Hash String for decoding
            encodedStringArrayList.remove(lastIndex);

            //Prepares String in same format as when encoding, generates Hash from the save file content
            String encodedMenuAsString = String.join(", ", encodedStringArrayList).trim();
            int encodedMenuHash = encodedMenuAsString.hashCode();

            //Checks if the generated Hash matches the saved Hash
            if (encodedMenuHash != fileHash) {
                return true;
            }
        } catch (Exception e) {
            isHashStringTampered = true;
            return true;
        }
        return false;
    }

    /**
     * Detects the areas of the save files that are tampered with
     * and prints out respective messages to the user, while minimizing repeats
     */
    public void detectTamper() {
        if (!isMenuTampered && !isOrdersTampered && !isPantryStockTampered && !isHashStringTampered) {
            return;
        }
        if (!isTamperedMessagePrinted) {
            ui.showToUser(Messages.SAVE_FILE_TAMPER_DETECTED);
            isTamperedMessagePrinted = true;
        }
        if (isHashStringTampered) {
            ui.showToUser(Messages.HASH_STRING_TAMPERED, Messages.HASH_STRING_MESSAGE, "");
            isHashStringTampered = false;
        }
        if (isMenuTampered) {
            ui.showToUser(Messages.SAVE_FILE_FORMAT_MENU);
            isMenuTampered = false;
        }
        if (isPantryStockTampered) {
            ui.showToUser(Messages.SAVE_FILE_FORMAT_PANTRY_STOCK);
            isPantryStockTampered = false;
        }
        if (isOrdersTampered) {
            ui.showToUser(Messages.SAVE_FILE_FORMAT_ORDERS);
            isOrdersTampered = false;
        }
        ui.showToUser("");
    }

    //@@author ShaniceTang
    /**
     * Loads menu data from a text file, decodes it, and returns it as a Menu object.
     *
     * @return A Menu object containing data from the file.
     */
    public Menu loadMenu() {
        logger.info("Loading menu...");
        try {
            ArrayList<String> encodedMenu = fileManager.readTextFile(FilePath.MENU_FILE_PATH);
            if (!isFileEmpty(encodedMenu) && isFileCorrupted(encodedMenu) && isHashingEnabled) {
                isMenuTampered = true;
                logger.log(Level.INFO, "Tampered Menu file");
                detectTamper();
            }
            return Decoder. decodeMenuData(encodedMenu);
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, "menu.txt not found!\n" + e.getMessage(), e);
            ui.showToUser(ErrorMessages.MENU_FILE_NOT_FOUND_MESSAGE, System.lineSeparator());
            return new Menu();
        }
    }

    /**
     * Encodes the provided menu data and writes it to a text file.
     *
     * @param menu The Menu object to be saved to the file.
     * @throws IOException if the file is not found in the specified file path.
     */
    private void saveMenu(Menu menu) throws IOException {
        logger.info("Saving menu...");
        fileManager.overwriteFile(FilePath.MENU_FILE_PATH, Encoder.encodeMenu(menu));
    }

    //@@author ziyi105
    /**
     * Read and decode pantryStock data from text file and pass it to the menu
     *
     * @return pantryStock with data from the file
     */
    public Pantry loadPantryStock() {
        try {
            ArrayList<String> encodedPantryStock = this.fileManager.readTextFile(FilePath.PANTRY_STOCK_FILE_PATH);
            if (!isFileEmpty(encodedPantryStock) && isFileCorrupted(encodedPantryStock) && isHashingEnabled) {
                isPantryStockTampered = true;
                logger.log(Level.INFO, "Tampered Pantry Stock file");
                detectTamper();
            }
            return Decoder.decodePantryStockData(encodedPantryStock);
        } catch (FileNotFoundException e) {
            ui.showToUser(ErrorMessages.PANTRY_FILE_NOT_FOUND_MESSAGE, System.lineSeparator());
            return new Pantry(ui);
        }
    }

    /**
     * Encode and write the data from PantryStock to the text file
     *
     * @param pantry pantry from current session
     * @throws IOException if the file is not found in the specified file path
     */
    private void savePantryStock(Pantry pantry) throws IOException {
        this.fileManager.overwriteFile(FilePath.PANTRY_STOCK_FILE_PATH, Encoder.encodePantryStock(pantry));
    }

    //@@author NaychiMin
    /**
     * Loads order lists from a text file, decodes it, and returns it as a Sales object.
     *
     * @return An OrderList object containing data from the file.
     */
    public Sales loadOrderList(Menu menu) {
        logger.info("Loading orders...");
        try {
            ArrayList<String> encodedOrderList = fileManager.readTextFile(FilePath.ORDERS_FILE_PATH);
            if (!isFileEmpty(encodedOrderList) && isFileCorrupted(encodedOrderList) && isHashingEnabled) {
                isOrdersTampered = true;
                logger.log(Level.INFO, "Tampered Order file");
                detectTamper();
            }
            return Decoder.decodeSales(encodedOrderList, menu);
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, "orders.txt not found!\n" + e.getMessage(), e);
            ui.showToUser(ErrorMessages.ORDER_LIST_FILE_NOT_FOUND_MESSAGE, System.lineSeparator());
            return new Sales();
        }
    }

    /**
     * Encodes the provided OrderList data from Sales object and writes it to a text file
     *
     * @param sales The Sales object containing the order to be saved to the file.
     * @throws IOException if the file is not found in the specified file path.
     */
    private void saveOrderList(Sales sales) throws IOException {
        logger.info("Saving orders...");
        this.fileManager.overwriteFile(FilePath.ORDERS_FILE_PATH, Encoder.encodeSales(sales));
    }

    //@@author ziyi105
    /**
     * Encode and write the data from menu, orderList and pantry to the respective text files
     *
     * @param menu menu from current session
     * @param sales sale object from current session
     * @param pantry pantry from current session
     */
    public void saveAll(Menu menu, Sales sales, Pantry pantry) {
        try {
            saveMenu(menu);
            saveOrderList(sales);
            savePantryStock(pantry);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Saving unsuccessful!\n" + e.getMessage(), e);
            ui.showToUser(e.getMessage());
        }
    }

}
