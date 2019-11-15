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

    // Calculate the number of Agents of each type.
    static int numberOfEachAgentType = POPULATION_SIZE / agentTypes.length;

    static long seed;

    static ArrayList<Integer> uniqueAgentTypes = new ArrayList<>();

    static ArrayList<ArrayList<Double>> endOfDayAverageSatisfactions = new ArrayList<>();
    static ArrayList<ArrayList<Double>> endOfRoundAverageSatisfactions = new ArrayList<>();
    static ArrayList<ArrayList<ArrayList<Integer>>> endOfDayPopulationDistributions = new ArrayList<>();

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
        for (Integer type : uniqueAgentTypes) {
            prePreparedAverageCSVWriter.append(",");
            prePreparedAverageCSVWriter.append(Inflect.getHumanReadableAgentType(type)).append(" Standard Deviation");
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
            SimulationRun.simulate(i, averageCSVWriter, individualCSVWriter);
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

        // Close the csv file writers once the simulation is complete.
        individualCSVWriter.close();
        averageCSVWriter.close();
        prePreparedAverageCSVWriter.close();
        prePreparedIndividualCSVWriter.close();
        prePreparedPopulationDistributionsCSVWriter.close();

        VisualiserInitiator.visualise(daysOfInterest, initialSeed, prePreparedPopulationDistributionsFile, prePreparedAverageFile, prePreparedIndividualFile);
    }
}
