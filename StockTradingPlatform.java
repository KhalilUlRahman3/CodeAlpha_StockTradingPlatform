
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StockTradingPlatform {

    static class Stock {
        String symbol;
        String name;
        double price;

        Stock(String symbol, String name, double price) {
            this.symbol = symbol;
            this.name = name;
            this.price = price;
        }
    }

    static class Holding {
        int shares;
        double averagePrice;

        Holding(int shares, double averagePrice) {
            this.shares = shares;
            this.averagePrice = averagePrice;
        }
    }

    static class Transaction {
        String type;
        String symbol;
        int shares;
        double price;
        String date;

        Transaction(String type, String symbol, int shares, double price) {
            this.type = type;
            this.symbol = symbol;
            this.shares = shares;
            this.price = price;
            this.date = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
            );
        }
    }

    static Scanner input = new Scanner(System.in);
    static Random random = new Random();

    static double balance = 100000;

    static LinkedHashMap<String, Stock> market = new LinkedHashMap<>();
    static LinkedHashMap<String, Holding> portfolio = new LinkedHashMap<>();

    static ArrayList<Transaction> transactions = new ArrayList<>();
    static ArrayList<String> performanceHistory = new ArrayList<>();

    static void initializeMarket() {
        market.put("AAPL", new Stock("AAPL", "Apple", 25000));
        market.put("GOOG", new Stock("GOOG", "Google", 18000));
        market.put("MSFT", new Stock("MSFT", "Microsoft", 22000));
        market.put("AMZN", new Stock("AMZN", "Amazon", 20000));
        market.put("TSLA", new Stock("TSLA", "Tesla", 15000));

        recordPerformance("Initial Portfolio");
    }

    static void displayMarket() {
        System.out.println("\n========== STOCK MARKET ==========");
        System.out.printf("%-10s %-15s %s%n", "Symbol", "Company", "Price (Rs.)");

        for (Stock stock : market.values()) {
            System.out.printf("%-10s %-15s %.2f%n",
                    stock.symbol, stock.name, stock.price);
        }
    }

    static int getPositiveNumber(String message) {
        while (true) {
            System.out.print(message);

            try {
                int number = Integer.parseInt(input.nextLine());

                if (number > 0) {
                    return number;
                }

                System.out.println("Enter a number greater than zero.");

            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Enter a whole number.");
            }
        }
    }

    static void buyStock() {
        displayMarket();

        System.out.print("\nEnter stock symbol to buy: ");
        String symbol = input.nextLine().trim().toUpperCase();

        Stock stock = market.get(symbol);

        if (stock == null) {
            System.out.println("Stock not found!");
            return;
        }

        int quantity = getPositiveNumber("Enter number of shares: ");
        double cost = stock.price * quantity;

        if (cost > balance) {
            System.out.println("Insufficient balance!");
            return;
        }

        Holding holding = portfolio.get(symbol);

        if (holding == null) {
            portfolio.put(symbol, new Holding(quantity, stock.price));
        } else {
            double totalCost =
                    (holding.shares * holding.averagePrice) + cost;

            holding.shares += quantity;
            holding.averagePrice = totalCost / holding.shares;
        }

        balance -= cost;

        transactions.add(new Transaction("BUY", symbol, quantity, stock.price));

        recordPerformance("Bought " + symbol);

        System.out.println("Stock purchased successfully!");
        System.out.printf("Total cost: Rs. %.2f%n", cost);
    }

    static void sellStock() {
        if (portfolio.isEmpty()) {
            System.out.println("Your portfolio is empty!");
            return;
        }

        displayPortfolio();

        System.out.print("\nEnter stock symbol to sell: ");
        String symbol = input.nextLine().trim().toUpperCase();

        Holding holding = portfolio.get(symbol);
        Stock stock = market.get(symbol);

        if (holding == null) {
            System.out.println("You do not own this stock!");
            return;
        }

        int quantity = getPositiveNumber("Enter shares to sell: ");

        if (quantity > holding.shares) {
            System.out.println("You do not have enough shares!");
            return;
        }

        double amount = stock.price * quantity;
        balance += amount;
        holding.shares -= quantity;

        if (holding.shares == 0) {
            portfolio.remove(symbol);
        }

        transactions.add(new Transaction("SELL", symbol, quantity, stock.price));

        recordPerformance("Sold " + symbol);

        System.out.println("Stock sold successfully!");
        System.out.printf("Amount received: Rs. %.2f%n", amount);
    }

    static double calculatePortfolioValue() {
        double total = balance;

        for (String symbol : portfolio.keySet()) {
            Holding holding = portfolio.get(symbol);
            Stock stock = market.get(symbol);

            total += holding.shares * stock.price;
        }

        return total;
    }

    static void displayPortfolio() {
        System.out.println("\n========== YOUR PORTFOLIO ==========");

        if (portfolio.isEmpty()) {
            System.out.println("No stocks purchased yet.");
        } else {
            System.out.printf("%-10s %-10s %-15s %-15s%n",
                    "Symbol", "Shares", "Current Value", "Profit/Loss");

            for (String symbol : portfolio.keySet()) {
                Holding holding = portfolio.get(symbol);
                Stock stock = market.get(symbol);

                double value = holding.shares * stock.price;
                double profit = (stock.price - holding.averagePrice)
                        * holding.shares;

                System.out.printf("%-10s %-10d Rs. %-11.2f Rs. %.2f%n",
                        symbol, holding.shares, value, profit);
            }
        }

        System.out.printf("%nAvailable Balance: Rs. %.2f%n", balance);
        System.out.printf("Total Portfolio Value: Rs. %.2f%n",
                calculatePortfolioValue());
    }

    static void displayTransactions() {
        System.out.println("\n========== TRANSACTION HISTORY ==========");

        if (transactions.isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }

        for (Transaction t : transactions) {
            System.out.printf("%s | %s | %d shares | Rs. %.2f | %s%n",
                    t.type, t.symbol, t.shares, t.price, t.date);
        }
    }

    static void recordPerformance(String event) {
        performanceHistory.add(
                event + " | Portfolio Value: Rs. "
                        + String.format("%.2f", calculatePortfolioValue())
        );
    }

    static void simulateMarket() {
        System.out.println("\nUpdating stock market prices...");

        for (Stock stock : market.values()) {
            double change = -0.05 + (random.nextDouble() * 0.10);
            stock.price *= (1 + change);

            if (stock.price < 1) {
                stock.price = 1;
            }
        }

        recordPerformance("Market Updated");

        System.out.println("Market prices updated successfully!");
        displayMarket();
    }

    static void displayPerformance() {
        System.out.println("\n========== PORTFOLIO PERFORMANCE ==========");

        for (int i = 0; i < performanceHistory.size(); i++) {
            System.out.println((i + 1) + ". " + performanceHistory.get(i));
        }

        System.out.printf("%nCurrent Portfolio Value: Rs. %.2f%n",
                calculatePortfolioValue());
    }

    public static void main(String[] args) {

        initializeMarket();

        System.out.println("==================================");
        System.out.println("     STOCK TRADING PLATFORM");
        System.out.println("==================================");
        System.out.println("Starting Balance: Rs. 100000.00");

        while (true) {
            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. View Stock Market");
            System.out.println("2. Buy Stocks");
            System.out.println("3. Sell Stocks");
            System.out.println("4. View Portfolio");
            System.out.println("5. Transaction History");
            System.out.println("6. Simulate Market Movement");
            System.out.println("7. Portfolio Performance");
            System.out.println("8. Exit");

            int choice = getPositiveNumber("Enter your choice: ");

            switch (choice) {
                case 1:
                    displayMarket();
                    break;

                case 2:
                    buyStock();
                    break;

                case 3:
                    sellStock();
                    break;

                case 4:
                    displayPortfolio();
                    break;

                case 5:
                    displayTransactions();
                    break;

                case 6:
                    simulateMarket();
                    break;

                case 7:
                    displayPerformance();
                    break;

                case 8:
                    System.out.println("Thank you for using Stock Trading Platform!");
                    input.close();
                    return;

                default:
                    System.out.println("Invalid choice! Enter 1 to 8.");
            }
        }
    }
}
