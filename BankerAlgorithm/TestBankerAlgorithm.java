package cn.lin.BankerAlgorithm;

import java.util.Scanner;

public class TestBankerAlgorithm {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("**********************************************************************");
        System.out.println("                        欢迎使用操作系统银行家算法                       ");
        System.out.println("**********************************************************************");
        System.out.println("请选择功能：\n开始操作，输入数字1\n退出操作，输入数字0");
        System.out.println("**********************************************************************");
        int function = sc.nextInt();

        if (function==1){
            setInfo();//获取各种数据,并执行一些操作
        }else if(function==0){
            quitOperate();//离开算法操作
        }else {
            System.out.println("你输入了不正确的数字，请重新输入！");
        }
    }

    private static void quitOperate(){
        System.out.println("*************************已退出操作，感谢您的使用!***********************");
        System.exit(0);
    }

    private static void setInfo(){
        System.out.println("请输入系统中进程数");
        int processNum = sc.nextInt();

        System.out.println("请输入进程的资源类型种数");
        int resourceNum = sc.nextInt();

        System.out.println("请输入"+processNum+"行"+resourceNum+"列的各进程的最大需求(Max)：");
        int Max[][] = new int[processNum][resourceNum];
        for(int i=0;i<processNum;i++){
            System.out.println("请输入进程P"+i+"的最大需求：");
            for (int j=0;j<resourceNum;j++){
                Max[i][j] = sc.nextInt();
            }
        }

        System.out.println("请输入"+processNum+"行"+resourceNum+"列的各进程的已被分配的各资源数目(Allocation)：");
        int Allocation[][] = new int[processNum][resourceNum];
        for(int i=0;i<processNum;i++){
            System.out.println("请输入已分配给进程P"+i+"的各资源数目：");
            for (int j=0;j<resourceNum;j++){
                Allocation[i][j] = sc.nextInt();
            }
        }

        System.out.println("请输入可用资源数(Available)：");
        int Available[] = new int[resourceNum];
        for (int i=0;i<resourceNum;i++){
            Available[i] = sc.nextInt();
        }

        //计算每个进程尚需的各类资源数Need
        int[][]Need = setNeed(processNum,resourceNum,Max,Allocation);


        BankerAlgorithm BA = new BankerAlgorithm(processNum,resourceNum,Max,Allocation,Need,Available);

        System.out.println("初始化得到T0时刻资源分配情况表如下：");
        BA.resultOfInitialization();

        //选择接下来的操作，进行检验
        someFunction(processNum,resourceNum,Max,Allocation,Need,Available);
    }

    private static void someFunction(int processNum,int resourceNum,int[][] Max,int[][] Allocation, int[][]Need,int[] Available){
        System.out.println("请选择您要操作的功能：");
        System.out.println("死锁避免：检测T0时刻，该系统是否安全，请输入数字1\n死锁检测：检测某进程提出申请，是否给分配，请输入数字2\n退出操作，输入数字0");
        System.out.println("**********************************************************************");

        BankerAlgorithm BAs = new BankerAlgorithm(processNum,resourceNum,Max,Allocation,Need,Available);
        boolean flag = true;
        while (flag){
            String choice = sc.nextLine();

            switch (choice){
                case "1":
                    BAs.securityCheck();//检测是否安全的算法
                    System.out.println("\n请重新选择功能：");
                    break;
                case "2":
                    BAs.resourceRequest();//检测是否分配资源
                    System.out.println("\n请重新选择功能：");
                    break;
                case"0":
                    quitOperate();
                    break;
            }
        }
    }

    //用于计算每个进程尚需的各类资源数Need
    private static int[][] setNeed(int processNum,int resourceNum,int[][]Max,int[][]Allocation){
        int Need[][] = new int[processNum][resourceNum];
        for(int i=0;i<processNum;i++){
            for(int j=0;j<resourceNum;j++){
                Need[i][j] = Max[i][j] - Allocation[i][j];
            }
        }
        return Need;
    }

}
