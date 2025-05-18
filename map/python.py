mapping = {
    "Bsb|": "#",
    "Bw/": "2",
    "Bw\\": "1",
    "Bw|": "|",
    "Edb": "D",
    "Eff": "/",
    "Efm": "F",
    "Em": "M",
    "Es": "S",
    "Ewf": "W",
    "Fds": "E",
    "Fms": "Z",
    "Fp": "T",
    "Gvs": "V",
    "Vc": "H",
    "Vct": "A",
    "Vh": "U",
    "Vhcr": "R",
    "Vhh": "I",
    "Vhhr": "J",
    "Vl": "C",
    "Wm": "X",
    "Xm": "L"
}

def convert_line(line):
    result = []
    tokens = [t.strip() for t in line.split(',')]
    for token in tokens:
        if '^' in token:
            parts = token.split('^')
            suffix = parts[1]
            result.append(mapping.get(suffix, '.'))
        else:
            result.append('.')
    return ''.join(result)

def process_file(input_path, output_path=None):
    with open(input_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()

    converted_lines = [convert_line(line) for line in lines]

    if output_path:
        with open(output_path, 'w', encoding='utf-8') as f:
            f.write('\n'.join(converted_lines))
    else:
        for line in converted_lines:
            print(line)

# Example usage
if __name__ == "__main__":
    process_file("map/maps.txt", "map/map_deco_converted.txt")
