import ast
import csv
import inflect
import os
import plotly as py
import sys

from typing import Any, Dict, List

""" Takes pre-prepared data from the VisualiserInitiator.java method and produces a series of graphs 
visualising the data. The types of graphs are as follows:
 - A line graph showing the average satisfaction of each agent type at the end of each day, as well as the average
    satisfaction of all agents if time slots were allocated randomly or optimally.
 - Line graphs showing the average satisfaction of each agent type at the end of each round of trading, a graph is
    generated for each of a series of days passed as a parameter.
 - A line graph showing how the population distribution alters over the course of the simulation.
All graphs use data that has been averaged over a series of simulations.

Parameters
---------
folderName : str
    The output destination folder, used to organise output data.
seed : str
    A unique tag so that generated graphs can easily be associated with their corresponding data sets.
averageSatisfactionLevels: str
    The absolute path of the data set required for generating the line graph showing the average satisfaction of each
    agent type at the end of each day.
keyDaysSatisfactionLevels: str
    The absolute path of the data set required for generating the line graphs showing the average satisfaction of each
    agent type at the end of each round of trading.
populationDistributions: str
    The absolute path of the data set required for generating the line graph showing the average population
    distributions at the end of each day.
totalDaysSimulated: int
    The total number of days that have been simulated, determines graphs axis dimensions.    
totalExchangesSimulated: int
    The total number of exchanges that have been simulated, determines graphs axis dimensions.    
daysToVisualise: str
    The specific days that will have a line graph of the agent satisfactions at the end of each round throughout the day
    generated. Note that this is immediately converted to type List[int].
"""

print('Visualising summary graphs...', flush=True)

# Get the output folder from command line arguments.
folderName: str = sys.argv[1]

# Unique identifier to identify which run the produced graphs are associated with.
identityNumber: str = sys.argv[2]

# Get the location of the raw data that requires visualising from command line arguments.
file: str = sys.argv[3]

# Get the scope of the data to be visualised.
totalExchangesSimulated: int = int(sys.argv[4])

# Get the specific days to be visualised.
daysToVisualise: List[int] = ast.literal_eval(sys.argv[5])

# Used to get ordinal word versions of integers for graph titles.
inflect = inflect.engine()

# Get the directories in which the generated graphs will be stored.
baseOutputDirectory: str = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))),
                                        'results/'
                                        + folderName
                                        + '/summaryGraphs/images')

# Create the output directories if they do not already exist.
if not os.path.exists(baseOutputDirectory):
    os.makedirs(baseOutputDirectory)

# Get suitable filenames format for the graphs that will be produced from existing raw data files.
baseFileName: str = file.split('/')[-1]
convertedBaseFileName: str = baseFileName.split('.')[0] + '.pdf'

# Store the scope of the data, which will be the same for each graph, as global lists.
exchanges: List[str] = []
fieldNames: List[str] = []

for exchange in range(1, totalExchangesSimulated + 1):
    exchanges.append(str(exchange))

# Options for graph styling.
colours: List[str] = [
    'rgba(93, 164, 214, 0.8)', 'rgba(255, 144, 14, 0.8)', 'rgba(44, 160, 101, 0.8)',
    'rgba(255, 65, 54, 0.8)', 'rgba(207, 114, 255, 0.8)', 'rgba(127, 96, 0, 0.8)',
]
lineTypes: List[str] = ['solid', 'dot', 'dash', 'dashdot', 'longdashdot']

# Average consumer satisfactions for each agent type at the end of each round day are visualised as a line graph.
# Only pre-selected days are visualised to minimise compute time.
with open(file) as summaryData:
    reader = csv.reader(summaryData)

    # Before visualising the full data set, pull data that can be reused for later visualisations.
    setupReader = csv.DictReader(summaryData)

    # The first columns are 'Day' and 'Exchanges' and so aren't needed here.
    fieldNames.extend(setupReader.fieldnames[2:])

    # Each pre-selected is visualised in its own graph.
    for i in range(len(daysToVisualise)):
        # Store calculated graph data
        data: Any = []

        reader = csv.reader(summaryData)

        for j in range(len(fieldNames)):
            if j < 2:
                # Used to distinguish results when many agent types present.
                lineType: int = 0

                endOfDayAverages: List[int] = []
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

                # Get new line styling combination, calculated to match with graphs not including random or optimum allocations.
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
            else:
                lineType: int = 1

                k = 0
                while k < 2:
                    colour = j + (len(colours) - 2)
                    while colour >= len(colours):
                        colour -= len(colours)

                    endOfDayAverages: List[int] = []

                    for l in range(len(exchanges)):
                        summaryData.seek(0)

                        # The first line contains only headers and so can be skipped.
                        next(reader)
                        dataFound: bool = False
                        for row in reader:
                            if int(row[0]) == int(exchanges[l]) and int(row[1]) == int(daysToVisualise[i]):
                                if k == 0:
                                    upperSD = float(row[j + 2]) + float(row[j])
                                    if upperSD > 1:
                                        upperSD = 1
                                    endOfDayAverages.append(upperSD)
                                    dataFound = True
                                    break
                                else:
                                    lowerSD = float(row[j]) - float(row[j + 2])
                                    if lowerSD < 0:
                                        lowerSD = 0
                                    endOfDayAverages.append(lowerSD)
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
                                dash=lineTypes[lineType],
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
            title='Average consumer satisfaction at the end of the ' + day + ' day',
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
                title='Average consumer satisfaction',
                showline=True,
                linecolor='black',
                linewidth=2,
                gridcolor='rgb(255, 255, 255)',
                gridwidth=2,
                range=[0, 1],
                tickmode='linear',
                tick0=0,
                dtick=0.1,
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
print('Data visualisation complete.', flush=True)
