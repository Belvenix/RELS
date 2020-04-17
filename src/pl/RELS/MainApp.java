package pl.RELS;
import org.jetbrains.annotations.NotNull;
import pl.RELS.FileChannelTest.FileHandler;
import pl.RELS.MultiThreadding.LinearRegression;
import pl.RELS.Offer.Offer;
import pl.RELS.Offer.OfferGenerator;
import pl.RELS.User.Buyer;
import pl.RELS.User.Seller;
import pl.RELS.User.User;

import java.io.*;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;
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
//Multithreading is implemented in the Statistic Counter class which is invoked in main->runStatistics(). Also
    //Critical section management is implemented there.
public class MainApp {

    //----------------------------------------FIELDS----------------------------------------
    /**
     * This field holds a reference to our server instance
     */
    protected static Server server;

    /**
     * This field holds our RANDOM class so that we have only one (saving memory)
     */
    private static final Random RANDOM = new Random();

    private static final Scanner SCANNER = new Scanner(System.in);

    /**
     * this variable if set to true will print ALL PROGRESS done in our runStatistics method. BEWARE - there will be
     * a lot of printed lines so use only for debugging processes only.
     */
    private static final boolean PRINT_PROGRESS = false;

    public MainApp(){
        server = new Server();
    }

    //----------------------------------------METHODS---------------------------------------------

    public static void main(String[] args) {
        MainApp platform = new MainApp();
        platform.multiThreadTest();
    }

    /**
     * Method responsible for running user interface
     */
    public void runMain(){
        while (true){
            System.out.println( "Welcome to the Real Estate Listing System!" +
                    "\nThis application let's you search for your perfect rent apartment or you can even find"+
                    "\nyour perfect house! Obviously you can also list your real estate for both sale and rent!"+
                    "\nHowever before we jump into it please register yourself in our glorious platform!\n");
            System.out.print("Please enter your name: ");
            String name = SCANNER.next();
            System.out.print("Please enter your surname: ");
            String surname = SCANNER.next();
            System.out.print("Please enter your desired username: ");
            String username = SCANNER.next();
            System.out.print("Please enter your desired password: ");
            String password = SCANNER.next();
            System.out.print("Please enter your credit card number: ");
            String bankId = SCANNER.next();

            String test;

            User user = null;
            do {
                System.out.print("Please specify whether you want to be a seller or buyer (enter 'seller' to become seller and"+
                        " 'buyer' to become a buyer): ");
                test = SCANNER.next();
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

    /**
     * Method responsible for showing example multithread processing of our offers from our server.
     *
     * First it will print:
     * "The goal of this program is to show the efficiency of teaching a simple Linear Regression
     * Model nad spreading it across the threads. On each of the threads we will have a dataset that consists
     * of (size of database) / (number of threads) and will try to predict the value of the house using this
     * formula: 'y_hat = slope * surface + intercept', where slope and intercept are calculated using the LR.
     * To enhance the model we will be using nThread part model that will average it's model over all predictions.
     * Also in order to represent the critical section access we will have a variable that will increment every
     * time it sees an outlier. In the end we will print the value of the outlier count."
     *
     * Which pretty much explains what the method does.
     */
    public void runStatistics(){
        System.out.println("The goal of this program is to show the efficiency of teaching a simple Linear Regression\n" +
                " Model nad spreading it across the threads. On each of the threads we will have a dataset that consists\n" +
                " of (size of database) / (number of threads) and will try to predict the value of the house using this\n" +
                " formula: 'y_hat = slope * surface + intercept', where slope and intercept are calculated using the LR.\n"+
                " To enhance the model we will be using nThread part model that will average it's model over all predictions.\n" +
                " Also in order to represent the critical section access we will have a variable that will increment every\n" +
                " time it sees an outlier. In the end we will print the value of the outlier count.");
        //We create needed variables.
        HashMap<String, ArrayList<String>> adrHashMap = setupAddressHashMap();
        OfferGenerator generator = new OfferGenerator(adrHashMap);

        //The number is just big for the purpose of the exercise
        int nOffers = 5000000;



        Seller s = new Seller(server);
        System.out.println("Creating a list of " + nOffers + " offers and uploading them to server.");
        long start = System.currentTimeMillis();

        //Here we generate our offers and upload them to the system (one user uploads them)
        for (int i = 0; i < nOffers; i++) {
            Offer o = generator.offerGenerator(s);
            s.uploadOffer(o);
        }
        //After it is uploaded we start the real work.

        long duration = System.currentTimeMillis() - start;
        System.out.println("Finished creating a list of offers in " + duration + "ms.");
        //First loop is responsible for handling how many threads we will use for the iteration
        for (int i = 1; i <= 10 ; i++) {

            //How many outliers are there in the dataset (price is too big compared to the surface)
            int outliersCount = 0;
            //Lists are responsible for holding the processing time so that after x iterations of the same code we would have
            //Average processing time (since there might be outliers ie skype push notification etc.
            ArrayList<Long> learnTime = new ArrayList<>();
            ArrayList<Long> errorTime = new ArrayList<>();

            //Second loop is responsible for handling the process j times so that we get an average oh how much time it takes
            //to process it
            for (int j = 0; j < 10; j++){
                long startLearn = System.currentTimeMillis();

                //A class responsible for all of the calculation
                StatisticCounter sc = new StatisticCounter(i);

                //here we teach our Linear Regression models with the data
                sc.learn();
                long durationLearn = System.currentTimeMillis() - startLearn;
                //here we add the time of execution of learning
                learnTime.add(durationLearn);


                long startMEA = System.currentTimeMillis();

                //Here we calculate the error of the method (which output is enabled only with PRINT_PROGRESS
                sc.MAE();
                long durationMAE = System.currentTimeMillis() - startMEA;
                errorTime.add(durationMAE);
                if(j == 0){
                    //we just get value from one model (it doesnt matter which one since it is the same for all of them)
                    outliersCount = sc.getOutlierCount();
                }
            }

            //Now we just sum the times and average the values so we can see the real average time of executiion of our method.
            Double averageLearn = learnTime.stream().mapToDouble(val -> val).average().orElse(0.0);
            System.out.println("Iteration " + i + ", statistic learning process finished in " + averageLearn + "ms.");
            Double averageMAE = errorTime.stream().mapToDouble(val -> val).average().orElse(0.0);
            System.out.println("Iteration " + i + ", statistic error calculation finished in " + averageMAE + "ms.");
            System.out.println("Iteration " + i + ", outlier count (sync) is equal to: " + outliersCount);
            System.out.print("\n---------------------------------------------------------------\n\n");

        }

    }

    /**
     * Main function that consists of command line interface for a user.
     * It enables user to input what he/she wants to do (help will show what is available) and then  start working with files
     * The object to be saved is Offer since we may want to store it in files instead of in memory. It will be stored in
     * text file, binary file and will be serialized (whole array). The rest of the function should be self-explanatory
     */
    public void runReadFiles(){
        //We create needed variables - for the data generation.
        Seller s = new Seller(server);
        ArrayList<Offer> arrayList = new ArrayList<>();
        String token="";

        System.out.println("Welcome to the file save and load testing program. We assume that you are already signed in.\n");


        while(!token.equals("quit")){
            System.out.println("Please specify action that you want to perform. If you do not know the commands please input 'help'");

            token = SCANNER.next().trim().toLowerCase();
            switch (token){
                case "help":
                    System.out.println("Possible commands: \n" +
                            "'help' - shows this help.\n" +
                            "'save' - saves your current array of offers to specified file extension.\n" +
                            "'load' - loads array of offers from specified file.\n" +
                            "'generate' - generates an array of offers with specified number of elements.\n" +
                            "'show' - shows your current array of offers.\n" +
                            "'mta' - runs test program for MultiThreadAccess to files" +
                            "'quit' - exits the program.\n");
                    break;
                case "save":
                    FileHandler.saveCLI(arrayList);
                    break;
                case "load":
                    arrayList = FileHandler.loadCLI();
                    break;
                case "generate":
                    arrayList = generateOffersCLI(s);
                    break;
                case "show":
                    printArray(arrayList);
                    break;
                case "mta":
                    multiThreadTest();
                    break;
                case "quit":
                    System.out.println("Quitting the program. Thank you for your time!");
                    break;
                default:
                    System.out.println("Invalid command has been entered [" + token + "]. Please refer to 'help' command.");
            }
        }

    }

    /**
     * Function that is used to test multiprocessing access. One has to run this function twice
     * and Thread.Sleep inside the functions will wait to present that the files are locked.
     * First we generate offers, print it, save it in 3 different ways and load it 3 different ways (every time we load
     * we show the list on the screen)
     */
    private void multiThreadTest(){
        ArrayList<Offer> arrTest = generateOffersCLI(new Seller(server));
        System.out.println("Printing generated array:");
        printArray(arrTest);
        FileHandler.savetxt(arrTest, "testing\\folder", "test.txt", true);
        FileHandler.savebin(arrTest, "testing\\folder", "test.dat", true);
        FileHandler.saveser(arrTest, "testing\\folder", "test.ser", true);
        arrTest = FileHandler.loadtxt("testing\\folder", "test.txt", true);
        printArray(arrTest);
        arrTest = FileHandler.loadbin("testing\\folder", "test.dat", true);
        printArray(arrTest);
        arrTest = FileHandler.loadser("testing\\folder", "test.ser", true);
        printArray(arrTest);
    }

    /**
     * Wrapper function for printing an array with some checking
     * @param arr ArrayList of Offers to be printed.
     */
    private void printArray(ArrayList<Offer> arr){
        if(arr != null){
            if (!arr.isEmpty()){
                for(Offer o : arr){
                    System.out.println(o.toString());
                }
            }
            else{
                System.out.println("The array is empty! Please generate or load the data to show them.");
            }
        }
        else {
            System.out.println("The array provided is NA.");
        }
    }

    /**
     * Simple Command Line Interface which asks user to input number of offers to be generated.
     * @param s - Seller object (someone that is logged in the account)
     * @return - arraylist of offers
     */
    private ArrayList<Offer> generateOffersCLI(Seller s){
        ArrayList<Offer> arr = new ArrayList<>();
        HashMap<String, ArrayList<String>> adrHashMap = setupAddressHashMap();
        OfferGenerator generator = new OfferGenerator(adrHashMap);
        System.out.println("In order to begin, please specify how many offers you want to be generated (so that we can save them and load them): ");
        int nOffers = SCANNER.nextInt();
        //Here we generate our offers and upload them to the system (our test user uploads them)

        for (int i = 0; i < nOffers; i++) {
            Offer o = generator.offerGenerator(s);
            if(o != null){
                arr.add(o);
            }
        }
        System.out.println("Array has been successfully generated.");
        return arr;
    }

    /**
     * This method returns a setup address HashMap which is needed for address generation in Offer generation.
     *
     * @return - a hashmap that contains mapped values of countries, states, cities, streets, building # and apartment #
     */
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

    /**
     * Class responsible for handling multithreaded process of linear regression analysis of relation surface to price in
     * offers. There is one model per thread This class implements multithreading as stated in exercise.
     *
     */
    class StatisticCounter{

        /**
         * An arraylist of model handlers which we will be teaching. worth noting is the fact that the size of the list
         * scales with nThreads variable which increases both number of threads and number of models
         */
        private ArrayList<ModelHandler> models;

        /**
         * custom varibalbe responsible for having the data abou the outliers (needed to show the synchronized access to
         * the variable)
         */
        private int outlierCount;

        /**
         * number of threads that we will be using
         */
        private int nThreads;

        /**
         * the value that we will consider to be an outlier if an error is bigger that this.
         */
        private double outlierRange;

        /**
         * Simple constructor of the class
         * @param nThreads - number of threads to be used
         */
        public StatisticCounter(int nThreads){
            this.nThreads = nThreads;
            this.models = new ArrayList<ModelHandler>();
            this.outlierCount = 0;
            this.outlierRange = this.getOutlier();
        }

        /**
         * Function creates and submits the model to Executor Service as well as to models list
         * @param j - the first index of the sublist in relation to original list (inclusive)
         * @param nextj the last index of the sublist in relation to original list (exclusive)
         * @param es - Executor Service instance that we submit the work to.
         */
        private void addModel(int j, int nextj, ExecutorService es){
            ArrayList<Offer> split = new ArrayList<Offer>(server.getAllOffers().subList(j, nextj));
            ModelHandler m = new ModelHandler(split);
            this.models.add(m);
            es.submit(m);
        }

        /**
         * Function is responsible for learning process of Linear Regression models. All of them happen to learn in
         * multiple threads, based on the number of them (nThreads)
         *
         * It creates n Threads which will learn from the sublist which are more or less evenly distributed among them.
         * Here is the exercise mostly implemented.
         */
        public void learn() {
            if (PRINT_PROGRESS)
                System.out.println("Starting the StatisticCounter instance with " + nThreads + " threads-models.");

            //A pool of threads. this will handle multiple threads automatically.
            ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
            int maxSize = server.getAllOffers().size();
            int batchSize = (maxSize / nThreads);

            //The loop ensures that the list is evenly divided as well as no values are excluded
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

            //Now we shutdown the threads - which are still running! hence we wait with the try catch clause
            executorService.shutdown();
            try {
                //Now we wait for the termination up to 60 seconds
                executorService.awaitTermination(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }

        }

        /**
         * Wrapper function with optional print (based on the value of PRINT_PROGRESS).
         */
        public void MAE(){
            double error = calculateMAE();
            if (PRINT_PROGRESS)
                System.out.println("MAE value is " + error + " with " + nThreads + " thread-models.");
        }

        /**
         * Synchronized incrementation of the outlier count. Adds one to the value
         */
        private synchronized void synchronizedIncrement(){
            this.outlierCount += 1;
        }

        /**
         * Simple getter of outlier count field
         * @return - int outlierCount
         */
        public int getOutlierCount(){
            return outlierCount;
        }

        /**
         * Method calculates the Mean Absolute Error of the predictions of the model compared to the real value
         * @return MAE double
         */
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

        /**
         * simple outlier range getter. DIFFERENT than getOutlierCount
         * @return
         */
        public double getOutlierRange(){
            return this.outlierRange;
        }

        /**
         * Calculates the value of outlier range - value which we consider to be a significant error and we have to report it
         * @return - outlier range double
         */
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

        /**
         * Method that predicts the price based on area using the mean output of all of the models.
         * @param area - 'x' variable that we use to predict the 'y' variable (price)
         * @return - predicted value of price
         */
        private double meanPredict(double area){
            double sum = 0;
            for (ModelHandler m : models){
                sum += m.predict(area);
            }
            sum /= nThreads;
            return sum;
        }

    }

    /**
     * Simple Class responsible for handling the Linear Regression model
     */
    class ModelHandler implements Runnable{
        LinearRegression model;
        ArrayList<Offer> data;

        public ModelHandler (ArrayList<Offer> offArr){
            this.data = offArr;
        }

        /**
         * Function need to be an overriden run method because it will run in the thread - therefore it implements the
         * Runnable interface which will call the function after Thread.start() will be called (which is called internally
         * by thread pool). Here are model is being trained.
         * There are optional prints provided for the user.
         */
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

        /**
         * Helper class that gets all of the surfaces from the data
         * @param offArr - an arraylist of Offer objects which contain the surfaces field which is then being returned
         * @return - an array(primitive) of doubles of surfaces need for the Linear model
         */
        private double [] getAreas(ArrayList<Offer> offArr){
            double [] ret = new double[offArr.size()];
            int i = 0;
            for (Offer o : offArr){
                ret[i++] = o.getPrice();
            }
            return ret;
        }

        /**
         * Helper class that gets all of the Price from the data
         * @param offArr - an arraylist of Offer objects which contain the Price field which is then being returned
         * @return - an array(primitive) of doubles of Price need for the Linear model
         */
        private double [] getPrices(ArrayList<Offer> offArr){
            double [] ret = new double[offArr.size()];
            int i = 0;
            for (Offer o : offArr){
                ret[i++] = o.getSurface();
            }
            return ret;
        }

        /**
         * Simple predict method wrapper
         * @param area - x variable that we use to predict the y (price)
         * @return - predicted price
         */
        public double predict(double area){
            return model.predict(area);
        }

        /**
         * not used wrapper for the show method
         */
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
