# MultiThreadDrifting
 
Rundown of what Moriz has done:

I created 5 classes:

TestThreadSort -> File for testing the different algorithms

ThreadMergeSort -> Standard merge sort, but every recursive calls creates a thread making it absolutely horrible as I found out

ThreadQuickSort -> same as above, but with quicksort

ThreadQuickSortNoProlif -> my attempts at using a fixed amount of threads and splitting the array into parts for them
	each part is then sorted with quicksort and merged back together into one sorted array

ThreadTimSort -> same as above but it uses the Java default sort called TimSort

I have had okay results with ThreadQuickSortNoProlif, but it is clearly slower than timsort.
ThreadTimSort right now has the best runtime, but it just cannot beat the 1 thread run with my tests with 10 threads (Tested with 100M elements)
Even before the merging step the 1 thread run is done first...

