import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PanelDelete extends JPanel {
    JTextField t_deleteName;
    JButton btnDelete;
    // 持有查询面板对象
    private PanelSearchAll searchPanel;

    public PanelDelete(PanelSearchAll searchPanel) {
        this.searchPanel = searchPanel;
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        JLabel label = new JLabel("输入要删除的商品名称：");
        t_deleteName = new JTextField(20);
        btnDelete = new JButton("删除商品");

        add(label);
        add(t_deleteName);
        add(btnDelete);
        setBackground(Color.MAGENTA);

        btnDelete.addActionListener(e -> deleteGood());
    }

    private void deleteGood() {
        String name = t_deleteName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入要删除的商品名称！");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除商品【" + name + "】吗？",
                "删除确认",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = GetDBConnection.connectDB();
            String sql = "DELETE FROM goods WHERE goods_name = ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, name);

            int row = pstmt.executeUpdate();
            if (row > 0) {
                JOptionPane.showMessageDialog(this, "商品删除成功！");
                t_deleteName.setText("");
                // 删除成功 → 刷新查询表格
                if(searchPanel != null){
                    searchPanel.refreshTable();
                }
            } else {
                JOptionPane.showMessageDialog(this, "未找到该商品，删除失败！");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "数据库错误：" + ex.getMessage());
        } finally {
            GetDBConnection.close(con, pstmt, null);
        }
    }
}