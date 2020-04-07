package pl.RELS.Offer;

import pl.RELS.User.Seller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;


/**
 * This class is responsible for creation of Offer objects. It's offerGenerator method returns one variation of the offer
 * per the given parameters that were set on creation. It has Random object so as to save some memory.
 */
public class OfferGenerator {

    //Random object to save memory and have constant output for testing purposes
    static final Random RANDOM = new Random(42);
    /**
     * This map is generated in MainApp method called setupAddressHashMap. This variable consists of parts of the address
     * to be taken from while generating the address. The fields are as follows:
     *         "countries" - ArrayList of possible Countries
     *         "states" - ArrayList of possible States
     *         "cities" - ArrayList of possible Cities
     *         "streets" - ArrayList of possible Streets
     *         "buildingNumbers" - ArrayList of possible Building Numbers
     *         "apartmentNumbers" - ArrayList of possible Apartment Numbers
     */
    HashMap<String, ArrayList<String>> pA;
    //Variables that are responsible for values that are outputed by random generator in number of rooms, price and surface
    //ratio is special variable since we create price using the surface generated previously so that it could fit linear
    //regression better. It is a coefficient by which we multiply the surface to get price.
    final double minPrice, ratio, minSurface, maxSurface, minRooms, maxRooms;

    //This will be responsible for error term in our price generator, because we cant have 1:1 translation of surface to price
    //(not including the ratio variable)
    final double ERROR = 100000;

    /**
     *  Constructor that takes HashMap and has other values set to default
     *
     * @param pA - Special HashMap generated using MainApp's method called setupAddressHashMap.
     *           This variable consists of parts of the address
     *       to be taken from while generating the address. The fields are as follows:
     *               "countries" - ArrayList of possible Countries
     *               "states" - ArrayList of possible States
     *               "cities" - ArrayList of possible Cities
     *               "streets" - ArrayList of possible Streets
     *               "buildingNumbers" - ArrayList of possible Building Numbers
     *               "apartmentNumbers" - ArrayList of possible Apartment Numbers
     */
    public OfferGenerator(HashMap<String, ArrayList<String>> pA){
        this.pA = pA;
        this.minPrice = 10000.0;
        this.ratio = 0.3;
        this.minSurface = 10.0;
        this.maxSurface = 10000.0;
        this.minRooms =  2.0;
        this.maxRooms = 20.0;
    }

    /**
     * Method returns Offer object which attributes were randomly created using the limits specified in Generator initialization.
     *
     * This method uses our HashMap to randomly pick them and create some fake address. Then it creates timestamps for the
     * offer's issue (current system time) and expiration date (fortnight from the issue).
     * Then it creates randomly generated surface which is bounded by the limits.
     * Aafterwards it uses Seller object to get it's offer and user Id. Then generates random floortype and offertype.
     * Also chooses whether apartment is furnished or not and how many rooms the apartment or house has.
     *
     * In the end method creates price - which has 0.01% to be an outlier value, and normal otherwise. Here is formula:
     * p = (surface * R * ratio) + {(min_p + R * error) || (min_p + R * error) *(100*R)} where R i random double(0,1).
     *
     * The formula is need because we dont want some random values to appear and literally destroy our estimator
     * (since there would be no variable it bases on).
     *
     * @param seller - Seller object that "creates" and offer
     * @return Offer object that is assigned to seller object from the parameter.
     */
    public Offer offerGenerator(Seller seller){
        if(pA != null && (pA.containsKey("countries") && pA.containsKey("states") && pA.containsKey("cities") &&
                pA.containsKey("streets") && pA.containsKey("buildingNumbers") && pA.containsKey("apartmentNumbers"))) {
            ArrayList<String> genAdr = addressGenerator(1, pA.get("countries"), pA.get("states"), pA.get("cities"),
                    pA.get("streets"), pA.get("buildingNumbers"), pA.get("apartmentNumbers"));
            Timestamp tsIssue = new Timestamp(System.currentTimeMillis());
            Timestamp tsExpiration = getFortNight(tsIssue);
            String a = genAdr.get(0);
            double surface = RANDOM.nextDouble() * maxSurface + minSurface;
            Offer.OfferType ot = Offer.OfferType.getRandomType();
            long oID = seller.getServer().getCurrentOfferId();
            long sID = seller.getUserId();
            Offer.FloorType ft = Offer.FloorType.getRandomFloor();
            boolean furnished = RANDOM.nextBoolean();
            double rooms = (double) (RANDOM.nextInt(((int) maxRooms - (int) minRooms)) + minRooms);
            String desc = "Lorem ipsum dolor sit amet";
            double p = 0.0;
            if (RANDOM.nextDouble() < 0.0001){
                p = surface * RANDOM.nextDouble() * ratio + (minPrice + RANDOM.nextDouble() * ERROR) * (RANDOM.nextDouble() * 100);
            }
            else {
                p = surface * RANDOM.nextDouble() * ratio + (minPrice + RANDOM.nextDouble() * ERROR);
            }

            Offer o = new Offer(tsIssue, tsExpiration, a, p, ot, oID, sID, ft, furnished, surface, rooms, desc);
            return o;
        }
        else
            return null;
    }

    /**
     * Function that return timestamp plus two weeks (fort night)
     * @param ts - input Timestamp object
     * @return ts + 2 weeks
     */
    private Timestamp getFortNight(Timestamp ts){
        Calendar cal = Calendar.getInstance();
        cal.setTime(ts);
        cal.add(Calendar.DAY_OF_WEEK, 14);
        ts.setTime(cal.getTime().getTime());
        return ts;
    }

    /**
     * Function returns an array of strings that imitates the address with this format
     * "country;state;city;street;buildingNumber;apartmentNumber"
     *
     * If we want to get only one address we specify n to be 1.
     *
     * @param n - number of addresses to be generated
     * @param countries - an arraylist of possible values for the country variable (Poland, Germany, France etc.)
     * @param states - an arraylist of possible values for the state variable (pomorskie etc.)
     * @param cities - an arraylist of possible values for the city variable (Gdansk, Berlin etc.)
     * @param streets - an arraylist of possible values for the street variable (Wojska Polskiego etc.)
     * @param buildingNumbers - an arraylist of possible values for the building Number variable (10, 12b, 18a)
     * @param apartmentNumbers - an arraylist of possible values for the apartment Number variable (1, 5, 8 etc.)
     * @return An Array of String addresses that is in the special format
     */
    public ArrayList<String> addressGenerator(int n, ArrayList<String> countries, ArrayList<String> states,
                                              ArrayList<String> cities, ArrayList<String> streets,
                                              ArrayList<String> buildingNumbers, ArrayList<String> apartmentNumbers){
        ArrayList<String> ret = new ArrayList<String>();
        String country, state, city, street, buildingNumber, apartmentNumber; int rand;
        for (int i = 0; i < n; i++) {
            rand = RANDOM.nextInt(countries.size());
            country = countries.get(rand);
            rand = RANDOM.nextInt(states.size());
            state = states.get(rand);
            rand = RANDOM.nextInt(cities.size());
            city = cities.get(rand);
            rand = RANDOM.nextInt(streets.size());
            street = buildingNumbers.get(rand);
            rand = RANDOM.nextInt(buildingNumbers.size());
            buildingNumber = buildingNumbers.get(rand);
            rand = RANDOM.nextInt(apartmentNumbers.size());
            apartmentNumber = apartmentNumbers.get(rand);
            String adr = String.join(";",country,state,city,street,buildingNumber,apartmentNumber);
            ret.add(adr);
        }
        return ret;
    }

}
