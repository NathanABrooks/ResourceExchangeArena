import ast
import sys
import os
import re
import pandas as pd
from distutils.util import strtobool
from typing import List

""" Creates csv files summarising the satisfaction levels for each agent type when they make up 100% of the population.
This is completed for each simulation version.

Parameters
---------
folderName : str
    The output destination folder, used to organise output data.
exchangesArray : str
    Array of the various number of exchanges per day that were simulated.
    Note that this is immediately converted to type List[int].
daysOfInterest : str
    Array containing the days to be analysed.
    Note that this is immediately converted to type List[int].
"""

folderName: str = sys.argv[1]
exchangesArray: List[int] = ast.literal_eval(sys.argv[2])
daysOfInterest: List[int] = ast.literal_eval(sys.argv[3])

baseOutputDirectory: str = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(__file__))))), folderName)

resultDir = baseOutputDirectory + "/comparativeHeatMaps/data/"
# Create the output directories if they do not already exist.
if not os.path.exists(resultDir):
    os.makedirs(resultDir)

ex: List[int] = []
dy: List[int] = []
sel: List[float] = []
scSoc: List[float] = []
wscSoc: List[float] = []
sel_sd: List[float] = []
scSoc_sd: List[float] = []
wscSoc_sd: List[float] = []
scSoc_sel_dif: List[float] = []
wscSoc_sel_dif: List[float] = []
wscSoc_scSoc_dif: List[float] = []

for e in exchangesArray:
    selPath = baseOutputDirectory + "/useSC_false_AType_Selfish/EX_" + str(e) + "_SR_Selfish/data/endOfDayAverages.csv"
    scSocPath = baseOutputDirectory + "/useSC_true_AType_Social/EX_" + str(e) + "_SR_Social/data/endOfDayAverages.csv"
    wscSocPath = baseOutputDirectory + "/useSC_false_AType_Social/EX_" + str(e) + "_SR_Social/data/endOfDayAverages.csv"
    sel_df = pd.read_csv(selPath)
    scSoc_df = pd.read_csv(scSocPath)
    wscSoc_df = pd.read_csv(wscSocPath)
    for d in daysOfInterest:
        sel_df_day = sel_df.loc[sel_df['Day'] == d]
        scSoc_df_day = scSoc_df.loc[scSoc_df['Day'] == d]
        wscSoc_df_day = wscSoc_df.loc[wscSoc_df['Day'] == d]

        ex.append(e)
        dy.append(d)

        selfish = 0
        socialCapitalSocial = 0
        noSocialCapitalSocial = 0

        selfishSD = 0
        socialCapitalSocialSD = 0
        socialCapitalSocialSD = 0

        for i in sel_df_day.index:
            selfish = float(sel_df_day['Selfish'][i])
            selfishsd = float(sel_df_day['Selfish Standard Deviation'][i])

        for i in scSoc_df_day.index:
            socialCapitalSocial = float(scSoc_df_day['Social'][i])
            socialCapitalSocialSD = float(scSoc_df_day['Social Standard Deviation'][i])

        for i in wscSoc_df_day.index:
            noSocialCapitalSocial = float(wscSoc_df_day['Social'][i])
            noSocialCapitalSocialSD = float(wscSoc_df_day['Social Standard Deviation'][i])

        sel.append(selfish)
        scSoc.append(socialCapitalSocial)
        wscSoc.append(noSocialCapitalSocial)
        sel_sd.append(selfishSD)
        scSoc_sd.append(socialCapitalSocialSD)
        wscSoc_sd.append(noSocialCapitalSocialSD)

        soc_selfish_dif = socialCapitalSocial - selfish
        noSCsoc_selfish_dif = noSocialCapitalSocial - selfish
        noSC_sc_dif = noSocialCapitalSocial - socialCapitalSocial

        scSoc_sel_dif.append(soc_selfish_dif)
        wscSoc_sel_dif.append(noSCsoc_selfish_dif)
        wscSoc_scSoc_dif.append(noSC_sc_dif)

d = {'Exchanges':ex, 'Day':dy, 'Selfish':sel, 'SC_Social':scSoc, 'WSC_Social':wscSoc, 'Selfish_Standard_Deviation':sel_sd, 'SC_Social_Standard_Deviation':scSoc_sd, 'WSC_Social_Standard_Deviation':wscSoc_sd, 'SCSoc_Sel_Satisfaction_Difference':scSoc_sel_dif, 'WSCSoc_Sel_Satisfaction_Difference':wscSoc_sel_dif, 'WSCSoc_SCSoc_Satisfaction_Difference':wscSoc_scSoc_dif}
df = pd.DataFrame(data=d)
fn = resultDir + "/individual_populations_summary.csv"
df.to_csv(fn, index=False)