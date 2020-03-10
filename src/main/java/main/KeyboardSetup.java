package main;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;

public class KeyboardSetup {
    // public ReplyKeyboard replyKeyboard;
    public static ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

    public KeyboardSetup() {

    }

    public KeyboardSetup(ReplyKeyboardMarkup replyKeyboardMarkup) {
        KeyboardSetup.replyKeyboardMarkup = replyKeyboardMarkup;
    }

    private void setReplyKeyboardMarkup() {
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
    }

    public String buttons() {
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton();
        KeyboardButton keyboardButton1 = new KeyboardButton();
        keyboardButton.setRequestLocation(true);
        keyboardButton.setText(Constants.SHARE_LOCATION);
        keyboardButton.setText("Current location");
        keyboardButton1.setText("Other location");
        setReplyKeyboardMarkup();

        keyboard.clear();
        keyboardFirstRow.clear();
        keyboardFirstRow.add(keyboardButton);
        keyboardFirstRow.add(keyboardButton1);
        keyboard.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return "Choose in which place you want to know weather forecast";

    }


}
