package test;

public class Card {
		public String color;
		public String point;
		
		public Card(String cardMsg) {
			String [] msg=cardMsg.split(" ");
			this.color = msg[0];
			this.point = msg[1];
		}
		public String toString(){
			return "color:"+this.color+" "+"point:"+this.point;
		}

}
