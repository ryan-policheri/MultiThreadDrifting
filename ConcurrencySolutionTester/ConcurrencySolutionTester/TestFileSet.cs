using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ConcurrencySolutionTester
{
    public class TestFileSet
    {
        public TestFileSet()
        {
            TestFiles = new List<TestFile>();
        }

        public int FileSize => TestFiles.First().FileSize;

        public string GenerationType => TestFiles.First().GenerationType;

        public ICollection<TestFile> TestFiles { get; set; }
    }
}
