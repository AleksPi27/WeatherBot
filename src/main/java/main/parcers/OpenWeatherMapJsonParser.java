package main.parcers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.utils.WeatherUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OpenWeatherMapJsonParser implements WeatherParser {

    private final static String API_CALL_TEMPLATE = "https://api.openweathermap.org/data/2.5/forecast?q=";
    private final static String API_KEY_TEMPLATE = "&units=metric&APPID=a6e76459bdbfb1d0c6beed7de3891da6";
    private final static String USER_AGENT = "Mozilla/5.0";
    private final static DateTimeFormatter INPUT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final static DateTimeFormatter OUTPUT_DATE_TIME_FORMAT_FOR_DAY_AND_TIME = DateTimeFormatter.ofPattern("dd HH:mm", Locale.US);
    private final static DateTimeFormatter OUTPUT_DATE_TIME_FORMAT_FOR_MONTH = DateTimeFormatter.ofPattern("MMMM", Locale.US);
    private final static Integer COLUMNS = 4;
    private static String month;

    private static String downloadJsonRawData(String city) throws Exception {
        String urlString = API_CALL_TEMPLATE + city + API_KEY_TEMPLATE;
        URL urlObject = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = connection.getResponseCode();
        if (responseCode == 404) {
            throw new IllegalArgumentException();
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    private static List<String> convertRawDataToList(String data) throws Exception {
        List<String> weatherList = new ArrayList<>();
        JsonNode arrNode = new ObjectMapper().readTree(data).get("list");
        if (arrNode.isArray()) {
            for (final JsonNode objNode : arrNode) {
                String forecastTime = objNode.get("dt_txt").toString();
                if (forecastTime.contains("00:00") || forecastTime.contains("03:00") || forecastTime.contains("06:00") || forecastTime.contains("09:00") || forecastTime.contains("12:00") || forecastTime.contains("15:00") || forecastTime.contains("18:00") || forecastTime.contains("21:00")) {
                    weatherList.add(objNode.toString());
                }
            }
        }
        return weatherList;
    }

    private static String parseForecastDataFromList(List<String> weatherList) throws Exception {
        final StringBuffer sb = new StringBuffer();
        ObjectMapper objectMapper = new ObjectMapper();

        for (String line : weatherList) {
            {
                String dateTime;
                JsonNode mainNode;
                JsonNode weatherArrNode;
                try {
                    mainNode = objectMapper.readTree(line).get("main");
                    weatherArrNode = objectMapper.readTree(line).get("weather");
                    for (final JsonNode objNode : weatherArrNode) {
                        dateTime = objectMapper.readTree(line).get("dt_txt").toString();
                        sb.append(formatForecastData(dateTime, objNode.get("main").toString(), mainNode.get("temp").asDouble()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    private static String formatForecastData(String dateTime, String description, double temperature) throws Exception {
        System.out.println(dateTime);
        LocalDateTime forecastDateTime = LocalDateTime.parse(dateTime.replaceAll("\"", ""), INPUT_DATE_TIME_FORMAT);
        String formattedDateTime = forecastDateTime.format(OUTPUT_DATE_TIME_FORMAT_FOR_DAY_AND_TIME);
        String current_month = forecastDateTime.format(OUTPUT_DATE_TIME_FORMAT_FOR_MONTH);
        System.out.println("Month : " + current_month);
        System.out.println("DAY : " + formattedDateTime);

        String formattedTemperature;
        long roundedTemperature = Math.round(temperature);
        if (roundedTemperature > 0) {
            formattedTemperature = "+" + String.valueOf(Math.round(temperature));
        } else {
            formattedTemperature = String.valueOf(Math.round(temperature));
        }


        String formattedDescription = description.replaceAll("\"", "");
        if (formattedTemperature.length() == 2) {
            formattedTemperature = " " + formattedTemperature + "  ";
            // formattedDescription=formattedDescription;
        } else formattedTemperature = " " + formattedTemperature + " ";
        System.out.println("Temperature ^ " + formattedTemperature + " SIZE: " + formattedTemperature.length());
        String weatherIconCode = WeatherUtils.weatherIconsCodes.get(formattedDescription);
        String format = "%-10s";
        if (!current_month.equals(month) || month.isEmpty()) {

            String result = current_month + "\n" + String.format(format, formattedDateTime);
//            , formattedTemperature,  formattedDescription, weatherIconCode, System.lineSeparator()
            if (formattedTemperature.length() == 2) {
                result = result + formattedTemperature;
                if(formattedDescription.length()==4)
                {
                    result=result+"    "+formattedDescription+"    "+weatherIconCode+"\n";
                }
                if(formattedDescription.length()==5)
                {
                    result=result+"    "+formattedDescription+"   "+weatherIconCode+"\n";
                }
                if(formattedDescription.length()==6)
                {
                    result=result+"    "+formattedDescription+"  "+weatherIconCode+"\n";
                }
            } else {
                result = result + formattedTemperature;
                if(formattedDescription.length()==4)
                {
                    result=result+"   "+formattedDescription+"    "+weatherIconCode+"\n";
                }
                if(formattedDescription.length()==5)
                {
                    result=result+"   "+formattedDescription+"   "+weatherIconCode+"\n";
                }
                if(formattedDescription.length()==6)
                            {
                                    result=result+"   "+formattedDescription+"  "+weatherIconCode+"\n";
                }
            }

            month = current_month;
            return result;
        } else {
            month = current_month;
            String result = String.format(format, formattedDateTime);
            if (formattedTemperature.length() == 2) {
                result = result + formattedTemperature;
                if(formattedDescription.length()==4)
                {
                    result=result+"    "+formattedDescription+"    "+weatherIconCode+System.lineSeparator();
                }
                if(formattedDescription.length()==5)
                {
                    result=result+"    "+formattedDescription+"   "+weatherIconCode+System.lineSeparator();
                }
                if(formattedDescription.length()==6)
                {
                    result=result+"    "+formattedDescription+"  "+weatherIconCode+System.lineSeparator();
                }
            } else {
                result = result + formattedTemperature;
                if(formattedDescription.length()==4)
                {
                    result=result+"   "+formattedDescription+"    "+weatherIconCode+System.lineSeparator();
                }
                if(formattedDescription.length()==5)
                {
                    result=result+"   "+formattedDescription+"   "+weatherIconCode+System.lineSeparator();
                }
                if(formattedDescription.length()==6)
                {
                    result=result+"   "+formattedDescription+"  "+weatherIconCode+System.lineSeparator();
                }
            }
            return result;
        }


    }

    @Override
    public String getReadyForecast(String city) {
        String result;
        try {
            String jsonRawData = downloadJsonRawData(city);
            List<String> linesOfForecast = convertRawDataToList(jsonRawData);
            result = String.format("%s:%s%s", city, System.lineSeparator(), parseForecastDataFromList(linesOfForecast));

        } catch (Exception e) {
            e.printStackTrace();
            return "Please enter a city in which you want to know a forecast";
        }
        return result;
    }

}
