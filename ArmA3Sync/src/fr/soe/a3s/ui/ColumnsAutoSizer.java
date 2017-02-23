package fr.soe.a3s.ui;

import java.awt.Component;
import java.awt.FontMetrics;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class ColumnsAutoSizer {

	public static void sizeColumnsToFit(JTable table,
			List<Integer> columnIndexes) {
		sizeColumnsToFit(table, 10, columnIndexes);
	}

	public static void sizeColumnsToFit(JTable table, int columnMargin,
			List<Integer> columnIndexes) {

		JTableHeader tableHeader = table.getTableHeader();
		if (tableHeader == null) {// can't auto size a table without a header
			return;
		}

		FontMetrics headerFontMetrics = tableHeader.getFontMetrics(tableHeader
				.getFont());

		int[] minWidths = new int[columnIndexes.size()];
		int[] maxWidths = new int[columnIndexes.size()];

		int j = 0;
		for (Integer columnIndex : columnIndexes) {
			int headerWidth = headerFontMetrics.stringWidth(table
					.getColumnName(columnIndex));
			minWidths[j] = headerWidth + columnMargin;
			int maxWidth = getMaximalRequiredColumnWidth(table, columnIndex,
					headerWidth);
			maxWidths[j] = Math.max(maxWidth, minWidths[j]) + columnMargin;
			j++;
		}

		adjustMaximumWidths(table, minWidths, maxWidths, columnIndexes);

		j = 0;
		for (Integer columnIndex : columnIndexes) {
			if (minWidths[j] > 0) {
				table.getColumnModel().getColumn(columnIndex)
						.setMinWidth(minWidths[j]);
			}
			if (maxWidths[j] > 0) {
				table.getColumnModel().getColumn(columnIndex)
						.setMaxWidth(maxWidths[j]);
				table.getColumnModel().getColumn(columnIndex)
						.setPreferredWidth(maxWidths[j]);
			}
			j++;
		}
	}

	private static void adjustMaximumWidths(JTable table, int[] minWidths,
			int[] maxWidths, List<Integer> columnIndexes) {
		
		if (table.getWidth() > 0) {
			// to prevent infinite loops in exceptional situations
			int breaker = 0;

			// keep stealing one pixel of the maximum width of the highest
			// column until we can fit in the width of the table
			while (sum(maxWidths) > table.getWidth() && breaker < 10000) {
				int highestWidthIndex = findLargestIndex(maxWidths,
						columnIndexes);

				maxWidths[highestWidthIndex] -= 1;

				maxWidths[highestWidthIndex] = Math.max(
						maxWidths[highestWidthIndex],
						minWidths[highestWidthIndex]);

				breaker++;
			}
		}
	}

	private static int getMaximalRequiredColumnWidth(JTable table,
			int columnIndex, int headerWidth) {
		
		int maxWidth = headerWidth;

		TableColumn column = table.getColumnModel().getColumn(columnIndex);
		TableCellRenderer cellRenderer = column.getCellRenderer();

		if (cellRenderer == null) {
			cellRenderer = new DefaultTableCellRenderer();
		}

		for (int row = 0; row < table.getModel().getRowCount(); row++) {
			Component rendererComponent = cellRenderer
					.getTableCellRendererComponent(table, table.getModel()
							.getValueAt(row, columnIndex), false, false, row,
							columnIndex);

			double valueWidth = rendererComponent.getPreferredSize().getWidth();

			maxWidth = (int) Math.max(maxWidth, valueWidth);
		}

		return maxWidth;
	}

	private static int findLargestIndex(int[] widths,
			List<Integer> columnIndexes) {

		int largestIndex = 0;
		int largestValue = 0;

		int j = 0;
		for (Integer columnIndex : columnIndexes) {
			if (widths[j] > largestValue) {
				largestIndex = columnIndex;
				largestValue = widths[j];
			}
			j++;
		}

		return largestIndex;
	}

	private static int sum(int[] widths) {
		int sum = 0;

		for (int width : widths) {
			sum += width;
		}

		return sum;
	}
}
