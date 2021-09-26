using System;

namespace ConcurrencySolutionTester
{
    public class ProcessStats
    {
        public ProcessStats(DateTime startTime, DateTime endTime, bool didExit)
        {
            StartTime = startTime;
            EndTime = endTime;
            DidFinish = didExit;
        }

        public DateTime StartTime { get; }

        public DateTime EndTime { get; }

        public bool DidFinish { get; }

        public int SecondsEllapsed => (EndTime - StartTime).Seconds;
        public int MillisecondsEllapsed => (EndTime - StartTime).Milliseconds;
    }
}
