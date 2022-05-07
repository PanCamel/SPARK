import java.sql.Timestamp;
import java.util.ArrayList;

public class Invoice {
    private long time;
    private String title;
    private String seller;
    private String buyer;
    private ArrayList<Car> carList;
    private String rodzaj;
    private int id;

    public Invoice(String seller, String buyer) {
        this.time = System.currentTimeMillis();
        this.seller = seller;
        this.buyer = buyer;
        this.id = id++;
    }

    public void setCarList(ArrayList<Car> carList) {
        this.carList = carList;
    }

    public void setRodzaj(String rodzaj) {
        this.rodzaj = rodzaj;
    }

    public long getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getSeller() {
        return seller;
    }

    public String getBuyer() {
        return buyer;
    }

    public String getRodzaj() {
        return rodzaj;
    }

    public void setTitle(int year, int month, int day, int listSize) {
        this.title = "VAT/"+year+"/"+month+"/"+day+"/"+listSize+"/"+(id+1);
    }

    public static Double getPrice(ArrayList<Car> list) {
        double wynik = 0;
        for(int i=0; i< list.size(); i++){
            wynik = wynik + list.get(i).getData().getPrice();
        }
        return wynik;
    }

    public static Double getPriceVAT(ArrayList<Car> list) {
        double wynik = 0;
        for(int i=0; i< list.size(); i++){
            wynik = wynik + list.get(i).getData().getVatPrice();
        }
        return wynik;
    }
}
