def rotate_odd_columns_up(input_path, output_path):
    # Read lines from input file and strip newline characters
    with open(input_path, 'r') as file:
        lines = [line.strip() for line in file.readlines()]

    # Convert lines to a grid of characters
    grid = [list(line) for line in lines]
    rows = len(grid)
    cols = len(grid[0]) if rows > 0 else 0

    # Rotate each odd-indexed column upwards by 1
    for col in range(1, cols, 2):  # 1, 3, 5, ...
        top = grid[0][col]
        for row in range(rows - 1):
            grid[row][col] = grid[row + 1][col]
        grid[rows - 1][col] = top  # wrap top to bottom

    # Write the result to the output file
    with open(output_path, 'w') as file:
        for row in grid:
            file.write(''.join(row) + '\n')

# Example usage
rotate_odd_columns_up('map/decor.txt', 'map/output.txt')
