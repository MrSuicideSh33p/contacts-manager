package com.vichu.japantrip.models;

public class ContactData {
    private String name;
    private String nickName;
    private String phone;
    private String email;
    private String field;
    private String university;
    private String notes;

    public ContactData(String name, String nickName, String phone, String email, String field, String university, String notes) {
        this.name = name;
        this.nickName = nickName;
        this.phone = phone;
        this.email = email;
        this.field = field;
        this.university = university;
        this.notes = notes;
    }

    public ContactData(String content) {
        String[] lines = content.split("\n");
        this.name = lines.length > 0 ? lines[0] : "";
        this.nickName = lines.length > 1 ? lines[1] : "";
        this.phone = lines.length > 2 ? lines[2] : "";
        this.email = lines.length > 3 ? lines[3] : "";
        this.field = lines.length > 4 ? lines[4] : "";
        this.university = lines.length > 5 ? lines[5] : "";
        this.notes = lines.length > 6 ? lines[6] : "";
    }

    public String getName() {
        return name;
    }

    public String getNickName() {
        return nickName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getField() {
        return field;
    }

    public String getUniversity() {
        return university;
    }

    public String getNotes() {
        return notes;
    }

    /**
     * Converts the contact details into a formatted string for file storage.
     */
    public String toFileFormat() {
        return name + "\n" + nickName + "\n" + phone + "\n" + email + "\n" + field + "\n" + university + "\n" + notes;
    }
}
