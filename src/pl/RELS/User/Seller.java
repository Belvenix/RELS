package pl.RELS.User;

import pl.RELS.Offer.Offer;
import pl.RELS.Server;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

//This class implements deep copy /1/
// and Comparable interface /2/
public class Seller extends User implements Comparable<Seller>, Cloneable{

    //This variable holds all of offers that are yours o the market
    private ArrayList<Offer> myOffers;

    public Seller(String name, String surname, String user, String pass, String bank, Server s) {
        super(name, surname, user, pass, bank, s);
        this.myOffers = new ArrayList<Offer>();
    }

    //Most basic constructor for testing purposes
    public Seller(Server s){
        super("Jakub", "Belter", "user", "123", "123456789", s);
        this.myOffers = new ArrayList<Offer>();
    }

    //--------------------------------------------------------------------------------------------
    //----------------------------------------METHODS---------------------------------------------
    //--------------------------------------------------------------------------------------------

    //In this main we will be testing how our sorting works first (The Comparable interface one)
    //And afterwards we will test the deepcopy (with comparison to shallow copy)
    public static void main(String[] args){
        //-----------Comparable/2/-----------
        //First we need to initialize our server to be able to store the offers there.
        Server s = new Server();

        //This will represent the number of sellers in our array
        int iter = 5;

        //Then we will create an array of Sellers and we will give them some Offers
        Seller[] selArr = new Seller[iter];
        for (int i = 0; i < iter; i++){
            selArr[i] = new Seller(s);
            for (int j = 0; j < i; j++){
                selArr[i].uploadOffer(new Offer(selArr[i]));
            }
        }

        //Then we sort the array and show the result
        Arrays.sort(selArr);
        System.out.println("Sorting of Seller's list with the Comparable interface before offer deletion:\n"+ Arrays.toString(selArr));

        //Afterwards we want to show what happens if we remove some offers - whether the sort really works.
        //Hence we delete 2 offers at index 0 and 1 in the third and fifth seller.
        selArr[2].delOffer(0); selArr[2].delOffer(0); selArr[4].delOffer(0);;selArr[4].delOffer(0);

        //Then we show the results
        Arrays.sort(selArr);
        System.out.println("Sorting of Seller's list with the Comparable interface after offer deletion:\n"+ Arrays.toString(selArr));

        //-----------DEEPCOPY/1/-----------
        //Now we will test the deep copy
        System.out.println("Copying list of sellers to two different variables: one with deepcloning and another without.");
        Seller[] selArrDeepCopy = new Seller[iter];
        Seller[] selArrCopy = new Seller[iter];
        for (int i = 0; i < iter; i++){
            try {
                selArrDeepCopy[i] = (Seller) selArr[i].clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            selArrCopy[i] = selArr[i];
        }
        System.out.println("Original list of Sellers:\n" + Arrays.toString(selArr));
        System.out.println("Shallow copied list of Sellers:\n" + Arrays.toString(selArrCopy));
        System.out.println("Deep copied list of Sellers:\n" + Arrays.toString(selArrDeepCopy));
        System.out.println("Changing values in the original.\n");

        //Some changes to the original
        selArr[3].delOffer(0); selArr[3].delOffer(0);
        selArr[1].setUserId(100);
        //Here we can see that the original list changed, whereas the copy stayed.
        System.out.println("Original list of Sellers, after changes:\n" + Arrays.toString(selArr));
        System.out.println("Shallow copied list of Sellers, after changes:\n" + Arrays.toString(selArrCopy));
        System.out.println("Deep copied list of Sellers, after changes:\n" + Arrays.toString(selArrDeepCopy));

    }

    //The cloning
    @Override
    protected Object clone() throws CloneNotSupportedException {
        Seller s = new Seller(this.getServer());
        s.setUsername(this.getUsername());
        s.setBankId(this.getBankId());
        s.setPassword(this.getPassword());
        s.setUserId(this.getUserId());
        for(Offer offer: this.getMyOffers()){
            //Here is a clue of the deep copy:
            //We copy the offer object so that it is not the same reference
            //Some values are the same like address and price, however some are new
            //Like offerId as well as viewcounter or list of followers (since no one follows the copy)
            Offer o = new Offer(new Timestamp(System.currentTimeMillis()),
                    new Timestamp(System.currentTimeMillis()),
                    offer.getAddress(),
                    offer.getPrice(),
                    offer.getType(),
                    s.getServer().getCurrentOfferId(),
                    s.getUserId(),
                    offer.getFloor(),
                    offer.getFurnished(),
                    offer.getSurface(),
                    offer.getRooms(),
                    offer.getDescription()
                    );
            s.uploadOffer(o);
        }
        return s;
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

    /**
     * Simple offer deleter handler which deletes on index i. This will delete the offer in both the Seller and Server.
     *
     * @param i - index at which we delete our offer
     */
    public void delOffer(int i){
        Offer o = this.getMyOffers().get(i);
        this.getServer().delOffer(o);
        this.getMyOffers().remove(i);
    }

    /**
     * Simple offer deleter handler which deletes Object o. This will delete the offer in both the Seller and Server
     * @param o - Offer object to be deleted
     */
    public void delOffer(Offer o){
        int i = this.getMyOffers().indexOf(o);
        this.getServer().delOffer(o);
        this.getMyOffers().remove(i);
    }

    /**
     * Simple offer upload handler. This will add the offer in both the Seller and the Server.
     * @param o - Offer object that is going to be added to both Seller instance and the Server.
     */
    public void uploadOffer(Offer o){
        this.getServer().addOffer(o);
        this.getMyOffers().add(o);
    }

    /**
     * This function will be used for sorting sellers list length.
     *
     * @param o - Other Seller object that we compareTo
     * @return - returns a difference between sizes of the Sellers offers.
     */
    @Override
    public int compareTo(Seller o) {
        return this.getMyOffers().size() - o.getMyOffers().size();
    }

    @Override
    public String toString(){
        return  "(id=" + this.userId + ", #offers=" + this.getMyOffers().size() + ")";
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


}
