package resource_exchange_arena;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ArenaEnvironment {
    // Data that is collected over simulation runs is held within the arenaEnvironment.
    ArrayList<ArrayList<Double>> keyDaysData = new ArrayList<>();
    ArrayList<Integer>  maxExchanges = new ArrayList<>();

    /**
     * The arena is the environment in which all simulations take place.
     *
     * @param folderName String representing the output destination folder, used to organise output data.
     * @param demandCurves Double arrays of demand used by the agents, when multiple curves are used the agents are
     *                     split equally between the curves.
     * @param availabilityCurve Integer array of energy availability used by the simulation.
     * @param socialCapital Boolean value that determines whether or not social agents will utilise social capital.
     * @param simulationRuns Integer value representing the number of simulations to be ran and averaged.
     * @param days Integer value representing the number of days to be simulated.
     * @param populationSize Integer value representing the size of the initial agent population.
     * @param uniqueTimeSlots Integer value representing the number of unique time-slots available in the simulation.
     * @param slotsPerAgent Integer value representing the number of time-slots each agent requires.
     * @param numberOfAgentsToEvolve Integer value representing the number of Agents who's strategy will change at the
     *                               end of each day.
     * @param agentTypes Integer array containing the agent types that the simulation will begin with. The same type
     *                   can exist multiple times in the array where more agents of one type are required.
     * @param singleAgentType Boolean value specifying whether only a single agent type should exist, used for
     *                        establishing baseline results.
     * @param selectedSingleAgentType Integer value representing the single agent type to be modelled when
     *                                singleAgentType is true.
     * @param pythonExe String representing the system path to python environment executable.
     * @param pythonPath String representing the system path to the python data visualiser.
     * @param β Double value that increases the the chance that agents will change their strategy.
     * @param satisfactionCurve Double array that determines the satisfaction fall off for slots received close to the agents preferences.
     * @exception IOException On input error.
     * @see IOException
     */
    ArenaEnvironment(
        String folderName,
        double[][] demandCurves,
        int[] availabilityCurve,
        boolean socialCapital,
        int simulationRuns,
        int days,
        int populationSize,
        int uniqueTimeSlots,
        int slotsPerAgent,
        int numberOfAgentsToEvolve,
        int[] agentTypes,
        boolean singleAgentType,
        int selectedSingleAgentType,
        String pythonExe,
        String pythonPath,
        double β,
        double[] satisfactionCurve
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
        String dataOutputFolder = folderName + "/data";
        Path dataOutputPath = Path.of(dataOutputFolder);
        Files.createDirectories(dataOutputPath);

        // Stores the amount of unspent social capital each agent has accumulated.
        File allDailyData = new File(dataOutputFolder, "dailyData.csv");

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
        allDailyDataCSVWriter.append(",");
        allDailyDataCSVWriter.append("Random Allocation Sat");
        allDailyDataCSVWriter.append(",");
        allDailyDataCSVWriter.append("Optimum ALlocation Sat");
        allDailyDataCSVWriter.append("\n");

        // Stores the amount of unspent social capital each agent has accumulated.
        File perAgentData = new File(dataOutputFolder, "agentData.csv");

        FileWriter perAgentDataCSVWriter = new FileWriter(perAgentData);
        
        perAgentDataCSVWriter.append("Simulation Run");
        perAgentDataCSVWriter.append(",");
        perAgentDataCSVWriter.append("Day");
        perAgentDataCSVWriter.append(",");
        perAgentDataCSVWriter.append("Agent Type");
        perAgentDataCSVWriter.append(",");
        perAgentDataCSVWriter.append("Satisfaction");
        perAgentDataCSVWriter.append(",");
        perAgentDataCSVWriter.append("Rejected Received Exchanges");
        perAgentDataCSVWriter.append(",");
        perAgentDataCSVWriter.append("Accepted Received Exchanges");
        perAgentDataCSVWriter.append(",");
        perAgentDataCSVWriter.append("Rejected Requested Exchanges");
        perAgentDataCSVWriter.append(",");
        perAgentDataCSVWriter.append("Accepted Requested Exchanges");
        perAgentDataCSVWriter.append(",");
        perAgentDataCSVWriter.append("Social Capital Exchanges");
        perAgentDataCSVWriter.append(",");
        perAgentDataCSVWriter.append("No Social Capital Exchanges");
        perAgentDataCSVWriter.append(",");
        perAgentDataCSVWriter.append("Unspent Social Capital");
        perAgentDataCSVWriter.append("\n");

        // Stores the satisfaction of each individual Agent at the end of every round throughout the simulation.
        File exchangeData = new File(dataOutputFolder,"exchangeData.csv");

        FileWriter eachRoundDataCSVWriter = new FileWriter(exchangeData);

        eachRoundDataCSVWriter.append("Simulation Run");
        eachRoundDataCSVWriter.append(",");
        eachRoundDataCSVWriter.append("Day");
        eachRoundDataCSVWriter.append(",");
        eachRoundDataCSVWriter.append("Round");
        eachRoundDataCSVWriter.append(",");
        eachRoundDataCSVWriter.append("Agent Type");
        eachRoundDataCSVWriter.append(",");
        eachRoundDataCSVWriter.append("Satisfaction");
        eachRoundDataCSVWriter.append("\n");

        // Stores the key data about the simulation about to begin in the data output location.
        File simulationData = new File(folderName + "/simulationData.txt");

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
        simulationDataWriter.append("Days after strategy takeover: ").append(String.valueOf(days)).append("\n");
        simulationDataWriter.append("Population size: ").append(String.valueOf(populationSize)).append("\n");
        simulationDataWriter.append("Unique time-slots: ").append(String.valueOf(uniqueTimeSlots)).append("\n");
        simulationDataWriter.append("Slots per agent: ").append(String.valueOf(slotsPerAgent)).append("\n");
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
        simulationDataWriter.append("\n\n");

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

        // Run as many simulations as has been requested.
        for (int simulationRun = 1; simulationRun <= simulationRuns; simulationRun++) {
            /*
             * Each Simulation run with the same parameters runs as an isolated instance although data is recorded in
             * a single location.
             *
             * @param demandCurves Double arrays of demand used by the agents, when multiple curves are used the agents
             *                    are split equally between the curves.
             * @param totalDemandValues Double values represeneting the sum of all values in their associated demand curves.
             * @param availabilityCurve Integer array representing the amount of energy available at each time-slot.
             * @param totalAvailability Integer value representing the total energy available throughout the day.
             * @param days Integer value representing the number of days to be simulated.
             * @param maxExchanges Stores the highest number of exchange rounds reached each simulation.
             * @param populationSize Integer value representing the size of the initial agent population.
             * @param uniqueTimeSlots Integer value representing the number of unique time-slots available in the
             *                        simulation.
             * @param slotsPerAgent Integer value representing the number of time-slots each agent requires.
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
             * @param keyDaysData Stores the state of the simulation when a population takes over and when the simulation ends.
             * @param allDailyDataCSVWriter Used to store data ragarding the state of the system at the end of each day.
             * @param perAgentDataCSVWriter Used to store data ragarding the state of the agent at the end of each day.
             * @param eachRoundDataCSVWriter Used to store data ragarding the state of the system at the end of each round.
             * @param β Double value that increases the the chance that agents will change their strategy.
             * @param satisfactionCurve Double array that determines the satisfaction fall off for slots received close to the agents preferences.
             * @exception IOException On input error.
             * @see IOException
             */
            new SimulationRun(
                    bucketedDemandCurves,
                    totalDemandValues,
                    bucketedAvailabilityCurve,
                    totalAvailability,
                    days,
                    maxExchanges,
                    populationSize,
                    uniqueTimeSlots,
                    slotsPerAgent,
                    numberOfAgentsToEvolve,
                    agentTypes,
                    uniqueAgentTypes,
                    singleAgentType,
                    selectedSingleAgentType,
                    socialCapital,
                    keyDaysData,
                    allDailyDataCSVWriter,
                    perAgentDataCSVWriter,
                    eachRoundDataCSVWriter,
                    simulationRun,
                    β,
                    satisfactionCurve
            );
            System.out.println("RUNS COMPLETED: " + simulationRun);
        }

        ArrayList<ArrayList<Double>> socialTakeoverDays = new ArrayList<>();
        ArrayList<ArrayList<Double>> selfishTakeoverDays = new ArrayList<>();

        ArrayList<ArrayList<Double>> socialFinalDays = new ArrayList<>();
        ArrayList<ArrayList<Double>> selfishFinalDays = new ArrayList<>();

        int socialRunsTotal = 0;
        int selfishRunsTotal = 0;

        for (ArrayList<Double> data: keyDaysData) {
            ArrayList<Double> newData = new ArrayList<>();
            newData.add(data.get(0));
            newData.add(data.get(1));

            if (data.get(data.size() - 1) == 0.0) {
                if(data.get(3) == 0) {
                    newData.add(data.get(4));
                    newData.add(data.get(6));

                    socialTakeoverDays.add(newData);
                    socialRunsTotal++;
                } else {
                    newData.add(data.get(5));
                    newData.add(data.get(7));

                    selfishTakeoverDays.add(newData);
                    selfishRunsTotal++;
                }
            } else {
                if(data.get(3) == 0) {
                    newData.add(data.get(4));
                    newData.add(data.get(6));

                    socialFinalDays.add(newData);
                } else {
                    newData.add(data.get(5));
                    newData.add(data.get(7));

                    selfishFinalDays.add(newData);
                }
            }
        }

        final int column = 1;
        Comparator<ArrayList<Double>> myComparator = new Comparator<ArrayList<Double>>() {
            @Override
            public int compare(ArrayList<Double> o1, ArrayList<Double> o2) {
                return o1.get(column).compareTo(o2.get(column));
            }
        };

        Collections.sort(socialTakeoverDays, myComparator);
        Collections.sort(selfishTakeoverDays, myComparator);

        Collections.sort(socialFinalDays, myComparator);
        Collections.sort(selfishFinalDays, myComparator);

        int middleSelfish = 0;
        int middleSocial = 0;

        if (socialRunsTotal > 0) {
            ArrayList<Double> middleSocialTakeover = socialTakeoverDays.get((int) Math.floor(socialRunsTotal / 2.0f));
            ArrayList<Double> slowestSocialTakeover = socialTakeoverDays.get(socialRunsTotal - 1);
            ArrayList<Double> fastestSocialTakeover = socialTakeoverDays.get(0);

            middleSocial = (int) Math.floor(middleSocialTakeover.get(0));
            int slowSocial = (int) Math.floor(slowestSocialTakeover.get(0));
            int fastSocial = (int) Math.floor(fastestSocialTakeover.get(0));

            simulationDataWriter.append("Social Takeovers: " + socialRunsTotal).append("\n");
            simulationDataWriter.append("Fastest Social: Run " + fastSocial).append("\n");
            simulationDataWriter.append("Slowest Social: Run " + slowSocial).append("\n");
            simulationDataWriter.append("Typical Social: Run " + middleSocial).append("\n");

            double avgDaysSocial = 0;
            double avgSatSocial = 0;
            double avgSDSocial = 0;

            for(ArrayList<Double> run: socialTakeoverDays) {
                avgDaysSocial += run.get(1);
                avgSatSocial += run.get(2);
                avgSDSocial += run.get(3);
            }

            simulationDataWriter.append("Average Takeover Days (social): " + avgDaysSocial / socialTakeoverDays.size()).append("\n");
            simulationDataWriter.append("Average Takeover Satisfaction (social): " + avgSatSocial / socialTakeoverDays.size()).append("\n");
            simulationDataWriter.append("Average Takeover SD (social): " + avgSDSocial / socialTakeoverDays.size()).append("\n");

            
            avgDaysSocial = 0;
            avgSatSocial = 0;
            avgSDSocial = 0;
            
            for(ArrayList<Double> run: socialFinalDays) {
                avgDaysSocial += run.get(1);
                avgSatSocial += run.get(2);
                avgSDSocial += run.get(3);
            }

            simulationDataWriter.append("Average Final Satisfaction (social): " + avgSatSocial / socialFinalDays.size()).append("\n");
            simulationDataWriter.append("Average Final SD (social): " + avgSDSocial / socialFinalDays.size()).append("\n\n");
        }

        if (selfishRunsTotal > 0) {
            ArrayList<Double> middleSelfishTakeover = selfishTakeoverDays.get((int) Math.floor(selfishRunsTotal / 2.0f));
            ArrayList<Double> slowestSelfishTakeover = selfishTakeoverDays.get(selfishRunsTotal - 1);
            ArrayList<Double> fastestSelfishTakeover = selfishTakeoverDays.get(0);

            middleSelfish = (int) Math.floor(middleSelfishTakeover.get(0));
            int slowSelfish = (int) Math.floor(slowestSelfishTakeover.get(0));
            int fastSelfish = (int) Math.floor(fastestSelfishTakeover.get(0));

            simulationDataWriter.append("Selfish Takeovers: " + selfishRunsTotal).append("\n");
            simulationDataWriter.append("Fastest selfish: Run " + fastSelfish).append("\n");
            simulationDataWriter.append("Slowest selfish: Run " + slowSelfish).append("\n");
            simulationDataWriter.append("Typical selfish: Run " + middleSelfish).append("\n");

            double avgDaysSelfish = 0;
            double avgSatSelfish = 0;
            double avgSDSelfish = 0;

            for(ArrayList<Double> run: selfishTakeoverDays) {
                avgDaysSelfish += run.get(1);
                avgSatSelfish += run.get(2);
                avgSDSelfish += run.get(3);
            }

            simulationDataWriter.append("Average Takeover Days (selfish): " + avgDaysSelfish / selfishTakeoverDays.size()).append("\n");
            simulationDataWriter.append("Average Takeover Satisfaction (selfish): " + avgSatSelfish / selfishTakeoverDays.size()).append("\n");
            simulationDataWriter.append("Average Takeover SD (selfish): " + avgSDSelfish / socialTakeoverDays.size()).append("\n");

            avgDaysSelfish = 0;
            avgSatSelfish = 0;
            avgSDSelfish = 0;

            for(ArrayList<Double> run: selfishFinalDays) {
                avgDaysSelfish += run.get(1);
                avgSatSelfish += run.get(2);
                avgSDSelfish += run.get(3);
            }

            simulationDataWriter.append("Average Final Satisfaction (selfish): " + avgSatSelfish / selfishFinalDays.size()).append("\n");
            simulationDataWriter.append("Average Final SD (selfish): " + avgSDSelfish / selfishFinalDays.size());
        }
        
        // Close the file writers once the simulation is complete.
        allDailyDataCSVWriter.close();
        perAgentDataCSVWriter.close();
        eachRoundDataCSVWriter.close();
        simulationDataWriter.close();

        /**
         * Begins python code that visualises the gathered data from the current environment being simulated.
         *
         * @param pythonExe String representing the system path to python environment executable.
         * @param pythonPath String representing the system path to the python data visualiser.
         * @param folderName String representing the output destination folder, used to organise output data.
         * @param environmentTag String detailing specifics about the simulation environment.
         * @param dataFile Stores all the data that can be analysed for each day.
         * @param typicalSocial The most average performing social run.
         * @param typicalSelfish The most average performing selfish run.
         * @exception IOException On input error.
         * @see IOException
         */
        new SimulationVisualiserInitiator(
                pythonExe,
                pythonPath,
                folderName,
                allDailyData,
                middleSocial,
                middleSelfish
        );
    }
}
