import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PanelCashier extends JPanel {
    // 商品选择区
    private JComboBox<String> cboGoods;
    private JTextField txtBuyNum;
    private JButton btnAddCart;

    // 购物车表格
    private JTable tableCart;
    private DefaultTableModel cartModel;
    private String[] cartCol = {"商品名", "单价", "数量", "小计"};

    // 金额结算区
    private JLabel lblTotal;
    private JTextField txtReceive;
    private JLabel lblChange;
    private JButton btnClearCart, btnSettle;

    // 购物车临时存储
    private List<CartItem> cartList = new ArrayList<>();
    private PanelSearchAll searchPanel;

    public PanelCashier(PanelSearchAll searchPanel) {
        this.searchPanel = searchPanel;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.LIGHT_GRAY);

        // 1. 顶部商品选择面板
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelTop.add(new JLabel("选择商品："));
        cboGoods = new JComboBox<>();
        loadGoodsCombo();
        panelTop.add(cboGoods);

        panelTop.add(new JLabel("购买数量："));
        txtBuyNum = new JTextField(6);
        panelTop.add(txtBuyNum);

        btnAddCart = new JButton("加入购物车");
        panelTop.add(btnAddCart);
        add(panelTop, BorderLayout.NORTH);

        // 2. 中间购物车表格
        cartModel = new DefaultTableModel(new String[0][4], cartCol) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableCart = new JTable(cartModel);
        JScrollPane scrollCart = new JScrollPane(tableCart);
        scrollCart.setPreferredSize(new Dimension(450, 220));
        add(scrollCart, BorderLayout.CENTER);

        // 3. 底部结算面板
        JPanel panelBottom = new JPanel(new GridLayout(4, 2, 8, 8));
        panelBottom.add(new JLabel("订单合计："));
        lblTotal = new JLabel("0.00");
        panelBottom.add(lblTotal);

        panelBottom.add(new JLabel("顾客付款："));
        txtReceive = new JTextField();
        panelBottom.add(txtReceive);

        panelBottom.add(new JLabel("应找零钱："));
        lblChange = new JLabel("0.00");
        panelBottom.add(lblChange);

        JPanel btnPanel = new JPanel();
        btnClearCart = new JButton("清空购物车");
        btnSettle = new JButton("确认结算");
        btnPanel.add(btnClearCart);
        btnPanel.add(btnSettle);
        panelBottom.add(btnPanel);
        add(panelBottom, BorderLayout.SOUTH);

        // 绑定按钮事件
        btnAddCart.addActionListener(e -> addToCart());
        btnClearCart.addActionListener(e -> clearCart());
        txtReceive.addActionListener(e -> calcChange());
        btnSettle.addActionListener(e -> settleOrder());
    }

    // 从数据库加载所有商品到下拉框
    private void loadGoodsCombo() {
        cboGoods.removeAllItems();
        Connection con = GetDBConnection.connectDB();
        if (con == null) return;
        String sql = "SELECT goods_name FROM goods";
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                cboGoods.addItem(rs.getString("goods_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            GetDBConnection.close(con, null, null);
        }
    }

    // 加入购物车
    private void addToCart() {
        String goodsName = (String) cboGoods.getSelectedItem();
        String numStr = txtBuyNum.getText().trim();
        if (goodsName == null || numStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请选择商品并填写数量");
            return;
        }
        int buyNum;
        try {
            buyNum = Integer.parseInt(numStr);
            if (buyNum <= 0) {
                JOptionPane.showMessageDialog(this, "数量必须大于0");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "数量请输入整数");
            return;
        }

        // 查询商品单价、库存
        Connection con = GetDBConnection.connectDB();
        if (con == null) return;
        double price = 0;
        int stock = 0;
        String sql = "SELECT goods_price,goods_count FROM goods WHERE goods_name=?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, goodsName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                price = rs.getDouble("goods_price");
                stock = rs.getInt("goods_count");
            } else {
                JOptionPane.showMessageDialog(this, "商品不存在");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        } finally {
            GetDBConnection.close(con, null, null);
        }

        if (stock < buyNum) {
            JOptionPane.showMessageDialog(this, "库存不足，当前库存：" + stock);
            return;
        }

        // 添加购物车对象
        double sub = price * buyNum;
        cartList.add(new CartItem(goodsName, price, buyNum, sub));
        refreshCartTable();
        calcTotal();
        txtBuyNum.setText("");
    }

    // 刷新购物车表格
    private void refreshCartTable() {
        cartModel.setRowCount(0);
        for (CartItem item : cartList) {
            Object[] row = {item.name, item.price, item.num, item.subTotal};
            cartModel.addRow(row);
        }
    }

    // 计算总金额
    private void calcTotal() {
        double total = 0;
        for (CartItem i : cartList) total += i.subTotal;
        lblTotal.setText(String.format("%.2f", total));
        calcChange();
    }

    // 自动计算找零
    private void calcChange() {
        double total = Double.parseDouble(lblTotal.getText());
        String receiveStr = txtReceive.getText().trim();
        if (receiveStr.isEmpty()) {
            lblChange.setText("0.00");
            return;
        }
        double receive;
        try {
            receive = Double.parseDouble(receiveStr);
        } catch (NumberFormatException e) {
            lblChange.setText("输入金额错误");
            return;
        }
        double change = receive - total;
        if (change < 0) {
            lblChange.setText("付款不足");
        } else {
            lblChange.setText(String.format("%.2f", change));
        }
    }

    // 清空购物车
    private void clearCart() {
        cartList.clear();
        refreshCartTable();
        lblTotal.setText("0.00");
        txtReceive.setText("");
        lblChange.setText("0.00");
    }

    // 结算订单（入库订单、扣库存、刷新商品表格）
    private void settleOrder() {
        if (cartList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "购物车为空，无法结算");
            return;
        }
        double total = Double.parseDouble(lblTotal.getText());
        double receive;
        try {
            receive = Double.parseDouble(txtReceive.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "请输入正确收款金额");
            return;
        }
        double change = receive - total;
        if (change < 0) {
            JOptionPane.showMessageDialog(this, "收款金额不足");
            return;
        }

        Connection con = GetDBConnection.connectDB();
        if (con == null) return;
        try {
            con.setAutoCommit(false); // 事务，出错回滚

            // 1. 插入订单主表
            String orderSql = "INSERT INTO sale_order(total_money,receive_money,change_money) VALUES (?,?,?)";
            PreparedStatement orderPstmt = con.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderPstmt.setDouble(1, total);
            orderPstmt.setDouble(2, receive);
            orderPstmt.setDouble(3, change);
            orderPstmt.executeUpdate();

            // 获取自增订单号
            ResultSet keyRs = orderPstmt.getGeneratedKeys();
            int orderId = 0;
            if (keyRs.next()) orderId = keyRs.getInt(1);

            // 2. 插入订单明细 + 扣减商品库存
            String itemSql = "INSERT INTO sale_order_item(order_id,goods_name,buy_num,single_price,sub_total) VALUES (?,?,?,?,?)";
            String updateStockSql = "UPDATE goods SET goods_count=goods_count-? WHERE goods_name=?";
            PreparedStatement itemPstmt = con.prepareStatement(itemSql);
            PreparedStatement stockPstmt = con.prepareStatement(updateStockSql);

            for (CartItem item : cartList) {
                // 明细
                itemPstmt.setInt(1, orderId);
                itemPstmt.setString(2, item.name);
                itemPstmt.setInt(3, item.num);
                itemPstmt.setDouble(4, item.price);
                itemPstmt.setDouble(5, item.subTotal);
                itemPstmt.executeUpdate();
                // 库存扣减
                stockPstmt.setInt(1, item.num);
                stockPstmt.setString(2, item.name);
                stockPstmt.executeUpdate();
            }

            con.commit();
            JOptionPane.showMessageDialog(this, "结算成功！订单号：" + orderId);
            // 刷新商品表格（库存更新同步）
            if (searchPanel != null) searchPanel.refreshTable();
            clearCart();
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "结算失败：" + e.getMessage());
            e.printStackTrace();
        } finally {
            GetDBConnection.close(con, null, null);
        }
    }

    // 购物车内嵌实体类
    static class CartItem {
        String name;
        double price;
        int num;
        double subTotal;
        public CartItem(String name, double price, int num, double subTotal) {
            this.name = name;
            this.price = price;
            this.num = num;
            this.subTotal = subTotal;
        }
    }
}