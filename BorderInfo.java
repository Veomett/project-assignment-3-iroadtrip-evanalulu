public class BorderInfo {
    private String neighboringCountry;
    private int distance;
    public BorderInfo(String neighboringCountry, int distance) {
        this.neighboringCountry = neighboringCountry;
        this.distance = (int) distance;
    }


    public String getNeighboringCountry() {
        return neighboringCountry;
    }

    public void setNeighboringCountry(String neighboringCountry) {
        this.neighboringCountry = neighboringCountry;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
