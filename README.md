# seamcarver

## Algorithm Description for Finding a Seam

I implemented a dynamic programming approach to identify vertical seams in an image. The process involves:

1. **Initialization**: Two matrices are created:
   - `energyGrid`: Stores the energy value of each pixel.
   - `distTo`: Tracks the minimum cumulative energy from any pixel in the top row to the pixel at position (x, y).

2. **Dynamic Programming**:
   - Start with the top row as the base case, where `distTo` values equal the corresponding `energyGrid` values.
   - For each subsequent row, compute the `distTo` value for a pixel by:
     - Considering the three adjacent pixels in the row above (columns x-1, x, x+1).
     - Taking the minimum of these three values and adding the current pixel’s energy.
   - Continue this process downward to fill the `distTo` matrix.

3. **Seam Identification**:
   - Locate the minimum energy value in the bottom row of `distTo` and add its column index to a stack.
   - Backtrack upward, at each row selecting the column (from the three above) with the minimum cumulative energy, adding it to the stack.
   - Pop the stack to construct the vertical seam array.

4. **Horizontal Seam**:
   - Transpose the image, apply the vertical seam algorithm, then transpose back.

## Suitability for Seam Carving

### Suitable Images
Images with large, uniform backgrounds (e.g., sky, ocean) work well. These areas have low contrast, so removing seams minimally affects the image’s content and structure, avoiding noticeable visual artifacts.

### Unsuitable Images
Images like human portraits are less suitable. High contrast and detailed contours (e.g., facial features) mean seam removal can distort critical structures, introducing significant visual artifacts.

## Performance Analysis

I conducted experiments to estimate the running time for reducing an image by one column and one row (calling `findVerticalSeam()`, `removeVerticalSeam()`, `findHorizontalSeam()`, and `removeHorizontalSeam()`). The "doubling" hypothesis was tested by increasing either width (W) or height (H) by a factor of 2.

### Experiment 1: Fixed W = 2000, Varying H
| H       | Time (seconds) | Ratio | Log Ratio |
|---------|----------------|-------|-----------|
| 1000    | 0.319          | n/a   | n/a       |
| 2000    | 0.596          | 1.868 | 0.901     |
| 4000    | 1.252          | 2.101 | 1.071     |
| 8000    | 2.509          | 2.003 | 1.002     |
| 16000   | 5.161          | 2.057 | 1.041     |
| 32000   | 10.203         | 1.977 | 0.983     |
| 64000   | 20.137         | 1.974 | 0.981     |
| 128000  | 40.351         | 2.004 | 1.003     |

### Experiment 2: Fixed H = 2000, Varying W
| W       | Time (seconds) | Ratio | Log Ratio |
|---------|----------------|-------|-----------|
| 1000    | 0.317          | n/a   | n/a       |
| 2000    | 0.611          | 1.927 | 0.946     |
| 4000    | 1.157          | 1.894 | 0.921     |
| 8000    | 2.542          | 2.197 | 1.136     |
| 16000   | 5.082          | 1.999 | 0.999     |
| 32000   | 10.174         | 2.002 | 1.001     |
| 64000   | 20.029         | 1.969 | 0.977     |
| 128000  | 39.985         | 1.996 | 0.997     |

## Running Time Formula

Using the empirical data, the running time (in seconds) to find and remove one horizontal and one vertical seam is approximated as:

~ 1.6e-07 * W * H


### Derivation
- **Exponent**: The average log ratios in both tables converge to ~1, indicating linear dependence on both W and H (time doubles as W or H doubles).
- **Coefficient**: Using the data point (W = 64000, H = 2000, time = 20.029), solve `T = a * W * H`:
  - `20.029 = a * 64000 * 2000`
  - `a = 20.029 / (64000 * 2000) ≈ 1.6e-07`

This formula, in tilde notation, captures the leading term of the running time as a function of image dimensions W and H.
