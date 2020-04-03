package pl.RELS.User;

import pl.RELS.Offer.Offer;
import pl.RELS.Server;

import java.sql.Timestamp;
import java.util.Scanner;

/**
 * This class is responsible for handling Buyer type user properly. It contains additional methods which are not inherited
 * from User like buy offer. Also the show offer method is different from the seller method.
 */
public class Buyer extends User {

    public Buyer(String name, String surname, String user, String pass, String bank, Server s) {
        super(name, surname, user, pass, bank, s);
    }

    //Overload the constructor /3/
    public Buyer(Server s){
        super("Patryk", "Kowalski", "user2", "321", "987654321", s);
    }

    //--------------------------------------------------------------------------------------------
    //----------------------------------------METHODS---------------------------------------------
    //--------------------------------------------------------------------------------------------

    //This main class is here to test some things, whether it works properly
    public static void main(String[] args){
        Server s = new Server();
        //Here we create an instance of buyer to test this class
        Buyer buyer = new Buyer("Jakub", "Belter", "Username", "123", "123456789", s);
        System.out.println("Showing all of the offers:");
        buyer.showOffers();
        System.out.println("Buying some offer: ");
        buyer.buyOffer();

        //Here we create an instance of seller to test this class
        Seller seller = new Seller("Patryk", "Dunajewski", "Username2", "123", "987654321", s);
        System.out.println("Showing your offers:");
        seller.showOffers();
        System.out.println("Listing some Real Estate");

        //Second method to add the offer into both server and seller itself
        seller.uploadOffer(new Offer(   new Timestamp(System.currentTimeMillis()),
                                        new Timestamp(System.currentTimeMillis()),
                                        "Polska;pomorskie;gdansk;wajdeloty;20;9",
                                        123456.75,
                                        Offer.OfferType.SALE,
                                        seller.getServer().getCurrentOfferId(),
                                        seller.getUserId(),
                                        Offer.FloorType.GROUND,
                                        true,
                                        720.50,
                                        5,
                                        "Super apartament kupuj smiało!"));
        System.out.println("Showing your offers after adding listing:");
        System.out.println("Seller:");
        seller.showOffers();
        System.out.println("Buyer:");
        buyer.showOffers();
    }

    //more explanation in base class
    @Override
    public int login() {
        System.out.println("Welcome to the login sequence of Buyer. You will be asked for your credentials.");
        Scanner scan = new Scanner(System.in);
        System.out.print("Please enter your username: ");
        String token1 = scan.next();
        System.out.print("\nPlease enter your password: ");
        String token2 = scan.next();
        if (this.authenticate(token1, token2))
            return 1;
        else
            return 0;
    }

    //more explanation in base class
    @Override
    protected boolean authenticate(String username, String password) {
        if (this.getUsername().equals(username) && this.getPassword().equals(password))
            return true;
        else
            return false;
    }


    //More explanation in abstract class
    @Override
    public int actionLoop() {
        int t = this.login();
        if(t==-1){
            return 2; //Note the explanation is in user class
        }
        else if (t==0){
            System.out.println("Invalid credentials!");
            return -1; //Note the explanation is in user class
        }
        else if (t==1){
            System.out.println("Successfully logged in!");
            String token = ""; //A token that will be just useful variable
            while (true){
                System.out.println( "Specify want you want to do (enter 'show' to show every offer, enter 'buy'"+
                        " to buy a real estate, 'logout' to logout of your account and 'quit' to quit "+
                        "program entirely): ");
                Scanner scan = new Scanner(System.in);
                token = scan.next();
                switch (token) { //Switch because it was suggested
                    case "show":
                        this.showOffers(); //We enter the showOffers method which handles showing all offers available
                        break;

                    case "buy":
                        this.buyOffer(); //We enter into buyOffer method which handles buying a real estate
                        break;

                    case "logout":
                        return 0; //Note the explanation is in user class

                    case "quit":
                        return 1; //Note the explanation is in user class

                    default:
                        //Obviously if we enter invalid command we get notified
                        System.out.println("Invalid command has been entered. Please check your spelling. (" + token + ")");
                        break;
                }
            }
        }
        //if everything breaks then we just return 2 for error
        return 2;
    }

    //Worth noting fact is that seller showOffers shows only offers created by you which is different to buyer showOffer
    //method which presents all available offers
    @Override
    public void showOffers() {
        if (Buyer.server.getAllOffers() != null && !Buyer.server.getAllOffers().isEmpty()){
            System.out.println("Here are the offers: ");
            for (Offer o : Buyer.server.getAllOffers()) {
                o.printMe();
            }
        }
        else {
            System.out.println("There are no offers!");
        }
    }

    //As the name suggests it buys a real estate (very simplified)
    private void buyOffer(){
        System.out.println("You bought an offer!");
    }

    //--------------------------------------------------------------------------------------------
    //----------------------------------------SETTERS---------------------------------------------
    //--------------------------------------------------------------------------------------------

    @Override
    protected void setUsername(String username) {
        this.username = username;
    }

    @Override
    protected void setPassword(String password) {
        this.password = password;
    }

    @Override
    protected void setBankId(String bankId) {
        this.bankId = bankId;
    }

    //This method is NOT SAFE
    @Override
    protected void setServer(Server server){
        User.server = server;
    }

    //This method is NOT SAFE
    @Override
    protected void setUserId(long userId){
        this.userId = userId;
    }

    //--------------------------------------------------------------------------------------------
    //----------------------------------------GETTERS---------------------------------------------
    //--------------------------------------------------------------------------------------------

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    protected String getPassword() {
        return password;
    }

    @Override
    public String getBankId() {
        return bankId;
    }

    @Override
    public Server getServer(){
        return server;
    }

    @Override
    public long getUserId() {
        return this.userId;
    }

}
