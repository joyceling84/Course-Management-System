import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Fee {
    boolean isMandatory;
    private String type;
    private String name;
    private double amount;

    public Fee(boolean isMandatory, String type, String name, double amount) {
        this.isMandatory = isMandatory;
        this.type = type;
        this.name = name;
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public String toCSVString() {
        return  isMandatory + "," + type + "," + "," + amount;
    }

    public static List<Fee> getAccommodation() {
        List<Fee> accommodationList = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get("fees.csv"));
            for (String line : lines) {
                String[] items = line.split(",");
                if (items[1].equals("Accommodation fee")) {
                    boolean isMandatory = items[0].equals("mandatory");
                    accommodationList.add(new Fee(isMandatory, items[1], items[2], Double.parseDouble(items[3])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accommodationList;
    }
    
}