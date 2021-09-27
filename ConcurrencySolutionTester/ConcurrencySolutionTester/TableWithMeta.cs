using System;
using System.Collections.Generic;
using System.Data;
using System.Text;

namespace ConcurrencySolutionTester
{
    public class TableWithMeta
    {
        public int InputSize { get; set; }

        public string Solution { get; set; }

        public string Header => $"Solution {Solution} Input Size: {InputSize}";

        public DataTable Table { get; set; }

        public int ColumnCount => Table.Columns.Count;
    }
}
