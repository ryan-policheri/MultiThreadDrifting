using System;
using System.Collections.Generic;
using System.Text;

namespace ConcurrencySolutionTester
{
    public class PerformedTest
    {
        public PerformedTest(TestFile inputFile, string solution, int threadCount)
        {
            InputFile = inputFile;
            Solution = solution;
            ThreadCount = threadCount;
        }

        public TestFile InputFile { get; }

        public string Solution { get; }

        public int ThreadCount { get; }

        public string OutputFile => CalculateOutputFile();
        public ProcessStats Stats { get; set; }

        public Exception Exception { get; set; }

        private string CalculateOutputFile()
        {
           string fileName = $"{InputFile.FileSize}_{InputFile.GenerationType}_{ThreadCount}_{Solution}.bin";
           string fullPath = SystemFunctions.CombineDirectoryComponents(InputFile.FileDirectory, fileName);
           return fullPath;
        }
    }
}
