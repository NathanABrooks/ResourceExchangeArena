import math
import ast
import csv
import inflect
import os
import plotly as py
import sys

from typing import Any, Dict, List

""" Takes pre-prepared data from the SimulationVisualiserInitiator class and produces a line graphs showing the
population distribution of each agent type at the end of each day.
Data is averaged over all simulation runs.

Parameters
---------
folderName : str
    The output destination folder, used to organise output data.
tag : str
    A unique tag so that generated graphs can easily be associated with their corresponding data sets.
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
totalAgents : str
    the number of Agents that exist in the simulation.
"""

# Get the output folder from command line arguments.
folderName: str = sys.argv[1]

# Unique identifier to identify which run the produced graphs are associated with.
tag: str = sys.argv[2]

# Get the location of the raw data that requires visualising from command line arguments.
populationDistributions: str = sys.argv[3]

# Get the scope of the data to be visualised.
totalDaysSimulated: int = int(sys.argv[4])
totalExchangesSimulated: int = int(sys.argv[5])

# Get the specific days to have average satisfaction visualised throughout the day.
daysToVisualise: List[int] = ast.literal_eval(sys.argv[6])

# Number of Agents that exist in the simulation.
totalAgents: int = int(sys.argv[7])

# Used to get ordinal word versions of integers for graph titles.
inflect = inflect.engine()

# Get the directory in which the generated graphs will be stored.
baseOutputDirectory: str = \
    os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(__file__)))),
                 folderName
                 + '/'
                 + tag
                 + '/images')

# Create the output directory if it does not already exist.
if not os.path.exists(baseOutputDirectory):
    os.makedirs(baseOutputDirectory)

# Get suitable filenames format for the graphs that will be produced from existing raw data files.
baseFileName: str = populationDistributions.split('/')[-1]
convertedBaseFileName: str = baseFileName.split('.')[0] + '.png'

# Store the scope of the data.
days: List[int] = []
fieldNames: List[str] = ["Selfish", "Social"]

for day in range(1, totalDaysSimulated + 1):
    days.append(day)

# Options for graph styling.
colours: List[str] = ['purple', 'green', 'red', 'blue']
lineTypes: List[str] = ['1px', 'solid', '15px', '5px']

# The population distribution for each day is visualised as a line graph.
with open(populationDistributions) as populationData:
    # Store calculated graph data
    data: Any = []

    # Used to distinguish results when many agent types present.
    lineType: int = 0
    colour: int = 0

    reader = csv.reader(populationData)

    # Each agent type is plotted separately.
    for i in range(len(fieldNames)):
        endOfDayPopulations: List[float] = []
        for j in range(len(days)):
            populationData.seek(0)
            next(reader)
            for row in reader:
                if int(row[0]) == days[j] and int(row[1]) == i + 1:
                    endOfDayPopulations.append(float(row[2]))
                    break
                
        # Add the agent types data plots to the graph data.
        data.append(
            py.graph_objs.Scatter(
                x=days,
                y=endOfDayPopulations,
                name=fieldNames[i],
                line=dict(
                    color=colours[colour],
                    dash=lineTypes[lineType],
                    width=1,
                ),
            )
        )
        lineType += 1
        colour += 1

    # Style the graph layout
    layout: any = dict(
        title=dict(
            text='Population of each Agent type at the end of each day',
            xanchor='center',
            x=0.5,
        ),
        xaxis=dict(
            title='Day',
            showline=True,
            linecolor='black',
            linewidth=1,
            gridcolor='rgb(225, 225, 225)',
            gridwidth=1,
            range=[days[0], days[-1]],
            tickmode='linear',
            tick0=0,
            dtick=100,
        ),
        yaxis=dict(
            title='Population',
            showline=True,
            linecolor='black',
            linewidth=1,
            gridcolor='rgb(225, 225, 225)',
            gridwidth=1,
            range=[0, totalAgents],
            tickmode='linear',
            tick0=0,
            dtick=math.floor(totalAgents/5),
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
            size=16
        ),
    )

    # Create the graph and save the file
    fig: Dict[any, any] = dict(data=data, layout=layout)
    fullPath: str = os.path.join(baseOutputDirectory, convertedBaseFileName)
    py.io.write_image(fig, fullPath, format="png")
