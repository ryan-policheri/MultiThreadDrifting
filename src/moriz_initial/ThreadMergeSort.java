package moriz_initial;
import java.util.ArrayList;

public class ThreadMergeSort<T extends Comparable<T>> implements Runnable  {
	   private Thread t;
	   private ArrayList<T> arr;
	   private int upper_bound;
	   private int lower_bound;
	   
	   ThreadMergeSort(ArrayList<T> arr, int lower_bound, int upper_bound) {
	      this.arr = arr;
	      this.upper_bound = upper_bound;
	      this.lower_bound = lower_bound;
	      
	   }
	   void merge(ArrayList<T> arr, int l, int m, int r)
	    {
	        // Find sizes of two subarrays to be merged
	        int n1 = m - l + 1;
	        int n2 = r - m;
	 
	        /* Create temp arrays */
	        ArrayList<T> L = new ArrayList<T>(n1);
	        ArrayList<T> R= new ArrayList<T>(n2);
	 
	        /*Copy data to temp arrays*/
	        for (int i = 0; i < n1; ++i)
	            L.add(arr.get(l + i));
	        for (int j = 0; j < n2; ++j)
	            R.add(arr.get(m + 1 + j));
	 
	        /* Merge the temp arrays */
	 
	        // Initial indexes of first and second subarrays
	        int i = 0, j = 0;
	 
	        // Initial index of merged subarry array
	        int k = l;
	        while (i < n1 && j < n2) {
	            if (L.get(i).compareTo(R.get(j)) <= 0) {
	                arr.set(k,L.get(i));
	                i++;
	            }
	            else {
	                arr.set(k,R.get(j));
	                j++;
	            }
	            k++;
	        }
	 
	        /* Copy remaining elements of L[] if any */
	        while (i < n1) {
                arr.set(k,L.get(i));
	            i++;
	            k++;
	        }
	 
	        /* Copy remaining elements of R[] if any */
	        while (j < n2) {
                arr.set(k,R.get(j));
	            j++;
	            k++;
	        }
	    }
	   void mergeSort(ArrayList<T> arr, int l, int r)
	    {
	        if (l < r) {
	            // Find the middle point
	            int m =l+ (r-l)/2;
	 
	            // Sort first and second halves
	            ThreadQuickSort<T> new_thread_1 = new ThreadQuickSort<T>(arr, l, m);
			    new_thread_1.start();
			    mergeSort(arr, m + 1, r);
			    try {
			    	new_thread_1.join();
			       } catch ( Exception e) {
			          System.out.println("Interrupted");
		       }
	            // Merge the sorted halves
	            merge(arr, l, m, r);
	        }
	    }

		public void run() {
	      try {
	    	 mergeSort(arr, lower_bound, upper_bound);
	      } catch (Exception e) { //InterruptedException
	    	  System.out.println(e);
	         System.out.println("Thread " +  lower_bound +"/"+ upper_bound + " interrupted.");
	      }
	      
	   }
	   
	   public void start () {
	      if (t == null) {
	         t = new Thread (this, lower_bound +"/"+ upper_bound);
	         t.start ();
	      }
	   }

		public void join() throws InterruptedException {
			t.join();
			
		}
	
 
    
}
