package org.example;


import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.*;

public class Bot extends TelegramLongPollingBot {

    // Order step maps
    private final Map<Long, String> userOrders = new HashMap<>();
    private final Map<Long, String> userAddOns = new HashMap<>();
    private final Map<Long, String> userPopcornChicken = new HashMap<>();
    private final Map<Long, String> userNuggets = new HashMap<>();
    private final Map<Long, String> userSpicyNuggets = new HashMap<>();
    private final Map<Long, String> userDrumlettes = new HashMap<>();
    private final Map<Long, String> userMcSpicyCutlet = new HashMap<>();
    private final Map<Long, String> userChickenBreast = new HashMap<>();
    private final Map<Long, String> userCheeseTofu = new HashMap<>();
    private final Map<Long, String> userQuesadilla = new HashMap<>();
    private final Map<Long, String> userSauce = new HashMap<>();
    private final Map<Long, String> userCollectionTime = new HashMap<>();
    private final Map<Long, String> userCollectionMethod = new HashMap<>();
    private final Map<Long, String> userActualUsername = new HashMap<>();
    private final Map<Long, String> userRemarks = new HashMap<>();



    // Store user location (room/lobby) and Telegram handle
    private final Map<Long, String> userLocation = new HashMap<>();
    private final Map<Long, String> userTelegram = new HashMap<>();

    // State map to track next user input step
    private final Map<Long, String> userState = new HashMap<>();

    // For editing the Add-ons message in-place
    private final Map<Long, Integer> addOnMessageIds = new HashMap<>();

    @Override
    public String getBotUsername() {
        return "Enter your username";
    }

    public String getBotToken() {
        return "TELEGRAM_BOT_TOKEN";
    }








    @Override
    public void onUpdateReceived(Update update) {
        Long chatId;




        if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();



            // Retrieve the actual Telegram username
            String actualUsername = update.getMessage().getFrom().getUserName();
            // Store it in the map (it might be null if the user has no username)
            userActualUsername.put(chatId, actualUsername != null ? actualUsername : "Not set");

            String userMessage = update.getMessage().getText();



            // 1) Awaiting Collection Time
            if ("AWAITING_COLLECTION_TIME".equals(userState.get(chatId))) {
                userCollectionTime.put(chatId, userMessage);
                userState.remove(chatId);
                // Ask for Collection Method next
                sendCollectionMethodOptions(chatId);
                return;
            }

            // 2) Awaiting Room Number or Lobby
            if ("AWAITING_LOCATION".equals(userState.get(chatId))) {
                userLocation.put(chatId, userMessage);
                userState.put(chatId, "AWAITING_REMARK");
                sendText(chatId, "Please type any remarks (or type 'none' if you have no remarks):");
                return;
            }

            // 3) Awaiting Remark
            if ("AWAITING_REMARK".equals(userState.get(chatId))) {
                userRemarks.put(chatId, userMessage);
                userState.remove(chatId);
                sendFinalOrderSummary(chatId);
                return;
            }



            // Normal commands
            if (userMessage.equals("start") || userMessage.equals("/start")) {
                sendText(chatId, "Hello lovelies! Are you ready to order? 😊\n\nPlease select a noodle option below.");
                sendNoodleOptions(chatId);
            }
        }
        else if (update.hasCallbackQuery()) {
            CallbackQuery query = update.getCallbackQuery();
            chatId = query.getMessage().getChatId();
            String data = query.getData();

            if (data.startsWith("noodle_")) {
                handleNoodleSelection(chatId, data);
            } else if (data.startsWith("addon_")) {
                handleAddOnSelection(chatId, data);
            } else if (data.startsWith("popcorn_")) {
                handlePopcornChickenSelection(chatId, data);
            } else if (data.startsWith("nugget_")) {
                handleNuggetSelection(chatId, data);
            } else if (data.startsWith("spicynugget_")) {
                handleSpicyNuggetSelection(chatId, data);
            } else if (data.startsWith("drumlette_")) {
                handleDrumletteSelection(chatId, data);
            } else if (data.startsWith("mcspicy_")) {
                handleMcSpicyCutletSelection(chatId, data);
            } else if (data.startsWith("chickenbreast_")) {
                handleChickenBreastSelection(chatId, data);
            } else if (data.startsWith("cheesetofu_")) {
                handleCheeseTofuSelection(chatId, data);
            } else if (data.startsWith("quesadilla_")) {
                handleQuesadillaSelection(chatId, data);
            } else if (data.startsWith("sauce_")) {
                handleSauceSelection(chatId, data);
            } else if (data.startsWith("method_")) {
                handleCollectionMethodSelection(chatId, data);
            }
        }
    }

    // ----------------- Helper Methods -----------------

    private void sendText(Long chatId, String text) {
        SendMessage sm = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .parseMode("Markdown")
                .build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    // ----------------- Noodle Selection -----------------

    private void sendNoodleOptions(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("🍜 *Select your Noodles:*");
        message.setParseMode("Markdown");

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(List.of(createButton("Indomie ($2)", "noodle_1")));
        keyboard.add(List.of(createButton("Samyang (Original) ($3.50)", "noodle_2")));
        keyboard.add(List.of(createButton("Samyang (Carbonara) ($3.50)", "noodle_3")));
        keyboard.add(List.of(createButton("Creamy Tomyum ($2)", "noodle_4")));
        keyboard.add(List.of(createButton("Shin Ramyun ($3)", "noodle_5")));
        keyboard.add(List.of(createButton("No Noodles (Boooo)", "noodle_6")));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleNoodleSelection(Long chatId, String callbackData) {
        String selectedNoodle = switch (callbackData) {
            case "noodle_1" -> "Indomie ($2)";
            case "noodle_2" -> "Samyang (Original) ($3.50)";
            case "noodle_3" -> "Samyang (Carbonara) ($3.50)";
            case "noodle_4" -> "Creamy Tomyum ($2)";
            case "noodle_5" -> "Shin Ramyun ($3)";
            case "noodle_6" -> "No Noodles (Boooo)";
            default -> null;
        };

        userOrders.put(chatId, selectedNoodle);
        sendText(chatId, "✅ You selected: *" + selectedNoodle + "*\nNow, would you like to add any extras?");
        sendAddOns(chatId);
    }

    // ----------------- Add-ons (In-Place Edit) -----------------

    // Show a red emoji on the "No Add-ons" button only if selected
    private String getNoAddOnText(Long chatId) {
        String currentAddOns = userAddOns.getOrDefault(chatId, "");
        if ("No add-ons selected.".equals(currentAddOns)) {
            return "🚫 No Add-ons";
        } else {
            return "No Add-ons";
        }
    }

    private void sendAddOns(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("🥢 *Would you like any add-ons?*\n(Click to select/unselect, then confirm)");
        message.setParseMode("Markdown");

        InlineKeyboardMarkup markup = buildAddOnKeyboard(chatId);
        message.setReplyMarkup(markup);

        try {
            Message sentMsg = execute(message);
            addOnMessageIds.put(chatId, sentMsg.getMessageId());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup buildAddOnKeyboard(Long chatId) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(List.of(createButton(getToggleText(chatId, "addon_1", "Fried Shallots (Free)"), "addon_1")));
        keyboard.add(List.of(createButton(getToggleText(chatId, "addon_2", "Chilli Padi (Free)"), "addon_2")));
        keyboard.add(List.of(createButton(getToggleText(chatId, "addon_3", "Egg (+$1)"), "addon_3")));
        keyboard.add(List.of(createButton(getNoAddOnText(chatId), "addon_no")));
        keyboard.add(List.of(createButton("Confirm", "addon_confirm")));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }

    private void handleAddOnSelection(Long chatId, String callbackData) {
        if (callbackData.equals("addon_confirm")) {
            String addOns = userAddOns.getOrDefault(chatId, "No add-ons selected.");
            sendText(chatId, "✅ Your final selection:\n"
                    + "🍜 Noodles: *" + userOrders.get(chatId) + "*\n"
                    + "➕ Add-ons: " + addOns
                    + "\n\nMoving to Popcorn Chicken selection... 🍗");
            sendPopcornChickenOptions(chatId);
            return;
        }

        if (callbackData.equals("addon_no")) {
            userAddOns.put(chatId, "No add-ons selected.");
        } else {
            String currentAddOns = userAddOns.getOrDefault(chatId, "");
            Set<String> selectedAddOns = new HashSet<>();
            if (!currentAddOns.isBlank() && !currentAddOns.equals("No add-ons selected.")) {
                selectedAddOns.addAll(Arrays.asList(currentAddOns.split(",\\s*")));
            }
            switch (callbackData) {
                case "addon_1" -> toggleSelection(selectedAddOns, "Fried Shallots (Free)");
                case "addon_2" -> toggleSelection(selectedAddOns, "Chilli Padi (Free)");
                case "addon_3" -> toggleSelection(selectedAddOns, "Egg (+$1)");
            }
            if (selectedAddOns.isEmpty()) {
                userAddOns.put(chatId, "No add-ons selected.");
            } else {
                userAddOns.put(chatId, String.join(", ", selectedAddOns));
            }
        }
        editAddOnMessage(chatId);
    }

    private void editAddOnMessage(Long chatId) {
        InlineKeyboardMarkup newMarkup = buildAddOnKeyboard(chatId);
        EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
        editMarkup.setChatId(chatId.toString());
        editMarkup.setMessageId(addOnMessageIds.get(chatId));
        editMarkup.setReplyMarkup(newMarkup);

        try {
            execute(editMarkup);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void toggleSelection(Set<String> selectedAddOns, String item) {
        if (selectedAddOns.contains(item)) {
            selectedAddOns.remove(item);
        } else {
            selectedAddOns.add(item);
        }
    }

    private String getToggleText(Long chatId, String callbackData, String displayText) {
        String addOns = userAddOns.getOrDefault(chatId, "");
        return addOns.contains(displayText) ? "✅ " + displayText : displayText;
    }

    // ----------------- Popcorn Chicken -----------------

    private void sendPopcornChickenOptions(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("🍗 *Would you like some Popcorn Chicken?*");
        message.setParseMode("Markdown");

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(List.of(createButton("Small ($2)", "popcorn_1")));
        keyboard.add(List.of(createButton("Large ($5)", "popcorn_2")));
        keyboard.add(List.of(createButton("No Popcorn Chicken", "popcorn_no")));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handlePopcornChickenSelection(Long chatId, String callbackData) {
        String selectedPopcorn = switch (callbackData) {
            case "popcorn_1" -> "Small Popcorn Chicken ($2)";
            case "popcorn_2" -> "Large Popcorn Chicken ($5)";
            case "popcorn_no" -> "No Popcorn Chicken selected.";
            default -> null;
        };

        userPopcornChicken.put(chatId, selectedPopcorn);
        sendText(chatId, "✅ Order Summary so far:\n"
                + "🍜 Noodles: *" + userOrders.get(chatId) + "*\n"
                + "➕ Add-ons: " + userAddOns.getOrDefault(chatId, "No add-ons selected.")
                + "\n🍗 Popcorn Chicken: " + selectedPopcorn
                + "\n\nMoving to Nuggets selection...");
        sendNuggetsOptions(chatId);
    }

    // ----------------- Regular Nuggets -----------------

    private void sendNuggetsOptions(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("🍗 *Would you like some Nuggets?*");
        message.setParseMode("Markdown");

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(List.of(createButton("1 ($0.50)", "nugget_1")));
        keyboard.add(List.of(createButton("2 ($1)", "nugget_2")));
        keyboard.add(List.of(createButton("3 ($1.50)", "nugget_3")));
        keyboard.add(List.of(createButton("5 ($2)", "nugget_4")));
        keyboard.add(List.of(createButton("No Nuggets", "nugget_no")));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleNuggetSelection(Long chatId, String callbackData) {
        String selectedNuggets = switch (callbackData) {
            case "nugget_1" -> "1 Nugget ($0.50)";
            case "nugget_2" -> "2 Nuggets ($1)";
            case "nugget_3" -> "3 Nuggets ($1.50)";
            case "nugget_4" -> "5 Nuggets ($2)";
            case "nugget_no" -> "No Nuggets selected.";
            default -> "";
        };

        userNuggets.put(chatId, selectedNuggets);
        sendText(chatId, "Got it! Now let's check if you want *Spicy Nuggets* too!");
        sendSpicyNuggetsOptions(chatId);
    }

    // ----------------- Spicy Nuggets -----------------

    private void sendSpicyNuggetsOptions(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("🌶 *Would you like some Spicy Nuggets?*");
        message.setParseMode("Markdown");

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(List.of(createButton("1 ($0.50)", "spicynugget_1")));
        keyboard.add(List.of(createButton("2 ($1)", "spicynugget_2")));
        keyboard.add(List.of(createButton("3 ($1.50)", "spicynugget_3")));
        keyboard.add(List.of(createButton("5 ($2)", "spicynugget_4")));
        keyboard.add(List.of(createButton("No Spicy Nuggets", "spicynugget_no")));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleSpicyNuggetSelection(Long chatId, String callbackData) {
        String selectedSpicyNuggets = switch (callbackData) {
            case "spicynugget_1" -> "1 Spicy Nugget ($0.50)";
            case "spicynugget_2" -> "2 Spicy Nuggets ($1)";
            case "spicynugget_3" -> "3 Spicy Nuggets ($1.50)";
            case "spicynugget_4" -> "5 Spicy Nuggets ($2)";
            case "spicynugget_no" -> "No Spicy Nuggets selected.";
            default -> "";
        };

        userSpicyNuggets.put(chatId, selectedSpicyNuggets);
        sendText(chatId, "Almost there! Now, would you like some *Drumlettes*?");
        sendDrumletteOptions(chatId);
    }

    // ----------------- Drumlettes -----------------

    private void sendDrumletteOptions(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("🥁 *Would you like some Drumlettes?*");
        message.setParseMode("Markdown");

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(List.of(createButton("1 ($1)", "drumlette_1")));
        keyboard.add(List.of(createButton("2 ($2)", "drumlette_2")));
        keyboard.add(List.of(createButton("3 ($3)", "drumlette_3")));
        keyboard.add(List.of(createButton("5 ($4)", "drumlette_4")));
        keyboard.add(List.of(createButton("No Drumlettes", "drumlette_no")));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleDrumletteSelection(Long chatId, String callbackData) {
        String selectedDrumlettes = switch (callbackData) {
            case "drumlette_1" -> "1 Drumlette ($1)";
            case "drumlette_2" -> "2 Drumlettes ($2)";
            case "drumlette_3" -> "3 Drumlettes ($3)";
            case "drumlette_4" -> "5 Drumlettes ($4)";
            case "drumlette_no" -> "No Drumlettes selected.";
            default -> "";
        };

        userDrumlettes.put(chatId, selectedDrumlettes);
        sendText(chatId, "Almost done! Now, would you like some *McSpicy Cutlet*?");
        sendMcSpicyCutletOptions(chatId);
    }

    // ----------------- McSpicy Cutlet -----------------

    private void sendMcSpicyCutletOptions(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("🍔 *Would you like some McSpicy Cutlet?*");
        message.setParseMode("Markdown");

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(List.of(createButton("1 ($2.50)", "mcspicy_1")));
        keyboard.add(List.of(createButton("2 ($5)", "mcspicy_2")));
        keyboard.add(List.of(createButton("3 ($7.50)", "mcspicy_3")));
        keyboard.add(List.of(createButton("No McSpicy Cutlet", "mcspicy_no")));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleMcSpicyCutletSelection(Long chatId, String callbackData) {
        String selectedMcSpicyCutlet = switch (callbackData) {
            case "mcspicy_1" -> "1 McSpicy Cutlet ($2.50)";
            case "mcspicy_2" -> "2 McSpicy Cutlet ($5)";
            case "mcspicy_3" -> "3 McSpicy Cutlet ($7.50)";
            case "mcspicy_no" -> "No McSpicy Cutlet selected.";
            default -> "";
        };

        userMcSpicyCutlet.put(chatId, selectedMcSpicyCutlet);
        sendText(chatId, "Almost done! Now, would you like some *Chicken Breast*?");
        sendChickenBreastOptions(chatId);
    }

    // ----------------- Chicken Breast -----------------

    private void sendChickenBreastOptions(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("🍗 *Would you like some Chicken Breast?*");
        message.setParseMode("Markdown");

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(List.of(createButton("1 ($2)", "chickenbreast_1")));
        keyboard.add(List.of(createButton("2 ($4)", "chickenbreast_2")));
        keyboard.add(List.of(createButton("3 ($6)", "chickenbreast_3")));
        keyboard.add(List.of(createButton("No Chicken Breast", "chickenbreast_no")));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleChickenBreastSelection(Long chatId, String callbackData) {
        String selectedChickenBreast = switch (callbackData) {
            case "chickenbreast_1" -> "1 Chicken Breast ($2)";
            case "chickenbreast_2" -> "2 Chicken Breasts ($4)";
            case "chickenbreast_3" -> "3 Chicken Breasts ($6)";
            case "chickenbreast_no" -> "No Chicken Breast selected.";
            default -> "";
        };

        userChickenBreast.put(chatId, selectedChickenBreast);
        sendText(chatId, "Almost done! Now, would you like some *Cheese Tofu*?");
        sendCheeseTofuOptions(chatId);
    }

    // ----------------- Cheese Tofu -----------------

    private void sendCheeseTofuOptions(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("🧀 *Would you like some Cheese Tofu?*");
        message.setParseMode("Markdown");

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(List.of(createButton("3 ($2)", "cheesetofu_1")));
        keyboard.add(List.of(createButton("6 ($4)", "cheesetofu_2")));
        keyboard.add(List.of(createButton("9 ($6)", "cheesetofu_3")));
        keyboard.add(List.of(createButton("No Cheese Tofu", "cheesetofu_no")));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleCheeseTofuSelection(Long chatId, String callbackData) {
        String selectedCheeseTofu = switch (callbackData) {
            case "cheesetofu_1" -> "3 Cheese Tofu ($2)";
            case "cheesetofu_2" -> "6 Cheese Tofu ($4)";
            case "cheesetofu_3" -> "9 Cheese Tofu ($6)";
            case "cheesetofu_no" -> "No Cheese Tofu selected.";
            default -> "";
        };

        userCheeseTofu.put(chatId, selectedCheeseTofu);
        sendText(chatId, "Almost done! Now, would you like some *Quesadilla*?");
        sendQuesadillaOptions(chatId);
    }

    // ----------------- Quesadilla -----------------

    private void sendQuesadillaOptions(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("🌮 *Would you like some Quesadilla?*");
        message.setParseMode("Markdown");

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(List.of(createButton("1 ($6)", "quesadilla_1")));
        keyboard.add(List.of(createButton("2 ($12)", "quesadilla_2")));
        keyboard.add(List.of(createButton("3 ($18)", "quesadilla_3")));
        keyboard.add(List.of(createButton("No Quesadilla", "quesadilla_no")));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleQuesadillaSelection(Long chatId, String callbackData) {
        String selectedQuesadilla = switch (callbackData) {
            case "quesadilla_1" -> "1 Quesadilla ($6)";
            case "quesadilla_2" -> "2 Quesadillas ($12)";
            case "quesadilla_3" -> "3 Quesadillas ($18)";
            case "quesadilla_no" -> "No Quesadilla selected.";
            default -> "";
        };

        userQuesadilla.put(chatId, selectedQuesadilla);
        // After Quesadilla, proceed to Sauce section.
        sendSauceOptions(chatId);
    }

    // ----------------- Sauce Section -----------------

    private void sendSauceOptions(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("🥫 *Choose your Sauce:*");
        message.setParseMode("Markdown");

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(List.of(createButton("Nacho Cheese ($1)", "sauce_nacho")));
        keyboard.add(List.of(createButton("Truffle Mayo ($1.50)", "sauce_truffle")));
        keyboard.add(List.of(createButton("Chilli Padi Mayo ($1.50)", "sauce_chilli_padi")));
        keyboard.add(List.of(createButton("Chilli Sauce", "sauce_chilli")));
        keyboard.add(List.of(createButton("Tomato Sauce", "sauce_tomato")));
        keyboard.add(List.of(createButton("No Sauce", "sauce_none")));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleSauceSelection(Long chatId, String callbackData) {
        String selectedSauce = switch (callbackData) {
            case "sauce_nacho" -> "Nacho Cheese ($1)";
            case "sauce_truffle" -> "Truffle Mayo ($1.50)";
            case "sauce_chilli_padi" -> "Chilli Padi Mayo ($1.50)";
            case "sauce_chilli" -> "Chilli Sauce";
            case "sauce_tomato" -> "Tomato Sauce";
            case "sauce_none" -> "No Sauce selected.";
            default -> "";
        };

        userSauce.put(chatId, selectedSauce);
        // Prompt for collection time next
        sendCollectionTimePrompt(chatId);
    }

    // ----------------- Collection Time -----------------

    private void sendCollectionTimePrompt(Long chatId) {
        sendText(chatId, "⏰ Please type the collection time you want.");
        userState.put(chatId, "AWAITING_COLLECTION_TIME");
    }

    // ----------------- Collection Method -----------------

    private void sendCollectionMethodOptions(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("🚚 *Choose your Collection Method:*");
        message.setParseMode("Markdown");

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        // 1) Self-Collection
        keyboard.add(List.of(createButton(
                "Self-Collection (Free)\nText @shivanicccc on tele for room number",
                "method_self")));

        // 2) Delivery to lobby or B/C/D/E ($0.50)
        keyboard.add(List.of(createButton(
                "Delivery to lobby or B/C/D/E ($0.50)",
                "method_lobby")));

        // 3) Delivery to A block ($1)
        keyboard.add(List.of(createButton(
                "Delivery to A block ($1)",
                "method_ablock")));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleCollectionMethodSelection(Long chatId, String callbackData) {
        String method = switch (callbackData) {
            case "method_self" -> "Self-Collection (Free)";
            case "method_lobby" -> "Delivery to lobby or B/C/D/E ($0.50)";
            case "method_ablock" -> "Delivery to A block ($1)";
            default -> "No Method Selected";
        };

        userCollectionMethod.put(chatId, method);

        // Next ask for Room Number or Lobby
        userState.put(chatId, "AWAITING_LOCATION");
        sendText(chatId, "Please type Room Number or Lobby:");
    }

    private double extractPrice(String item) {
        if (item == null || item.trim().isEmpty() || item.toLowerCase().startsWith("no ")) {
            return 0.0;
        }
        // This regex looks for a dollar sign inside parentheses, e.g. ($3.50) or (+$1)
        Pattern pattern = Pattern.compile("\\(.*?\\$([\\d\\.]+).*?\\)");
        Matcher matcher = pattern.matcher(item);
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }

    // If a field contains multiple items separated by commas (like add-ons), split and sum their prices.
    private double extractMultiplePrice(String text) {
        if (text == null || text.isBlank() || text.toLowerCase().startsWith("no ")) {
            return 0.0;
        }
        double sum = 0.0;
        String[] parts = text.split(",\\s*");
        for (String part : parts) {
            sum += extractPrice(part);
        }
        return sum;
    }

    // Sums up the prices from all the order fields for a given chat.
    private double calculateTotalPrice(Long chatId) {
        double total = 0.0;
        total += extractMultiplePrice(userOrders.get(chatId));
        total += extractMultiplePrice(userAddOns.get(chatId));
        total += extractMultiplePrice(userPopcornChicken.get(chatId));
        total += extractMultiplePrice(userNuggets.get(chatId));
        total += extractMultiplePrice(userSpicyNuggets.get(chatId));
        total += extractMultiplePrice(userDrumlettes.get(chatId));
        total += extractMultiplePrice(userMcSpicyCutlet.get(chatId));
        total += extractMultiplePrice(userChickenBreast.get(chatId));
        total += extractMultiplePrice(userCheeseTofu.get(chatId));
        total += extractMultiplePrice(userQuesadilla.get(chatId));
        total += extractMultiplePrice(userSauce.get(chatId));
        // Also add any collection method fee (e.g. "$0.50" or "$1")
        total += extractMultiplePrice(userCollectionMethod.get(chatId));
        return total;
    }

    // ----------------- Final Order Summary -----------------

    private static final String ADMIN_CHAT_ID = ""; // Replace with your group chat id

    private void sendFinalOrderSummary(Long chatId) {
        String collectionTime = userCollectionTime.getOrDefault(chatId, "Not specified");
        String collectionMethod = userCollectionMethod.getOrDefault(chatId, "Not specified");
        String location = userLocation.getOrDefault(chatId, "Not specified");
        String actualUsername = userActualUsername.getOrDefault(chatId, "Not set");
        String remark = userRemarks.getOrDefault(chatId, "None");
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        StringBuilder sb = new StringBuilder("✅ Final Order Summary:\n\n");

        // Map containing all the order items
        Map<String, String> orderItems = new HashMap<>();
        orderItems.put("🍜 Noodles", userOrders.get(chatId));
        orderItems.put("➕ Add-ons", userAddOns.get(chatId));
        orderItems.put("🍗 Popcorn Chicken", userPopcornChicken.get(chatId));
        orderItems.put("🍗 Nuggets", userNuggets.get(chatId));
        orderItems.put("🌶 Spicy Nuggets", userSpicyNuggets.get(chatId));
        orderItems.put("🥁 Drumlettes", userDrumlettes.get(chatId));
        orderItems.put("🍔 McSpicy Cutlet", userMcSpicyCutlet.get(chatId));
        orderItems.put("🍗 Chicken Breast", userChickenBreast.get(chatId));
        orderItems.put("🧀 Cheese Tofu", userCheeseTofu.get(chatId));
        orderItems.put("🌮 Quesadilla", userQuesadilla.get(chatId));
        orderItems.put("🥫 Sauce", userSauce.get(chatId));

        // Append only the items that have been ordered
        orderItems.forEach((key, value) -> {
            if (value != null && !value.toLowerCase().startsWith("no ")) {
                sb.append(key).append(": ").append(value).append("\n");
            }
        });

        // Always show these details
        sb.append("\n⏰ Collection Time: ").append(collectionTime).append("\n");
        sb.append("🚚 Collection Method: ").append(collectionMethod).append("\n");
        sb.append("🏠 Room/Lobby: ").append(location).append("\n");

        if (!remark.equalsIgnoreCase("none")) {
            sb.append("📝 Remark: ").append(remark).append("\n");
        }

        // Calculate and append the total price
        double totalPrice = calculateTotalPrice(chatId);
        sb.append("\n💵 Total Price: $").append(String.format("%.2f", totalPrice)).append("\n");

        sb.append("\nPaynow to 90045396.\n\n");
        sb.append("Thank you for your order! 🎉 \n\n");
        sb.append("Type start for new order");

        String finalOrderSummary = sb.toString();

        // Send summary to the user
        sendText(chatId, finalOrderSummary);

        // Send summary to the admin group for monitoring
        sendText(Long.parseLong(ADMIN_CHAT_ID), "New order from @" + actualUsername + ":\n" + finalOrderSummary);

        // Create the row for Google Sheets
        List<Object> row = Arrays.asList(
                userOrders.get(chatId),
                userAddOns.get(chatId),
                userPopcornChicken.get(chatId),
                userNuggets.get(chatId),
                userSpicyNuggets.get(chatId),
                userDrumlettes.get(chatId),
                userMcSpicyCutlet.get(chatId),
                userChickenBreast.get(chatId),
                userCheeseTofu.get(chatId),
                userQuesadilla.get(chatId),
                userSauce.get(chatId),
                userCollectionTime.get(chatId),
                userCollectionMethod.get(chatId),
                userLocation.get(chatId),
                remark,
                actualUsername,
                timestamp
        );

        System.out.println("Row to append: " + row);
        try {
            SheetsUtil.appendOrderData(row);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
