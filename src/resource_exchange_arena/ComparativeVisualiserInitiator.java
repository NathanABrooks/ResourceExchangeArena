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
     * @param populationSize Integer value representing the size of the initial agent population.
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
            int[] daysToVisualise,
            int populationSize
    ) throws IOException {
        System.out.println("Starting comparative data visualisation...");

        pythonPath += "comparative_data_analysis/";

        // Pass comparative population distribution data to python to be visualised.
        List<String> popDistPythonArgs = new ArrayList<>();

        String PopDistPath = pythonPath + "PopulationDistributionAgainstExchanges.py";

        popDistPythonArgs.add(pythonExe);
        popDistPythonArgs.add(PopDistPath);
        popDistPythonArgs.add(folderName);
        popDistPythonArgs.add(String.valueOf(identityNumber));
        popDistPythonArgs.add(populationDistributionsFile.getAbsolutePath());
        popDistPythonArgs.add(String.valueOf(maximumExchangesSimulated));
        popDistPythonArgs.add(Arrays.toString(daysToVisualise));
        popDistPythonArgs.add(String.valueOf(populationSize));

        ProcessBuilder popDistBuilder = new ProcessBuilder(popDistPythonArgs);

        // IO from the Python is shared with the same terminal as the Java code.
        popDistBuilder.inheritIO();
        popDistBuilder.redirectErrorStream(true);

        Process popDistProcess = popDistBuilder.start();
        try {
            popDistProcess.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Comparative population distribution data visualisation complete.");

        // Pass comparative satisfaction data to python to be visualised.
        List<String> satisfactionPythonArgs = new ArrayList<>();

        String satisfactionPath = pythonPath + "SatisfactionAgainstExchanges.py";

        satisfactionPythonArgs.add(pythonExe);
        satisfactionPythonArgs.add(satisfactionPath);
        satisfactionPythonArgs.add(folderName);
        satisfactionPythonArgs.add(String.valueOf(identityNumber));
        satisfactionPythonArgs.add(exchangesFile.getAbsolutePath());
        satisfactionPythonArgs.add(String.valueOf(maximumExchangesSimulated));
        satisfactionPythonArgs.add(Arrays.toString(daysToVisualise));

        ProcessBuilder satisfactionBuilder = new ProcessBuilder(satisfactionPythonArgs);

        // IO from the Python is shared with the same terminal as the Java code.
        satisfactionBuilder.inheritIO();
        satisfactionBuilder.redirectErrorStream(true);

        Process satisfactionProcess = satisfactionBuilder.start();
        try {
            satisfactionProcess.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Comparative satisfaction data visualisation complete.");
    }
}
