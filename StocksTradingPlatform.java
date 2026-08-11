import java.util.*;
import java.io.*;

// Class to represent a Stock
class Stock {
    String name;
    double price;
    double yesterdayPrice;
    int available;

    Stock(String name, double price, double yesterdayPrice, int available) {
        this.name = name;
        this.price = price;
        this.yesterdayPrice = yesterdayPrice;
        this.available = available;
    }

    void showMarketData() {
        System.out.println(name + " → Price: ₹" + price + ", Available: " + available);
        if (price > yesterdayPrice) {
            System.out.println("📈 Trend: Raised since yesterday");
        } else if (price < yesterdayPrice) {
            System.out.println("📉 Trend: Downfall since yesterday");
        } else {
            System.out.println("➖ Trend: No change");
        }
    }
}

// Class to represent a User Portfolio
class User {
    String name;
    Map<String, Integer> portfolio; // stock name → quantity owned
    double balance;

    User(String name, double balance) {
        this.name = name;
        this.balance = balance;
        this.portfolio = new HashMap<>();
    }

    void buyStock(Stock stock, int qty) {
        double cost = qty * stock.price;
        if (qty <= stock.available && balance >= cost) {
            stock.available -= qty;
            balance -= cost;
            portfolio.put(stock.name, portfolio.getOrDefault(stock.name, 0) + qty);
            System.out.println("✅ Bought " + qty + " of " + stock.name + " for ₹" + cost);

            double diff = stock.price - stock.yesterdayPrice;
            if (diff > 0) {
                System.out.println("📈 Profit potential: +" + (diff * qty));
            } else if (diff < 0) {
                System.out.println("📉 Loss potential: " + (diff * qty));
            } else {
                System.out.println("➖ No change compared to yesterday.");
            }

            if (stock.available == 0) {
                System.out.println("⚠️ " + stock.name + " is SOLD OUT!");
            }
        } else {
            System.out.println("❌ Transaction failed! Check balance or stock availability.");
        }
    }

    void sellStock(Stock stock, int qty) {
        int owned = portfolio.getOrDefault(stock.name, 0);
        if (qty <= owned) {
            double revenue = qty * stock.price;
            stock.available += qty;
            balance += revenue;
            portfolio.put(stock.name, owned - qty);
            System.out.println("✅ Sold " + qty + " of " + stock.name + " for ₹" + revenue);

            double diff = stock.price - stock.yesterdayPrice;
            if (diff > 0) {
                System.out.println("📈 Profit gained: +" + (diff * qty));
            } else if (diff < 0) {
                System.out.println("📉 Loss incurred: " + (diff * qty));
            } else {
                System.out.println("➖ No change compared to yesterday.");
            }
        } else {
            System.out.println("❌ You don’t own enough shares to sell!");
        }
    }

    void showPortfolio(Map<String, Stock> market) {
        System.out.println("\n📊 Portfolio of " + name);
        System.out.println("Balance: ₹" + balance);
        for (String stockName : portfolio.keySet()) {
            int qty = portfolio.get(stockName);
            double currentValue = qty * market.get(stockName).price;
            double profitLoss = qty * (market.get(stockName).price - market.get(stockName).yesterdayPrice);
            System.out.println(stockName + " → Owned: " + qty + ", Value: ₹" + currentValue +
                               ", P/L: " + profitLoss);
        }
    }

    // Save portfolio to file
    void savePortfolio(String filename) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println(name + "," + balance);
            for (String stockName : portfolio.keySet()) {
                pw.println(stockName + "," + portfolio.get(stockName));
            }
            System.out.println("💾 Portfolio saved to " + filename);
        } catch (IOException e) {
            System.out.println("❌ Error saving portfolio: " + e.getMessage());
        }
    }

    // Load portfolio from file
    void loadPortfolio(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine();
            if (line != null) {
                String[] parts = line.split(",");
                this.name = parts[0];
                this.balance = Double.parseDouble(parts[1]);
            }
            portfolio.clear();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                portfolio.put(parts[0], Integer.parseInt(parts[1]));
            }
            System.out.println("📂 Portfolio loaded from " + filename);
        } catch (IOException e) {
            System.out.println("❌ Error loading portfolio: " + e.getMessage());
        }
    }
}

public class StockTradingPlatform {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=====================================");
        System.out.println("💹✨ Welcome to CodeAlpha Stock Trading ✨💹");
        System.out.println("=====================================");

        Map<String, Stock> market = new HashMap<>();
        market.put("Reliance", new Stock("Reliance", 2500, 2450, 100));
        market.put("TCS", new Stock("TCS", 3500, 3550, 80));
        market.put("Infosys", new Stock("Infosys", 1500, 1480, 120));
        market.put("HDFC", new Stock("HDFC", 1600, 1600, 90));

        User user = new User("Sunanda", 50000);

        while (true) {
            System.out.println("\n===== Stock Trading Menu =====");
            System.out.println("1. View Market Data");
            System.out.println("2. Buy Stock");
            System.out.println("3. Sell Stock");
            System.out.println("4. View Portfolio Performance");
            System.out.println("5. Daily Summary Report");
            System.out.println("6. Save Portfolio");
            System.out.println("7. Load Portfolio");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("\n📊 Market Data:");
                    for (Stock s : market.values()) {
                        s.showMarketData();
                    }
                    break;
                case 2:
                    System.out.print("Enter stock name to buy: ");
                    String buyName = sc.nextLine();
                    System.out.print("Enter quantity: ");
                    int buyQty = sc.nextInt();
                    sc.nextLine();
                    if (market.containsKey(buyName)) {
                        user.buyStock(market.get(buyName), buyQty);
                    } else {
                        System.out.println("❌ Stock not found!");
                    }
                    break;
                case 3:
                    System.out.print("Enter stock name to sell: ");
                    String sellName = sc.nextLine();
                    System.out.print("Enter quantity: ");
                    int sellQty = sc.nextInt();
                    sc.nextLine();
                    if (market.containsKey(sellName)) {
                        user.sellStock(market.get(sellName), sellQty);
                    } else {
                        System.out.println("❌ Stock not found!");
                    }
                    break;
                case 4:
                    user.showPortfolio(market);
                    break;
                case 5:
                    System.out.println("\n===== 📑 Daily Stock Summary =====");
                    int totalAvailable = 0;
                    for (Stock s : market.values()) {
                        s.showMarketData();
                        totalAvailable += s.available;
                    }
                    System.out.println("📦 Total stocks available today: " + totalAvailable);
                    break;
                case 6:
                    user.savePortfolio("portfolio.txt");
                    break;
                case 7:
                    user.loadPortfolio("portfolio.txt");
                    break;
                case 8:
                    System.out.println("👋 Exiting CodeAlpha Stock Trading. Goodbye!");
                    return;
                default:
                    System.out.println("❌ Invalid choice. Try again.");
            }
        }
    }
}
