def process_file(input_file, output_file):
    # Mapping dictionary
    mapping = {
        "Bsb": "=", "Bw/": "2", "Bw\\": "1", "Bw|": "|", "Edb": "D", "Eff": "/",
        "Efm": "F", "Em": "M", "Es": "S", "Ewf": "W", "Fds": "E", "Fms": "Z",
        "Fp": "T", "Gvs": "V", "Vc": "H", "Vct": "A", "Vh": "U", "Vhcr": "R",
        "Vhh": "I", "Vhhr": "J", "Vl": "C", "Wm": "X", "Xm": "L"
    }

    with open(input_file, 'r') as fin, open(output_file, 'w') as fout:
        for line in fin:
            items = [item.strip() for item in line.strip().split(',')]
            processed = []
            for item in items:
                if '^' not in item:
                    processed.append('.')
                else:
                    code = item.split('^')[1]
                    processed.append(mapping.get(code, '?'))
            # <-- c'était manquant :
            fout.write(''.join(processed) + '\n')

# Example usage:
process_file('map/maps.txt', 'map/decoroutput.txt')
