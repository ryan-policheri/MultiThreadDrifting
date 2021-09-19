package mapreduce;

public class ReductionResultPair {
    public ReductionResult Result1;
    public ReductionResult Result2;

    public int calculateWeightedLength() {
        return Result1.RecordWeightedLength + Result2.RecordWeightedLength;
    }
}
