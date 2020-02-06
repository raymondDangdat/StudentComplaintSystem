package comq.example.raymond.studentcomplaintsystem.Model;

public class RegistryComplaintModel {
    private String uId, subject, complaint, respondent, status;
    private long date;

    public RegistryComplaintModel(String uId, String subject, String complaint,
                                  String respondent, String status, long date) {
        this.uId = uId;
        this.subject = subject;
        this.complaint = complaint;
        this.respondent = respondent;
        this.status = status;
        this.date = date;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
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
