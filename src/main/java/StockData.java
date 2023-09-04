import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

public class StockData {
    private final double open;
    private final double high;
    private final double low;
    private final double close;
    private final int volume;
    private final Timestamp timestamp;

    public StockData(double open, double high, double low, double close, int volume, Timestamp timestamp) {
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.timestamp = timestamp;
    }

    public static double calculateMovingAverage(List<StockData> data) {
        double sum = 0.0;
        for (StockData stock : data) {
            sum += stock.getClose();
        }
        return sum / data.size();
    }

    public static double calculateStandardDeviation(List<StockData> data, double mean) {
        double sum = 0.0;
        for (StockData stock : data) {
            sum += Math.pow(stock.getClose() - mean, 2);
        }
        return Math.sqrt(sum / data.size());
    }

    // Existing getters and setters

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public double getOpen() {
        return open;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getClose() {
        return close;
    }

    @Override
    public String toString() {
        return "StockData{" +
                "open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", volume=" + volume +
                ", timestamp='" + timestamp.toString() + '\'' +
                '}';
    }
}
