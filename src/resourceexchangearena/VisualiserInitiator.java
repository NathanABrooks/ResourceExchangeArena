package resourceexchangearena;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class VisualiserInitiator {
    static void visualise(int[] daysOfInterest, String initialSeed, File prePreparedPopulationDistributionsFile, File prePreparedBoxPlotFile, File prePreparedAverageFile, File prePreparedIndividualFile) throws IOException {
        // Collect the required data and pass it to the Python data visualiser to produce graphs of the data.
        String pythonExe = "/home/nathan/anaconda3/envs/ResourceExchangeArena/bin/python";
        String pythonPath = "/home/nathan/code/ResourceExchangeArena/src/datahandler/DataVisualiser.py";
        String daysToAnalyse = Arrays.toString(daysOfInterest);

        List<String> pythonArgs = new ArrayList<>();

        String thirdGraph;
        if (ResourceExchangeArena.VARIED_AGENT_TYPES) {
            thirdGraph = prePreparedPopulationDistributionsFile.getAbsolutePath();
        } else {
            thirdGraph = prePreparedBoxPlotFile.getAbsolutePath();
        }

        pythonArgs.add(pythonExe);
        pythonArgs.add(pythonPath);
        pythonArgs.add(ResourceExchangeArena.RELEASE_VERSION);
        pythonArgs.add(initialSeed);
        pythonArgs.add(prePreparedAverageFile.getAbsolutePath());
        pythonArgs.add(prePreparedIndividualFile.getAbsolutePath());
        pythonArgs.add(thirdGraph);
        pythonArgs.add(Integer.toString(ResourceExchangeArena.DAYS));
        pythonArgs.add(Integer.toString(ResourceExchangeArena.EXCHANGES));
        pythonArgs.add(daysToAnalyse);

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
