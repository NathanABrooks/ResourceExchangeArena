package resource_exchange_arena;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class SimulationVisualiserInitiator {

    /**
     * Begins python code that visualises the gathered data from the current environment being simulated.
     *
     * @param pythonExe      {@link String} representing the system path to python environment executable.
     * @param pythonPath     {@link String} representing the system path to the python data visualiser.
     * @param folderName     {@link String} representing the output destination folder, used to organise output data.
     * @param environmentTag {@link String} detailing specifics about the simulation environment.
     * @param dataFile       Stores all the data that can be analysed for each {@link Day}.
     * @param typicalSocial  The most average performing social run.
     * @param typicalSelfish The most average performing selfish run.
     * @throws IOException On input error.
     * @see IOException
     */
    SimulationVisualiserInitiator(
            String pythonExe,
            String pythonPath,
            String folderName,
            String environmentTag,
            @NotNull File dataFile,
            double typicalSocial,
            double typicalSelfish
    ) throws IOException {
        System.out.println("Starting typical run visualisation...");

        // Pass average satisfaction levels data to python to be visualised.
        List<String> satisfactionPythonArgs = new ArrayList<>();

        String satisfactionPythonPath = pythonPath + "TypicalRun.py";

        for (String s : Arrays.asList(pythonExe,
                satisfactionPythonPath,
                folderName,
                environmentTag,
                dataFile.getAbsolutePath(),
                Double.toString(typicalSocial),
                Double.toString(typicalSelfish)))
            satisfactionPythonArgs.add(s);

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
        System.out.println("Visualisation complete.");
    }
}
