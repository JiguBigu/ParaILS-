package GA;



public class GAThread extends Thread{

    private int fileSize = 7;
    private Individual[] best = new Individual[fileSize];
    private double PC;
    private double PM;
    private int POPSIZE;
    private int G;
    private double wasteTime;
    private String[] fileList= {
            "G:\\Brandimarte_Data\\Text\\Mk01.fjs",
            "G:\\Brandimarte_Data\\Text\\Mk02.fjs",
            "G:\\Brandimarte_Data\\Text\\Mk03.fjs",
            "G:\\Brandimarte_Data\\Text\\Mk04.fjs",
            "G:\\Brandimarte_Data\\Text\\Mk05.fjs",
            "G:\\Brandimarte_Data\\Text\\Mk06.fjs",
            "G:\\Brandimarte_Data\\Text\\Mk07.fjs"
//            "G:\\Brandimarte_Data\\Text\\Mk08.fjs",
//            "G:\\Brandimarte_Data\\Text\\Mk09.fjs",
//            "G:\\Brandimarte_Data\\Text\\Mk10.fjs"
    };


    public void setConfig(double PC, double PM, int POPSIZE, int G){
        this.PC = PC;
        this.PM = PM;
        this.POPSIZE = POPSIZE;
        this.G = G;
    }



    @Override
    public void run()
    {

        double startTime = System.currentTimeMillis();
        for(int i = 0; i < fileList.length; i++){
            FileRead fileRead=new FileRead();
            fileRead.Read(fileList[i]);
            Population population=new Population(PC,PM,POPSIZE,fileRead.Time,fileRead.ProcessNum,fileRead.MachineNum,fileRead.WorkpieceNum,fileRead.Pronum);
            population.Init();

            for (int j = 0; j < G; j++) {
                population.Select();
                population.Cross();
                population.Variations();
            }
            best[i]=population.FindBest();
        }
        wasteTime = System.currentTimeMillis() - startTime;
    }

    public Individual[] getBest() {
        return best;
    }

    public double getWasteTime() {
        return wasteTime;
    }
}
