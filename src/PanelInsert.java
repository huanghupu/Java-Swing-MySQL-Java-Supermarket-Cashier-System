import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PanelInsert extends JPanel {
    JLabel l_goodName, l_goodCount, l_goodPrice;
    JTextField t_goodName, t_goodCount, t_goodPrice;
    JComboBox<String> comboBoxName;
    JButton btnQuery, btnInsert;
    Box boxH, boxBtn;
    Box boxVOne, boxVTwo;
    JPanel panel, panelBtn;
    ButtonListener listener;
    // 持有查询面板对象，用于刷新
    private PanelSearchAll searchPanel;

    // 构造方法接收查询面板
    public PanelInsert(PanelSearchAll searchPanel) {
        this.searchPanel = searchPanel;
        comboBoxName = new JComboBox<>();
        comboBoxName.addItem("从数据库提取数据");
        comboBoxName.addItem("黑人牙膏");
        comboBoxName.addItem("康师傅红烧牛肉面");
        comboBoxName.addItem("农夫山泉矿泉水");

        btnQuery = new JButton("按名字查询");
        btnInsert = new JButton("插入新商品");
        listener = new ButtonListener();

        listener.setButton(btnQuery);
        listener.setComboBox(comboBoxName);

        l_goodName = new JLabel("商品名称");
        t_goodName = new JTextField(20);
        l_goodPrice = new JLabel("商品价格");
        t_goodPrice = new JTextField(20);
        l_goodCount = new JLabel("商品数量");
        t_goodCount = new JTextField(20);

        listener.setJTextField(t_goodName, t_goodCount, t_goodPrice);
        btnQuery.addActionListener(listener);

        panel = new JPanel();
        panel.add(comboBoxName);
        panel.add(btnQuery);

        panelBtn = new JPanel();
        panelBtn.add(btnInsert);

        boxH = Box.createHorizontalBox();
        boxBtn = Box.createHorizontalBox();
        boxVOne = Box.createVerticalBox();
        boxVTwo = Box.createVerticalBox();

        boxVOne.add(l_goodName);
        boxVOne.add(Box.createHorizontalStrut(10));
        boxVOne.add(l_goodPrice);
        boxVOne.add(Box.createHorizontalStrut(10));
        boxVOne.add(l_goodCount);

        boxVTwo.add(t_goodName);
        boxVTwo.add(Box.createHorizontalStrut(10));
        boxVTwo.add(t_goodPrice);
        boxVTwo.add(Box.createHorizontalStrut(10));
        boxVTwo.add(t_goodCount);

        boxH.add(boxVOne);
        boxH.add(Box.createHorizontalStrut(10));
        boxH.add(boxVTwo);

        boxBtn.add(btnInsert);

        add(panel);
        add(boxH);
        add(boxBtn);
        setBackground(Color.PINK);

        btnInsert.addActionListener(e -> insertGood());
    }

    private void insertGood() {
        String name = t_goodName.getText().trim();
        String countStr = t_goodCount.getText().trim();
        String priceStr = t_goodPrice.getText().trim();

        if (name.isEmpty() || countStr.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写完整的商品信息！");
            return;
        }

        int count;
        double price;
        try {
            count = Integer.parseInt(countStr);
            price = Double.parseDouble(priceStr);
            if (count < 0 || price < 0) {
                JOptionPane.showMessageDialog(this, "数量和价格不能为负数！");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "数量必须是整数，价格必须是数字！");
            return;
        }

        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = GetDBConnection.connectDB();
            String sql = "INSERT INTO goods(goods_name, goods_count, goods_price, goods_category, goods_unit) " +
                    "VALUES (?, ?, ?, '其他', '个')";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setInt(2, count);
            pstmt.setDouble(3, price);

            int row = pstmt.executeUpdate();
            if (row > 0) {
                JOptionPane.showMessageDialog(this, "商品插入成功！");
                clearText();
                comboBoxName.addItem(name);
                // 插入成功 → 刷新查询表格
                if(searchPanel != null){
                    searchPanel.refreshTable();
                }
            } else {
                JOptionPane.showMessageDialog(this, "插入失败，请重试！");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "数据库错误：" + ex.getMessage());
        } finally {
            GetDBConnection.close(con, pstmt, null);
        }
    }

    private void clearText() {
        t_goodName.setText("");
        t_goodCount.setText("");
        t_goodPrice.setText("");
    }
}