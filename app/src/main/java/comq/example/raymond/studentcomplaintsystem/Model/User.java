package comq.example.raymond.studentcomplaintsystem.Model;

public class User {
    private String email;
    private String name;
    private String faculty;
    private String department;
    private String uId;


    public User() {
    }

    public User(String email, String name, String faculty, String department, String uId) {
        this.email = email;
        this.name = name;
        this.faculty = faculty;
        this.department = department;
        this.uId = uId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
}
