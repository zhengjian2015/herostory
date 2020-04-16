public class Test2 {
    public static void main(String[] args) {
        PrimaryGenerater primaryGenerater = PrimaryGenerater.getInstance();
        String a = primaryGenerater.generaterNextNumber("202004130001");
        System.out.println(a);
    }
}
