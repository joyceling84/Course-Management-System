import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Billing {
    private Student student;
    private List<Fee> fees = readFeesFromFile();
    private double totalAmount;
    //private double discountRate;
    private Boolean accommodation;
    private String accommodationType;

    public Billing(Student student, List<Fee> fees, double totalAmount) {
        this.student = student;
        this.fees = fees;
        this.totalAmount = totalAmount;
    }

    public Billing(String name, String id, Boolean accommodation, String accommodationType) {
        this.student = new Student(name, id);
        this.accommodation = accommodation;
        this.accommodationType = accommodationType;
    }

    public Student getStudent() {
        return student;
    }

    public List<Fee> getFees() {
        return fees;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public Boolean getAccommodation() {
        return accommodation;
    }

    public String getAccommodationType() {
        return accommodationType;
    }

    public double calculateCourseFee(Student student){
        double total = 0;
        for (Course course : student.getCoursesEnrolled()) {
            total += course.getFee();
        }
        return total;
    }

    public double calculateMandatoryFee(){
        double total = 0;
        for (Fee fee : fees) {
            if (fee.isMandatory()) {
                total += fee.getAmount();
            }
        }
        return total;
    }

    public static double calculateOptionalFee(Student student, String accommodationType){
        List<Billing> bills = readBillsFromFile();
        List<Fee> fees = readFeesFromFile();
        double accommodationAmount = 0;
        for (Billing bill : bills) {
            if (bill.getStudent().getId().equals(student.getId())) {
                if (bill.getAccommodation() && bill.getAccommodationType().equals(accommodationType)) {
                    for (Fee fee : fees) {
                        if (fee.getType().equals("Accommodation fee") && fee.getName().equals(accommodationType)) {
                            accommodationAmount = fee.getAmount();
                            break;
                        }
                    }
                }
            }
        }
        return accommodationAmount;
    }

    public double totalAmount(Student student, Boolean accommodation, String accommodationType) {
        double totalAmount = 0;
        totalAmount = calculateCourseFee(student) + calculateMandatoryFee();
        if (accommodation) 
            totalAmount += calculateOptionalFee(student, accommodationType);
        return totalAmount;
    }

    public double calculateDiscountAmount(Student student, Boolean stackup, Boolean accommodation, String accommodationType) {
        double discountRate = 0;
        double discountAmount = 0;
        if (stackup) {
            discountRate = 0.1;
        }
        discountAmount = totalAmount(student, accommodation, accommodationType) * discountRate;
        
        return discountAmount;
    }

    public double calculateNetPayable(Student student, Boolean stackup, Boolean accommodation, String accommodationType) {
        double netPayableAmount = totalAmount(student, accommodation, accommodationType) - calculateDiscountAmount(student, stackup, accommodation, accommodationType);

        return netPayableAmount;
    }

    private static List<Fee> readFeesFromFile() {
        List <Fee> fees = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get("fees.csv"));
            for (String line : lines) {
                String[] items = line.split(",");
                boolean isMandatory = items[0].equals("mandatory");
                String type = items[1];
                String name = items[2];
                double amount = Double.parseDouble(items[3]);
                fees.add(new Fee( isMandatory, type, name, amount));
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return fees;
    }

    public static List<Billing> readBillsFromFile() {
        List <Billing> bills = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get("bills.csv"));
            for (String line : lines) {
                String[] items = line.split(",");
                String name = items[0];
                String id = items[1];
                Boolean accommodation = items[2].equals("accommodation");
                String accommodationType = items[3];
                bills.add(new Billing(name, id, accommodation, accommodationType));
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return bills;
    }

    public static void updateBillFile(String id, String choice, String accommodationType) {
        try {
            List<String> lines = Files.readAllLines(Paths.get("bills.csv"));
            List<String> updatedLines = new ArrayList<>();

            for (String line : lines) {
                String[] items = line.split(","); 
                if (items.length > 1 && items[1].equals(id)) {
                    if (items.length > 2) {
                        items[2] = choice;
                        items[3] = accommodationType;
                    } 
                    else {
                        line += choice + "," + accommodationType + ",";
                        updatedLines.add(line);
                        continue;
                    }
                    // Reconstruct the line with the updated type
                    StringBuilder updatedLine = new StringBuilder();
                    for (int i = 0; i < items.length; i++) {
                        updatedLine.append(items[i]);
                        if (i < items.length - 1) {
                            updatedLine.append(",");
                        }
                    }
                    updatedLines.add(updatedLine.toString());
                } else {
                    updatedLines.add(line); 
                }
            }
            Files.write(Paths.get("bills.csv"), updatedLines);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
