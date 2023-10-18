import csv
import inflect
import os
import plotly as py
import sys

from typing import Any, Dict, List

""" Takes pre-prepared data from the SimulationVisualiserInitiator class and produces a line graphs demonstrating how agent satisfaction and population
varies throughout a typical day.

Parameters
---------
folderName : str
    The output destination folder, used to organise output data.
data : str
    The absolute path of the data set required for generating the line graph.
typicalSocial : str
    The typical social run to be visualised.
typicalSelfish : str
    The typical selfish run to be visualised.
"""

# Get the output folder from command line arguments.
folderName: str = sys.argv[1]

# Get the location of the raw data that requires visualising from command line arguments.
dataFile: str = sys.argv[2]

# Get the specific runs to be visualised.
typicalSocial: int = sys.argv[3]
typicalSelfish: int = sys.argv[4]

# Used to get ordinal word versions of integers for graph titles.
inflect = inflect.engine()

# Get the directory in which the generated graphs will be stored.
baseOutputDirectory: str = \
    os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(__file__)))),
                 folderName
                 + '/images')

# Create the output directory if it does not already exist.
if not os.path.exists(baseOutputDirectory):
    os.makedirs(baseOutputDirectory)

# Get suitable filenames format for the graphs that will be produced from existing raw data files.
baseFileName: str = dataFile.split('/')[-1]
convertedBaseFileName: str = baseFileName.split('dailyData.')[0] + 'typicalRuns.png'

# Store the scope of the data.
socialDays: List[str] = []
selfishDays: List[str] = []

# Average consumer satisfactions for each agent type for each day are visualised as a line graph.
# Hypothetical random and optimum allocations are also visualised.
with open(dataFile) as rawData:

    # Store calculated graph data
    data: Any = []

    reader = csv.reader(rawData)

    next(reader)
    for row in reader:
        if (float(row[0]) == float(typicalSocial)):
            socialDays.append(row[1])

    rawData.seek(0)
    next(reader)
    for row in reader:
        if (float(row[0]) == float(typicalSelfish)):
            selfishDays.append(row[1])

    totalDays: List[int] = []
    if len(socialDays) > len(selfishDays):
        totalDays = socialDays
    else:
        totalDays = selfishDays

    socialSat: List[float] = []
    selfishSat: List[float] = []

    rawData.seek(0)
    next(reader)

    if len(socialDays) > 0:
        for i in range(len(socialDays)):
            for row in reader:
                if (float(row[1]) == float(socialDays[i])) and (float(row[0]) == float(typicalSocial)):
                    socialSat.append(float(row[4]))
                    break

        socialSatTrend: List[float] = []
        for s in range(len(socialSat)):
            socialSatTemp: List[float] = []
            socialSatTemp.append(socialSat[s])
            if s+1 < len(socialSat):
                socialSatTemp.append(socialSat[s+1])
            if s+2 < len(socialSat):
                socialSatTemp.append(socialSat[s+2])
            if s-1 >= 0:
                socialSatTemp.append(socialSat[s-1])
            if s-2 >= 0:
                socialSatTemp.append(socialSat[s-2])
            temp = 0
            for t in range(len(socialSatTemp)):
                temp = temp + socialSatTemp[t]
            temp = temp / len(socialSatTemp)
            socialSatTrend.append(temp)


        data.append(
            py.graph_objs.Scatter(
                x=socialDays,
                y=socialSatTrend,
                name="Social",
                line=dict(
                    color="green",
                    dash="solid",
                    width=0.8,
                    shape='spline',
                    smoothing=1.3,
                ),
            )
        )
    rawData.seek(0)
    next(reader)

    if len(selfishDays) > 0:
        for i in range(len(selfishDays)):
            for row in reader:
                if (float(row[1]) == float(selfishDays[i])) and (float(row[0]) == float(typicalSelfish)):
                    selfishSat.append(float(row[5]))
                    break

        selfishSatTrend: List[float] = []
        for s in range(len(selfishSat)):
            selfishSatTemp: List[float] = []
            selfishSatTemp.append(selfishSat[s])
            if s+1 < len(selfishSat):
                selfishSatTemp.append(selfishSat[s+1])
            if s+2 < len(selfishSat):
                selfishSatTemp.append(selfishSat[s+2])
            if s-1 >= 0:
                selfishSatTemp.append(selfishSat[s-1])
            if s-2 >= 0:
                selfishSatTemp.append(selfishSat[s-2])
            temp = 0
            for t in range(len(selfishSatTemp)):
                temp = temp + selfishSatTemp[t]
            temp = temp / len(selfishSatTemp)
            selfishSatTrend.append(temp)

        data.append(
            py.graph_objs.Scatter(
                x=selfishDays,
                y=selfishSatTrend,
                name="Selfish",
                line=dict(
                    color="purple",
                    dash="solid",
                    width=0.8,
                    shape='spline',
                    smoothing=1.3,
                ),
            )
        )
            
    # Style the graph layout
    layout: any = dict(
        title=dict(
            text='Typical Simulation Runs',
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
            range=[totalDays[0], totalDays[-1]],
            tickmode='linear',
            tick0=-1,
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
            size=16
        ),
    )

    # Create the graph and save the file
    fig: Dict[any, any] = dict(data=data, layout=layout)
    fullPath: str = os.path.join(baseOutputDirectory, convertedBaseFileName)
    py.io.write_image(fig, fullPath, format="png")
