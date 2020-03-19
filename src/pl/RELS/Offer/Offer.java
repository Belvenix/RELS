package pl.RELS.Offer;

import java.sql.Timestamp;
import java.util.ArrayList;


public class Offer {

    /**
     * Enum Type that gives us values for most common types of offers we will have in our system. In the future we may
     * change it so we will check them via instanceof() syntax
     */
    public enum OfferType{
        SALE(0),
        RENT(1),
        LONG_TERM_RENT(2),
        SHORT_TERM_RENT(3),
        NONE(4);
        private int value;
        private OfferType(int value){
            this.value = value;
        }
        public int getValue(){
            return this.value;
        }
    }

    /**
     * Enum Type that gives us values for most common types of floor levels in our offers. The namings should be
     * self explanatory. Also added some int values functionality for ease of use.
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
    }

    //The variables are explained in constructor
    protected final Timestamp issueDate;
    protected Timestamp expirationDate;
    protected String address;
    protected double price;
    public final OfferType type;
    protected final long offerId;
    protected final long userId;
    protected FloorType floor;
    protected boolean isFurnished;
    protected double surface;
    protected double rooms;
    public String description;
    protected int viewCounter;
    protected ArrayList<Long> followers;

    /**
     * Here is a constructor to our abstract class Offer.
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

    /**
     * This setter sets a new value to the expirationDate.
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

    /**
     * This setter sets a new value to the address variable.
     * @param address - String address that would be formatted like this:
     *                Country;State;City;Street;BuildingNumber;ApartmentNumber
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * This setter sets a new value to the price variable
     * @param price - double variable represents new value in dollars
     */
    public void setPrice(double price){
        this.price = price;
    }

    //simple setter
    public void setFloor(FloorType floor) {
        this.floor = floor;
    }

    /**
     * This is a temporary setter for the followers list. In the future we might change it to some kind of append
     * or delete kind of setter.
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

    /**
     * This is a temporary setter for the view counter. In the future we might change it to some kind of append
     * or delete kind of setter.
     * @param viewCounter - new int of how many views the offer has
     */
    public void setViewCounter(int viewCounter) {
        this.viewCounter = viewCounter;
    }

    //simple setter
    public void setDescription(String description) {
        this.description = description;
    }

    public void printMe(){
        System.out.println("Offer id: " + this.offerId + " cost: " + this.price);
    }

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

    public ArrayList<Long> getFollowers() {
        return followers;
    }
}
