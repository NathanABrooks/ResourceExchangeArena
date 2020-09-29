import ast
import csv
import inflect
import os
import plotly as py
import sys

from typing import Any, Dict, List

""" Takes pre-prepared data from the VisualiserInitiator.java method and produces a series of graphs visualising the 
    data. The types of graphs are as follows:
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
averageSatisfactionLevels : str
    The absolute path of the data set required for generating the line graph showing the average satisfaction of each
    agent type at the end of each day.
keyDaysSatisfactionLevels : str
    The absolute path of the data set required for generating the line graphs showing the average satisfaction of each
    agent type at the end of each round of trading.
populationDistributions : str
    The absolute path of the data set required for generating the line graph showing the average population
    distributions at the end of each day.
totalDaysSimulated : int
    The total number of days that have been simulated, determines graphs axis dimensions.
totalExchangesSimulated : int
    The total number of exchanges that have been simulated, determines graphs axis dimensions.
daysToVisualise : str
    The specific days that will have a line graph of the agent satisfactions at the end of each round throughout the 
    day generated. Note that this is immediately converted to type List[int].
"""

# Get the output folder from command line arguments.
folderName: str = sys.argv[1]

# Unique identifier to identify which run the produced graphs are associated with.
seed: str = sys.argv[2]

# Get the location of the raw data that requires visualising from command line arguments.
individualSatisfactions: str = sys.argv[3]

# Get the scope of the data to be visualised.
totalDaysSimulated: int = int(sys.argv[4])
totalExchangesSimulated: int = int(sys.argv[5])

# Get the specific days to have average satisfaction visualised throughout the day.
daysToVisualise: List[int] = ast.literal_eval(sys.argv[6])

# Used to get ordinal word versions of integers for graph titles.
inflect = inflect.engine()

# Get the directory in which the generated graphs will be stored.
duringDayOutputDirectory: str = \
    os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(__file__)))),
                 'results/'
                 + folderName
                 + '/'
                 + seed
                 + '/images/duringDayAverages')

# Create the output directory if it does not already exist.
if not os.path.exists(duringDayOutputDirectory):
    os.makedirs(duringDayOutputDirectory)

# Get suitable filenames format for the graphs that will be produced from existing raw data files.
baseFileName: str = individualSatisfactions.split('/')[-1]
convertedBaseFileName: str = baseFileName.split('.')[0] + '.pdf'

# Store the scope of the data, which will be the same for each graph, as global lists.
days: List[str] = []
exchanges: List[str] = []
fieldNames: List[str] = ["Selfish", "Social"]

for day in range(1, totalDaysSimulated + 1):
    days.append(str(day))

for exchange in range(1, totalExchangesSimulated + 1):
    exchanges.append(str(exchange))

# Options for graph styling.
colours: List[str] = ['red', 'blue', 'purple', 'green']
lineTypes: List[str] = ['15px', '5px', '1px', 'solid']

# Violin plots show the distributions of individual satisfactions for individual agents at the end of key days
with open(individualSatisfactions) as individualSatisfactionDeviations:
    reader = csv.reader(individualSatisfactionDeviations)

    # Each pre-selected is visualised in its own graph.
    for i in range(len(daysToVisualise)):

        # Store calculated graph data
        data: Any = []

        # Used to distinguish results when many agent types present.
        lineType: int = 0

        # Each agent type is plotted separately.
        firstType = True
        for j in range(len(fieldNames)):
            satisfactions: List[str] = []
            individualSatisfactionDeviations.seek(0)

            # The first line contains only headers and so can be skipped.
            next(reader)
            for row in reader:
                # The field type column + 1 used as agent types start at 1 as opposed to 0.
                if int(row[0]) == int(daysToVisualise[i]) \
                        and int(row[1]) == int(j + 1):
                    satisfactions.append(row[2])

            # Get new line styling combination.
            colour: int = j
            while colour >= len(colours):
                colour -= len(colours)
                lineType += 1
            while lineType >= len(lineTypes):
                lineType -= len(lineTypes)

            # Add the agent types data plots to the graph data.
            if firstType:
                data.append(
                    py.graph_objs.Violin(
                        y=satisfactions,
                        x0=' ',
                        width=1,
                        name=fieldNames[j],
                        side='negative',
                        line=dict(
                            color=colours[colour],
                        ),
                        meanline_visible=True,
                        scalemode='count',
                        spanmode='hard',
                        points=False,
                    )
                )
                firstType = False
            else:
                data.append(
                    py.graph_objs.Violin(
                        y=satisfactions,
                        x0=' ',
                        width=1,
                        name=fieldNames[j],
                        side='positive',
                        line=dict(
                            color=colours[colour],
                        ),
                        meanline_visible=True,
                        scalemode='count',
                        spanmode='hard',
                        points=False,
                    )
                )

        # The day value is converted into the ordinal word form for styling.
        day: str = inflect.number_to_words(inflect.ordinal(daysToVisualise[i]))
        title: str = 'Satisfaction deviation during the ' + day + ' day'

        # Style the graph layout
        layout: any = dict(
            title=title,
            violinmode='overlay',
            violingap=0,
            paper_bgcolor='rgb(243, 243, 243)',
            plot_bgcolor='rgb(243, 243, 243)',
        )

        # Create the graph and save the file
        fig: Dict[any, any] = dict(data=data, layout=layout)
        fileName: str = convertedBaseFileName.replace(
            '.pdf', '_' + str(daysToVisualise[i]) + '.pdf')
        fullPath: str = os.path.join(duringDayOutputDirectory, fileName)
        py.io.write_image(fig, fullPath)
