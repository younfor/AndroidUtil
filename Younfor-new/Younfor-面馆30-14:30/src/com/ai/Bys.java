package com.ai;

public class Bys {
	
	public static int fold=0,call=1,raise=2;
	int p[][][][][]=new int[3][3][3][3][10];
	public void addBys(int []ac,int ranklevel)
	{
		p[ac[0]][ac[1]][ac[2]][ac[3]][ranklevel]++;
	}
	public double getVal(int ac[])
	{
		int num[]=getBys(ac);
		double total=0,up=0;
		for(int i=1;i<10;i++)
		{
			total+=num[i];
			up+=num[i]*i;
		}
		if(total==0)
			return 0;
		return up/total;
	}
	public int[] getBys(int ac[])
	{
		// count p(r=1|A0=1,A1=1)
				int ans[]=new int[10];
				int i[]=new int[5];
				for(i[0]=0;i[0]<3;i[0]++)
					for(i[1]=0;i[1]<3;i[1]++)
						for(i[2]=0;i[2]<3;i[2]++)
							for(i[3]=0;i[3]<3;i[3]++)
								for(i[4]=1;i[4]<10;i[4]++)
								{
									boolean flag=true;
									for(int j=0;j<ac.length;j++)
									{
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