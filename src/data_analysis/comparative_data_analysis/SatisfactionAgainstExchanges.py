import ast
import csv
import inflect
import os
import plotly as py
import sys

from typing import Any, Dict, List

""" Takes pre-prepared data from the ComparativeVisualiserInitiator class and produces a series of line graphs
comparing how the number of exchange rounds taking place influences the average satisfaction levels of the different
agent types at the end of a series of specified key days.
Data is averaged over all simulation runs.

Parameters
---------
folderName : str
    The output destination folder, used to organise output data.
identityNumber : str
    A unique tag so that generated graphs can easily be associated with their corresponding data sets.
exchangesFile : str
    The absolute path of the data set required for generating the graphs comparing exchanges and performance.
maximumExchangesSimulated : int
    The maximum number of exchanges that have been simulated, determines graphs axis dimensions.
daysToVisualise : str
    The specific days that will have a line graph showing the satisfaction of each agent type generated.
"""

# Get the output folder from command line arguments.
folderName: str = sys.argv[1]

# Unique identifier to identify which run the produced graphs are associated with.
identityNumber: str = sys.argv[2]

# Get the locations of the raw data that requires visualising from command line arguments.
exchangesFile: str = sys.argv[3]

# Get the scope of the data to be visualised.
maxExchangesSimulated: int = int(sys.argv[4])

# Get the specific days to be visualised.
daysToVisualise: List[int] = ast.literal_eval(sys.argv[5])

# Used to get ordinal word versions of integers for graph titles.
inflect = inflect.engine()

# Get the directories in which the generated graphs will be stored.
baseOutputDirectory: str = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(__file__)))),
                                        folderName
                                        + '/comparative/images/SaE')

# Create the output directories if they do not already exist.
if not os.path.exists(baseOutputDirectory):
    os.makedirs(baseOutputDirectory)

# Get suitable filenames format for the graphs that will be produced from existing raw data files.
baseFileName: str = exchangesFile.split('/')[-1]
convertedBaseFileName: str = (baseFileName.split('.')[0] + '.png').replace('Data', '')

# Store the scope of the data.
exchanges: List[str] = []
fieldNames: List[str] = []

for exchange in range(1, maxExchangesSimulated + 1):
    exchanges.append(exchange)

# Options for graph styling.
colours: List[str] = ['purple', 'green', 'red', 'blue']
lineTypes: List[str] = ['1px', 'solid', '15px']

# Average consumer satisfactions for each agent type at the end of each day given the number of exchanges are
# visualised as a line graph. Only pre-selected days are visualised to minimise compute time.
with open(exchangesFile) as summaryData:
    # Before visualising the full data set, pull data field names.
    setupReader = csv.DictReader(summaryData)

    # The first columns are 'Day' and 'Exchanges' and so aren't needed here.
    fieldNames.extend(setupReader.fieldnames[2:])

    # Each pre-selected day is visualised in its own graph.
    for i in range(len(daysToVisualise)):
        # Store calculated graph data
        data: Any = []

        reader = csv.reader(summaryData)

        for j in range(len(fieldNames)):
            # If the data is for standard deviations, (j >= 2), then it needs to be handled differently.
            if j < 2:
                endOfDayAverages: List[float] = []
                for k in range(len(exchanges)):
                    summaryData.seek(0)

                    # The first line contains only headers and so can be skipped.
                    next(reader)
                    dataFound: bool = False
                    for row in reader:
                        if int(row[1]) == int(daysToVisualise[i]) and int(row[0]) == exchanges[k]:
                            endOfDayAverages.append(float(row[j + 2]))
                            dataFound = True
                            break
                    if not dataFound:
                        endOfDayAverages.append(None)

                # Get new line styling combination,
                # calculated to match with graphs not including random or optimum allocations.
                colour: int = j + (len(colours))
                while colour >= len(colours):
                    colour -= len(colours)
                lineType: int = j + (len(lineTypes))
                while lineType >= len(lineTypes):
                    lineType -= len(lineTypes)

                # Add the agent types data plots to the graph data.
                data.append(
                    py.graph_objs.Scatter(
                        x=exchanges,
                        y=endOfDayAverages,
                        name=fieldNames[j],
                        line=dict(
                            color=colours[colour],
                            dash=lineTypes[lineType],
                            width=1,
                        ),
                        connectgaps=True,
                    )
                )
            else:
                k = 0
                while k < 2:
                    colour = j + (len(colours) - 2)
                    while colour >= len(colours):
                        colour -= len(colours)

                    endOfDayAverages: List[float] = []

                    for m in range(len(exchanges)):
                        summaryData.seek(0)

                        # The first line contains only headers and so can be skipped.
                        next(reader)
                        dataFound: bool = False
                        for row in reader:
                            if int(row[0]) == exchanges[m] and int(row[1]) == int(daysToVisualise[i]):
                                if k == 0:
                                    upperSD = float(row[j]) + (float(row[j + 2]) / 2)
                                    # The upper standard deviation line can be capped at the maximum possible value.
                                    if upperSD > 1:
                                        upperSD = 1
                                    endOfDayAverages.append(float(upperSD))
                                    dataFound = True
                                    break
                                else:
                                    lowerSD = float(row[j]) - (float(row[j + 2]) / 2)
                                    if lowerSD < 0:
                                        lowerSD = 0
                                    endOfDayAverages.append(float(lowerSD))
                                    dataFound = True
                                    break
                        if not dataFound:
                            endOfDayAverages.append(None)
                    # Add the agent types data plots to the graph data.
                    data.append(
                        py.graph_objs.Scatter(
                            x=exchanges,
                            y=endOfDayAverages,
                            showlegend=False,
                            line=dict(
                                color=colours[colour],
                                dash='5px',
                                width=1,
                            ),
                            connectgaps=True,
                        )
                    )
                    k = k + 1
                    colour = i + (len(colours) - 2)

        # The day value is converted into the ordinal word form for styling.
        day: str = inflect.number_to_words(inflect.ordinal(daysToVisualise[i]))

        # Style the graph layout
        layout: any = dict(
            title=dict(
                text='Average consumer satisfaction at the end of the<br>' + day + ' day',
                xanchor='center',
                x=0.5,
            ),
            xaxis=dict(
                title='Exchanges per day',
                showline=True,
                linecolor='black',
                linewidth=1,
                gridcolor='rgb(225, 225, 225)',
                gridwidth=1,
                range=[exchanges[0], exchanges[-1]],
                tickmode='linear',
                tick0=0,
                dtick=50,
            ),
            yaxis=dict(
                title='Average consumer satisfaction',
                showline=True,
                linecolor='black',
                linewidth=1,
                gridcolor='rgb(225, 225, 225)',
                gridwidth=1,
                range=[0, 1],
                tickmode='linear',
                tick0=0,
                dtick=0.2,
            ),
            margin=dict(
                l=40,
                r=30,
                b=80,
                t=100,
            ),
            paper_bgcolor='rgb(255, 255, 255)',
            plot_bgcolor='rgb(255, 255, 255)',
            font=dict(
                size=19
            ),
        )

        # Create the graph and save the file
        fig: Dict[any, any] = dict(data=data, layout=layout)
        fileName: str = convertedBaseFileName.replace(
            '.png', '_day_' + str(daysToVisualise[i]) + '.png')
        fullPath: str = os.path.join(baseOutputDirectory, fileName)
        py.io.write_image(fig, fullPath, format="png")
