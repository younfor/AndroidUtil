import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class game {

	static MyCards myCards = null;
	static Persons persons = null;

	//�жϳ��ƵĲ���
	static int myId = 0;
	static int mySeat = 0;//�ҵ���λ���  ���� 0����Сäע��1�����äע ��2����ǰ��3������
	
	static int mySeatNum = 0;//�ҵ���λ�ţ���ׯ�ң�Сäע����äע���ȵ� ��˳������
	static int personsLeft = 0;//ʣ�����������ֵ����Ϊ 12345678
	static int personsAll = 0;//�����������ֵ����Ϊ 1-8
	static int othersAction = 0;//ǰ����ҵ���ע������������Է�Ϊ��0 δ��ע  1 ��ע  2 allin
	
	static int bTime = 0;//����ǰ����
	static int fTime = 0;//���Ľ׶μ���
	static int tTime = 0;//ת�ƽ׶μ���
	static int rTime = 0;//���ƽ׶μ���
	static int askTime = 0;
	
	static int[] seatRound;
	
	static Datas datas;
	static boolean IsFirstInquire;
	
	

	final static int[][] twoCardsCheck = new int[][] { 
			{ -3, -3, -3, -3, -3, -3 - 3, -3 },
			{ -3, -3, -3, -3, -3, -3 - 3, -3 },
			{ -3, -3, -3, -3, -3, -3 - 3, -3 },
			{ -3, -3, -3, -3, -3, -3 - 3, -3 },

	};
	
	final static int[][] twoCardsRaiseLittle = new int[][] { 
		{ -3, 3, 3, 4, 4, 5, 5, 6 }, 
		{ -3, 3, 3, 4, 4, 5, 5, 6 }, 
		{ -3, 3, 3, 4, 4, 5, 5, 6 }, 
		{ -3, 3, 3, 4, 4, 5, 5, 6 }

	};
	final static int[][] twoCardsRaise = new int[][] { 
		{ -3, 5, 5, 6, 6, 7, 7, 8 }, 
		{ -3, 5, 5, 6, 6, 7, 7, 8 }, 
		{ -3, 5, 5, 6, 6, 7, 7, 8 }, 
		{ -3, 5, 5, 6, 6, 7, 7, 8 }

	};
	
	final static int[][] twoCardsRaiseMuch = new int[][] { 
		{ -3, 7, 7, 8, 8, 9, 9, 10 }, 
		{ -3, 7, 7, 8, 8, 9, 9, 10 }, 
		{ -3, 7, 7, 8, 8, 9, 9, 10 }, 
		{ -3, 7, 7, 8, 8, 9, 9, 10 }, 

	};
	
	final static int[][] twoCardsRaiseMuchMore = new int[][] { 
		{ -3, 8, 8, 9, 10, 11, 12, 12 }, 
		{ -3, 8, 8, 9, 10, 11, 12, 12 },
		{ -3, 8, 8, 9, 10, 11, 12, 12 }, 
		{ -3, 8, 8, 9, 10, 11, 12, 12 }

	};
	final static int[][] twoCardsAllin = new int[][] { 
		{ -3, 16, 16, 16, 16, 16, 16, 16 },
		{ -3, 16, 16, 16, 16, 16, 16, 16 },
		{ -3, 16, 16, 16, 16, 16, 16, 16 },
		{ -3, 16, 16, 16, 16, 16, 16, 16 },

	};
	
	final static double[][] fiveCardsCheck = new double[][] { 
		{ 0, 0, 0, 0, 0, 0, 0, 0 }, 
		{ 0, 0, 0, 0, 0, 0, 0, 0 },
		{ 0, 0, 0, 0, 0, 0, 0, 0 }, 
		{ 0, 0, 0, 0, 0, 0, 0, 0 }
	};

	final static double[][] fiveCardsRaiseLittle = new double[][] { 
			{ 0, 0.5, 0.5, 0.5, 0.56, 0.67, 0.8, 0.88 }, 
			{ 0, 0.5, 0.5, 0.5, 0.56, 0.67, 0.8, 0.88 },
			{ 0, 0.5, 0.5, 0.5, 0.56, 0.67, 0.8, 0.88 }, 
			{ 0, 0.5, 0.5, 0.5, 0.56, 0.67, 0.8, 0.88 }
	};
	
	
//	final static double[][] fiveCardsRaise = new double[][] { 
//			{ 0, 0.7, 0.75, 0.77, 0.80, 0.85, 0.9, 0.95 },
//			{ 0, 0.7, 0.75, 0.77, 0.80, 0.85, 0.9, 0.95 },
//			{ 0, 0.7, 0.75, 0.77, 0.80, 0.85, 0.9, 0.95 },
//			{ 0, 0.7, 0.75, 0.77, 0.80, 0.85, 0.9, 0.95 }
//
//	};
//	
//	final static double[][] fiveCardsRaiseMuch = new double[][] { 
//			{ 0, 0.8, 0.82, 0.85, 0.88, 0.93, 0.98, 0.99 },
//			{ 0, 0.8, 0.82, 0.85, 0.88, 0.93, 0.98, 0.99 }, 
//			{ 0, 0.8, 0.82, 0.85, 0.88, 0.93, 0.98, 0.99 },
//			{ 0, 0.8, 0.82, 0.85, 0.88, 0.93, 0.99, 0.99 }
//
//	};
//	
//	final static double[][] fiveCardsRaiseMuchMore = new double[][] { 
//			{ 0, 0.85, 0.85, 0.85, 0.88, 0.93, 0.98, 0.99 },
//			{ 0, 0.85, 0.85, 0.85, 0.88, 0.93, 0.98, 0.99 }, 
//			{ 0, 0.85, 0.85, 0.85, 0.88, 0.93, 0.98, 0.99 },
//			{ 0, 0.85, 0.85, 0.85, 0.88, 0.93, 0.99, 0.99 }
//
//	};
//	final static double[][] fiveCardsRaiseMuchMuchMore = new double[][] { 
//			{ 0, 0.9, 0.91, 0.92, 0.93, 0.95, 0.98, 0.99 },
//			{ 0, 0.9, 0.91, 0.92, 0.93, 0.95, 0.98, 0.99 },
//			{ 0, 0.9, 0.91, 0.92, 0.93, 0.95, 0.98, 0.99 },
//			{ 0, 0.9, 0.91, 0.92, 0.93, 0.95, 0.98, 0.99 }
//
//	};
	final static double[][] fiveCardsRaise = new double[][] { 
		{ 0, 0.7, 0.75, 0.77, 0.80, 0.85, 0.9, 0.95 },
		{ 0, 0.7, 0.75, 0.77, 0.80, 0.85, 0.9, 0.95 },
		{ 0, 0.7, 0.75, 0.77, 0.80, 0.85, 0.9, 0.95 },
		{ 0, 0.7, 0.75, 0.77, 0.80, 0.85, 0.9, 0.95 }

	};

	final static double[][] fiveCardsRaiseMuch = new double[][] { 
		{ 0, 0.8, 0.83, 0.85, 0.88, 0.93, 0.98, 0.99 },
		{ 0, 0.8, 0.83, 0.85, 0.88, 0.93, 0.98, 0.99 }, 
		{ 0, 0.8, 0.83, 0.85, 0.88, 0.93, 0.98, 0.99 },
		{ 0, 0.8, 0.83, 0.85, 0.88, 0.93, 0.99, 0.99 }

	};

	final static double[][] fiveCardsRaiseMuchMore = new double[][] { 
		{ 0, 0.86, 0.88, 0.88, 0.9, 0.93, 0.98, 0.99 },
		{ 0, 0.86, 0.88, 0.88, 0.9, 0.93, 0.98, 0.99 }, 
		{ 0, 0.86, 0.88, 0.88, 0.9, 0.93, 0.98, 0.99 },
		{ 0, 0.86, 0.88, 0.88, 0.9, 0.93, 0.99, 0.99 }

	};
	final static double[][] fiveCardsRaiseMuchMuchMore = new double[][] { 
		{ 0, 0.91, 0.93, 0.94, 0.94, 0.95, 0.98, 0.99 },
		{ 0, 0.91, 0.93, 0.94, 0.94, 0.95, 0.98, 0.99 },
		{ 0, 0.91, 0.93, 0.94, 0.94, 0.95, 0.98, 0.99 },
		{ 0, 0.91, 0.93, 0.94, 0.94, 0.95, 0.98, 0.99 }

	};

	final static double[][] fiveCardsAllin = new double[][] { 
			{ 0, 0.965, 0.986, 0.987, 0.99, 0.99, 0.995, 0.995 },
			{ 0, 0.965, 0.986, 0.987, 0.99, 0.99, 0.995, 0.995 }, 
			{ 0, 0.965, 0.986, 0.987, 0.99, 0.99, 0.995, 0.995 },
			{ 0, 0.965, 0.986, 0.987, 0.99, 0.99, 0.995, 0.995 }

	};

	

	//���紫���ַ���
	static String sendMsg = null;
	static String saveMsg = null;
	static byte[] saveBuf = null;

	static ArrayList<byte[]> bList = null;

	//����\n ��byte[]
	static byte[] nbyte = new String("\n").getBytes();
	static byte[] _byte = new String(" ").getBytes();
	static byte[] seatbyte1 = new String("seat/").getBytes();
	static byte[] seatbyte2 = new String("/seat").getBytes();
	static byte[] holdbyte = new String("hold").getBytes();
	static byte[] flopbyte = new String("flop").getBytes();
	static byte[] turnbyte = new String("turn").getBytes();
	static byte[] riverbyte = new String("river").getBytes();
	static byte[] inquirebyte = new String("inquire").getBytes();
	static byte[] notifybyte = new String("notify").getBytes();

	// ��ʼ������״̬��
	static Status status = Status.INITIAL;

	public static enum Action {
		check, //����
		call, //����
		raise, //��ע
		all_in, //ȫѹ
		fold, //����
		blind //äע
	}

	static DataInputStream inStream = null;
	static DataOutputStream outStream = null;

	// ����״̬����7��״̬
	public enum Status {

		INITIAL, // ��ʼ��״̬
		SEAT, // ����
		BLIND, //äע
		HOLD, //����
		BEFOREFLOP, // ����ǰ
		FLOP, // ����Ȧ
		TURN, // ת��Ȧ
		RIVER, // ����Ȧ
		END, // ����״̬
		GAMEOVER//ȫ����Ϸ����
	}

	//������Ϣ���ͣ�Server->Client������10����Ϣ��
	public enum Message {
		seat_info_msg, //������Ϣ
		game_over_msg, //��Ϸ������Ϣ
		blind_msg, //äע��Ϣ
		hold_cards_msg, //������Ϣ
		inquire_msg, //ѯ����Ϣ
		notify_msg, //֪ͨ��Ϣ
		flop_msg, //������Ϣ
		turn_msg, //ת����Ϣ
		river_msg, //������Ϣ
		showdown_msg, //̯����Ϣ
		pot_win_msg//�ʳط�����Ϣ
	}
	
	static Socket socket = null;
	static int iii = 0;
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			
			e1.printStackTrace();
		}
		// ѭ���������Զ���������
		while (true) {

			// ����socket�쳣
			try {
				//				./game  192.168.0.1  1024  192.168.0.2  2048  6001
				//				���� 192.168.0.1 ����������IP�� 1024 ����������˿ں�
				//				���� 192.168.0.2 �����ֳ���󶨵�IP�� 2048�����ֳ���󶨵Ķ˿ں�
				//				���� 6001 ������������ע���ID
				while (true) {
					try {
						
						socket = new Socket();
						socket.setReuseAddress(true);
						socket.bind(new InetSocketAddress(args[2], Integer.parseInt(args[3])));
						
						socket.connect(new InetSocketAddress(args[0], Integer.parseInt(args[1])));
						socket.setKeepAlive(true);
						break;
					} catch (Exception e) {
						
						Thread.sleep(200);
						socket.close();
						System.out.println(e);
					}

				}
				
				// ���������������
				inStream = new DataInputStream(socket.getInputStream());

				outStream = new DataOutputStream(socket.getOutputStream());

				int intLen; // ����������

				// ��ʼ��MyCards
				if (myCards == null)
					myCards = new MyCards();
				if (persons == null)
					persons = new Persons();

				System.out.println("into INITIAL");

				myCards.clearAll();
				
				//��ʼ��Datas
				datas = new Datas();

				//����ע����Ϣ
				//sendMsg = new String("reg: " + args[4] + " guagua need_notify \n");
				sendMsg = new String("reg: " + args[4] + " player1 need_notify \n");
				myId = Integer.parseInt(args[4]);

				outStream.write(sendMsg.getBytes());
				//ָʾ
				System.out.println(sendMsg);

				ArrayList<byte[]> inlist = null;
				ArrayList<byte[]> inlist1 = null;
				ArrayList<byte[]> holdlist = null;
				ArrayList<byte[]> holdlist1 = null;

				//�����¼
				BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("info.txt"));
				

				// ״̬������
				while (true) {
					intLen = 0;

					saveBuf = new byte[2048];

					intLen = inStream.read(saveBuf);
					long time1 = System.currentTimeMillis();
					saveMsg = new String(saveBuf);
					if (intLen != -1) {
						if (saveMsg.contains("pot-win/")) {
							
							status = Status.SEAT;
						}

						if (saveMsg.contains("seat")) {
							System.out.println("seat");
							
							bufferedWriter.newLine();
							bufferedWriter.write("this is num " + iii++ + " game");
							bufferedWriter.flush();
							
							byte[] save = new byte[300];

							save = getByteArrays(saveBuf, seatbyte1, seatbyte2).get(0);

							inlist = getByteArrays(save, nbyte, nbyte);
							//��ȡ������
							personsAll = inlist.size();
							personsLeft = personsAll;
							
							//�½���λ˳���
							seatRound = new int[8];
							
							
							//����ǵ�һ����Ϸ�����¼��Ա�������������ݴ洢��
							if (iii == 1) {
								for(int i=0;i<inlist.size();i++){
									String st1 = new String(inlist.get(i));
									inlist1 = getByteArrays(inlist.get(i), _byte, _byte);
									RaiseData ra = new RaiseData();
									
									if (st1.contains("button")) {
										ra.pid = Integer.parseInt(new String(inlist1.get(0)));
									} else if (st1.contains("small")) {
										ra.pid = Integer.parseInt(new String(inlist1.get(1)));
									} else if (st1.contains("big blind")) {
										ra.pid = Integer.parseInt(new String(inlist1.get(1)));
									} else {
										int u = 0;
										String ss = new String();
										while (st1.charAt(u) != ' ') {
											ss += st1.charAt(u++);
										}
										ra.pid = Integer.parseInt(ss);
									}
									seatRound[i] = ra.pid;
									datas.raiseDatas.add(ra);
									System.out.println("datas.raiseDatas has add " + datas.raiseDatas.size());
									bufferedWriter.newLine();
									bufferedWriter.write("datas.raiseDatas has add " + datas.raiseDatas.size());
									bufferedWriter.flush();
								}
							}else {
								for(int i=0;i<inlist.size();i++){
									String st1 = new String(inlist.get(i));
									inlist1 = getByteArrays(inlist.get(i), _byte, _byte);
									int Id =0;
									
									if (st1.contains("button")) {
										Id = Integer.parseInt(new String(inlist1.get(0)));
									} else if (st1.contains("small")) {
										Id = Integer.parseInt(new String(inlist1.get(1)));
									} else if (st1.contains("big blind")) {
										Id = Integer.parseInt(new String(inlist1.get(1)));
									} else {
										int u = 0;
										String ss = new String();
										while (st1.charAt(u) != ' ') {
											ss += st1.charAt(u++);
										}
										Id = Integer.parseInt(ss);
									}
									seatRound[i] = Id;
								}
							}

							for (int i = 0; i < inlist.size(); i++) {
								String str = new String(inlist.get(i));
								if (str.contains(Integer.toString(myId))) {
									if (str.contains("button")) {
										mySeat = 3;//ׯ��
									} else if (str.contains("big blind")) {
										mySeat = 1;//��äע
									} else if (str.contains("small")) {
										mySeat = 0;//Сäע
									} else {
										mySeat = 2;//��ǰ
									}
								}
							}
							
							bTime = 0;
							fTime = 0;
							tTime = 0;
							rTime = 0;

							status = Status.BLIND;
						}
						if (saveMsg.contains("blind/")) {
							System.out.println("blind");
							//�������äע��Ϣ�����л������ƽ׶�
							status = Status.HOLD;
						}
						if (saveMsg.contains("hold/")) {
							System.out.println("hold");
							//���ճ����ֱ�Ӱ�hold֮���������ȡ�����������洢

							bList = getByteArrays(saveBuf, holdbyte, holdbyte);
							if (bList.size() != 0) {
								holdlist = getByteArrays(bList.get(0), nbyte, nbyte);
								myCards.holdCard1.setColor(JudgeColor(holdlist.get(0)));
								myCards.holdCard2.setColor(JudgeColor(holdlist.get(1)));
								holdlist1 = getByteArrays(holdlist.get(0), _byte, _byte);
								myCards.holdCard1.setPoint(JudgePoint(holdlist1.get(0)));
								holdlist1 = getByteArrays(holdlist.get(1), _byte, _byte);
								myCards.holdCard2.setPoint(JudgePoint(holdlist1.get(0)));

								bufferedWriter.newLine();
								bufferedWriter.write("hold1 " + myCards.holdCard1.getPoint() + "  "
										+ myCards.holdCard1.getColor());
								bufferedWriter.flush();
								bufferedWriter.newLine();
								bufferedWriter.write("hold2 " + myCards.holdCard2.getPoint() + "  "
										+ myCards.holdCard2.getColor());
								bufferedWriter.flush();

							} else {
								System.out.println("ճ��ʱ hold ���ƽ�������");
							}
							//�������������Ϣ�����л�������ǰ״̬
							status = Status.BEFOREFLOP;

						}
						if (saveMsg.contains("flop/")) {
							System.out.println("flop");
							bList = getByteArrays(saveBuf, flopbyte, flopbyte);
							try {
								inlist = getByteArrays(bList.get(0), nbyte, nbyte);
								myCards.flopCard1.setColor(JudgeColor(inlist.get(0)));
								myCards.flopCard2.setColor(JudgeColor(inlist.get(1)));
								myCards.flopCard3.setColor(JudgeColor(inlist.get(2)));
								inlist1 = getByteArrays(inlist.get(0), _byte, _byte);
								myCards.flopCard1.setPoint(JudgePoint(inlist1.get(0)));
								inlist1 = getByteArrays(inlist.get(1), _byte, _byte);
								myCards.flopCard2.setPoint(JudgePoint(inlist1.get(0)));
								inlist1 = getByteArrays(inlist.get(2), _byte, _byte);
								myCards.flopCard3.setPoint(JudgePoint(inlist1.get(0)));

								bufferedWriter.newLine();
								bufferedWriter.write("flop1 " + myCards.flopCard1.getPoint() + "  "
										+ myCards.flopCard1.getColor());
								bufferedWriter.flush();
								bufferedWriter.newLine();
								bufferedWriter.write("flop2 " + myCards.flopCard2.getPoint() + "  "
										+ myCards.flopCard2.getColor());
								bufferedWriter.flush();
								bufferedWriter.newLine();
								bufferedWriter.write("flop3 " + myCards.flopCard3.getPoint() + "  "
										+ myCards.flopCard3.getColor());
								bufferedWriter.flush();

							} catch (Exception e) {

								System.out.println("catch exception when beforeflop flop_msg" + e);
							}

							//ת��������Ȧ
							status = Status.FLOP;
						}

						if (saveMsg.contains("turn/")) {
							System.out.println("turn");
							bList = getByteArrays(saveBuf, turnbyte, turnbyte);
							try {
								inlist = getByteArrays(bList.get(0), nbyte, new String("\n/").getBytes());
								myCards.turnCard.setColor(JudgeColor(inlist.get(0)));
								inlist1 = getByteArrays(inlist.get(0), _byte, _byte);
								myCards.turnCard.setPoint(JudgePoint(inlist1.get(0)));

								bufferedWriter.newLine();
								bufferedWriter.write("turn " + myCards.turnCard.getPoint() + "  "
										+ myCards.turnCard.getColor());
								bufferedWriter.flush();

							} catch (Exception e) {

								System.out.println("catch exception when flop turn_msg" + e);
							}

							//ת��������Ȧ
							status = Status.TURN;
						}

						if (saveMsg.contains("river/")) {
							System.out.println("river");
							bList = getByteArrays(saveBuf, riverbyte, riverbyte);
							try {
								inlist = getByteArrays(bList.get(0), nbyte, new String("\n/").getBytes());
								myCards.riverCard.setColor(JudgeColor(inlist.get(0)));
								inlist1 = getByteArrays(inlist.get(0), _byte, _byte);
								myCards.riverCard.setPoint(JudgePoint(inlist1.get(0)));

								bufferedWriter.newLine();
								bufferedWriter.write("river " + myCards.riverCard.getPoint() + "  "
										+ myCards.riverCard.getColor());
								bufferedWriter.flush();
								bufferedWriter.newLine();
								bufferedWriter.write("\n");
								bufferedWriter.flush();

							} catch (Exception e) {

								System.out.println("catch exception when flop river_msg" + e);
							}

							//ת��������Ȧ
							status = Status.RIVER;
						}

						if (saveMsg.contains("inquire")) {
							System.out.println("inquire");
							int raiseMoney =0;
							int mybet = 0;
							int maxbet = 0;
							bList = getByteArrays(saveBuf, inquirebyte, inquirebyte);
							if (bList.size() != 0) {
								try {

									persons.personListold.removeAll(persons.personListold);

									if (IsFirstInquire) {
										IsFirstInquire = false;
									} else {
										persons.personListold = (ArrayList<person>) persons.personList.clone();
									}

									persons.personList.removeAll(persons.personList);

									inlist = getByteArrays(bList.get(0), nbyte, nbyte);

									for (int j = 0; j < inlist.size() - 1; j++) {
										person p = new person();

										int u = 0;
										String st = new String(inlist.get(j));
										String ss = new String();
										while (st.charAt(u) != ' ') {
											ss += st.charAt(u++);
										}
										p.pid = Integer.parseInt(ss);

										inlist1 = getByteArrays(inlist.get(j), _byte, _byte);
										String[] s = new String[4];

										for (int k = 0; k < 4; k++) {
											s[k] = new String(inlist1.get(k));
										}
										p.jetton = Integer.valueOf(s[0]);
										p.money = Integer.valueOf(s[1]);
										p.bet = Integer.valueOf(s[2]);
										p.action = judgeAction(s[3]);
										
										persons.personList.add(p);

										if (IsFirstInquire) {
											persons.personListold.add(p);
										}

										for (person pso : persons.personListold) {
											if (pso.pid == p.pid) {
												p.raiseJetton = p.bet - pso.bet;
											}
										}

										personsLeft = persons.getperonsLeft(personsAll);
										//��¼datas��Ϊ���жϷ蹷�����ڣ�
										for (int l = 0; l < datas.raiseDatas.size(); l++) {
											if (datas.raiseDatas.get(l).pid == p.pid) {
												if (p.raiseJetton > 0) {
													int j1 = 0;
													switch (status) {
														case BEFOREFLOP: {
															j1 = 0;
														}
															break;
														case FLOP: {
															j1 = 1;
														}
															break;
														case TURN: {
															j1 = 2;
														}
															break;
														case RIVER: {
															j1 = 3;
														}
															break;
														default:
															break;
													}
													datas.raiseDatas.get(l).raiseTime[j1]++;
													if (datas.raiseDatas.get(l).minRaise[j1] == 0) {
														datas.raiseDatas.get(l).minRaise[j1] = p.raiseJetton;
													} else {
														if (p.raiseJetton < datas.raiseDatas.get(l).minRaise[j1]) {
															datas.raiseDatas.get(l).minRaise[j1] = p.raiseJetton;
														}
													}
													if (datas.raiseDatas.get(l).maxRaise[j1] == 0) {
														datas.raiseDatas.get(l).maxRaise[j1] = p.raiseJetton;
													} else {
														if (p.raiseJetton > datas.raiseDatas.get(l).maxRaise[j1]) {
															datas.raiseDatas.get(l).maxRaise[j1] = p.raiseJetton;
														}
													}
													if (datas.raiseDatas.get(l).averRaise[j1] == 0) {
														datas.raiseDatas.get(l).averRaise[j1] = p.raiseJetton;
													} else {
														datas.raiseDatas.get(l).averRaise[j1] = (int) ((float) (datas.raiseDatas
																.get(l).averRaise[j1]
																* (datas.raiseDatas.get(l).raiseTime[j1] - 1) + p.raiseJetton) / (float) (datas.raiseDatas
																.get(l).raiseTime[j1]));
													}
												}
											}
										}
									}
									//�ٰ��ջس����룬��ȡtotal pot

									inlist1 = getByteArrays(inlist.get(inlist.size() - 1), _byte, _byte);
									String s = new String(inlist1.get(inlist1.size() - 1));
									persons.totalpot = Integer.valueOf(s);

									
									int mm = 0;
									for (person po : persons.personList) {
										if (po.pid == myId) {
											mybet = po.bet;
										}
									}

									mm = mybet;
									for (person po : persons.personList) {
										if (po.bet >= mm) {
											maxbet = po.bet;
											mm = maxbet;
										}
									}

									raiseMoney = maxbet - mybet;

								} catch (Exception e) {

									System.out.println("catch exception when river inquire" + e);
									e.printStackTrace();
								}
							}

							//�˴�������Ա��Ϣ��������
							//���������͸�����Ϣ
							float value=0;
							float weight = 0;
//							if (iii < 20) {
//								value = JudgeTwoCard(myCards);
//								if(value>=7){
//									int s = randomNum(320, 460);
//									sendMsg = new String("raise " + s);
//									
//								}else if (value>=12) {
//									int s = randomNum(420, 560);
//									sendMsg = new String("raise " + s);
//								}
//								else {
//									sendMsg = new String("fold");
//								}
//								outStream.write(sendMsg.getBytes());
//							} else {
							switch (status) {
								case BEFOREFLOP: {
									bTime++;

									value = JudgeTwoCard(myCards);

									weight = getTwoCardWeight(persons, raiseMoney);

									//										if (mySeat == 3 && bTime == 1 && raiseMoney == 40) {
									//											int s = randomNum(120, 160);
									//											sendMsg = new String("raise " + s);
									//										} else if (mySeat == 1 && bTime == 1 && raiseMoney == 0) {
									//											int s = randomNum(120, 160);
									//											sendMsg = new String("raise " + s);
									//										} else if (mySeat == 0 && bTime == 1 && raiseMoney == 20) {
									//											int s = randomNum(120, 160);
									//											sendMsg = new String("raise " + s);
									//										} else {

									if (value >= weight) {
										sendMsg = new String("call");
										if (value >= 10) {
											int s = randomNum(81, 120);
											sendMsg = new String("raise " + s);
										}
										if (value >= 14) {
											int s = randomNum(80, 130);
											sendMsg = new String("raise " + s);
										}
										if (value >= 16) {
											int s = randomNum(150, 220);
											sendMsg = new String("raise " + s);
										}
										
										if (bTime > 2) {
											sendMsg = new String("call");
										}

									} else {
										sendMsg = new String("fold");

									}
									

//									if (JudgePersonBigMad()) {
//										bufferedWriter.newLine();
//										bufferedWriter.write("\t\t\t\tJudgePersonBigMad = " + "true");
//										bufferedWriter.flush();
//										if (value >= 7) {
//											sendMsg = new String("call");
//										}
//									}
									outStream.write(sendMsg.getBytes());
								}

									break;
								case FLOP: {
									fTime++;
									
									value = JudgeFiveCard(myCards);
									weight = getFiveCardWeight(persons, raiseMoney);

									if (value >= weight) {
										sendMsg = new String("call");
//										if (value >= 0.8) {
//											int s = randomNum(50, 79);
//											sendMsg = new String("raise " + s);
//										}
										if (value >= 0.88) {
											int s = randomNum(70, 129);
											sendMsg = new String("raise " + s);
										}
										if (value >= 0.95) {
											int s = randomNum(70, 159);
											sendMsg = new String("raise " + s);
										}
										
										if (fTime > 2) {
											sendMsg = new String("call");
										}
									} else {
										sendMsg = new String("fold");

									}
//									if (JudgePersonBigMad()) {
//										bufferedWriter.newLine();
//										bufferedWriter.write("\t\t\t\tJudgePersonBigMad = " + "true");
//										bufferedWriter.flush();
//										if (value >= 0.6) {
//											sendMsg = new String("call");
//										}
//									}
									outStream.write(sendMsg.getBytes());
								}

									break;
								case TURN: {
									tTime++;

									value = JudgeSixCard(myCards);
									weight = getSixCardWeight(persons, raiseMoney);

									if (value >= weight) {
										sendMsg = new String("call");
//										if (value >= 0.80) {
//											int s = randomNum(50, 79);
//											sendMsg = new String("raise " + s);
//										}
										if (value >= 0.88) {
											int s = randomNum(70, 129);
											sendMsg = new String("raise " + s);
										}
										if (value >= 0.95) {
											int s = randomNum(159, 210);
											sendMsg = new String("raise " + s);
										}
										if (value >= 0.99) {
											int s = randomNum(220, 320);
											sendMsg = new String("raise " + s);
										}
										
										if (tTime > 2) {
											sendMsg = new String("call");
										}
									} else {
										sendMsg = new String("fold");

									}
//									if (JudgePersonBigMad()) {
//										bufferedWriter.newLine();
//										bufferedWriter.write("\t\t\t\tJudgePersonBigMad = " + "true");
//										bufferedWriter.flush();
//										if (value >= 0.6) {
//											sendMsg = new String("call");
//										}
//									}
									outStream.write(sendMsg.getBytes());
								}

									break;
								case RIVER: {
									rTime++;

									value = JudgeSevenCard(myCards);
									weight = getSevenCardWeight(persons, raiseMoney);

									if (value >= weight) {
										sendMsg = new String("call");
										if (value >= 0.8 && value < 0.90) {
											int s = randomNum(40, 80);
											sendMsg = new String("raise " + s);
										}
										if (value >= 0.90 && value < 0.95) {
											int s = randomNum(60, 120);
											sendMsg = new String("raise " + s);
										}
										if (value >= 0.95 && value < 0.98) {
											int s = randomNum(100, 150);
											sendMsg = new String("raise " + s);
										}
										if (value >= 0.98 && value < 0.996) {
											int s = randomNum(250, 300);
											sendMsg = new String("raise " + s);
										}
										if (value >= 0.996) {
											int s = randomNum(400, 550);
											sendMsg = new String("raise " + s);
										}
										if (value > 0.999) {
											int s = randomNum(550, 1020);
											sendMsg = new String("raise " + s);
										}
										if (rTime > 2) {
											sendMsg = new String("call");
										}
									} else {
										sendMsg = new String("fold");

									}
//									if (JudgePersonBigMad()) {
//										bufferedWriter.newLine();
//										bufferedWriter.write("\t\t\t\tJudgePersonBigMad = " + "true");
//										bufferedWriter.flush();
//										if (value >= 0.6) {
//											sendMsg = new String("call");
//										}
//									}
									outStream.write(sendMsg.getBytes());
								}

									break;
								default: {
									sendMsg = new String("fold");
									outStream.write(sendMsg.getBytes());
								}
									break;
							}

							persons.show(bufferedWriter, persons);
							bufferedWriter.newLine();
							bufferedWriter.write("\t\tvalue = " +value);
							bufferedWriter.newLine();
							bufferedWriter.write("\t\tweight = "+weight);
							bufferedWriter.newLine();
							bufferedWriter.write("\t\tmaxbet = "+maxbet);
							bufferedWriter.newLine();
							bufferedWriter.write("\t\tmybet = "+mybet);
							bufferedWriter.newLine();
							bufferedWriter.write("\t\traiseMoney = "+raiseMoney);
							bufferedWriter.newLine();
							bufferedWriter.write("\t\t\tTimes are = " + bTime + "\t" + fTime + "\t" + tTime + "\t"
									+ rTime + "\t");
							bufferedWriter.flush();
							bufferedWriter.newLine();
							bufferedWriter.write("\t\tsendMsg msg = " + sendMsg);
							bufferedWriter.flush();
							

							for (RaiseData ra : datas.raiseDatas) {
								bufferedWriter.newLine();
								bufferedWriter.write("\t\t\t = " + ra.pid + "\t" + ra.averRaise[0] + "\t"
										+ ra.averRaise[1] + "\t" + ra.averRaise[2] + "\t" + ra.averRaise[3]);
							}
							for (RaiseData ra : datas.raiseDatas) {
								bufferedWriter.newLine();
								bufferedWriter.write("\t\t\t = " + ra.pid + "\t" + ra.maxRaise[0] + "\t"
										+ ra.maxRaise[1] + "\t" + ra.maxRaise[2] + "\t" + ra.maxRaise[3]);
							}
							for (RaiseData ra : datas.raiseDatas) {
								bufferedWriter.newLine();
								bufferedWriter.write("\t\t\t = " + ra.pid + "\t" + ra.minRaise[0] + "\t"
										+ ra.minRaise[1] + "\t" + ra.minRaise[2] + "\t" + ra.minRaise[3]);
							}
							for (RaiseData ra : datas.raiseDatas) {
								bufferedWriter.newLine();
								bufferedWriter.write("\t\t\t\t = " + ra.pid + "\t" + ra.raiseTime[0] + "\t"
										+ ra.raiseTime[1] + "\t" + ra.raiseTime[2] + "\t" + ra.raiseTime[3]);
							}
							bufferedWriter.flush();
							
						}
						
						if (saveMsg.contains("notify")) {
							System.out.println("notify");
							int raiseMoney =0;
							int mybet = 0;
							int maxbet = 0;
							bList = getByteArrays(saveBuf, notifybyte, notifybyte);
							if (bList.size() != 0) {
								try {
									
									persons.personListold.removeAll(persons.personListold);
									
									if(IsFirstInquire){
										IsFirstInquire =false;
									}else {
										persons.personListold =(ArrayList<person>) persons.personList.clone();
									}
									
									persons.personList.removeAll(persons.personList);
									
									inlist = getByteArrays(bList.get(0), nbyte, nbyte);

									for (int j = 0; j < inlist.size() - 1; j++) {
										person p = new person();

										int u = 0;
										String st = new String(inlist.get(j));
										String ss = new String();
										while (st.charAt(u) != ' ') {
											ss += st.charAt(u++);
										}
										p.pid = Integer.parseInt(ss);

										inlist1 = getByteArrays(inlist.get(j), _byte, _byte);
										String[] s = new String[4];

										for (int k = 0; k < 4; k++) {
											s[k] = new String(inlist1.get(k));
										}
										p.jetton = Integer.valueOf(s[0]);
										p.money = Integer.valueOf(s[1]);
										p.bet = Integer.valueOf(s[2]);
										p.action = judgeAction(s[3]);
										
										
										persons.personList.add(p);
										if(IsFirstInquire){
											persons.personListold.add(p);
										}
										
										
										for (person pso : persons.personListold) {
											if(pso.pid == p.pid){
												p.raiseJetton = p.bet - pso.bet;
											}
										}
										
										personsLeft = persons.getperonsLeft(personsAll);
										
										//��¼datas��Ϊ���жϷ蹷�����ڣ�
										for (int l = 0; l < datas.raiseDatas.size(); l++) {
											if (datas.raiseDatas.get(l).pid == p.pid) {
												if (p.raiseJetton > 0) {
													int j1 = 0;
													switch (status) {
														case BEFOREFLOP: {
															j1 = 0;
														}
															break;
														case FLOP: {
															j1 = 1;
														}
															break;
														case TURN: {
															j1 = 2;
														}
															break;
														case RIVER: {
															j1 = 3;
														}
															break;
														default:
															break;
													}
													datas.raiseDatas.get(l).raiseTime[j1]++;
													if (datas.raiseDatas.get(l).minRaise[j1] == 0) {
														datas.raiseDatas.get(l).minRaise[j1] = p.raiseJetton;
													} else {
														if (p.raiseJetton < datas.raiseDatas.get(l).minRaise[j1]) {
															datas.raiseDatas.get(l).minRaise[j1] = p.raiseJetton;
														}
													}
													if (datas.raiseDatas.get(l).maxRaise[j1] == 0) {
														datas.raiseDatas.get(l).maxRaise[j1] = p.raiseJetton;
													} else {
														if (p.raiseJetton > datas.raiseDatas.get(l).maxRaise[j1]) {
															datas.raiseDatas.get(l).maxRaise[j1] = p.raiseJetton;
														}
													}
													if (datas.raiseDatas.get(l).averRaise[j1] == 0) {
														datas.raiseDatas.get(l).averRaise[j1] = p.raiseJetton;
													} else {
														datas.raiseDatas.get(l).averRaise[j1] = (int) ((float) (datas.raiseDatas
																.get(l).averRaise[j1]
																* (datas.raiseDatas.get(l).raiseTime[j1] - 1) + p.raiseJetton) / (float) (datas.raiseDatas
																.get(l).raiseTime[j1]));
													}
												}
											}

										}

									}
									//�ٰ��ջس����룬��ȡtotal pot

									inlist1 = getByteArrays(inlist.get(inlist.size() - 1), _byte, _byte);
									String s = new String(inlist1.get(inlist1.size() - 1));
									persons.totalpot = Integer.valueOf(s);

									//	raiseMoney = persons.totalpot-personsOld.totalpot;
									
									int mm = 0;
									for (person po : persons.personList) {
										if (po.pid == myId) {
											mybet = po.bet;
										}
									}

									mm = mybet;
									for (person po : persons.personList) {
										if (po.bet >= mm) {
											maxbet = po.bet;
											mm = maxbet;
										}
									}

									raiseMoney = maxbet - mybet;

								} catch (Exception e) {

									System.out.println("catch exception when inquire" + e);
									e.printStackTrace();
								}
							}

							//�˴�������Ա��Ϣ��������
							//���������͸�����Ϣ
							
//							persons.show(bufferedWriter, persons);
//							bufferedWriter.newLine();
//							bufferedWriter.write("\t\tmaxbet = "+maxbet);
//							bufferedWriter.newLine();
//							bufferedWriter.write("\t\tmybet = "+mybet);
//							bufferedWriter.newLine();
//							bufferedWriter.write("\t\traiseMoney = "+raiseMoney);
//							bufferedWriter.flush();
//							bufferedWriter.newLine();
//							bufferedWriter.write("\t\tsendMsg msg = " + sendMsg);
//							bufferedWriter.flush();
//							
//							for (RaiseData ra : datas.raiseDatas) {
//								bufferedWriter.newLine();
//								bufferedWriter.write("\t\t\t = " + ra.pid + "\t" + ra.averRaise[0] + "\t"
//										+ ra.averRaise[1] + "\t" + ra.averRaise[2] + "\t" + ra.averRaise[3]);
//							}
//							for (RaiseData ra : datas.raiseDatas) {
//								bufferedWriter.newLine();
//								bufferedWriter.write("\t\t\t = " + ra.pid + "\t" + ra.maxRaise[0] + "\t"
//										+ ra.maxRaise[1] + "\t" + ra.maxRaise[2] + "\t" + ra.maxRaise[3]);
//							}
//							for (RaiseData ra : datas.raiseDatas) {
//								bufferedWriter.newLine();
//								bufferedWriter.write("\t\t\t = " + ra.pid + "\t" + ra.minRaise[0] + "\t"
//										+ ra.minRaise[1] + "\t" + ra.minRaise[2] + "\t" + ra.minRaise[3]);
//							}
//							for (RaiseData ra : datas.raiseDatas) {
//								bufferedWriter.newLine();
//								bufferedWriter.write("\t\t\t\t = " + ra.pid + "\t" + ra.raiseTime[0] + "\t"
//										+ ra.raiseTime[1] + "\t" + ra.raiseTime[2] + "\t" + ra.raiseTime[3]);
//							}
//							bufferedWriter.flush();
							
						}

						if (saveMsg.contains("game-over")) {
							System.out.println("game-over");
							System.out.println("into GAMEOVER");

							inStream.close();
							outStream.close();

							socket.close();
							System.out.println("finally i will exit");
							System.exit(0);

						}
						if (saveMsg.contains("showdown")) {
							System.out.println("showdown");
							status = Status.END;

						}
					}
					long time2 = System.currentTimeMillis();
					System.out.println("time = " + (time2 - time1)+" ms ");
					
					bufferedWriter.newLine();
					bufferedWriter.write("time = " + (time2 - time1));
					bufferedWriter.flush();
				}

			} catch (Exception e) {
				System.out.println("exception : " + e);
				try {
					socket.close();
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
				e.printStackTrace();
				System.exit(0);
			}

		}

	}

	public static boolean JudgePersonBigMad() {
		if (iii > 20) {
			if (personsLeft <= 3) {
				for (person p : persons.personList) {
					if (p.pid != myId) {
						if (p.action != Action.fold) {
							for (RaiseData ra : datas.raiseDatas) {
								if (ra.pid == p.pid) {
									if (ra.averRaise[0] >= 250 || ra.averRaise[1] >= 350) {
										return true;
									}
								}
							}
						}
					}
				}
			}
		}

		return false;
	}
	
	

	public static int randomNum(int min, int max) {

		Random random = new Random();
		int s = random.nextInt(max) % (max - min + 1) + min;
		return s;
	}
	
	public static float getTwoCardWeight(Persons ps, int raisemoney) {
		float weight = 0;
		if (raisemoney > 399) {
			weight = twoCardsAllin[mySeat][personsLeft - 1];
			return weight;
		} else if (raisemoney > 239 && raisemoney <= 399) {
			weight = twoCardsRaiseMuchMore[mySeat][personsLeft - 1];
			return weight;
		} else if (raisemoney > 159 && raisemoney <= 239) {
			weight = twoCardsRaiseMuch[mySeat][personsLeft - 1];
			return weight;
		} else if (raisemoney > 80 && raisemoney <= 159) {
			weight = twoCardsRaise[mySeat][personsLeft - 1];
			return weight;
		} else if (raisemoney > 20 && raisemoney <= 80) {
			weight = twoCardsRaiseLittle[mySeat][personsLeft - 1];
			return weight;
		} else if (raisemoney <= 20) {
			weight = twoCardsCheck[mySeat][personsLeft - 1];
			return weight;
		}

		return weight;
		
	}
	
	public static float getSevenCardWeight(Persons ps,int raisemoney){
		double weight = 0;
		if (raisemoney >= 1059) {
			weight = fiveCardsAllin[mySeat][personsLeft - 1];
			return (float) weight;
		} else if (raisemoney >= 459 && raisemoney <= 1059) {
			weight = fiveCardsRaiseMuchMuchMore[mySeat][personsLeft - 1];
			return (float) weight;
		} else if (raisemoney >= 279 && raisemoney <= 459) {
			weight = fiveCardsRaiseMuchMore[mySeat][personsLeft - 1];
			return (float) weight;
		} else if (raisemoney >= 179 && raisemoney <= 279) {
			weight = fiveCardsRaiseMuch[mySeat][personsLeft - 1];
			return (float) weight;
		} else if (raisemoney >= 100 && raisemoney <= 179) {
			weight = fiveCardsRaise[mySeat][personsLeft - 1];
			return (float) weight;
		} else if (raisemoney >= 30 && raisemoney < 100) {
			weight = fiveCardsRaiseLittle[mySeat][personsLeft - 1];
			return (float) weight;
		} else if (raisemoney < 30) {
			weight = fiveCardsCheck[mySeat][personsLeft - 1];
		}

		return (float) weight;

	}
	
	public static float getSixCardWeight(Persons ps,int raisemoney){
		double weight = 0;
		if (raisemoney >= 559) {
			weight = fiveCardsAllin[mySeat][personsLeft - 1];
			return (float) weight;
		} else if (raisemoney >= 389 && raisemoney <= 559) {
			weight = fiveCardsRaiseMuchMuchMore[mySeat][personsLeft - 1];
			return (float) weight;
		} else if (raisemoney >= 269 && raisemoney <= 389) {
			weight = fiveCardsRaiseMuchMore[mySeat][personsLeft - 1];
			return (float) weight;
		} else if (raisemoney >= 179 && raisemoney <= 269) {
			weight = fiveCardsRaiseMuch[mySeat][personsLeft - 1];
			return (float) weight;
		} else if (raisemoney >= 100 && raisemoney <= 179) {
			weight = fiveCardsRaise[mySeat][personsLeft - 1];
			return (float) weight;
		} else if (raisemoney >= 30 && raisemoney < 100) {
			weight = fiveCardsRaiseLittle[mySeat][personsLeft - 1];
			return (float) weight;
		} else if (raisemoney < 30) {
			weight = fiveCardsCheck[mySeat][personsLeft - 1];
		}

		return (float) weight;

	}
	
	
	
	public static float getFiveCardWeight(Persons ps,int raisemoney){
		double weight = 0;
		if (raisemoney >= 659) {
			weight = fiveCardsAllin[mySeat][personsLeft - 1];
			return (float) weight;
		} else if (raisemoney >= 459 && raisemoney <= 659) {
			weight = fiveCardsRaiseMuchMuchMore[mySeat][personsLeft - 1];
			return (float) weight;
		} else if (raisemoney >= 299 && raisemoney <= 459) {
			weight = fiveCardsRaiseMuchMore[mySeat][personsLeft - 1];
			return (float) weight;
		} else if (raisemoney >= 199 && raisemoney <= 299) {
			weight = fiveCardsRaiseMuch[mySeat][personsLeft - 1];
			return (float) weight;
		} else if (raisemoney >= 100 && raisemoney <= 199) {
			weight = fiveCardsRaise[mySeat][personsLeft - 1];
			return (float) weight;
		} else if (raisemoney >= 30 && raisemoney < 100) {
			weight = fiveCardsRaiseLittle[mySeat][personsLeft - 1];
			return (float) weight;
		} else if (raisemoney < 30) {
			weight = fiveCardsCheck[mySeat][personsLeft - 1];
		}

		return (float) weight;

	}
	
//	public static float getFiveCardWeight(Persons ps,int raisemoney){
//		double weight = 0;
//		if (raisemoney >= 559) {
//			weight = fiveCardsAllin[mySeat][personsLeft - 1];
//			return (float) weight;
//		} else if (raisemoney >= 369 && raisemoney <= 559) {
//			weight = fiveCardsRaiseMuchMuchMore[mySeat][personsLeft - 1];
//			return (float) weight;
//		} else if (raisemoney >= 269 && raisemoney <= 369) {
//			weight = fiveCardsRaiseMuchMore[mySeat][personsLeft - 1];
//			return (float) weight;
//		} else if (raisemoney >= 179 && raisemoney <= 269) {
//			weight = fiveCardsRaiseMuch[mySeat][personsLeft - 1];
//			return (float) weight;
//		} else if (raisemoney >= 80 && raisemoney <= 179) {
//			weight = fiveCardsRaise[mySeat][personsLeft - 1];
//			return (float) weight;
//		} else if (raisemoney >= 30 && raisemoney < 80) {
//			weight = fiveCardsRaiseLittle[mySeat][personsLeft - 1];
//			return (float) weight;
//		} else if (raisemoney < 30) {
//			weight = fiveCardsCheck[mySeat][personsLeft - 1];
//		}
//
//		return (float) weight;
//
//	}

	public static int JudgeTwoCard(MyCards myCards) {
		double i = 0;
		int max = (myCards.holdCard1.getPoint() >= myCards.holdCard2.getPoint()) ? myCards.holdCard1.getPoint()
				: myCards.holdCard2.getPoint();
		int min = (myCards.holdCard1.getPoint() <= myCards.holdCard2.getPoint()) ? myCards.holdCard1.getPoint()
				: myCards.holdCard2.getPoint();
		int a = max - min;

		if (myCards.holdCard1.getPoint() == myCards.holdCard2.getPoint()) {
			switch (myCards.holdCard1.getPoint()) {
				case 12: {
					i = 20;
				}
					break;
				case 11: {
					i = 16;
				}
					break;
				case 10: {
					i = 14;
				}
					break;
				case 9: {
					i = 12;
				}
					break;
				default: {
					i = (myCards.holdCard1.getPoint() + 2);
				}
					break;
			}
			if (i < 5)
				i = 5;
		} else {

			switch (max) {
				case 12: {
					i = 10;
				}
					break;
				case 11: {
					i = 8;
				}
					break;
				case 10: {
					i = 7;
				}
					break;
				case 9: {
					i = 6;
				}
					break;
				default: {
					i = max / 2;
				}
					break;
			}
			if (a < 2) {
				;
			} else if (a == 2) {
				i -= 1;
			} else if (a == 3) {
				i -= 2;
			} else if (a == 4) {
				i -= 4;
			} else if (a >= 5) {
				i -= 5;
			} else {

			}
		}
		if (myCards.holdCard1.getColor() == myCards.holdCard2.getColor()) {
			i += 2;
		}
		if (a < 3 && max < 10) {
			i += 1;
		}

		return (int) Math.ceil(i);
	}

	public static float JudgeFiveCard(MyCards myCards) {
		int[] handcard = new int[2];
		int[] flopcard = new int[3];
		handcard[0] = CardCodingValue.CardCodingValue[(12 - myCards.holdCard1.getPoint()) * 4
				+ myCards.holdCard1.getColor()];
		handcard[1] = CardCodingValue.CardCodingValue[(12 - myCards.holdCard2.getPoint()) * 4
				+ myCards.holdCard2.getColor()];
		flopcard[0] = CardCodingValue.CardCodingValue[(12 - myCards.flopCard1.getPoint()) * 4
				+ myCards.flopCard1.getColor()];
		flopcard[1] = CardCodingValue.CardCodingValue[(12 - myCards.flopCard2.getPoint()) * 4
				+ myCards.flopCard2.getColor()];
		flopcard[2] = CardCodingValue.CardCodingValue[(12 - myCards.flopCard3.getPoint()) * 4
				+ myCards.flopCard3.getColor()];

		return CardByXST.HAND_STRENGTH(handcard, flopcard);

	}

	public static float JudgeSixCard(MyCards myCards) {
		int[] handcard = new int[2];
		int[] flopcard = new int[4];

		handcard[0] = CardCodingValue.CardCodingValue[(12 - myCards.holdCard1.getPoint()) * 4
				+ myCards.holdCard1.getColor()];
		handcard[1] = CardCodingValue.CardCodingValue[(12 - myCards.holdCard2.getPoint()) * 4
				+ myCards.holdCard2.getColor()];
		flopcard[0] = CardCodingValue.CardCodingValue[(12 - myCards.flopCard1.getPoint()) * 4
				+ myCards.flopCard1.getColor()];
		flopcard[1] = CardCodingValue.CardCodingValue[(12 - myCards.flopCard2.getPoint()) * 4
				+ myCards.flopCard2.getColor()];
		flopcard[2] = CardCodingValue.CardCodingValue[(12 - myCards.flopCard3.getPoint()) * 4
				+ myCards.flopCard3.getColor()];
		flopcard[3] = CardCodingValue.CardCodingValue[(12 - myCards.turnCard.getPoint()) * 4
				+ myCards.turnCard.getColor()];

		return CardByXST.HAND_STRENGTH(handcard, flopcard);

	}

	public static float JudgeSevenCard(MyCards myCards) {
		int[] handcard = new int[2];
		int[] flopcard = new int[5];
		handcard[0] = CardCodingValue.CardCodingValue[(12 - myCards.holdCard1.getPoint()) * 4
				+ myCards.holdCard1.getColor()];
		handcard[1] = CardCodingValue.CardCodingValue[(12 - myCards.holdCard2.getPoint()) * 4
				+ myCards.holdCard2.getColor()];
		flopcard[0] = CardCodingValue.CardCodingValue[(12 - myCards.flopCard1.getPoint()) * 4
				+ myCards.flopCard1.getColor()];
		flopcard[1] = CardCodingValue.CardCodingValue[(12 - myCards.flopCard2.getPoint()) * 4
				+ myCards.flopCard2.getColor()];
		flopcard[2] = CardCodingValue.CardCodingValue[(12 - myCards.flopCard3.getPoint()) * 4
				+ myCards.flopCard3.getColor()];
		flopcard[3] = CardCodingValue.CardCodingValue[(12 - myCards.turnCard.getPoint()) * 4
				+ myCards.turnCard.getColor()];
		flopcard[4] = CardCodingValue.CardCodingValue[(12 - myCards.riverCard.getPoint()) * 4
				+ myCards.riverCard.getColor()];

		return CardByXST.HAND_STRENGTH(handcard, flopcard);

	}

	public static float JudgeCard() {
		switch (status) {
			case BEFOREFLOP: {
				return 0;
			}

			case FLOP: {
				return JudgeFiveCard(myCards);
			}

			case TURN: {
				return JudgeSixCard(myCards);
			}

			case RIVER: {
				return JudgeSevenCard(myCards);
			}

			default:
				return -1;

		}

	}


	public static int JudgePoint(byte b[]) {
		switch (b[0]) {
			case '2':
				return 0;
			case '3':
				return 1;
			case '4':
				return 2;
			case '5':
				return 3;
			case '6':
				return 4;
			case '7':
				return 5;
			case '8':
				return 6;
			case '9':
				return 7;
			case '1':
				return 8;
			case 'J':
				return 9;
			case 'Q':
				return 10;
			case 'K':
				return 11;
			case 'A':
				return 12;
			default:
				break;
		}
		return -1;

	}

	public static int JudgeColor(byte b[]) {
		switch (b[0]) {
			case 'S': {
				return 3;
			}
			case 'H': {
				return 2;
			}
			case 'C': {
				return 0;
			}
			case 'D': {
				return 1;
			}
			default:
				break;
		}
		return -1;
	}

	public static Action judgeAction(String s) {
		if (s.contains("blind"))
			return Action.blind;
		else if (s.contains("check"))
			return Action.check;
		else if (s.contains("call"))
			return Action.call;
		else if (s.contains("raise"))
			return Action.raise;
		else if (s.contains("all_in"))
			return Action.all_in;
		else if (s.contains("fold"))
			return Action.fold;
		else {
			return null;
		}

	}

	/**
	 * ��byte1 Ϊ��ʼ�� byte2Ϊ����������toBeSplit��
	 * 
	 */
	public static ArrayList<byte[]> getByteArrays(byte[] toBeSplit, byte[] byte1, byte[] byte2) {
		ArrayList<byte[]> listByte = new ArrayList<byte[]>();
		if (toBeSplit.length <= byte1.length + byte2.length) {
			System.out.println("��������̫�٣�");
			return listByte; //listByte Ϊ�ա�
		}
		byte[] temp = null;
		int start = 0;
		int end = 0;

		for (int i = 0; i < toBeSplit.length - (byte1.length + byte2.length); i++) {
			//<span style="color: #FF0000;">���Ч��</span>
			if (toBeSplit[i] != byte1[0]) {//�׸��ֽ�ƥ���
				continue;
			}
			//��ѭ����ƥ����ʼ����
			byte[] tempByteArrayStart = Arrays.copyOfRange(toBeSplit, i, i + byte1.length);
			if (Arrays.equals(tempByteArrayStart, byte1)) {
				start = i;
				//��ѭ����ƥ���������
				for (int j = start + byte1.length; j < toBeSplit.length - byte2.length + 1; j++) {
					//<span style="color: #FF0000;">�ж��׸��ֽ�, �����Ч��</span>
					if (toBeSplit[j] != byte2[0]) {
						continue;
					}
					byte[] tempByteArrayEnd = Arrays.copyOfRange(toBeSplit, j, j + byte2.length);
					//�ҵ�ǰ��ƥ�䣬��ȡ�м䲿�֣���һ����������list�
					if (Arrays.equals(tempByteArrayEnd, byte2)) {
						end = j;
						temp = Arrays.copyOfRange(toBeSplit, start + byte1.length, end);
						listByte.add(temp);
						i = (end + byte1.length - 1 - byte1.length);
						break;
					}
				}
				if (i == start) {//��ȣ�˵���ҵ������Ҳû���ҵ����������ѭ���� ���򣬼���Ѱ�ҡ�
					break;
				}
			}
		}
		return listByte;
	}

}

class MyCards {

	//���캯�����޲�����ʼ�������п�ֵΪ-1
	public MyCards() {
		this.holdCard1 = new Card();
		this.holdCard2 = new Card();
		this.flopCard1 = new Card();
		this.flopCard2 = new Card();
		this.flopCard3 = new Card();
		this.turnCard = new Card();
		this.riverCard = new Card();
	}

	public void clearAll() {
		this.holdCard1.clearAll();
		this.holdCard2.clearAll();
		this.flopCard1.clearAll();
		this.flopCard2.clearAll();
		this.flopCard3.clearAll();
		this.turnCard.clearAll();
		this.riverCard.clearAll();
	}

	//����1
	public Card holdCard1;
	//����2
	public Card holdCard2;
	//����1
	public Card flopCard1;
	//����2
	public Card flopCard2;
	//����3
	public Card flopCard3;
	//ת��
	public Card turnCard;
	//����
	public Card riverCard;

}

//һ�ſ������ݽṹ
class Card {

	//���캯��
	//�����ֵ�ͷ�����ֵ�����û�и�ֵ�򷵻�-1
	public Card() {
		this.card = -1;
		this.point = -1;
		this.color = -1;
	}

	public Card(int card) {
		this.card = card;
		this.point = card % 13;
		this.color = card / 13;
	}

	public Card(int point, int color) {
		this.point = point;
		this.color = color;
		this.card = color * 13 + point;
	}

	public void clearAll() {
		this.card = -1;
		this.point = -1;
		this.color = -1;
	}

	/*
	 * card ���ţ�0--51 
	 * card = color * 13 + point;
	 */
	private int card;

	public int getCard() {
		return card;
	}

	public void setCard(int card) {
		this.card = card;
	}

	/*
	 * point Ϊ���ϵĵ����� 
	 * 0=��2����1=��3��������8=��10����9=��J����10=��Q����11=��K����12=��A�� 
	 * point = card % 13;
	 */
	private int point;

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	/*
	 * color Ϊ������ɫ
	 * 0 = CULBS��÷����
	 * 1 = DIAMONDS����Ƭ��
	 * 2 = HEARTS�����ң�
	 * 3 = SPADES�����ң�
	 * color = card / 13;
	 */
	private int color;

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

}

class Persons implements Cloneable{
	//������Ա��Ϣ������ʱ�������ɽ���Զ���У��ϼ�ΪpersonList[0]��
		ArrayList<person> personList;
		//������Ա��Ϣ������ʱ�������ɽ���Զ���У��ϼ�ΪpersonList[0]�������ڱ�����һ����Ϣ��
		ArrayList<person> personListold;
		//�ʳ��ܽ��
		int totalpot;

		public int raiseMoney;

		public Persons() {
			personList = new ArrayList<person>();
			personListold = new ArrayList<person>();
		}
		
		public void show(BufferedWriter bufferedWriter,Persons ps) throws IOException {
			for (person p : ps.personList) {
				bufferedWriter.newLine();
				bufferedWriter.write(p.pid + "\t" + p.jetton + "\t" + p.money + "\t" + p.bet + "\t" + p.action);
				bufferedWriter.flush();
			}
			for (person p : ps.personListold) {
				bufferedWriter.newLine();
				bufferedWriter.write("\t" + p.pid + "\t" + p.jetton + "\t" + p.money + "\t" + p.bet + "\t" + p.action);
				bufferedWriter.flush();
			}

		}


	public int getperonsLeft(int personsall) {
		int left = personsall;
		for (int i = 0; i < personList.size(); i++) {
			if (personList.get(i).action == game.Action.fold) {
				left--;
			}
		}
		return left;
	}

	public void clearAll() {
		personList.removeAll(personList);
	}
}

class person {
	public int pid;
	public int jetton;
	public int money;
	public int bet;
	public game.Action action;
	public int raiseJetton;

}

class RaiseData {
	public int pid;
	
	//��ע��� ��������ע
	public int[] averRaise;
	public int[] maxRaise;
	public int[] minRaise;
	public int[] raiseTime;

	public int[] iniRaiseTime;

	//��ִ���
//	public int[] attendTime;
	
	//������ע���
//	public int[] averAdd;
//	public int[] maxAdd;
//	public int[] minAdd;
//	public int[] addTime;

	public RaiseData() {
		pid = 0;
		averRaise = new int[] { 0, 0, 0, 0 };
		maxRaise = new int[] { 0, 0, 0, 0 };
		minRaise = new int[] { 0, 0, 0, 0 };
		raiseTime = new int[] { 0, 0, 0, 0 };
		
		iniRaiseTime = new int[] { 0, 0, 0, 0 };
		
//		attendTime = new int[] { 0, 0, 0, 0 };
//		
//		averAdd = new int[] { 0, 0, 0, 0 };
//		maxAdd = new int[] { 0, 0, 0, 0 };
//		minAdd = new int[] { 0, 0, 0, 0 };
//		addTime = new int[] { 0, 0, 0, 0 };
	}
}

class Datas {
	public ArrayList<RaiseData> raiseDatas;

	public Datas() {
		raiseDatas = new ArrayList<RaiseData>();
	}
}
