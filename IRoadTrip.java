import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyStore;
import java.util.List;
import java.util.*;

public class IRoadTrip {
    private static HashMap<String, List> countryBordersMap = new HashMap<>();
    private static HashMap<String, List<String>> countryCodeMap = new HashMap<>();
    private static HashMap<String, List<Tuple<String, Integer>>> borderDistanceMap = new HashMap<>();


    public IRoadTrip (String [] args) {
        // Replace with your code
    }


    public int getDistance(String country1, String country2) {
        // Replace with your code
        return -1;
    }


    public List<String> findPath(String country1, String country2) {
        // Replace with your code
        return null;
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

//            List<String> route = findPath(country1, country2);
//            if (route.isEmpty()) {
//                System.out.println("No route found between " + country1 + " and " + country2 + ".");
//            } else {
//                System.out.println("Route from " + country1 + " to " + country2 + ":");
//                printRoute(route);
//            }
        }

    }


    public static void main(String[] args) {
        IRoadTrip a3 = new IRoadTrip(args);


        // Putting the country and its borders with distances into the HashMap
        populateCountryBordersMap("borders.txt");
        populateCountryCodeMap("state_name.tsv");
        populateBorderDistanceMap("capdist.csv");

//        printCountryCodeMap();
//        printBorderDistanceMap();
//        a3.acceptUserInput();

        calculateShortestPathsFromSource("gabon", "france");
    }

    /* Populating Hashmaps */
    private static void populateCountryBordersMap(String fileName) {
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
                        String[] borderParts = border.trim().split(" ");
                        StringBuilder countryName = new StringBuilder();
                        for (String part : borderParts) {
                            if (!part.matches(".*\\d.*") && !part.equalsIgnoreCase("km")) {
                                countryName.append(part).append(" ");
                            }
                        }
                        if (countryName.length() > 0) {
                            borderingCountries.add(countryName.toString().trim().toLowerCase());
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
    public static void populateCountryCodeMap(String fileName) {
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

    private static void populateBorderDistanceMap(String fileName) {
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


    /* TEST */
    private static void calculateShortestPathsFromSource(String source, String destination) {
        Set<String> visited = new HashSet<>();
        HashMap<String, Integer> distances = new HashMap<>();
        HashMap<String, String> previous = new HashMap<>();
        PriorityQueue<Tuple<String, Integer>> pq = new PriorityQueue<>(Comparator.comparingInt(t -> t.distance));

        // Initialize distances with infinity for all nodes
        for (String node : borderDistanceMap.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
        }

        // Set distance of source node to 0
        distances.put(source, 0);
        pq.add(new Tuple<>(source, 0));

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

                            // Add a check to ensure neighborCountry exists in distances
                            if (distances.containsKey(neighborCountry)) {
                                int newDistance = distances.get(currentCountry) + edgeWeight;
                                if (newDistance < distances.get(neighborCountry)) {
                                    distances.put(neighborCountry, newDistance);
                                    previous.put(neighborCountry, currentCountry);
                                    pq.add(new Tuple<>(neighborCountry, newDistance));
                                }
                            }
                        }
                    }
                }
            }
        }

        // Print shortest paths
        printShortestPath(source, destination, previous, distances);

    }

    private static void printShortestPath(String source, String destination, HashMap<String, String> previous, HashMap<String, Integer> distances) {
        List<String> path = new ArrayList<>();
        for (String country = destination; country != null; country = previous.get(country)) {
            path.add(country);
        }
        Collections.reverse(path);

        System.out.println("Route from " + source + " to " + destination + ":");
        int totalDistance = distances.get(destination);
        for (int i = 0; i < path.size() - 1; i++) {
            String from = path.get(i);
            String to = path.get(i + 1);
            int dist = distances.get(to) - distances.get(from);
            System.out.println("* " + from + " --> " + to + " (" + dist + " km.)");
        }
        System.out.println("Total distance: " + totalDistance + " km.");
    }



}

