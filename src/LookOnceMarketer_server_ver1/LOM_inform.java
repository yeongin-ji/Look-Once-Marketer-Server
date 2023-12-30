package LookOnceMarketer_server_ver1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.jfree.chart.ChartPanel;

public class LOM_inform extends JFrame {
	private JLabel imageLabel = new JLabel();
	private JLabel mkabilityLabel;
	private JLabel resultLabel;
	private ChartPanel chartPanel;
	
	private MyLabel mkMeanResultText;
	private MyLabel mkStdResultText;
	private MyLabel mkMaxResultText;
	private MyLabel mkMinResultText;
	
	private MyLabel mkMeasurementResultText;
	private MyLabel mkMeasurementGradeResultText;
	
	static int chartElementNum;
	static Server_Import chartFrame;
	ChartPanel mkBarchartPanel;

	public LOM_inform() {
		LOM_DBManager db = new LOM_DBManager();
		ArrayList<Fruit> fetchedMkData = db.dbFetch();
		chartElementNum = fetchedMkData.get(fetchedMkData.size() - 1).fruitNum;
		chartFrame = new Server_Import("Title", "Measurement of Marketability", fetchedMkData);
		 
		mkBarchartPanel = chartFrame.getChartPanel();
		 
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setTitle("LookOnceMarketer-server ver 1.0");
		
		// create entry panel
		MyPanel entryPanel = new MyPanel(new BorderLayout());
		
		
		MyPanel resultPanel = new MyPanel(new FlowLayout());
		
		
		MyPanel picturePanel = new MyPanel(new FlowLayout());
		Image loadImage = new ImageIcon("img/apples.jpg").getImage().getScaledInstance(480, 270, Image.SCALE_DEFAULT);
		imageLabel = new JLabel(new ImageIcon(loadImage));
		picturePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 45)); //상하좌우 10씩 띄우기
		picturePanel.add(imageLabel);
		
		
		MyPanel valuePanel = new MyPanel(new BorderLayout());
		
		LineBorder bb = new LineBorder(Color.black, 2, true); 
		MyPanel statisticPanel = new MyPanel(new GridLayout(4, 2));
		MyLabel mkMeanText = new MyLabel("Mean: ");
		mkMeanResultText = new MyLabel("0.84");
		setMkMeanResultText(LOM_server.calcMkMean(fetchedMkData));
		
		MyLabel mkStdText = new MyLabel("Std: ");
		mkStdResultText = new MyLabel("0.10");
		setMkStdResultText(LOM_server.calcMkStd(fetchedMkData));
		
		MyLabel mkMaxText = new MyLabel("Max: ");
		mkMaxResultText = new MyLabel("0.98");
		setMkMaxResultText(LOM_server.findMaxMk(fetchedMkData));
		
		MyLabel mkMinText = new MyLabel("min: ");
		mkMinResultText = new MyLabel("0.33");
		setMkMinResultText(LOM_server.findMinMk(fetchedMkData));
		
		statisticPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 25, 0));
		statisticPanel.setBackground(new Color(255,218,185));
		
		
		MyPanel mkMeasurementResultPanel = new MyPanel(new GridLayout(2, 2));
		MyLabel mkMeasurementText = new MyLabel("Marketability: ");
		mkMeasurementResultText = new MyLabel("-");
		
		MyLabel mkMeasurementGradeText = new MyLabel("Result: ");
		mkMeasurementGradeResultText = new MyLabel("-");
		
		mkMeasurementResultPanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 10, 0));
		mkMeasurementResultPanel.setBackground(new Color(255, 218, 185));
		
		mkMeasurementResultPanel.add(mkMeasurementText);
		mkMeasurementResultPanel.add(mkMeasurementResultText);
		mkMeasurementResultPanel.add(mkMeasurementGradeText);
		mkMeasurementResultPanel.add(mkMeasurementGradeResultText);
		
		
		statisticPanel.add(mkMeanText);
		statisticPanel.add(mkMeanResultText);
		
		statisticPanel.add(mkStdText);
		statisticPanel.add(mkStdResultText);
		
		statisticPanel.add(mkMaxText);
		statisticPanel.add(mkMaxResultText);
		
		statisticPanel.add(mkMinText);
		statisticPanel.add(mkMinResultText);
		
		
		valuePanel.add(statisticPanel, BorderLayout.NORTH);
		valuePanel.add(mkMeasurementResultPanel, BorderLayout.SOUTH);
		
		
		
		resultPanel.add(picturePanel);
		resultPanel.add(valuePanel);	
		
		entryPanel.add(resultPanel, BorderLayout.NORTH);
		
		
		MyPanel chartPanel = new MyPanel(new FlowLayout());	
		chartPanel.add(mkBarchartPanel);
		entryPanel.add(chartPanel, BorderLayout.SOUTH);
		
		entryPanel.setBorder(BorderFactory.createEmptyBorder(10 , 10 , 10 , 10)); //상하좌우 10씩 띄우기
		
		// 패널 추가
		setContentPane(entryPanel);
		pack();
		setVisible(true);
		
		LOM_server server = new LOM_server(this, db);
	}
	
	public void setChartElementNum(int num) {
		this.chartElementNum = num;
	}
	
	public void setMkMeasurementResultText(float data) {
		mkMeasurementResultText.setText(String.format("%.2f", data * 100));
	}
	public void setMkMeasurementGradeResultText(int data) {
		String grade;
		switch(data) {
		case 1:
			grade = "Excellent";
			break;
		case 2:
			grade = "Good";
			break;
		case 3:
			grade = "Not bad";
			break;
		case 4:
			grade = "Soso";
			break;
		case 5:
			grade = "Bad";
			break;
		default:
			grade = "Terrible";
		}
		mkMeasurementGradeResultText.setText(grade);
	}
	public String getmkMeasurementGradeResultText() {
		return mkMeasurementGradeResultText.getText();
	}
	
	public void setMkMeanResultText(float data) {
		mkMeanResultText.setText(String.format("%.2f", data * 100));
	}
	public void setMkStdResultText(float data) {
		mkStdResultText.setText(String.format("%.2f", data * 100));
	}
	public void setMkMaxResultText(float data) {
		mkMaxResultText.setText(String.format("%.2f", data * 100));
	}
	public void setMkMinResultText(float data) {
		mkMinResultText.setText(String.format("%.2f", data * 100));
	}
	
	public void mkabilitySetText(String s) {
		mkabilityLabel.setText(s);
	}
	
	public void resultSetText(String s) {
		resultLabel.setText(s);
	}
	
	public void setImg(Image i) {
		imageLabel.setIcon(new ImageIcon(i));
	}
	
	public void updateChart(float data) {
		chartFrame.updateChartData(data);
	}
}

class MyLabel extends JLabel {
	public MyLabel(String title) {
		super(title);
		setFont(new Font("Times New Roman", Font.PLAIN, 15));
		setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
	}
}

class MyPanel extends JPanel {
	public MyPanel(LayoutManager layout) {
		super(layout);
		Color color= new Color(255, 239 ,213);
		setBackground(color);
	}
}
