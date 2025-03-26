import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {

    // creates an instance of the input picture
    private Picture pic;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        checkArg(picture);
        int width = picture.width();
        int height = picture.height();
        pic = new Picture(width, height);
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                pic.setRGB(col, row, picture.getRGB(col, row));
            }
        }
    }

    // current picture
    public Picture picture() {
        // creates a deep copy of the picture
        Picture newPic = new Picture(width(), height());

        for (int col = 0; col < width(); col++) {
            for (int row = 0; row < height(); row++) {
                newPic.setRGB(col, row, this.pic.getRGB(col, row));
            }
        }
        return newPic;
    }

    // width of current picture
    public int width() {
        return pic.width();
    }

    // height of current picture
    public int height() {
        return pic.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        checkRange(x, y);
        return Math.sqrt(calcXGrad(x, y) + calcYGrad(x, y));
    }

    // helper method to calculate x-gradient of the energy function
    private double calcXGrad(int x, int y) {
        int xLeft = x - 1;
        int xRight = x + 1;

        if (x == 0) xLeft = width() - 1;
        if (x == width() - 1) xRight = 0;

        int leftColor = pic.getRGB(xLeft, y);
        int rightColor = pic.getRGB(xRight, y);

        // calculates the central differences in the RBG components
        return calcGrad(leftColor, rightColor);
    }

    // helper method to calculate the y-gradient of the energy function
    private double calcYGrad(int x, int y) {
        int yTop = y - 1;
        int yBottom = y + 1;

        if (y == 0) yTop = height() - 1;
        if (y == height() - 1) yBottom = 0;

        int topColor = pic.getRGB(x, yTop);
        int bottomColor = pic.getRGB(x, yBottom);

        return calcGrad(topColor, bottomColor);
    }

    // helper method to calculate the energy gradient given RGB of two pixels
    private double calcGrad(int a, int b) {
        // calculates the central differences in the RBG components
        double rx = rbgToRed(a) - rbgToRed(b);
        double gx = rbgToGreen(a) - rbgToGreen(b);
        double bx = rbgToBlue(a) - rbgToBlue(b);

        return Math.pow(rx, 2) + Math.pow(gx, 2) + Math.pow(bx, 2);
    }

    // helper method to convert 32-bit color encoding to 8-bit red component
    private int rbgToRed(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    // helper method to convert 32-bit color encoding to 8-bit green component
    private int rbgToGreen(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    // helper method to convert 32-bit color encoding to 8-bit blue component
    private int rbgToBlue(int rgb) {
        return (rgb) & 0xFF;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        transpose(this.pic);
        int[] seam = findVerticalSeam();
        transpose(this.pic);
        return seam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int[] seam = new int[height()];

        double[][] energyGrid = energyGrid();
        double[][] distTo = distTo(energyGrid);

        // keeps track of which col in the previous row led to the current pixel
        Stack<Integer> colTo = new Stack<>();

        // finds the minimum energy value of the last row in the distTo matrix
        // and its corresponding col index
        int row = height() - 1;
        int index = 0;
        double minTotal = Double.POSITIVE_INFINITY;
        for (int col = 0; col < width(); col++) {
            if (distTo[row][col] < minTotal) {
                minTotal = distTo[row][col];
                index = col;
            }
        }
        colTo.push(index);

        // given the col index of the min energy of the last row, retrace up the
        // picture to compute the path that it took from any top pixel
        while (row > 0) {
            // if the value exists, takes the index of the column in the
            // previous row with the lowest value in the distTo matrix
            double minEnergy = distTo[row - 1][index];
            int champ = index;

            if (index != 0 && distTo[row - 1][index - 1] < minEnergy) {
                champ = index - 1;
                minEnergy = distTo[row - 1][index - 1];
            }
            if (index != width() - 1 && distTo[row - 1][index + 1] < minEnergy) {
                champ = index + 1;
            }
            index = champ;
            colTo.push(index);
            row--;
        }

        // outputs values from stack into an array
        int i = 0;
        while (!colTo.isEmpty()) {
            seam[i] = colTo.pop();
            i++;
        }
        return seam;
    }

    // helper method that store energy of min energy value from any top pixel to
    // pixel (col, row) in a 2d array
    private double[][] distTo(double[][] energyGrid) {
        double[][] grid = new double[height()][width()];

        // base case
        for (int col = 0; col < width(); col++) {
            grid[0][col] = energyGrid[0][col];
        }

        // dynamic programming approach
        for (int row = 1; row < height(); row++) {
            for (int col = 0; col < width(); col++) {

                double leftEnergy = Double.POSITIVE_INFINITY;
                double straightEnergy = grid[row - 1][col];
                double rightEnergy = Double.POSITIVE_INFINITY;

                if (col != 0) leftEnergy = grid[row - 1][col - 1];
                if (col != width() - 1) rightEnergy = grid[row - 1][col + 1];

                // computes the energy of col i-1, i, and i+1 of the previous
                // row from any top pixel and take the minimum of the three
                double minEnergy;
                // corner case if col is on the border
                if (col == 0) {
                    minEnergy = Math.min(straightEnergy, rightEnergy);
                }
                // corner case if col is on the border
                else if (col == width() - 1) {
                    minEnergy = Math.min(straightEnergy, leftEnergy);
                }
                else {
                    minEnergy = Math.min(leftEnergy, straightEnergy);
                    minEnergy = Math.min(minEnergy, rightEnergy);
                }
                // min energy value is the minimum of the three col of the previous row
                // adjacent to the current pixel plus the energy of the current pixel
                grid[row][col] = minEnergy + energyGrid[row][col];
            }
        }
        return grid;
    }

    // helper method that stores the energy value of each pixel in the picture
    private double[][] energyGrid() {
        double[][] grid = new double[height()][width()];
        for (int row = 0; row < height(); row++) {
            for (int col = 0; col < width(); col++) {
                grid[row][col] = energy(col, row);
            }
        }
        return grid;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        checkArg(seam);
        validateHorizontal(seam);
        transpose(this.pic);
        removeVerticalSeam(seam);
        transpose(this.pic);
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        checkArg(seam);
        validateVertical(seam);
        Picture newPic = new Picture(width() - 1, height());

        for (int row = 0; row < height(); row++) {
            int index = seam[row];
            for (int col = 0; col < width(); col++) {
                if (col < index) {
                    newPic.setRGB(col, row, this.pic.getRGB(col, row));
                }

                if (col > index) {
                    newPic.setRGB(col - 1, row, this.pic.getRGB(col, row));
                }
            }
        }
        this.pic = newPic;
    }

    // helper method to transpose the picture, interchanging the row and
    // column indices
    private void transpose(Picture picture) {
        Picture transposedPic = new Picture(height(), width());
        for (int col = 0; col < width(); col++) {
            for (int row = 0; row < height(); row++) {
                transposedPic.setRGB(row, col, picture.getRGB(col, row));
            }
        }
        this.pic = transposedPic;
    }

    // checks to see if argument parameter is null
    private void checkArg(Object argument) {
        if (argument == null) throw new IllegalArgumentException("");
    }

    // checks if either x-coordinate or y-coordinate is outside prescribed range
    private void checkRange(int x, int y) {
        if (x < 0 || x > width() - 1) throw new IllegalArgumentException("");
        if (y < 0 || y > height() - 1) throw new IllegalArgumentException("");
    }

    // checks to see if call to remove horizontal seam is valid
    private void validateHorizontal(int[] seam) {
        if (height() == 1) throw new IllegalArgumentException("");
        if (seam.length != width()) throw new IllegalArgumentException("");
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] > height() - 1) {
                throw new IllegalArgumentException("");
            }

            if (i != 0 && Math.abs(seam[i] - seam[i - 1]) > 1) {
                throw new IllegalArgumentException("");
            }
            if (i != seam.length - 1 && Math.abs(seam[i] - seam[i + 1]) > 1) {
                throw new IllegalArgumentException("");
            }
        }
    }

    // checks to see if call to remove vertical seam is valid
    private void validateVertical(int[] seam) {
        if (width() == 1) throw new IllegalArgumentException("");
        if (seam.length != height()) throw new IllegalArgumentException("");
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] > width() - 1) {
                throw new IllegalArgumentException("");
            }
            if (i != 0 && Math.abs(seam[i] - seam[i - 1]) > 1) {
                throw new IllegalArgumentException("");
            }
            if (i != seam.length - 1 && Math.abs(seam[i] - seam[i + 1]) > 1) {
                throw new IllegalArgumentException("");
            }
        }
    }

    //  unit testing (required)
    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        SeamCarver carver = new SeamCarver(picture);

        // test correctness of width() and height()
        StdOut.print("Width(): ");
        StdOut.println(carver.width());
        StdOut.print("Height(): ");
        StdOut.println(carver.height());

        StdOut.println();

        // test correctness of picture()
        Picture pic = carver.picture();
        pic.show();

        // test findVerticalSeam
        int[] vSeam = carver.findVerticalSeam();
        StdOut.println("Vertical Seam: ");
        for (int i = 0; i < vSeam.length; i++) {
            StdOut.print(vSeam[i] + " ");
        }
        StdOut.println();
        StdOut.println();

        // test correctness of remove vertical seam
        carver.removeVerticalSeam(vSeam);
        Picture postVertPic = carver.picture();
        postVertPic.show();

        // test findHorizontalSeam()
        int[] hSeam = carver.findHorizontalSeam();
        StdOut.println("Horizontal Seam: ");
        for (int i = 0; i < hSeam.length; i++) {
            StdOut.print(hSeam[i] + " ");
        }
        StdOut.println();

        // test correctness of remove vertical seam
        carver.removeHorizontalSeam(hSeam);
        Picture postHorSeam = carver.picture();
        postHorSeam.show();
        StdOut.println();

        // test correctness of energy()
        StdOut.println("Energy of (0,0): ");
        StdOut.println(carver.energy(0, 0));
        StdOut.println();

        // tests correctness of energy 2D array
        StdOut.println("Energy Grid: ");
        double[][] energyGrid = carver.energyGrid();
        for (int i = 0; i < energyGrid.length; i++) {
            for (int j = 0; j < energyGrid[0].length; j++) {
                String marker = " ";
                StdOut.printf("%7.2f%s ", energyGrid[i][j], marker);
            }
            StdOut.println();
        }

        StdOut.println();

        // tests correctness of distTo 2D array
        StdOut.println("distTo matrix: ");
        double[][] distTo = carver.distTo(energyGrid);
        for (int i = 0; i < distTo.length; i++) {
            for (int j = 0; j < distTo[0].length; j++) {
                String marker = " ";
                StdOut.printf("%7.2f%s ", distTo[i][j], marker);
            }
            StdOut.println();
        }
    }
}
