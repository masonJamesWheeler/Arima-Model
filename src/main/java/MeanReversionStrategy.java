import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;

public class MeanReversionStrategy {
    private double entryThreshold;
    private double exitThreshold;
    private int lookBackPeriod;
    private List<StockData> stockData;

    // Add a new member for holding the number of shares
    private int numberOfShares = 0;

    // Cumulative return
    private double cumulativeReturn = 0.0;

    public MeanReversionStrategy(double entryThreshold, double exitThreshold, int lookBackPeriod, List<StockData> stockData) {
        this.entryThreshold = entryThreshold;
        this.exitThreshold = exitThreshold;
        this.lookBackPeriod = lookBackPeriod;
        this.stockData = stockData;
    }

    public List<String> generateSignals() {
        List<String> signals = new ArrayList<>();

        for (int i = lookBackPeriod; i < stockData.size(); i++) {
            List<StockData> lastNData = stockData.subList(i - lookBackPeriod, i);
            double mean = StockData.calculateMovingAverage(lastNData);
            double stdDev = StockData.calculateStandardDeviation(lastNData, mean);

            double zScore = (stockData.get(i).getClose() - mean) / stdDev;

            if (zScore < -entryThreshold) {
                signals.add("BUY");
            } else if (zScore >= -exitThreshold) {
                signals.add("SELL");
            } else {
                signals.add("HOLD");
            }
        }
        return signals;
    }

    public void applyStrategy() {
        // Initialize cash on hand
        double cash = 10000.0;  // Start with $10,000

        for (String signal : generateSignals()) {
            double currentPrice = stockData.get(lookBackPeriod).getClose();

            if ("BUY".equals(signal)) {
                // Buy one share
                if (cash >= currentPrice) {
                    numberOfShares++;
                    cash -= currentPrice;
                }
            } else if ("SELL".equals(signal)) {
                // Sell one share
                if (numberOfShares > 0) {
                    numberOfShares--;
                    cash += currentPrice;
                }
            }
            // "HOLD" doesn't do anything

            // Update cumulative returns
            cumulativeReturn = (cash + numberOfShares * currentPrice) / 10000.0 - 1.0;
        }
    }

    public double getCumulativeReturn() {
        return cumulativeReturn;
    }
}
