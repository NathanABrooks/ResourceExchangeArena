package resource_exchange_arena;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ArenaEnvironment {
    // Data that is collected over simulation runs is held within the arenaEnvironment.
    ArrayList<ArrayList<Double>> endOfDaySatisfactions = new ArrayList<>();
    ArrayList<ArrayList<Double>> endOfDayAverageSatisfactions = new ArrayList<>();
    ArrayList<ArrayList<Double>> endOfRoundAverageSatisfactions = new ArrayList<>();
    ArrayList<ArrayList<ArrayList<Integer>>> endOfDayPopulationDistributions = new ArrayList<>();

    ArrayList<ArrayList<Integer>>  socialCapitalTracking = new ArrayList<>();
    ArrayList<ArrayList<Integer>>  exchangeTypeTracking = new ArrayList<>();
    ArrayList<ArrayList<Integer>>  exchangeSuccessTracking = new ArrayList<>();

    /**
     * The arena is the environment in which all simulations take place.
     *
     * @param folderName String representing the output destination folder, used to organise output data.
     * @param environmentTag String detailing specifics about the simulation environment.
     * @param daysOfInterest Integer array containing the days be shown in graphs produced after the simulation.
     * @param demandCurves Double arrays of demand used by the agents, when multiple curves are used the agents are
     *                     split equally between the curves.
     * @param availabilityCurve Integer array of energy availability used by the simulation.
     * @param socialCapital Boolean value that determines whether or not social agents will utilise social capital.
     * @param simulationRuns Integer value representing the number of simulations to be ran and averaged.
     * @param days Integer value representing the number of days to be simulated.
     * @param exchanges Integer value representing the number of times all agents perform pairwise exchanges per day.
     * @param populationSize Integer value representing the size of the initial agent population.
     * @param uniqueTimeSlots Integer value representing the number of unique time slots available in the simulation.
     * @param slotsPerAgent Integer value representing the number of time slots each agent requires.
     * @param numberOfAgentsToEvolve Integer value representing the number of Agents who's strategy will change at the
     *                               end of each day.
     * @param agentTypes Integer array containing the agent types that the simulation will begin with. The same type
     *                   can exist multiple times in the array where more agents of one type are required.
     * @param singleAgentType Boolean value specifying whether only a single agent type should exist, used for
     *                        establishing baseline results.
     * @param selectedSingleAgentType Integer value representing the single agent type to be modelled when
     *                                singleAgentType is true.
     * @param comparingExchangesCSVWriter FileWriter used to add data to summaryGraphs file.
     * @param comparingPopulationDistributionsCSVWriter FileWriter used to add data to population distributions summary
     *                                                  file.
     * @param pythonExe String representing the system path to python environment executable.
     * @param pythonPath String representing the system path to the python data visualiser.
     * @exception IOException On input error.
     * @see IOException
     */
    ArenaEnvironment(
            String folderName,
            String environmentTag,
            int[] daysOfInterest,
            double[][] demandCurves,
            int[] availabilityCurve,
            boolean socialCapital,
            int simulationRuns,
            int days,
            int exchanges,
            int populationSize,
            int uniqueTimeSlots,
            int slotsPerAgent,
            int numberOfAgentsToEvolve,
            int[] agentTypes,
            boolean singleAgentType,
            int selectedSingleAgentType,
            FileWriter comparingExchangesCSVWriter,
            FileWriter comparingPopulationDistributionsCSVWriter,
            String pythonExe,
            String pythonPath
    ) throws IOException {

        System.out.println("Starting simulation...");

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
        String dataOutputFolder = folderName + "/" + environmentTag + "/data";
        Path dataOutputPath = Path.of(dataOutputFolder);
        Files.createDirectories(dataOutputPath);



        // Stores the amount of unspent social capital each agent has accumulated.
        File allDailyData = new File(dataOutputFolder, "allDailyData.csv");

        FileWriter allDailyDataCSVWriter = new FileWriter(allDailyData);
        
        allDailyDataCSVWriter.append("Simulation Run");
        allDailyDataCSVWriter.append(",");
        allDailyDataCSVWriter.append("Day");
        allDailyDataCSVWriter.append(",");
        allDailyDataCSVWriter.append("Social Pop");
        allDailyDataCSVWriter.append(",");
        allDailyDataCSVWriter.append("Selfish Pop");
        allDailyDataCSVWriter.append(",");
        allDailyDataCSVWriter.append("Social Sat");
        allDailyDataCSVWriter.append(",");
        allDailyDataCSVWriter.append("Selfish Sat");
        allDailyDataCSVWriter.append(",");
        allDailyDataCSVWriter.append("Social SD");
        allDailyDataCSVWriter.append(",");
        allDailyDataCSVWriter.append("Selfish SD");
        allDailyDataCSVWriter.append(",");
        allDailyDataCSVWriter.append("Social Upper Quartile");
        allDailyDataCSVWriter.append(",");
        allDailyDataCSVWriter.append("Selfish Upper Quartile");
        allDailyDataCSVWriter.append(",");
        allDailyDataCSVWriter.append("Social Lower Quartile");
        allDailyDataCSVWriter.append(",");
        allDailyDataCSVWriter.append("Selfish Lower Quartile");
        allDailyDataCSVWriter.append(",");
        allDailyDataCSVWriter.append("Social 95th Percentile");
        allDailyDataCSVWriter.append(",");
        allDailyDataCSVWriter.append("Selfish 95th Percentile");
        allDailyDataCSVWriter.append(",");
        allDailyDataCSVWriter.append("Social Max");
        allDailyDataCSVWriter.append(",");
        allDailyDataCSVWriter.append("Selfish Max");
        allDailyDataCSVWriter.append(",");
        allDailyDataCSVWriter.append("Social Min");
        allDailyDataCSVWriter.append(",");
        allDailyDataCSVWriter.append("Selfish Min");
        allDailyDataCSVWriter.append(",");
        allDailyDataCSVWriter.append("Social Median");
        allDailyDataCSVWriter.append(",");
        allDailyDataCSVWriter.append("Selfish Median");
        allDailyDataCSVWriter.append("\n");

        // Stores the amount of unspent social capital each agent has accumulated.
        File socialCapitalData = new File(dataOutputFolder, "socialCapitalTracking.csv");

        FileWriter socialCapitalDataCSVWriter = new FileWriter(socialCapitalData);
        
        socialCapitalDataCSVWriter.append("Day");
        socialCapitalDataCSVWriter.append(",");
        socialCapitalDataCSVWriter.append("Agent Type");
        socialCapitalDataCSVWriter.append(",");
        socialCapitalDataCSVWriter.append("Unspent Social Capital");
        socialCapitalDataCSVWriter.append("\n");

        // Stores the amount of exchanges that used social capital and those that did not.
        File exchangeTypeData = new File(dataOutputFolder, "exchangeTypeTracking.csv");

        FileWriter exchangeTypeDataCSVWriter = new FileWriter(exchangeTypeData);

        exchangeTypeDataCSVWriter.append("Day");
        exchangeTypeDataCSVWriter.append(",");
        exchangeTypeDataCSVWriter.append("Agent Type");
        exchangeTypeDataCSVWriter.append(",");
        exchangeTypeDataCSVWriter.append("Social Capital Exchanges");
        exchangeTypeDataCSVWriter.append(",");
        exchangeTypeDataCSVWriter.append("No Social Capital Exchanges");
        exchangeTypeDataCSVWriter.append("\n");

        // Stores the amount of exchange requests that were considered and accepted and considered and rejected.
        File exchangeSuccessData = new File(dataOutputFolder, "exchangeSuccessTracking.csv");

        FileWriter exchangeSuccessDataCSVWriter = new FileWriter(exchangeSuccessData);

        exchangeSuccessDataCSVWriter.append("Day");
        exchangeSuccessDataCSVWriter.append(",");
        exchangeSuccessDataCSVWriter.append("Agent Type");
        exchangeSuccessDataCSVWriter.append(",");
        exchangeSuccessDataCSVWriter.append("Rejected Received Exchanges");
        exchangeSuccessDataCSVWriter.append(",");
        exchangeSuccessDataCSVWriter.append("Accepted Received Exchanges");
        exchangeSuccessDataCSVWriter.append(",");
        exchangeSuccessDataCSVWriter.append("Rejected Requested Exchanges");
        exchangeSuccessDataCSVWriter.append(",");
        exchangeSuccessDataCSVWriter.append("Accepted Requested Exchanges");
        exchangeSuccessDataCSVWriter.append("\n");

        // Stores a summary of various tracked stats.
        File trackingSummaryData = new File(dataOutputFolder, "trackingSummary.txt");

        FileWriter statsSummaryDataWriter = new FileWriter(trackingSummaryData);
        

        // Stores the average satisfaction of each Agent type at the end of each day, as well as the optimum average
        // satisfaction and the satisfaction if allocations remained random. Values are averaged over multiple
        // simulation runs rather than stored separately.
        File averageSatisfactionsFile = new File(dataOutputFolder,"endOfDayAverages.csv");

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
        File individualsDataFile = new File(dataOutputFolder,"duringDayAverages.csv");

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
        File populationDistributionsFile = new File(dataOutputFolder,"populationDistributions.csv");

        FileWriter populationDistributionsCSVWriter = new FileWriter(populationDistributionsFile);

        populationDistributionsCSVWriter.append("Day");
        populationDistributionsCSVWriter.append(",");
        populationDistributionsCSVWriter.append("Agent Type");
        populationDistributionsCSVWriter.append(",");
        populationDistributionsCSVWriter.append("Population");
        populationDistributionsCSVWriter.append("\n");

        // Shows how the population's satisfaction levels vary on days of interest..
        File endOfDaySatisfactionsFile = new File(dataOutputFolder,"endOfDaySatisfactions.csv");

        FileWriter individualSatisfactionsCSVWriter = new FileWriter(endOfDaySatisfactionsFile);

        individualSatisfactionsCSVWriter.append("Day");
        individualSatisfactionsCSVWriter.append(",");
        individualSatisfactionsCSVWriter.append("Agent Type");
        individualSatisfactionsCSVWriter.append(",");
        individualSatisfactionsCSVWriter.append("Satisfaction");
        individualSatisfactionsCSVWriter.append("\n");

        // Stores the key data about the simulation about to begin in the data output location.
        File simulationData = new File(folderName + "/" + environmentTag,"simulationData.txt");

        FileWriter simulationDataWriter = new FileWriter(simulationData);

        simulationDataWriter.append("Simulation Information: \n\n");
        simulationDataWriter.append("Seed: ").append(String.valueOf(ResourceExchangeArena.seed)).append("\n");
        simulationDataWriter.append("Single agent type: ").append(String.valueOf(singleAgentType)).append("\n");
        if (singleAgentType) {
            simulationDataWriter.append("Agent type: ")
                    .append(String.valueOf(selectedSingleAgentType)).append("\n");
        }
        simulationDataWriter.append("Use social capital: ").append(String.valueOf(socialCapital)).append("\n");
        simulationDataWriter.append("Simulation runs: ").append(String.valueOf(simulationRuns)).append("\n");
        simulationDataWriter.append("Days: ").append(String.valueOf(days)).append("\n");
        simulationDataWriter.append("Days of interest: ").append(Arrays.toString(daysOfInterest)).append("\n");
        simulationDataWriter.append("Population size: ").append(String.valueOf(populationSize)).append("\n");
        simulationDataWriter.append("Unique time slots: ").append(String.valueOf(uniqueTimeSlots)).append("\n");
        simulationDataWriter.append("Slots per agent: ").append(String.valueOf(slotsPerAgent)).append("\n");
        simulationDataWriter.append("Exchanges: ").append(String.valueOf(exchanges)).append("\n");
        simulationDataWriter.append("Number of agents to evolve: ").append(String.valueOf(numberOfAgentsToEvolve))
                .append("\n");
        simulationDataWriter.append("Starting ratio of agent types: ");
        int typesListed = 0;
        for (int type : agentTypes) {
            if(typesListed != 0){
                simulationDataWriter.append(" : ");
            }
            typesListed++;
            simulationDataWriter.append(Inflect.getHumanReadableAgentType(type));
        }
        simulationDataWriter.close();

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

        // The demand curves are bucketed before the simulations for efficiency, as they will all use the same bucketed values.
        double[][] bucketedDemandCurves = new double[demandCurves.length][uniqueTimeSlots];
        double[] totalDemandValues = new double[demandCurves.length];

        for (int i = 0; i < demandCurves.length; i++) {
            double[] bucketedDemandCurve = new double[uniqueTimeSlots];
            int bucket = 0;
            int bucketFill = 0;
            for (int j = 0; j < demandCurves[i].length; j++) {
                bucketedDemandCurve[bucket] = bucketedDemandCurve[bucket] + demandCurves[i][j];
                bucketFill++;
                if (bucketFill == 6) {
                    // Rounding to fix precision errors.
                    bucketedDemandCurve[bucket] = Math.round(bucketedDemandCurve[bucket] * 10.0) / 10.0;
                    bucketFill = 0;
                    bucket++;
                }
            }
            bucketedDemandCurves[i] = bucketedDemandCurve;
    
            // The total demand is also calculated here for efficiency.
            double totalDemand = 0;
            for (int j = 0; j < bucketedDemandCurve.length; j++) {
                totalDemand = totalDemand + bucketedDemandCurve[j];
            }
            totalDemand = Math.round(totalDemand * 10.0) / 10.0;
            totalDemandValues[i] = totalDemand;

        }

        // The availability curve is bucketed before the simulations for efficiency, as they will all use the same bucketed values.
        int[] bucketedAvailabilityCurve = new int[uniqueTimeSlots];
        int totalAvailability = 0;
        
        int bucket = 0;
        int bucketFill = 0;
        int bucketValue = 0;
        for (int i = 0; i < availabilityCurve.length; i++) {
            totalAvailability += availabilityCurve[i];
            bucketValue += availabilityCurve[i];
            bucketFill++;

            if (bucketFill == 2) {
                bucketedAvailabilityCurve[bucket] = bucketValue;
                bucket++;
                bucketValue = 0;
                bucketFill = 0;
            }
        }


        ArrayList<ArrayList<Integer>>  socialCapitalTracking = new ArrayList<>();
        ArrayList<ArrayList<Integer>>  exchangeTypeTracking = new ArrayList<>();
        ArrayList<ArrayList<Integer>>  exchangeSuccessTracking = new ArrayList<>();
        // Run as many simulations as has been requested.
        for (int simulationRun = 1; simulationRun <= simulationRuns; simulationRun++) {
            /*
             * Each Simulation run with the same parameters runs as an isolated instance although data is recorded in
             * a single location.
             *
             * @param daysOfInterest Integer array containing the days be shown in graphs produced after the simulation.
             * @param demandCurves Double arrays of demand used by the agents, when multiple curves are used the agents
             *                    are split equally between the curves.
             * @param totalDemandValues Double values represeneting the sum of all values in their associated demand curves.
             * @param availabilityCurve Integer array representing the amount of energy available at each timeslot.
             * @param totalAvailability Integer value representing the total energy available throughout the day.
             * @param days Integer value representing the number of days to be simulated.
             * @param exchanges Integer value representing the number of times all agents perform pairwise exchanges
             *                  per day.
             * @param populationSize Integer value representing the size of the initial agent population.
             * @param uniqueTimeSlots Integer value representing the number of unique time slots available in the
             *                        simulation.
             * @param slotsPerAgent Integer value representing the number of time slots each agent requires.
             * @param numberOfAgentsToEvolve Integer value representing the number of Agents who's strategy will change
             *                               at the end of each day.
             * @param agentTypes Integer array containing the agent types that the simulation will begin with. The same
             *                   type can exist multiple times in the array where more agents of one type are required.
             * @param uniqueAgentTypes Integer ArrayList containing each unique agent type that exists when the
             *                         simulation begins.
             * @param singleAgentType Boolean value specifying whether only a single agent type should exist, used for
             *                        establishing baseline results.
             * @param selectedSingleAgentType Integer value representing the single agent type to be modelled when
             *                                singleAgentType is true.
             * @param socialCapital Boolean value that determines whether or not social agents will utilise
             *                      social capital.
             * @param socialCapitalTracking Stores the amount of social capital per agent for each agent type.
             * @param exchangeTypeTracking Stores how many exchanges used social capital and how many did not.
             * @param exchangeSuccessTracking Stores how many potential exchanges were accepted.
             * @param endOfDaySatisfactions Stores the satisfaction of each agent at the end of days of interest.
             * @param endOfRoundAverageSatisfactions Stores the average satisfaction for each agent type at the end of
             *                                       each round.
             * @param endOfDayAverageSatisfactions Stores the average satisfaction for each agent type at the end of
             *                                      each day.
             * @param endOfDayPopulationDistributions Stores the population of each agent type at the end of each day.
             * @exception IOException On input error.
             * @see IOException
             */
            new SimulationRun(
                    daysOfInterest,
                    bucketedDemandCurves,
                    totalDemandValues,
                    bucketedAvailabilityCurve,
                    totalAvailability,
                    days,
                    exchanges,
                    populationSize,
                    uniqueTimeSlots,
                    slotsPerAgent,
                    numberOfAgentsToEvolve,
                    agentTypes,
                    uniqueAgentTypes,
                    singleAgentType,
                    selectedSingleAgentType,
                    socialCapital,
                    socialCapitalTracking,
                    exchangeTypeTracking,
                    exchangeSuccessTracking,
                    endOfDaySatisfactions,
                    endOfRoundAverageSatisfactions,
                    endOfDayAverageSatisfactions,
                    endOfDayPopulationDistributions,
                    allDailyDataCSVWriter,
                    simulationRun
            );
            System.out.println("RUNS COMPLETED: " + simulationRun);
        }

        // The end of day satisfactions for each agent type, as well as for random and optimum allocations,
        // are averaged over simulation runs and appended to the averageSatisfactionsFile.
        int types = (uniqueAgentTypes.size() * 2) + 2;
        for (int day = 1; day <= days; day++) {
            averageSatisfactionsCSVWriter.append(String.valueOf(day));

            for(int element: daysOfInterest) {
                if (day == element) {
                    comparingExchangesCSVWriter.append(String.valueOf(exchanges));
                    comparingExchangesCSVWriter.append(",");
                    comparingExchangesCSVWriter.append(String.valueOf(day));
                }
            }

            for (int agentType = 1; agentType <= types; agentType++) {
                ArrayList<Double> allSatisfactions = new ArrayList<>();
                for (ArrayList<Double> endOfDayAverageSatisfaction : endOfDayAverageSatisfactions) {
                    if (endOfDayAverageSatisfaction.get(0) == (double) day) {
                        allSatisfactions.add(endOfDayAverageSatisfaction.get(agentType));
                    }
                }
                double averageOverSims = allSatisfactions.stream().mapToDouble(val -> val).average().orElse(0.0);
                averageSatisfactionsCSVWriter.append(",");
                averageSatisfactionsCSVWriter.append(String.valueOf(averageOverSims));

                // Data saved for comparing exchanges graph, except for random and optimum allocations.
                if(agentType > 2) {
                    for(int element: daysOfInterest) {
                        if (day == element) {
                            comparingExchangesCSVWriter.append(",");
                            comparingExchangesCSVWriter.append(String.valueOf(averageOverSims));
                        }
                    }
                }
            }
            averageSatisfactionsCSVWriter.append("\n");
            for(int element: daysOfInterest) {
                if (day == element) {
                    comparingExchangesCSVWriter.append("\n");
                }
            }
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
        // Data saved to track unspent social capital, the types of exchanges that occur and rejected exchanges is
        // also handled here.
        for (int day : daysOfInterest) {
            statsSummaryDataWriter.append("Day: " + (String.valueOf(day)) + "\n");
            for (int agentType : uniqueAgentTypes) {
                statsSummaryDataWriter.append("Agent Type: " + Inflect.getHumanReadableAgentType(agentType) + "\n");

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

                ArrayList<Integer> unspentSocialCapital = new ArrayList<>();
                for (ArrayList<Integer> sct : socialCapitalTracking) {
                    if ((sct.get(0) == day) && (sct.get(1) == agentType)) {
                        socialCapitalDataCSVWriter.append(String.valueOf(day));
                        socialCapitalDataCSVWriter.append(",");
                        socialCapitalDataCSVWriter.append(String.valueOf(agentType));
                        socialCapitalDataCSVWriter.append(",");
                        socialCapitalDataCSVWriter.append(String.valueOf(sct.get(2)));
                        socialCapitalDataCSVWriter.append("\n");
                        unspentSocialCapital.add(sct.get(2));
                    }
                }
                double usc = unspentSocialCapital.stream().mapToDouble(val -> val).average().orElse(0.0);
                statsSummaryDataWriter.append("Unspent Social Capital: " + (String.valueOf(usc))+ "\n");

                ArrayList<Integer> socialCapitalExchanges = new ArrayList<>();
                ArrayList<Integer> noSocialCapitalExchanges = new ArrayList<>();
                for (ArrayList<Integer> ett : exchangeTypeTracking) {
                    if ((ett.get(0) == day) && (ett.get(1) == agentType)) {
                        exchangeTypeDataCSVWriter.append(String.valueOf(day));
                        exchangeTypeDataCSVWriter.append(",");
                        exchangeTypeDataCSVWriter.append(String.valueOf(agentType));
                        exchangeTypeDataCSVWriter.append(",");
                        exchangeTypeDataCSVWriter.append(String.valueOf(ett.get(2)));
                        exchangeTypeDataCSVWriter.append(",");
                        exchangeTypeDataCSVWriter.append(String.valueOf(ett.get(3)));
                        exchangeTypeDataCSVWriter.append("\n");
                        socialCapitalExchanges.add(ett.get(2));
                        noSocialCapitalExchanges.add(ett.get(3));
                    }
                }
                double sce = socialCapitalExchanges.stream().mapToDouble(val -> val).average().orElse(0.0);
                double nsce = noSocialCapitalExchanges.stream().mapToDouble(val -> val).average().orElse(0.0);
                statsSummaryDataWriter.append("Social Capital Exchanges: " + (String.valueOf(sce))+ "\n");
                statsSummaryDataWriter.append("No Social Capital Exchanges: " + (String.valueOf(nsce))+ "\n");

                ArrayList<Integer> rejectedReceivedExchanges = new ArrayList<>();
                ArrayList<Integer> acceptedReceivedExchanges = new ArrayList<>();
                ArrayList<Integer> rejectedRequestedExchanges = new ArrayList<>();
                ArrayList<Integer> acceptedRequestedExchanges = new ArrayList<>();
                for (ArrayList<Integer> est : exchangeSuccessTracking) {
                    if ((est.get(0) == day) && (est.get(1) == agentType)) {
                        exchangeSuccessDataCSVWriter.append(String.valueOf(day));
                        exchangeSuccessDataCSVWriter.append(",");
                        exchangeSuccessDataCSVWriter.append(String.valueOf(agentType));
                        exchangeSuccessDataCSVWriter.append(",");
                        exchangeSuccessDataCSVWriter.append(String.valueOf(est.get(2)));
                        exchangeSuccessDataCSVWriter.append(",");
                        exchangeSuccessDataCSVWriter.append(String.valueOf(est.get(3)));
                        exchangeSuccessDataCSVWriter.append(",");
                        exchangeSuccessDataCSVWriter.append(String.valueOf(est.get(4)));
                        exchangeSuccessDataCSVWriter.append(",");
                        exchangeSuccessDataCSVWriter.append(String.valueOf(est.get(5)));
                        exchangeSuccessDataCSVWriter.append("\n");
                        rejectedReceivedExchanges.add(est.get(2));
                        acceptedReceivedExchanges.add(est.get(3));
                        rejectedRequestedExchanges.add(est.get(4));
                        acceptedRequestedExchanges.add(est.get(5));
                    }
                }
                double rec = rejectedReceivedExchanges.stream().mapToDouble(val -> val).average().orElse(0.0);
                double aerec = acceptedReceivedExchanges.stream().mapToDouble(val -> val).average().orElse(0.0);
                double req = rejectedRequestedExchanges.stream().mapToDouble(val -> val).average().orElse(0.0);
                double aereq = acceptedRequestedExchanges.stream().mapToDouble(val -> val).average().orElse(0.0);
                statsSummaryDataWriter.append("Rejected Received Exchanges: " + (String.valueOf(rec))+ "\n");
                statsSummaryDataWriter.append("Accepted Received Exchanges: " + (String.valueOf(aerec))+ "\n");
                statsSummaryDataWriter.append("Rejected Requested Exchanges: " + (String.valueOf(req))+ "\n");
                statsSummaryDataWriter.append("Accepted Requested Exchanges: " + (String.valueOf(aereq))+ "\n");
                statsSummaryDataWriter.append("\n");
            }
        }

        // The population distributions for the simulations are averaged before being added to the population
        // distributions csv file.
        for (int day = 1; day <= days; day++) {
            ArrayList<ArrayList<Integer>> daysPopulations = endOfDayPopulationDistributions.get(day - 1);
            boolean dayOfInterest = false;
            for(int element: daysOfInterest) {
                if (day == element) {
                    dayOfInterest = true;
                    comparingPopulationDistributionsCSVWriter.append(String.valueOf(exchanges));
                    comparingPopulationDistributionsCSVWriter.append(",");
                    comparingPopulationDistributionsCSVWriter.append(String.valueOf(day));
                }
            }
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
                if (dayOfInterest) {
                    comparingPopulationDistributionsCSVWriter.append(",");
                    comparingPopulationDistributionsCSVWriter.append(String.valueOf(averagePopulation));
                }
            }
            if (dayOfInterest) {
                comparingPopulationDistributionsCSVWriter.append("\n");
            }
        }

        // Close the csv file writers once the simulation is complete.
        allDailyDataCSVWriter.close();
        socialCapitalDataCSVWriter.close();
        exchangeTypeDataCSVWriter.close();
        exchangeSuccessDataCSVWriter.close();
        statsSummaryDataWriter.close();
        averageSatisfactionsCSVWriter.close();
        individualSatisfactionsCSVWriter.close();
        individualsDataCSVWriter.close();
        populationDistributionsCSVWriter.close();

        /*
         * Begins python code that visualises the gathered data from the current environment being simulated.
         *
         * @param pythonExe String representing the system path to python environment executable.
         * @param pythonPath String representing the system path to the python data visualiser.
         * @param folderName String representing the output destination folder, used to organise output data.
         * @param environmentTag String detailing specifics about the simulation environment.
         * @param averageSatisfactionsFile Stores the average satisfaction of each Agent type at the end of each day,
         *                                 as well as the optimum average satisfaction and the satisfaction if
         *                                 allocations remained random.
         * @param individualsDataFile Stores the satisfaction of each individual Agent at the end of every round
         *                            throughout the simulation.
         * @param populationDistributionsFile Shows how the population of each Agent type varies throughout the
         *                                    simulation, influenced by social learning.
         * @param endOfDaySatisfactionsFile Stores the satisfaction of each agent at the end of days of interest.
         * @param allDataFile Stores all the data that can be analysed for the day.
         * @param days Integer value representing the number of days to be simulated.
         * @param exchanges Integer value representing the number of times all agents perform pairwise exchanges
         *                  per day.
         * @param daysToVisualise Integer array containing the days be shown in graphs produced after the simulation.
         * @param populationSize Integer value representing the size of the initial agent population.
         * @exception IOException On input error.
         * @see IOException
         */
        new SimulationVisualiserInitiator(
                pythonExe,
                pythonPath,
                folderName,
                environmentTag,
                averageSatisfactionsFile,
                individualsDataFile,
                populationDistributionsFile,
                endOfDaySatisfactionsFile,
                allDailyData,
                days,
                exchanges,
                daysOfInterest,
                populationSize
        );
    }
}
