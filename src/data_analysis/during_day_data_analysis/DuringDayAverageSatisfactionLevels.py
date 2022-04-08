import ast
import csv
import inflect
import os
import plotly as py
import sys

from typing import Any, Dict, List

""" Takes pre-prepared data from the SimulationVisualiserInitiator class and produces a series of line graphs
comparing how the number of exchange rounds taking place influences the average satisfaction levels of the different
agent types throughout a series of specified key days.
Data is averaged over all simulation runs.

Parameters
---------
folderName : str
    The output destination folder, used to organise output data.
tag : str
    A unique tag so that generated graphs can easily be associated with their corresponding data sets.
keyDaysSatisfactionLevels : str
    The absolute path of the data set required for generating the line graphs showing the average satisfaction of each
    agent type at the end of each round of trading.
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
tag: str = sys.argv[2]

# Get the location of the raw data that requires visualising from command line arguments.
keyDaysSatisfactionLevels: str = sys.argv[3]

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
                 folderName
                 + '/'
                 + tag
                 + '/images/DDDataAnalysis/DDASL')

# Create the output directory if it does not already exist.
if not os.path.exists(duringDayOutputDirectory):
    os.makedirs(duringDayOutputDirectory)

# Get suitable filenames format for the graphs that will be produced from existing raw data files.
baseFileName: str = keyDaysSatisfactionLevels.split('/')[-1]
convertedBaseFileName: str = baseFileName.split('.')[0] + '.png'

# Store the scope of the data.
days: List[str] = []
exchanges: List[int] = []
fieldNames: List[str] = ["Selfish", "Social"]

for day in range(1, totalDaysSimulated + 1):
    days.append(str(day))

for exchange in range(1, totalExchangesSimulated + 1):
    exchanges.append(exchange)

# Options for graph styling.
colours: List[str] = ['purple', 'green', 'red', 'blue']
lineTypes: List[str] = ['1px', 'solid', '15px', '5px']

# Average consumer satisfactions for each agent type at the end of each round day are visualised as a line graph.
# Only pre-selected days are visualised to minimise compute time.
with open(keyDaysSatisfactionLevels) as duringKeyDaysSatisfactionLevels:
    reader = csv.reader(duringKeyDaysSatisfactionLevels)

    # Each pre-selected is visualised in its own graph.
    for i in range(len(daysToVisualise)):
        # Store calculated graph data
        data: Any = []

        # Used to distinguish results when many agent types present.
        lineType: int = 0
        colour: int = 0

        # Each agent type is plotted separately.
        for j in range(len(fieldNames)):
            endOfRoundAverages: List[float] = []
            for k in range(len(exchanges)):
                duringKeyDaysSatisfactionLevels.seek(0)

                # The first line contains only headers and so can be skipped.
                next(reader)
                for row in reader:
                    # The field type column + 1 used as agent types start at 1 as opposed to 0.
                    if int(row[0]) == int(daysToVisualise[i]) \
                            and int(row[1]) == exchanges[k] \
                            and int(row[2]) == int(j + 1):
                        endOfRoundAverages.append(float(row[3]))
                        break

            # Add the agent types data plots to the graph data.
            data.append(
                py.graph_objs.Scatter(
                    x=exchanges,
                    y=endOfRoundAverages,
                    name=fieldNames[j],
                    line=dict(
                        color=colours[colour],
                        dash=lineTypes[lineType],
                        width=1,
                    ),
                )
            )
            lineType += 1
            colour += 1

        # The day value is converted into the ordinal word form for styling.
        day: str = inflect.number_to_words(inflect.ordinal(daysToVisualise[i]))

        lowx = 0
        highx = 200
        if len(exchanges) > 1:
             lowx = exchanges[0]
             highx = exchanges[-1]

        # Style the graph layout
        layout: any = dict(
            title=dict(
                text='Average consumer satisfaction during the<br>' + day + ' day',
                xanchor='center',
                x=0.5,
            ),
            xaxis=dict(
                title='Rounds',
                showline=True,
                linecolor='black',
                linewidth=1,
                gridcolor='rgb(225, 225, 225)',
                gridwidth=1,
                range=[lowx, highx],
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
        fullPath: str = os.path.join(duringDayOutputDirectory, fileName)
        py.io.write_image(fig, fullPath, format="png")
