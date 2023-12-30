package LookOnceMarketer_server_ver1;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.opencv.core.Mat;

public class LOM_server {
	private ServerSocket serversocket = null;
	private Socket socket = null;
	private final int port = 90;

	private InputStream in = null;
	private OutputStream out = null;
	private ObjectInputStream oin = null;
	private DataOutputStream dout = null;

	LOM_inform inform;
	LOM_DBManager db;

	Image img = null;

	public LOM_server(LOM_inform inform, LOM_DBManager db) {
		this.inform = inform;
		this.db = db;
		
		serverInit();
		int count = 1;

		try {

			System.out.println("[Server]: Waiting img..");
			
			//apple class number: 53
			while (true) {
				if ((img = (Image) ImageIO.read(in)) != null) {
					System.out.println("[Server]: Image received!");
					String fileNm = "C:\\Users\\adfsd\\LookOnceMarketer_server\\LookOnceMarketer_server_ver1\\img\\receivedImg_" + (inform.chartElementNum+1) + ".jpg";
					
					
					ImageIO.write((RenderedImage) img, "jpg", new File(fileNm));
					String[] cmdArray = {"python", "yolov5/detect.py", "--weights", "yolov5s.pt", "--source", fileNm, "--save-txt"};
					
					
					Process p = Runtime.getRuntime().exec(cmdArray);
					BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

				    String line = null;
				    while((line = br.readLine()) != null){
				        System.out.println(line);
				    }
					
					String detectedLabelPath = "C:\\Users\\adfsd\\LookOnceMarketer_server\\LookOnceMarketer_server_ver1\\detectResults\\detectTxt\\receivedImg_" + (inform.chartElementNum+1) + ".txt";
					String detectedImgPath = "C:\\Users\\adfsd\\LookOnceMarketer_server\\LookOnceMarketer_server_ver1\\detectResults\\detectImg\\receivedImg_" + (inform.chartElementNum+1) + ".jpg";
					Image detectedImg = new ImageIcon(detectedImgPath).getImage().getScaledInstance(480, 270, Image.SCALE_DEFAULT);
					inform.setImg(detectedImg);
					
					br = new BufferedReader(new FileReader(detectedLabelPath));
					String readStr =null;
					String lineStr[] = null;
					String maxLineStr[] = null;
					float max = Float.MIN_VALUE;
					int flag = 0;
					while((readStr = br.readLine())!=null) {
						lineStr = readStr.split(" ");
						if(lineStr[0].equals("47")) {
							flag++;
							float w = Float.parseFloat(lineStr[3]);
							float h = Float.parseFloat(lineStr[4]);
							if(w * h > max) {
								max = w * h;
								maxLineStr = lineStr;
							}
						}
					}
					if(flag == 0) { //apple(47) not found
						System.out.println("[Server]: Apple not found!");
						continue;
					}
					
					inform.setChartElementNum(inform.chartElementNum + 1);
					DetectedFruit df = new DetectedFruit(
							inform.chartElementNum,
							Float.parseFloat(maxLineStr[1]), //x
							Float.parseFloat(maxLineStr[2]), //y
							Float.parseFloat(maxLineStr[3]), //w
							Float.parseFloat(maxLineStr[4]),  //h
							detectedImg
					);
					
					
					System.out.println("Insert: fruitNum: " + df.fruitNum + ", marketability: " + df.marketability + ", result: " + df.result);
					
					inform.setMkMeasurementResultText(df.marketability);
					inform.setMkMeasurementGradeResultText(df.result);					
					
					if(df.result > 5) {
						JOptionPane.showMessageDialog(null, "Context Aware: Your apple grade too Bad");
					}
					
					db.dbInsert(df);
					
					inform.updateChart(df.marketability);
					
					ArrayList<Fruit> fruitData = db.dbFetch();
					inform.setMkMeanResultText(calcMkMean(fruitData));
					inform.setMkStdResultText(calcMkStd(fruitData));
					inform.setMkMaxResultText(findMaxMk(fruitData));
					inform.setMkMinResultText(findMinMk(fruitData));
					
					String sendStr = String.format("%.2f", df.marketability * 100) + " " + inform.getmkMeasurementGradeResultText();
					dout.writeUTF(sendStr);
					dout.flush();
					//out.write(sendStr.getBytes());
					count++;
				}
				if (count == 100) {
					break;
				}
			}
			System.out.println("[Server]: End loop, terminate process..");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("[Server]: Main function error!!");
			e.printStackTrace();
		} finally {
			try {
				serversocket.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				socket.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

	}

	public void serverInit() {
		getClient();
		getIOStream();
	}

	void getClient() {
		try {
			serversocket = new ServerSocket(port);
			socket = serversocket.accept();
			System.out.println("[Server]: Client accept success.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("[Server]: getClient error!!");
			e.printStackTrace();
		}
	}

	void getIOStream() {
		try {
			in = socket.getInputStream();
			out = socket.getOutputStream();
			dout = new DataOutputStream(out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("[Server]: getStream error!!");
			e.printStackTrace();
		}
	}
	
	//작물이름 / 평균 / 표준편차 / 최대값 / 최소값
	public static float calcMkMean(List<Fruit> fruitList) {
		float sum = 0.0f;
		for(Fruit i: fruitList) {
			sum += i.marketability;
		}
		return sum / fruitList.size();
	}
	
	public static float calcMkStd(List<Fruit> fruitList) {
		final int devideNum = fruitList.size();
		final float mkMean = calcMkMean(fruitList);
		float sum = 0.0f;
		float mkStd;
		for(Fruit i: fruitList) {
			sum += Math.pow(i.marketability - mkMean, 2);
		}
		mkStd = (float) Math.pow(sum / devideNum, 0.5);
		return mkStd;
	}
	
	public static float findMaxMk(List<Fruit> fruitList) {
		float max = Float.MIN_VALUE;
		for(Fruit i: fruitList) {
			if(i.marketability > max) {
				max = i.marketability;
			}
		}
		return max;
	}
	
	public static float findMinMk(List<Fruit> fruitList) {
		float min = Float.MAX_VALUE;
		for(Fruit i: fruitList) {
			if(i.marketability < min) {
				min = i.marketability;
			}
		}
		return min;
	}
}

class Fruit {
	protected int fruitNum;
	protected float marketability;
	protected int result;
	
	//default constructor
	public Fruit() {
		
	}
	
	public Fruit(int fruitNum, float marketability, int result) {
		this.fruitNum = fruitNum;
		this.marketability = marketability;
		this.result = result;
	}
	
	public int getFruitNum() {
		return fruitNum;
	}
	public float getMarketability() {
		return marketability;
	}
	public int getResult() {
		return result;
	}
}

class DetectedFruit extends Fruit {
	float x, y, w, h;
	Image img;

	DetectedFruit(int fruitNum, float x, float y, float w, float h) {
		super.fruitNum = fruitNum;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		super.marketability = CalcMarketability();
		super.result = CalcResult(super.marketability);
	}

	DetectedFruit(int fruitNum, float x, float y, float w, float h, Image img) {
		this.fruitNum = fruitNum;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.img = img;
		super.marketability = CalcMarketability();
		super.result = CalcResult(super.marketability);
	}

	Float CalcMarketability() {
		return w * h;
	}

	int CalcResult(Float marketability) {
		float ratio = marketability;
		int rank = 0;
		
		if (ratio > 0.40)
			rank = 1;
		else if (ratio > 0.32)
			rank = 2;
		else if (ratio > 0.24)
			rank = 3;
		else if (ratio > 0.16)
			rank = 4;
		else if (ratio > 0.08)
			rank = 5;
		else
			rank = 6;
		return rank;
	}

	public void setDetectedImage(Image detectedImage) {
		img = detectedImage;
	}

	public Image getDetectedImage() {
		return img;
	}
}