package pl.RELS.Offer;

import java.sql.Timestamp;

// OOP principles /1/
// Override /3.2/
public class Rent extends Offer {
    /**
     * Here is a constructor to our abstract class Offer.
     * <p>
     * It is worth mentioning here what all of the variables actually mean in our program.
     * On top of the explanation of each variable one important thing. If someone decides to buy an apartment
     * the listing simply disappears from the listing and communication between the buyer and seller occurs/
     * This will be yet to be implemented.
     * <p>
     * Not mentioned variables here are viewCounter and followers variable.
     * viewCounter - variable that tracks how many times user showed more information on the offer
     * followers - variable that tracks ids of users that tract our offer.
     * The first one is initialized as 0 (because obviously no views are at the moment of uploading the offer) and
     * the second one is initialized to empty list because there are no followers yet.
     *
     * @param issueDate      - this is a timestamp when was the offer added to the system
     * @param expirationDate - this is a timestamp when the offer's validity ends. Usually it is 2 months from issueDate
     * @param address        - this is a String in which the address is in. Later we may change it to atomic variables (like
     *                       street or city to make the searches more efficient without touching the String format). It should be
     *                       formatted in this way (no checking yet):
     *                       Country;State;City;Street;BuildingNumber;ApartmentNumber
     * @param price          - this is a double which indicates whole price for the listed apartment. the price per square meter
     *                       will be converted via function. In the future there might be different variables for e.g. in rent for
     *                       electricity bills and/or water bills, to make it more clear.
     * @param type           - this OfferType variable indicates what type of offer it is. We may not need it (because we may have
     *                       a check like instanceof() for Java that checks the class.
     * @param offerId        - this long id will indicate an id of the offer in our system. In the beginning we will be tracking it, however later
     *                       on the DBMS will handle it for us.
     * @param userId         - this long id will indicate an id of the user in our system. In the beginning we will be tracking it, however later
     *                       *           on the DBMS will handle it for us.
     * @param floor          - this enum FloorType will indicate on which floor we have our real estate. In the future we might
     *                       deviate from this for more efficiency.
     * @param isFurnished    - this boolean variable gives us information whether the real estate comes with furniture
     * @param surface        - this double variable gives us number of squared meters in our real estate
     * @param rooms          - double variable because there are offers like 1,5 room (1 room plus kitchen that is shared)
     * @param description    - this should be self explanatory String variable. The seller writes long description of the
     *                       listed real estate. in the future we might handle it as html text and or limit the character
     *                       number
     */
    public Rent(Timestamp issueDate, Timestamp expirationDate, String address, double price, OfferType type, long offerId, long userId, FloorType floor, boolean isFurnished, double surface, double rooms, String description) {
        super(issueDate, expirationDate, address, price, type, offerId, userId, floor, isFurnished, surface, rooms, description);
    }

    @Override
    public void printMe() {
        System.out.print("This is a rent offer:");
        super.printMe();
        if(this.isFurnished)
            System.out.print("This apartment is furnished");
        else
            System.out.print("This apartment is not furnished");
    }
}
