package pl.RELS;
import org.jetbrains.annotations.NotNull;
import pl.RELS.MultiThreadding.LinearRegression;
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
    private static final boolean PRINT_PROGRESS = false;

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
        System.out.println("The goal of this program is to show the efficiency of teaching a simple Linear Regression\n" +
                " Model nad spreading it across the threads. On each of the threads we will have a dataset that consists\n" +
                " of (size of database) / (number of threads) and will try to predict the value of the house using this\n" +
                " formula: 'y_hat = slope * surface + intercept', where slope and intercept are calculated using the LR.\n"+
                " To enhance the model we will be using nThread part model that will average it's model over all predictions.\n" +
                " Also in order to represent the critical section access we will have a variable that will increment every\n" +
                " time it sees an outlier. In the end we will print the value of the outlier count.");
        HashMap<String, ArrayList<String>> adrHashMap = setupAddressHashMap();
        OfferGenerator generator = new OfferGenerator(adrHashMap);
        int nOffers = 5000000;
        ArrayList<Long> learnTime = new ArrayList<>();
        ArrayList<Long> errorTime = new ArrayList<>();
        Seller s = new Seller(server);
        System.out.println("Creating a list of " + nOffers + " offers and uploading them to server.");
        long start = System.currentTimeMillis();
        for (int i = 0; i < nOffers; i++) {
            Offer o = generator.offerGenerator(s);
            s.uploadOffer(o);
        }
        long duration = System.currentTimeMillis() - start;
        System.out.println("Finished creating a list of offers in " + duration + "ms.");
        for (int i = 1; i <= 5; i++) {
            int outliersCount = 0;
            for (int j = 0; j < 10; j++){
                long startLearn = System.currentTimeMillis();
                StatisticCounter sc = new StatisticCounter(i);
                sc.learn();
                long durationLearn = System.currentTimeMillis() - startLearn;
                learnTime.add(durationLearn);


                long startMEA = System.currentTimeMillis();
                sc.MAE();
                long durationMAE = System.currentTimeMillis() - startMEA;
                errorTime.add(durationMAE);
                if(j == 0){
                    outliersCount = sc.getOutlierCount();
                }
            }
            Double averageLearn = learnTime.stream().mapToDouble(val -> val).average().orElse(0.0);
            System.out.println("Iteration " + i + ", statistic learning process finished in " + averageLearn + "ms.");
            Double averageMAE = errorTime.stream().mapToDouble(val -> val).average().orElse(0.0);
            System.out.println("Iteration " + i + ", statistic error calculation finished in " + averageMAE + "ms.");
            System.out.println("Iteration " + i + ", outlier count (sync) is equal to: " + outliersCount);
            System.out.print("\n---------------------------------------------------------------\n\n");

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


    class StatisticCounter{
        private ArrayList<ModelHandler> models;
        private int outlierCount, outlierCountNoSync;
        private int nThreads;
        private double outlierRange;

        public StatisticCounter(int nThreads){
            this.nThreads = nThreads;
            this.models = new ArrayList<ModelHandler>();
            this.outlierCount = 0;
            this.outlierCountNoSync = 0;
            this.outlierRange = this.getOutlier();
        }

        private void addModel(int j, int nextj, ExecutorService es){
            ArrayList<Offer> split = new ArrayList<Offer>(server.getAllOffers().subList(j, nextj));
            ModelHandler m = new ModelHandler(split);
            this.models.add(m);
            es.submit(m);
        }

        public void learn() {
            if (PRINT_PROGRESS)
                System.out.println("Starting the StatisticCounter instance with " + nThreads + " threads-models.");
            ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
            int maxSize = server.getAllOffers().size();
            int batchSize = (maxSize / nThreads);
            for (int j = 0, nextj = batchSize; j <= maxSize; j += batchSize, nextj += batchSize){
                if (nextj > maxSize){
                    nextj = maxSize;
                }
                else if(nextj + batchSize > maxSize){
                    nextj = maxSize;
                    addModel(j, nextj, executorService);
                    break;
                }
                else{
                    addModel(j, nextj, executorService);
                }
            }
            executorService.shutdown();
            try {
                executorService.awaitTermination(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }

        }

        public void MAE(){
            double error = calculateMAE();
            if (PRINT_PROGRESS)
                System.out.println("MAE value is " + error + " with " + nThreads + " thread-models.");
        }

        private synchronized void synchronizedIncrement(){
            this.outlierCount += 1;
        }

        public int getOutlierCount(){
            return outlierCount;
        }

        private double calculateMAE(){
            double sum = 0;
            if (PRINT_PROGRESS)
                System.out.println("Started calculation of MAE");
            ArrayList<Offer> allOff = server.getAllOffers();
            for (int i = 0; i < allOff.size(); i++){
                Offer o = allOff.get(i);
                double error = Math.abs(meanPredict(o.getSurface()) - o.getPrice());
                if (error > this.getOutlierRange()){
                    this.synchronizedIncrement();
                }
                sum += error;
            }
            sum /= server.getAllOffers().size();
            if (PRINT_PROGRESS)
                System.out.println("MAE has been calculated.");
            return sum;
        }

        public double getOutlierRange(){
            return this.outlierRange;
        }

        private double getOutlier(){
            double sum = 0.0,  std = 0.0, xx = 0.0;
            int n = server.getAllOffers().size();
            for(Offer o : server.getAllOffers()){
                sum += o.getSurface();
            }
            double mean = sum / n;
            for(Offer o : server.getAllOffers()){
                xx += (o.getPrice() - mean) * (o.getPrice() - mean);
            }
            std = Math.sqrt(xx / (n-1));
            return 2 * std;
        }

        private double meanPredict(double area){
            double sum = 0;
            for (ModelHandler m : models){
                sum += m.predict(area);
            }
            sum /= nThreads;
            return sum;
        }

    }

    class ModelHandler implements Runnable{
        LinearRegression model;
        ArrayList<Offer> data;

        public ModelHandler (ArrayList<Offer> offArr){
            this.data = offArr;
        }

        @Override
        public void run() {
            if (PRINT_PROGRESS)
                System.out.println("Starting the calculation of linear regressor coefficients in " + Thread.currentThread().getName());
            long start = System.currentTimeMillis();
            this.model = new LinearRegression(getAreas(data), getPrices(data));
            long duration = System.currentTimeMillis() - start;
            if (PRINT_PROGRESS)
                System.out.println("Linear regressor " + Thread.currentThread().getName() + " has been calculated in " + duration + "ms");
        }

        private double [] getAreas(ArrayList<Offer> offArr){
            double [] ret = new double[offArr.size()];
            int i = 0;
            for (Offer o : offArr){
                ret[i++] = o.getPrice();
            }
            return ret;
        }

        private double [] getPrices(ArrayList<Offer> offArr){
            double [] ret = new double[offArr.size()];
            int i = 0;
            for (Offer o : offArr){
                ret[i++] = o.getSurface();
            }
            return ret;
        }

        public double predict(double area){
            return model.predict(area);
        }

        public void showModel(){
            System.out.println(model.toString());
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
