import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.*;

public class IRoadTrip {
    private static HashMap<String, List> countryBordersMap = new HashMap<>();
    private static HashMap<String, String> countryCodeMap = new HashMap<>();
    private static HashMap<String, List<Tuple<String, Integer>>> borderDistanceMap = new HashMap<>();


    public IRoadTrip (String [] args) {
        // Replace with your code
    }


    public int getDistance (String country1, String country2) {
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

//        printBorderDistance();
//        printCountryBordersMap();
//        a3.acceptUserInput();

    }

//    private static void populateCountryBordersMap(String fileName) {
//        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                String[] parts = line.split("=");
//                if (parts.length == 2) {
//                    String[] countryAndAlias = parts[0].trim().split("\\("); // Split at "(" to separate country and alias
//                    String country = countryAndAlias[0].trim().toLowerCase();
//                    String alias = null; // Store alias name if it exists
//
//                    if (countryAndAlias.length > 1) {
//                        alias = countryAndAlias[1].replace(")", "").trim().toLowerCase(); // Remove ")" and trim the alias
//                    }
//
//                    String countryPart = parts[0].trim().toLowerCase();
//                    String[] countrySegments = countryPart.split(", ");
//
//                    List<String> borderingCountries = new ArrayList<>();
//                    String[] borders = parts[1].split(";");
//                    for (String border : borders) {
//                        String[] borderParts = border.trim().split(" ");
//                        StringBuilder countryName = new StringBuilder();
//                        for (String part : borderParts) {
//                            if (!part.matches(".*\\d.*") && !part.equalsIgnoreCase("km")) {
//                                countryName.append(part).append(" ");
//                            }
//                        }
//                        if (countryName.length() > 0) {
//                            borderingCountries.add(countryName.toString().trim().toLowerCase());
//                        }
//                    }
//
//                    // Process each segment separately
//                    for (String segment : countrySegments) {
//                        // Exclude segments with aliases
//                        if (alias != null) {
//                            // Check if the segment is North or South
//                            if (segment.equalsIgnoreCase("North") || segment.equalsIgnoreCase("South")) {
//                                if (countryBordersMap.containsKey(countrySegments[1] + " " + countrySegments[0])) {
//                                    countryBordersMap.get(countrySegments[1] + " " + countrySegments[0]).addAll(borderingCountries);
//                                } else {
//                                    countryBordersMap.put(countrySegments[1] + " " + countrySegments[0], borderingCountries);
//                                }
//                            }
//                        } else {
//                            countryBordersMap.put(segment, borderingCountries);
//                        }
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

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

                    // If an alias exists, make a seperate entry in map
                    if (alias != null)
                        countryBordersMap.put(alias, borderingCountries);

                    // South Korea North Korea edge case:
                    if (country.equalsIgnoreCase("Korea, North"))
                        countryBordersMap.put("north korea", borderingCountries);
                    else if (country.equalsIgnoreCase("Korea, South"))
                        countryBordersMap.put("south Korea", borderingCountries);

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
                        countryCodeMap.put(stateID, countryName.toLowerCase());
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
                String countryA = countryCodeMap.get(ida);
                String countryB = countryCodeMap.get(idb);

                if (countryA != null && countryB != null) {
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

    private static void printBorderDistance() {
        for (String country : borderDistanceMap.keySet()) {
            System.out.print(country + ": {");
            List<Tuple<String, Integer>> borderInfo = borderDistanceMap.get(country);
            for (Tuple<String, Integer> tuple : borderInfo) {
                System.out.print(tuple.x + ": " + tuple.y + ", ");
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



}

