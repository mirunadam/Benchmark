package bench;

import java.util.Arrays;
import java.util.Random;

public class DemoBenchmark implements IBenchmark{
    private int[] array;
    private int n;
    private boolean running = true;

    public void initialize(Object... params){
        Random random = new Random();
        this.n = (Integer) params[0];
        this.array = new int[n];
        // initialize the array with random integers between [0,1000)
        for (int i = 0; i < n; i++) {
            array[i] = random.nextInt(1000);
        }
        running = true; // reset running flag when initialized
    }

    public void run() {
        run(n); // default run with full array size
    }

    public void run(Object... params) {
        if (!running) return;

        // If a size parameter is provided, use it (for partial runs)
        int size = params.length > 0 ? (Integer) params[0] : n;
        quickSort(0, size - 1);
    }

    private void quickSort(int low, int high) {
        if (low < high && running) {
            int pivotIndex = partition(low, high);
            quickSort(low, pivotIndex - 1);  // sort left partition
            quickSort(pivotIndex + 1, high); // sort right partition
        }
    }

    // partitioning logic
    private int partition(int low, int high) {
        int pivot = array[high];
        int i = low - 1;

        for (int j = low; j < high && running; j++) {
            if (array[j] <= pivot) {
                i++;
                swap(i, j);
            }
        }
        swap(i + 1, high);
        return i + 1;
    }

    // helper method to swap elements
    private void swap(int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public void clean() {
        array = null;
        n = 0;
    }

    public void cancel() {
        running = false;
    }

}
