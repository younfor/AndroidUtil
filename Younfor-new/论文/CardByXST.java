
import java.util.ArrayList;


public class CardByXST {


		public enum CardNum{		//��ֵ
			N2,N3,N4,N5,N6,N7,N8,N9,NT,NJ,NQ,NK,NA
		}
		public enum CardColor{		//�ƵĻ�ɫ�����ң����ң�÷������Ƭ
			SPADES,HEARTS,DIAMONDS,CLUBS
		}
		public static int Prime[] = {		//��ֵ��Ӧ������	
			2,3,5,7,11,13,17,19,23,29,31,37,41
		};
		
		/**
		 * �����Ƶ���ֵ�ͻ�ɫ����
		 * @param cardnum		��ֵ
		 * @param cardcolor		��ɫ
		 * @return	����ֵ
		 */
		public static int CardCoding(CardNum cardnum,CardColor cardcolor) {
			int CardCodingNum = 0;
			CardCodingNum = 0x00000001<<(cardnum.ordinal()+16);
			CardCodingNum |= 0x01<< (cardcolor.ordinal()+12);
			CardCodingNum |= (~(0x01<<cardcolor.ordinal())&0x0f)<<8;
			CardCodingNum |= Prime[cardnum.ordinal()];
			return CardCodingNum;
		}
		
		/**
		 * �ж������5���ƿ��Թ��ɵ�������
		 * @param fivecards		�����5����
		 */
		public static int Rank(int[] fivecards){
			
			if (fivecards.length==5) {
				int sum = 1;
				for (int i = 0; i < fivecards.length; i++) {
					sum *= (fivecards[i]&0x3f);
				}
				//System.out.println("sum is:"+sum);
				int FiveAndResult = fivecards[0]&fivecards[1]&fivecards[2]&fivecards[3]&fivecards[4];
				int FiveOrResult = fivecards[0]|fivecards[1]|fivecards[2]|fivecards[3]|fivecards[4];
				if ((FiveAndResult&0xf000)!=0) {			//��ͬ��
					//�ж��Ƿ�Ϊ˳��
					for (int i = 0; i < 9; i++) {
						if (((FiveOrResult>>(16+i))&0x1f)==0x1f) {
							//��˳�ӣ�����ͬ��˳��ͨ�������ж�����
							for (int j = 0; j < CardPowerTable.Straight_flush_table.length; j++) {
								if (sum==CardPowerTable.Straight_flush_table[j]) {
									return (j+1);
								}
							}
						}
					}
					if (((FiveOrResult>>16)&0x100f)==0x100f) {
						//ͬ��˳A2345
						if (sum==8610) {
							return 10;
						}
					}
					else {		//ֻ��ͬ��
						for (int i = 0; i < CardPowerTable.Flush_table.length; i++) {
							if (sum==CardPowerTable.Flush_table[i]) {
								return (323+i);
							}
						}
					}
				}
				else {				//����ͬ��
					//�ж��Ƿ�Ϊ˳��
					for (int i = 0; i < 9; i++) {
						if (((FiveOrResult>>(16+i))&0x1f)==0x1f) {
							//��˳�ӣ�ͨ�������ж�����
							return (1608-i);
						}
					}
					if (((FiveOrResult>>16)&0x100f)==0x100f) {
						//˳��A2345
						if (sum==8610) {
							return 1609;
						}
					}
					//�ж�FiveOrResult��ֵλ1�ĸ���������������ж�
					int OneBitCounter = 0;
					for (int i = 0; i < 13; i++) {
						if (((FiveOrResult>>(16+i))&0x01)!=0) {
							OneBitCounter++;
						}
					}
					switch (OneBitCounter) {
					case 2:{		//���Ż��ߺ�«
						int counter_temp = 0;
						for (int i = 0; i < 4; i++) {
							if (((fivecards[0]&fivecards[i+1])>>16)!=0) {
								counter_temp++;
							}
						}
						if (counter_temp==1||counter_temp==2) {		//��«
							for (int i = 0; i < CardPowerTable.Full_house_table.length; i++) {
								if (sum==CardPowerTable.Full_house_table[i]) {
									return (i+167);
								}
							}
						}
						else {		//����
							for (int i = 0; i < CardPowerTable.Four_of_a_kind_table.length; i++) {
								if (sum==CardPowerTable.Four_of_a_kind_table[i]) {
									return (11+i);
								}
							}
						}
					}break;
					case 3:{		//���Ż�������
						for (int i = 0; i < 3; i++) {
							int counter_temp=0;
							for (int j = 0; j < 5; j++) {
								if ((i!=j)&&(((fivecards[i]&fivecards[j])>>16)!=0)) {
									counter_temp++;
								}
							}
							if (counter_temp==2) {	//����
								for (int j = 0; j < CardPowerTable.Three_of_a_kind_table.length; j++) {
									if (sum==CardPowerTable.Three_of_a_kind_table[j]) {
										return (1610+j);
									}
								}
							}
						}
						for (int i = 0; i < CardPowerTable.Two_pair_table.length; i++) {
							if (sum==CardPowerTable.Two_pair_table[i]) {
								return (2468+i);
							}
						}
					}break;
					case 4:{		//һ��
						for (int i = 0; i < CardPowerTable.One_pair_table.length; i++) {
							if (sum==CardPowerTable.One_pair_table[i]) {
								return (3326+i);
							}
						}					
					}break;
					case 5:{		//����
						for (int i = 0; i < CardPowerTable.High_Card_table.length; i++) {
							if (sum==CardPowerTable.High_Card_table[i]) {
								return (6186+i);
							}
						}
					}break;
						

					default:
						return -1;
					}
					
				}
				return -1;
			}
			else {
				System.err.println("error param!");
				return -1;
			}
		}
		
		/**
		 * ����������ַ������ֵ
		 * @param i	����ֵ
		 * @return	��ֵ
		 */
		public static CardNum getCardNum(int i) {
			if (i<13&&i>=0) {
				switch (i) {
				case 0:
					return CardNum.N2;
				case 1:
					return CardNum.N3;
				case 2:
					return CardNum.N4;
				case 3:
					return CardNum.N5;
				case 4:
					return CardNum.N6;
				case 5:
					return CardNum.N7;
				case 6:
					return CardNum.N8;
				case 7:
					return CardNum.N9;
				case 8:
					return CardNum.NT;
				case 9:
					return CardNum.NJ;
				case 10:
					return CardNum.NQ;
				case 11:
					return CardNum.NK;
				case 12:
					return CardNum.NA;

				default:
					return CardNum.N2;
				}
			}
			else {
				System.err.println("error param");
				return CardNum.N2;
			}
		}
		
		/**
		 * ����5/6/7���ƣ�ȷ������ϳɵ�������
		 * @param cardlist
		 * @return
		 */
		public static int getBiggestCardGPPower(ArrayList<Integer> cardlist) {
//			ArrayList<Integer> cardlist = new ArrayList<Integer>();
//			
//			for (int i = 0; i < cardlistArray.size(); i++) {
//				
//				cardlist.add(cardlistArray.get(i));
//			}
			switch (cardlist.size()) {
			case 5:{
				int BiggestGPResult = 0;
				int fivecards[] = new int[5];
				for (int i = 0; i < 5; i++) {
					fivecards[i] = cardlist.get(i);
				}
				BiggestGPResult = Rank(fivecards);
				return BiggestGPResult;
			}
			case 6:{
				int fivecards[] = new int[5];
				int groupresult[] = new int[6];
				int RemoveCard = 0;
				int BiggestGPResult = 0;
				for (int i = 0; i < 6; i++) {
					RemoveCard = cardlist.get(i);
					cardlist.remove(i);
					for (int j = 0; j < 5; j++) {
						fivecards[j] = cardlist.get(j);
					}
					groupresult[i] = CardByXST.Rank(fivecards);
					cardlist.add(i,RemoveCard);
				}
				BiggestGPResult = groupresult[0];
				for (int i = 0; i < 6; i++) {
					if (groupresult[i]<BiggestGPResult) {
						BiggestGPResult = groupresult[i];
					}
				}
				return BiggestGPResult;
			}
			case 7:{
				int fivecards[] = new int[5];
				int groupresult[] = new int[21];
				int RemoveCard1 = 0;
				int RemoveCard2 = 0;
				int BiggestGPResult = 0;
				int counter = 0;
				for (int i = 0; i < 6; i++) {
					for (int j = i+1; j < 7; j++) {
						RemoveCard1 = cardlist.get(i);
						RemoveCard2 = cardlist.get(j);
						cardlist.remove(j);
						cardlist.remove(i);
						
						for (int m = 0; m < 5; m++) {
							fivecards[m] = cardlist.get(m);
							
						}
						groupresult[counter++] = CardByXST.Rank(fivecards);
						cardlist.add(i,RemoveCard1);
						cardlist.add(j,RemoveCard2);
					}
				}
				counter = 0;
				BiggestGPResult = groupresult[0];
				for (int i = 0; i < 21; i++) {
					if (groupresult[i]<BiggestGPResult) {
						BiggestGPResult = groupresult[i];
					}
				}
				return BiggestGPResult;
			}
			default:{
				System.err.println("please input 5 or 6 or 7 cards!!!");
				return -1;
			}
			
			}
			
		}	
		
		/**
		 * ���������ƽ�������
		 * @param OurTwoCards	���ŵ���
		 * @return              �����Ƶ���������ֵ
		 */
		public static float getPowerOfTwoCards(int[] OurTwoCards){
			float result = 0;
			float result_temp = 0;
			CardNum cardnum1 = CardCodingValue.getCardNumFromCodingValue(OurTwoCards[0]);
			CardNum cardnum2 = CardCodingValue.getCardNumFromCodingValue(OurTwoCards[1]);
			
			CardColor cardcolor1 = CardCodingValue.getCardColorFromCodingValue(OurTwoCards[0]);
			CardColor cardcolor2 = CardCodingValue.getCardColorFromCodingValue(OurTwoCards[1]);
			
			int biggerCardnum = 0;
			if (cardnum1.ordinal()>=cardnum2.ordinal()) {
				biggerCardnum = cardnum1.ordinal()+2;
			}
			else {
				biggerCardnum = cardnum2.ordinal()+2;
			}
			//result = biggerCardnum;
			switch (biggerCardnum) {
			case 14:{
				result_temp = 10;
			}break;
			case 13:{
				result_temp = 8;
			}break;			
			case 12:{
				result_temp = 7;
			}break;	
			case 11:{
				result_temp = 6;
			}break;	
			default:{
				result_temp = (float) (biggerCardnum/2.0);
			}break;	
			}
			if (cardnum1==cardnum2) {	//����
				result = result_temp*2;
				if (result<5) {
					result = 5;
				}
				return result;
			}
			else {		//�Ƕ���
				if (cardcolor1==cardcolor2) {	//ͬ�ֻ�ɫ
					result = result_temp+2;
				}
				int suboftwocardnum = Math.abs(cardnum1.ordinal()-cardnum2.ordinal());
				switch (suboftwocardnum) {
				case 1:{
					result = result_temp+0;
					if ((cardnum1.ordinal()<10)&&(cardcolor2.ordinal()<10)) {
						result += 3;
					}
				}break;
				case 2:{
					result = result_temp - 1;
					if ((cardnum1.ordinal()<10)&&(cardcolor2.ordinal()<10)) {
						result += 3;
					}
				}break;
				case 3:{
					result = result_temp - 2;
				}break;			
				case 4:{
					result = result_temp - 4;
				}break;	
				default:{
					result = result_temp - 5;
				}break;
					
				}
			}
			return result;
		}
		
		/**
		 * ��������+�����Ƶ�����
		 * @param ourcards		����
		 * @param boardcards	������
		 * @return				����ֵHS
		 */
		public static float HAND_STRENGTH(int[] ourcards,int[] boardcards) {
			int ahead = 0;
			int tied = 0;
			int behind = 0;
			int Ourrank = 0;
			int opporientrank = 0;
			float handstrength = 0;
			ArrayList<Integer> RemaincardvalueList = new ArrayList<Integer>();
			ArrayList<Integer> allcardsList = new ArrayList<Integer>();
			
			for (int i = 0; i < 2; i++) {
				allcardsList.add(ourcards[i]);
			}
			for (int i = 0; i < boardcards.length; i++) {
				allcardsList.add(boardcards[i]);
			}
			Ourrank = getBiggestCardGPPower(allcardsList);
			for (int i = 0; i < 52; i++) {
				RemaincardvalueList.add(CardCodingValue.CardCodingValue[i]);
				for (int j = 0; j < allcardsList.size(); j++) {
					if (CardCodingValue.CardCodingValue[i]==allcardsList.get(j)) {
						RemaincardvalueList.remove(RemaincardvalueList.size()-1);
					}				
				}
			}

			for (int i = 0; i < RemaincardvalueList.size()-1; i++) {
				for (int j = i+1; j < RemaincardvalueList.size(); j++) {
					allcardsList.remove(1);
					allcardsList.remove(0);				
					allcardsList.add(0,RemaincardvalueList.get(i));
					allcardsList.add(1,RemaincardvalueList.get(j));
					opporientrank = getBiggestCardGPPower(allcardsList);
					if (Ourrank<opporientrank) {
						ahead += 1;
					}
					else if (Ourrank==opporientrank) {
						tied += 1;
					}
					else {
						behind += 1;
					}
				}
			}
			handstrength = (float) (((float) ((ahead+tied)))/((ahead+tied+behind)*1.0));
			return handstrength;
		}

		/**
		 * �������ŵ��ƺ͹����Ƽ���ʤ��Ǳ��
		 * @param ourcards		����
		 * @param boardcards	������
		 * @return				ʤ��Ǳ��ֵPPot
		 */
		public static float HAND_POTENTIAL(int[] ourcards,int[] boardcards) {
			float PPot = 0;
			int HP[][] = new int[3][3];
			int HPTotal[] = new int[3];
			final int ahead_index = 0;
			final int tied_index = 1;
			final int behind_index = 2;
			int index = 0;
			
			int Ourrank = 0;
			int opporientrank = 0;
			
			int ourbest = 0;
			int oppentbest = 0;		
			
			ArrayList<Integer> RemaincardvalueList = new ArrayList<Integer>();	
			ArrayList<Integer> allcardsList = new ArrayList<Integer>();
			ArrayList<Integer> assembleOfturnORriverlist = new ArrayList<Integer>();

			
			for (int i = 0; i < 2; i++) {
				allcardsList.add(ourcards[i]);
			}
			for (int i = 0; i < boardcards.length; i++) {
				allcardsList.add(boardcards[i]);
			
			}
			Ourrank = getBiggestCardGPPower(allcardsList);
			for (int i = 0; i < 52; i++) {
				RemaincardvalueList.add(CardCodingValue.CardCodingValue[i]);
				for (int j = 0; j < allcardsList.size(); j++) {
					if (CardCodingValue.CardCodingValue[i]==allcardsList.get(j)) {
						RemaincardvalueList.remove(RemaincardvalueList.size()-1);
					}				
				}
			}
		
			
			ArrayList<Integer> allcardtemp = new ArrayList<Integer>();
			
			switch (boardcards.length) {
			case 3:{
				//ģ�������������
//				for (int i = 0; i < RemaincardvalueList.size()-1; i++) {
//					for (int j = i+1; j < RemaincardvalueList.size(); j++) {
				for (int i = 0; i < 13; i++) {
					for (int j = i+1; j < 14; j++) {
						if (allcardtemp.size()!=0) {
							allcardsList.clear();
							for (int k = 0; k < allcardtemp.size(); k++) {
								allcardsList.add(allcardtemp.get(k));
							}						
						}

						if (allcardsList.size()==7) {
							allcardsList.remove(6);
							allcardsList.remove(5);
						}				
						allcardsList.remove(1);
						allcardsList.remove(0);	
						allcardsList.add(0,RemaincardvalueList.get(i));
						allcardsList.add(1,RemaincardvalueList.get(j));
						opporientrank = getBiggestCardGPPower(allcardsList);
						if (Ourrank<opporientrank) {
							index = ahead_index;
						}
						else if (Ourrank==opporientrank) {
							index = tied_index;
						}
						else {
							index = behind_index;
						}
						HPTotal[index] += 1;
						//��ʣ�������ģ��turn��river����Ǳ������
						allcardsList.add(0,ourcards[1]);
						allcardsList.add(0,ourcards[0]);
						assembleOfturnORriverlist.clear();
						for (int m = 0; m < 52; m++) {
							assembleOfturnORriverlist.add(CardCodingValue.CardCodingValue[m]);
							for (int k = 0; k < allcardsList.size(); k++) {
								if (CardCodingValue.CardCodingValue[m]==allcardsList.get(k)) {
									assembleOfturnORriverlist.remove(assembleOfturnORriverlist.size()-1);
								}						
							}
						}
						int assemblesize = assembleOfturnORriverlist.size();
						allcardsList.remove(1);
						allcardsList.remove(0);
						long t2 = System.nanoTime();
						
//						for (int turn_counter = 0; turn_counter < assemblesize-1; turn_counter++) {
//							for (int river_counter = turn_counter+1; river_counter < assemblesize; river_counter++) {
						for (int turn_counter = 0; turn_counter < 13; turn_counter++) {
							for (int river_counter = turn_counter+1; river_counter < 14; river_counter++) {		
								if (allcardtemp.size()!=0) {
									allcardsList.clear();
									for (int k = 0; k < allcardtemp.size(); k++) {
										allcardsList.add(allcardtemp.get(k));
									}								
								}

								if (allcardsList.size()==7) {
									allcardsList.remove(6);
									allcardsList.remove(5);
								}
								allcardsList.add(assembleOfturnORriverlist.get(turn_counter));
								allcardsList.add(assembleOfturnORriverlist.get(river_counter));
								
								oppentbest = getBiggestCardGPPower(allcardsList);
								
								int oppentvalue1 = allcardsList.get(0);
								int oppentvalue2 = allcardsList.get(1);
								
								allcardsList.remove(1);
								allcardsList.remove(0);						
								
								allcardsList.add(0,ourcards[0]);
								allcardsList.add(1,ourcards[1]);
								
								ourbest = getBiggestCardGPPower(allcardsList);	
								
								allcardsList.remove(1);
								allcardsList.remove(0);
								
								allcardsList.add(0,oppentvalue1);
								allcardsList.add(1,oppentvalue2);						
								
								allcardtemp.clear();
								for (int k = 0; k < allcardsList.size(); k++) {
									allcardtemp.add(allcardsList.get(k));
								}
								if (ourbest<oppentbest) {
									HP[index][ahead_index] += 1;
								}
								else if (ourbest==oppentbest) {
									HP[index][tied_index] += 1;
								}
								else {
									HP[index][behind_index] += 1;
								}
							
							}
						}
						long time2 = System.nanoTime();
//						System.out.println(time2-time1);
//						System.out.println(t2-t1);
//						System.out.println(time1-t2);
					}
				}
				PPot = (float) (HP[behind_index][ahead_index]+HP[behind_index][tied_index]/2.0+HP[tied_index][ahead_index]/2.0)/(HPTotal[behind_index]+HPTotal[tied_index]);
				return PPot;
			}
			case 4:{
				for (int i = 0; i < RemaincardvalueList.size()-1; i++) {
					for (int j = i+1; j < RemaincardvalueList.size(); j++) {
						long time1 = System.nanoTime();
						if (allcardtemp.size()!=0) {
							allcardsList.clear();
							for (int k = 0; k < allcardtemp.size(); k++) {
								allcardsList.add(allcardtemp.get(k));
							}						
						}
			
						allcardsList.remove(1);			//�Ƴ����ŵ���
						allcardsList.remove(0);	
						allcardsList.add(0,RemaincardvalueList.get(i));			//ģ����ֵ���
						allcardsList.add(1,RemaincardvalueList.get(j));
						opporientrank = getBiggestCardGPPower(allcardsList);
						if (Ourrank<opporientrank) {
							index = ahead_index;
						}
						else if (Ourrank==opporientrank) {
							index = tied_index;
						}
						else {
							index = behind_index;
						}
						HPTotal[index] += 1;
						//��ʣ�������ģ��river����Ǳ������
						allcardsList.add(0,ourcards[1]);
						allcardsList.add(0,ourcards[0]);
						//assembleOfturnORriverlist:��ȥourcards��opporientcard��board֮�⣬river����ѡ��ģ��ļ���
						long t1 = System.nanoTime();
						assembleOfturnORriverlist.clear();
						for (int m = 0; m < 52; m++) {
							assembleOfturnORriverlist.add(CardCodingValue.CardCodingValue[m]);
							for (int k = 0; k < allcardsList.size(); k++) {
								if (CardCodingValue.CardCodingValue[m]==allcardsList.get(k)) {
									assembleOfturnORriverlist.remove(assembleOfturnORriverlist.size()-1);
								}						
							}
						}
						int assemblesize = assembleOfturnORriverlist.size();
						allcardsList.remove(1);			//allcardsList��ģ�������+������
						allcardsList.remove(0);
						long t2 = System.nanoTime();
						
						for (int turn_counter = 0; turn_counter < assemblesize-1; turn_counter++) {
							
					
								if (allcardtemp.size()!=0) {
									allcardsList.clear();
									for (int k = 0; k < allcardtemp.size(); k++) {
										allcardsList.add(allcardtemp.get(k));
									}								
								}

								if (allcardsList.size()==7) {
									allcardsList.remove(6);
								}
								allcardsList.add(assembleOfturnORriverlist.get(turn_counter));
							
								
								oppentbest = getBiggestCardGPPower(allcardsList);
								
								int oppentvalue1 = allcardsList.get(0);
								int oppentvalue2 = allcardsList.get(1);
								
								allcardsList.remove(1);
								allcardsList.remove(0);						
								
								allcardsList.add(0,ourcards[0]);
								allcardsList.add(1,ourcards[1]);
								
								ourbest = getBiggestCardGPPower(allcardsList);	
								
								allcardsList.remove(1);
								allcardsList.remove(0);
								
								allcardsList.add(0,oppentvalue1);
								allcardsList.add(1,oppentvalue2);						
								
								allcardtemp.clear();
								for (int k = 0; k < allcardsList.size(); k++) {
									allcardtemp.add(allcardsList.get(k));
								}
								if (ourbest<oppentbest) {
									HP[index][ahead_index] += 1;
								}
								else if (ourbest==oppentbest) {
									HP[index][tied_index] += 1;
								}
								else {
									HP[index][behind_index] += 1;
								}
							
							}
						}
						long time2 = System.nanoTime();
//						System.out.println(time2-time1);
//						System.out.println(t2-t1);
//						System.out.println(time1-t2);
					
				}
				PPot = (float) (HP[behind_index][ahead_index]+HP[behind_index][tied_index]/2.0+HP[tied_index][ahead_index]/2.0)/(HPTotal[behind_index]+HPTotal[tied_index]);
				return PPot;			
			}			
			default:
				PPot = 0;
				break;
			}
			
			return PPot;
		}



	



}


class CardCodingValue {

	//52���Ƶı���ֵ ���ϵ��������ǣ�A~2�������ҵ�������CLUBS��DIAMONDS��HEARTS��SPADES
	public static int[] CardCodingValue = {
		268470057,268454697,268447017,268443177,
		134252325,134236965,134229285,134225445,
		67143455,67128095,67120415,67116575,
		33589021,33573661,33565981,33562141,
		16811799,16796439,16788759,16784919,
		8423187,8407827,8400147,8396307,
		4228881,4213521,4205841,4202001,
		2131725,2116365,2108685,2104845,
		1083147,1067787,1060107,1056267,
		558855,543495,535815,531975,
		296709,281349,273669,269829,
		165635,150275,142595,138755,
		100098,84738,77058,73218
	};
	
	/**
	 * ���ݱ���ֵ����������ֵ
	 * @param value	����ֵ
	 * @return		��ֵ
	 */
	public static CardByXST.CardNum getCardNumFromCodingValue(int value){
		value = value>>16;
		for (int i = 0; i < 13; i++) {
			if ((value&0x01)!=0) {
				return CardByXST.getCardNum(i);
			}
			else {
				value = value>>1; 
			}
		}
		System.err.println("cann't find card num!!!");
		return CardByXST.CardNum.N2;
	}
	/**
	 * ���ݱ���ֵ���������ƵĻ�ɫ
	 * @param value	����ֵ
	 * @return		��ɫ
	 */
	public static CardByXST.CardColor getCardColorFromCodingValue(int value) {
		value = value>>12;
		for (int i = 0; i < 4; i++) {
			if ((value&0x01)!=0) {
				switch (i) {
				case 0:
					return CardByXST.CardColor.SPADES;
				case 1:
					return CardByXST.CardColor.HEARTS;
				case 2:
					return CardByXST.CardColor.DIAMONDS;
				case 3:
					return CardByXST.CardColor.CLUBS;
				default:
					break;
				}
			}
			else {
				value = value>>1;
			}
		}
		System.err.println("Cannot find Card color!!!");
		return CardByXST.CardColor.CLUBS;
	}
	
}



