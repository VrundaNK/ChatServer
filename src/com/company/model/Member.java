package com.company.model;

/**
 * Created by vnagpurkar on 11/28/2015.
 */
public class Member {

    private String loginName;
    private String password;
    private String firstName;
    private String lastName;
    private String emailId;

    public Member(String loginName, String password){
        this.loginName = loginName;
        this.password = password;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Member member = (Member) o;

        if (loginName != null ? !loginName.equals(member.loginName) : member.loginName != null) return false;
        if (password != null ? !password.equals(member.password) : member.password != null) return false;
        if (firstName != null ? !firstName.equals(member.firstName) : member.firstName != null) return false;
        if (lastName != null ? !lastName.equals(member.lastName) : member.lastName != null) return false;
        return !(emailId != null ? !emailId.equals(member.emailId) : member.emailId != null);

    }

    @Override
    public int hashCode() {
        int result = loginName != null ? loginName.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (emailId != null ? emailId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Member{" +
                "loginName='" + loginName + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", emailId='" + emailId + '\'' +
                '}';
    }
}
