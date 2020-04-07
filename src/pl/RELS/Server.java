package pl.RELS;

import pl.RELS.Offer.Offer;

import java.util.ArrayList;

/** This class is responsible for handling all of the data like a Server Manager.
 * Per first creation it would be a very simple class that would later on handle SQL code etc.
 */
public class Server {

    /** allOffers variable for now is having all of the offers inside the system.
     */
    protected ArrayList<Offer> allOffers;
    /** currentOfferId field has current offer id. It will increment per one added offer.
     */
    protected long currentOfferId;
    /** currentUserId field has current user id.
     * In the future It will increment per one added user (not yet supported!).
     */
    protected long currentUserId;

    /**
     * public constructor that sets basic Server class parameters -
     *  allOffers = empty list
     *  currentOfferId = 1
     *  currentUserId = 1
     */
    public Server(){
        this.allOffers = new ArrayList<Offer>();
        this.currentOfferId = 1;
        this.currentUserId = 1;
    }

    /** This is a function of mainApp that handles how to add an offer to the system. It increments currentOfferId as well
     *
     * @param o - this is properly initialized Offer object to be added to the list.
     * @return - returns 1 if everything went great,
     *           returns -1 if something went wrong
     */
    public int addOffer(Offer o){
        if (o != null) {
            o.setOfferId(currentOfferId++);
            currentOfferId += 1;
            allOffers.add(o);
            return 1;
        }
        else{
            return -1;
        }
    }


    /** This is a simple function that deletes a certain offer object from the list after the buyer buys it.
     * This method DOES NOT decrement current offer id since it is not 100% the last element
     * @param o - an Offer object to be deleted.
     * @return - returns 1 if it deletes the object.
     *           returns -1 if something goes wrong.
     */
    public int delOffer(Offer o){
        if (o != null){
            this.getAllOffers().remove(o);
            return 1;
        }
        else{
            System.out.println("Sorry, something went wrong! The offer doesnt exist");
            return -1;
        }
    }

    /**
     * This function returns current offer id and DOESN'T add one to the value. It is changed in addOffer
     * @return - returns current offer id
     */
    public long getCurrentOfferId(){
        return this.currentOfferId;
    }


    /**
     * Method for getting the offer using it's index.
     * @param id - integer index in the list
     * @return Offer object at index specified
     */
    public Offer getByIndex(int id){
        if (id < this.getAllOffers().size())
            return this.getAllOffers().get(id);
        else{
            System.out.println(this.getAllOffers().size() + " " + id);
            return null;
        }

    }

    /**
     * This function returns current user Id, then adds one to it.
     * @return - returns current user id
     */
    public long currentUserId(){
        long ret = this.currentUserId;
        this.currentUserId += 1;
        return ret;
    }

    public ArrayList<Offer> getAllOffers() {
        return allOffers;
    }

}
