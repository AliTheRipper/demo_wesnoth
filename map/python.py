# Mapping dictionary
conversion = {
    "Ke": "K",
    "Khr": "C",
    "Ce": "V",
    "Chr": "R",
    "Chw": "S",
    "Ds": "B",
    "Gd": "Y",
    "Gg": "G",
    "Gll": "F",
    "Gs": "J",
    "Hh": "H",
    "Mm": "M",
    "Re": "D",
    "Rp": "P",
    "Ss": "A",
    "Wo": "O",
    "Ww": "W",
    "Wwf": "X",
    "Wwg": "E"
}

# Read from file
with open("map/maps.txt", "r") as f:
    content = f.read()

# Process the map (assumes comma-separated values)
tiles = [x.strip().split("^")[0] for x in content.replace("\n", ",").split(",") if x.strip()]
result = ""

# Convert each tile
for tile in tiles:
    result += conversion.get(tile, "?")  # "?" for unknown entries

# Write result to file
with open("converted_output.txt", "w") as f:
    f.write(result)

print("Conversion done. Output saved to converted_output.txt")
