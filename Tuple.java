public class Tuple<COUNTRY, DISTANCE> {
    public final COUNTRY country;
    public final DISTANCE distance;
    public Tuple(COUNTRY country, DISTANCE distance) {
        this.country = country;
        this.distance = distance;
    }

    public DISTANCE getDistance() {
        return distance;
    }
}