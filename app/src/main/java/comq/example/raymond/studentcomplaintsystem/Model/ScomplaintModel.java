package comq.example.raymond.studentcomplaintsystem.Model;

public class ScomplaintModel {
    private String name, uId, matNo,faculty, department, subject, complaint, respondent, phone, level, status;
    private long date;

    public ScomplaintModel() {
    }

    public ScomplaintModel(String name, String uId, String matNo, String faculty,
                           String department, String subject, String complaint, String respondent,
                           String phone, String level, String status, long date) {
        this.name = name;
        this.uId = uId;
        this.matNo = matNo;
        this.faculty = faculty;
        this.department = department;
        this.subject = subject;
        this.complaint = complaint;
        this.respondent = respondent;
        this.phone = phone;
        this.level = level;
        this.status = status;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getMatNo() {
        return matNo;
    }

    public void setMatNo(String matNo) {
        this.matNo = matNo;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getComplaint() {
        return complaint;
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }

    public String getRespondent() {
        return respondent;
    }

    public void setRespondent(String respondent) {
        this.respondent = respondent;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
