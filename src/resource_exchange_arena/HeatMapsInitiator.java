package resource_exchange_arena;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class HeatMapsInitiator {
    /**
     * Begins python code that generates heatmaps to compare various simulations.
     *
     * @param pythonExe String representing the system path to python environment executable.
     * @param pythonPath String representing the system path to the python data visualiser.
     * @param folderName String representing the output destination folder, used to organise output data.
     * @param comparisonLevel Integer used to quickly identify which heat maps should be generated.
     * @param learningPercentages Integer array of the percentage of Agents that possibly used social Learning per day.
     * @param exchangesArray Integer array of the various number of exchanges per day that were simulated.
     * @param startingRatiosArray String arraylist of the various starting ratios between agent types that were simulated.
     * @param daysOfInterest Integer array containing the days to be analysed.
     * @see IOException
     */
    HeatMapsInitiator(
        String pythonExe,
        String pythonPath,
        String folderName,
        int comparisonLevel,
        int[] evolvingAgentsArray,
        int[] exchangesArray,
        ArrayList<String> startingRatiosArray,
        int[] daysToVisualise
    ) throws IOException {
        System.out.println("Starting heat maps generation...");

        pythonPath += "heat_maps/";

        // Here collect all the mean population satisfaction data from a version of the simulation
        // (Such as all runs with social capital enabled and a mixed population)
        // into a single file for statistical significance testing.
        // This is completed for each simulation version.
        List<String> meanPopulationSatisfactionArgs = new ArrayList<>();

        String meanPopulationSatisfactionPath = pythonPath + "summarise_data/collect_mean_satisfaction.py";

        meanPopulationSatisfactionArgs.add(pythonExe);
        meanPopulationSatisfactionArgs.add(meanPopulationSatisfactionPath);
        meanPopulationSatisfactionArgs.add(folderName);
        meanPopulationSatisfactionArgs.add(Arrays.toString(evolvingAgentsArray));
        meanPopulationSatisfactionArgs.add(Arrays.toString(exchangesArray));
        meanPopulationSatisfactionArgs.add(startingRatiosArray.toString());
        meanPopulationSatisfactionArgs.add(Arrays.toString(daysToVisualise));

        ProcessBuilder meanPopulationSatisfactionBuilder = new ProcessBuilder(meanPopulationSatisfactionArgs);

        // IO from the Python is shared with the same terminal as the Java code.
        meanPopulationSatisfactionBuilder.inheritIO();
        meanPopulationSatisfactionBuilder.redirectErrorStream(true);

        Process meanPopulationSatisfactionProcess = meanPopulationSatisfactionBuilder.start();
        try {
            meanPopulationSatisfactionProcess.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Mean population satisfaction summary file created.");
    }
}
