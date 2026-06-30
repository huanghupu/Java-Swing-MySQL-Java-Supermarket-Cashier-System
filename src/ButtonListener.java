import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ButtonListener implements ActionListener {
    private JButton button;
    private JComboBox<String> comboBox;
    private JTextField t_goodName, t_goodCount, t_goodPrice;

    public void setComboBox(JComboBox<String> comboBox) {
        this.comboBox = comboBox;
    }

    public void setJTextField(JTextField t_goodName, JTextField t_goodCount, JTextField t_goodPrice) {
        this.t_goodName = t_goodName;
        this.t_goodCount = t_goodCount;
        this.t_goodPrice = t_goodPrice;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String goodName = (String) comboBox.getSelectedItem();
        if (goodName == null || goodName.equals("从数据库提取数据")) {
            JOptionPane.showMessageDialog(null, "请选择商品名称");
            return;
        }

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = GetDBConnection.connectDB();
            // 教材推荐：使用PreparedStatement防止SQL注入
            String sql = "SELECT goods_count, goods_price FROM goods WHERE goods_name = ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, goodName); // 给?赋值
            rs = pstmt.executeQuery();

            if (rs.next()) {
                t_goodName.setText(goodName);
                t_goodCount.setText(rs.getString("goods_count"));
                t_goodPrice.setText(rs.getString("goods_price"));
            } else {
                JOptionPane.showMessageDialog(null, "未找到该商品");
                clearText();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            GetDBConnection.close(con, pstmt, rs);
        }
    }

    private void clearText() {
        t_goodName.setText("");
        t_goodCount.setText("");
        t_goodPrice.setText("");
    }




        public void setButton(JButton button) {
            this.button = button;
        }

    }