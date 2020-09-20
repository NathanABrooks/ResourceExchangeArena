package resource_exchange_arena;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ComparativeVisualiserInitiator {
    /**
     * Begins python code that visualises comparisons of the various environments being simulated.
     *
     * @param pythonExe String representing the system path to python environment executable.
     * @param pythonPath String representing the system path to the python data visualiser.
     * @param folderName String representing the output destination folder, used to organise output data.
     * @param identityNumber Integer unique tag so that generated graphs can easily be associated with their
     *                    corresponding data sets.
     * @param exchangesFile The data set required for generating the graphs comparing exchanges and performance.
     * @param populationDistributionsFile The data set required for generating the graphs showing the average
     *                                    population distributions.
     * @param maximumExchangesSimulated Integer representing the total number of exchanges that have been simulated,
     *                                determines graphs axis dimensions.
     * @param daysToVisualise Integer array containing the days be shown in graphs produced after the simulation.
     * @exception IOException On input error.
     * @see IOException
     */
    ComparativeVisualiserInitiator(
            String pythonExe,
            String pythonPath,
            String folderName,
            int identityNumber,
            File exchangesFile,
            File populationDistributionsFile,
            int maximumExchangesSimulated,
            int[] daysToVisualise
    ) throws IOException {

        // Collect the required data and pass it to the Python data visualiser to produce graphs of the data.
        List<String> pythonArgs = new ArrayList<>();

        pythonArgs.add(pythonExe);
        pythonArgs.add(pythonPath);
        pythonArgs.add(folderName);
        pythonArgs.add(String.valueOf(identityNumber));
        pythonArgs.add(exchangesFile.getAbsolutePath());
        pythonArgs.add(populationDistributionsFile.getAbsolutePath());
        pythonArgs.add(String.valueOf(maximumExchangesSimulated));
        pythonArgs.add(Arrays.toString(daysToVisualise));

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
