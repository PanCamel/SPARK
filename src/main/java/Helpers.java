public class Helpers {

    public static Integer randomNumber(Integer limit) {
        return (int)(Math.random()*limit+1);
    }

    public static Integer randomArrayElement(Integer size) {
        return (int)(Math.random()*size);
    }

    public static Double randomPrice() {
        return (double)(Math.round(Math.random()*10000000+800000)/100);
    }

}
