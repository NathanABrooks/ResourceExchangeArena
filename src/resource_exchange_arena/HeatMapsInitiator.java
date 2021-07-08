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
     * @param socialCapital Boolean representing whether social capital was enabled if the comparison level was set to 0.
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
        boolean socialCapital,
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

        String meanPopulationSatisfactionPath = pythonPath + "summarise_data/collect_mean_population_satisfaction.py";

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


        // Here we create csv files summarising the population distributions with and without social capital.
        // This is completed for each simulation version.
        List<String> popDistArgs = new ArrayList<>();

        String popDistPath = pythonPath + "summarise_data/collect_merged_pop.py";

        popDistArgs.add(pythonExe);
        popDistArgs.add(popDistPath);
        popDistArgs.add(folderName);
        popDistArgs.add(Arrays.toString(evolvingAgentsArray));
        popDistArgs.add(Arrays.toString(exchangesArray));
        popDistArgs.add(startingRatiosArray.toString());
        popDistArgs.add(Arrays.toString(daysToVisualise));

        ProcessBuilder popDistBuilder = new ProcessBuilder(popDistArgs);

        // IO from the Python is shared with the same terminal as the Java code.
        popDistBuilder.inheritIO();
        popDistBuilder.redirectErrorStream(true);

        Process popDistProcess = popDistBuilder.start();
        try {
            popDistProcess.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Pop dist summary files created.");

        // Here we create csv files summarising the satisfaction levels for each agent type with and without social capital.
        // This is completed for each simulation version.
        List<String> mergedSummaryArgs = new ArrayList<>();

        String mergedSummaryPath = pythonPath + "summarise_data/collect_merged.py";

        mergedSummaryArgs.add(pythonExe);
        mergedSummaryArgs.add(mergedSummaryPath);
        mergedSummaryArgs.add(folderName);
        mergedSummaryArgs.add(Arrays.toString(evolvingAgentsArray));
        mergedSummaryArgs.add(Arrays.toString(exchangesArray));
        mergedSummaryArgs.add(startingRatiosArray.toString());
        mergedSummaryArgs.add(Arrays.toString(daysToVisualise));

        ProcessBuilder mergedSummaryBuilder = new ProcessBuilder(mergedSummaryArgs);

        // IO from the Python is shared with the same terminal as the Java code.
        mergedSummaryBuilder.inheritIO();
        mergedSummaryBuilder.redirectErrorStream(true);

        Process mergedSummaryProcess = mergedSummaryBuilder.start();
        try {
            mergedSummaryProcess.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Merged summary files created.");

        if (comparisonLevel != 0) {
            // Here we create csv files summarising the satisfaction levels for each agent type with and without social capital.
            // This is completed for each simulation version.
            List<String> compMergedSummaryArgs = new ArrayList<>();

            String compMergedSummaryPath = pythonPath + "summarise_data/collect_comp_merged.py";

            compMergedSummaryArgs.add(pythonExe);
            compMergedSummaryArgs.add(compMergedSummaryPath);
            compMergedSummaryArgs.add(folderName);
            compMergedSummaryArgs.add(Arrays.toString(evolvingAgentsArray));
            compMergedSummaryArgs.add(Arrays.toString(exchangesArray));
            compMergedSummaryArgs.add(startingRatiosArray.toString());
            compMergedSummaryArgs.add(Arrays.toString(daysToVisualise));

            ProcessBuilder compMergedSummaryBuilder = new ProcessBuilder(compMergedSummaryArgs);

            // IO from the Python is shared with the same terminal as the Java code.
            compMergedSummaryBuilder.inheritIO();
            compMergedSummaryBuilder.redirectErrorStream(true);

            Process compMergedSummaryProcess = compMergedSummaryBuilder.start();
            try {
                compMergedSummaryProcess.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Comp merged summary files created.");

            // Here we create csv files summarising the mean satisfaction levels for each agent type with and without social capital.
            // This is completed for each simulation version.
            List<String> compMeanSatisfactionArgs = new ArrayList<>();

            String compMeanSatisfactionPath = pythonPath + "summarise_data/collect_mean_satisfaction.py";

            compMeanSatisfactionArgs.add(pythonExe);
            compMeanSatisfactionArgs.add(compMeanSatisfactionPath);
            compMeanSatisfactionArgs.add(folderName);
            compMeanSatisfactionArgs.add(Arrays.toString(evolvingAgentsArray));
            compMeanSatisfactionArgs.add(Arrays.toString(exchangesArray));
            compMeanSatisfactionArgs.add(startingRatiosArray.toString());
            compMeanSatisfactionArgs.add(Arrays.toString(daysToVisualise));

            ProcessBuilder compMeanSatisfactionBuilder = new ProcessBuilder(compMeanSatisfactionArgs);

            // IO from the Python is shared with the same terminal as the Java code.
            compMeanSatisfactionBuilder.inheritIO();
            compMeanSatisfactionBuilder.redirectErrorStream(true);

            Process compMeanSatisfactionProcess = compMeanSatisfactionBuilder.start();
            try {
                compMeanSatisfactionProcess.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Comp mean satisfaction files created.");
        }

        if (comparisonLevel == 2) {
            // Here we create csv files summarising the satisfaction levels for each agent type when they make up 100% of the population.
            // This is completed for each simulation version.
            List<String> indiPopSummaryArgs = new ArrayList<>();

            String indiPopSummaryPath = pythonPath + "summarise_data/collect_individual_populations.py";

            indiPopSummaryArgs.add(pythonExe);
            indiPopSummaryArgs.add(indiPopSummaryPath);
            indiPopSummaryArgs.add(folderName);
            indiPopSummaryArgs.add(Arrays.toString(evolvingAgentsArray));
            indiPopSummaryArgs.add(Arrays.toString(exchangesArray));
            indiPopSummaryArgs.add(startingRatiosArray.toString());
            indiPopSummaryArgs.add(Arrays.toString(daysToVisualise));

            ProcessBuilder indiPopSummaryBuilder = new ProcessBuilder(indiPopSummaryArgs);

            // IO from the Python is shared with the same terminal as the Java code.
            indiPopSummaryBuilder.inheritIO();
            indiPopSummaryBuilder.redirectErrorStream(true);

            Process indiPopSummaryProcess = indiPopSummaryBuilder.start();
            try {
                indiPopSummaryProcess.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Individual populations summary files created.");
        }
    }
}
