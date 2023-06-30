package resource_exchange_arena;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public class ArenaEnvironment {
    // Data that is collected over simulation runs is held within the arenaEnvironment.
    ArrayList<ArrayList<Double>> keyDaysData = new ArrayList<>();
    ArrayList<Integer> maxExchanges = new ArrayList<>();

    /**
     * The arena is the environment in which all simulations take place.
     *
     * @param folderName              {@link String} representing the output destination folder, used to organise output data.
     * @param environmentTag          {@link String} detailing specifics about the simulation environment.
     * @param demandCurves            {@link Double} arrays of demand used by the {@link Agent}s, when multiple curves are used the {@link Agent}s are split equally between the curves.
     * @param availabilityCurve       {@link Integer} array of energy availability used by the simulation.
     * @param socialCapital           {@link Boolean} value that determines whether social {@link Agent}s will utilise social capital.
     * @param simulationRuns          {@link Integer} value representing the number of simulations to be run and averaged.
     * @param days                    {@link Integer} value representing the number of {@link Day}s to be simulated.
     * @param populationSize          {@link Integer} value representing the size of the initial {@link Agent} population.
     * @param uniqueTimeSlots         {@link Integer} value representing the number of unique time slots available in the simulation.
     * @param slotsPerAgent           {@link Integer} value representing the number of time slots each {@link Agent} requires.
     * @param numberOfAgentsToEvolve  {@link Integer} value representing the number of {@link Agent}s whose strategy will change at the end of each {@link Day}.
     * @param agentTypes              {@link Integer} array containing the {@link Agent} types that the simulation will begin with. The same type can exist multiple times in the array where more {@link Agent}s of one type are required.
     * @param singleAgentType         {@link Boolean} value specifying whether only a single {@link Agent} type should exist, used for establishing baseline results.
     * @param selectedSingleAgentType {@link Integer} value representing the single {@link Agent} type to be modelled when {@code singleAgentType} is true.
     * @param pythonExe               {@link String} representing the system path to python environment executable.
     * @param pythonPath              {@link String} representing the system path to the python data visualiser.
     * @throws IOException On input error.
     * @see IOException
     */
    ArenaEnvironment(
            String folderName,
            String environmentTag,
            double[][] demandCurves,
            int[] availabilityCurve,
            boolean socialCapital,
            int simulationRuns,
            int days,
            int populationSize,
            int uniqueTimeSlots,
            int slotsPerAgent,
            int numberOfAgentsToEvolve,
            int @NotNull [] agentTypes,
            boolean singleAgentType,
            int selectedSingleAgentType,
            String pythonExe,
            String pythonPath
    ) throws IOException {

        System.out.println("Starting simulation...");

        // Array of the unique agent types used in the simulation.
        ArrayList<Integer> uniqueAgentTypes =
                Arrays.stream(agentTypes).distinct().boxed().sorted().collect(Collectors.toCollection(ArrayList::new));

        // Sort the agent types so that they are ordered correctly in the output csv files.

        // Create a directory to store the data output by the simulation.
        String dataOutputFolder = folderName + "/" + environmentTag + "/data";
        Path dataOutputPath = Path.of(dataOutputFolder);
        Files.createDirectories(dataOutputPath);

        // Stores the amount of unspent social capital each agent has accumulated.
        File allDailyData = new File(dataOutputFolder, "dailyData.csv");

        FileWriter allDailyDataCSVWriter = new FileWriter(allDailyData);

        Utilities.write(allDailyDataCSVWriter, "Simulation Run",
                ",", "Day",
                ",", "Social Pop",
                ",", "Selfish Pop",
                ",", "Social Sat",
                ",", "Selfish Sat",
                ",", "Social SD",
                ",", "Selfish SD",
                ",", "Social Upper Quartile",
                ",", "Selfish Upper Quartile",
                ",", "Social Lower Quartile",
                ",", "Selfish Lower Quartile",
                ",", "Social 95th Percentile",
                ",", "Selfish 95th Percentile",
                ",", "Social Max",
                ",", "Selfish Max",
                ",", "Social Min",
                ",", "Selfish Min",
                ",", "Social Median",
                ",", "Selfish Median",
                ",", "Random Allocation Sat",
                ",", "Optimum Allocation Sat", "\n");

        // Stores the amount of unspent social capital each agent has accumulated.
        File perAgentData = new File(dataOutputFolder, "agentData.csv");

        FileWriter perAgentDataCSVWriter = new FileWriter(perAgentData);

        Utilities.write(perAgentDataCSVWriter, "Simulation Run",
                ",", "Day",
                ",", "Agent Type",
                ",", "Satisfaction",
                ",", "Rejected Received Exchanges",
                ",", "Accepted Received Exchanges",
                ",", "Rejected Requested Exchanges",
                ",", "Accepted Requested Exchanges",
                ",", "Social Capital Exchanges",
                ",", "No Social Capital Exchanges",
                ",", "Unspent Social Capital", "\n");

        // Stores the satisfaction of each individual Agent at the end of every round throughout the simulation.
        File exchangeData = new File(dataOutputFolder, "exchangeData.csv");

        FileWriter eachRoundDataCSVWriter = new FileWriter(exchangeData);

        Utilities.write(eachRoundDataCSVWriter, "Simulation Run",
                ",", "Day",
                ",", "Round",
                ",", "Agent Type",
                ",", "Satisfaction", "\n");

        // Stores the key data about the simulation about to begin in the data output location.
        File simulationData = new File(folderName + "/" + environmentTag, "simulationData.txt");

        FileWriter simulationDataWriter = new FileWriter(simulationData);

        Utilities.write(simulationDataWriter, "Simulation Information: \n\n",
                "Seed: ",
                String.valueOf(ResourceExchangeArena.seed),
                "\n", "Single agent type: ",
                String.valueOf(singleAgentType),
                "\n", "Use social capital: ",
                String.valueOf(socialCapital),
                "\n", "Simulation runs: ",
                String.valueOf(simulationRuns),
                "\n", "Days after strategy takeover: ",
                String.valueOf(days),
                "\n", "Population size: ",
                String.valueOf(populationSize),
                "\n", "Unique time slots: ",
                String.valueOf(uniqueTimeSlots),
                "\n", "Slots per agent: ",
                String.valueOf(slotsPerAgent),
                "\n", "Number of agents to evolve: ",
                String.valueOf(numberOfAgentsToEvolve),
                "\n", "Starting ratio of agent types: ");

        int typesListed = 0;
        for (int type : agentTypes) {
            if (typesListed != 0) {
                simulationDataWriter.append(" : ");
            }
            typesListed++;
            simulationDataWriter.append(Inflect.getHumanReadableAgentType(type));
        }
        if (singleAgentType) {
            simulationDataWriter.append("Agent type: ")
                    .append(String.valueOf(selectedSingleAgentType)).append("\n");
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
            double totalDemand = Arrays.stream(bucketedDemandCurve).sum();
            totalDemand = Math.round(totalDemand * 10.0) / 10.0;
            totalDemandValues[i] = totalDemand;
        }

        // The availability curve is bucketed before the simulations for efficiency, as they will all use the same bucketed values.
        int[] bucketedAvailabilityCurve = new int[uniqueTimeSlots];
        int totalAvailability = 0;

        int bucket = 0;
        int bucketFill = 0;
        int bucketValue = 0;
        for (int j : availabilityCurve) {
            totalAvailability += j;
            bucketValue += j;
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

            // Create a new simulation run.
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
                    simulationRun
            );
            System.out.println("RUNS COMPLETED: " + simulationRun);
        }

        ArrayList<ArrayList<Double>> socialTakeoverDays = new ArrayList<>();
        ArrayList<ArrayList<Double>> selfishTakeoverDays = new ArrayList<>();

        ArrayList<ArrayList<Double>> socialFinalDays = new ArrayList<>();
        ArrayList<ArrayList<Double>> selfishFinalDays = new ArrayList<>();

        int socialRunsTotal = 0;
        int selfishRunsTotal = 0;

        for (ArrayList<Double> data : keyDaysData) {
            ArrayList<Double> newData = new ArrayList<>();
            newData.add(data.get(0));
            newData.add(data.get(1));

            if (data.get(data.size() - 1) == 0.0) {
                if (data.get(3) == 0) {
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
                if (data.get(3) == 0) {
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
        Comparator<ArrayList<Double>> myComparator = Comparator.comparing(o -> o.get(column));

        socialTakeoverDays.sort(myComparator);
        selfishTakeoverDays.sort(myComparator);

        socialFinalDays.sort(myComparator);
        selfishFinalDays.sort(myComparator);

        int middleSelfish = 0;
        int middleSocial = 0;

        if (socialRunsTotal > 0) {
            ArrayList<Double> middleSocialTakeover = socialTakeoverDays.get((int) Math.floor(socialRunsTotal / 2.0f));
            ArrayList<Double> slowestSocialTakeover = socialTakeoverDays.get(socialRunsTotal - 1);
            ArrayList<Double> fastestSocialTakeover = socialTakeoverDays.get(0);

            middleSocial = (int) Math.floor(middleSocialTakeover.get(0));
            int slowSocial = (int) Math.floor(slowestSocialTakeover.get(0));
            int fastSocial = (int) Math.floor(fastestSocialTakeover.get(0));

            for (Writer writer : Arrays.asList(
                    simulationDataWriter.append("Social Takeovers: ").append(String.valueOf(socialRunsTotal)),
                    simulationDataWriter.append("Fastest Social: Run ").append(String.valueOf(fastSocial)),
                    simulationDataWriter.append("Slowest Social: Run ").append(String.valueOf(slowSocial)),
                    simulationDataWriter.append("Typical Social: Run ").append(String.valueOf(middleSocial))))
                writer.append("\n");

            double avgDaysSocial = 0;
            double avgSatSocial = 0;
            double avgSDSocial = 0;

            for (ArrayList<Double> run : socialTakeoverDays) {
                avgDaysSocial += run.get(1);
                avgSatSocial += run.get(2);
                avgSDSocial += run.get(3);
            }

            for (String s : Arrays.asList("Average Takeover Days (social): ",
                    String.valueOf(avgDaysSocial / socialTakeoverDays.size()),
                    "Average Takeover Satisfaction (social): ",
                    String.valueOf(avgSatSocial / socialTakeoverDays.size()),
                    "Average Takeover SD (social): ",
                    String.valueOf(avgSDSocial / socialTakeoverDays.size())))
                simulationDataWriter.append(s).append("\n");

            avgDaysSocial = 0;
            avgSatSocial = 0;
            avgSDSocial = 0;

            for (ArrayList<Double> run : socialFinalDays) {
                avgDaysSocial += run.get(1);
                avgSatSocial += run.get(2);
                avgSDSocial += run.get(3);
            }

            simulationDataWriter.append("Average Final Satisfaction (social): ").append(String.valueOf(avgSatSocial / socialFinalDays.size())).append("\n");
            simulationDataWriter.append("Average Final SD (social): ").append(String.valueOf(avgSDSocial / socialFinalDays.size())).append("\n\n");
        }

        if (selfishRunsTotal > 0) {
            ArrayList<Double> middleSelfishTakeover = selfishTakeoverDays.get((int) Math.floor(selfishRunsTotal / 2.0f));
            ArrayList<Double> slowestSelfishTakeover = selfishTakeoverDays.get(selfishRunsTotal - 1);
            ArrayList<Double> fastestSelfishTakeover = selfishTakeoverDays.get(0);

            middleSelfish = (int) Math.floor(middleSelfishTakeover.get(0));
            int slowSelfish = (int) Math.floor(slowestSelfishTakeover.get(0));
            int fastSelfish = (int) Math.floor(fastestSelfishTakeover.get(0));

            Utilities.write(simulationDataWriter, "Selfish Takeovers: ", "\n",
                    String.valueOf(selfishRunsTotal), "\n",
                    "Fastest selfish: Run ", "\n",
                    String.valueOf(fastSelfish), "\n",
                    "Slowest selfish: Run ", "\n",
                    String.valueOf(slowSelfish), "\n",
                    "Typical selfish: Run ", "\n",
                    String.valueOf(middleSelfish));

            double avgDaysSelfish = 0;
            double avgSatSelfish = 0;
            double avgSDSelfish = 0;

            for (ArrayList<Double> run : selfishTakeoverDays) {
                avgDaysSelfish += run.get(1);
                avgSatSelfish += run.get(2);
                avgSDSelfish += run.get(3);
            }

            Utilities.write(simulationDataWriter, "Average Takeover Days (selfish): ",
                    String.valueOf(avgDaysSelfish / selfishTakeoverDays.size()),
                    "\n", "Average Takeover Satisfaction (selfish): ",
                    String.valueOf(avgSatSelfish / selfishTakeoverDays.size()),
                    "\n", "Average Takeover SD (selfish): ",
                    String.valueOf(avgSDSelfish / socialTakeoverDays.size()),
                    "\n");

            avgDaysSelfish = 0;
            avgSatSelfish = 0;
            avgSDSelfish = 0;

            for (ArrayList<Double> run : selfishFinalDays) {
                avgDaysSelfish += run.get(1);
                avgSatSelfish += run.get(2);
                avgSDSelfish += run.get(3);
            }

            simulationDataWriter.append("Average Final Satisfaction (selfish): ").append(String.valueOf(avgSatSelfish / selfishFinalDays.size())).append("\n");
            simulationDataWriter.append("Average Final SD (selfish): ").append(String.valueOf(avgSDSelfish / selfishFinalDays.size()));
        }

        // Close the file writers once the simulation is complete.
        for (FileWriter fileWriter : Arrays.asList(allDailyDataCSVWriter, perAgentDataCSVWriter, eachRoundDataCSVWriter, simulationDataWriter))
            fileWriter.close();

        // Begin visualisation
        new SimulationVisualiserInitiator(
                pythonExe,
                pythonPath,
                folderName,
                environmentTag,
                allDailyData,
                middleSocial,
                middleSelfish
        );
    }
}
