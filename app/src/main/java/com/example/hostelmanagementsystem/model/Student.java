package com.example.hostelmanagementsystem.model;

public class Student {

    private String studentId; // Firebase key
    private String registrationNo;
    private String name;
    private String email;
    private String phoneNumber;
    private String password;
    private String cnic;
    private float cgpa;
    private int semester;
    private boolean isEligible;

    // Empty constructor required for Firebase
    public Student() {}

    // Full constructor
    public Student(String studentId, String registrationNo, String name, String email,
                   String phoneNumber, String password, String cnic,
                   float cgpa, int semester, boolean isEligible) {
        this.studentId = studentId;
        this.registrationNo = registrationNo;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.cnic = cnic;
        this.cgpa = cgpa;
        this.semester = semester;
        this.isEligible = isEligible;
    }

    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getCnic() { return cnic; }
    public void setCnic(String cnic) { this.cnic = cnic; }

    public float getCgpa() { return cgpa; }
    public void setCgpa(float cgpa) { this.cgpa = cgpa; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public boolean isEligible() { return isEligible; }
    public void setIsEligible(boolean eligible) { isEligible = eligible; }

    // Domain behavior
    public void receiveNotification(String message) {
        // This can later trigger Firebase notification or local UI alert
        System.out.println("Notification for " + name + ": " + message);
    }
}
