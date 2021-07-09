import ast
import sys
import os
import re
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
from typing import List

""" Creates heat maps comparing satisfaction levels with and without social capital.
This is completed for each simulation version.

Parameters
---------
folderName : str
    The output destination folder, used to organise output data.
learningPercentages : str
    Array of the percentage of Agents that possibly used social Learning per day.
    Note that this is immediately converted to type List[int].
exchangesArray : str
    Array of the various number of exchanges per day that were simulated.
    Note that this is immediately converted to type List[int].
startingRatiosArray : str[]
    Array of the various agent type starting ratios that were simulated.
daysOfInterest : str
    Array containing the days to be analysed.
    Note that this is immediately converted to type List[int].
"""

folderName: str = sys.argv[1]
learningPercentages: List[int] = ast.literal_eval(sys.argv[2])
exchangesArray: List[int] = ast.literal_eval(sys.argv[3])

temp: str = re.sub('\[|\]| ', '', sys.argv[4])
startingRatiosArray: List[str] = list(temp.split(","))

daysOfInterest: List[int] = ast.literal_eval(sys.argv[5])

baseOutputDirectory: str = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(__file__))))), folderName)

dataDir = baseOutputDirectory + "/comparativeHeatMaps/data/"
imageDir = baseOutputDirectory + "/comparativeHeatMaps/images/"

# Create the output directories if they do not already exist.
if not os.path.exists(imageDir):
    os.makedirs(imageDir)

for r in startingRatiosArray:

    #reads a .csv I created to provide an overview of mean Popultion Satisfaction for runs with and without social captial
    df = pd.read_csv(dataDir + r + "_comp_mean_satisfaction.csv")

    #rounds all value to 2 decimal places (for the sake of the heatmap visualisation
    df = df.round(2)

    i_name = df[(~df['Day'].isin(daysOfInterest)) | (~df['Exchanges'].isin(exchangesArray))].index
    df = df.drop(i_name)

    subplots = []
    for l in learningPercentages:
        #create a series of sub_dataframes for each desired learning %, limit dataframe to desired days and exchanges.
        df_l = df.loc[df['%_Learning'] == l]

        #create pivot tables in order to be able to create heatmaps. 
        #Pivot for the difference between selfish and social (see csv for reference)
        df_l_pivot = df_l.pivot("Day", "Exchanges", "Difference")
        subplots.append(df_l_pivot)


    columns = len(learningPercentages)
    fig, axs = plt.subplots(nrows=1, ncols=columns, sharex='col', sharey='row')

    for index, s in enumerate(subplots):
        #seaborn heatmap per pivot table
        sns.heatmap(s, cmap="PuOr", center= 0, vmin=-0.4, vmax=0.4, ax=axs[index], linewidths=0.1, linecolor="white", cbar=True, annot=True, annot_kws={"size": 10})
        title: str = "Learning " + str(learningPercentages[index]) + "%"
        axs[index].set_title(title, fontsize=12)
        axs[index].invert_yaxis()
        axs[index].set_ylabel('')
        axs[index].set_xlabel('')

    #adjust position of subplot
    plt.subplots_adjust(hspace = .2)
    plt.subplots_adjust(wspace = .2)
    plt.subplots_adjust(top = .8)
    plt.subplots_adjust(bottom = .2)
    plt.subplots_adjust(left = .12)
    plt.subplots_adjust(right = .95)

    #set x and y axis labels and plot title
    fig.text(0.5, 0.02, 'Exchanges', ha='center', fontsize=14)
    fig.text(0.04, 0.5, 'Day', va='center', rotation='vertical', fontsize=14)
    fig.suptitle('With and Without Social Capital Comparisons', fontsize=14)

    fname = imageDir + r + "_social_capital_comparison"

    plt.savefig(fname,
        dpi=None,
        facecolor='w',
        edgecolor='w',
        orientation='portrait',
        format=None,
        transparent=False,
        bbox_inches=None,
        pad_inches=0.1,
        metadata=None)
