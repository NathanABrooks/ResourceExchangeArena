import csv
import inflect
import os
import sys
import plotly as py
import plotly.graph_objects as go
from plotly.subplots import make_subplots

from typing import Any, Dict, List

""" Takes pre-prepared data from the SimulationVisualiserInitiator class and produces a line graphs demonstrating how agent satisfaction and population
varies throughout a typical day.

Parameters
---------
folderName : str
    The output destination folder, used to organise output data.
tag : str
    A unique tag so that generated graphs can easily be associated with their corresponding data sets.
data : str
    The absolute path of the data set required for generating the line graph.
typicalSocial : str
    The typical social run to be visualised.
typicalSelfish : str
    The typical selfish run to be visualised.
"""

# Get the output folder from command line arguments.
folderName: str = "/home/brooks/code/ResourceExchangeArena/results/RWD_mixed_05/RWD_mixed_05"

# Unique identifier to identify which run the produced graphs are associated with.
tag: str = "AE_100_SR_Selfish_Social"

# Get the location of the raw data that requires visualising from command line arguments.
dataFile: str = "/home/brooks/code/ResourceExchangeArena/results/RWD_mixed_05/RWD_mixed_05/AE_100_SR_Selfish_Social/data/dailyData.csv"

# Get the specific runs to be visualised.
typicalSocial: int = 8
typicalSelfish: int = 19

popSize: int = 96

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
baseFileName: str = dataFile.split('/')[-1]
convertedBaseFileName: str = baseFileName.split('dailyData.')[0] + 'typicalRuns2soc.png'
convertedBaseFileName2: str = baseFileName.split('dailyData.')[0] + 'typicalRuns2sel.png'

# Create figures with secondary y-axis
socFig = make_subplots(specs=[[{"secondary_y": True}]])
selFig = make_subplots(specs=[[{"secondary_y": True}]])

# Store the scope of the data.
socialDays: List[str] = []
selfishDays: List[str] = []

# Average consumer satisfactions for each agent type for each day are visualised as a line graph.
# Hypothetical random and optimum allocations are also visualised.
with open(dataFile) as rawData:
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

    rawData.seek(0)
    next(reader)

    if len(socialDays) > 0:

        socialSat: List[float] = []
        selfishSat: List[float] = []
        socialPop: List[float] = []
        selfishPop: List[float] = []

        randomSat: List[float] = []
        optimalSat: List[float] = []

        for i in range(len(socialDays)):
            for row in reader:
                if (float(row[1]) == float(socialDays[i])) and (float(row[0]) == float(typicalSocial)):
                    socialSat.append(float(row[4]))
                    selfishSat.append(float(row[5]))
                    socialPop.append(float(row[2]))
                    selfishPop.append(float(row[3]))

                    randomSat.append(float(row[20]))
                    optimalSat.append(float(row[21]))
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

        socialPopTrend: List[float] = []
        for s in range(len(socialPop)):
            socialPopTemp: List[float] = []
            socialPopTemp.append(socialPop[s])
            if s+1 < len(socialPop):
                socialPopTemp.append(socialPop[s+1])
            if s+2 < len(socialPop):
                socialPopTemp.append(socialPop[s+2])
            if s-1 >= 0:
                socialPopTemp.append(socialPop[s-1])
            if s-2 >= 0:
                socialPopTemp.append(socialPop[s-2])
            temp = 0
            for t in range(len(socialPopTemp)):
                temp = temp + socialPopTemp[t]
            temp = temp / len(socialPopTemp)
            socialPopTrend.append(temp)

        selfishPopTrend: List[float] = []
        for s in range(len(selfishPop)):
            selfishPopTemp: List[float] = []
            selfishPopTemp.append(selfishPop[s])
            if s+1 < len(selfishPop):
                selfishPopTemp.append(selfishPop[s+1])
            if s+2 < len(selfishPop):
                selfishPopTemp.append(selfishPop[s+2])
            if s-1 >= 0:
                selfishPopTemp.append(selfishPop[s-1])
            if s-2 >= 0:
                selfishPopTemp.append(selfishPop[s-2])
            temp = 0
            for t in range(len(selfishPopTemp)):
                temp = temp + selfishPopTemp[t]
            temp = temp / len(selfishPopTemp)
            selfishPopTrend.append(temp)

        randomSatTrend: List[float] = []
        for s in range(len(randomSat)):
            randomSatTemp: List[float] = []
            randomSatTemp.append(randomSat[s])
            if s+1 < len(randomSat):
                randomSatTemp.append(randomSat[s+1])
            if s+2 < len(randomSat):
                randomSatTemp.append(randomSat[s+2])
            if s-1 >= 0:
                randomSatTemp.append(randomSat[s-1])
            if s-2 >= 0:
                randomSatTemp.append(randomSat[s-2])
            temp = 0
            for t in range(len(randomSatTemp)):
                temp = temp + randomSatTemp[t]
            temp = temp / len(randomSatTemp)
            randomSatTrend.append(temp)

        optimalSatTrend: List[float] = []
        for s in range(len(optimalSat)):
            optimalSatTemp: List[float] = []
            optimalSatTemp.append(optimalSat[s])
            if s+1 < len(optimalSat):
                optimalSatTemp.append(optimalSat[s+1])
            if s+2 < len(optimalSat):
                optimalSatTemp.append(optimalSat[s+2])
            if s-1 >= 0:
                optimalSatTemp.append(optimalSat[s-1])
            if s-2 >= 0:
                optimalSatTemp.append(optimalSat[s-2])
            temp = 0
            for t in range(len(optimalSatTemp)):
                temp = temp + optimalSatTemp[t]
            temp = temp / len(optimalSatTemp)
            optimalSatTrend.append(temp)


        socFig.add_trace(
            go.Scatter(
                x=socialDays,
                y=socialSatTrend,
                name="Social Satisfaction",
                legendgroup="satisfaction",
                line=dict(
                    color="green",
                    dash="solid",
                    width=0.8,
                    shape='spline',
                    smoothing=1.3,
                ),
            ),
            secondary_y=False,
        )
        
        socFig.add_trace(
            go.Scatter(
                x=socialDays,
                y=selfishSatTrend,
                name="Selfish Satisfaction",
                legendgroup="satisfaction",
                line=dict(
                    color="purple",
                    dash="solid",
                    width=0.8,
                    shape='spline',
                    smoothing=1.3,
                ),
            ),
            secondary_y=False,
        )

        socFig.add_trace(
            go.Scatter(
                x=socialDays,
                y=socialPopTrend,
                name="Social Population",
                legendgroup="population",
                line=dict(
                    color="green",
                    dash="dot",
                    width=1,
                    shape='spline',
                    smoothing=1.3,
                ),
            ),
            secondary_y=True,
        )

        socFig.add_trace(
            go.Scatter(
                x=socialDays,
                y=selfishPopTrend,
                name="Selfish Population",
                legendgroup="population",
                line=dict(
                    color="purple",
                    dash="dot",
                    width=1,
                    shape='spline',
                    smoothing=1.3,
                ),
            ),
            secondary_y=True,
        )
        
        socFig.add_trace(
            go.Scatter(
                x=socialDays,
                y=optimalSatTrend,
                name="Optimal Satisfaction",
                legendgroup="satisfaction",
                line=dict(
                    color="black",
                    dash="solid",
                    width=0.8,
                    shape='spline',
                    smoothing=1.3,
                ),
            ),
            secondary_y=False,
        )

        socFig.add_trace(
            go.Scatter(
                x=socialDays,
                y=randomSatTrend,
                name="Random Satisfaction",
                legendgroup="satisfaction",
                line=dict(
                    color="black",
                    dash="dot",
                    width=1,
                    shape='spline',
                    smoothing=1.3,
                ),
            ),
            secondary_y=False,
        )

        # Style the graph layout
        socFig.update_layout(
            title=dict(
                text='Typical Social Run',
                xanchor='center',
                x=0.5,
            ),
            paper_bgcolor='rgb(255, 255, 255)',
            plot_bgcolor='rgb(255, 255, 255)',
            font=dict(
                size=16
            ),
            margin=dict(
                l=30,
                r=60,
                b=30,
                t=60,
            ),
            showlegend=True,
            legend_font_size=16
        )

        socFig.update_xaxes(
            title_text='Day',
            showline=True,
            linecolor='black',
            linewidth=1,
            gridcolor='rgb(225, 225, 225)',
            gridwidth=1,
            range=[socialDays[0], socialDays[-1]],
            tickmode='linear',
            tick0=-1,
            dtick=50,
        )

        socFig.update_yaxes(
            title_text='Average Consumer Satisfaction',
            showline=True,
            linecolor='black',
            linewidth=1,
            gridcolor='rgb(225, 225, 225)',
            gridwidth=1,
            range=[0, 1],
            tickmode='linear',
            tick0=0,
            dtick=0.25,
            secondary_y=False
        )

        socFig.update_yaxes(
            title_text='',
            showline=True,
            linecolor='black',
            linewidth=1,
            gridcolor='rgb(225, 225, 225)',
            gridwidth=1,
            range=[0, popSize],
            tickmode='linear',
            tick0=0,
            dtick=popSize/4,
            secondary_y=True
        )
    
        fullPath: str = os.path.join(baseOutputDirectory, convertedBaseFileName)
        py.io.write_image(socFig, fullPath, format="png")

        rawData.seek(0)
        next(reader)

    if len(selfishDays) > 0:

        socialSat: List[float] = []
        selfishSat: List[float] = []
        socialPop: List[float] = []
        selfishPop: List[float] = []

        randomSat: List[float] = []
        optimalSat: List[float] = []

        for i in range(len(selfishDays)):
            for row in reader:
                if (float(row[1]) == float(selfishDays[i])) and (float(row[0]) == float(typicalSelfish)):
                    socialSat.append(float(row[4]))
                    selfishSat.append(float(row[5]))
                    socialPop.append(float(row[2]))
                    selfishPop.append(float(row[3]))

                    randomSat.append(float(row[20]))
                    optimalSat.append(float(row[21]))
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

        socialPopTrend: List[float] = []
        for s in range(len(socialPop)):
            socialPopTemp: List[float] = []
            socialPopTemp.append(socialPop[s])
            if s+1 < len(socialPop):
                socialPopTemp.append(socialPop[s+1])
            if s+2 < len(socialPop):
                socialPopTemp.append(socialPop[s+2])
            if s-1 >= 0:
                socialPopTemp.append(socialPop[s-1])
            if s-2 >= 0:
                socialPopTemp.append(socialPop[s-2])
            temp = 0
            for t in range(len(socialPopTemp)):
                temp = temp + socialPopTemp[t]
            temp = temp / len(socialPopTemp)
            socialPopTrend.append(temp)

        selfishPopTrend: List[float] = []
        for s in range(len(selfishPop)):
            selfishPopTemp: List[float] = []
            selfishPopTemp.append(selfishPop[s])
            if s+1 < len(selfishPop):
                selfishPopTemp.append(selfishPop[s+1])
            if s+2 < len(selfishPop):
                selfishPopTemp.append(selfishPop[s+2])
            if s-1 >= 0:
                selfishPopTemp.append(selfishPop[s-1])
            if s-2 >= 0:
                selfishPopTemp.append(selfishPop[s-2])
            temp = 0
            for t in range(len(selfishPopTemp)):
                temp = temp + selfishPopTemp[t]
            temp = temp / len(selfishPopTemp)
            selfishPopTrend.append(temp)

        randomSatTrend: List[float] = []
        for s in range(len(randomSat)):
            randomSatTemp: List[float] = []
            randomSatTemp.append(randomSat[s])
            if s+1 < len(randomSat):
                randomSatTemp.append(randomSat[s+1])
            if s+2 < len(randomSat):
                randomSatTemp.append(randomSat[s+2])
            if s-1 >= 0:
                randomSatTemp.append(randomSat[s-1])
            if s-2 >= 0:
                randomSatTemp.append(randomSat[s-2])
            temp = 0
            for t in range(len(randomSatTemp)):
                temp = temp + randomSatTemp[t]
            temp = temp / len(randomSatTemp)
            randomSatTrend.append(temp)

        optimalSatTrend: List[float] = []
        for s in range(len(optimalSat)):
            optimalSatTemp: List[float] = []
            optimalSatTemp.append(optimalSat[s])
            if s+1 < len(optimalSat):
                optimalSatTemp.append(optimalSat[s+1])
            if s+2 < len(optimalSat):
                optimalSatTemp.append(optimalSat[s+2])
            if s-1 >= 0:
                optimalSatTemp.append(optimalSat[s-1])
            if s-2 >= 0:
                optimalSatTemp.append(optimalSat[s-2])
            temp = 0
            for t in range(len(optimalSatTemp)):
                temp = temp + optimalSatTemp[t]
            temp = temp / len(optimalSatTemp)
            optimalSatTrend.append(temp)

        selFig.add_trace(
            go.Scatter(
                x=selfishDays,
                y=socialSatTrend,
                name="Social Satisfaction",
                line=dict(
                    color="green",
                    dash="solid",
                    width=0.8,
                    shape='spline',
                    smoothing=1.3,
                ),
            ),
            secondary_y=False,
        )
        
        selFig.add_trace(
            go.Scatter(
                x=selfishDays,
                y=selfishSatTrend,
                name="Selfish Satisfaction",
                line=dict(
                    color="purple",
                    dash="solid",
                    width=0.8,
                    shape='spline',
                    smoothing=1.3,
                ),
            ),
            secondary_y=False,
        )

        selFig.add_trace(
            go.Scatter(
                x=selfishDays,
                y=socialPopTrend,
                name="Social Population",
                line=dict(
                    color="green",
                    dash="dot",
                    width=1,
                    shape='spline',
                    smoothing=1.3,
                ),
            ),
            secondary_y=True,
        )

        selFig.add_trace(
            go.Scatter(
                x=selfishDays,
                y=selfishPopTrend,
                name="Selfish Population",
                line=dict(
                    color="purple",
                    dash="dot",
                    width=1,
                    shape='spline',
                    smoothing=1.3,
                ),
            ),
            secondary_y=True,
        )
        
        selFig.add_trace(
            go.Scatter(
                x=selfishDays,
                y=optimalSatTrend,
                name="Optimal Satisfaction",
                line=dict(
                    color="black",
                    dash="solid",
                    width=0.8,
                    shape='spline',
                    smoothing=1.3,
                ),
            ),
            secondary_y=False,
        )

        selFig.add_trace(
            go.Scatter(
                x=selfishDays,
                y=randomSatTrend,
                name="Random Satisfaction",
                line=dict(
                    color="black",
                    dash="dot",
                    width=1,
                    shape='spline',
                    smoothing=1.3,
                ),
            ),
            secondary_y=False,
        )

        # Style the graph layout
        selFig.update_layout(
            title=dict(
                text='Typical Selfish Run',
                xanchor='center',
                x=0.5,
            ),
            paper_bgcolor='rgb(255, 255, 255)',
            plot_bgcolor='rgb(255, 255, 255)',
            font=dict(
                size=16
            ),
            margin=dict(
                l=30,
                r=60,
                b=30,
                t=60,
            ),
            showlegend=False,
            legend_font_size=14
        )

        selFig.update_xaxes(
            title_text='Day',
            showline=True,
            linecolor='black',
            linewidth=1,
            gridcolor='rgb(225, 225, 225)',
            gridwidth=1,
            range=[selfishDays[0], selfishDays[-1]],
            tickmode='linear',
            tick0=-1,
            dtick=50,
        )

        selFig.update_yaxes(
            title_text='Average Consumer Satisfaction',
            showline=True,
            linecolor='black',
            linewidth=1,
            gridcolor='rgb(225, 225, 225)',
            gridwidth=1,
            range=[0, 1],
            tickmode='linear',
            tick0=0,
            dtick=0.25,
            secondary_y=False
        )

        selFig.update_yaxes(
            title_text='Population Size',
            showline=True,
            linecolor='black',
            linewidth=1,
            gridcolor='rgb(225, 225, 225)',
            gridwidth=1,
            range=[0, popSize],
            tickmode='linear',
            tick0=0,
            dtick=popSize/4,
            secondary_y=True
        )
    
        fullPath: str = os.path.join(baseOutputDirectory, convertedBaseFileName2)
        py.io.write_image(selFig, fullPath, format="png")
        