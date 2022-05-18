package contacts;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws NoSuchMethodException {
        new ContactsApp().chooseAction();
    }
}

class ContactsApp {
    private List<Contact> contacts = new ArrayList<>();


    void chooseAction() {
        while (true) {
            System.out.print("[menu] Enter action (add, list, search, count, exit): ");
            Main.scanner.reset();
            String action = Main.scanner.nextLine();
            switch (action) {
                case "add":
                    System.out.print("Enter the type (person, organization): ");
                    String contactType = Main.scanner.nextLine();
                    if (contactType.equals("person")) {
                        this.contacts.add(new Person());
                    } else if (contactType.equals("organization")) {
                        this.contacts.add(new Organization());
                    } else {
                        System.out.println("invalid contact type!\n");
                        break;
                    }
                    System.out.println("The record added.\n");
                    break;
                case "search":
                    searchmenu(search1());
                    break;
                case "edit":
                    if (!listContacts(contacts)) {
                        System.out.println("No records to edit!");
                    } else {
                        System.out.print("Select a record: ");
                        try {
                            editContact(Integer.valueOf(Main.scanner.nextLine()) - 1);
                        } catch (NumberFormatException nfe) {
                            System.out.println("wrong record format");
                        }
                    }
                    break;
                case "count":
                    System.out.printf("The Phone Book has %d records.%n%n", contacts.size());
                    break;
                case "list":
                    if (listContacts(contacts)) {
                        System.out.print("\n[list] Enter action ([number], back): ");
                        String listAction = Main.scanner.nextLine();
                        if (listAction.equals("back")) {
                            break;
                        } else {
                            try {
                                Contact contact = contacts.get(Integer.valueOf(listAction) - 1);
                                contact.printInfo();
                                recordActions(contact);
                            } catch (Exception ife) {
                                System.out.println("invalid number");
                            }
                        }
                    } else {
                        System.out.println("No contacts in list");
                    }
                    break;
                case "exit":
                    System.exit(1);
                    break;
                default:
                    break;
            }
        }
    }

    List<Contact> search1() {
        System.out.print("Enter search query: ");
        String searchString = Main.scanner.nextLine();
        List<Contact> searchContactsList = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?i).*" + searchString + ".*");
        for (Contact contact : contacts) {
            Matcher matcher = pattern.matcher(contact.getContactData());
            if (matcher.matches()) {
                searchContactsList.add(contact);
            }
        }
        if (searchContactsList.size() == 0) {
            System.out.println("No matches found.");
        } else {
            System.out.println("Found " + searchContactsList.size() + " results:");
            listContacts(searchContactsList);
        }
        System.out.println();
        return searchContactsList;
    }

    void searchmenu(List<Contact> searchList) {
        boolean inSearchMenu = true;

        while (inSearchMenu) {
            System.out.print("[search] Enter action ([number], back, again): ");
            String searchMenuSelect = Main.scanner.nextLine();
            switch (searchMenuSelect) {
                case "back":
                    inSearchMenu = false;
                    System.out.println();
                    break;
                case "again":
                    searchList = search1();
                    break;
                default:
                    try {
                        Contact selectedContact = searchList.get(Integer.valueOf(searchMenuSelect) - 1);
                        selectedContact.printInfo();
                        recordActions(selectedContact);
                        inSearchMenu = false;
                    } catch (Exception ex) {
                        System.out.println("invalid index!");
                    }
                    break;
            }
        }
    }

    boolean listContacts(List<Contact> contacts) {
        if (contacts.isEmpty()) {
            return false;
        }
        int i = 0;
        for (Contact contact : contacts) {
            System.out.printf("%d. %s%n", ++i, contact);
        }
        return true;
    }


    void recordActions(Contact contact) {
        boolean inRecordMenu = true;
        while (inRecordMenu) {
            System.out.print("[record] Enter action (edit, delete, menu): ");
            switch (Main.scanner.nextLine()) {
                case "menu":
                    inRecordMenu = false;
                    break;
                case "delete":
                    contacts.remove(contact);
                    System.out.println("Record deleted!");
                    inRecordMenu = false;
                    break;
                case "edit":
                    editContact(contact);
                    break;
            }
        }
        System.out.println();
    }

    void editContact(int contactIndex) {

        if (contactIndex > contacts.size() || contactIndex < 0) {
            System.out.println("No such contact!");
        } else {

            Contact contactToEdit = contacts.get(contactIndex);
            contactToEdit.setTimeOflastEdit(LocalDateTime.now().withNano(0));

            System.out.print("Select a field " + Arrays.toString(contactToEdit.getChangeableFields())
                    .replace("[", "(")
                    .replace("]", ")") + ": ");
            String fieldToEdit = Main.scanner.nextLine();
            if (Arrays.asList(contactToEdit.getChangeableFields()).contains(fieldToEdit)) {
                System.out.print("Enter " + fieldToEdit + ": ");
                contactToEdit.updateField(fieldToEdit);
            } else {
                System.out.println("Wrong field name");
            }
        }
        System.out.println();
    }

    void editContact(Contact contact) {

        contact.setTimeOflastEdit(LocalDateTime.now().withNano(0));

        System.out.print("Select a field " + Arrays.toString(contact.getChangeableFields())
                .replace("[", "(")
                .replace("]", ")") + ": ");
        String fieldToEdit = Main.scanner.nextLine();
        if (Arrays.asList(contact.getChangeableFields()).contains(fieldToEdit)) {
            System.out.print("Enter " + fieldToEdit + ": ");
            contact.updateField(fieldToEdit);
        } else {
            System.out.println("Wrong field name");
        }
        System.out.println();
    }
}

abstract class Contact {
    private String number = "";
    private LocalDateTime timeOfCreation = LocalDateTime.now().withNano(0);
    private LocalDateTime timeOflastEdit = timeOfCreation;

    abstract void updateField(String field);

    abstract String[] getChangeableFields();

    abstract String getContactData();

    abstract void printInfo();

    public void setNumber(String number) {
        this.number = phoneNumberMatcher(number) ? number : "";
    }

    public String getNumber() {
        return number;
    }

    public boolean hasNumber() {
        return !number.isEmpty();
    }

    public LocalDateTime getTimeOfCreation() {
        return timeOfCreation;
    }

    public LocalDateTime getTimeOflastEdit() {
        return timeOflastEdit;
    }

    public void setTimeOflastEdit(LocalDateTime timeOflastEdit) {
        this.timeOflastEdit = timeOflastEdit;
    }

    static boolean phoneNumberMatcher(String text) {
        Pattern pattern = Pattern
                .compile("\\+?((((\\([a-zA-Z\\d]{1,}\\)[\\s|-][a-zA-Z\\d]{2,})" +
                        "|([a-zA-Z\\d]{1,}[\\s|-]\\([a-zA-Z\\d]{2,}\\))" +
                        "|([a-zA-Z\\d]{1,}[\\s|-][a-zA-Z\\d]{2,}))" +
                        "([\\s|-][a-zA-Z\\d]{2,}){0,})" +
                        "|([a-zA-Z\\d]{1,})" +
                        "|(\\([a-zA-Z\\d]{1,}\\)))");
        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches()) {
            System.out.println("Wrong number format!");
        }
        return matcher.matches();
    }
}

class Person extends Contact {


    private String name;
    private String surName;
    private String birthDate;
    private String gender;

    public Person() {
        System.out.printf("Enter the name: ");
        setName(Main.scanner.nextLine());

        System.out.print("Enter the surname: ");
        setSurName(Main.scanner.nextLine());

        System.out.print("Enter the birth date: ");
        setBirthDate(Main.scanner.nextLine());

        System.out.print("Enter the gender (M, F): ");
        setGender(Main.scanner.nextLine());

        System.out.print("Enter the number: ");
        setNumber(Main.scanner.nextLine());
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        if (gender.equals("M") || gender.equals("F")) {
            this.gender = gender;
        } else {
            System.out.println("Bad gender!");
            this.gender = "[no data]";
        }
    }

    public void setBirthDate(String birthDate) {
        try {
            this.birthDate = LocalDate.parse(birthDate).toString();
        } catch (DateTimeException dateTimeException) {
            System.out.println("Bad birth date!");
            this.birthDate = "[no data]";
        }
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getName() {
        return name;
    }

    public String getSurName() {
        return surName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getGender() {
        return gender;
    }


    @Override
    public String toString() {
        return name + " " + surName;
    }

    @Override
    void printInfo() {
        System.out.println("Name: " + getName() +
                "\nSurname: " + getSurName() +
                "\nBirth date: " + getBirthDate() +
                "\nGender: " + getGender() +
                "\nNumber: " + getNumber() +
                "\nTime created: " + getTimeOfCreation() +
                "\nTime last edit: " + getTimeOflastEdit() + "\n");
    }

    @Override
    void updateField(String field) {
        String updateValue = Main.scanner.nextLine();
        switch (field) {
            case "name":
                setName(updateValue);
                System.out.println("Saved");
                break;
            case "surname":
                setSurName(updateValue);
                System.out.println("Saved");
                break;
            case "birth":
                setBirthDate(updateValue);
                if (getBirthDate().equals(updateValue)) {
                    System.out.println("Saved");
                }
                break;
            case "number":
                setNumber(updateValue);
                if (getNumber().equals(updateValue)) {
                    System.out.println("Saved");
                }
                break;
            case "gender":
                setGender(updateValue);
                if (getGender().equals(updateValue)) {
                    System.out.println("Saved");
                }
                break;
        }
        this.printInfo();
    }

    @Override
    String getContactData() {
        return getName() + getSurName() + getNumber();
    }

    @Override
    String[] getChangeableFields() {
        return new String[]{"name", "surname", "birth", "gender", "number"};
    }
}

class Organization extends Contact {
    private String organizationName;
    private String address;

    public Organization() {
        System.out.printf("Enter the organization name: ");
        setOrganizationName(Main.scanner.nextLine());

        System.out.print("Enter the address: ");
        setAddress(Main.scanner.nextLine());

        System.out.print("Enter the number: ");
        setNumber(Main.scanner.nextLine());
    }

    public void setOrganizationName(String name) {
        this.organizationName = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return organizationName;
    }

    public void printInfo() {
        System.out.println("Organization name: " + getOrganizationName() +
                "\nAddress: " + getAddress() +
                "\nNumber: " + getNumber() +
                "\nTime created: " + getTimeOfCreation() +
                "\nTime last edit: " + getTimeOflastEdit() + "\n");
    }

    @Override
    void updateField(String field) {
        String updateValue = Main.scanner.nextLine();
        switch (field) {
            case "name":
                setOrganizationName(updateValue);
                System.out.println("Saved");
                break;
            case "address":
                setAddress(updateValue);
                System.out.println("Saved");
                break;
            case "number":
                setNumber(updateValue);
                if (this.getNumber().equals(updateValue)) {
                    System.out.println("Saved");
                }
                break;
        }
        this.printInfo();
    }

    @Override
    String[] getChangeableFields() {
        return new String[]{"name", "address", "number"};
    }

    @Override
    String getContactData() {
        return getOrganizationName() + getAddress() + getNumber();
    }
}