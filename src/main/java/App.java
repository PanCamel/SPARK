import com.fasterxml.uuid.Generators;
import com.google.gson.Gson;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import spark.Request;
import spark.Response;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Month;
import java.time.Year;
import java.util.*;
import java.util.List;

import static spark.Spark.*;

public class App {

    public static void main(String[] args) {
        port(getHerokuPort());
        staticFiles.location("/public");

        ArrayList<Car> carArrayList = new ArrayList<>();
        ArrayList<Car> generatedCarList = new ArrayList<>();
        ArrayList<Car> generatedSearchCarList = new ArrayList<>();

        post("/add", (req, res) -> addFunction(req, res, carArrayList));
        get("/json", (req, res) -> jsonFunction(req, res, carArrayList));
        post("/delete", (req, res) -> deleteFunction(req, res, carArrayList));
        post("/update", (req, res) -> updateFunction(req, res, carArrayList));

        post("/generate", (req, res) -> generateFunction(req, res, generatedCarList));
        get("/generatedjson", (req, res) -> generatedJsonFunction(req, res, generatedCarList));
        post("/invoice", (req, res) -> invoiceFunction(req, res, generatedCarList));
        get("/invoices/:name", (req, res) -> invoicesFunction(req, res));

        post("/searchgenerate", (req, res) -> searchGenerateFunction(req, res, generatedSearchCarList));
        post("/invoiceall", (req, res) -> invoiceAllFunction(req, res, generatedSearchCarList));

    }
    static int getHerokuPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567;
    }

    static String addFunction(Request req, Response res, ArrayList<Car> carArrayList){
        Gson gson = new Gson();
        System.out.println(req.body());
        Car car = gson.fromJson(req.body(), Car.class);
        if(carArrayList.isEmpty()){
            car.setId(1);
        }
        else{
            int indeks = carArrayList.size() - 1;
            int i = carArrayList.get(indeks).getId() + 1;
            car.setId(i);
        }
        carArrayList.add(car);
        System.out.println(gson.toJson(car));
        res.type("application/json");
        return gson.toJson(car);
    }

    static String jsonFunction(Request req, Response res, ArrayList<Car> carArrayList){
        res.type("application/json");
        Gson gson = new Gson();
        return gson.toJson(carArrayList);
    }

    static String deleteFunction(Request req, Response res, ArrayList<Car> carArrayList){
        res.type("application/json");
        Gson gson = new Gson();
        Integer id = gson.fromJson(req.body(), Car.class).getId();
        System.out.println(String.valueOf(id));
        for(int i = 0;i<carArrayList.size();i++){
            if(Objects.equals(carArrayList.get(i).getId(), id)){
                carArrayList.remove(i);
            }
        }
        return "deleted";
    }

    static String updateFunction(Request req, Response res, ArrayList<Car> carArrayList){
        res.type("application/json");
        Gson gson = new Gson();
        Integer id = gson.fromJson(req.body(), Car.class).getId();
        Integer rok = gson.fromJson(req.body(), Car.class).getRok();
        String model = gson.fromJson(req.body(), Car.class).getModel();
        for(int i = 0;i<carArrayList.size();i++){
            if(Objects.equals(carArrayList.get(i).getId(), id)){
                carArrayList.get(i).setModel(model);
                carArrayList.get(i).setRok(rok);
            }
        }
        return "add";
    }

    static String generateFunction(Request req, Response res, ArrayList<Car> generatedCarList){
        int listSize = generatedCarList.size();
        int id = 1;
        if(!generatedCarList.isEmpty()){
            id = generatedCarList.get(listSize-1).getId() + 1;
        }
        String[] models = {"Renault", "Smart", "Opel", "Skoda"};
        int[] years = {2012, 1994, 2019, 2006};
        String[] colors = {"blue", "red", "green", "black", "gray"};
        for(int i=0; i<100; i++){
            int m = (int)(Math.random()*4);
            int y = (int)(Math.random()*4);
            int c = (int)(Math.random()*5);
            ArrayList<Airbag> airbags = new ArrayList<>();
            Random rd = new Random();
            airbags.add(new Airbag("kierowca", rd.nextBoolean()));
            airbags.add(new Airbag("pasażer", rd.nextBoolean()));
            airbags.add(new Airbag("kanapa", rd.nextBoolean()));
            airbags.add(new Airbag("boczne", rd.nextBoolean()));
            Car car = new Car(models[m], years[y], airbags, colors[c]);
            car.setId(id);
            generatedCarList.add(car);
            id++;
        }
        res.type("application/json");
        Gson gson = new Gson();
        return gson.toJson(generatedCarList);
    }

    static String generatedJsonFunction(Request req, Response res, ArrayList<Car> generatedCarList){
        res.type("application/json");
        Gson gson = new Gson();
        return gson.toJson(generatedCarList);
    }

    static String invoiceFunction(Request req, Response res, ArrayList<Car> generatedCarList) throws IOException, DocumentException {
        res.type("application/json");
        Gson gson = new Gson();
        Integer id = gson.fromJson(req.body(), Car.class).getId();
        for(int i = 0;i<generatedCarList.size();i++){
            if(Objects.equals(generatedCarList.get(i).getId(), id)){
                Car car = generatedCarList.get(i);
                car.setFaktura(true);
                Document document = new Document(); // dokument pdf
                String path = "invoices/" + car.getUuid() + ".pdf"; // lokalizacja zapisu
                PdfWriter.getInstance(document, new FileOutputStream(path));

                document.open();
                Font font = FontFactory.getFont(FontFactory.HELVETICA, 16, BaseColor.BLACK);
                Font fontBig = FontFactory.getFont(FontFactory.HELVETICA, 20, BaseColor.BLACK);
                Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
                HashMap<String, BaseColor> map = new HashMap<>() {{
                    put("red", BaseColor.RED);
                    put("green", BaseColor.GREEN);
                    put("blue", BaseColor.BLUE);
                    put("black", BaseColor.BLACK);
                    put("gray", BaseColor.GRAY);
                }};
                Font fontColor = FontFactory.getFont(FontFactory.HELVETICA, 16, map.get(car.getKolor()));
                Paragraph chunk = new Paragraph("FAKTURA dla: " + car.getUuid(), fontBold);
                Paragraph model = new Paragraph("model: " + car.getModel(), fontBig);
                Paragraph kolor = new Paragraph("kolor: " + car.getKolor(), fontColor);
                Paragraph rok = new Paragraph("rok: " + car.getRok(), font);
                document.add(chunk);
                document.add(model);
                document.add(kolor);
                document.add(rok);
                List<Airbag> airbags= Arrays.asList(car.getAirbags().get(0), car.getAirbags().get(1), car.getAirbags().get(2), car.getAirbags().get(3));
                airbags.stream()
                        .map( x -> "poduszka: "+x.getDesciption()+" - "+x.getValue())
                        .forEach(y-> {
                            Paragraph poduszka = new Paragraph(y, font);
                            try {
                                document.add(poduszka);
                            } catch (DocumentException e) {
                                e.printStackTrace();
                            }
                        });
                Image img = Image.getInstance("src/main/resources/cars/"+car.getModel()+".jpg");
                document.add(img);
                document.close();
            }
        }
        return gson.toJson(generatedCarList);
    }

    static String invoicesFunction(Request req, Response res) throws IOException {
        res.type("application/octet-stream"); //
        res.header("Content-Disposition", "attachment; filename="+req.params("name")+".pdf"); // nagłówek

        OutputStream outputStream = res.raw().getOutputStream();
        outputStream.write(Files.readAllBytes(Path.of("invoices/" + req.params("name") + ".pdf"))); // response pliku do przeglądarki
        return "downloaded";
    }

    static String searchGenerateFunction(Request req, Response res, ArrayList<Car> generatedCarList) throws IOException {
        int listSize = generatedCarList.size();
        int id = 1;
        if(!generatedCarList.isEmpty()){
            id = generatedCarList.get(listSize-1).getId() + 1;
        }
        String[] models = {"Renault", "Smart", "Opel", "Skoda"};
        int[] years = {2012, 1994, 2019, 2006};
        String[] colors = {"blue", "red", "green", "black", "gray"};
        for(int i=0; i<100; i++){
            int m = (int)(Math.random()*4);
            int y = (int)(Math.random()*4);
            int c = (int)(Math.random()*5);
            ArrayList<Airbag> airbags = new ArrayList<>();
            Random rd = new Random();
            airbags.add(new Airbag("kierowca", rd.nextBoolean()));
            airbags.add(new Airbag("pasażer", rd.nextBoolean()));
            airbags.add(new Airbag("kanapa", rd.nextBoolean()));
            airbags.add(new Airbag("boczne", rd.nextBoolean()));
            Car car = new Car(models[m], years[y], airbags, colors[c]);
            CustomDate data = new CustomDate();
            car.setId(id);
            car.setData(data);
            generatedCarList.add(car);
            id++;
        }
        res.type("application/json");
        Gson gson = new Gson();
        return gson.toJson(generatedCarList);
    }

    static String invoiceAllFunction(Request req, Response res, ArrayList<Car> generatedCarList) throws IOException, DocumentException {
        res.type("application/json");
        Gson gson = new Gson();
        String rodzaj = gson.fromJson(req.body(), Invoices.class).getRodzaj();
        Invoice invoice = new Invoice( "sprzedawca: firma sprzedająca auta", "nabywca: nabywca");
        ArrayList<Car> list = generatedCarList;
        if(Objects.equals(rodzaj, "invoice_all_cars_by_year")){
            int rok = gson.fromJson(req.body(), Invoices.class).getRok();
            invoice.setRodzaj("Faktura za wszystkie auta z rocznika " + rok);
        }
        if(Objects.equals(rodzaj, "invoice_all_cars_by_price")){
            int zakresOd = gson.fromJson(req.body(), Invoices.class).getZakresOd();
            int zakresDo = gson.fromJson(req.body(), Invoices.class).getZakresDo();
            invoice.setRodzaj("Faktura za wszystkie auta z zakresu od " + zakresOd + " zł do " + zakresDo + " zł");
        }
        if(Objects.equals(rodzaj, "invoice_all_cars")){
            invoice.setRodzaj("Faktura za wszystkie auta");
        }

        for(int i = 0;i<list.size();i++){
                Car car = list.get(i);
                car.setFaktura(true);
                Document document = new Document(); // dokument pdf
                String path = "invoices/" + rodzaj + "_" + invoice.getTime() + ".pdf"; // lokalizacja zapisu
                PdfWriter.getInstance(document, new FileOutputStream(path));
                document.open();
                Font font = FontFactory.getFont(FontFactory.HELVETICA, 16, BaseColor.BLACK);
                Font fontTable = FontFactory.getFont(FontFactory.HELVETICA, 13, BaseColor.BLACK);
                Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK);
                Font fontColor = FontFactory.getFont(FontFactory.HELVETICA, 16, BaseColor.RED);
                GregorianCalendar data = new GregorianCalendar();
                invoice.setTitle(Year.now().getValue(), data.get(Calendar.MONTH) + 1, data.get(Calendar.DAY_OF_MONTH), list.size());
                Paragraph titlef = new Paragraph("FAKTURA: " + invoice.getTitle(), fontBold);
                Paragraph buyer = new Paragraph("Nabywca: " + invoice.getSeller(), font);
                Paragraph seller = new Paragraph("Sprzedawca: " + invoice.getBuyer(), font);
                Paragraph kolor = new Paragraph(invoice.getRodzaj(), fontColor);
                document.add(titlef);
                document.add(buyer);
                document.add(seller);
                document.add(kolor);
                PdfPTable table = new PdfPTable(4);
                PdfPCell c = new PdfPCell(new Phrase("lp", fontTable));
                table.addCell(c);
                c = new PdfPCell(new Phrase("cena", fontTable));
                table.addCell(c);
                c = new PdfPCell(new Phrase("vat", fontTable));
                table.addCell(c);
                c = new PdfPCell(new Phrase("wartosc", fontTable));
                table.addCell(c);
                for(int j = 0;j<list.size();j++) {
                    c = new PdfPCell(new Phrase(String.valueOf(list.get(j).getId()), fontTable));
                    table.addCell(c);
                    c = new PdfPCell(new Phrase(String.valueOf(list.get(j).getData().getPrice()), fontTable));
                    table.addCell(c);
                    c = new PdfPCell(new Phrase(String.valueOf(list.get(j).getData().getVat()) + "%",fontTable));
                    table.addCell(c);
                    c = new PdfPCell(new Phrase(String.valueOf(list.get(j).getData().getVatPrice()),fontTable));
                    table.addCell(c);
                }
                document.add(table);
                Paragraph titlep = new Paragraph("DO ZAPLATY " + invoice.getPriceVAT(list), fontBold);
                document.add(titlep);
                document.close();
            }

        return gson.toJson(list);
    }
}

class Car{
    private String model;
    private Integer rok;
    private ArrayList<Airbag> airbags;
    private String kolor;
    private String uuid;
    private Integer id;
    private Boolean faktura;
    private CustomDate data;

    public Car(String model, Integer rok, ArrayList<Airbag> airbags, String kolor) {
        this.model = model;
        this.rok = rok;
        this.airbags = airbags;
        this.kolor = kolor;
        this.uuid = String.valueOf(Generators.randomBasedGenerator().generate());
        this.faktura = false;
    }

    public Car() {
    }

    public ArrayList<Airbag> getAirbags() {
        return airbags;
    }

    public String getUuid() {
        return uuid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        this.uuid = String.valueOf(Generators.randomBasedGenerator().generate());
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setRok(Integer rok) {
        this.rok = rok;
    }

    public String getModel() {
        return model;
    }

    public Integer getRok() {
        return rok;
    }

    public String getKolor() {
        return kolor;
    }

    public Boolean getFaktura() {
        return faktura;
    }

    public void setFaktura(Boolean faktura) {
        this.faktura = faktura;
    }

    public CustomDate getData() {
        return data;
    }

    public void setData(CustomDate data) {
        this.data = data;
    }
}

class Airbag{
    private String description;
    private Boolean value;

    public Airbag(String desciption, Boolean value) {
        this.description = desciption;
        this.value = value;
    }

    public String getDesciption() {
        return description;
    }

    public Boolean getValue() {
        return value;
    }
}

