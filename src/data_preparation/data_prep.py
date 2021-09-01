""" Takes data copied from spreadsheets and formats them for use with the simulation."""

# Copy data into this string.
dataCopy: str = ""

preparedData:str = ' '.join(dataCopy.split()).replace(" ",",")

print(preparedData)