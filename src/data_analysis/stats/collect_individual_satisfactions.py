import ast
import sys
import os
import re
import pandas as pd
from distutils.util import strtobool
from typing import List

""" Collects all the satiscfaction data for each agent from a version of the simulation
(Such as all runs with social capital enabled and a mixed population) into a single file for statistical significance testing.
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

baseOutputDirectory: str = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(__file__)))), folderName)

simulationVersions: List[str] = []
for it in os.scandir(baseOutputDirectory):
    if it.is_dir():
        simulationVersions.append(it.path)

for v in simulationVersions:
    if v.endswith('mixed'):
        socialCapital: bool = strtobool((v.split('useSC_')[1]).split('_')[0])
        for r in startingRatiosArray:

            sc: List[str] = []
            ln: List[int] = []
            ex: List[int] = []
            satis: List[float] = []
            a_t: List[int] = []
            day: List[int] = []

            for l in learningPercentages:
                for e in exchangesArray:
                    path = v + "/EX_" + str(e) +"_AE_" + str(l) + "_SR_" + r + "/data/endOfDaySatisfactions.csv"
                    df = pd.read_csv(path)
                    i_name = df[~df['Day'].isin(daysOfInterest)].index
                    df = df.drop(i_name)
                    for i in range(0, len(df.index)):
                        satis.append(df.iloc[i]['Satisfaction'])
                        a_t.append(df.iloc[i]['Agent Type'].astype(int))
                        day.append(df.iloc[i]['Day'].astype(int))
                        ln.append(l)
                        ex.append(e)
                        if socialCapital:
                            sc.append("Y")
                        else:
                            sc.append("N")


            d = {'Day':day, '%_Learning':ln, 'Agent Type':a_t, 'Exchanges':ex, 'Satisfaction':satis, 'Social_Capital':sc,}
            df = pd.DataFrame(data=d)
            dir = v + "/comparative/data/stats/" + r
            # Create the output directories if they do not already exist.
            if not os.path.exists(dir):
                os.makedirs(dir)
            fn = dir + "/SatisfactionBreakdown.csv"
            df.to_csv(fn, index=False)
