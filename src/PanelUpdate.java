import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PanelUpdate extends JPanel {
    private JComboBox<String> cboGoodsName;
    private JTextField txtName, txtCount, txtPrice;
    private JButton btnLoad, btnUpdate;
    private PanelSearchAll searchPanel;

    public PanelUpdate(PanelSearchAll searchPanel) {
        this.searchPanel = searchPanel;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.CYAN);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        cboGoodsName = new JComboBox<>();
        txtName = new JTextField(20);
        txtCount = new JTextField(20);
        txtPrice = new JTextField(20);
        btnLoad = new JButton("选中商品，加载数据");
        btnUpdate = new JButton("提交修改");

        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel1.add(new JLabel("选择商品："));
        panel1.add(cboGoodsName);
        panel1.add(btnLoad);
        add(panel1);

        JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel2.add(new JLabel("商品名称："));
        panel2.add(txtName);
        add(panel2);

        JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel3.add(new JLabel("商品数量："));
        panel3.add(txtCount);
        add(panel3);

        JPanel panel4 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel4.add(new JLabel("商品价格："));
        panel4.add(txtPrice);
        add(panel4);

        JPanel panel5 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        panel5.add(btnUpdate);
        add(panel5);

        loadGoodsToCombo();
        btnLoad.addActionListener(e -> loadGoodsInfo());
        btnUpdate.addActionListener(e -> updateGoods());
    }

    private void loadGoodsToCombo() {
        cboGoodsName.removeAllItems();
        Connection con = GetDBConnection.connectDB();
        if (con == null) {
            JOptionPane.showMessageDialog(this, "数据库连接失败，无法加载商品！");
            return;
        }
        String sql = "SELECT goods_name FROM goods";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString("goods_name");
                cboGoodsName.addItem(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            GetDBConnection.close(con, null, null);
        }
    }

    private void loadGoodsInfo() {
        String selectName = (String) cboGoodsName.getSelectedItem();
        if (selectName == null) {
            JOptionPane.showMessageDialog(this, "请先选择商品！");
            return;
        }

        Connection con = GetDBConnection.connectDB();
        if (con == null) {
            JOptionPane.showMessageDialog(this, "数据库连接失败！");
            return;
        }

        String sql = "SELECT goods_name, goods_count, goods_price FROM goods WHERE goods_name = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, selectName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                txtName.setText(rs.getString("goods_name"));
                txtCount.setText(rs.getString("goods_count"));
                txtPrice.setText(rs.getString("goods_price"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载商品信息失败！");
        } finally {
            GetDBConnection.close(con, null, null);
        }
    }

    private void updateGoods() {
        String oldName = (String) cboGoodsName.getSelectedItem();
        String newName = txtName.getText().trim();
        String countStr = txtCount.getText().trim();
        String priceStr = txtPrice.getText().trim();

        if (newName.isEmpty() || countStr.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "商品信息不能为空！");
            return;
        }

        int newCount;
        double newPrice;
        try {
            newCount = Integer.parseInt(countStr);
            newPrice = Double.parseDouble(priceStr);
            if (newCount < 0 || newPrice < 0) {
                JOptionPane.showMessageDialog(this, "数量、价格不能为负数！");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "数量必须是整数，价格必须是数字！");
            return;
        }

        Connection con = GetDBConnection.connectDB();
        if (con == null) {
            JOptionPane.showMessageDialog(this, "数据库连接失败！");
            return;
        }

        String sql = "UPDATE goods SET goods_name=?, goods_count=?, goods_price=? WHERE goods_name=?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setInt(2, newCount);
            pstmt.setDouble(3, newPrice);
            pstmt.setString(4, oldName);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "修改商品成功！");
                // 刷新表格
                if(searchPanel != null){
                    searchPanel.refreshTable();
                }
                loadGoodsToCombo();
                clearText();
            } else {
                JOptionPane.showMessageDialog(this, "未找到对应商品，修改失败！");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "数据库修改失败：" + e.getMessage());
        } finally {
            GetDBConnection.close(con, null, null);
        }
    }

    private void clearText() {
        txtName.setText("");
        txtCount.setText("");
        txtPrice.setText("");
    }
}