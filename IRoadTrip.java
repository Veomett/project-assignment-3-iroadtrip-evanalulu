import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.*;

public class IRoadTrip {

    /* Hash Maps */
    private static HashMap<String, List> countryBordersMap = new HashMap<>();
    private static HashMap<String, List<String>> countryCodeMap = new HashMap<>();
    private static HashMap<String, List<Tuple<String, Integer>>> borderDistanceMap = new HashMap<>();

    public IRoadTrip (String [] args) {
        if (args.length != 3) {
            System.err.println("Incorrect number of files provided.");
            System.exit(1);
        }
        try {
            // Putting the country and its borders with distances into HashMap
            populateCountryBordersMap(args[0]);
            populateCountryCodeMap(args[2]);
            populateBorderDistanceMap(args[1]);
        } catch (IOException e) {
            System.err.println("Error reading files: " + e.getMessage());
            System.exit(1);
        }
    }

    public int getDistance(String country1, String country2) {
        country1 = country1.toLowerCase();
        country2 =  country2.toLowerCase();
        if (borderDistanceMap.containsKey(country1)) {
            List<Tuple<String, Integer>> borders = borderDistanceMap.get(country1);
            // Check if country2 exists in country1's neighboring countries
            for (Tuple<String, Integer> border : borders) {
                if (border.country.equals(country2)) {
                    return border.getDistance();
                }
            }
        }
        return -1;
    }

    public List<String> findPath(String country1, String country2) {
        country1 = country1.toLowerCase();
        country2 =  country2.toLowerCase();

        Set<String> visited = new HashSet<>();
        HashMap<String, Integer> distances = new HashMap<>();
        HashMap<String, String> previous = new HashMap<>();
        PriorityQueue<Tuple<String, Integer>> pq = new PriorityQueue<>(Comparator.comparingInt(t -> t.distance));

        // Initialize distances with infinity for all nodes
        for (String node : borderDistanceMap.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
        }

        // Set distance of country1 node to 0
        distances.put(country1, 0);
        pq.add(new Tuple<>(country1, 0));

        while (!pq.isEmpty()) {
            Tuple<String, Integer> current = pq.poll();
            String currentCountry = current.country;

            if (!visited.contains(currentCountry)) {
                visited.add(currentCountry);

                if (borderDistanceMap.containsKey(currentCountry)) {
                    List<Tuple<String, Integer>> neighbors = borderDistanceMap.get(currentCountry);
                    if (neighbors != null) {
                        for (Tuple<String, Integer> neighbor : neighbors) {
                            String neighborCountry = neighbor.country;
                            int edgeWeight = neighbor.distance;

                            // Calculate new distance
                            int newDistance = distances.get(currentCountry) + edgeWeight;

                            // Compare new distance to the recorded distance for neighborCountry
                            if (newDistance < distances.getOrDefault(neighborCountry, Integer.MAX_VALUE)) {
                                distances.put(neighborCountry, newDistance);
                                previous.put(neighborCountry, currentCountry);
                                pq.add(new Tuple<>(neighborCountry, newDistance));
                            }
                        }
                    }
                }
            }
        }

        // Generate path
        List<String> path = new ArrayList<>();
        if (!previous.containsKey(country2)) {
            // No path found between the countries
            return path;
        }

        String currentCountry = country2;
        while (!currentCountry.equals(country1)) {
            String prevCountry = previous.get(currentCountry);
            int distance = distances.get(currentCountry) - distances.get(prevCountry);
            path.add(prevCountry + " --> " + currentCountry + " (" + distance + " km.)");
            currentCountry = prevCountry;
        }
        Collections.reverse(path);

        return path;
    }

    private static void printShortestPath(List<String> path) {
        if (path.isEmpty()) {
            System.out.println("No route exists.");
            return;
        }

        System.out.println("Route:");
        for (String step : path) {
            System.out.println("* " + step);
        }
    }


    public void acceptUserInput() {
        // Replace with your code
        System.out.println("IRoadTrip - skeleton");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter the name of the first country (type EXIT to quit): ");
            String country1 = scanner.nextLine().trim().toLowerCase();

            if (country1.equalsIgnoreCase("EXIT"))
                break;

            if (!countryBordersMap.containsKey(country1)) {
                System.out.println("Invalid country name. Please enter a valid country name.");
                continue;
            }

            System.out.print("Enter the name of the second country (type EXIT to quit): ");
            String country2 = scanner.nextLine().trim().toLowerCase();

            if (country2.equalsIgnoreCase("EXIT"))
                break;

            if (!countryBordersMap.containsKey(country2)) {
                System.out.println("Invalid country name. Please enter a valid country name.");
                continue;
            }

            List<String> path = findPath(country1, country2);
            printShortestPath(path);
        }

    }

    public static void main(String[] args) {
        IRoadTrip a3 = new IRoadTrip(args);

//        System.out.println(a3.findPath("Nepal", "Indonesia"));
        a3.acceptUserInput();
    }

    /* Populating Hashmaps */
    private static void populateCountryBordersMap(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=");

                if (parts.length == 2) {
                    String[] countryAndAlias = parts[0].trim().split("\\("); // Split at "(" to separate country and alias
                    String country = countryAndAlias[0].trim().toLowerCase();
                    String alias = null; // Store alias name if it exists

                    if (countryAndAlias.length > 1) {
                        alias = countryAndAlias[1].replace(")", "").trim().toLowerCase(); // Remove ")" and trim the alias
                    }
                    String[] borders = parts[1].split(";");
                    List<String> borderingCountries = new ArrayList<>();
                    for (String border : borders) {
                        boolean hasAlias = false;
                        String borderAlias = "";

                        String[] borderParts = border.trim().split(" ");
                        StringBuilder countryName = new StringBuilder();
                        for (String part : borderParts) {
                            if (part.contains("(")) {
                                hasAlias = true;
                                borderAlias = part.replace("(", "").replace(")", "").trim().toLowerCase();
                            }

                            if (!part.matches(".*\\d.*") && !part.equalsIgnoreCase("km") && !hasAlias) {
                                countryName.append(part).append(" ");
                            }

                        }
                        if (countryName.length() > 0 && !hasAlias) {
                            borderingCountries.add(countryName.toString().trim().toLowerCase());
                        } else {
                            borderingCountries.add(countryName.toString().trim().toLowerCase());
                            borderingCountries.add(borderAlias);
                        }
                    }

                    // If an alias exists, make a separate entry in map
                    if (alias != null) {
                        countryBordersMap.put(alias, borderingCountries);
                    }

                    // Handle countries with two-word names
                    if (country.contains(",")) {
                        String[] countryNameParts = country.split(",");
                        if (countryNameParts.length == 2) {
                            String updatedCountryName = countryNameParts[1].trim() + " " + countryNameParts[0].trim();
                            countryBordersMap.put(updatedCountryName, borderingCountries);
                        }
                    }

                    countryBordersMap.put(country, borderingCountries);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void populateCountryCodeMap(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");

                if (data.length >= 5) {
                    String stateID = data[1].trim();
                    String countryName = data[2].trim();
                    String endDate = data[4].trim();

                    // Check if the data is for the most recent date (2020-12-31)
                    if (endDate.equals("2020-12-31")) {
                        String[] countryAndAlias = countryName.split("\\(");
                        String country = countryAndAlias[0].trim();
                        String alias = null;

                        if (countryAndAlias.length > 1) {
                            alias = countryAndAlias[1].replace(")", "").trim();
                        }

                        // Handle countries with commas
                        if (country.contains(",")) {
                            String[] countryNameParts = country.split(",");
                            if (countryNameParts.length == 2) {
                                country = countryNameParts[1].trim() + " " + countryNameParts[0].trim();
                            }
                        }

                        if (alias != null) {
                            countryCodeMap.put(stateID, List.of(country.toLowerCase(), alias.toLowerCase()));
                        } else {
                            countryCodeMap.put(stateID, List.of(country.toLowerCase()));
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void populateBorderDistanceMap(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean firstLine = true; // Flag to track the first line
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; // Skip processing the first line
                    continue;
                }

                String[] data = line.split(",");
                String ida = data[1].trim(); // Country code A
                String idb = data[3].trim(); // Country code B
                int distance = Integer.parseInt(data[4].trim()); // Distance

                // Fetch full country names from countryCodeMap
                List<String> countryAList = countryCodeMap.get(ida);
                List<String> countryBList = countryCodeMap.get(idb);

                if (countryAList != null && countryBList != null) {
                    // Choose the first country name for simplicity, assuming it's the primary name
                    String countryA = countryAList.get(0);
                    String countryB = countryBList.get(0);

                    if (countryBordersMap.containsKey(countryA) && countryBordersMap.get(countryA).contains(countryB)) {
                        Tuple<String, Integer> tuple = new Tuple<>(countryB, distance);
                        List<Tuple<String, Integer>> borderInfos = borderDistanceMap.getOrDefault(countryA, new ArrayList<>());
                        borderInfos.add(tuple);
                        borderDistanceMap.put(countryA, borderInfos);
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /* Debug */
    private static void printCountryBordersMap() {
        for (String country : countryBordersMap.keySet()) {
            System.out.print(country + ":");
            List<String> borders = countryBordersMap.get(country);
            for (String border : borders) {
                System.out.print(border + ", ");
            }
            System.out.println();
        }
    }
    private static void printBorderDistanceMap() {
        for (String country : borderDistanceMap.keySet()) {
            System.out.print(country + ": {");
            List<Tuple<String, Integer>> borderInfo = borderDistanceMap.get(country);
            for (Tuple<String, Integer> tuple : borderInfo) {
                System.out.print(tuple.country + ": " + tuple.distance + ", ");
            }
            System.out.print("}");
            System.out.println();
        }
    }
    public static void printCountryCodeMap() {
        for (Map.Entry<String, List<String>> entry : countryCodeMap.entrySet()) {
            String countryCode = entry.getKey();
            List<String> countryNames = entry.getValue();

            System.out.print(countryCode + ": ");
            for (String name : countryNames) {
                System.out.print(name + ", ");
            }
            System.out.println();
        }
    }
}

