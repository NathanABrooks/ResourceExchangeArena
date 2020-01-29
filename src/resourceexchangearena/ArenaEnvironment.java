package resourceexchangearena;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ArenaEnvironment {

    // Data that is collected over simulation runs is held within the arenaEnvironment.
    ArrayList<ArrayList<Double>> endOfDaySatisfactions = new ArrayList<>();
    ArrayList<ArrayList<Double>> endOfDayAverageSatisfactions = new ArrayList<>();
    ArrayList<ArrayList<Double>> endOfRoundAverageSatisfactions = new ArrayList<>();
    ArrayList<ArrayList<ArrayList<Integer>>> endOfDayPopulationDistributions = new ArrayList<>();

    /**
     * The arena is the environment in which all simulations take place.
     *
     * @param releaseVersion String representing the current version of the simulation, used to organise output data.
     * @param daysOfInterest Integer array containing the days be shown in graphs produced after the simulation.
     * @param additionalData Boolean value that configures the simulation to output the state of each agent after each
     *                       exchange and at the end of each day.
     * @param simulationRuns Integer value representing the number of simulations to be ran and averaged.
     * @param days Integer value representing the number of days to be simulated.
     * @param exchanges Integer value representing the number of times all agents perform pairwise exchanges per day.
     * @param populationSize Integer value representing the size of the initial agent population.
     * @param maximumPeakConsumption Integer value representing how many agents can be allocated to each time slot.
     * @param uniqueTimeSlots Integer value representing the number of unique time slots available in the simulation.
     * @param slotsPerAgent Integer value representing the number of time slots each agent requires.
     * @param numberOfAgentsToEvolve Integer value representing the number of Agents who's strategy will change at the
     *                               end of each day.
     * @exception IOException On input error.
     * @see IOException
     */
    ArenaEnvironment(
            String releaseVersion,
            int[] daysOfInterest,
            boolean additionalData,
            int simulationRuns,
            int days,
            int exchanges,
            int populationSize,
            int maximumPeakConsumption,
            int uniqueTimeSlots,
            int slotsPerAgent,
            int numberOfAgentsToEvolve
    ) throws IOException {

        System.out.println("Starting simulation...");

        // The starting seed is copied to a string so that it can be tied to results for future replication.
        String initialSeed = Long.toString(ResourceExchangeArena.seed);

        // Agent types that will be simulated.
        int[] agentTypes = {ResourceExchangeArena.SELFISH, ResourceExchangeArena.SOCIAL};

        // Array of the unique agent types used in the simulation.
        ArrayList<Integer> uniqueAgentTypes = new ArrayList<>();
        for (int type : agentTypes) {
            if (!uniqueAgentTypes.contains(type)) {
                uniqueAgentTypes.add(type);
            }
        }

        // Sort the agent types so that they are ordered correctly in the output csv files.
        Collections.sort(uniqueAgentTypes);

        // Create a directory to store the data output by the simulation.
        String dataOutputFolder =
                "results/" + releaseVersion + "/" + initialSeed + "/data";
        Path dataOutputPath = Paths.get(dataOutputFolder);
        Files.createDirectories(dataOutputPath);

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
        // Values are averaged over multiple simulation runs rather than stored separately.
        File averageSatisfactionsFile = new File(
                dataOutputFolder,
                "endOfDayAverages_" + fileName + ".csv");

        FileWriter averageSatisfactionsCSVWriter = new FileWriter(averageSatisfactionsFile);

        averageSatisfactionsCSVWriter.append("Day");
        averageSatisfactionsCSVWriter.append(",");
        averageSatisfactionsCSVWriter.append("Random (No exchange)");
        averageSatisfactionsCSVWriter.append(",");
        averageSatisfactionsCSVWriter.append("Optimum (No exchange)");
        for (Integer type : uniqueAgentTypes) {
            averageSatisfactionsCSVWriter.append(",");
            averageSatisfactionsCSVWriter.append(Inflect.getHumanReadableAgentType(type));
        }
        for (Integer type : uniqueAgentTypes) {
            averageSatisfactionsCSVWriter.append(",");
            averageSatisfactionsCSVWriter.append(Inflect.getHumanReadableAgentType(type)).append(" Standard Deviation");
        }

        averageSatisfactionsCSVWriter.append("\n");

        // Stores the satisfaction of each individual Agent at the end of every round throughout the simulation.
        // Only stores data for days in the daysOfInterest array and averages data over multiple simulation runs.
        File individualsDataFile = new File(
                dataOutputFolder,
                "duringDayAverages_" + fileName + ".csv");

        FileWriter individualsDataCSVWriter = new FileWriter(individualsDataFile);

        individualsDataCSVWriter.append("Day");
        individualsDataCSVWriter.append(",");
        individualsDataCSVWriter.append("Round");
        individualsDataCSVWriter.append(",");
        individualsDataCSVWriter.append("Agent Type");
        individualsDataCSVWriter.append(",");
        individualsDataCSVWriter.append("Satisfaction");
        individualsDataCSVWriter.append("\n");

        // Shows how the population of each Agent type varies throughout the simulation, influenced by social learning.
        File populationDistributionsFile = new File(
                dataOutputFolder,
                "populationDistributions_" + fileName + ".csv");


        FileWriter populationDistributionsCSVWriter = new FileWriter(populationDistributionsFile);

        populationDistributionsCSVWriter.append("Day");
        populationDistributionsCSVWriter.append(",");
        populationDistributionsCSVWriter.append("Agent Type");
        populationDistributionsCSVWriter.append(",");
        populationDistributionsCSVWriter.append("Population");
        populationDistributionsCSVWriter.append("\n");

        // Shows how the population's satisfaction levels vary on days of interest..
        File endOfDaySatisfactionsFile = new File(
                dataOutputFolder,
                "endOfDaySatisfactions_" + fileName + ".csv");


        FileWriter individualSatisfactionsCSVWriter = new FileWriter(endOfDaySatisfactionsFile);

        individualSatisfactionsCSVWriter.append("Day");
        individualSatisfactionsCSVWriter.append(",");
        individualSatisfactionsCSVWriter.append("Agent Type");
        individualSatisfactionsCSVWriter.append(",");
        individualSatisfactionsCSVWriter.append("Satisfaction");
        individualSatisfactionsCSVWriter.append("\n");

        // Temporary file that is  deleted prior to graph generation. Used as a placeholder for when additional data
        // has not been requested.
        File tempFile = new File(
                dataOutputFolder,
                "temp" + ".csv");
        FileWriter additionalAverageSatisfactionsCSVWriter = new FileWriter(tempFile);
        FileWriter additionalIndividualsDataCSVWriter = new FileWriter(tempFile);

        if (additionalData) {
            // Create a directory to store the additional data output by the simulation.
            String additionalDataOutputFolder =
                    "results/" + releaseVersion + "/" + initialSeed + "/additionalData";
            Path additionalDataOutputPath = Paths.get(additionalDataOutputFolder);
            Files.createDirectories(additionalDataOutputPath);

            // Stores the average satisfaction of each agent type at the end of each day, as well
            // as the optimum average satisfaction and the satisfaction if allocations remained random.
            File additionalAverageSatisfactionsFile = new File(
                    additionalDataOutputFolder,
                    "endOfDayAverages_" + fileName + ".csv");

            additionalAverageSatisfactionsCSVWriter = new FileWriter(additionalAverageSatisfactionsFile);

            additionalAverageSatisfactionsCSVWriter.append("Simulation Run");
            additionalAverageSatisfactionsCSVWriter.append(",");
            additionalAverageSatisfactionsCSVWriter.append("Day");
            additionalAverageSatisfactionsCSVWriter.append(",");
            additionalAverageSatisfactionsCSVWriter.append("Random (No exchange)");
            additionalAverageSatisfactionsCSVWriter.append(",");
            additionalAverageSatisfactionsCSVWriter.append("Optimum (No exchange)");
            for (Integer type : uniqueAgentTypes) {
                additionalAverageSatisfactionsCSVWriter.append(",");
                additionalAverageSatisfactionsCSVWriter.append(Inflect.getHumanReadableAgentType(type));
            }
            additionalAverageSatisfactionsCSVWriter.append("\n");

            // Stores the satisfaction of each individual agent at the end of every round throughout the simulation.
            File additionalIndividualsDataFile = new File(
                    additionalDataOutputFolder,
                    "duringDayAverages_" + fileName + ".csv");

            additionalIndividualsDataCSVWriter = new FileWriter(additionalIndividualsDataFile);

            additionalIndividualsDataCSVWriter.append("Simulation Run");
            additionalIndividualsDataCSVWriter.append(",");
            additionalIndividualsDataCSVWriter.append("Day");
            additionalIndividualsDataCSVWriter.append(",");
            additionalIndividualsDataCSVWriter.append("Round");
            additionalIndividualsDataCSVWriter.append(",");
            additionalIndividualsDataCSVWriter.append("Agent ID");
            additionalIndividualsDataCSVWriter.append(",");
            additionalIndividualsDataCSVWriter.append("Agent Type");
            additionalIndividualsDataCSVWriter.append(",");
            additionalIndividualsDataCSVWriter.append("Satisfaction");
            additionalIndividualsDataCSVWriter.append("\n");
        }

        // Array lists used to temporarily store data before averaging and adding it to csv files.
        for (int day = 1; day <= days; day++) {
            ArrayList<ArrayList<Integer>> endOfDayPopulationDistribution = new ArrayList<>();
            endOfDayPopulationDistributions.add(endOfDayPopulationDistribution);
        }
        for (ArrayList<ArrayList<Integer>> day : endOfDayPopulationDistributions) {
            for (int agentType = 1; agentType <= uniqueAgentTypes.size(); agentType++) {
                ArrayList<Integer> populations = new ArrayList<>();
                day.add(populations);
            }
        }

        // Run as many simulations as has been requested.
        for (int simulationRun = 1; simulationRun <= simulationRuns; simulationRun++) {

            /*
             * Each Simulation run with the same parameters runs as an isolated instance although data is recorded in
             * a single location.
             *
             * @param daysOfInterest Integer array containing the days be shown in graphs produced after the simulation.
             * @param additionalData Boolean value that configures the simulation to output the state of each agent
             *                       after each exchange and at the end of each day.
             * @param days Integer value representing the number of days to be simulated.
             * @param exchanges Integer value representing the number of times all agents perform pairwise exchanges
             *                  per day.
             * @param populationSize Integer value representing the size of the initial agent population.
             * @param maximumPeakConsumption Integer value representing how many agents can be allocated to each time
             *                               slot.
             * @param uniqueTimeSlots Integer value representing the number of unique time slots available in the
             *                        simulation.
             * @param slotsPerAgent Integer value representing the number of time slots each agent requires.
             * @param numberOfAgentsToEvolve Integer value representing the number of Agents who's strategy will change
             *                               at the end of each day.
             * @param agentTypes Integer array containing the agent types that the simulation will begin with. The same
             *                   type can exist multiple times in the array where more agents of one type are required.
             * @param uniqueAgentTypes Integer ArrayList containing each unique agent type that exists when the
             *                         simulation begins.
             * @param endOfDaySatisfactions  Stores the satisfaction of each agent at the end of days of interest.
             * @param endOfRoundAverageSatisfactions Stores the average satisfaction for each agent type at the end of
             *                                       each round.
             * @param endOfDayAverageSatisfactions  Stores the average satisfaction for each agent type at the end of
             *                                      each day.
             * @param endOfDayPopulationDistributions Stores the population of each agent type at the end of each day.
             * @param averageCSVWriter Writes additional data on the average satisfaction of every agent at the end of
             *                         each day when additional data is requested.
             * @param individualCSVWriter Writes additional data on the individual agents satisfaction after each
             *                            exchange when  additional data is requested.
             * @exception IOException On input error.
             * @see IOException
             */
            new SimulationRun(
                    daysOfInterest,
                    additionalData,
                    days,
                    exchanges,
                    populationSize,
                    maximumPeakConsumption,
                    uniqueTimeSlots,
                    slotsPerAgent,
                    numberOfAgentsToEvolve,
                    agentTypes,
                    uniqueAgentTypes,
                    endOfDaySatisfactions,
                    endOfRoundAverageSatisfactions,
                    endOfDayAverageSatisfactions,
                    endOfDayPopulationDistributions,
                    additionalAverageSatisfactionsCSVWriter,
                    additionalIndividualsDataCSVWriter
            );
            System.out.println("RUNS COMPLETED: " + simulationRun);
        }

        // The end of day satisfactions for each agent type, as well as for random and optimum allocations,
        // are averaged over simulation runs and appended to the averageSatisfactionsFile.
        int types = (uniqueAgentTypes.size() * 2) + 2;
        for (int day = 1; day <= days; day++) {
            averageSatisfactionsCSVWriter.append(String.valueOf(day));
            for (int agentType = 1; agentType <= types; agentType++) {
                ArrayList<Double> allSatisfactions = new ArrayList<>();
                for (ArrayList<Double> endOfDayAverageSatisfaction : endOfDayAverageSatisfactions) {
                    if (endOfDayAverageSatisfaction.get(0) == (double) day) {
                        if (!Double.isNaN(endOfDayAverageSatisfaction.get(agentType))) {
                            allSatisfactions.add(endOfDayAverageSatisfaction.get(agentType));
                        }
                    }
                }
                double averageOverSims = allSatisfactions.stream().mapToDouble(val -> val).average().orElse(0.0);
                averageSatisfactionsCSVWriter.append(",");
                averageSatisfactionsCSVWriter.append(String.valueOf(averageOverSims));
            }
            averageSatisfactionsCSVWriter.append("\n");
        }

        // The average end of round satisfaction is stored for each Agent type for all rounds during the days in the
        // daysOfInterest array. These end of round averages are themselves averaged over all simulation runs before
        // being added to the individualsDataFile.
        for (int day : daysOfInterest) {
            for (int exchange = 1; exchange <= exchanges; exchange++) {
                for (int agentType : uniqueAgentTypes) {
                    ArrayList<Double> allSimsEndOfRoundAverageSatisfaction = new ArrayList<>();
                    for (ArrayList<Double> endOfRoundAverageSatisfaction : endOfRoundAverageSatisfactions) {
                        if ((endOfRoundAverageSatisfaction.get(0) == (double) day) &&
                                (endOfRoundAverageSatisfaction.get(1) == (double) exchange) &&
                                (endOfRoundAverageSatisfaction.get(2) == (double) agentType)) {
                            allSimsEndOfRoundAverageSatisfaction.add(endOfRoundAverageSatisfaction.get(3));
                        }
                    }
                    double averageOverSims = allSimsEndOfRoundAverageSatisfaction.stream()
                            .mapToDouble(val -> val).average().orElse(0.0);

                    individualsDataCSVWriter.append(String.valueOf(day));
                    individualsDataCSVWriter.append(",");
                    individualsDataCSVWriter.append(String.valueOf(exchange));
                    individualsDataCSVWriter.append(",");
                    individualsDataCSVWriter.append(String.valueOf(agentType));
                    individualsDataCSVWriter.append(",");
                    individualsDataCSVWriter.append(String.valueOf(averageOverSims));
                    individualsDataCSVWriter.append("\n");
                }
            }
        }

        // Individual satisfaction levels for days of interest are sorted into a csv file so that they can be added to
        // violin plots.
        for (int day : daysOfInterest) {
            for (int agentType : uniqueAgentTypes) {
                for (ArrayList<Double> endOfDaySatisfaction : endOfDaySatisfactions) {
                    if ((endOfDaySatisfaction.get(0) == (double) day) &&
                            (endOfDaySatisfaction.get(1) == (double) agentType)) {
                        individualSatisfactionsCSVWriter.append(String.valueOf(day));
                        individualSatisfactionsCSVWriter.append(",");
                        individualSatisfactionsCSVWriter.append(String.valueOf(agentType));
                        individualSatisfactionsCSVWriter.append(",");
                        individualSatisfactionsCSVWriter.append(String.valueOf(endOfDaySatisfaction.get(2)));
                        individualSatisfactionsCSVWriter.append("\n");
                    }
                }
            }
        }

        // The population distributions for the simulations are averaged before being added to the population
        // distributions csv file.
        for (int day = 1; day <= days; day++) {
            ArrayList<ArrayList<Integer>> daysPopulations = endOfDayPopulationDistributions.get(day - 1);
            for (int agentType = 0; agentType < uniqueAgentTypes.size(); agentType++) {
                populationDistributionsCSVWriter.append(String.valueOf(day));
                populationDistributionsCSVWriter.append(",");
                populationDistributionsCSVWriter.append(String.valueOf(uniqueAgentTypes.get(agentType)));
                populationDistributionsCSVWriter.append(",");

                ArrayList<Integer> allPopulations = daysPopulations.get(agentType);
                int sumOfPopulations = 0;
                for (Integer population : allPopulations) {
                    sumOfPopulations = sumOfPopulations + population;
                }
                double averagePopulation = (double) sumOfPopulations / simulationRuns;

                populationDistributionsCSVWriter.append(String.valueOf(averagePopulation));
                populationDistributionsCSVWriter.append("\n");
            }
        }

        // Close the csv file writers once the simulation is complete.
        averageSatisfactionsCSVWriter.close();
        individualSatisfactionsCSVWriter.close();
        individualsDataCSVWriter.close();
        populationDistributionsCSVWriter.close();
        additionalAverageSatisfactionsCSVWriter.close();
        additionalIndividualsDataCSVWriter.close();

        // Delete the temporary file.
        if (!tempFile.delete()) {
            System.out.println("Issues with temporary file");
        }

        // Stores the key data about the finished simulation.
        File simulationData = new File(
                "results/" + releaseVersion + "/" + initialSeed,
                "simulationData_" + fileName + ".txt");

        FileWriter simulationDataWriter = new FileWriter(simulationData);

        simulationDataWriter.append("Simulation Information: \n\n");
        simulationDataWriter.append("Release version: ").append(releaseVersion).append("\n");
        simulationDataWriter.append("Days of interest: ").append(Arrays.toString(daysOfInterest)).append("\n");
        simulationDataWriter.append("Additional data: ").append(String.valueOf(additionalData)).append("\n");
        simulationDataWriter.append("Simulation runs: ").append(String.valueOf(simulationRuns)).append("\n");
        simulationDataWriter.append("Days: ").append(String.valueOf(days)).append("\n");
        simulationDataWriter.append("Exchanges: ").append(String.valueOf(exchanges)).append("\n");
        simulationDataWriter.append("Population size: ").append(String.valueOf(populationSize)).append("\n");
        simulationDataWriter.append("Maximum peak consumption: ").append(String.valueOf(maximumPeakConsumption))
                .append("\n");
        simulationDataWriter.append("Unique time slots: ").append(String.valueOf(uniqueTimeSlots)).append("\n");
        simulationDataWriter.append("Slots per agent: ").append(String.valueOf(slotsPerAgent)).append("\n");
        simulationDataWriter.append("Number of agents to evolve: ").append(String.valueOf(numberOfAgentsToEvolve));

        simulationDataWriter.close();

        /*
         * The arena is the environment in which all simulations take place.
         *
         * @param releaseVersion String representing the current version of the simulation, used to organise output
         *                       data.
         * @param initialSeed String representing the seed of the first simulation run included in the results, this
         *                    string added to the results file names so that they can be easily replicated.
         * @param daysOfInterest Integer array containing the days be shown in graphs produced after the simulation.
         * @param days Integer value representing the number of days to be simulated.
         * @param exchanges Integer value representing the number of times all agents perform pairwise exchanges per
         *                  day.
         * @param endOfDaySatisfactionsFile  Stores the satisfaction of each agent at the end of days of interest.
         * @param averageSatisfactionsFile Stores the average satisfaction of each Agent type at the end of each day,
         *                                 as well as the optimum average satisfaction and the satisfaction if
         *                                 allocations remained random.
         * @param individualsDataFile Stores the satisfaction of each individual Agent at the end of every round
         *                            throughout the simulation.
         * @param populationDistributionsFile Shows how the population of each Agent type varies throughout the
         *                                    simulation, influenced by social learning.
         * @exception IOException On input error.
         * @see IOException
         */
        new VisualiserInitiator(
                releaseVersion,
                initialSeed,
                daysOfInterest,
                days,
                exchanges,
                endOfDaySatisfactionsFile,
                averageSatisfactionsFile,
                individualsDataFile,
                populationDistributionsFile
        );
    }
}
