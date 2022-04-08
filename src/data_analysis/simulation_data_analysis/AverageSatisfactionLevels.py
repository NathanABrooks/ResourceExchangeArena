import ast
import csv
import inflect
import os
import plotly as py
import sys

from typing import Any, Dict, List

""" Takes pre-prepared data from the SimulationVisualiserInitiator class and produces a line graphs comparing the
average satisfaction of each agent type at the end of each day, as well as the average satisfaction of all agents if 
time slots were allocated randomly or optimally.
Data is averaged over all simulation runs.

Parameters
---------
folderName : str
    The output destination folder, used to organise output data.
tag : str
    A unique tag so that generated graphs can easily be associated with their corresponding data sets.
averageSatisfactionLevels : str
    The absolute path of the data set required for generating the line graph showing the average satisfaction of each
    agent type at the end of each day.
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
averageSatisfactionLevels: str = sys.argv[3]

# Get the scope of the data to be visualised.
totalDaysSimulated: int = int(sys.argv[4])
totalExchangesSimulated: int = int(sys.argv[5])

# Get the specific days to have average satisfaction visualised throughout the day.
daysToVisualise: List[int] = ast.literal_eval(sys.argv[6])

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
baseFileName: str = averageSatisfactionLevels.split('/')[-1]
convertedBaseFileName: str = baseFileName.split('.')[0] + '.png'

# Store the scope of the data.
days: List[int] = []
fieldNames: List[str] = []

for day in range(1, totalDaysSimulated + 1):
    days.append(day)

# Options for graph styling.
colours: List[str] = ['red', 'blue', 'purple', 'green']
lineTypes: List[str] = ['15px', '5px', '1px', 'solid']

# Average consumer satisfactions for each agent type for each day are visualised as a line graph.
# Hypothetical random and optimum allocations are also visualised.
with open(averageSatisfactionLevels) as dailyAverageSatisfactionLevels:
    # Before visualising the full data set, pull data field names.
    setupReader = csv.DictReader(dailyAverageSatisfactionLevels)

    # The first column will always be 'Day', and so this shouldn't be stored with the list of global field names.
    fieldNames.extend(setupReader.fieldnames[1:])

    # Store calculated graph data
    data: Any = []

    # Used to distinguish results when many agent types present.
    lineType: int = 0
    colour: int = 0

    # Should error bars be offset?
    offset: bool = False

    reader = csv.reader(dailyAverageSatisfactionLevels)

    # Each agent type, including random and optimum allocation, is plotted separately.
    for i in range(len(fieldNames)):
        if i < 4:
            endOfDayAverages: List[float] = []
            errorBarValuesPlus: List[float] = []
            errorBarValuesMinus: List[float] = []
            dailyAverageSatisfactionLevels.seek(0)
            next(reader)
            for j in range(len(days)):
                for row in reader:
                    if int(row[0]) == days[j]:
                        # Append the column + 1, to account for the 'Day' column.
                        endOfDayAverages.append(float(row[i + 1]))
                        if offset:
                            if int(row[0]) > 5 \
                                    and ((int(row[0]) % 50 == 5 and i == 2) or (int(row[0]) % 50 == 45 and i == 3)):
                                positiveError: float = float(row[i + 3])
                                negativeError: float = float(row[i + 3])
                                if float(row[i + 1]) + float(row[i + 3]) >= 1.0:
                                    positiveError = float(1 - float(row[i + 1]))
                                if float(row[i + 1]) - float(row[i + 3]) <= 0.0:
                                    negativeError = float(row[i + 1])
                                errorBarValuesPlus.append(positiveError)
                                errorBarValuesMinus.append(negativeError)
                            else:
                                errorBarValuesPlus.append('null')
                                errorBarValuesMinus.append('null')
                        else:
                            if int(row[0]) % 50 == 0:
                                positiveError: float = float(row[i + 3])
                                negativeError: float = float(row[i + 3])
                                if float(row[i + 1]) + float(row[i + 3]) >= 1.0:
                                    positiveError = float(1 - float(row[i + 1]))
                                if float(row[i + 1]) - float(row[i + 3]) <= 0.0:
                                    negativeError = float(row[i + 1])
                                errorBarValuesPlus.append(positiveError)
                                errorBarValuesMinus.append(negativeError)
                            else:
                                errorBarValuesPlus.append('null')
                                errorBarValuesMinus.append('null')
                        break
            # Add the agent types data plots to the graph data.
            if i > 1:
                data.append(
                    py.graph_objs.Scatter(
                        x=days,
                        y=endOfDayAverages,
                        error_y=dict(
                            type='data',
                            symmetric=False,
                            array=errorBarValuesPlus,
                            arrayminus=errorBarValuesMinus,
                            thickness=2,
                            visible=True),
                        name=fieldNames[i],
                        line=dict(
                            color=colours[colour],
                            dash=lineTypes[lineType],
                            width=1,
                            shape='spline',
                        ),
                    )
                )
            else:
                data.append(
                    py.graph_objs.Scatter(
                        x=days,
                        y=endOfDayAverages,
                        name=fieldNames[i],
                        line=dict(
                            color=colours[colour],
                            dash=lineTypes[lineType],
                            width=1,
                            shape='spline',
                        ),
                    )
                )
            lineType += 1
            colour += 1
    # Style the graph layout
    layout: any = dict(
        title=dict(
            text='Average consumer satisfaction at the end of each day',
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
            size=16
        ),
    )

    # Create the graph and save the file
    fig: Dict[any, any] = dict(data=data, layout=layout)
    fullPath: str = os.path.join(baseOutputDirectory, convertedBaseFileName)
    py.io.write_image(fig, fullPath, format="png")
