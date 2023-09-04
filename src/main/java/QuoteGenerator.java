import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class QuoteGenerator {
    private static final String API_KEY = "A5V08V16P0UGS8VY";
    private final OkHttpClient client;

    public QuoteGenerator() {
        this.client = new OkHttpClient();
    }

    public List<StockData> getTimeSeriesIntraday(String interval, String month, String output_size) throws IOException {
        String apiUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY" +
                "&entitlement=realtime" +
                "&symbol=AAPL" +
                "&interval=" + interval +
                "&month=" + month +
                "&outputsize=" + output_size +
                "&apikey=" + API_KEY +
                "&datatype=json";

        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        List<StockData> timeSeriesData = new ArrayList<>();

        try (Response response = client.newCall(request).execute()) {
            String jsonData = response.body().string();
            JSONObject jsonObject = new JSONObject(jsonData);

            JSONObject timeSeries = jsonObject.getJSONObject("Time Series (" + interval + ")");
            for (String timeStamp : timeSeries.keySet()) {
                JSONObject stockInfo = timeSeries.getJSONObject(timeStamp);

                // Convert the timeStamp string to a Timestamp object
                Timestamp timestampObject = Timestamp.valueOf(timeStamp);

                StockData stockData = new StockData(
                        stockInfo.getDouble("1. open"),
                        stockInfo.getDouble("2. high"),
                        stockInfo.getDouble("3. low"),
                        stockInfo.getDouble("4. close"),
                        stockInfo.getInt("5. volume"),
                        timestampObject  // Use the Timestamp object instead of the string
                );
                timeSeriesData.add(stockData);
            }
        }

        // Sort the list by timestamp
        timeSeriesData.sort(Comparator.comparing(StockData::getTimestamp));

        return timeSeriesData;
    }

    public List<StockData> getTimeSeriesIntradayForMonths(String interval, YearMonth startMonth, YearMonth endMonth, String output_size) throws IOException {
        // Create an ArrayList to store StockData objects across months
        List<StockData> accumulatedData = new ArrayList<>();

        // Generate the list of months
        List<YearMonth> months = generateMonthsInRange(startMonth, endMonth);

        // Fetch data for each month and add it to the accumulatedData list
        for (YearMonth month : months) {
            System.out.println("Fetching data for month: " + month);
            String monthStr = month.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            List<StockData> monthData = getTimeSeriesIntraday(interval, monthStr, output_size); // Assuming getTimeSeriesIntraday now returns List<StockData>
            accumulatedData.addAll(monthData);
        }

        // Sort the accumulatedData list by timestamp
        accumulatedData.sort(Comparator.comparing(StockData::getTimestamp));

        return accumulatedData;
    }

    private List<YearMonth> generateMonthsInRange(YearMonth start, YearMonth end) {
        List<YearMonth> months = new ArrayList<>();
        for (YearMonth month = start; !month.isAfter(end); month = month.plusMonths(1)) {
            months.add(month);
        }
        return months;
    }
}

