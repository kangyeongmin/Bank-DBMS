import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class BankApp {

    static Scanner keyboard = new Scanner(System.in);
    static Connection con;
    static PreparedStatement pst;
    static Statement st;
    static ResultSet rs;

    public static void main(String argv[]) throws Exception {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            System.out.print("접속 정보: ");
            String line = keyboard.nextLine();
            String tmp[] = line.split(" ");
            con = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:" + tmp[0] + "/bank", tmp[1], tmp[2]);

            if (con == null) {
                System.out.println("DB 접속 실패");
                return;
            } else
                System.out.println("DB 접속 성공");

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("DB 접속 실패");
            return;
        }

        st = con.createStatement();

        switch (selectMenu()) {
            case 0:
                return;
            case 1:
                System.out.print("관리자 아이디를 입력해주세요. : ");
                int mid = keyboard.nextInt();
                manager(mid);
                break;
            case 2:
                System.out.print("주민등록번호를 입력해주세요. : ");
                int ssn = keyboard.nextInt();
                user(ssn);
                break;
            case 3:
                makeNewUser();
                break;
        }
        st.close();
        con.close();
    }

    public static void makeNewUser() throws SQLException {
        int ssn, phonenum, sex;
        String name, Mname;

        while(true) {
            System.out.println("반갑습니다!\n고객님의 성함을 입력해주세요.");
            name = keyboard.next();
            if(name != null) {
                break;
            }
        }

        while(true) {
            System.out.println("고객님의 성별의 번호 입력해주세요.");
            System.out.println("1.남 / 2.여");
            sex = keyboard.nextInt();

            if (!(sex == 1 || sex == 2)) {
                System.out.println("잘못된 성별입니다.");
            } else {

                break;
            }
        }
        while(true) {
            System.out.println("고객님의 주민번호(뒤7자리)를 입력해주세요. ");
            ssn = keyboard.nextInt();

            pst = con.prepareStatement("select Ssn from user where Ssn = ? ");
            pst.setInt(1, ssn);
            rs = pst.executeQuery();
            if(rs.next()){
                System.out.println("이미 가입되어 있는 고객입니다.");
                return;
            }
            if (Integer.toString(ssn).length() != 7){
                System.out.println("다시 한번 올바른 주민번호를 입력해주세요. ");
            } else {
                break;
            }
        }
        while(true) {
            System.out.println("고객님의 전화번호(뒤8자리)를 입력해주세요. ");
            phonenum = keyboard.nextInt();
            if (Integer.toString(phonenum).length() != 8){
                System.out.println("다시 한번 올바른 전화번호를 입력해주세요. ");
            } else {
                break;
            }
        }
        System.out.println("모든 정보를 입력을 다하셨습니다. 고객님을 관리할 직원의 이름을 선택하시고 입력해주세요.");
        pst = con.prepareStatement("select All Name from manager");
        rs = pst.executeQuery();
        while(rs.next()){
            System.out.print(rs.getString(1) + " / ");
        }
        System.out.println();
        Mname = keyboard.next();
        pst = con.prepareStatement("select ManagerID from manager where Name = ? ");
        pst.setString(1, Mname);
        rs = pst.executeQuery();
        rs.next();
        pst = con.prepareStatement("insert into user values ( ?, ?, ?, ?, ? )");
        pst.setInt(1,ssn);
        pst.setString(2, name);
        String sx = "여";
        if(sex == 1)
            sx = "남";
        pst.setString(3, sx);
        pst.setInt(4, phonenum);
        pst.setInt(5, rs.getInt(1));
        pst.executeQuery();
        System.out.println("축하드립니다! 고객님의 가입이 완료되었습니다. 고객님의 관리 사원은 " + Mname + "입니다.");
    }

    public static Integer selectMenu() {
        System.out.println("\n------------Select Menu-------------");
        System.out.println("0. 종료");
        System.out.println("1. 관리자 모드");
        System.out.println("2. 사용자 모드");
        System.out.println("3. 사용자 신규 가입");
        System.out.println("------------------------------------");
        System.out.print("위의 메뉴에서 원하시는 모드의 번호를 입력해주세요. : ");
        int menu = keyboard.nextInt();
        return menu;
    }

    public static void user(int ssn) throws SQLException {
        System.out.println("--------------User Mode-----------------");
        pst = con.prepareStatement("select Name from user where Ssn = ?");
        pst.setInt(1, ssn);
        rs = pst.executeQuery();
        rs.next();
        String name = rs.getString(1);
        System.out.println("안녕하세요! " + name + "고객님,");
        boolean flag = true;
        int i = 1;

        while(flag) {
            System.out.println("원하시는 업무의 번호를 입력해주세요.");
            System.out.println("1.입출금 거래 / 2.계좌 개설 및 삭제");
            Scanner keyboard = new Scanner(System.in);
            int menu = keyboard.nextInt();

            switch (menu) {
                case 1:
                    int accountNumber;
                    int password;
                    int typeNum;
                    System.out.println("거래하실 계좌번호를 입력해주세요. ");
                    while (true) {
                        accountNumber = keyboard.nextInt();
                        if(accountNumber == 0){
                            i = 0;
                            break;
                        }
                        pst = con.prepareStatement("select * from account where UserSsn = ? and AccountNumber = ?");
                        pst.setInt(1, ssn);
                        pst.setInt(2, accountNumber);
                        rs = pst.executeQuery();
                        if (rs.next()) {
                            break;
                        } else {
                            System.out.println("고객님의 명의로 된 계좌번호가 존재하지 않습니다. 거래하실 계좌번호를 다시 입력해주세요. ");
                            System.out.println("계좌가 없으시다면 0을 입력하시고 계좌 개설을 진행하세요.");
                        }
                    }
                    if(i == 0){
                        i = 1;
                        continue;
                    }
                    System.out.println("원하시는 거래의 번호를 입력해주세요.");
                    System.out.println("1.입금 / 2.출금");
                    typeNum = keyboard.nextInt();
                    String transType;
                    int transmoney;
                    switch (typeNum) {
                        case 1:
                            transType = "입금";
                            System.out.println("입금하실 돈을 입력해주세요.");
                            int money = keyboard.nextInt();
                            transmoney = money;
                            money += rs.getInt(4);
                            pst = con.prepareStatement("update account set balance = ? where UserSsn = ? and AccountNumber = ?");
                            pst.setInt(1, money);
                            pst.setInt(2, ssn);
                            pst.setInt(3, accountNumber);
                            pst.executeQuery();
                            System.out.println("입금이 완료되었습니다. 잔액은 " + money + "원 입니다.");
                            makeHistory(transType, transmoney, money, accountNumber, ssn);
                            break;
                        case 2:
                            transType = "출금";
                            System.out.println("출금하실 계좌의 비밀번호를 입력해주세요. | 계좌번호 : " + accountNumber);
                            while (true) {
                                password = keyboard.nextInt();
                                pst = con.prepareStatement("select * from account where UserSsn = ? and AccountNumber = ? and Password = ?");
                                pst.setInt(1, ssn);
                                pst.setInt(2, accountNumber);
                                pst.setInt(3, password);
                                rs = pst.executeQuery();
                                if (rs.next())
                                    break;
                                else
                                    System.out.println("비밀번호가 틀렸습니다. 출금하실 계좌의 비밀번호를 다시 입력해주세요.");
                            }
                            int balance = rs.getInt(4);
                            System.out.println("출금하실 돈을 입력해주세요. | 잔액 : " + balance);
                            while (true) {
                                money = keyboard.nextInt();
                                if (balance >= money) {
                                    balance -= money;
                                    pst = con.prepareStatement("update account set Balance = ? where UserSsn = ? and AccountNumber = ?");
                                    pst.setInt(1, balance);
                                    pst.setInt(2, ssn);
                                    pst.setInt(3, accountNumber);
                                    pst.executeQuery();
                                    break;
                                } else {
                                    System.out.println("잔액이 부족합니다. 잔액보다 적은 금액으로 출금을 실행해주세요.");
                                }
                            }
                            System.out.println("출금이 완료되었습니다. 잔액은 " + balance + "원 입니다.");
                            makeHistory(transType, money, balance, accountNumber, ssn);
                            break;
                    }
                    break;
                case 2:
                    System.out.println("원하시는 업무의 번호를 입력해주세요.");
                    System.out.println("1.계좌 신규 개설 / 2.계좌 삭제 ");
                    typeNum = keyboard.nextInt();
                    switch (typeNum) {
                        case 1:
                            System.out.println("만드실 계좌의 비밀번호를 설정해주세요.");
                            int PW = keyboard.nextInt();
                            int newAccount = makeNewAccount();
                            pst = con.prepareStatement("insert into account values (?, ?, ?, default)");
                            pst.setInt(1, newAccount);
                            pst.setInt(2, ssn);
                            pst.setInt(3, PW);
                            pst.executeQuery();
                            System.out.println("계좌 개설이 완료되었습니다. 고객님의 신규계좌번호는 " + newAccount + "입니다.");
                            break;
                        case 2:
                            System.out.println("삭제하실 계좌의 번호를 입력해주세요.");
                            while (true) {
                                accountNumber = keyboard.nextInt();
                                pst = con.prepareStatement("select * from account where UserSsn = ? and AccountNumber = ?");
                                pst.setInt(1, ssn);
                                pst.setInt(2, accountNumber);
                                rs = pst.executeQuery();
                                if (rs.next()) {
                                    break;
                                } else {
                                    System.out.println("고객님의 명의로 된 계좌번호가 존재하지 않습니다. 삭제하실 계좌번호를 다시 입력해주세요.");
                                }
                            }
                            System.out.println("삭제하실 계좌의 비밀번호를 입력해주세요. | 계좌번호 : " + accountNumber);
                            while (true) {
                                password = keyboard.nextInt();
                                pst = con.prepareStatement("select * from account where UserSsn = ? and AccountNumber = ? and Password = ?");
                                pst.setInt(1, ssn);
                                pst.setInt(2, accountNumber);
                                pst.setInt(3, password);
                                rs = pst.executeQuery();
                                if (rs.next()) {
                                    break;
                                } else {
                                    System.out.println("비밀번호가 틀렸습니다. 삭제하실 계좌의 비밀번호를 다시 입력해주세요.");
                                }
                            }
                            pst = con.prepareStatement("delete from account where AccountNumber = ?");
                            pst.setInt(1, accountNumber);
                            pst.executeQuery();
                            System.out.println("계좌 삭제가 완료되었습니다.");
                            break;
                    }
                    break;
            }
            System.out.println("또 다른 업무를 진행하시겠습니까?");
            System.out.println("1.네 / 2.아니오");
            int still = keyboard.nextInt();

            if(still == 1)
                flag = true;
            else {
                flag = false;
                System.out.println("이용해주셔서 감사합니다.");
            }
        }
        return;
    }

    public static Integer makeNewAccount(){
        int newAccount;
        while(true) {
            try {
                newAccount = (int) (Math.random() * 100000000);
                pst = con.prepareStatement("select * from account where AccountNumber = ?");
                pst.setInt(1, newAccount);
                rs = pst.executeQuery();
                if (rs.next())
                    continue;
                else
                    break;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return newAccount;
    }

    public static void makeHistory(String transType, int transMoney, int money, int accountNum, int ssn) throws SQLException {
        LocalDateTime now = LocalDateTime.now();
        String format = now.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초"));
        pst = con.prepareStatement("insert into history values (?, ?, ?, ?, ?, ?)");
        pst.setString(1, format);
        pst.setInt(2, ssn);
        pst.setInt(3, transMoney);
        pst.setInt(4, money);
        pst.setString(5, transType);
        pst.setInt(6, accountNum);
        pst.executeQuery();
        return;
    }

    public static void manager(int managerID) throws SQLException {
        pst = con.prepareStatement("select managerID from manager where managerID = ?");
        pst.setInt(1, managerID);
        rs = pst.executeQuery();
        if (!rs.next()) {
            System.out.println("관리자 접근 권한이 없습니다.");
            return;
        }


        System.out.println("--------------Manager Mode-----------------");
        pst = con.prepareStatement("select * from manager where managerID = ?");
        pst.setInt(1, managerID);
        rs = pst.executeQuery();
        rs.next();

        System.out.print("안녕하세요! " + rs.getString(2) + "사원님 | ");
        pst = con.prepareStatement("select * from bank where BankID = ?");
        pst.setInt(1, rs.getInt(4));
        rs = pst.executeQuery();
        rs.next();
        int nowBank = rs.getInt(1);
        int nowNum = rs.getInt(4);
        System.out.println( rs.getString(2) + " 소속");
        boolean flag = true;

        while (flag) {
            System.out.println("원하시는 업무의 번호를 입력해주세요.");
            System.out.println("1.입출금 내역 관리 / 2.지점 이동 신청");
            Scanner keyboard = new Scanner(System.in);
            int menu = keyboard.nextInt();

            switch (menu) {
                case 1:
                    int typeNum;
                    System.out.println("원하시는 업무 번호를 입력해주세요.");
                    System.out.println("1.전체 조회 / 2.검색");
                    typeNum = keyboard.nextInt();

                    switch (typeNum){
                        case 1:
                            pst = con.prepareStatement("select * from history");
                            rs = pst.executeQuery();
                            //rs.next();
                            if(!(rs.next())){
                                System.out.println("입출금 내역이 존재하지 않습니다.");
                            } else {
                                printHistory(rs);
                            }
                            break;
                        case 2:
                            int num;
                            int j = 1;
                            System.out.println("검색하고자 하는 고객의 주민번호를 입력해주세요.");
                            while(j == 1) {
                                num = keyboard.nextInt();
                                pst = con.prepareStatement("select * from history where Ussn = ? ");
                                pst.setInt(1, num);
                                rs = pst.executeQuery();
                                rs.next();
                                if (num != rs.getInt(2)) {
                                    System.out.println("입력하신 " + num + "번호는 입출금거래내역에서 존재하지 않습니다. 다시 입력해주세요.");
                                } else {
                                    printHistory(rs);
                                    j = 0;
                                }
                            }
                            break;
                    }
                    break;
                case 2:
                    int id;
                    System.out.println("이동을 원하시는 지점의 번호를 입력해주세요. * 사원수가 10명 초과인 곳은 이동 신청이 제한됩니다.");
                    pst = con.prepareStatement("select * from bank");
                    rs = pst.executeQuery();
                    while(rs.next()){
                        System.out.print(rs.getInt(1) +  ". " + rs.getString(2) + " | 위치: " + rs.getString(3));
                        System.out.println(" | 사원수: " + rs.getInt(4));
                    }
                    id = keyboard.nextInt();
                    pst = con.prepareStatement("select * from bank where bankID = ?");
                    pst.setInt(1, id);
                    rs = pst.executeQuery();
                    rs.next();

                    if(rs.getInt(4) < 10){
                        System.out.println("이동 신청이 수락되었습니다. 내일부터 " + rs.getString(2) + "으로 출근하세요.");
                        int newNum = rs.getInt(4) + 1;
                        int newBank = rs.getInt(1);
                        pst = con.prepareStatement("update bank set Mnum = ? where BankID = ? ");
                        pst.setInt(1, newNum);
                        pst.setInt(2, newBank);
                        pst.executeQuery();

                        pst = con.prepareStatement("update bank set Mnum = ? where BankID = ? ");
                        pst.setInt(1, nowNum - 1);
                        pst.setInt(2, nowBank);
                        pst.executeQuery();//오래된 은행 사원수 선택

                        pst = con.prepareStatement("update manager set BId = ? where ManagerID = ? ");
                        pst.setInt(1, newBank);
                        pst.setInt(2, managerID);
                        pst.executeQuery();
                    } else {
                        System.out.println(rs.getString(2) + "은 현재 사원수 초과로 이동이 불가합니다.");
                    }
                    break;
            }
            System.out.println("또 다른 업무를 진행하시겠습니까?");
            System.out.println("1.네 / 2.아니오");
            int still = keyboard.nextInt();
            if(still == 1)
                flag = true;
            else {
                flag = false;
                System.out.println("이용해주셔서 감사합니다.");
            }
        }
        return;
    }

    public static void printHistory(ResultSet rs) throws SQLException {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("|          날짜 및 시간          | 사용자번호 |  계좌번호  | 거래액수 |   잔액   | 종류 |");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.print("| " + rs.getString(1) + " |");
        System.out.print(" " + rs.getInt(2) + " |");
        System.out.print(" " + rs.getInt(6) + " |");
        System.out.print(" " + rs.getInt(3) + "원 |");
        System.out.print(" " + rs.getInt(4) + "원 |");
        System.out.println(" " + rs.getString(5) + " |");
        while(rs.next()){
            System.out.println("------------------------------------------------------------------------------------");
            System.out.print("| " + rs.getString(1) + " |");
            System.out.print(" " + rs.getInt(2) + " |");
            System.out.print(" " + rs.getInt(6) + " |");
            System.out.print(" " + rs.getInt(3) + "원 |");
            System.out.print(" " + rs.getInt(4) + "원 |");
            System.out.println(" " + rs.getString(5) + " |");
        }
        System.out.println("------------------------------------------------------------------------------------");
        return;
    }

}
