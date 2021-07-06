package resource_exchange_arena;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class significanceTestInitiator {
    /**
     * Begins python code that runs statistical significance test on the simulation results.
     *
     * @param pythonExe String representing the system path to python environment executable.
     * @param pythonPath String representing the system path to the python data visualiser.
     * @param folderName String representing the output destination folder, used to organise output data.
     * @param learningPercentages Integer array of the percentage of Agents that possibly used social Learning per day.
     * @param exchangesArray Integer array of the various number of exchanges per day that were simulated.
     * @param startingRatiosArray String arraylist of the various starting ratios between agent types that were simulated.
     * @param daysOfInterest Integer array containing the days to be analysed.
     * @exception IOException On input error.
     * @see IOException
     */
    significanceTestInitiator(
        String pythonExe,
        String pythonPath,
        String folderName,
        int[] evolvingAgentsArray,
        int[] exchangesArray,
        ArrayList<String> startingRatiosArray,
        int[] daysToVisualise
    ) throws IOException {    
        System.out.println("Starting satistical significance analysis...");

        pythonPath += "stats/";

        // Here collect all the satiscfaction data for each agent from a version of the simulation
        // (Such as all runs with social capital enabled and a mixed population)
        // into a single file for statistical significance testing.
        // This is completed for each simulation version.
        List<String> satisfactionBreakdownArgs = new ArrayList<>();

        String satisfactionBreakdownPath = pythonPath + "collect_individual_satisfactions.py";

        satisfactionBreakdownArgs.add(pythonExe);
        satisfactionBreakdownArgs.add(satisfactionBreakdownPath);
        satisfactionBreakdownArgs.add(folderName);
        satisfactionBreakdownArgs.add(Arrays.toString(evolvingAgentsArray));
        satisfactionBreakdownArgs.add(Arrays.toString(exchangesArray));
        satisfactionBreakdownArgs.add(startingRatiosArray.toString());
        satisfactionBreakdownArgs.add(Arrays.toString(daysToVisualise));

        ProcessBuilder satisfactionBreakdownBuilder = new ProcessBuilder(satisfactionBreakdownArgs);

        // IO from the Python is shared with the same terminal as the Java code.
        satisfactionBreakdownBuilder.inheritIO();
        satisfactionBreakdownBuilder.redirectErrorStream(true);

        Process satisfactionBreakdownProcess = satisfactionBreakdownBuilder.start();
        try {
            satisfactionBreakdownProcess.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Individual satisfaction breakdown file created.");

        // Here perform statistical significance testing from the data previously summarised.
        List<String> statsArgs = new ArrayList<>();

        String statsPath = pythonPath + "stats.py";

        statsArgs.add(pythonExe);
        statsArgs.add(statsPath);
        statsArgs.add(folderName);
        statsArgs.add(Arrays.toString(evolvingAgentsArray));
        statsArgs.add(Arrays.toString(exchangesArray));
        statsArgs.add(startingRatiosArray.toString());
        statsArgs.add(Arrays.toString(daysToVisualise));

        ProcessBuilder statsBuilder = new ProcessBuilder(statsArgs);

        // IO from the Python is shared with the same terminal as the Java code.
        statsBuilder.inheritIO();
        statsBuilder.redirectErrorStream(true);

        Process statsProcess = statsBuilder.start();
        try {
            statsProcess.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Statistical significance testing complete.");
    }
}
