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
dataFile: str = "/home/brooks/code/ResourceExchangeArena/results/RWD_small/useSC_true_AType_mixed/AE_100_SR_Selfish_Social/data/agentData.csv"
dataFile2: str = "/home/brooks/code/ResourceExchangeArena/results/RWD_large/useSC_true_AType_mixed/AE_100_SR_Selfish_Social/data/agentData.csv"

d1Size = 24
d2Size = 192

def Average(lst):
    return sum(lst) / len(lst)


# Average consumer satisfactions for each agent type for each day are visualised as a line graph.
# Hypothetical random and optimum allocations are also visualised.
with open(dataFile) as rawData:
    socCap = []

    reader = csv.reader(rawData)

    next(reader)

    day = 0
    tempSocCap = [0] * d1Size
    i = 0

    for row in reader:
        newDay = int(row[1])
        i = i+1

        if i == d1Size:
            i = 0

        if (newDay < day):
            day = 0
            socCap.extend(tempSocCap)
        else:
            day = newDay
        
        tempSocCap[i] = int(row[10])
    socCap.extend(tempSocCap)
    
    print(Average(socCap))

with open(dataFile2) as rawData:
    socCap = []

    reader = csv.reader(rawData)

    next(reader)

    day = 0
    tempSocCap = [0] * d2Size
    i = 0

    for row in reader:
        newDay = int(row[1])
        i = i+1

        if i == d2Size:
            i = 0

        if (newDay < day):
            day = 0
            socCap.extend(tempSocCap)
        else:
            day = newDay
        
        tempSocCap[i] = int(row[10])
    socCap.extend(tempSocCap)
    
    print(Average(socCap))
