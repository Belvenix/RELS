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

    /** This is a constructor for Buyer class
     *
     * @param name - this is String name of the user
     * @param surname - this is String surname of the user
     * @param user - this is String username of the user
     * @param pass - this is String not hashed user password
     * @param bank - this is String bank id (credit card)
     */
    public Buyer(String name, String surname, String user, String pass, String bank, Server s) {
        super(name, surname, user, pass, bank, s);
    }

    /**
     * Constructor that uses only one parameter - Server variable
     * @param s - Server instance to which we want our user to be tied.
     */
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

    /** This method is responsible for handling the login action of user (input operations too)
     *
     * <p>
     *     In the beginning the passwords wont be hashed nor stored for simplicity. Although later on it will look
     *     for the password in some files or database and might be hashed in the future. It doesnt need any parameters
     * </p>
     *
     * @return the return of the function are giving us information what happened.
     *          If the login went great we get 1
     *          If there were invalid credentials given it will return 0
     *          Otherwise (some problem other than invalid credentials) it will return -1
     */
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

    /** This function will authenticate the user if valid credentials (username and password) are given
     *
     * @param username - username given by user trying to log in
     * @param password - password given by user trying to log in
     * @return true if there were good credentials given false otherwise
     */
    @Override
    protected boolean authenticate(String username, String password) {
        if (this.getUsername().equals(username) && this.getPassword().equals(password))
            return true;
        else
            return false;
    }


    /** This is a function that takes control of the program for the time being (until the logout or until user dont want
     *
     *  to interact with it)
     * @return -    returns -1 if it was failed login sequence
     *              returns 0 if user decided to logout and continue with program
     *              returns 1 if user decided to logout and quit the program
     *              returns 2 if there was some error
     */
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

    /** This method show all the offers to the user.
     * Worth noting fact is that seller showOffers shows only offers created by you which is different to buyer showOffer
     * method which presents all available offers
     * Might be transported to application in the future. More of a handler of situation.
     */
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
    protected void buyOffer(){
        System.out.println("You bought an offer!");
    }

    /**
     * Very basic buy offer method to imitate the buyer's buy action by index in the array. Later it may use the ID instead.
     * @param index - index at which the offer is stored in array
     */
    public void buyOffer(int index){
        this.getServer().delOffer(this.getServer().getByIndex(index));
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
