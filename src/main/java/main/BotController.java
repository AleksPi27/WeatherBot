package main;


import main.parcers.OpenWeatherMapJsonParser;

public class BotController {

    private static KeyboardSetup keyboardSetup = new KeyboardSetup();


    public static String getMessage(String msg) throws Exception {
        if (msg.equals("/start")) {

            return "Hi. Type \"go\"";

        }
        if(msg.equals("go")) {
            return keyboardSetup.buttons();
        }
        if (!msg.equals("/start")&&!msg.equals("go")&&msg!=null)
        {
            OpenWeatherMapJsonParser openWeatherMapJsonParser=new OpenWeatherMapJsonParser();
            return openWeatherMapJsonParser.getReadyForecast(msg);
        }
        return "";
    }

}
