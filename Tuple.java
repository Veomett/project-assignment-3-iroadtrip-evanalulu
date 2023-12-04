public class Tuple<COUNTRY, DISTANCE> {
    public final COUNTRY country;
    public final DISTANCE distance;

    /**
     * Constructs a Tuple object with a country and its associated distance.
     *
     * @param country  The country.
     * @param distance The distance related to the country.
     */
    public Tuple(COUNTRY country, DISTANCE distance) {
        this.country = country;
        this.distance = distance;
    }

    /**
     * Retrieves the distance associated with the country in the tuple.
     *
     * @return The distance related to the country.
     */
    public DISTANCE getDistance() {
        return distance;
    }
}