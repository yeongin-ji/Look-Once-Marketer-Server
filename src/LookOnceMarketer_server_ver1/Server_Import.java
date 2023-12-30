package LookOnceMarketer_server_ver1;

import java.awt.Color;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;

public class Server_Import extends ApplicationFrame {
	private DefaultCategoryDataset dataset;
	private ChartPanel chartPanel;
	private int num = 0; // To indicate the order

	public Server_Import(String applicationTitle, String chartTitle, ArrayList<Fruit> mkability) {
		super(applicationTitle);
		dataset = createDataset(mkability);
		this.num++;
		System.out.println("[Server_Import]: Display success!");
		JFreeChart barChart = ChartFactory.createBarChart(chartTitle, "Apples", "Marketability", dataset,
				PlotOrientation.VERTICAL, true, true, false);

		CategoryPlot plot = (CategoryPlot) barChart.getPlot();
		barChart.getPlot().setBackgroundPaint(new Color(255, 239 ,213));
		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryMargin(0.5); // Adjust the category margin here
		
		chartPanel = new ChartPanel(barChart);
		chartPanel.setBackground(new Color(255, 239 ,213));
		chartPanel.setPreferredSize(new java.awt.Dimension(727, 360));
		setContentPane(chartPanel);
	}

	public ChartPanel getChartPanel() {
		return chartPanel;
	}

	private DefaultCategoryDataset createDataset(ArrayList<Fruit> mkability) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (Fruit i : mkability) {
			dataset.addValue(i.marketability, "Apple", i.fruitNum + "th");
		}
		return dataset;
	}

	public void updateChartData(float data) {
		// Update the dataset with the new value
		dataset.addValue(data, "Apple", (LOM_inform.chartElementNum) + "th");
		// System.out.println("The order number is : "+ num);
		// Refresh the chart panel to display the updated chart
		chartPanel.repaint();
	}

}