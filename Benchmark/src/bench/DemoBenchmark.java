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

    @Override
    public void warmUp() {
        // Clone the original array so we don't modify the real one
        int[] backup = Arrays.copyOf(array, array.length);

        // Run quickSort on a small subset (e.g., 10% of the array or up to 10,000 elements)
        int warmupSize = Math.min(10000, n / 10);

        // Temporarily sort a small chunk
        int[] warmupArray = Arrays.copyOf(backup, warmupSize);

        // Temporarily swap in the warmup array
        int[] original = this.array;
        this.array = warmupArray;

        quickSort(0, warmupSize - 1);

        // Restore original array
        this.array = original;
    }

    public String getResult(){
        return String.format("Sorted %d elements. Min: %d, Max: %d",
                array.length, array[0], array[array.length - 1]);
    }


}
