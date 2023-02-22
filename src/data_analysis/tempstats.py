import csv
import inflect
import os
import sys
import scipy.stats as stats

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

# Get the location of the raw data that requires visualising from command line arguments.
dataFile: str = "/home/brooks/code/ResourceExchangeArena/results/Base/useSC_true_AType_Social/SR_Social/data/dailyData.csv"
dataFile2: str = "/home/brooks/code/ResourceExchangeArena/results/Base/useSC_false_AType_Selfish/SR_Selfish/data/dailyData.csv"

# Store the scope of the data.
socialSat: List[float] = []
selfishSat: List[float] = []

def Average(lst):
    return sum(lst) / len(lst)

targetDay = 9999

# Average consumer satisfactions for each agent type for each day are visualised as a line graph.
# Hypothetical random and optimum allocations are also visualised.
with open(dataFile) as rawData:
    reader = csv.reader(rawData)

    next(reader)
    for row in reader:
        if (float(row[3]) == float(0) and targetDay == 9999):
            targetDay = int(row[1]) + 98
        if (int(row[1]) == targetDay):
            socialSat.append(float(row[4]))
            targetDay = 9999

with open(dataFile2) as rawData:
    reader = csv.reader(rawData)

    next(reader)
    for row in reader:
        if (int(row[1]) == 1):
            selfishSat.append(float(row[5]))

print(Average(selfishSat))
print(Average(socialSat))

selfishSat.sort()
socialSat.sort()

print(selfishSat)
print(socialSat)

print(stats.mannwhitneyu(socialSat, selfishSat))
