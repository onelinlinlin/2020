package cn.lin.BankerAlgorithm;

import java.util.Scanner;

class BankerAlgorithm {
    private int processNum;//进程数
    private int resourceNum;//资源种类数
    private int[][] Max;//最大需求矩阵
    private int[][] Allocation;//资源已分配矩阵
    private int[][] Need;//需求矩阵=最大需求矩阵-资源已分配矩阵
    private int[] Available;//系统中的可用资源向量数组
    private int[][] Request;//需求数组
    private int[] Work;//工作向量
    private int[] securedSequence;//得到的安全序列
    Scanner sc = new Scanner(System.in);

    public BankerAlgorithm(int processNum, int resourceNum, int[][] Max, int[][] Allocation, int[][] Need, int[] Available) {
        this.processNum = processNum;
        this.resourceNum = resourceNum;
        this.Max = Max;
        this.Allocation = Allocation;
        this.Need = Need;
        this.Available = Available;
        this.Request = new int[processNum][resourceNum];
        this.Work = new int[resourceNum];
        this.securedSequence = new int[processNum];
    }

    //检测系统是否处于安全状态，安全性算法
    public boolean securityCheck(){
        //初始化设置工作向量，表示系统可提供的各类资源数目
        for (int i=0;i<resourceNum;i++){
            Work[i] = Available[i];
        }
        //标志向量，Finish[i]=false，表示进程i尚未完成，Finish[i]=true，表示进程i已完成
        boolean[] Finish = new boolean[processNum];
        for(int i=0;i<processNum;i++){
            Finish[i] = false;
        }

        int processFinish = 0;//用于记录完成的进程数以及位置
        int cycleNum = 0;//记录循环次数
        while(cycleNum<processNum){
            int fulfilNum = 0;
            for (int i=0;i < processNum;i++){
                for(int j=0;j < resourceNum;j++){
                    if(Need[i][j] <= Work[j]){
                        fulfilNum++;
                    }
                }
                //执行判断，当前的进程未完成且所需的各种资源数量都小于工作向量，即系统可提供的各类资源数量
                if (Finish[i] == false && fulfilNum == resourceNum){
                    for (int k=0;k<resourceNum;k++){
                        Work[k] = Work[k]+ Allocation[i][k];
                    }
                    //顺利执行，则标识Finish[i]为true
                    Finish[i] = true;
                    //得到顺利执行的进程标号，并放进安全序列
                    securedSequence[processFinish] = i;
                    //执行完成的进程数+1，执行下一次的判断
                    processFinish++;
                }
                fulfilNum=0;
            }
            cycleNum++;
        }

        //所有进程都能最终执行，即Finish[i]均为true
        if (processFinish==processNum){
            //打印出得到的安全序列
            System.out.println("存在一个安全序列，所以该系统在T0时刻处于安全状态！");
            System.out.println("得到的安全序列如下：");
            for (int i=0;i<securedSequence.length;i++){
                if (i==securedSequence.length-1){
                    System.out.println("P"+securedSequence[i]);
                }else{
                    System.out.print("P"+securedSequence[i]+"—→");
                }
            }
            return true;
        }else{
            System.out.println("找不到安全序列，系统处于不安全状态!");
            return false;
        }
    }

    //判断是否分配给资源
    public void resourceRequest(){
        System.out.println("请输入请求资源的进程编号：(注意进程p下标起始为0)");
        int thisProcessNum = sc.nextInt();
        System.out.println("进程P"+thisProcessNum+"提出了请求");
        System.out.println("请输入要申请的资源的数量Request：");
        for (int i=0;i<resourceNum;i++){
            Request[thisProcessNum][i] = sc.nextInt();
        }

        //提出申请，进行安全检查
        int cycleNum1 = 0;
        int cycleNum2 = 0;
        for (int i=0;i<resourceNum;i++){
            //检查需求申请是否超出最大需求
            if (Request[thisProcessNum][i]<=Need[thisProcessNum][i]){
                cycleNum1++;
                //检查系统有无足够资源，若无，Pi阻塞等待
                if (Request[thisProcessNum][i]<=Available[i]){
                    cycleNum2++;
                }
            }
        }

        //系统试探把要求的资源分配给进程i并修改有关数据结构的值
        if (cycleNum1==resourceNum && cycleNum2==resourceNum){
            for (int i=0;i<resourceNum;i++){
                Available[i] = Available[i] - Request[thisProcessNum][i];
                Allocation[thisProcessNum][i] = Allocation[thisProcessNum][i] + Request[thisProcessNum][i];
                Need[thisProcessNum][i] = Need[thisProcessNum][i] - Request[thisProcessNum][i];
            }
        }else if (cycleNum1<resourceNum){
            System.out.println("该进程的需求申请超出了最大需求，不合法，无法进行下一步操作！");
            return;
        }else if (cycleNum2<resourceNum){
            System.out.println("系统没有足够资源满足该进程需要，不合法，无法进行下一步操作！");
            return;
        }

        System.out.println("试探分配该时刻的资源，情况表如下：");
        resultOfInitialization();
        System.out.println("执行安全算法，对资源重分配后的系统进行安全检查：");

        //判断重分配资源后，系统是否处于安全状态
        boolean ifSafe = securityCheck();
        //安全，则提示安全，并输出安全序列
        if (ifSafe == true){
            System.out.println("此次资源分配后，系统处于安全状态，正式分配资源给进程P"+thisProcessNum+"，完成本次分配！");
        }else {
            //不安全，则分配作废，将数据返回原来状态
            for (int i=0;i<resourceNum;i++){
                Available[i] = Available[i] + Request[thisProcessNum][i];
                Allocation[thisProcessNum][i] = Allocation[thisProcessNum][i] - Request[thisProcessNum][i];
                Need[thisProcessNum][i] = Need[thisProcessNum][i] + Request[thisProcessNum][i];
            }
            System.out.println("试探分配作废，已恢复原状态，进程"+"p"+thisProcessNum+"等待！");
        }
    }

    //打印该时刻资源分配情况表
    public void resultOfInitialization(){
        System.out.println("**********************************************************************");
        System.out.println("\t\tMax\t\t\t\tAllocation\t\tNeed\t\t\tAvailable");
        System.out.print("\t\t");
        //找到字符A对应的ASCII码，可以强转为字符，每次循环+1，循环后重新初始化
        int firstResource = 65;
        for(int i=1;i<=4;i++){
            for (int j=1;j<=resourceNum;j++){
                System.out.print((char)firstResource+" ");
                firstResource++;
            }
            firstResource=65;
            System.out.print("\t\t");
        }
        System.out.println();
        //打印各类信息
        for (int i=0;i<processNum;i++){
            System.out.print("P"+i+":");
            System.out.print("\t\t");
            for(int j=0;j<resourceNum;j++){
                System.out.print(Max[i][j]+" ");
            }
            System.out.print("\t\t");
            for(int j=0;j<resourceNum;j++){
                System.out.print(Allocation[i][j]+" ");
            }
            System.out.print("\t\t");
            for(int j=0;j<resourceNum;j++){
                System.out.print(Need[i][j]+" ");
            }
            if (i==0){
                System.out.print("\t\t");
                for(int j=0;j<resourceNum;j++){
                    System.out.print(Available[j]+" ");
                }
            }
            System.out.println();
        }
        System.out.println("**********************************************************************");
    }

}
