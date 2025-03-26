import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

public class SeamTimer {
    public static void main(String[] args) {
        SeamCarver carver = new SeamCarver(new Picture("city8000-by-2000.png"));
        // Perform the operations
        Stopwatch time = new Stopwatch();
        for (int i = 0; i < 16; i++) {
            int[] vSeam = carver.findVerticalSeam();
            carver.removeVerticalSeam(vSeam);
            int[] hSeam = carver.findHorizontalSeam();
            carver.removeHorizontalSeam(hSeam);
        }

        double elapsedTime = time.elapsedTime();
        StdOut.println(elapsedTime);

    }
}

