package resource_exchange_arena;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class VisualiserInitiator {

    /**
     * The arena is the environment in which all simulations take place.
     *
     * @param folderName String representing the output destination folder, used to organise output data.
     * @param initialSeed String representing the seed of the first simulation run included in the results, this string
     *                    added to the results file names so that they can be easily replicated.
     * @param daysOfInterest Integer array containing the days be shown in graphs produced after the simulation.
     * @param days Integer value representing the number of days to be simulated.
     * @param exchanges Integer value representing the number of times all agents perform pairwise exchanges per day.
     * @param endOfDaySatisfactionsFile Stores the satisfaction of each agent at the end of days of interest.
     * @param averageSatisfactionsFile Stores the average satisfaction of each Agent type at the end of each day, as
     *                                 well as the optimum average satisfaction and the satisfaction if allocations
     *                                 remained random.
     * @param individualsDataFile Stores the satisfaction of each individual Agent at the end of every round throughout
     *                            the simulation.
     * @param populationDistributionsFile Shows how the population of each Agent type varies throughout the simulation,
     *                                    influenced by social learning.
     * @exception IOException On input error.
     * @see IOException
     */
    VisualiserInitiator(
            String folderName,
            String initialSeed,
            int[] daysOfInterest,
            int days,
            int exchanges,
            File endOfDaySatisfactionsFile,
            File averageSatisfactionsFile,
            File individualsDataFile,
            File populationDistributionsFile
    ) throws IOException {

        // Collect the required data and pass it to the Python data visualiser to produce graphs of the data.
        List<String> pythonArgs = new ArrayList<>();

        pythonArgs.add(ResourceExchangeArena.pythonExe);
        pythonArgs.add(ResourceExchangeArena.pythonPath);
        pythonArgs.add(folderName);
        pythonArgs.add(initialSeed);
        pythonArgs.add(averageSatisfactionsFile.getAbsolutePath());
        pythonArgs.add(individualsDataFile.getAbsolutePath());
        pythonArgs.add(populationDistributionsFile.getAbsolutePath());
        pythonArgs.add(endOfDaySatisfactionsFile.getAbsolutePath());
        pythonArgs.add(Integer.toString(days));
        pythonArgs.add(Integer.toString(exchanges));
        pythonArgs.add(Arrays.toString(daysOfInterest));

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
}
