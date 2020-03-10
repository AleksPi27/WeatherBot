package main;

import main.parcers.OpenWeatherMapJsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

public class Bot extends TelegramLongPollingBot {
    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    @Override
    public String getBotToken() {
        return "984690202:AAGl24nCP94h138XiUx_rXTZlbuK65Fa3kM";
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage sendMessage = new SendMessage().setChatId(update.getMessage().getChatId());
        sendMessage.setReplyMarkup(KeyboardSetup.replyKeyboardMarkup);
        if (!update.getMessage().hasLocation()) {
            try {
                sendMessage.setText(BotController.getMessage(update.getMessage().getText()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            if (update.getMessage().getText().equals("Current location")) {
                sendMessage.setText("You have chosen current location");
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            } else if (update.getMessage().getText().equals("Other location")) {
                sendMessage.setText("You have chosen other location");
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else {
            HttpGet request = new HttpGet("https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=" + update.getMessage().getLocation().getLatitude() + "&lon=" + update.getMessage().getLocation().getLongitude());

            // add request headers


            try (CloseableHttpResponse response = httpClient.execute(request)) {

                // Get HttpResponse Status
                System.out.println(response.getStatusLine().toString());

                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    // return it as a String
                    String result = EntityUtils.toString(entity);
                    int city=result.indexOf("city\":");
                    city=city+7;
                    result=result.substring(city);
                    String name_of_city = result.split(",")[0];

                  name_of_city=name_of_city.substring(0,name_of_city.length()-1);
                    System.out.println(name_of_city);
                    System.out.println(result);
                    OpenWeatherMapJsonParser openWeatherMapJsonParser=new OpenWeatherMapJsonParser();
                    sendMessage.setText(openWeatherMapJsonParser.getReadyForecast(name_of_city));
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    @Override
    public String getBotUsername() {
        return "weatherbot";
    }


}
