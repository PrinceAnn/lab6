public class testClone {
    public static void main(String[] args){

        AClass oj1 = new AClass();
        AClass oj2 = new AClass();
        oj1.map =new mapUnit[10][];
        for (int i = 0 ; i < 10; i ++){
            oj1.map[i] = new mapUnit[5];
            for (int j = 0 ; j <5; j++){
                oj1.map[i][j] = new mapUnit(1);
            }
        }

        try {
            oj2 = (AClass) oj1.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println("clone not supported");
        }

    }
}

class AClass implements Cloneable {
    mapUnit[][] map;

    @Override
    public Object clone() throws CloneNotSupportedException {
        AClass mapClone = (AClass)super.clone();
        mapClone.map = new mapUnit[this.map.length][];
        for (int i = 0; i < this.map.length; i++) {
            mapClone.map[i] = new mapUnit[this.map[i].length];
            for (int j = 0; j < this.map[i].length; j++) {
                mapClone.map[i][j] = new mapUnit(this.map[i][j].mapUnitType);
            }
        }
        return mapClone;
    }
}

class mapUnit{
    int mapUnitType;
    mapUnit(int mapUnitType){
        this.mapUnitType = mapUnitType;
    }
}
