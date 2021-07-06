import ast
import re
import sys
import pandas as pd
import os
from scipy.stats import ttest_ind
from scipy.stats import mannwhitneyu
from typing import List

""" Performs statistical significance testing from the data previously summarised.

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
resultDir = baseOutputDirectory + "/statsTestResults/"
# Create the output directories if they do not already exist.
if not os.path.exists(resultDir):
    os.makedirs(resultDir)

for r in startingRatiosArray:
    dfSC = pd.read_csv(baseOutputDirectory + "/useSC_true_AType_mixed/comparative/data/stats/" + r + "/SatisfactionBreakdown.csv")
    dfWSC = pd.read_csv(baseOutputDirectory + "/useSC_false_AType_mixed/comparative/data/stats/" + r + "/SatisfactionBreakdown.csv")
    outputPath: str = resultDir + "/SR_" + r + "_results.txt"
    f = open(outputPath , "w")
    sc_df = {}
    wsc_df= {}
    for l in learningPercentages:
        for e in exchangesArray:
            for d in daysOfInterest:
                f.write("%_Learning=" + str(l) + "  Exchanges=" + str(e) + "  Day=" + str(d) + "\n ")
                sc_df = dfSC.loc[(dfSC['%_Learning'] == l)]
                sc_df = sc_df.loc[(sc_df['Exchanges'] == e)]
                sc_df = sc_df.loc[(sc_df['Day'] == d)]
                wsc_df = dfWSC.loc[(dfWSC['%_Learning'] == l)]
                wsc_df = wsc_df.loc[(wsc_df['Exchanges'] == e)]
                wsc_df = wsc_df.loc[(wsc_df['Day'] == d)]

                f.write("Social Capital: mean=" + str(sc_df['Satisfaction'].mean()) + "  variance=" + str(sc_df['Satisfaction'].var()) + "  median=" + str(sc_df['Satisfaction'].median()) + "\n ")
                f.write("Without Social Capital: mean=" + str(wsc_df['Satisfaction'].mean()) + "  variance=" + str(wsc_df['Satisfaction'].var()) + "  median=" + str(wsc_df['Satisfaction'].median()) + "\n ")

                p_t = ttest_ind(sc_df['Satisfaction'], wsc_df['Satisfaction'], equal_var=False)
                p_m = mannwhitneyu(sc_df['Satisfaction'], wsc_df['Satisfaction'])

                f.write("T-test: Pass(p<0.01)=")
                if p_t[1] <0.01:
                    f.write("True")
                else:
                    f.write("False")
                f.write("  p-value=" + str(p_t[1]) + "  t-statistic=" + str(p_t[0])+ "\n ")

                f.write("Mann-Whitney U: Pass(p<0.01)=")
                if p_m[1] <0.01:
                    f.write("True")
                else:
                    f.write("False")
                f.write("  p-value=" + str(p_m[1]) + "  statistic=" + str(p_m[0])+ "\n \n ")
    f.close
