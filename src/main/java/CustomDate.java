public class CustomDate {
    private int day;
    private int month;
    private int year;
    private Double price;
    private int vat;
    private String full;

    public CustomDate() {
        int month = Helpers.randomNumber(12);
        this.month = month;
        int dayLimit = 0;
        if(month==2) dayLimit = 28;
        if(month==4||month==6||month==9||month==11) dayLimit = 30;
        if(month==1||month==3||month==4||month==7||month==8||month==10||month==12) dayLimit = 31;
        this.day = Helpers.randomNumber(dayLimit);
        int[] years2 = {2010, 2014, 2019, 2021};
        this.year = years2[Helpers.randomArrayElement(4)];
        this.price = Helpers.randomPrice();
        int[] vatValue = {0, 7, 22};
        this.vat = vatValue[Helpers.randomArrayElement(3)];
        this.full = getCarDate();
    }

    public String getCarDate(){
        return year+"/"+month+"/"+day;
    }

    public Double getVatPrice(){
        if(vat != 0){
            return price + (double)(Math.round(price * vat)/100);
        }
        else{
            return price;
        }

    }

    public Double getPrice() {
        return price;
    }

    public int getVat() {
        return vat;
    }
}
