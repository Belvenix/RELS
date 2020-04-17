package pl.RELS.Offer;

import pl.RELS.Server;
import pl.RELS.User.Seller;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

// Comparators /2/

/**
 * Offer class handles everything related to offers. It handles uploading and deleting offers as well as their proper
 * creation (although the input of the user is handled inside Seller class)
 */
public class Offer implements Serializable {



    /** Enum Type that gives us values for most common types of offers we will have in our system.
     * In the future we may change it so we will check them via instanceof() syntax
     */
    public enum OfferType{
        SALE(0),
        RENT(1),
        LONG_TERM_RENT(2),
        SHORT_TERM_RENT(3),
        NONE(-1);
        private int value;
        OfferType(int value){
            this.value = value;
        }
        public int getValue(){
            return this.value;
        }
        private static final List<OfferType> VALUES =
                Collections.unmodifiableList(Arrays.asList(values()));
        private static final int SIZE = VALUES.size();
        private static final Random RANDOM = new Random();

        public static OfferType getRandomType(){
            return VALUES.get(RANDOM.nextInt(SIZE));
        }
    }

    /** Enum Type that gives us values for most common types of floor levels in our offers.
     * The namings should be self explanatory. Also added randomizer for extra functionality (getRandomFloor).
     */
    public enum FloorType{
        GROUND(0),
        FIRST(1),
        SECOND(2),
        THIRD(3),
        FORTH(4),
        FIFTH(5),
        SIXTH(6),
        SEVENTH(7),
        EIGHTH(8),
        NINTH(9),
        TENTH(10),
        NONE(-1);
        private int value;
        private FloorType(int value){
            this.value = value;
        }
        public int getValue(){
            return this.value;
        }

        //This syntax ensures that we dont have many instances of the Random class etc.
        private static final List<FloorType> VALUES =
                Collections.unmodifiableList(Arrays.asList(values()));
        private static final int SIZE = VALUES.size();
        private static final Random RANDOM = new Random();

        public static FloorType getRandomFloor(){
            return VALUES.get(RANDOM.nextInt(SIZE));
        }
    }

    //The variables are explained in constructor
    protected final Timestamp issueDate;
    protected Timestamp expirationDate;
    protected String address;
    protected double price;
    public final OfferType type;
    protected long offerId;
    protected final long userId;
    protected FloorType floor;
    protected boolean isFurnished;
    protected double surface;
    protected double rooms;
    public String description;
    protected int viewCounter;
    protected ArrayList<Long> followers;

    //Additional field so that serialization works 100% properly.
    static final long serialVersionUID = 42L;

    /** Here is a constructor to our abstract class Offer.
     *
     * <p>
     *     It is worth mentioning here what all of the variables actually mean in our program.
     *     On top of the explanation of each variable one important thing. If someone decides to buy an apartment
     *     the listing simply disappears from the listing and communication between the buyer and seller occurs/
     *     This will be yet to be implemented.
     *
     *      Not mentioned variables here are viewCounter and followers variable.
     *      viewCounter - variable that tracks how many times user showed more information on the offer
     *      followers - variable that tracks ids of users that tract our offer.
     *      The first one is initialized as 0 (because obviously no views are at the moment of uploading the offer) and
     *      the second one is initialized to empty list because there are no followers yet.
     *
     * @param issueDate - this is a timestamp when was the offer added to the system
     * @param expirationDate - this is a timestamp when the offer's validity ends. Usually it is 2 months from issueDate
     * @param address - this is a String in which the address is in. Later we may change it to atomic variables (like
     *                street or city to make the searches more efficient without touching the String format). It should be
     *                formatted in this way (no checking yet):
     *                Country;State;City;Street;BuildingNumber;ApartmentNumber
     * @param price - this is a double which indicates whole price for the listed apartment. the price per square meter
     *              will be converted via function. In the future there might be different variables for e.g. in rent for
     *              electricity bills and/or water bills, to make it more clear.
     * @param type - this OfferType variable indicates what type of offer it is. We may not need it (because we may have
     *             a check like instanceof() for Java that checks the class.
     * @param offerId - this long id will indicate an id of the offer in our system. In the beginning we will be tracking it, however later
     *           on the DBMS will handle it for us.
     * @param userId - this long id will indicate an id of the user in our system. In the beginning we will be tracking it, however later
     *      *           on the DBMS will handle it for us.
     * @param floor - this enum FloorType will indicate on which floor we have our real estate. In the future we might
     *              deviate from this for more efficiency.
     * @param isFurnished - this boolean variable gives us information whether the real estate comes with furniture
     * @param surface - this double variable gives us number of squared meters in our real estate
     * @param rooms - double variable because there are offers like 1,5 room (1 room plus kitchen that is shared)
     * @param description - this should be self explanatory String variable. The seller writes long description of the
     *                    listed real estate. in the future we might handle it as html text and or limit the character
     *                    number
     *
     *
     * </p>
     */
    public Offer(Timestamp issueDate,
                    Timestamp expirationDate,
                    String address,
                    double price,
                    OfferType type,
                    long offerId,
                    long userId,
                    FloorType floor,
                    boolean isFurnished,
                    double surface,
                    double rooms,
                    String description){
        this.issueDate = issueDate;
        this.expirationDate = expirationDate;
        this.address = address;
        this.price = price;
        this.type = type;
        this.offerId = offerId;
        this.userId = userId;
        this.floor = floor;
        this.isFurnished = isFurnished;
        this.surface = surface;
        this.rooms = rooms;
        this.description = description;
        this.viewCounter = 0;
        this.followers = new ArrayList<Long>();
    }

    //Default constructor for testing purposes
    //However we do need to pass Seller object to make it possible to "upload" the offer to server
    public Offer(Seller s){
        this.issueDate = new Timestamp(System.currentTimeMillis());
        this.expirationDate = new Timestamp(System.currentTimeMillis());
        this.address = "Polska;pomorskie;gdansk;wajdeloty;20;9";
        this.price = 123456.75;
        this.type = Offer.OfferType.SALE;
        this.offerId = s.getServer().getCurrentOfferId();
        this.userId = s.getUserId();
        this.floor = Offer.FloorType.GROUND;
        this.isFurnished = true;
        this.surface = 720.50;
        this.rooms = 5;
        this.description = "Super apartament kupuj smiało!";
        this.viewCounter = 0;
        this.followers = new ArrayList<Long>();
    }

    public Offer(){
        this.issueDate = new Timestamp(System.currentTimeMillis());
        this.expirationDate = new Timestamp(System.currentTimeMillis());
        this.address = "Polska;pomorskie;gdansk;wajdeloty;20;9";
        this.price = 123456.75;
        this.type = Offer.OfferType.SALE;
        this.offerId = -1;
        this.userId = -1;
        this.floor = Offer.FloorType.GROUND;
        this.isFurnished = true;
        this.surface = 720.50;
        this.rooms = 5;
        this.description = "Super apartament kupuj smiało!";
        this.viewCounter = 0;
        this.followers = new ArrayList<Long>();
    }

    //Constructor used for testing the Comparator Classes in the main method (Offer.class)

    public Offer(Seller s, double price, double surface){
        this.issueDate = new Timestamp(System.currentTimeMillis());
        this.expirationDate = new Timestamp(System.currentTimeMillis());
        this.address = "Polska;pomorskie;gdansk;wajdeloty;20;9";
        this.price = price;
        this.type = Offer.OfferType.SALE;
        this.offerId = s.getServer().getCurrentOfferId();
        this.userId = s.getUserId();
        this.floor = Offer.FloorType.GROUND;
        this.isFurnished = true;
        this.surface = surface;
        this.rooms = 5;
        this.description = "Super apartament kupuj smiało!";
        this.viewCounter = 0;
        this.followers = new ArrayList<Long>();
    }

    //--------------------------------------------------------------------------------------------
    //----------------------------------------METHODS---------------------------------------------
    //--------------------------------------------------------------------------------------------

    //Main class used for testing of two Comparator classes: PriceComparator and SurfaceComparator
    public static void main(String[] args){
        //First we initiate basic classes
        Server sr = new Server();
        Seller sl = new Seller(sr);

        Offer[] offArr = new Offer[10];
        //Then we upload ten different (in our exercise purpose) offers. The module here is to make the list not sorted
        for (int i = 0; i < 10; i++){
            if (i % 5 == 4){
                offArr[i] = new Offer(sl, 4567400.0 + i * 43.0, 430 - i * 42.4);
            }
            else {
                if (i % 2 == 0)
                    offArr[i] = new Offer(sl, (i+1) * 1000.0, 4000.0 / (i+1));
                else
                    offArr[i] = new Offer(sl, 100000.0 / (i+1), 40.0 * (i+1));
            }
        }

        //Now we will present three results: Default, Price and Surface sort.
        //Default Sort - (no sort, because we dont have the interface implemented!)
        System.out.println("Offer array, default (no) sort:\n" + Arrays.toString(offArr) + "\n");

        //Price Sort
        Arrays.sort(offArr, Offer.PriceComparator);
        System.out.println("Offer array, price sort:\n" + Arrays.toString(offArr) + "\n");

        //Surface Sort
        Arrays.sort(offArr, Offer.SurfaceComparator);
        System.out.println("Offer array, surface sort:\n" + Arrays.toString(offArr) + "\n");
    }

    /** Simple printer of our method. Only for testing purposes.
     *
     * WARNING! this returns different output compared to toString() method!
     */
    public void printMe(){
        System.out.println("Offer id: " + this.offerId + " cost: " + this.price + " surface: " + this.surface);
    }

    @Override
    public String toString(){
        return  "issueDate=" + this.issueDate.getTime() + "|expirationDate=" + this.expirationDate.getTime()  + "|address=" + this.address +
                "|price=" + this.price + "|type=" + this.type + "|offerId=" + this.offerId  + "|userId=" + this.userId +
                "|floor=" + this.floor + "|isFurnished=" + isFurnished + "|surface=" + this.surface +
                "|rooms=" + this.rooms + "|description=" + this.description;
    }

    //-----------------------------------------------------------------------------------------------
    //--------------------------------------COMPARATORS/2/-------------------------------------------
    //-----------------------------------------------------------------------------------------------

    /** Represents a comparator that will be using price field in Offer class for the order
     *
     *  This class will be used inside the sort function for the array variable in the Database (Server class) to sort
     *  in order all of the offers in the Database (Server class)
     */
    public static Comparator<Offer> PriceComparator = new Comparator<Offer>() {

        /** This function is necessary for the Comparator implementation. Compares its two arguments for order.
         *
         * @param o1 - first object to be compared (inside the Array sort function)
         * @param o2 - second object to be compared (inside the Array sort function)
         * @return - returns the difference between first and second object's price (of the real estate offer)
         */
        @Override
        public int compare(Offer o1, Offer o2) {
            return (int) (o1.getPrice() - o2.getPrice());
        }
    };

    /** Represents a comparator that will be using surface field in Offer class for the order
     *
     *  This class will be used inside the sort function for the array variable in the Database (Server class) to sort
     *  in order all of the offers in the Database (Server class)
     */
    public static Comparator<Offer> SurfaceComparator = new Comparator<Offer>() {

        /** This function is necessary for the Comparator implementation. Compares its two arguments for order.
         *
         * @param o1 - first object to be compared (inside the Array sort function)
         * @param o2 - second object to be compared (inside the Array sort function)
         * @return - returns the difference between first and second object's surface (of the real estate offer)
         */
        @Override
        public int compare(Offer o1, Offer o2) {
            return (int) (o1.getSurface() - o2.getSurface());
        }
    };

    //--------------------------------------------------------------------------------------------
    //----------------------------------------SETTERS---------------------------------------------
    //--------------------------------------------------------------------------------------------

    /** This setter sets a new value to the expirationDate.
     * <p>
     *     We want this behaviour only with expirationDate and not with issueDate because we can give an option
     *     to extend the offer validity time not the time it was issued. If we want to change the issueDate then
     *     we need to make a new offer.
     * </p>
     *
     * @param tmsp - new timestamp that we want to give the offer
     */
    public void setExpirationDate(Timestamp tmsp){
        this.expirationDate = tmsp;
    }

    /** This setter sets a new value to the address variable.
     *
     * @param address - String address that would be formatted like this:
     *                Country;State;City;Street;BuildingNumber;ApartmentNumber
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /** This setter sets a new value to the price variable
     *
     * @param price - double variable represents new value in dollars
     */
    public void setPrice(double price){
        this.price = price;
    }

    //simple setter
    public void setFloor(FloorType floor) {
        this.floor = floor;
    }

    /** This is a temporary setter for the followers list. In the future we might change it to some kind of append
     * or delete kind of setter.
     *
     * @param followers - new list of user ids that follow the specific offer
     */
    public void setFollowers(ArrayList<Long> followers) {
        this.followers = followers;
    }

    //simple setter
    public void setFurnished(boolean furnished) {
        isFurnished = furnished;
    }

    //simple setter
    public void setRooms(double rooms) {
        this.rooms = rooms;
    }

    //simple setter
    public void setSurface(double surface) {
        this.surface = surface;
    }

    /** This is a temporary setter for the view counter. In the future we might change it to some kind of append
     * or delete kind of setter.
     *
     * @param viewCounter - new int of how many views the offer has
     */
    public void setViewCounter(int viewCounter) {
        this.viewCounter = viewCounter;
    }

    //simple setter
    public void setDescription(String description) {
        this.description = description;
    }

    public void setOfferId(long id){
        this.offerId = id;
    }

    //--------------------------------------------------------------------------------------------
    //----------------------------------------GETTERS---------------------------------------------
    //--------------------------------------------------------------------------------------------

    //Here is a huge section of simple getters.
    //Nothing else.
    public double getPrice() {
        return price;
    }

    public double getRooms() {
        return rooms;
    }

    public double getSurface() {
        return surface;
    }

    public int getViewCounter() {
        return viewCounter;
    }

    public long getOfferId() {
        return offerId;
    }

    public long getUserId() {
        return userId;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public Timestamp getExpirationDate() {
        return expirationDate;
    }

    public Timestamp getIssueDate() {
        return issueDate;
    }

    public FloorType getFloor() {
        return floor;
    }

    public OfferType getType() {
        return type;
    }

    public boolean getFurnished() {
        return isFurnished;
    }

    public ArrayList<Long> getFollowers() {
        return followers;
    }
}
