package pl.RELS;
import pl.RELS.User.Buyer;
import pl.RELS.User.Seller;
import pl.RELS.User.User;

import java.util.Scanner;

/**
 * Created by Jakub Belter on 18/03/2020.
 */

public class MainApp {

    protected static Server server;

    public MainApp(){
        server = new Server();
    }

    //This weird structure was due to the fact that main is static and I wanted to go around it.
    public static void main(String[] args) {
        MainApp platform = new MainApp();
        platform.run();
    }

    public void run(){
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

    //This method simply gets server
    public Server getServer(){
        return server;
    }

    private void setServer(Server s){
        server = s;
    }
}
