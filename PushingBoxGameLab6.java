
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
/**
 *主类,框架
 */
public class PushingBoxGameLab6 {
    public static void main(String[] args){
        Scanner input = new Scanner(System.in); // 创建 Scanner 对象
        Map mapA = new Map();//两玩家共享一个地图
        mapA.startGame(input);

        
         
         
    }
}
/**
 * Map类用来读取并构建地图
 */
class Map{
    /**
     * 棋盘
     * 内部的map[i][j] 墙或空地或障碍物（玩家另外存储）
     */
    private Board boardA, boardB,board;

    /**两玩家：玩家A和玩家B */
    private Player playerA,playerB;
    private ArrayList<String> list;//读取地图与后续指令的容器


 /**启动游戏将地图数据及所有后续命令读入*/
    void startGame(Scanner input){
        
        this.list = new ArrayList<>();//读取地图与后续指令的容器
       
       

        //读入两名玩家的名字
        String[] playerName = (input.nextLine()).split(" ");
        String playerNameA = playerName[0];
        String playerNameB = playerName[1]; 

        int countRow = 0; //得到地图的行数
        countRow = readMap(list, input, countRow) - 1;//去掉多读的命令行才是真正的地图

        String firstLineOfCommand = list.get(countRow);
       
            dealWithMap( playerNameA, playerNameB, countRow);
       
        dealWithCommand(firstLineOfCommand,playerA,playerB,input);//要处理读入的移动指令。
    }

    
    private void dealWithMap( String playerNameA, String playerNameB, int countRow ) {
        int[][] data = new int[countRow][]; //临时存放地图的容器,将数据再转移到mapUnit类的map中
        
        mapUnit[][] map = new mapUnit[countRow][];

        this.playerA= new Player(playerNameA);
        this.playerB= new Player(playerNameB);

        for (int i = 0 ; i < countRow ; i ++){
            String[] dataInString = list.get(i).split(" ");
            int[] row = Arrays.stream(dataInString).mapToInt(Integer::parseInt).toArray();
            //在这里，代码使用 Java 8 的 Stream API 将一个字符串数组转换为 int 型数组。具体来说，它执行以下操作：
            // Arrays.stream(parts) 将字符串数组 parts 转换为一个流（Stream）对象。
            // .mapToInt(Integer::parseInt) 应用 Integer.parseInt() 方法将每个字符串变为 int 值，并返回 IntStream 流对象。
            // .toArray() 将 IntStream 对象转换回原始的 int 数组。---------chatgpt告诉我的
            data[i] = row;
            map[i] = new mapUnit[data[i].length];
            for (int j = 0 ; j < data[i].length; j++){
                map[i][j] = new mapUnit(data[i][j]);
                if (map[i][j].mapUnitType == 2){
                    setPlayerProperties( map, i, j);
                } 
                if (map[i][j].mapUnitType == 5){
                    setBoxProperties(map, i, j);
                }
            }
        }

        this.boardA = new Board(map);//为两玩家创建两个地图。
        this.boardB = new Board(map);
        
    }


    


    /**
     * 读入地图但多会读入第一行的命令
     * @param list
     * @param input
     * @param countRow
     * @return 返回读入的行数
     */
    private int readMap(ArrayList<String> list, Scanner input, int countRow) {
        do{
            String line = input.nextLine(); // 读取下一行
            list.add(line);
        }while(Character.isDigit((list.get(countRow++)).charAt(0)));// 判断是否有下一行
        return countRow;
    }

    /**设置箱子的位置 */
    private void setBoxProperties(mapUnit[][] map, int i, int j) {
        playerA.box.setPositionX(i);
        playerA.box.setPositionY(j);
        playerB.box.setPositionX(i);
        playerB.box.setPositionY(j);
        map[i][j].mapUnitType = 0;
    }

    /**设置玩家属性，位置 */
    private void setPlayerProperties( mapUnit[][] map, int i, int j) {
       
        //将Player对象与Map类中的player变量关联起来，避免出现空指针异常
        
        this.playerA.setPositionX(i);
        this. playerA.setPositionY(j);
        
        this.playerB.setPositionX(i);
        this.playerB.setPositionY(j);  
        map[i][j].mapUnitType = 0;
        //角色其实并不构成地图的基本单元，通过Player类里的positionX，positionY来记录其坐标，而原本的位置即为空地
    }


    /**
     * 处理读入的命令。
     * @param firstCommandLine
     * @param playerA
     * @param playerB
     * @param input
     */
     void dealWithCommand(String firstCommandLine,Player playerA,Player playerB, Scanner input){
        String tempCommandLine = firstCommandLine;
        String[] nameAndCommand= tempCommandLine.split(" ");//nameAndCommand[0]是名字，nameAndCommand[1]是命令
        String lastRecordName = tempCommandLine.split(" ")[0];//lastRecordName是第一句命令中的人名
        char command = tempCommandLine.split(" ")[1].charAt(0);//第一个输入的玩家的指令
        int loopTime = 0,flag = 0;
        


        while((playerA.getQuitGameFlag() == false && playerA.getwinFlag() == false) ||//有任何一位玩家未结束游戏，就继续进行游戏
        (playerB.getQuitGameFlag() == false && playerB.getwinFlag() == false )){
            
            if((playerA.getQuitGameFlag() == true || playerA.getwinFlag() == true) 
            && nameAndCommand[0].equals(playerA.getName())){ //若A玩家以结束游戏仍继续输入，则使得该玩家错误移动+1
                playerA.addIllegalInput(); 
                flag = 1;//flag == 1表示错误移动次序，因而不对其读入的command作出响应                  
            }
            if((playerB.getQuitGameFlag() == true || playerB.getwinFlag() == true) 
                && nameAndCommand[0].equals(playerB.getName())){//若B玩家以结束游戏仍继续输入，则使得该玩家错误移动+1
                    playerB.addIllegalInput();
                    flag = 1;//flag == 1表示错误移动次序，因而不对其读入的command作出响应
                    
                }
            if((nameAndCommand[0].equals(lastRecordName) && loopTime != 0)){//判断是否有玩家连续输入
                if((playerA.getQuitGameFlag() == false && playerA.getwinFlag() == false) && 
                (playerB.getQuitGameFlag() == false && playerB.getwinFlag() == false)){ //双方都没有结束游戏时需交替输入
                    dealWithWrongInputOrder(playerA, playerB, lastRecordName);
                    flag = 1;//flag == 1表示错误移动次序，因而不对其读入的command作出响应
                   
                }
               
                
            }
           
            if(flag == 0){ //flag == 0表示有效的输入次序
                Player player;
                if(playerA.getName().equals(nameAndCommand[0])){
                    player = playerA;board = boardA;
                    //根据命令对于其中之一的角色进行移动，关联到角色A或角色B
                    }
                else{
                    player = playerB;board = boardB;
                }
                switch(command){
                    case 'h':dealWithLeftMove(player);break;
                    case 'j':dealWithDownMove(player);break;
                    case 'k':dealWithUpMove(player);break;
                    case 'l':dealWithRightMove(player);break;
                    case 'q':player.switchquitGameFlag();break;       
                    default :player.addInvalidMove();break;
                    //q不算在有效命令内
                }
            }
            if(flag == 0 && isValid(command)){ //如果上一次输入的人与此次不同，或者输入的是合法的，才会被记录到lastRecordName，lastRecordName中记录的是上一次合法输入的玩家的名字，若玩家输入了一次非法指令，则不将其记录入lastRecordName中
                lastRecordName = nameAndCommand[0];
            }
            if(flag == 0 && loopTime == 0 && isValid(command) == false){//第一次的玩家就输入了非法输入
                if(lastRecordName.equals(playerA.getName())){
                    lastRecordName = playerB.getName();
                }
                else{
                    lastRecordName = playerA.getName();
                }
            }

            if((playerA.getQuitGameFlag() == true || playerA.getwinFlag() == true) &&
        (playerB.getQuitGameFlag() == true || playerB.getwinFlag() == true )){
            break;
        }

            tempCommandLine = input.nextLine();

            nameAndCommand = tempCommandLine.split(" ");
            
            command = nameAndCommand[1].charAt(0);

            loopTime++;flag = 0;
        }

        printResult(playerA, playerB);
    }

    /**
     * 判断玩家输入的指令是否合法
     * @param instruction
     * @return
     */
    private boolean isValid(char instruction){
        if(instruction !='h' && instruction != 'j' && instruction 
        != 'k' && instruction != 'l' &&instruction != 'q'){
            return false;
        }
        else return true;
    }


    /**打印地图输出的结果
     * @param playerA 玩家A
     * @param playerB 玩家B
     */
    private void printResult(Player playerA, Player playerB) {
        System.out.printf("%s %d %d %d %d %d %d %d\n", playerA.getName(),
        playerA.getValidMove(),playerA.getIllegalInput(),playerA.getInvalidMove(),
        playerA.getmeetWallCount(),playerA.getOverlapBarrierTimes(),playerA.box.getmeetWallCount(),playerA.box.getmeetBarrierTimes());

        System.out.printf("%s %d %d %d %d %d %d %d\n", playerB.getName(),
        playerB.getValidMove(),playerB.getIllegalInput(),playerB.getInvalidMove(),
        playerB.getmeetWallCount(),playerB.getOverlapBarrierTimes(),playerB.box.getmeetWallCount(),playerB.box.getmeetBarrierTimes());

        String winnerName = judgeWhoWins(playerA, playerB);

        System.out.printf("%s", winnerName);

    }

    /**
     * 哪位玩家获胜则返回那位玩家的名字，如若平局则返回“draw”
     * @param playerA
     * @param playerB
     * @return
     */
    private String judgeWhoWins(Player playerA, Player playerB) {
        String winnerName = "00000";
        int scoreA = playerA.getValidMove() + playerA.box.getmeetBarrierTimes() + playerA.box.getmeetWallCount() + 
                    playerA.getOverlapBarrierTimes() + playerA.getmeetWallCount();
        int scoreB = playerB.getValidMove() + playerB.box.getmeetBarrierTimes() + playerB.box.getmeetWallCount() + 
                    playerA.getOverlapBarrierTimes() + playerA.getmeetWallCount();


        if(playerA.getwinFlag() == true && playerB.getwinFlag() == true){
            if (scoreA < scoreB){
                winnerName = playerA.getName();
            }
            else if(scoreB < scoreA){
                winnerName = playerB.getName();
            }
            else{
               if(playerA.getWinningRank() < playerB.getWinningRank()){         //此处尚未检查，可能有错：在双方都到达终点并且得分相同的情况
                    winnerName = playerA.getName();
               }
               else if(playerB.getWinningRank() < playerA.getWinningRank()){
                    winnerName = playerB.getName();
               }
            }
        }else if(playerA.getwinFlag() == true && playerB.getwinFlag() == false){
            winnerName = playerA.getName();
        }else if(playerA.getwinFlag() == false && playerB.getwinFlag() == true){
            winnerName = playerB.getName();
        }
        else if(playerA.getwinFlag() == false && playerB.getwinFlag() == false){
            winnerName = "draw";
        }
        return winnerName;
    }


/**处理玩家错误的输入顺序，eg.同一位玩家连续两次输入有效命令 
 * @param playerA
 * @param playerB
 * @param lastRecordName
*/
    private void dealWithWrongInputOrder(Player playerA, Player playerB, String lastRecordName) {
        if(lastRecordName.equals(playerA.getName())){
            playerA.addIllegalInput();
        }
        else{
            playerB.addIllegalInput();
        }
    }


/**处理玩家右移的情况，判断是否会有撞墙，遇到障碍物，到达终点的情况
 * @param player
 */
    private void dealWithRightMove(Player player) {
        player.addValidMove();

        int flag = 0;//判断箱子是否正常移动
        if(player.getPositionX() == player.box.getPositionX() &&//玩家先是在原地去推动箱子，先去判断箱子遇到的情况，如果箱子正常移动玩家方能正常移动
        (player.getPositionY() + 1)== player.box.getPositionY()){
            player.box.rightMove();
            if(isBarrier(player.box.getPositionX(), player.box.getPositionY())){
                player.box.addmeetBarrierTimes();player.box.leftMove();flag = 1;
            }
            if(isWall(player.box.getPositionX(), player.box.getPositionY()) ){
                player.box.leftMove();player.box.addMeetWallCount();flag = 1;
            }
        }

        if(flag == 1){
            return;
        }
        player.rightMove();
        

         if(isBarrier(player.getPositionX(), player.getPositionY())){//此处先判断是否移动后的位置为障碍物再去判断是否为墙的情况可以避免障碍物贴着墙，玩家撞到后折返反复统计遇到障碍物的次数的问题
            player.addOverlapBarrierTimes();
        }
        if(isWall(player.getPositionX(), player.getPositionY())){
            player.leftMove();player.addMeetWallCount();
        }
        
        
        if(reachEnd(player.box.getPositionX(), player.box.getPositionY())){
            player.switchWinFlag();
            Player.addWinningCount();
            player.setWinningRank(Player.getWinningCount());
        }
    }


/**处理玩家上移的情况，判断是否会有撞墙，遇到障碍物，到达终点的情况
 * @param player
 */
    private void dealWithUpMove(Player player) {
       
        player.addValidMove();

        int flag = 0;//判断箱子是否正常移动
        if((player.getPositionX() - 1) == player.box.getPositionX() &&//玩家先是在原地去推动箱子，先去判断箱子遇到的情况，如果箱子正常移动玩家方能正常移动
        (player.getPositionY() )== player.box.getPositionY()){
            player.box.upMove();
            if(isBarrier(player.box.getPositionX(), player.box.getPositionY())){
                player.box.addmeetBarrierTimes();player.box.downMove();flag = 1;
            }
            if(isWall(player.box.getPositionX(), player.box.getPositionY()) ){
                player.box.downMove();player.box.addMeetWallCount();flag = 1;
            }
        }

        if(flag == 1){
            return;
        }
        player.upMove();
        if(isBarrier(player.getPositionX(), player.getPositionY())){
            player.addOverlapBarrierTimes();
        }
        if(isWall(player.getPositionX(), player.getPositionY())){
            player.downMove();player.addMeetWallCount();
        }
        
        if(reachEnd(player.box.getPositionX(), player.box.getPositionY())){
            player.switchWinFlag();
            Player.addWinningCount();
            player.setWinningRank(Player.getWinningCount());
        }
    }


/**处理玩家下移的情况，判断是否会有撞墙，遇到障碍物，到达终点的情况
 * @param player
 */
    private void dealWithDownMove(Player player) {
        player.addValidMove();

        int flag = 0;//判断箱子是否正常移动
        if((player.getPositionX() +1) == player.box.getPositionX() &&//玩家先是在原地去推动箱子，先去判断箱子遇到的情况，如果箱子正常移动玩家方能正常移动
        (player.getPositionY())== player.box.getPositionY()){
            player.box.downMove();
            if(isBarrier(player.box.getPositionX(), player.box.getPositionY())){
                player.box.addmeetBarrierTimes();player.box.upMove();flag = 1;
            }
            if(isWall(player.box.getPositionX(), player.box.getPositionY()) ){
                player.box.upMove();player.box.addMeetWallCount();flag = 1;
            }
        }

        if(flag == 1){
            return;
        }
        player.downMove();
        if(isBarrier(player.getPositionX(), player.getPositionY())){
            player.addOverlapBarrierTimes();
        }
        if(isWall(player.getPositionX(), player.getPositionY())){
            player.upMove();player.addMeetWallCount();
        }
        
        if(reachEnd(player.box.getPositionX(), player.box.getPositionY())){
            player.switchWinFlag();
            Player.addWinningCount();
            player.setWinningRank(Player.getWinningCount());
        }
    }


/**处理玩家左移的情况，判断是否会有撞墙，遇到障碍物，到达终点的情况
 * @param player
 */
    private void dealWithLeftMove(Player player) {
        player.addValidMove();

        int flag = 0;//判断箱子是否正常移动
        if(player.getPositionX() == player.box.getPositionX() &&//玩家先是在原地去推动箱子，先去判断箱子遇到的情况，如果箱子正常移动玩家方能正常移动
        (player.getPositionY() - 1)== player.box.getPositionY()){
            player.box.leftMove();
            if(isBarrier(player.box.getPositionX(), player.box.getPositionY())){
                player.box.addmeetBarrierTimes();player.box.rightMove();flag = 1;
            }
            if(isWall(player.box.getPositionX(), player.box.getPositionY()) ){
                player.box.rightMove();player.box.addMeetWallCount();flag = 1;
            }
        }

        if(flag == 1){
            return;
        }
        player.leftMove();
        if(isBarrier(player.getPositionX(), player.getPositionY())){
            player.addOverlapBarrierTimes();
        }
        if(isWall(player.getPositionX(), player.getPositionY())){
            player.rightMove();player.addMeetWallCount();
        }
        
        if(reachEnd(player.box.getPositionX(), player.box.getPositionY())){
            player.switchWinFlag();
            Player.addWinningCount();
            player.setWinningRank(Player.getWinningCount());
        }
    }

    /**
     * 判断x,y 位置是否为墙。
     * @param x 
     * @param y
     * @return true则表明是墙，false则反之
     */
    boolean isWall(int x, int y){
        if (board.map[x][y].mapUnitType == 1){
            return true;
        }
        return false;  
    }

/**
     * 判断x,y 位置是否为障碍物。
     * @param x 
     * @param y
     * @return true则表明是障碍物，false则反之
     */
    boolean isBarrier(int x, int y){
        if (board.map[x][y].mapUnitType == 4){
            return true;
        }
        return false;  
    }
    /**
     * 判断x,y 位置是否为终点。
     * @param x 
     * @param y
     * @return true则表明是终点，false则反之
     */
    boolean reachEnd(int x, int y){
        if (board.map[x][y].mapUnitType == 3){
            return true;
        }
        return false;  
    }
    
}

class Board{
   public mapUnit[][] map ;
    Board(mapUnit[][] map){
        this.map = map;
    }
}

    

/**
 * 负责记录角色所处的位置并对于所输入的命令进行相应的操作
 */
class Player{
    /**
     * 玩家的x坐标
     */
    private int positionX;
    /**
     * 玩家的y坐标
     */
    private int positionY;
    /**
     * 玩家的名字
     */
    private String playerName;
    /**
     * 非法的输入（连续由同一个人输入）
     */
    private int illegalInput = 0;
    /**
     * 无效移动数
     */
    private int inValidMove = 0;
    /**
     * 与障碍物重合的次数
     */
    private int overlapBarrierTimes = 0;
    /**
     * 有效移动数
     */
    private int validMove = 0;
    /**
     * 是否获胜的标志
     */
    private boolean winFlag = false;
    /**
     * 撞墙数
     */
    private int meetWallCount = 0;
    /**
     * 是否退出游戏的标志
     */
    private boolean quitGameFlag;
    /**
     * 到达终点的玩家按先后顺序的排行
     */
    private int winningRank = 0;
    /**
     * 获胜的得分
     */
    private static int winningCount = 0;

    Box box;

    Player(String playerName){
        this.playerName = playerName;
        this.box = new Box();
    }

    /** 左移 */
    void leftMove(){   
        positionY--;
    }
    /** 右移*/
    void rightMove(){
        positionY++;
    }
    /**上移*/
    void upMove(){
        positionX--;
    }
    /**下移*/
    void downMove(){
        positionX++;
    }

    /** 获取位置x
     * @return 位置x
     */
    int getPositionX(){
        return positionX;
    }
    /** 获取位置y
     * @return 位置y 
     */
    int getPositionY(){
        return positionY;
    }
    /**
     * 设置位置x
     */
    void setPositionX(int X){
        positionX = X;
    }
    /**
     * 设置位置y
     */
    void setPositionY(int Y){
        positionY = Y;
    }
    /**
     * 返回玩家姓名
     * @return 玩家的名字
     */
    String getName(){
        return playerName;
    }
    /**
     * 获取错误移动次序数
     * @return 错误移动次序数
     */
    int getIllegalInput(){
        return illegalInput;
    }
    /**
     * 错误移动次序数+1
     */
    void addIllegalInput(){
        illegalInput++;
    }
    /**
     * 获取非法移动次数
     * @return 非法移动次数
     */
    int getInvalidMove(){
        return inValidMove;
    }
    /**
     * 非法移动数+1
     */
    void addInvalidMove(){
        inValidMove++;
    }
    /**
     * 获取与障碍物重合次数
     * @return 与障碍物重合次数
     */
    int getOverlapBarrierTimes(){
        return overlapBarrierTimes;
    }
    /**
     * 与障碍物重合次数+1
     */
    void addOverlapBarrierTimes(){
        overlapBarrierTimes++;
    }
    /**
     * 获取合法移动数
     * @return 合法移动数
     */
    int getValidMove(){
        return validMove;
    }
    /**
     * 合法移动数+1
     */
    void addValidMove(){
        validMove++;
    }
    /**
     * 获取是否到达终点的标志，到达了返回true
     * @return 到达终点的标志
     */
    boolean getwinFlag(){
        return winFlag;
    }
    /**
     * 设置到达终点的标志为true
     */
    void switchWinFlag(){
       winFlag = true;
    }
    /**
     * 获取撞墙次数
     * @return 撞墙次数
     */
    int getmeetWallCount(){
        return meetWallCount;
    }
    /**
     * 撞墙次数+1
     */
    void addMeetWallCount(){
        meetWallCount++;
    }
    /**
     * 获取是否主动退出游戏的标志
     * @return 主动退出游戏的标志
     */
    boolean getQuitGameFlag(){
        return quitGameFlag;
    }
    /**
     * 将主动退出游戏的标志设置为true
     */
    void switchquitGameFlag(){
       quitGameFlag = true;
    }
    /**
     * 获取到达终点的先后排名
     * @return 到达终点的排名
     */
    int getWinningRank(){
        return winningRank;
    }
    /**
     * 设置到达终点的排名
     * @param n 到达终点的排名
     */
    void setWinningRank(int n){
        this.winningRank = n;
    }
    /**
     * 获胜得分
     */
   static void addWinningCount(){
        winningCount++;
    }
    /**
     * 获取获胜得分 
     * @return 获胜得分
     */
   static int  getWinningCount(){
        return winningCount;
    }
}



/**
 * 棋盘上的每一个格子的类型，是墙还是空地
 */
class mapUnit{
    /**
     * map组成的基本元素
     */
    int mapUnitType;
    mapUnit(int mapUnitType){
        this.mapUnitType = mapUnitType;
    }
}

class Box{
    /**
     * 箱子的x坐标
     */
    private int positionX;
    /**
     * 箱子的y坐标
     */
    private int positionY;
    private int meetWallCount = 0;
    private int meetBarrierTimes = 0;
    
    
    /** 获取位置x
     * @return 位置x
     */
    int getPositionX(){
        return positionX;
    }
    /** 获取位置y
     * @return 位置y 
     */
    int getPositionY(){
        return positionY;
    }
    /**
     * 设置位置x
     */
    void setPositionX(int X){
        positionX = X;
    }
    /**
     * 设置位置y
     */
    void setPositionY(int Y){
        positionY = Y;
    }

    /** 左移 */
    void leftMove(){   
        positionY--;
    }
    /** 右移*/
    void rightMove(){
        positionY++;
    }
    /**上移*/
    void upMove(){
        positionX--;
    }
    /**下移*/
    void downMove(){
        positionX++;
    }

    /**
     * 获取与障碍物重合次数
     * @return 与障碍物重合次数
     */
    int getmeetBarrierTimes(){
        return meetBarrierTimes;
    }
    /**
     * 与障碍物重合次数+1
     */
    void addmeetBarrierTimes(){
        meetBarrierTimes++;
    }

    /**
     * 获取撞墙次数
     * @return 撞墙次数
     */
    int getmeetWallCount(){
        return meetWallCount;
    }
    /**
     * 撞墙次数+1
     */
    void addMeetWallCount(){
        meetWallCount++;
    }
}