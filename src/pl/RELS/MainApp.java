package pl.RELS;
import pl.RELS.Offer.Offer;
import pl.RELS.User.Buyer;
import pl.RELS.User.Seller;
import pl.RELS.User.User;

import java.util.*;

/**
 * Created by Jakub Belter on 18/03/2020.
 */

public class MainApp {

    //Fields

    protected static Server server;

    public MainApp(){
        server = new Server();
    }

    //----------------------------------------METHODS---------------------------------------------

    //This weird structure was due to the fact that main is static and I wanted to go around it.
    public static void main(String[] args) {
        MainApp platform = new MainApp();
        platform.runMain();
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

    public void runTest(){

    }

    //----------------------------------------THREADS---------------------------------------------

    //Proper address format
    //Country;State;City;Street;BuildingNumber;ApartmentNumber
    class Validator implements Runnable{
        Thread myThread;
        Server s;
        Set<String> validCountries;
        Set<String> validCities;
        long validAddresses = 0;
        long invalidAddresses = 0;

        Validator(Server s){
            myThread = new Thread(this, "Address validator");
            this.s = s;
            validCountries = new HashSet<>(Arrays.asList("Poland", "Germany", "France"));
            validCities = new HashSet<>(Arrays.asList("Gdansk", "Warsaw", "Cracow", "Berlin", "Hamburg", "Monachium",
                    "Paris", "Marseilles", "Lyon"));
            System.out.println("Thread for address validation has been created" + myThread);
            myThread.start();
        }

        @Override
        public void run(){
            ArrayList<Offer> offArr = this.s.getAllOffers();
            for (Offer o : offArr){
                String[] oAddress = o.getAddress().split(";");
                if (oAddress.length == 6){
                    if (validCountries.contains(oAddress[0]) && validCities.contains(oAddress[1]) &&
                            this.isInt(oAddress[5])){
                        validAddresses += 1;
                    }
                    else
                        invalidAddresses += 1;
                }
                else{
                    invalidAddresses += 1;
                }
            }
        }

        //From stackoverflow (the most robust isInt)
        public boolean isInt(String str) {
            if (str == null) {
                return false;
            }
            int length = str.length();
            if (length == 0) {
                return false;
            }
            int i = 0;
            if (str.charAt(0) == '-') {
                if (length == 1) {
                    return false;
                }
                i = 1;
            }
            for (; i < length; i++) {
                char c = str.charAt(i);
                if (c <= '/' || c >= ':') {
                    return false;
                }
            }
            return true;
        }
    }

    //----------------------------------------GETTERS---------------------------------------------

    /**
     * A simple getter for Server in MainApp class
     * @return - Server field of MainApp
     */
    public Server getServer(){
        return server;
    }

    //----------------------------------------SETTERS---------------------------------------------

    /**
     * A simple Server setter for MainApp class
     * @param s - Initialized Server object
     */
    private void setServer(Server s){
        server = s;
    }
}
