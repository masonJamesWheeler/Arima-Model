import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        QuoteGenerator generator = new QuoteGenerator();
        try {
            // Define the start and end months for data retrieval
            YearMonth startMonth = YearMonth.of(2021, 1);
            YearMonth endMonth = YearMonth.of(2021, 6);
            Timestamp startDate = Timestamp.valueOf(startMonth.atDay(1).atStartOfDay());
            Timestamp endDate = Timestamp.valueOf(endMonth.atEndOfMonth().atTime(23, 59, 59));


            // Get the stock data for the defined range of months
            List<StockData> totalDataFrame = generator.getTimeSeriesIntradayForMonths("1min", startMonth, endMonth, "full");

            // Create and apply the mean reversion strategy
            MeanReversionStrategy strategy = new MeanReversionStrategy(1.0, 0.0, 20, totalDataFrame);
            List<String> signals = strategy.generateSignals();

            try {
                // Apply the strategy
                strategy.applyStrategy();

                // Print the cumulative return
                System.out.println("Cumulative Return: " + strategy.getCumulativeReturn());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}







