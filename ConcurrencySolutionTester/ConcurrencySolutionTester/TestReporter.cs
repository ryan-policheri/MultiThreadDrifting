using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;

namespace ConcurrencySolutionTester
{
    public class TestReporter
    {
        private readonly IEnumerable<PerformedTest> _tests;

        public TestReporter(IEnumerable<PerformedTest> tests)
        {
            _tests = tests;
        }

        public IEnumerable<string> Solutions => _tests.Select(x => x.Solution).Distinct();

        public IEnumerable<int> FileSizes => _tests.Select(x => x.InputFile.FileSize).Distinct();

        public IEnumerable<string> GenerationTypes => _tests.Select(x => x.InputFile.GenerationType).Distinct();

        public IEnumerable<int> ThreadCounts => _tests.Select(x => x.ThreadCount).Distinct();

        public IEnumerable<TableWithMeta> GetResultsBySolution(string solution)
        {
            ICollection<TableWithMeta> tables = new List<TableWithMeta>();
            var tests = _tests.Where(x => x.Solution == solution);

            foreach (int fileSize in FileSizes)
            {
                TableWithMeta table = CreateDataTable(solution, fileSize);
                tables.Add(table);
            }

            return tables;
        }

        private TableWithMeta CreateDataTable(string solution, int fileSize)
        {
            DataTable table = new DataTable();
            table.Columns.Add("Thread Count");
            foreach (string genType in GenerationTypes)
            {
                table.Columns.Add(genType);
            }

            var tests = _tests.Where(x => x.Solution == solution && x.InputFile.FileSize == fileSize);
            var groups = tests.GroupBy(x =>  new { x.Solution, x.InputFile.FileSize, x.ThreadCount });
            foreach (var group in groups)
            {
                DataRow row = table.NewRow();
                row["Thread Count"] = group.First().ThreadCount;
                foreach (string genType in GenerationTypes)
                {
                    var genResults = group.Where(x => x.InputFile.GenerationType == genType).First();
                    row[genType] = genResults.Stats != null ? genResults.Stats.MillisecondsEllapsed : -1;
                }

                table.Rows.Add(row);
            }

            TableWithMeta tableWithMeta = new TableWithMeta();
            tableWithMeta.Table = table;
            tableWithMeta.Solution = solution;
            tableWithMeta.InputSize = fileSize;
            return tableWithMeta;
        }
    }
}
