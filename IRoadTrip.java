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

    /**
     * Constructs an IRoadTrip object, initializing the application with provided file data.
     *
     * The constructor takes an array of strings containing filenames required for initialization.
     * It ensures the correct number of files are provided and attempts to read and process the
     * country borders, country codes, and border distance data from the respective files.
     *
     * @param args An array of strings representing filenames: [borders.txt, capdist.csv, state_name.tsv]
     */
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

    /**
     * Retrieves the distance between two countries.
     *
     * @param country1 The name of the first country.
     * @param country2 The name of the second country.
     * @return The distance between the two countries in kilometers. Returns -1 if the countries do not share a land border or if either of the countries does not exist.
     */
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

    /**
     * Finds the shortest path between two countries.
     *
     * @param country1 The starting country.
     * @param country2 The destination country.
     * @return A list of strings representing the shortest path between country1 and country2.
     *         Each string in the list represents a step in the path in the format: "from_country --> to_country (DISTANCE_IN_KM)".
     *         Returns an empty list if no path exists between the countries or if either country doesn't exist.
     */
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

    /**
     * Prints the shortest path between countries.
     *
     * @param path The list representing the path between countries. Each element of the list should be in the format "Country A --> Country B (Distance in km)".
     *             If the list is empty, it indicates that no route exists between the countries.
     */
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

    /**
     * Allows the user to input country names to find the shortest path between two countries.
     * Takes user input for two country names and displays the shortest path between them until the user exits by typing "EXIT".
     * The method prompts the user for the names of two countries and validates the input. If the input is invalid (not in the country list),
     * it prompts the user again for a valid country name.
     * It finds the shortest path between the given countries and displays the path step by step until the user exits.
     */
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
        IRoadTrip roadTrip = new IRoadTrip(args);
        roadTrip.acceptUserInput();
    }

    /* Populating Hashmaps */

    /**
     * Reads the contents of a file to populate the countryBordersMap, which stores countries and their neighboring countries.
     * Each line in the file contains country information in the format "CountryName = NeighboringCountry1; NeighboringCountry2; ..."
     * The method reads the file, parses the content, and populates the countryBordersMap accordingly.
     * It processes each line to extract country names, their aliases (if present), and their neighboring countries,
     * then updates the countryBordersMap with the extracted information.
     * If an alias exists for a country, it's stored as a separate entry in the map.
     * Handles countries with two-word names and differentiates aliases from neighboring countries.
     * The method throws an IOException if there's an issue reading the file.
     *
     * @param fileName The name of the file containing country and neighboring country information.
     * @throws IOException If an I/O error occurs while reading the file.
     */
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

    /**
     * Populates the countryCodeMap with country codes and their respective country names or aliases.
     * Reads the contents of the given file, where each line contains data in a tab-separated format.
     * Extracts information including state ID, country name, and end date from the file.
     * Checks for the most recent date (2020-12-31) data and processes country names and aliases accordingly.
     * If an alias exists for a country, it's stored as a separate entry in the map along with the country name.
     * Handles countries with commas in their names by reformatting them to a standardized representation.
     * The method throws an IOException if there's an issue reading the file.
     *
     * @param fileName The name of the file containing country code and name information.
     * @throws IOException If an I/O error occurs while reading the file.
     */
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

    /**
     * Populates the borderDistanceMap with information about distances between countries.
     * Reads the contents of the given file, where each line contains data in a comma-separated format.
     * Extracts data including country codes and distances between them.
     * Utilizes the countryCodeMap to fetch full country names based on the provided country codes.
     * Checks if the fetched country names exist in the countryBordersMap, indicating a land border between them.
     * If both countries exist and share a land border, the method adds the border information (country and distance) to the map.
     * The method throws IOException or NumberFormatException if there's an issue reading the file or parsing the distance data.
     *
     * @param fileName The name of the file containing country codes and distance information.
     * @throws IOException              If an I/O error occurs while reading the file.
     * @throws NumberFormatException    If there's an issue parsing distance data to integers.
     */
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

    /* For Debugging Help */

    /**
     * Prints the country borders map, displaying each country along with its associated bordering countries.
     * Iterates through the countryBordersMap and prints the country names followed by their respective bordering countries.
     */
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

    /**
     * Prints the border distance map, displaying country pairs and the distances between them.
     * Iterates through the borderDistanceMap and prints country names with their respective neighboring country distances.
     */
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

    /**
     * Prints the country code map, displaying country codes along with their associated country names.
     * Iterates through the countryCodeMap and prints the country codes followed by their respective country names.
     */
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

