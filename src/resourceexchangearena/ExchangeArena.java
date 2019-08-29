package resourceexchangearena;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;

/**
 * The arena is the core of the simulation, this is where all the resource exchanges take place.
 */
public class ExchangeArena {
    // The current version of the simulation, used to organise output data.
    private static final String RELEASE_VERSION = "v2.0";

    // Constants defining the scope of the simulation.
    private static final int SIMULATION_RUNS = 50;
    private static final int DAYS = 50;
    private static final int EXCHANGES = 200;
    private static final int POPULATION_SIZE = 96;
    private static final int MAXIMUM_PEAK_CONSUMPTION = 16;
    static final int UNIQUE_TIME_SLOTS = 24;
    static final int SLOTS_PER_AGENT = 4;

    // Boolean constant determining whether Agents of different types will initially exist in the same simulation.
    // Essentially used to switch between versions 1.0 and 2.0;
    private static final boolean VARIED_AGENT_TYPES = true;

    // Constants representing the available agent types for the simulation.
    private static final int NON_TRADER = 0;
    static final int SELFISH = 1;
    static final int SOCIAL = 2;

    // Create a single Random object for generating random numerical data for the simulation.
    static Random random = new Random();

    // List of all the Agents that are part of the current simulation.
    static List<Agent> agents = new ArrayList<>();

    // List of all the possible allocations that exist in the current simulation.
    private static List<Integer> availableTimeSlots = new ArrayList<>();

    /**
     * This is the main method which runs the entire ResourceExchangeArena simulation.
     *
     * @param args Unused.
     * @exception IOException On input error.
     * @see IOException
     */
    public static void main(String[] args) throws IOException {
        // Agent types that will be simulated.
        int[] agentTypes = {SELFISH, SOCIAL, SOCIAL, SOCIAL, SOCIAL};

        // Days that will have the Agents average satisfaction over the course of the day,
        // and satisfaction distribution at the end of the day visualised.
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

        // Create a unique seed that can be used to identify files from the same run of the simulation.
        long seed = System.currentTimeMillis();
        String initialSeed = Long.toString(seed);

        // Create directories to store the data output by the simulation.
        String rawDataOutputFolder =
                "outputData/" + RELEASE_VERSION + "/" + initialSeed + "/rawData";
        Path rawDataOutputPath = Paths.get(rawDataOutputFolder);
        Files.createDirectories(rawDataOutputPath);

        String prePreparedDataOutputFolder =
                "outputData/" + RELEASE_VERSION + "/" + initialSeed + "/prePreparedData";
        Path prePreparedDataOutputPath = Paths.get(prePreparedDataOutputFolder);
        Files.createDirectories(prePreparedDataOutputPath);

        // Create an identifying filename containing the seed and types of agents in the simulation to form
        // the basis of the filenames for all data output by the simulation.
        StringBuilder fileName = new StringBuilder();
        for (Integer type : uniqueAgentTypes) {
            fileName.append(getHumanReadableAgentType(type));
        }
        fileName.append("_");
        fileName.append(initialSeed);

        // Stores the average satisfaction of each Agent type at the end of each day, as well
        // as the optimum average satisfaction and the satisfaction if allocations remained random.
        File averageFile = new File(
                rawDataOutputFolder ,
                "endOfDayAverages_" + fileName + ".csv");

        FileWriter averageCSVWriter = new FileWriter(averageFile);

        averageCSVWriter.append("Simulation Run");
        averageCSVWriter.append(",");
        averageCSVWriter.append("Day");
        averageCSVWriter.append(",");
        averageCSVWriter.append("Random (No exchange)");
        averageCSVWriter.append(",");
        averageCSVWriter.append("Optimum (No exchange)");
        for (Integer type : uniqueAgentTypes) {
            averageCSVWriter.append(",");
            averageCSVWriter.append(getHumanReadableAgentType(type));
        }
        averageCSVWriter.append("\n");

        // Stores the average satisfaction of each Agent type at the end of each day, as well
        // as the optimum average satisfaction and the satisfaction if allocations remained random.
        // Values are averaged over multiple simulation runs rather than stored separately.
        File prePreparedAverageFile = new File(
                prePreparedDataOutputFolder ,
                "prePreparedEndOfDayAverages_" + fileName + ".csv");

        FileWriter prePreparedAverageCSVWriter = new FileWriter(prePreparedAverageFile);

        prePreparedAverageCSVWriter.append("Day");
        prePreparedAverageCSVWriter.append(",");
        prePreparedAverageCSVWriter.append("Random (No exchange)");
        prePreparedAverageCSVWriter.append(",");
        prePreparedAverageCSVWriter.append("Optimum (No exchange)");
        for (Integer type : uniqueAgentTypes) {
            prePreparedAverageCSVWriter.append(",");
            prePreparedAverageCSVWriter.append(getHumanReadableAgentType(type));
        }
        prePreparedAverageCSVWriter.append("\n");

        // Stores the satisfaction of each individual Agent at the end of every round throughout the simulation.
        File individualFile = new File(
                rawDataOutputFolder,
                "duringDayAverages_" + fileName + ".csv");

        FileWriter individualCSVWriter = new FileWriter(individualFile);

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

        // Stores the satisfaction of each individual Agent at the end of every round throughout the simulation.
        // Only stores data for days in the daysOfInterest array and averages data over multiple simulation runs.
        File prePreparedIndividualFile = new File(
                prePreparedDataOutputFolder,
                "prePreparedDuringDayAverages_" + fileName + ".csv");

        FileWriter prePreparedIndividualCSVWriter = new FileWriter(prePreparedIndividualFile);

        prePreparedIndividualCSVWriter.append("Day");
        prePreparedIndividualCSVWriter.append(",");
        prePreparedIndividualCSVWriter.append("Round");
        prePreparedIndividualCSVWriter.append(",");
        prePreparedIndividualCSVWriter.append("Agent Type");
        prePreparedIndividualCSVWriter.append(",");
        prePreparedIndividualCSVWriter.append("Satisfaction");
        prePreparedIndividualCSVWriter.append("\n");

        // Stores the satisfaction of each individual Agent at the end of each of the days in the daysOfInterest array.
        // Satisfactions are averaged over multiple simulation runs and can be separated by the Agents Type in order
        // to produce box and whisker plots of the satisfaction distributions.
        File prePreparedBoxPlotFile = new File(
                prePreparedDataOutputFolder,
                "prePreparedBoxPlotData_" + fileName + ".csv");


        FileWriter prePreparedBoxPlotCSVWriter = new FileWriter(prePreparedBoxPlotFile);

        prePreparedBoxPlotCSVWriter.append("Day");
        prePreparedBoxPlotCSVWriter.append(",");
        prePreparedBoxPlotCSVWriter.append("Agent Type");
        prePreparedBoxPlotCSVWriter.append(",");
        prePreparedBoxPlotCSVWriter.append("Satisfaction");
        prePreparedBoxPlotCSVWriter.append("\n");

        // Shows how the population of each Agent type varies throughout the simulation, influenced by social learning.
        File prePreparedPopulationDistributionsFile = new File(
                prePreparedDataOutputFolder,
                "prePreparedPopulationDistributionsData_" + fileName + ".csv");


        FileWriter prePreparedPopulationDistributionsCSVWriter = new FileWriter(prePreparedPopulationDistributionsFile);

        prePreparedPopulationDistributionsCSVWriter.append("Day");
        prePreparedPopulationDistributionsCSVWriter.append(",");
        prePreparedPopulationDistributionsCSVWriter.append("Agent Type");
        prePreparedPopulationDistributionsCSVWriter.append(",");
        prePreparedPopulationDistributionsCSVWriter.append("Population");
        prePreparedPopulationDistributionsCSVWriter.append("\n");

        if (VARIED_AGENT_TYPES) {
            // Calculate the number of Agents of each type for when VARIED_AGENT_TYPES is true.
            int numberOfEachAgentType = POPULATION_SIZE / agentTypes.length;

            // Array lists used to temporarily store data before averaging and adding it to csv files.
            ArrayList<ArrayList<Double>> endOfDayAverageSatisfactions = new ArrayList<>();
            ArrayList<ArrayList<Double>> endOfRoundAverageSatisfactions = new ArrayList<>();
            ArrayList<ArrayList<ArrayList<Integer>>> endOfDayPopulationDistributions = new ArrayList<>();

            for (int i = 1; i <= DAYS; i++) {
                ArrayList<ArrayList<Integer>> endOfDayPopulationDistribution = new ArrayList<>();
                endOfDayPopulationDistributions.add(endOfDayPopulationDistribution);
            }
            for (ArrayList<ArrayList<Integer>> day: endOfDayPopulationDistributions) {
                for (int i = 1; i <= uniqueAgentTypes.size(); i++) {
                    ArrayList<Integer> populations = new ArrayList<>();
                    day.add(populations);
                }
            }

            for (int i = 1; i <= SIMULATION_RUNS; i++) {
                // Clear the list of agents before a simulation begins.
                if (!agents.isEmpty()) {
                    agents.clear();
                }

                int agentType = 0;
                int agentsOfThisType = 0;

                // Create the Agents for the simulation.
                for (int j = 1; j <= POPULATION_SIZE; j++) {
                    if (agentsOfThisType >= numberOfEachAgentType) {
                        agentType++;
                        agentsOfThisType = 0;
                    }
                    if (agentType < agentTypes.length) {
                        new Agent(j, agentTypes[agentType]);
                        agentsOfThisType++;
                    } else {
                        j--;
                        agentType = 0;
                        agentsOfThisType = 0;
                        numberOfEachAgentType = 1;
                    }
                }

                // Increment the simulations seed each run.
                seed++;
                random.setSeed(seed);

                // Initialise each Agents relations with each other Agent if trading Agents are being simulated.
                for (Agent a : agents) {
                    a.initializeFavoursStore();
                }


                for (int j = 1; j <= DAYS; j++) {
                    // Create a copy of the Agents list that can be shuffled so Agents act in a random order.
                    List<Agent> shuffledAgents = new ArrayList<>(agents);

                    // Fill the available time slots with all the slots that exist each day.
                    for (int k = 1; k <= UNIQUE_TIME_SLOTS; k++) {
                        for (int l = 1; l <= MAXIMUM_PEAK_CONSUMPTION; l++) {
                            availableTimeSlots.add(k);
                        }
                    }
                    // Agents start the day by requesting and receiving an allocation of time slots.
                    Collections.shuffle(shuffledAgents, random);
                    for (Agent a : shuffledAgents) {
                        ArrayList<Integer> requestedTimeSlots = a.requestTimeSlots();
                        ArrayList<Integer> allocatedTimeSlots = getRandomInitialAllocation(requestedTimeSlots);
                        a.receiveAllocatedTimeSlots(allocatedTimeSlots);
                    }

                    double randomAllocations = averageAgentSatisfaction();
                    double optimumAllocations = optimumAgentSatisfaction();

                    // The random and optimum average satisfaction scores are calculated before exchanges take place.
                    averageCSVWriter.append(String.valueOf(seed));
                    averageCSVWriter.append(",");
                    averageCSVWriter.append(String.valueOf(j));
                    averageCSVWriter.append(",");
                    averageCSVWriter.append(String.valueOf(randomAllocations));
                    averageCSVWriter.append(",");
                    averageCSVWriter.append(String.valueOf(optimumAllocations));

                    for (int k = 1; k <= EXCHANGES; k++) {
                        ArrayList<ArrayList<Integer>> advertisingBoard = new ArrayList<>();

                        // Reset the check for whether each Agent has made an interaction this round.
                        for (Agent a : agents) {
                            a.setMadeInteraction(false);
                        }

                        // Exchanges start by Agents advertising time slots they may be willing to exchange.
                        Collections.shuffle(shuffledAgents, random);
                        for (Agent a : shuffledAgents) {
                            ArrayList<Integer> unlockedTimeSlots = a.publishUnlockedTimeSlots();
                            if (!unlockedTimeSlots.isEmpty()) {
                                ArrayList<Integer> advert = new ArrayList<>();
                                advert.add(a.agentID);
                                advert.addAll(a.publishUnlockedTimeSlots());
                                advertisingBoard.add(advert);
                            }
                        }

                        // Each Agent has the opportunity to make exchange requests for advertised time slots.
                        Collections.shuffle(shuffledAgents, random);
                        for (Agent a : shuffledAgents) {
                            if (a.canMakeInteraction()) {
                                ArrayList<Integer> chosenAdvert = a.requestExchange(advertisingBoard);
                                a.setMadeInteraction(true);
                                if (!chosenAdvert.isEmpty()) {
                                    // Select an unwanted time slot to offer in the exchange.
                                    ArrayList<Integer> unwantedTimeSlots = a.publishUnwantedTimeSlots();
                                    int selector = random.nextInt(unwantedTimeSlots.size());
                                    int unwantedTimeSlot = unwantedTimeSlots.get(selector);

                                    ArrayList<Integer> request = new ArrayList<>();
                                    request.add(a.agentID);
                                    request.add(chosenAdvert.get(1));
                                    request.add(unwantedTimeSlot);

                                    // The agent who offered the requested time slot receives the exchange request.
                                    for (Agent b : agents) {
                                        if (b.agentID == chosenAdvert.get(0)) {
                                            b.receiveExchangeRequest(request);
                                            b.setMadeInteraction(true);
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        // Agents who have received a request consider it.
                        Collections.shuffle(shuffledAgents, random);
                        for (Agent a : shuffledAgents) {
                            if (!a.getExchangeRequestReceived().isEmpty()) {
                                a.considerRequest();
                            }
                        }

                        // Agents confirm and complete approved requests if they are able to do so, and update
                        // their relations with other Agents accordingly.
                        Collections.shuffle(shuffledAgents, random);
                        for (Agent a : shuffledAgents) {
                            if (a.getExchangeRequestApproved()) {
                                ArrayList<Integer> offer = a.getExchangeRequestReceived();
                                if (a.finalCheck(offer.get(1))) {
                                    for (Agent b : agents) {
                                        if (b.agentID == offer.get(0)) {
                                            if (b.finalCheck(offer.get(2))) {
                                                b.completeRequestedExchange(offer, a.agentID);
                                                a.completeReceivedExchange(offer);
                                            }
                                            break;
                                        }
                                    }
                                }
                                a.setExchangeRequestApproved();
                            }
                            // Clear the agents accepted offers list before the next exchange round.
                            if (!a.getExchangeRequestReceived().isEmpty()) {
                                a.setExchangeRequestReceived();
                            }
                        }

                        for (Agent a : agents) {
                            individualCSVWriter.append(String.valueOf(seed));
                            individualCSVWriter.append(",");
                            individualCSVWriter.append(String.valueOf(j));
                            individualCSVWriter.append(",");
                            individualCSVWriter.append(String.valueOf(k));
                            individualCSVWriter.append(",");
                            individualCSVWriter.append(String.valueOf(a.agentID));
                            individualCSVWriter.append(",");
                            individualCSVWriter.append(String.valueOf(a.getAgentType()));
                            individualCSVWriter.append(",");
                            individualCSVWriter.append(String.valueOf(a.calculateSatisfaction(null)));
                            individualCSVWriter.append("\n");
                        }

                        // The average end of round satisfaction is stored for each Agent type if the current day exists in
                        // the daysOfInterest array. This data can later be averaged over simulation runs and added to
                        // the prePreparedIndividualFile.
                        int currentDay = j;
                        if (IntStream.of(daysOfInterest).anyMatch(val -> val == currentDay)) {
                            for (int uniqueAgentType : uniqueAgentTypes) {
                                double averageSatisfactionForType = averageAgentSatisfaction(uniqueAgentType);
                                ArrayList<Double> endOfRoundAverageSatisfaction = new ArrayList<>();
                                endOfRoundAverageSatisfaction.add((double) j);
                                endOfRoundAverageSatisfaction.add((double) k);
                                endOfRoundAverageSatisfaction.add((double) uniqueAgentType);
                                endOfRoundAverageSatisfaction.add(averageSatisfactionForType);

                                endOfRoundAverageSatisfactions.add(endOfRoundAverageSatisfaction);
                            }
                        }
                    }

                    // The average end of day satisfaction is stored for each Agent type to later be averaged
                    // and added to the prePreparedAverageFile.
                    ArrayList<Double> endOfDayAverageSatisfaction = new ArrayList<>();
                    endOfDayAverageSatisfaction.add((double) j);
                    endOfDayAverageSatisfaction.add(randomAllocations);
                    endOfDayAverageSatisfaction.add(optimumAllocations);

                    // Store the end of day average satisfaction for each agent type.
                    for (int uniqueAgentType : uniqueAgentTypes) {
                        double typeAverageSatisfaction = averageAgentSatisfaction(uniqueAgentType);
                        averageCSVWriter.append(",");
                        averageCSVWriter.append(String.valueOf(typeAverageSatisfaction));
                        endOfDayAverageSatisfaction.add(typeAverageSatisfaction);
                    }
                    averageCSVWriter.append("\n");
                    endOfDayAverageSatisfactions.add(endOfDayAverageSatisfaction);

                    // EVOLUTION
                    int[][] agentFitnessScores = new int[POPULATION_SIZE][3];
                    Collections.shuffle(shuffledAgents, random);
                    for (Agent a : shuffledAgents) {
                        agentFitnessScores[a.agentID - 1][0] = a.agentID;
                        agentFitnessScores[a.agentID - 1][1] = (int)(a.calculateSatisfaction(null) * SLOTS_PER_AGENT);
                        agentFitnessScores[a.agentID - 1][2] = a.getAgentType();
                    }

                    Arrays.sort(agentFitnessScores, Comparator.comparingInt(o -> o[1]));


                    int totalFitness = 0;
                    for (int[] agentFitnessScore : agentFitnessScores) {
                        totalFitness = totalFitness + agentFitnessScore[1];
                    }

                    for (Agent a : agents) {
                        int rouletteOutcome = random.nextInt(totalFitness) + 1;
                        int rouletteSegment = 0;
                        for (int[] agentFitnessScore : agentFitnessScores) {
                            rouletteSegment = rouletteSegment + agentFitnessScore[1];
                            if (rouletteSegment >= rouletteOutcome) {
                                a.setType(agentFitnessScore[2]);
                                break;
                            }
                        }
                    }

                    for (Integer uniqueAgentType : uniqueAgentTypes) {
                        int populationQuantity = 0;
                        for (Agent a : agents) {
                            if (a.getAgentType() == uniqueAgentType) {
                                populationQuantity++;
                            }
                        }
                        endOfDayPopulationDistributions.get(j - 1).get(uniqueAgentTypes.indexOf(uniqueAgentType)).add(populationQuantity);
                    }
                    // Only update the user on the simulations status every 10 days to reduce output spam.
                    if (j % 10 == 0) {
                        System.out.println("RUN: " + i + "  DAY: " + j);
                    }
                }
            }

            // The end of day satisfactions for each agent type, as well as for random and optimum allocations,
            // are averaged over simulation runs and appended to the prePreparedAverageFile.
            int types = uniqueAgentTypes.size() + 2;
            for (int i = 1; i <= DAYS; i++) {
                prePreparedAverageCSVWriter.append(String.valueOf(i));
                for (int j = 1; j <= types; j++) {
                    ArrayList<Double> allSatisfactions = new ArrayList<>();
                    for (ArrayList<Double> endOfDayAverageSatisfaction : endOfDayAverageSatisfactions) {
                        if (endOfDayAverageSatisfaction.get(0) == (double) i) {
                            allSatisfactions.add(endOfDayAverageSatisfaction.get(j));
                        }
                    }
                    double averageOverSims = allSatisfactions.stream().mapToDouble(val -> val).average().orElse(0.0);
                    prePreparedAverageCSVWriter.append(",");
                    prePreparedAverageCSVWriter.append(String.valueOf(averageOverSims));
                }
                prePreparedAverageCSVWriter.append("\n");
            }

            // The average end of round satisfaction is stored for each Agent type for all rounds during the days in the
            // daysOfInterest array. These end of round averages are themselves averaged over all simulation runs before
            // being added to the prePreparedIndividualFile.
            for (int day : daysOfInterest) {
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

                        prePreparedIndividualCSVWriter.append(String.valueOf(day));
                        prePreparedIndividualCSVWriter.append(",");
                        prePreparedIndividualCSVWriter.append(String.valueOf(i));
                        prePreparedIndividualCSVWriter.append(",");
                        prePreparedIndividualCSVWriter.append(String.valueOf(agentType));
                        prePreparedIndividualCSVWriter.append(",");
                        prePreparedIndividualCSVWriter.append(String.valueOf(averageOverSims));
                        prePreparedIndividualCSVWriter.append("\n");
                    }
                }
            }

            for (int i = 1; i <= DAYS; i++) {
                ArrayList<ArrayList<Integer>> daysPopulations = endOfDayPopulationDistributions.get(i - 1);
                for (int j = 0; j < uniqueAgentTypes.size(); j++) {
                    prePreparedPopulationDistributionsCSVWriter.append(String.valueOf(i));
                    prePreparedPopulationDistributionsCSVWriter.append(",");
                    prePreparedPopulationDistributionsCSVWriter.append(String.valueOf(uniqueAgentTypes.get(j)));
                    prePreparedPopulationDistributionsCSVWriter.append(",");

                    ArrayList<Integer> allPopulations = daysPopulations.get(j);
                    int sumOfPopulations = 0;
                    for (Integer population : allPopulations) {
                        sumOfPopulations = sumOfPopulations + population;
                    }
                    double averagePopulation = (double) sumOfPopulations / SIMULATION_RUNS;

                    prePreparedPopulationDistributionsCSVWriter.append(String.valueOf(averagePopulation));
                    prePreparedPopulationDistributionsCSVWriter.append("\n");
                }
            }
        } else {                                                                                                         // TODO: THERE IS A LOT OF UNUSED DATA BEING COLLECTED HERE FOR THE RAW DATA FILES, FURTHER OPTIMISATION WOULD REDUCE OUTPUT FILE SIZE BY OVER 1GB.
            ArrayList<ArrayList<Double>> endOfDayAverageSatisfactions = new ArrayList<>();

            // Starts at -2 for simulating random and optimal allocations.
            for (int type = -2; type < uniqueAgentTypes.size(); type++) {
                ArrayList<Double> averagedEndOfDayAverageSatisfactions = new ArrayList<>();
                endOfDayAverageSatisfactions.add(averagedEndOfDayAverageSatisfactions);

                // Array lists used to temporarily store data before averaging and adding it to csv files.
                ArrayList<ArrayList<Double>> endOfDayAverageSatisfactionsForType = new ArrayList<>();
                ArrayList<ArrayList<Double>> endOfRoundAverageSatisfactions = new ArrayList<>();
                ArrayList<ArrayList<Double>> endOfDayIndividualSatisfactions = new ArrayList<>();

                for (int i = 1; i <= DAYS; i++) {
                    ArrayList<Double> endOfDayAverageSatisfactionPerDay = new ArrayList<>();
                    endOfDayAverageSatisfactionsForType.add(endOfDayAverageSatisfactionPerDay);
                }

                for (int i = 1; i <= SIMULATION_RUNS; i++) {
                    // Clear the list of agents before a simulation begins.
                    if (!agents.isEmpty()) {
                        agents.clear();
                    }

                    if (type < uniqueAgentTypes.size()) {
                        if (type < 0) {
                            for (int j = 1; j <= POPULATION_SIZE; j++) {
                                new Agent(j, NON_TRADER);
                            }
                        } else {
                            for (int j = 1; j <= POPULATION_SIZE; j++) {
                                new Agent(j, uniqueAgentTypes.get(type));
                            }
                        }
                    } else {
                        break;
                    }

                    // Increment the simulations seed each run.
                    seed++;
                    random.setSeed(seed);

                    // Initialise each Agents relations with each other Agent if trading Agents are being simulated.
                    if (type >= 0) {
                        for (Agent a : agents) {
                            a.initializeFavoursStore();
                        }
                    }

                    // Create a copy of the Agents list that can be shuffled so Agents act in a random order.
                    List<Agent> shuffledAgents = new ArrayList<>(agents);

                    for (int j = 1; j <= DAYS; j++) {
                        // Fill the available time slots with all the slots that exist each day.
                        for (int k = 1; k <= UNIQUE_TIME_SLOTS; k++) {
                            for (int l = 1; l <= MAXIMUM_PEAK_CONSUMPTION; l++) {
                                availableTimeSlots.add(k);
                            }
                        }
                        // Agents start the day by requesting and receiving an allocation of time slots.
                        Collections.shuffle(shuffledAgents, random);
                        for (Agent a : shuffledAgents) {
                            ArrayList<Integer> requestedTimeSlots = a.requestTimeSlots();
                            ArrayList<Integer> allocatedTimeSlots = getRandomInitialAllocation(requestedTimeSlots);
                            a.receiveAllocatedTimeSlots(allocatedTimeSlots);
                        }

                        double randomAllocations = averageAgentSatisfaction();
                        double optimumAllocations = optimumAgentSatisfaction();

                        // The random and optimum average satisfaction scores are calculated before exchanges take place.
                        averageCSVWriter.append(String.valueOf(seed));
                        averageCSVWriter.append(",");
                        averageCSVWriter.append(String.valueOf(j));
                        averageCSVWriter.append(",");
                        averageCSVWriter.append(String.valueOf(randomAllocations));
                        averageCSVWriter.append(",");
                        averageCSVWriter.append(String.valueOf(optimumAllocations));

                        if (type >= 0) {
                            for (int k = 1; k <= EXCHANGES; k++) {
                                ArrayList<ArrayList<Integer>> advertisingBoard = new ArrayList<>();

                                // Reset the check for whether each Agent has made an interaction this round.
                                for (Agent a : shuffledAgents) {
                                    a.setMadeInteraction(false);
                                }

                                // Exchanges start by Agents advertising time slots they may be willing to exchange.
                                Collections.shuffle(shuffledAgents, random);
                                for (Agent a : shuffledAgents) {
                                    ArrayList<Integer> unlockedTimeSlots = a.publishUnlockedTimeSlots();
                                    if (!unlockedTimeSlots.isEmpty()) {
                                        ArrayList<Integer> advert = new ArrayList<>();
                                        advert.add(a.agentID);
                                        advert.addAll(a.publishUnlockedTimeSlots());
                                        advertisingBoard.add(advert);
                                    }
                                }

                                // Each Agent has the opportunity to make exchange requests for advertised time slots.
                                Collections.shuffle(shuffledAgents, random);
                                for (Agent a : shuffledAgents) {
                                    if (a.canMakeInteraction()) {
                                        ArrayList<Integer> chosenAdvert = a.requestExchange(advertisingBoard);
                                        a.setMadeInteraction(true);
                                        if (!chosenAdvert.isEmpty()) {
                                            // Select an unwanted time slot to offer in the exchange.
                                            ArrayList<Integer> unwantedTimeSlots = a.publishUnwantedTimeSlots();
                                            int selector = random.nextInt(unwantedTimeSlots.size ());
                                            int unwantedTimeSlot = unwantedTimeSlots.get(selector);

                                            ArrayList<Integer> request = new ArrayList<>();
                                            request.add(a.agentID);
                                            request.add(chosenAdvert.get(1));
                                            request.add(unwantedTimeSlot);

                                            // The agent who offered the requested time slot receives the exchange request.
                                            for (Agent b : agents) {
                                                if (b.agentID == chosenAdvert.get(0)) {
                                                    b.receiveExchangeRequest(request);
                                                    b.setMadeInteraction(true);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }

                                // Agents who have received a request consider it.
                                Collections.shuffle(shuffledAgents, random);
                                for (Agent a : agents) {
                                    if (!a.getExchangeRequestReceived().isEmpty()) {
                                        a.considerRequest();
                                    }
                                }

                                // Agents confirm and complete approved requests if they are able to do so, and update
                                // their relations with other Agents accordingly.
                                Collections.shuffle(shuffledAgents, random);
                                for (Agent a : shuffledAgents) {
                                    if (a.getExchangeRequestApproved()) {
                                        ArrayList<Integer> offer = a.getExchangeRequestReceived();
                                        if (a.finalCheck(offer.get(1))) {
                                            for (Agent b : agents) {
                                                if (b.agentID == offer.get(0)) {
                                                    if (b.finalCheck(offer.get(2))) {
                                                        b.completeRequestedExchange(offer, a.agentID);
                                                        a.completeReceivedExchange(offer);
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                        a.setExchangeRequestApproved();
                                    }
                                    // Clear the agents accepted offers list before the next exchange round.
                                    if (!a.getExchangeRequestReceived().isEmpty()) {
                                        a.setExchangeRequestReceived();
                                    }
                                }

                                for (Agent a : agents) {
                                    individualCSVWriter.append(String.valueOf(seed));
                                    individualCSVWriter.append(",");
                                    individualCSVWriter.append(String.valueOf(j));
                                    individualCSVWriter.append(",");
                                    individualCSVWriter.append(String.valueOf(k));
                                    individualCSVWriter.append(",");
                                    individualCSVWriter.append(String.valueOf(a.agentID));
                                    individualCSVWriter.append(",");
                                    individualCSVWriter.append(String.valueOf(a.getAgentType()));
                                    individualCSVWriter.append(",");
                                    individualCSVWriter.append(String.valueOf(a.calculateSatisfaction(null)));
                                    individualCSVWriter.append("\n");
                                }

                                // The average end of round satisfaction is stored for each Agent type if the current day exists in
                                // the daysOfInterest array. This data can later be averaged over simulation runs and added to
                                // the prePreparedIndividualFile.
                                int currentDay = j;
                                if (IntStream.of(daysOfInterest).anyMatch(val -> val == currentDay)) {
                                    for (int uniqueAgentType : uniqueAgentTypes) {
                                        double averageSatisfactionForType = averageAgentSatisfaction(uniqueAgentType);
                                        ArrayList<Double> endOfRoundAverageSatisfaction = new ArrayList<>();
                                        endOfRoundAverageSatisfaction.add((double) j);
                                        endOfRoundAverageSatisfaction.add((double) k);
                                        endOfRoundAverageSatisfaction.add((double) uniqueAgentType);
                                        endOfRoundAverageSatisfaction.add(averageSatisfactionForType);

                                        endOfRoundAverageSatisfactions.add(endOfRoundAverageSatisfaction);
                                    }
                                }
                            }
                        }

                        // The average end of day satisfaction is stored for each Agent type to later be averaged
                        // and added to the prePreparedAverageFile.
                        if (type == -2) {
                            endOfDayAverageSatisfactionsForType.get(j - 1).add(randomAllocations);
                        } else if (type == -1) {
                            endOfDayAverageSatisfactionsForType.get(j - 1).add(optimumAllocations);
                        } else {
                            double typeAverageSatisfaction = averageAgentSatisfaction(uniqueAgentTypes.get(type));
                            endOfDayAverageSatisfactionsForType.get(j - 1).add(typeAverageSatisfaction);
                        }

                        // Store the end of day average satisfaction for each agent type.
                        for (int uniqueAgentType : uniqueAgentTypes) {
                            double typeAverageSatisfaction = averageAgentSatisfaction(uniqueAgentType);
                            averageCSVWriter.append(",");
                            averageCSVWriter.append(String.valueOf(typeAverageSatisfaction));
                        }
                        averageCSVWriter.append("\n");

                        // The end of day satisfaction is stored for each Agent if the current day exists in
                        // the daysOfInterest array. This data can later be averaged over simulation runs and added to
                        // the prePreparedBoxPlotFile.
                        int currentDay = j;
                        if (IntStream.of(daysOfInterest).anyMatch(val -> val == currentDay)) {
                            for (Agent a : agents) {
                                ArrayList<Double> individualSatisfaction = new ArrayList<>();
                                individualSatisfaction.add((double) i);
                                individualSatisfaction.add((double) j);
                                individualSatisfaction.add((double) a.getAgentType());
                                individualSatisfaction.add(a.calculateSatisfaction(null));

                                endOfDayIndividualSatisfactions.add(individualSatisfaction);
                            }
                        }

                        // Only update the user on the simulations status every 10 days to reduce output spam.
                        if (j % 10 == 0) {
                            System.out.println("Agent Type: " + type + "  RUN: " + i + "  DAY: " + j);
                        }
                    }
                }


                for (int i = 0; i < DAYS; i++) {
                    double averageOverSims = endOfDayAverageSatisfactionsForType.get(i).stream().mapToDouble(val -> val).average().orElse(0.0);
                    endOfDayAverageSatisfactions.get(type + 2).add(averageOverSims);
                }

                // The average end of round satisfaction is stored for each Agent type for all rounds during the days in the
                // daysOfInterest array. These end of round averages are themselves averaged over all simulation runs before
                // being added to the prePreparedIndividualFile.
                if (type >= 0) {
                    for (int day : daysOfInterest) {
                        for (int i = 1; i <= EXCHANGES; i++) {
                            ArrayList<Double> allSimsEndOfRoundAverageSatisfaction = new ArrayList<>();
                            for (ArrayList<Double> endOfRoundAverageSatisfaction : endOfRoundAverageSatisfactions) {
                                if ((endOfRoundAverageSatisfaction.get(0) == (double) day) &&
                                        (endOfRoundAverageSatisfaction.get(1) == (double) i) &&
                                        (endOfRoundAverageSatisfaction.get(2) == (double) uniqueAgentTypes.get(type))) {
                                    allSimsEndOfRoundAverageSatisfaction.add(endOfRoundAverageSatisfaction.get(3));
                                }
                            }
                            double averageOverSims = allSimsEndOfRoundAverageSatisfaction.stream().mapToDouble(val -> val).average().orElse(0.0);

                            prePreparedIndividualCSVWriter.append(String.valueOf(day));
                            prePreparedIndividualCSVWriter.append(",");
                            prePreparedIndividualCSVWriter.append(String.valueOf(i));
                            prePreparedIndividualCSVWriter.append(",");
                            prePreparedIndividualCSVWriter.append(String.valueOf(uniqueAgentTypes.get(type)));
                            prePreparedIndividualCSVWriter.append(",");
                            prePreparedIndividualCSVWriter.append(String.valueOf(averageOverSims));
                            prePreparedIndividualCSVWriter.append("\n");
                        }
                    }
                }

                // The end of day satisfaction is stored for each Agents for all days in the daysOfInterest array. Each Agent's
                // satisfaction is averaged over all simulation runs, such that the least satisfied Agent one day is averaged
                // with the least satisfied Agent of each other day, the second least with the second least etc.
                for (int day : daysOfInterest) {
                    for (int agentType : uniqueAgentTypes) {
                        ArrayList<ArrayList<Double>> thisType = new ArrayList<>();
                        for (ArrayList<Double> endOfDayIndividualSatisfaction : endOfDayIndividualSatisfactions) {
                            if ((endOfDayIndividualSatisfaction.get(1) == (double) day) &&
                                    (endOfDayIndividualSatisfaction.get(2) == (double) agentType)) {
                                ArrayList<Double> thisAgent = new ArrayList<>();
                                thisAgent.add(endOfDayIndividualSatisfaction.get(0));
                                thisAgent.add(endOfDayIndividualSatisfaction.get(3));
                                thisType.add(thisAgent);
                            }
                        }
                        ArrayList<ArrayList<Double>> allSatisfactionLevels = new ArrayList<>();
                        int size = 0;
                        for (int i = 1; i <= SIMULATION_RUNS; i++) {
                            ArrayList<Double> satisfactionLevels = new ArrayList<>();
                            for (ArrayList<Double> thisAgent : thisType) {
                                if (thisAgent.get(0) == i) {
                                    satisfactionLevels.add(thisAgent.get(1));
                                }
                            }
                            Collections.sort(satisfactionLevels);
                            size = satisfactionLevels.size();
                            allSatisfactionLevels.add(satisfactionLevels);
                        }
                        for (int i = 0; i < size; i++) {
                            ArrayList<Double> averageForPosition = new ArrayList<>();
                            for (ArrayList<Double> satisfactionLevel : allSatisfactionLevels) {
                                averageForPosition.add(satisfactionLevel.get(i));
                            }
                            double averageOverSims = averageForPosition.stream().mapToDouble(val -> val).average().orElse(0.0);

                            prePreparedBoxPlotCSVWriter.append(String.valueOf(day));
                            prePreparedBoxPlotCSVWriter.append(",");
                            prePreparedBoxPlotCSVWriter.append(String.valueOf(agentType));
                            prePreparedBoxPlotCSVWriter.append(",");
                            prePreparedBoxPlotCSVWriter.append(String.valueOf(averageOverSims));
                            prePreparedBoxPlotCSVWriter.append("\n");
                        }
                    }
                }
            }
            // The end of day satisfactions for each agent type, as well as for random and optimum allocations,
            // are averaged over simulation runs and appended to the prePreparedAverageFile.
            int types = uniqueAgentTypes.size() + 2;
            for (int i = 1; i <= DAYS; i++) {
                prePreparedAverageCSVWriter.append(String.valueOf(i));
                for (int j = 0; j < types; j++) {
                    double averageOverSims =  endOfDayAverageSatisfactions.get(j).get(i - 1);
                    prePreparedAverageCSVWriter.append(",");
                    prePreparedAverageCSVWriter.append(String.valueOf(averageOverSims));
                }
                prePreparedAverageCSVWriter.append("\n");
            }
        }

        // Close the csv file writers once the simulation is complete.
        individualCSVWriter.close();
        averageCSVWriter.close();
        prePreparedAverageCSVWriter.close();
        prePreparedIndividualCSVWriter.close();
        prePreparedBoxPlotCSVWriter.close();
        prePreparedPopulationDistributionsCSVWriter.close();

        // Collect the required data and pass it to the Python data visualiser to produce graphs of the data.
        String pythonExe = "I:/code/REA_CondaEnvironment/python.exe";
        String pythonPath = "I:/code/ResourceExchangeArena/src/datahandler/DataVisualiser.py";
        String daysToAnalyse = Arrays.toString(daysOfInterest);

        List<String> pythonArgs = new ArrayList<>();

        String thirdGraph;
        if (VARIED_AGENT_TYPES) {
            thirdGraph = prePreparedPopulationDistributionsFile.getAbsolutePath();
        } else {
            thirdGraph = prePreparedBoxPlotFile.getAbsolutePath();
        }

        pythonArgs.add(pythonExe);
        pythonArgs.add(pythonPath);
        pythonArgs.add(RELEASE_VERSION);
        pythonArgs.add(initialSeed);
        pythonArgs.add(prePreparedAverageFile.getAbsolutePath());
        pythonArgs.add(prePreparedIndividualFile.getAbsolutePath());
        pythonArgs.add(thirdGraph);
        pythonArgs.add(Integer.toString(DAYS));
        pythonArgs.add(Integer.toString(EXCHANGES));
        pythonArgs.add(daysToAnalyse);

        ProcessBuilder builder = new ProcessBuilder(pythonArgs);

        // IO from the Python is shared with the same terminal as the Java code.
        builder.inheritIO();
        builder.redirectErrorStream(true);

        Process process = builder.start();
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gives a random initial time slot allocation to an Agent based on the number of time slots it requests and
     * the time slots that are currently available.
     *
     * @param requestedTimeSlots The time slots that the Agent has requested.
     * @return ArrayList<Integer> Returns a list of time slots to allocated to the Agent.
     */
    private static ArrayList<Integer> getRandomInitialAllocation(ArrayList<Integer> requestedTimeSlots) {                // TODO: CAN ASSIGN SAME SLOT TWICE TO AN AGENT, NEED TO USE PSEUDO RANDOM APPROACH WITH HASH MAPS, ASSIGNING MOST COMMONLY AVAILABLE SLOTS FIRST TO SOLVE THIS.
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
        return timeSlots;
    }

    /**
     * Takes all Agents individual satisfactions and calculates the average satisfaction of all Agents
     * in the simulation.
     *
     * @return Double Returns the average satisfaction between 0 and 1 of all agents in the simulation.
     */
    private static double averageAgentSatisfaction() {
        ArrayList<Double> agentSatisfactions = new ArrayList<>();
        for (Agent a : agents) {
            agentSatisfactions.add(a.calculateSatisfaction(null));
        }
        return agentSatisfactions.stream().mapToDouble(val -> val).average().orElse(0.0);
    }

    /**
     * Takes all Agents of a given types individual satisfactions and calculates the average satisfaction 
     * of the Agents of that type.
     *
     * @param agentType The type for which to calculate the average satisfaction of all Agents of that type.
     * @return Double Returns the average satisfaction between 0 and 1 of all agents of the given type.
     */
    private static double averageAgentSatisfaction(int agentType) {
        ArrayList<Double> agentSatisfactions = new ArrayList<>();
        for (Agent a : agents) {
            if (a.getAgentType() == agentType) {
                agentSatisfactions.add(a.calculateSatisfaction(null));
            }
        }
        return agentSatisfactions.stream().mapToDouble(val -> val).average().orElse(0.0);
    }

    /**
     * Returns the optimum average satisfaction possible for all agents given the current requests and allocations 
     * in the simulation.
     *
     * @return Double Returns the highest possible average satisfaction between 0 and 1 of all agents in the simulation.
     */
    private static double optimumAgentSatisfaction() {
        ArrayList<Integer> allRequestedSlots = new ArrayList<>();
        ArrayList<Integer> allAllocatedSlots = new ArrayList<>();

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
        return ((double)satisfiedSlots) / totalSlots;
    }

    /**
     * Takes an agentType and converts it from it's integer format to a descriptive string name to organise the data
     * output by the simulation.
     *
     * @return String Returns the given agentType as a descriptive string.
     */
    private static String getHumanReadableAgentType(int agentType) {
        String name;
        // Names match types specified by constant integers for the ExchangeArena.
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
