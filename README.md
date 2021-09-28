# MultiThreadDrifting

To run the program, run the RootDriver located in the Root package.

Pass in the input file, the output file, and the number of threads. Optionally also pass in MAP_REDUCE to run that solution, if omitted the QUICK_SORT solution will be ran by default. See examples below

To use **QuickSort** (Default/Official Submission)
```
C:\Users\Ryan\input_file.bin C:\Users\Ryan\output_file.bin 8
```
To use **MapReduce**
```
C:\Users\Ryan\input_file.bin C:\Users\Ryan\output_file.bin 4 MAP_REDUCE
```

**Quicksort**
The quicksort package contains the QUICK_SORT solution. The entry point to that solution is the QuickSortProcessor

- **QuickSortProcessor**
    - Orchestrates entire reading, sorting, and writing process
- **LoadRunnable** *(single-threaded)*
    - Loads given file into an array one chunk at a time
- **QuickSortRunnable** *(multi-threaded)*
    - Sorts a given chunk using quicksort
- **MergeRunnable** *(multi-threaded)*
    - Merges sorted chunks together into bigger chunks until one chunk is remaining
- **Driver**
    - Mainly used for local testing

**MapReduce**
The mapreduce package contains the MAP_REDUCE solution. The entry point to that solution is the MapReduceProcessor

- **Driver**
    - This is only used for quick/hacky testing. Use RootDriver for actual testing
- **MapReduceDataManager**
    - Singleton object that synchronizes data access
- **MapReduceProcessor**
    - Entry point for the MapReduce Solution
- **ReducedRecord**
    - POJO containing a number and the amount of times it is repeated
- **ReducedRecordWriter**
    - Buffered file writer that writes reduced records
- **ReduceJob**
    - Runnable that reduces a long chunk or a ReductionResultPair
- **Reducer**
    - Underlying object that performs the reduction, sorting, and multiplexing
- **ReductionResult**
    - The result of a reduction. Contains and array of ReductionResult and the weighted count
- **ReductionResultPair**
    - Two ReductionResults paired together
