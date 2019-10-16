package ParamILS;

import GA.GAThread;
import GA.Individual;

import java.util.Random;

import static java.lang.Thread.State.TERMINATED;

/**
 * @author Jigubigu
 * @version 1.0
 * @date 2019/10/12 18:35
 */
public class ParamILS {
    double[] PC={0.7,0.75,0.8,0.85,0.9};
    private double[] PM={0.005,0.1,0.15,0.2,0.25};
    private int[] POPSIZE={100,125,150,175,200};
    private int[] G5={400,500,600,700,800};
    public Config[] config = new Config[1000];
    private final int r = 10;
    private final int s = 3;
    private final double p = 0.01;
    Random random  = new Random();

    /**
     * 配置内部类
     */
    class Config{
        public double PC;
        public double PM;
        public int POSIZE;
        public int G5;

        public Config(double PC, double PM, int POSIZE, int g5) {
            this.PC = PC;
            this.PM = PM;
            this.POSIZE = POSIZE;
            G5 = g5;
        }

        public boolean equals(Config config){
            if(this.PC == config.PC && this.PM == config.PM &&
                    this.POSIZE == config.POSIZE && this.G5 == config.G5){
                return true;
            }else {
                return false;
            }
        }

        @Override
        public String toString() {
            return "Config{" +
                    "PC=" + PC +
                    ", PM=" + PM +
                    ", POSIZE=" + POSIZE +
                    ", G5=" + G5 +
                    '}';
        }
    }

    /**
     * 构造函数初始化配置空间
     */
    public ParamILS(){
        int count = 0;
        for(int i = 0; i < PC.length; i++){
            for(int j = 0; j  < PM.length; j++){
                for(int k = 0; k < POPSIZE.length; k++){
                    for(int l = 0; l < G5.length; l++){
                        config[count] = new Config(PC[i], PM[j], POPSIZE[k], G5[l]);
                        count++;
                    }
                }
            }
        }
    }

    //<r, s, p> = <10, 3, 0.01>
    public Config run(){
        Config initConfig = new Config(0.8, 0.1, 200, 500);
        for(int i = 1; i <= r; i++){
            Config midConfig = config[random.nextInt(625)];
            if(better(midConfig, initConfig)){
                initConfig = midConfig;
            }
        }
        Config ilsConfig = iterativeFirstImprovement(initConfig);
        int count = 0;
        do {
            initConfig = ilsConfig;
            //扰动
            for(int i = 1; i <= s; i++){
                initConfig = Nbh(initConfig);
            }
            //领域搜索
            initConfig = iterativeFirstImprovement(initConfig);
            //比较
            if(better(initConfig, ilsConfig)){
                ilsConfig = initConfig;
            }
            if(random.nextInt(1000) < 10){
                ilsConfig = config[random.nextInt(625)];
            }
            count ++;
        }while (count < 100);
        return ilsConfig;
    }

    /**
     * 获取两个配置中较优的一个
     * @param a 配置一
     * @param b 配置二
     * @return
     */
    public boolean better(Config a, Config b){
        GAThread thread1 = new GAThread();
        GAThread thread2 = new GAThread();
        thread1.setConfig(a.PC, a.PM, a.POSIZE, a.G5);
        thread2.setConfig(b.PC, b.PM, b.POSIZE, b.G5);
        thread1.start();
        thread2.start();
        while (thread1.getState() != TERMINATED || thread2.getState() != TERMINATED){}
        boolean isBetter = false;
        isBetter = thread1.getWasteTime() > thread2.getWasteTime() ? false : true;
        return isBetter;
    }

    public Config iterativeFirstImprovement(Config config){
        System.out.println("runing iterative search...");
        Config config1 = new Config(config.PC, config.PM, config.POSIZE, config.G5);
        int count = 0;
        do {
            Config config2 = Nbh(config1);
            if(better(config2, config1)){
                config = config2;
                break;
            }
            count ++;
        }while (count <= 625);
        return config;
    }

    public Config Nbh(Config config){
        Config config1 = new Config(config.PC, config.PM, config.POSIZE, config.G5);
        int typeIndex = random.nextInt(4);
        int valueIndex = random.nextInt(5);
        if(typeIndex == 0){
            config1.PC = PC[valueIndex];
        }else if(typeIndex == 1){
            config1.PM = PM[valueIndex];
        }else if(typeIndex == 2){
            config1.POSIZE = POPSIZE[valueIndex];
        }else {
            config1.G5 = G5[valueIndex];
        }
        return config1;
    }

    public static void main(String[] args) {
//        ParamILS paramILS = new ParamILS();
//        System.out.println(paramILS.run());

        GAThread thread1 = new GAThread();
        GAThread thread2 = new GAThread();
        thread1.setConfig(0.8, 0.1, 200, 500);
        thread2.setConfig(0.8, 0.005, 100, 500);
        thread1.start();
        thread2.start();
        while (thread1.getState() != TERMINATED || thread2.getState() != TERMINATED){}
        System.out.println("经验值");
        System.out.println("测试集运行时间：" + thread1.getWasteTime()/1000);
        Individual[] individuals = thread1.getBest();
        for(int i = 0; i < 3; i++){
            System.out.println(individuals[i].getTime());
        }
        System.out.println();
        System.out.println("实验值");
        System.out.println("测试集运行时间：" + thread2.getWasteTime()/1000);
        individuals = thread2.getBest();
        for(int i = 0; i < 3; i++){
            System.out.println(individuals[i].getTime());
        }
    }
}
