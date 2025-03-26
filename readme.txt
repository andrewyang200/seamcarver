Programming Assignment 7: Seam Carving


/* *****************************************************************************
 *  Describe concisely your algorithm to find a horizontal (or vertical)
 *  seam.
 **************************************************************************** */
I used the dynamic programming approach to find the vertical seam. First I created
two matrixes: energyGrid and distTo. energyGrid tracks the energy value of each
individual pixel. Using that, I then used dynamic programming to compute the distTo
matrix, which tracked the minimum energy value of pixel (x,y) from starting from
any pixel in the top row. The basic idea is to start with the base case which is
just the top row which with each of their respective energy pixel. Then starting
with the next row down, we find the three adjacent pixels in the row above in col
x-1, x, and x+1. We take the minimum value of the three and then add the energy
value of the current pixel to get the cumulative minimum energy level of that pixel.
The same design continues downward to the last to until we have filled up the entire
distTo matrix.

To then find the vertical seam, I found the minimum energy level of the last row
and stored that value in a stack. Afterward, I looked at the three adjecent columns
in the row above and found the column with the minimum cimulative value and added
that to the stack. I kept doing this all the way up to the top row. Then by popping
the values from the stack, I obtained the array for the vertical seam.

To find the horizontal seam, I just transposed the picture, called find vertical
seam, and then transposed the picture back.


/* *****************************************************************************
 *  Describe what makes an image suitable to the seam-carving approach
 *  (in terms of preserving the content and structure of the original
 *  image, without introducing visual artifacts). Describe an image that
 *  would not work well.
 **************************************************************************** */
A suitable image would be one with a lot of background such as the sky or ocean.
Because there is not much contrast in these background regions, removing a seam
would not disturb or introduce a lot of visual artifacts.

A not so suitable image would be one of a human portrait. This is because there is
a lot of contrast and contour in a human face that when you apply seam-carving, it
will distrupt the structure of the original image.

/* *****************************************************************************
 *  Perform computational experiments to estimate the running time to reduce
 *  a W-by-H image by one column and one row (i.e., one call each to
 *  findVerticalSeam(), removeVerticalSeam(), findHorizontalSeam(), and
 *  removeHorizontalSeam()). Use a "doubling" hypothesis, where you
 *  successively increase either W or H by a constant multiplicative
 *  factor (not necessarily 2).
 *
 *  To do so, fill in the two tables below. Each table must have 5-10
 *  data points, ranging in time from around 0.25 seconds for the smallest
 *  data point to around 30 seconds for the largest one.
 **************************************************************************** */

(keep W constant)
 W = 2000
 multiplicative factor (for H) = 2

 H           time (seconds)      ratio       log ratio
------------------------------------------------------
1000            0.319           n/a             n/a
2000            0.596           1.868           0.901
4000            1.252           2.101           1.071
8000            2.509           2.003           1.002
16000           5.161           2.057           1.041
32000           10.203          1.977           0.983
64000           20.137          1.974           0.981
128000          40.351          2.004           1.003


(keep H constant)
 H = 2000
 multiplicative factor (for W) = 2

 W           time (seconds)      ratio       log ratio
------------------------------------------------------
1000            0.317           n/a             n/a
2000            0.611           1.927           0.946
4000            1.157           1.894           0.921
8000            2.542           2.197           1.136
16000           5.082           1.999           0.999
32000           10.174          2.002           1.001
64000           20.029          1.969           0.977
128000          39.985          1.996           0.997


/* *****************************************************************************
 *  Using the empirical data from the above two tables, give a formula
 *  (using tilde notation) for the running time (in seconds) as a function
 *  of both W and H, such as
 *
 *       ~ 5.3*10^-8 * W^5.1 * H^1.5
 *
 *  Briefly explain how you determined the formula for the running time.
 *  Recall that with tilde notation, you include both the coefficient
 *  and exponents of the leading term (but not lower-order terms).
 *  Round each coefficient and exponent to two significant digits.
 **************************************************************************** */

Running time (in seconds) to find and remove one horizontal seam and one
vertical seam, as a function of both W and H:


Looking at the average of the log ratio of both tables, both round up and converge to
1. This indicates that it is linear with respect to both W and H. In other words,
the running time doubel as H doubles or W doubles, holding one of the variables
constant. To determine the coefficient, we can use one of the sample. For instance,
I used W = 64000 and H = 2000 with runtime of 20.029. Solving the equation T = aWH,
we get 20.029 = a(64000)(2000). Therefore, a = 1.6e-07

    ~   1.6e-07 * W * H
       _______________________________________




/* *****************************************************************************
 *  Known bugs / limitations.
 **************************************************************************** */
There are no known bugs or limitations.


/* *****************************************************************************
 *  Describe any serious problems you encountered.
 **************************************************************************** */
None


/* *****************************************************************************
 *  List any other comments here. Feel free to provide any feedback
 *  on how much you learned from doing the assignment, and whether
 *  you enjoyed doing it.
 **************************************************************************** */
