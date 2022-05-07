public class Invoices {
    private int zakresDo;
    private int zakresOd;
    private int rok;
    private String rodzaj;

    public Invoices() {
    }

    public void setZakresDo(int zakresDo) {
        this.zakresDo = zakresDo;
    }

    public void setZakresOd(int zakresOd) {
        this.zakresOd = zakresOd;
    }

    public void setRok(int rok) {
        this.rok = rok;
    }

    public String getRodzaj() {
        return rodzaj;
    }

    public int getZakresDo() {
        return zakresDo;
    }

    public int getZakresOd() {
        return zakresOd;
    }

    public int getRok() {
        return rok;
    }
}
