package resourceexchangearena;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;

// The arena is the core of the simulation, this is where all the resource exchanges take place.
public class ExchangeArena {
    // The current version of the simulation, used to organise output data.
    private static final String releaseVersion = "v1.0";

    // Create a single Random object for generating random numerical data for the simulation.
    static Random random = new Random();

    // Number of times the simulation will be run with the same parameters in order to take average results.
    private static final int SIMULATION_RUNS = 50;

    // Number of days to be simulated.
    private static final int DAYS = 50;

    // Maximum number of Agents who can be assigned to each time slot, assuming equal power draw per Agent.
    private static final int MAXIMUM_PEAK_CONSUMPTION = 16;

    // Number of rounds of exchanges per day.
    private static final int EXCHANGES = 200;

    // Number of time slots per day.
    static final int TOTAL_TIME_SLOTS = 24;

    // The number of agents in the simulation.
    private static final int POPULATION_SIZE = 96;

    // Integer values representing the available agent types for the simulation.
    static final int SELFISH = 1;
    static final int SOCIAL = 2;

    // List of all the Agents that are part of the exchange arena.
    static List<Agent> agents = new ArrayList<>();

    // List of all the time slots that are currently Allocated in the exchange arena.
    private static List<Integer> availableTimeSlots = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        // Python compiler.
        String pythonExe = "I:/code/REA_CondaEnvironment/python.exe";

        // Agent types requested for the simulation.
        int[] agentTypes = {SELFISH, SOCIAL};

        // Days that will have the agents satisfaction over the course of the day visualised.
        int[] daysOfInterest = {1,25,50};

        // Array of the unique agent types used in the simulation.
        ArrayList<Integer> uniqueAgentTypes = new ArrayList<>();
        for (int type : agentTypes) {
            if (!uniqueAgentTypes.contains(type)) {
                uniqueAgentTypes.add(type);
            }
        }

        // Sort the agent types so that they are ordered correctly in the output csv files.
        Collections.sort(uniqueAgentTypes);

        // Calculates the number of agents of each type for the simulation.
        int numberOfEachAgentType = POPULATION_SIZE / agentTypes.length;

        // 2D array list holding the average end of round satisfactions for each agent type.
        ArrayList<ArrayList<Double>> endOfRoundAverageSatisfactions = new ArrayList<>();

        // Create a unique name that will pair files from the same run of the simulation.
        long uniqueTag = System.currentTimeMillis();

        // Create a directory to store the raw data output by the simulation.
        String rawDataOutputFolder =
                "outputData/" + releaseVersion + "/" + uniqueTag + "/rawData";
        Path rawDataOutputPath = Paths.get(rawDataOutputFolder);
        Files.createDirectories(rawDataOutputPath);

        // Create an identifying filename containing the seed and types of agents in the simulation.
        StringBuilder fileName = new StringBuilder();
        for (Integer type : uniqueAgentTypes) {
            fileName.append(getHumanReadableAgentType(type));
        }
        fileName.append("_");
        fileName.append(uniqueTag);

        // Create a new csv file to store the individual agents satisfaction throughout the trading process.
        File individualFile = new File(
                rawDataOutputFolder,
                "duringDayAverages_" + fileName + ".csv");

        FileWriter individualCSVWriter = new FileWriter(individualFile);

        // Store the column headers for the individualCSVWriter file.
        individualCSVWriter.append("Simulation Run");
        individualCSVWriter.append(",");
        individualCSVWriter.append("Day");
        individualCSVWriter.append(",");
        individualCSVWriter.append("Round");
        individualCSVWriter.append(",");
        individualCSVWriter.append("Agent ID");
        individualCSVWriter.append(",");
        individualCSVWriter.append("Agent Type");
        individualCSVWriter.append(",");
        individualCSVWriter.append("Satisfaction");
        individualCSVWriter.append("\n");

        // Create a new csv file to store the individual agents satisfaction throughout the trading process,
        // averaged over multiple runs and storing only information on days that will later be analysed.
        // This greatly speeds up the python data visualisation.
        File filteredIndividualFile = new File(
                rawDataOutputFolder,
                "filteredDuringDayAverages_" + fileName + ".csv");

        FileWriter filteredIndividualCSVWriter = new FileWriter(filteredIndividualFile);

        // Store the column headers for the individualCSVWriter file.
        filteredIndividualCSVWriter.append("Day");
        filteredIndividualCSVWriter.append(",");
        filteredIndividualCSVWriter.append("Round");
        filteredIndividualCSVWriter.append(",");
        filteredIndividualCSVWriter.append("Agent Type");
        filteredIndividualCSVWriter.append(",");
        filteredIndividualCSVWriter.append("Satisfaction");
        filteredIndividualCSVWriter.append("\n");

        // Create a new csv file to store the average agent satisfaction at the end of each day.
        File averageFile = new File(
                rawDataOutputFolder ,
                "endOfDayAverages_" + fileName + ".csv");

        FileWriter averageCSVWriter = new FileWriter(averageFile);

        // Store the column headers for the averageCSVWriter file.
        averageCSVWriter.append("Simulation Run");
        averageCSVWriter.append(",");
        averageCSVWriter.append("Day");
        averageCSVWriter.append(",");
        averageCSVWriter.append("Random (No exchange)");
        averageCSVWriter.append(",");
        averageCSVWriter.append("Optimum (No exchange)");
        // Add columns for each unique agent type that exists in the simulation.
        for (Integer type : uniqueAgentTypes) {
            averageCSVWriter.append(",");
            averageCSVWriter.append(getHumanReadableAgentType(type));
        }
        averageCSVWriter.append("\n");

        // Run the simulation for as many runs as requested.
        for (int i = 1; i <= SIMULATION_RUNS; i++) {

            // The current system time is used to generate a replicable seed.
            long seed = System.currentTimeMillis();

            // The seed is used to set the simulations Random object.
            random.setSeed(seed);

            // The array position in 'agentTypes[]' of the agent type assigned to the agent.
            int agentType = 0;

            // The number of agents with the current agent type.
            int agentsOfThisType = 0;

            // Create the Agents for the simulation.
            for (int j = 1; j <= POPULATION_SIZE; j++){
                if (agentsOfThisType >= numberOfEachAgentType) {
                    agentType++;
                    agentsOfThisType = 0;
                }
                new Agent(j, agentTypes[agentType]);
                agentsOfThisType++;
            }

            // Initialise each Agents relations with each other Agent.
            for (Agent a: agents) {
                a.initializeFavoursStore();
            }
            for (int j = 1; j <= DAYS; j++) {

                // Fill the available time slots with all the slots that exist each day.
                for (int k = 1; k <= TOTAL_TIME_SLOTS; k++) {
                    for (int l = 1; l <= MAXIMUM_PEAK_CONSUMPTION; l++) {
                        // Each time slot can be allocated as many times as the maximum peak consumption allows.
                        availableTimeSlots.add(k);
                    }
                }

                // Start the day by having each agent make its requests and receive its initial random allocation.
                for (Agent a : agents) {
                    // Each agent makes its initial request for time slots.
                    ArrayList<Integer> requestedTimeSlots = a.requestTimeSlots();

                    // A random allocation of time slots is generated from what is available.
                    ArrayList<Integer> allocatedTimeSlots = getRandomInitialAllocation(requestedTimeSlots);

                    // The allocated time slots are given to the Agent.
                    a.receiveAllocatedTimeSlots(allocatedTimeSlots);
                }

                // Store the data for pre-exchange random and optimum average agent satisfactions.
                averageCSVWriter.append(String.valueOf(seed));
                averageCSVWriter.append(",");
                averageCSVWriter.append(String.valueOf(j));
                averageCSVWriter.append(",");
                averageCSVWriter.append(String.valueOf(averageAverageSatisfaction()));
                averageCSVWriter.append(",");
                averageCSVWriter.append(String.valueOf(optimumAllocationSatisfaction()));

                for (int k = 1; k <= EXCHANGES; k++) {
                    // 2D array list holding time slots each Agent may be willing to trade.
                    ArrayList<ArrayList<Integer>> advertisingBoard = new ArrayList<>();

                    // Create a copy of the Agents list and shuffle it, so they perform the exchanges in a random order.
                    List<Agent> shuffledAgents = new ArrayList<>(agents);
                    Collections.shuffle(shuffledAgents, random);

                    // Fill the advertising board.
                    for (Agent a : shuffledAgents) {
                        // The time slots that an Agent will trade and it's unique ID.
                        ArrayList<Integer> tradingData = new ArrayList<>();

                        // The agents identifying ID is always in position 0.
                        tradingData.add(a.ID);
                        tradingData.addAll(a.publishUnlockedTimeSlots());

                        // Add the data to the advertising board.
                        advertisingBoard.add(tradingData);
                    }

                    // Reshuffle the Agents.
                    Collections.shuffle(shuffledAgents, random);

                    // Agents make requests for trades.
                    for (Agent a : shuffledAgents) {
                        ArrayList<Integer> chosenAdvert = a.requestExchange(advertisingBoard);
                        // Only attempt to send the request if one has been made.
                        if (chosenAdvert != null) {
                            ArrayList<Integer> request = new ArrayList<>();
                            // An exchange request first contains the offering agents ID.
                            request.add(a.ID);

                            // Next it adds the time slot that has been requested.
                            request.add(chosenAdvert.get(1));

                            // Finally it adds a random unwanted time slot to exchange.
                            ArrayList<Integer> unwantedTimeSlots = a.publishUnwantedTimeSlots();
                            int selector = random.nextInt(unwantedTimeSlots.size());
                            int unwantedTimeSlot = unwantedTimeSlots.get(selector);
                            request.add(unwantedTimeSlot);

                            for (Agent b : agents) {
                                if (b.ID == chosenAdvert.get(0)) {
                                    b.receiveExchangeRequest(request);
                                    break;
                                }
                            }
                        }
                    }

                    // Reshuffle the Agents.
                    Collections.shuffle(shuffledAgents, random);
                    for (Agent a : shuffledAgents) {
                        // Agents consider and prioritise the requests they have received.
                        a.considerRequests();
                    }

                    // Reshuffle the Agents.
                    Collections.shuffle(shuffledAgents, random);

                    for (Agent a: shuffledAgents) {
                        ArrayList<ArrayList<Integer>> offersToAccept = a.getAcceptableRequests();
                        for (ArrayList<Integer> offer : offersToAccept) {
                            if (a.finalCheck(offer.get(1))) {
                                for (Agent b : agents) {
                                    if (b.ID == offer.get(0)) {
                                        if (b.finalCheck(offer.get(2))) {
                                            b.completeRequestedExchange(offer, a.ID);
                                            a.completeReceivedExchange(offer);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        // Clear the agents accepted offers list before the next exchange round.
                        if (!offersToAccept.isEmpty()) {
                            a.clearAcceptedRequests();
                        }
                    }

                    // Store the data for post-exchange individual agent satisfaction for each Agent.
                    for (Agent a : agents) {
                        individualCSVWriter.append(String.valueOf(seed));
                        individualCSVWriter.append(",");
                        individualCSVWriter.append(String.valueOf(j));
                        individualCSVWriter.append(",");
                        individualCSVWriter.append(String.valueOf(k));
                        individualCSVWriter.append(",");
                        individualCSVWriter.append(String.valueOf(a.ID));
                        individualCSVWriter.append(",");
                        individualCSVWriter.append(String.valueOf(a.getAgentType()));
                        individualCSVWriter.append(",");
                        individualCSVWriter.append(String.valueOf(a.calculateSatisfaction(null)));
                        individualCSVWriter.append("\n");
                    }

                    // Store filtered data for post-exchange individual agent satisfaction scores.
                    int currentDay = j;
                    if (IntStream.of(daysOfInterest).anyMatch(val -> val == currentDay)) {
                        for (int uniqueAgentType : uniqueAgentTypes) {
                            double averageSatisfaction = averageAverageSatisfaction(uniqueAgentType);
                            ArrayList<Double> endOfRoundAverageSatisfaction = new ArrayList<>();
                            endOfRoundAverageSatisfaction.add((double) j);
                            endOfRoundAverageSatisfaction.add((double) k);
                            endOfRoundAverageSatisfaction.add((double) uniqueAgentType);
                            endOfRoundAverageSatisfaction.add(averageSatisfaction);

                            endOfRoundAverageSatisfactions.add(endOfRoundAverageSatisfaction);
                        }
                    }
                }

                // Store the data for post-exchange average agent satisfactions for each agent type.
                for (int uniqueAgentType : uniqueAgentTypes) {
                    // Get the integer representing the unique agent type.
                    averageCSVWriter.append(",");
                    averageCSVWriter.append(String.valueOf(averageAverageSatisfaction(uniqueAgentType)));
                }
                averageCSVWriter.append("\n");

                // Print to console after each 10th day so the progress can be monitored.
                if (j % 10 == 0) {
                    System.out.println("RUN: " + i + "  DAY: " + j);
                }
            }

            // Clear the list of agents before the next simulation begins.
            if (!agents.isEmpty()) {
                agents.clear();
            }
        }

        for (int day: daysOfInterest) {
            for (int i = 1; i <= EXCHANGES; i++) {
                for (int agentType : uniqueAgentTypes) {
                    ArrayList<Double> allSimsEndOfRoundAverageSatisfaction = new ArrayList<>();
                    for (ArrayList<Double> endOfRoundAverageSatisfaction : endOfRoundAverageSatisfactions) {
                        if ((endOfRoundAverageSatisfaction.get(0) == (double) day) &&
                                (endOfRoundAverageSatisfaction.get(1) == (double) i) &&
                                (endOfRoundAverageSatisfaction.get(2) == (double) agentType)) {
                            allSimsEndOfRoundAverageSatisfaction.add(endOfRoundAverageSatisfaction.get(3));
                        }
                    }
                    double averageOverSims = allSimsEndOfRoundAverageSatisfaction.stream().mapToDouble(val -> val).average().orElse(0.0);

                    filteredIndividualCSVWriter.append(String.valueOf(day));
                    filteredIndividualCSVWriter.append(",");
                    filteredIndividualCSVWriter.append(String.valueOf(i));
                    filteredIndividualCSVWriter.append(",");
                    filteredIndividualCSVWriter.append(String.valueOf(agentType));
                    filteredIndividualCSVWriter.append(",");
                    filteredIndividualCSVWriter.append(String.valueOf(averageOverSims));
                    filteredIndividualCSVWriter.append("\n");
                }
            }
        }

        // Close the csv file writers once the simulation is complete.
        individualCSVWriter.close();
        averageCSVWriter.close();
        filteredIndividualCSVWriter.close();

        String pythonPath = "I:/code/ResourceExchangeArena/src/datahandler/DataVisualiser.py";
        String daysToAnalyse = Arrays.toString(daysOfInterest);

        List<String> pythonArgs = new ArrayList<>();

        pythonArgs.add(pythonExe);
        pythonArgs.add(pythonPath);
        pythonArgs.add(releaseVersion);
        pythonArgs.add(Long.toString(uniqueTag));
        pythonArgs.add(averageFile.getAbsolutePath());
        pythonArgs.add(filteredIndividualFile.getAbsolutePath());
        pythonArgs.add(daysToAnalyse);

        ProcessBuilder builder = new ProcessBuilder(pythonArgs);
        builder.inheritIO();
        builder.redirectErrorStream(true);

        Process process = builder.start();
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Give a random initial time slot allocation to an Agent based on the number of time slots it requests.
    private static ArrayList<Integer> getRandomInitialAllocation(ArrayList<Integer> requestedTimeSlots) {                   // CAN ASSIGN SAME SLOT TWICE TO AN AGENT, NEED TO USE PSEUDO RANDOM APPROACH WITH HASH MAPS, ASSIGNING MOST COMMONLY AVAILABLE SLOTS FIRST TO SOLVE THIS.
        // The time slots that will be allocated to the Agent.
        ArrayList<Integer> timeSlots = new ArrayList<>();

        for (int i = 1; i <= requestedTimeSlots.size(); i++) {
            // Only allocate time slots if there are slots available to allocate.
            if (!availableTimeSlots.isEmpty()) {
                int selector = random.nextInt(availableTimeSlots.size());
                int timeSlot = availableTimeSlots.get(selector);

                timeSlots.add(timeSlot);

                availableTimeSlots.remove(selector);
            }
        }
        //System.out.println(availableTimeSlots);
        return timeSlots;
    }

    // Returns the average satisfaction of all agents.
    private static double averageAverageSatisfaction() {
        // Stores the individual Agents satisfaction values.
        ArrayList<Double> agentSatisfactions = new ArrayList<>();

        // Each agent calculates its individual satisfaction level.
        for (Agent a : agents) {
            agentSatisfactions.add(a.calculateSatisfaction(null));
        }
        // The average satisfaction of all agents is calculated and returned.
        return agentSatisfactions.stream().mapToDouble(val -> val).average().orElse(0.0);
    }

    // Returns the average satisfaction of all agents of a given type.
    private static double averageAverageSatisfaction(int agentType) {
        // Stores the individual Agents satisfaction values.
        ArrayList<Double> agentSatisfactions = new ArrayList<>();

        // Each agent calculates its individual satisfaction level.
        for (Agent a : agents) {
            if (a.getAgentType() == agentType) {
                agentSatisfactions.add(a.calculateSatisfaction(null));
            }
        }
        // The average satisfaction of all agents is calculated and returned.
        return agentSatisfactions.stream().mapToDouble(val -> val).average().orElse(0.0);
    }

    // Returns the optimum average satisfaction possible with the current requests and allocations in the simulation.
    private static double optimumAllocationSatisfaction() {
        // Stores all the slots that the agents have requested.
        ArrayList<Integer> allRequestedSlots = new ArrayList<>();

        // Stores all the slots that the agents have been allocated.
        ArrayList<Integer> allAllocatedSlots = new ArrayList<>();

        // Each agent shares its requested and allocated slots.
        for (Agent a : agents) {
            allRequestedSlots.addAll(a.publishRequestedTimeSlots());
            allAllocatedSlots.addAll(a.publishAllocatedTimeSlots());
        }

        // Stores the number of slots that could potentially be fulfilled with perfect trading.
        int satisfiedSlots = 0;

        // Stores the total number of slots requested by all Agents.
        int totalSlots = allRequestedSlots.size();

        for (Integer slot : allRequestedSlots) {
            if (allAllocatedSlots.contains(slot)) {
                // For each request, if it has been allocated to any agent, increase the number of satisfied slots.
                satisfiedSlots++;

                // Remove the slot from the list of all allocated slots so no slots can be allocated twice.
                allAllocatedSlots.remove(slot);
            }
        }
        // Return the average satisfaction if all slots were fulfilled optimally, between 1 and 0.
        return ((double)satisfiedSlots) / totalSlots;
    }

    // Returns the agent type in a human readable form for file names.
    private static String getHumanReadableAgentType(int agentType) {
        String name;
        // Names match types specified by static Integers for the ExchangeArena.
        switch(agentType) {
            case SOCIAL:
                name = "Social";
                break;
            case SELFISH:
                name = "Selfish";
                break;
            default:
                // If a human readable name doesnt exist, return the integer agentType as a string.
                name = String.valueOf(agentType);
        }
        return name;
    }
}
