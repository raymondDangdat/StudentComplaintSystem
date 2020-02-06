package comq.example.raymond.studentcomplaintsystem.Model;

public class OfficersModel {
    private String faculty, department, name, email;

    public OfficersModel() {
    }

    public OfficersModel(String faculty, String department, String name, String email) {
        this.faculty = faculty;
        this.department = department;
        this.name = name;
        this.email = email;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
