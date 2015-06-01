package com.ai;

import java.io.IOException;

import com.util.Log;

public class Bys2 {
	
	public static int fold=0,call=1,raise0=2,raise3=3,raise8=4;
	int p[][][][][]=new int[5][5][5][5][2];
	public void addBys(int []ac,int win) throws IOException
	{
		int stat[]=new int[]{fold,fold,fold,fold};
		for(int i=0;i<ac.length;i++)
		{
			stat[i]=ac[i];
			if(stat[i]==-1)
				stat[i]=call;
		}
		p[stat[0]][stat[1]][stat[2]][stat[3]][win]++;
	}
	private void debug(String s) throws IOException
	{
		Log.getIns("7777").log(s);
	}
	public double getVal(int ac[]) throws IOException
	{
		int num[]=getBys(ac);
		double total=num[0]+num[1],up=num[1];
		//debug("total:"+total+",win:"+up);
		if(total==0)
			return 0;
		return up/total;
	}
	public int[] getBys(int ac[]) throws IOException
	{
		// count p(r=1|A0=1,A1=1)
				int ans[]=new int[2];
				int i[]=new int[5];
				for(i[0]=0;i[0]<5;i[0]++)
					for(i[1]=0;i[1]<5;i[1]++)
						for(i[2]=0;i[2]<5;i[2]++)
							for(i[3]=0;i[3]<5;i[3]++)
								for(i[4]=0;i[4]<2;i[4]++)
								{
									boolean flag=true;
									for(int j=0;j<ac.length;j++)
									{
										if(ac[j]==-1)
											ac[j]=call;
										if(i[j]!=ac[j])
										{
											flag=false;
											break;
										}
									}
									if(flag)
									{
										ans[i[4]]+=p[i[0]][i[1]][i[2]][i[3]][i[4]];
									}
								}
				return ans;
	}
	
}