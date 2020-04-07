package pl.RELS.Offer;

import pl.RELS.User.Seller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class OfferGenerator {

    static final Random RANDOM = new Random();
    HashMap<String, ArrayList<String>> pA;
    double minPrice, ratio, minSurface, maxSurface, minRooms, maxRooms;
    final double ERROR = 100000;

    public OfferGenerator(HashMap<String, ArrayList<String>> pA){
        this.pA = pA;
        this.minPrice = 10000.0;
        this.ratio = 0.3;
        this.minSurface = 10.0;
        this.maxSurface = 10000.0;
        this.minRooms =  2.0;
        this.maxRooms = 20.0;
    }

    public Offer offerGenerator(Seller seller){
        if(pA != null && (pA.containsKey("countries") && pA.containsKey("states") && pA.containsKey("cities") &&
                pA.containsKey("streets") && pA.containsKey("buildingNumbers") && pA.containsKey("apartmentNumbers"))) {
            ArrayList<String> genAdr = addressGenerator(1, pA.get("countries"), pA.get("states"), pA.get("cities"),
                    pA.get("streets"), pA.get("buildingNumbers"), pA.get("apartmentNumbers"));
            Timestamp tsIssue = new Timestamp(System.currentTimeMillis());
            Timestamp tsExpiration = getFortNight(tsIssue);
            String a = genAdr.get(0);
            double surface = RANDOM.nextDouble() * maxSurface + minSurface;
            double p = surface * RANDOM.nextDouble() * 0.3 + (minPrice + RANDOM.nextDouble() * ERROR);
            Offer.OfferType ot = Offer.OfferType.getRandomType();
            long oID = seller.getServer().getCurrentOfferId();
            long sID = seller.getUserId();
            Offer.FloorType ft = Offer.FloorType.getRandomFloor();
            boolean furnished = RANDOM.nextBoolean();
            double rooms = (double) (RANDOM.nextInt(((int) maxRooms - (int) minRooms)) + minRooms);
            String desc = "Lorem ipsum dolor sit amet";

            Offer o = new Offer(tsIssue, tsExpiration, a, p, ot, oID, sID, ft, furnished, surface, rooms, desc);
            return o;
        }
        else
            return null;
    }

    private Timestamp getFortNight(Timestamp ts){
        Calendar cal = Calendar.getInstance();
        cal.setTime(ts);
        cal.add(Calendar.DAY_OF_WEEK, 14);
        ts.setTime(cal.getTime().getTime());
        return ts;
    }

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
