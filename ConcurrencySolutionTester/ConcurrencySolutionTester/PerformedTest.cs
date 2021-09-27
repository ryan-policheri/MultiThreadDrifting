using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ConcurrencySolutionTester
{
    public class PerformedTest
    {
        public PerformedTest(TestFileSet inputFileSet, string solution, int threadCount)
        {
            InputFileSet = inputFileSet;
            Solution = solution;
            ThreadCount = threadCount;
        }

        public TestFileSet InputFileSet { get; }

        public string Solution { get; }

        public int ThreadCount { get; }

        public string[] OutputFiles => CalculateOutputFiles();

        public ProcessStats Stats { get; set; }

        public int BestRunAveragedAcrossSets { get; set; }

        public Exception Exception { get; set; }

        private string[] CalculateOutputFiles()
        {
            ICollection<string> outputFiles = new List<String>();
            foreach (TestFile file in InputFileSet.TestFiles)
            {
                string fileName = $"{file.FileSize}_{file.GenerationType}_Set{file.OwningSet}_{ThreadCount}_{Solution}.bin";
                string fullPath = SystemFunctions.CombineDirectoryComponents(file.FileDirectory, fileName);
                outputFiles.Add(fullPath);
            }
            return outputFiles.ToArray();
        }
    }
}
