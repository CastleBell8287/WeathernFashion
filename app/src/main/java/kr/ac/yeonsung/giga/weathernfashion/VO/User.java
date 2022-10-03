package kr.ac.yeonsung.giga.weathernfashion.VO;

import java.util.List;

public class User {
    public String user_email;
    public String user_pw;
    public String user_name;
    public String user_phone;
    public List<String> user_styles;
    public String reg_date;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String user_email, String user_pw, String user_name, String user_phone, List<String> user_styles, String reg_date) {
        this.user_email = user_email;
        this.user_pw = user_pw;
        this.user_name = user_name;
        this.user_phone = user_phone;
        this.user_styles = user_styles;
        this.reg_date = reg_date;
    }
}
