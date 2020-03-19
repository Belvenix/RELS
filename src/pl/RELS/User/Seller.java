package pl.RELS.User;

import pl.RELS.Offer.Offer;
import pl.RELS.Server;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;

public class Seller extends User {

    //This variable holds all of offers that are yours o the market
    private ArrayList<Offer> myOffers;

    public Seller(String name, String surname, String user, String pass, String bank, Server s) {
        super(name, surname, user, pass, bank, s);
        this.myOffers = new ArrayList<Offer>();
    }

    //Overload the constructor /3/
    public Seller(Server s){
        super("Jakub", "Belter", "user", "123", "123456789", s);
    }

    @Override
    public int login() {
        System.out.println("Welcome to the login sequence of Seller. You will be asked for your credentials.");
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

    @Override
    protected boolean authenticate(String username, String password) {
        return this.getUsername().equals(username) && this.getPassword().equals(password);
    }

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

    public ArrayList<Offer> getMyOffers(){
        return this.myOffers;
    }

    @Override
    public Server getServer(){
        return server;
    }

    @Override
    public long getUserId() {
        return this.userId;
    }

    //Main loop of the seller class
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
                System.out.println( "Specify want you want to do (enter 'show' to show your offers, enter 'sell'"+
                        " to list your real estate, 'logout' to logout of your account and 'quit' to quit "+
                        "program entirely): ");
                Scanner scan = new Scanner(System.in);
                token = scan.next();
                switch (token) { //Switch because it was suggested
                    case "show":
                        this.showOffers(); //We enter the showOffers method which handles showing all offers available
                        break;

                    case "sell":
                        this.putOffer(); //We enter into buyOffer method which handles buying a real estate
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
        //First check whether there are any offers if yes loop through them and print them, otherwise print string
        if ((this.getMyOffers() != null) && (!this.getMyOffers().isEmpty())){
            for (Offer o : this.getMyOffers()){
                o.printMe();
            }
        }
        else {
            System.out.println("You don't have any offers!");
        }
    }

    //As the name suggests it puts an offer to public
    //This handles the code for listing new offer
    public void putOffer(){
        Scanner scan = new Scanner(System.in);

        //Sample code to initialize the sql timestamp from current time
        Timestamp tmsp = new Timestamp(System.currentTimeMillis());
        System.out.print("Please specify address of the real estate (Country;State;City;Street;BuildingNumber;ApartmentNumber): ");
        String adr = scan.next();
        System.out.print("Please specify full price for the real estate offer: ");
        double price = scan.nextDouble();

        //Here maybe not so efficient way of gettign teh proper enum type variable
        System.out.print("Please specify type of offer (SALE or RENT or LONG_TERM_RENT or SHORT_TERM_RENT): ");
        String type = scan.next();
        Offer.OfferType ot;
        switch (type) {
            case "SALE":
                ot = Offer.OfferType.SALE;
                break;
            case "RENT":
                ot = Offer.OfferType.RENT;
                break;
            case "LONG_TERM_RENT":
                ot = Offer.OfferType.LONG_TERM_RENT;
                break;
            case "SHORT_TERM_RENT":
                ot = Offer.OfferType.SHORT_TERM_RENT;
                break;
            default:
                ot = Offer.OfferType.NONE;
                break;
        }
        long offerId = this.getServer().getCurrentOfferId();
        long userId = this.getServer().currentUserId();

        //Here maybe not so efficient way of getting teh proper enum type variable
        System.out.print("Please specify floor level of real estate (enter number):" +
                "GROUND(0)," +" FIRST(1)," + " SECOND(2)," + " THIRD(3)," + " FORTH(4)," +
                " FIFTH(5)," + " SIXTH(6)," + " SEVENTH(7)," + " EIGHTH(8)," + " NINTH(9)," + " TENTH(10):");
        String level = scan.next();
        Offer.FloorType ft;
        switch (level) {
            case "0":
                ft = Offer.FloorType.GROUND;
                break;
            case "1":
                ft = Offer.FloorType.FIRST;
                break;
            case "2":
                ft = Offer.FloorType.SECOND;
                break;
            case "3":
                ft = Offer.FloorType.THIRD;
                break;
            case "4":
                ft = Offer.FloorType.FORTH;
                break;
            case "5":
                ft = Offer.FloorType.FIFTH;
                break;
            case "6":
                ft = Offer.FloorType.SIXTH;
                break;
            case "7":
                ft = Offer.FloorType.SEVENTH;
                break;
            case "8":
                ft = Offer.FloorType.EIGHTH;
                break;
            case "9":
                ft = Offer.FloorType.NINTH;
                break;
            case "10":
                ft = Offer.FloorType.TENTH;
                break;
            default:
                ft = Offer.FloorType.NONE;
                break;
        }

        System.out.print("Please specify whether the real estate is furnished (True or False): ");
        boolean x = scan.nextBoolean();
        System.out.print("Please specify the surface: ");
        double surface = scan.nextDouble();
        System.out.print("Please specify the number of rooms: ");
        double rooms = scan.nextDouble();

        //Unfortunately only one-line is supported as for now
        System.out.print("Please enter description: ");
        String desc = scan.next();

        //Worth noting is fact that there is ABSOLUTELY NO error checking, which should be addressed in the future
        Offer offer = new Offer(tmsp, tmsp, adr, price, ot, offerId, userId, ft, x, surface, rooms, desc);

        //Here we add the object to both places. In the future it will be only one place or even function that getMyOffers
        //will get it from sql server
        this.uploadOffer(offer);
    }

    public void uploadOffer(Offer o){
        this.getServer().addOffer(o);
        this.getMyOffers().add(o);
    }
}
