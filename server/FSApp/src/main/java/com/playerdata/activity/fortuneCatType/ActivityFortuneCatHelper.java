package com.playerdata.activity.fortuneCatType;

import java.util.Random;

public class ActivityFortuneCatHelper {
	private static Random r = new Random();
	/**
	 * 
	 * @return  高斯分布,不太准
	 */
	public static double gaussianDistribution(double miu,double sigma2){
		double N = 12;
		double x = 0,temp = N;
		do{
			x = 0;
			for(int i = 0;i < N;i++){
				x = x + (Math.random());
				x = (x - temp/2)/(Math.sqrt(temp/12));
				x = miu + x * Math.sqrt(sigma2);
				
				System.out.println(" i = " + i + "     x = " + x);
			}
			System.out.println( "~~~~~~~~~~~~~~~     x = " + x);
		}while(x <= 0);
		return x;
	}
	
	/**
	 * 
	 * @return  高斯分布  miu代表中心量，默认为0，应取需求最大和最小值之和/2,即波峰所对x值,；sigma2代表波长的1/6，默认为1/6,应为最大最小值之差的1/6,
	 */
	public static double normalDistribution(double miu,double sigma2){
		double r = 0;
//		double i小于0 = 0*sigma2,n1=0;
//		double i0到100 = 1*sigma2,n2=0;
//		double i100到200 = 2*sigma2,n3=0;
//		double i200到300 = 3*sigma2,n4=0;
//		double i300到400 = 4*sigma2,n5=0;
//		double i400到500 = 5*sigma2,n6=0;
//		double i500到600 = 6*sigma2,n7=0;
//		double i600到700 = 7*sigma2,n8=0;
//		double i700到800 = 8*sigma2,n9=0;
//		double i800到900 = 9*sigma2,n10=0;
//		double i900到1000 = 10*sigma2,n11=0;
//		double i1000加 = 10*sigma2,n12=0;
		
		for(int i = 0;i < 1 ; i ++){
			double u1 = ActivityFortuneCatHelper.r.nextDouble();
			double u2 = ActivityFortuneCatHelper.r.nextDouble();
			r= miu + sigma2*Math.cos(2*u2*3.1415926)*Math.sqrt(-2*Math.log(u1));
//			System.out.println("200    <     " + r + "    <  1000");
//			if(r < i小于0){
//				n1 ++;
//			}else if(r < i0到100){
//				n2 ++;
//			}else if(r < i100到200){
//				n3 ++;
//			}else if(r < i200到300){
//				n4 ++;
//			}else if(r < i300到400){
//				n5 ++;
//			}else if(r < i400到500){
//				n6 ++;
//			}else if(r < i500到600){
//				n7 ++;
//			}else if(r < i600到700){
//				n8 ++;
//			}else if(r < i700到800){
//				n9 ++;
//			}else if(r < i800到900){
//				n10 ++;
//			}else if(r < i900到1000){
//				n11 ++;
////				System.out.println(r );
//			}else if(r > i1000加){
//				n12 ++;
////				System.out.println(r );
//			}
		}
//		System.out.println(n1 + "       " + n2 +"        "+ n3 +   "      " + n4);
//		System.out.println(n5 + "       " + n6 +"        "+ n7 +   "      " + n8);
//		System.out.println(n9 + "       " + n10 +"        "+ n11 +   "      " + n12);
//		System.out.println(miu + "    " + sigma2);
		return r;
	}
	
	
	
	public static void main(String[] args) throws InterruptedException {
//		gaussianDistribution(10, 20);
		normalDistribution(1400, 233);
		
		
	}
	
	
}
