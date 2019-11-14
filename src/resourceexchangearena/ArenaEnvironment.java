package resourceexchangearena;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ArenaEnvironment extends ResourceExchangeArena{

    // Agent types that will be simulated.
    static int[] agentTypes = {SELFISH, SOCIAL};

    // Days that will have the Agents average satisfaction over the course of the day,
    // and satisfaction distribution at the end of the day visualised.
    static int[] daysOfInterest = {1, 25, 50};

    // Calculate the number of Agents of each type for when VARIED_AGENT_TYPES is true.
    static int numberOfEachAgentType = POPULATION_SIZE / agentTypes.length;

    static long seed;

    static ArrayList<Integer> uniqueAgentTypes = new ArrayList<>();

    static ArrayList<ArrayList<Double>> endOfDayAverageSatisfactions = new ArrayList<>();
    static ArrayList<ArrayList<Double>> endOfRoundAverageSatisfactions = new ArrayList<>();
    static ArrayList<ArrayList<ArrayList<Integer>>> endOfDayPopulationDistributions = new ArrayList<>();

    static ArrayList<ArrayList<Double>> endOfDayAverageSatisfactionsForType = new ArrayList<>();
    static ArrayList<ArrayList<Double>> endOfDayIndividualSatisfactions = new ArrayList<>();

    ArenaEnvironment() throws IOException {
        // Array of the unique agent types used in the simulation.
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
            fileName.append(Inflect.getHumanReadableAgentType(type));
        }
        fileName.append("_");
        fileName.append(initialSeed);

        // Stores the average satisfaction of each Agent type at the end of each day, as well
        // as the optimum average satisfaction and the satisfaction if allocations remained random.
        File averageFile = new File(
                rawDataOutputFolder,
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
            averageCSVWriter.append(Inflect.getHumanReadableAgentType(type));
        }
        averageCSVWriter.append("\n");

        // Stores the average satisfaction of each Agent type at the end of each day, as well
        // as the optimum average satisfaction and the satisfaction if allocations remained random.
        // Values are averaged over multiple simulation runs rather than stored separately.
        File prePreparedAverageFile = new File(
                prePreparedDataOutputFolder,
                "prePreparedEndOfDayAverages_" + fileName + ".csv");

        FileWriter prePreparedAverageCSVWriter = new FileWriter(prePreparedAverageFile);

        prePreparedAverageCSVWriter.append("Day");
        prePreparedAverageCSVWriter.append(",");
        prePreparedAverageCSVWriter.append("Random (No exchange)");
        prePreparedAverageCSVWriter.append(",");
        prePreparedAverageCSVWriter.append("Optimum (No exchange)");
        for (Integer type : uniqueAgentTypes) {
            prePreparedAverageCSVWriter.append(",");
            prePreparedAverageCSVWriter.append(Inflect.getHumanReadableAgentType(type));
        }
        if (VARIED_AGENT_TYPES) {
            for (Integer type : uniqueAgentTypes) {
                prePreparedAverageCSVWriter.append(",");
                prePreparedAverageCSVWriter.append(Inflect.getHumanReadableAgentType(type)).append(" Standard Deviation");
            }
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
            // Array lists used to temporarily store data before averaging and adding it to csv files.
            for (int i = 1; i <= DAYS; i++) {
                ArrayList<ArrayList<Integer>> endOfDayPopulationDistribution = new ArrayList<>();
                endOfDayPopulationDistributions.add(endOfDayPopulationDistribution);
            }
            for (ArrayList<ArrayList<Integer>> day : endOfDayPopulationDistributions) {
                for (int i = 1; i <= uniqueAgentTypes.size(); i++) {
                    ArrayList<Integer> populations = new ArrayList<>();
                    day.add(populations);
                }
            }

            for (int i = 1; i <= SIMULATION_RUNS; i++) {
                SimulationRun.simulateVariedAgents(averageCSVWriter, individualCSVWriter);
                System.out.println("RUN: " + i);
            }

            // The end of day satisfactions for each agent type, as well as for random and optimum allocations,
            // are averaged over simulation runs and appended to the prePreparedAverageFile.
            int types = (uniqueAgentTypes.size() * 2) + 2;
            for (int i = 1; i <= DAYS; i++) {
                prePreparedAverageCSVWriter.append(String.valueOf(i));
                for (int j = 1; j <= types; j++) {
                    ArrayList<Double> allSatisfactions = new ArrayList<>();
                    for (ArrayList<Double> endOfDayAverageSatisfaction : endOfDayAverageSatisfactions) {
                        if (endOfDayAverageSatisfaction.get(0) == (double) i) {
                            if (!Double.isNaN(endOfDayAverageSatisfaction.get(j))) {
                                allSatisfactions.add(endOfDayAverageSatisfaction.get(j));
                            }
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

                for (int i = 1; i <= DAYS; i++) {
                    ArrayList<Double> endOfDayAverageSatisfactionPerDay = new ArrayList<>();
                    endOfDayAverageSatisfactionsForType.add(endOfDayAverageSatisfactionPerDay);
                }

                for (int i = 1; i <= SIMULATION_RUNS; i++) {
                   SimulationRun.simulate(i, type, averageCSVWriter, individualCSVWriter);
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
                    double averageOverSims = endOfDayAverageSatisfactions.get(j).get(i - 1);
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

        VisualiserInitiator.visualise(daysOfInterest, initialSeed, prePreparedPopulationDistributionsFile, prePreparedBoxPlotFile, prePreparedAverageFile, prePreparedIndividualFile);
    }
}