import ast
import csv
import inflect
import os
import plotly as py
import sys

from typing import Any, Dict, List

""" Takes pre-prepared data from the ComparativeVisualiserInitiator class and produces a series of graphs 
visualising the data. These graphs show how the results of the simulation varies as the number of exchange rounds
taking place each day increases. They also show how the population distribution varies in the same scenario.
 All graphs use data that has been averaged over a series of simulations.

Parameters
---------
folderName : str
    The output destination folder, used to organise output data.
identityNumber : str
    A unique tag so that generated graphs can easily be associated with their corresponding data sets.
exchangesFile : str
    The absolute path of the data set required for generating the graphs comparing exchanges and performance.
populationDistributionsFile : str
    The absolute path of the data set required for generating the graphs showing the average population distributions.
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
populationDistributionsFile: str = sys.argv[3]

# Get the scope of the data to be visualised.
maxExchangesSimulated: int = int(sys.argv[4])

# Get the specific days to be visualised.
daysToVisualise: List[int] = ast.literal_eval(sys.argv[5])

# Used to get ordinal word versions of integers for graph titles.
inflect = inflect.engine()

# Get the directories in which the generated graphs will be stored.
baseOutputDirectory: str = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(__file__)))),
                                        'results/'
                                        + folderName
                                        + '/comparativeGraphs/images')

# Create the output directories if they do not already exist.
if not os.path.exists(baseOutputDirectory):
    os.makedirs(baseOutputDirectory)

# Get suitable filenames format for the graphs that will be produced from existing raw data files.
baseFileName: str = populationDistributionsFile.split('/')[-1]
convertedBaseFileName: str = (baseFileName.split('.')[0] + '.pdf').replace('Data', '')

# Store the scope of the data, which will be the same for each graph, as global lists.
exchanges: List[str] = []
fieldNames: List[str] = []

for exchange in range(1, maxExchangesSimulated + 1):
    exchanges.append(str(exchange))

# Options for graph styling.
colours: List[str] = [
    'rgba(93, 164, 214, 0.8)', 'rgba(255, 144, 14, 0.8)', 'rgba(44, 160, 101, 0.8)',
    'rgba(255, 65, 54, 0.8)', 'rgba(207, 114, 255, 0.8)', 'rgba(127, 96, 0, 0.8)',
]
lineTypes: List[str] = ['solid', 'dot', 'dash', 'dashdot', 'longdashdot']

# Average population sizes for each agent type at the end of each day given the number of exchanges are visualised as a
# line graph. Only pre-selected days are visualised to minimise compute time.
with open(populationDistributionsFile) as summaryData:
    # Before visualising the full data set, pull data field names.
    setupReader = csv.DictReader(summaryData)

    # The first columns are 'Day' and 'Exchanges' and so aren't needed here.
    fieldNames.extend(setupReader.fieldnames[2:])

    # Each pre-selected day is visualised in its own graph.
    for i in range(len(daysToVisualise)):
        # Store calculated graph data
        data: Any = []

        reader = csv.reader(summaryData)
        # Standard deviations are not shown here and so only half the fieldNames are used.
        for j in range(int(len(fieldNames))):
            # Used to distinguish results when many agent types present.
            lineType: int = 0

            endOfDayAverages: List = []
            for k in range(len(exchanges)):
                summaryData.seek(0)

                # The first line contains only headers and so can be skipped.
                next(reader)
                dataFound: bool = False
                for row in reader:
                    if int(row[1]) == int(daysToVisualise[i]) and int(row[0]) == int(exchanges[k]):
                        endOfDayAverages.append(row[j + 2])
                        dataFound = True
                        break
                if not dataFound:
                    endOfDayAverages.append(None)

            # Get new line styling combination,
            # calculated to match with graphs not including random or optimum allocations.
            colour: int = j + (len(colours))
            while colour >= len(colours):
                colour -= len(colours)

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

        # The day value is converted into the ordinal word form for styling.
        day: str = inflect.number_to_words(inflect.ordinal(daysToVisualise[i]))

        # Style the graph layout
        layout: any = dict(
            title='Average population distribution at the end of the ' + day + ' day',
            xaxis=dict(
                title='Exchanges per day',
                showline=True,
                linecolor='black',
                linewidth=2,
                gridcolor='rgb(255, 255, 255)',
                gridwidth=2,
                range=[exchanges[0], exchanges[-1]],
                tickmode='linear',
                tick0=0,
                dtick=10,
            ),
            yaxis=dict(
                title='Average population size',
                showline=True,
                linecolor='black',
                linewidth=2,
                gridcolor='rgb(255, 255, 255)',
                gridwidth=2,
                range=[0, 96],
                tickmode='linear',
                tick0=0,
                dtick=5,
            ),
            margin=dict(
                l=40,
                r=30,
                b=80,
                t=100,
            ),
            paper_bgcolor='rgb(243, 243, 243)',
            plot_bgcolor='rgb(243, 243, 243)',
        )

        # Create the graph and save the file
        fig: Dict[any, any] = dict(data=data, layout=layout)
        fileName: str = convertedBaseFileName.replace(
            '.pdf', '_' + str(daysToVisualise[i]) + '.pdf')
        fullPath: str = os.path.join(baseOutputDirectory, fileName)
        py.io.write_image(fig, fullPath)
