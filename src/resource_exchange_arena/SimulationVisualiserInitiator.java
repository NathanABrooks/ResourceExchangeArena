package resource_exchange_arena;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class SimulationVisualiserInitiator {
    /**
     * Begins python code that visualises the gathered data from the current environment being simulated.
     *
     * @param pythonExe String representing the system path to python environment executable.
     * @param pythonPath String representing the system path to the python data visualiser.
     * @param folderName String representing the output destination folder, used to organise output data.
     * @param dataFile Stores all the data that can be analysed for each day.
     * @param typicalSocial The most average performing social run.
     * @param typicalSelfish The most average performing selfish run.
     * @exception IOException On input error.
     * @see IOException
     */
    SimulationVisualiserInitiator(
            String pythonExe,
            String pythonPath,
            String folderName,
            File dataFile,
            double typicalSocial,
            double typicalSelfish
    ) throws IOException {
        System.out.println("Starting typical run visualisation...");

        // Pass average satisfaction levels data to python to be visualised.
        List<String> satisfactionPythonArgs = new ArrayList<>();

        String satisfactionPythonPath = pythonPath + "TypicalRun.py";

        satisfactionPythonArgs.add(pythonExe);
        satisfactionPythonArgs.add(satisfactionPythonPath);
        satisfactionPythonArgs.add(folderName);
        satisfactionPythonArgs.add(dataFile.getAbsolutePath());
        satisfactionPythonArgs.add(Double.toString(typicalSocial));
        satisfactionPythonArgs.add(Double.toString(typicalSelfish));

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
