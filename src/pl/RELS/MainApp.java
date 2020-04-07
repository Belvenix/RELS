package pl.RELS;
import org.jetbrains.annotations.NotNull;
import pl.RELS.Offer.Offer;
import pl.RELS.Offer.OfferGenerator;
import pl.RELS.User.Buyer;
import pl.RELS.User.Seller;
import pl.RELS.User.User;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Math.round;

/** This class represents our Real Estate Listing System application
 * It contains a Server class to handle the storage of the offers as well as a main function that works like an app for
 * end user. We may login put some offers and buy some offers
 * @author jbelter
 */
public class MainApp {

    //Fields

    protected static Server server;
    private static final Random RANDOM = new Random();

    public MainApp(){
        server = new Server();
    }

    //----------------------------------------METHODS---------------------------------------------

    public static void main(String[] args) {
        MainApp platform = new MainApp();
        platform.runStatistics();

    }

    public void runMain(){
        Scanner scan = new Scanner(System.in); // Initialize a new scanner object
        while (true){
            System.out.println( "Welcome to the Real Estate Listing System!" +
                    "\nThis application let's you search for your perfect rent apartment or you can even find"+
                    "\nyour perfect house! Obviously you can also list your real estate for both sale and rent!"+
                    "\nHowever before we jump into it please register yourself in our glorious platform!\n");
            System.out.print("Please enter your name: ");
            String name = scan.next();
            System.out.print("Please enter your surname: ");
            String surname = scan.next();
            System.out.print("Please enter your desired username: ");
            String username = scan.next();
            System.out.print("Please enter your desired password: ");
            String password = scan.next();
            System.out.print("Please enter your credit card number: ");
            String bankId = scan.next();

            String test;

            //Polymorphism /4/
            User user = null;
            do {
                System.out.print("Please specify whether you want to be a seller or buyer (enter 'seller' to become seller and"+
                        " 'buyer' to become a buyer): ");
                test = scan.next();
                if (test.equals("seller")){
                    user = new Seller(name, surname, username, password, bankId, getServer());
                }
                else if (test.equals("buyer")){
                    user = new Buyer(name, surname, username, password, bankId, getServer());
                }
                else {
                    System.out.println("Please specify correct type of user!");
                }
            } while (!test.equals("seller") && !test.equals("buyer"));

            System.out.println("Now please login into your newly created account!\n");
            assert user != null;
            int result = user.actionLoop();
            if (result == 1 || result == 2)
                break;
            else if (result == 0){
                System.out.println("You have logged off successfully");
                continue;
            }
            else
                continue;

        }
    }

    public void runStatistics(){
        HashMap<String, ArrayList<String>> adrHashMap = setupAddressHashMap();
        OfferGenerator generator = new OfferGenerator(adrHashMap);
        int nOffers = 6000000;
        double threshold = 5000;
        Seller s = new Seller(server);
        for (int i = 0; i < nOffers; i++) {
            Offer o = generator.offerGenerator(s);
            s.uploadOffer(o);
        }

        for (int i = 1; i <= 10; i++) {
            ExecutorService executorService = Executors.newFixedThreadPool(i);
            ArrayList<Offer> goodOffers = new ArrayList<>();
            long start = System.currentTimeMillis();
            int batchSize = (server.getAllOffers().size() / i);
            for (int j = 0, nextj = batchSize; j <= server.getAllOffers().size() && nextj <= server.getAllOffers().size();
                 j += batchSize, nextj += batchSize){
                ArrayList<Offer> split = new ArrayList<Offer>(server.getAllOffers().subList(j, nextj));
                executorService.submit(new Thread(new StatisticCounter(split, goodOffers, 5000)));
            }
            executorService.shutdown();
            try {
                executorService.awaitTermination(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
            long duration = System.currentTimeMillis() - start;
            System.out.println("Final mean price is equal to: " + synSum.getSumPrice() / nOffers +
                                "\nFinal mean surface is equal to:" + synSum.getSumArea() / nOffers +
                                "\nFinal percent of good offers: " + ((double)synSum.getGoodOffers().size() / (double)nOffers)*100
                    + "%\nTotal process time: " + duration + "ms\n" + "Best offer price offer: " + synSum.goodOffers.get(0).toString());


        }

    }

    public HashMap<String, ArrayList<String>> setupAddressHashMap(){
        ArrayList<String> possibleCountries = new ArrayList<>(Arrays.asList("Poland", "Germany", "France"));
        ArrayList<String> possibleStates = new ArrayList<>(Arrays.asList("pomeranian", "masovian", "lesser poland", "#!%", "berlin",
                "hamburg", "bavaria", "ile-de-france", "alpes-cote-d'Azur",
                "auvergne-rhone-alpes"));
        ArrayList<String> possibleCities = new ArrayList<>(Arrays.asList("Gdansk", "Warsaw", "Cracow", "Berlin", "Hamburg", "Monachium",
                "Paris", "Marseille", "Lyon", "New York"));
        ArrayList<String> possibleStreets = new ArrayList<>(Arrays.asList("Lwowa", "Pooh", "kurzgesagt", "zolipapa", "ambasadors",
                "Cute", "Unlucky"));
        ArrayList<String> possibleBuildingNumbers = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6b", "7a", "94",
                "14c", "30c", "18", "19", "11", "45b", "29a", "29b", "30", "80",
                "9", "11", "13b", "12a"));
        ArrayList<String> possibleApartmentNumbers = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7a", "94",
                "14c", "30c", "18", "19", "11", "45b", "29a", "29b", "30", "80", "9", "11", "13b", "12a", "13", "14", "15",
                "16", "17", "20"));
        HashMap<String, ArrayList<String>> hshMap = new HashMap<String,ArrayList<String>>();
        hshMap.put("countries", possibleCountries);
        hshMap.put("states", possibleStates);
        hshMap.put("cities", possibleCities);
        hshMap.put("streets", possibleStreets);
        hshMap.put("buildingNumbers", possibleBuildingNumbers);
        hshMap.put("apartmentNumbers", possibleApartmentNumbers);
        return hshMap;
    }

    //----------------------------------------THREADS---------------------------------------------


    class StatisticCounter implements Runnable{
        private volatile double sumPrice = 0, sumArea = 0;
        private volatile ArrayList<Offer> goodOffers;
        private final ArrayList<Offer> list;
        private double threshold;

        StatisticCounter(ArrayList<Offer> list, ArrayList<Offer> goodOffers, double threshold){
            this.goodOffers = goodOffers;
            this.list = list;
            this.threshold = threshold;
        }

        @Override
        public void run() {
            for (Offer o : this.list) {
                sumPrice += o.getPrice();
                sumArea += o.getSurface();
                double ratio = o.getPrice() / o.getSurface();
                if (ratio < threshold) {
                    goodOffers.add(o);
                }
            }
        }
        public double getSumPrice() {
            return sumPrice;
        }

        public double getSumArea() {
            return sumArea;
        }

        public ArrayList<Offer> getGoodOffers(){
            return goodOffers;
        }
    }

    //----------------------------------------GETTERS---------------------------------------------

    /** A simple getter for Server in MainApp class
     * @return - Server field of MainApp
     */
    public Server getServer(){
        return server;
    }

    //----------------------------------------SETTERS---------------------------------------------

    /** A simple Server setter for MainApp class
     * @param s - Initialized Server object
     */
    private void setServer(Server s){
        server = s;
    }
}
