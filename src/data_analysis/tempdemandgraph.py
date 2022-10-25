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
folderName: str = "/home/brooks/code/ResourceExchangeArena/results/"

# Unique identifier to identify which run the produced graphs are associated with.
tag: str = "AE_100_SR_Selfish_Social"

# Get the location of the raw data that requires visualising from command line arguments.
dataFile: str = "/home/brooks/code/ResourceExchangeArena/results/dailyData.csv"


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
convertedBaseFileName: str = baseFileName.split('dailyData.')[0] + 'test.png'

# Create figures with secondary y-axis
socFig = make_subplots(specs=[[{"secondary_y": False}]])

# Average consumer satisfactions for each agent type for each day are visualised as a line graph.
# Hypothetical random and optimum allocations are also visualised.

rwd  = [31.6,32.9,27.7,24.6,24.0,19.5,16.4,14.6,15.2,11.5,9.3,8.6,7.4,6.4,7.4,10.4,9.9,7.4,6.7,10.1,6.2,7.8,7.0,7.6,5.5,6.7,5.2,4.7,4.9,4.2,5.3,4.3,3.8,4.7,5.5,6.0,7.0,5.4,6.7,13.2,17.9,21.2,23.8,28.7,28.8,36.4,41.3,49.0,55.0,58.1,59.5,66.2,72.7,78.3,79.6,78.7,81.4,82.2,84.0,86.0,82.9,87.4,83.1,88.1,85.1,83.1,82.1,82.9,81.2,81.9,81.2,79.2,77.8,79.1,77.6,76.7,73.8,71.9,72.6,73.0,70.4,69.9,71.7,67.6,65.7,68.2,63.8,68.0,67.1,68.5,68.3,67.7,70.1,67.7,68.2,65.2,64.1,65.8,68.4,64.4,61.0,57.4,64.2,66.1,57.3,55.9,55.7,58.9,61.6,64.4,63.3,61.3,58.4,62.8,70.3,74.3,75.2,76.7,71.9,73.5,73.9,69.3,67.9,68.2,69.2,65.5,61.1,63.3,60.3,53.6,51.3,44.4,45.1,42.3,44.1,44.9,46.3,42.1,41.8,39.7,36.3,37.1,35.7,32.1] 
pen = [5.2,2.6,0.7,1.5,3.4,2.6,0.4,0.2,0.3,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.3,0.2,0.2,0.1,0.2,0.3,0.3,0.3,0.4,0.6,1.0,1.0,3.4,7.9,4.0,5.8,14.3,21.6,20.1,24.9,26.1,18.0,18.8,13.2,23.4,20.7,13.3,19.0,15.5,21.7,39.4,42.4,32.4,22.7,27.9,28.5,42.4,36.6,40.6,45.2,49.8,52.3,54.7,37.1,38.8,23.6,29.1,30.8,27.5,18.1,18.8,21.2,18.6,29.9,21.6,17.1,18.0,17.1,16.6,21.3,17.3,9.7,11.1,8.7,10.8,19.0,23.1,20.1,19.8,15.2,12.5,14.9,24.2,24.7,17.7,13.7,8.8,8.0,8.2,4.7,6.4,7.8,7.7,8.4,7.8,8.2,12.6,12.7,10.2,12.5,26.7,21.0,13.7,7.1,8.1,5.3,19.3,25.8,11.0,7.4,6.2,11.7,9.5,7.6,2.6,4.4,10.0,12.2,7.5,4.8,2.3,6.9,10.8,13.1,7.1,2.8,8.3,7.4,6.7,10.6,4.8]
non_pen = [8.1,10.2,7.8,7.0,13.3,14.9,12.4,11.0,10.7,8.8,6.7,7.8,10.0,8.2,20.3,36.0,29.9,26.8,21.6,46.0,23.7,19.5,13.7,7.5,6.1,5.6,7.3,8.0,5.3,4.0,3.4,4.5,2.8,6.6,8.0,3.2,8.1,7.2,3.7,5.2,7.9,18.0,16.8,22.4,19.3,18.3,17.2,13.8,21.3,22.9,19.0,32.4,31.6,25.7,23.0,22.6,29.9,30.4,27.3,33.7,27.2,29.0,28.1,31.7,35.5,27.9,21.1,22.3,24.0,21.4,16.7,14.4,21.1,20.3,21.6,22.4,16.7,20.3,12.1,8.6,19.7,24.3,20.3,17.4,13.2,21.3,21.6,16.7,14.4,18.7,20.9,16.6,10.0,7.2,7.3,9.1,10.8,14.3,19.2,18.4,25.8,16.3,14.0,18.2,12.7,17.2,20.6,17.8,24.2,30.3,32.2,24.4,15.6,15.9,17.5,19.5,29.5,24.6,16.3,26.0,20.8,19.2,21.1,27.6,21.5,27.0,24.9,37.4,25.7,29.0,21.1,14.1,21.0,17.6,21.1,15.0,9.7,6.7,9.3,6.8,11.0,11.2,11.2,6.9]
flat = [1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0]

def compact(list):
    new = []
    temp = 0
    nums = 0
    for i in list:
        temp = temp + i
        nums = nums + 1
        if nums == 6:
            new.append(temp)
            nums = 0
            temp = 0
    return new

rwd2 = compact(rwd)
pen2 = compact(pen)
non_pen2 = compact(non_pen)
flat2 = compact(flat)

rwd3 = [float(i)/max(rwd2) for i in rwd2]
pen3 = [float(i)/max(pen2) for i in pen2]
non_pen3 = [float(i)/max(non_pen2) for i in non_pen2]
flat3 = [(float(i)/max(flat2)) / 2 for i in flat2]

print(rwd3)
print(pen3)
print(non_pen3)
print(flat3)

range2 = list(range(24))

socFig.add_trace(
            go.Scatter(
                x=range2,
                y=rwd3,
                name="Switchable Appliances",
                line=dict(
                    color="green",
                    dash="solid",
                    width=0.8,
                ),
            ),
        )
        
socFig.add_trace(
            go.Scatter(
                x=range2,
                y=pen3,
                name="Single Pensioners",
                line=dict(
                    color="purple",
                    dash="solid",
                    width=0.8,
                ),
            ),
        )

socFig.add_trace(
            go.Scatter(
                x=range2,
                y=non_pen3,
                name="Single Non-Pensioners",
                line=dict(
                    color="black",
                    dash="solid",
                    width=1,
                ),
            ),
        )

socFig.add_trace(
            go.Scatter(
                x=range2,
                y=flat3,
                name="Flat Demand",
                line=dict(
                    color="black",
                    dash="dot",
                    width=1,
                ),
            ),
        )
        

        # Style the graph layout
socFig.update_layout(
            title=dict(
                text='Normalised demand curves',
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
            legend_font_size=14
        )

socFig.update_xaxes(
            title_text='Hour',
            showline=True,
            linecolor='black',
            linewidth=1,
            gridcolor='rgb(225, 225, 225)',
            gridwidth=1,
            tickmode='linear',
            tick0=0,
            dtick=4,
            range=(0,24)
        )

socFig.update_yaxes(
            title_text='Demand',
            showline=True,
            linecolor='black',
            linewidth=1,
            gridcolor='rgb(225, 225, 225)',
            gridwidth=1,
            tickmode='linear',
            tick0=0,
            dtick=0.1,
            range=(0,1)
        )
    
fullPath: str = os.path.join(baseOutputDirectory, convertedBaseFileName)
py.io.write_image(socFig, fullPath, format="png")
