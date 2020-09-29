package resource_exchange_arena;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class SimulationVisualiserInitiator {
    /**
     * Begins python code that visualises the gathered data from the current environment being simulated.
     *
     * @param pythonExe String representing the system path to python environment executable.
     * @param pythonPath String representing the system path to the python data visualiser.
     * @param folderName String representing the output destination folder, used to organise output data.
     * @param initialSeed String representing the seed of the first simulation run included in the results, this string
     *                    added to the results file names so that they can be easily replicated.
     * @param averageSatisfactionsFile Stores the average satisfaction of each Agent type at the end of each day, as
     *                                 well as the optimum average satisfaction and the satisfaction if allocations
     *                                 remained random.
     * @param individualsDataFile Stores the satisfaction of each individual Agent at the end of every round throughout
     *                            the simulation.
     * @param populationDistributionsFile Shows how the population of each Agent type varies throughout the simulation,
     *                                    influenced by social learning.
     * @param endOfDaySatisfactionsFile Stores the satisfaction of each agent at the end of days of interest.
     * @param days Integer value representing the number of days to be simulated.
     * @param exchanges Integer value representing the number of times all agents perform pairwise exchanges per day.
     * @param daysToVisualise Integer array containing the days be shown in graphs produced after the simulation.
     * @exception IOException On input error.
     * @see IOException
     */
    SimulationVisualiserInitiator(
            String pythonExe,
            String pythonPath,
            String folderName,
            String initialSeed,
            File averageSatisfactionsFile,
            File individualsDataFile,
            File populationDistributionsFile,
            File endOfDaySatisfactionsFile,
            int days,
            int exchanges,
            int[] daysToVisualise
    ) throws IOException {
        System.out.println("Starting simulation data visualisation...");

        String simulationPythonPath = pythonPath + "simulation_data_analysis/";
        String duringDayPythonPath = pythonPath + "during_day_data_analysis/";

        // Collect the required data and pass it to the Python data visualiser to produce graphs of the data.
        List<String> satisfactionPythonArgs = new ArrayList<>();

        String satisfactionPythonPath = simulationPythonPath + "AverageSatisfactionLevels.py";

        satisfactionPythonArgs.add(pythonExe);
        satisfactionPythonArgs.add(satisfactionPythonPath);
        satisfactionPythonArgs.add(folderName);
        satisfactionPythonArgs.add(initialSeed);
        satisfactionPythonArgs.add(averageSatisfactionsFile.getAbsolutePath());
        satisfactionPythonArgs.add(Integer.toString(days));
        satisfactionPythonArgs.add(Integer.toString(exchanges));
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
        System.out.println("Average satisfaction levels data visualisation complete.");

        // Collect the required data and pass it to the Python data visualiser to produce graphs of the data.
        List<String> popDistPythonArgs = new ArrayList<>();

        String popDistsPythonPath = simulationPythonPath + "PopulationDistribution.py";

        popDistPythonArgs.add(pythonExe);
        popDistPythonArgs.add(popDistsPythonPath);
        popDistPythonArgs.add(folderName);
        popDistPythonArgs.add(initialSeed);
        popDistPythonArgs.add(populationDistributionsFile.getAbsolutePath());
        popDistPythonArgs.add(Integer.toString(days));
        popDistPythonArgs.add(Integer.toString(exchanges));
        popDistPythonArgs.add(Arrays.toString(daysToVisualise));

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
        System.out.println("Average satisfaction levels data visualisation complete.");

        // Collect the required data and pass it to the Python data visualiser to produce graphs of the data.
        List<String> dailySatisfactionPythonArgs = new ArrayList<>();

        String dailySatisfactionPythonPath = duringDayPythonPath + "DuringDayAverageSatisfactionLevels.py";

        dailySatisfactionPythonArgs.add(pythonExe);
        dailySatisfactionPythonArgs.add(dailySatisfactionPythonPath);
        dailySatisfactionPythonArgs.add(folderName);
        dailySatisfactionPythonArgs.add(initialSeed);
        dailySatisfactionPythonArgs.add(individualsDataFile.getAbsolutePath());
        dailySatisfactionPythonArgs.add(Integer.toString(days));
        dailySatisfactionPythonArgs.add(Integer.toString(exchanges));
        dailySatisfactionPythonArgs.add(Arrays.toString(daysToVisualise));

        ProcessBuilder dailySatisfactionBuilder = new ProcessBuilder(dailySatisfactionPythonArgs);

        // IO from the Python is shared with the same terminal as the Java code.
        dailySatisfactionBuilder.inheritIO();
        dailySatisfactionBuilder.redirectErrorStream(true);

        Process dailySatisfactionProcess = dailySatisfactionBuilder.start();
        try {
            dailySatisfactionProcess.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Average satisfaction levels data visualisation complete.");

        // Collect the required data and pass it to the Python data visualiser to produce graphs of the data.
        List<String> dailyDistributionPythonArgs = new ArrayList<>();

        String dailyDistributionPythonPath = duringDayPythonPath + "EndOfDaySatisfactionDistribution.py";

        dailyDistributionPythonArgs.add(pythonExe);
        dailyDistributionPythonArgs.add(dailyDistributionPythonPath);
        dailyDistributionPythonArgs.add(folderName);
        dailyDistributionPythonArgs.add(initialSeed);
        dailyDistributionPythonArgs.add(endOfDaySatisfactionsFile.getAbsolutePath());
        dailyDistributionPythonArgs.add(Integer.toString(days));
        dailyDistributionPythonArgs.add(Integer.toString(exchanges));
        dailyDistributionPythonArgs.add(Arrays.toString(daysToVisualise));

        ProcessBuilder dailyDistributionBuilder = new ProcessBuilder(dailyDistributionPythonArgs);

        // IO from the Python is shared with the same terminal as the Java code.
        dailyDistributionBuilder.inheritIO();
        dailyDistributionBuilder.redirectErrorStream(true);

        Process dailyDistributionProcess = dailyDistributionBuilder.start();
        try {
            dailyDistributionProcess.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Average satisfaction levels data visualisation complete.");

        System.out.println("Simulation data visualisation complete.");
    }
}
