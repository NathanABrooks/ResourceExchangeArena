import ast
import csv
import inflect
import os
import plotly as py
import sys

from typing import Any, Dict, List

""" Takes pre-prepared data from the SimulationVisualiserInitiator class and produces a series of violin plots
comparing the satisfaction distributions of the different agent types at the end of a series of specified key days.
Data is averaged over all simulation runs.

Parameters
---------
folderName : str
    The output destination folder, used to organise output data.
tag : str
    A unique tag so that generated graphs can easily be associated with their corresponding data sets.
individualSatisfactions : str
    The absolute path of the data set required for generating the violin plots showing the satisfaction distributions
    for each agent type at the end of the key days.
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
                 folderName
                 + '/'
                 + tag
                 + '/images/DDDataAnalysis/EoDSD')

# Create the output directory if it does not already exist.
if not os.path.exists(duringDayOutputDirectory):
    os.makedirs(duringDayOutputDirectory)

# Get suitable filenames format for the graphs that will be produced from existing raw data files.
baseFileName: str = individualSatisfactions.split('/')[-1]
convertedBaseFileName: str = baseFileName.split('.')[0] + '.png'

# Store the scope of the data.
days: List[str] = []
fieldNames: List[str] = ["Selfish", "Social"]

for day in range(1, totalDaysSimulated + 1):
    days.append(str(day))

# Options for graph styling.
colours: List[str] = ['purple', 'green', 'red', 'blue']
lineTypes: List[str] = ['1px', 'solid', '15px', '5px']

# Violin plots show the distributions of individual satisfactions for individual agents at the end of key days
with open(individualSatisfactions) as individualSatisfactionDeviations:
    reader = csv.reader(individualSatisfactionDeviations)

    # Each pre-selected is visualised in its own graph.
    for i in range(len(daysToVisualise)):

        # Store calculated graph data
        data: Any = []

        # Used to distinguish results when many agent types present.
        lineType: int = 0
        colour: int = 0

        # Each agent type is plotted separately.
        firstType = True
        for j in range(len(fieldNames)):
            satisfactions: List[float] = []
            individualSatisfactionDeviations.seek(0)

            # The first line contains only headers and so can be skipped.
            next(reader)
            for row in reader:
                # The field type column + 1 used as agent types start at 1 as opposed to 0.
                if int(row[0]) == int(daysToVisualise[i]) \
                        and int(row[1]) == int(j + 1):
                    satisfactions.append(float(row[2]))

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
                        showlegend=True,
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
                        showlegend=True,
                    )
                )
            lineType += 1
            colour += 1

        # The day value is converted into the ordinal word form for styling.
        day: str = inflect.number_to_words(inflect.ordinal(daysToVisualise[i]))

        # Style the graph layout
        layout: any = dict(
            title=dict(
                text='Satisfaction deviation during the<br>' + day + ' day',
                xanchor='center',
                x=0.5,
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
            xaxis=dict(
                showline=True,
                linecolor='black',
                linewidth=1,
            ),
            violinmode='overlay',
            violingap=0,
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
